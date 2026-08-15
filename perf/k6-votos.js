import http from 'k6/http';
import { check } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';

// Teste de carga do endpoint de voto (bônus 2 — cenário de centenas de milhares de votos).
//
// Pré-condição: uma pauta com sessão ABERTA e duração longa o suficiente para o teste.
//   1) POST /api/v1/pautas                      -> pega o {id}
//   2) POST /api/v1/pautas/{id}/sessoes  {"duracaoMinutos": 30}   <- use uma duração > tempo do teste!
//
// Execução:
//   k6 run -e BASE_URL=http://localhost:8080 -e PAUTA_ID=a72bd1c9-cecc-4721-9c6e-b05196a08f17 perf/k6-votos.js
//
// O número de p95 (abaixo em thresholds) é OBTIDO ao rodar este script; não vem medido
// neste entregável. Ajuste vus/iterations conforme a máquina.

// Métricas de negócio: separam votos REGISTRADOS (201) dos RECUSADOS (rejeição de regra:
// 409 duplicado/sessão fechada, 422 inelegível) e dos ERROS (404/503/5xx). Sem isso, um cenário
// em que tudo cai em 409 aparece "zerado" no check, sem explicar o porquê.
const votosRegistrados = new Counter('votos_registrados'); // 201
const votosRecusados = new Counter('votos_recusados'); // 409/422 (regra de negócio)
const votosErro = new Counter('votos_erro'); // 404/503/5xx (não deveria acontecer)
const taxaRegistro = new Rate('taxa_registro'); // % de requisições que viraram voto
const duracaoRegistro = new Trend('duracao_registro', true); // latência só dos votos 201

export const options = {
  scenarios: {
    votos: {
      executor: 'shared-iterations',
      vus: 2000,
      iterations: 300000,
      maxDuration: '10m',
    },
  },
  thresholds: {
    http_req_duration: ['p(95)<500'], // meta: p95 < 500ms (todas as respostas)
    'duracao_registro': ['p(95)<500'], // p95 só dos votos efetivamente registrados
    'taxa_registro': ['rate>0.99'], // ao menos 99% das requisições viram voto
    checks: ['rate>0.99'],
  },
};

const BASE = __ENV.BASE_URL || 'http://localhost:8080';
const PAUTA = __ENV.PAUTA_ID;
const RUN_ID = __ENV.RUN_ID || `${Date.now()}-${Math.random().toString(36).slice(2, 10)}`;

// Diagnóstico: guarda, POR VU, os status já logados para imprimir cada tipo de resposta não-201
// uma única vez (com o corpo ProblemDetail: type/detail). Evita inundar o log a 100k iterações
// e ainda mostra exatamente QUAL erro está ocorrendo.
const statusJaLogado = new Set();

function logarPrimeiraOcorrencia(res) {
  if (statusJaLogado.has(res.status)) return;
  statusJaLogado.add(res.status);
  if (res.status === 0) {
    // falha de rede/conexão: não há resposta HTTP; k6 traz o motivo em res.error/res.error_code
    console.error(`[voto] falha de rede status=0 error_code=${res.error_code} error=${res.error}`);
  } else {
    console.error(`[voto] status=${res.status} body=${res.body}`);
  }
}

export default function () {
  // associadoId único por execução, VU e iteração — a unicidade é por (pauta, associado),
  // então reexecutar o teste na mesma pauta não reaproveita associados e evita 409 duplicado.
  const assoc = `assoc-${RUN_ID}-${__VU}-${__ITER}`;
  const body = JSON.stringify({
    associadoId: assoc,
    cpf: '19839091069',
    opcao: __ITER % 2 ? 'SIM' : 'NAO',
  });
  const res = http.post(`${BASE}/api/v1/pautas/${PAUTA}/votos`, body, {
    headers: { 'Content-Type': 'application/json' },
    tags: { name: 'registrar_voto' }, // agrupa a métrica http_* por endpoint
  });

  const registrado = res.status === 201;
  const recusado = res.status === 409 || res.status === 422;

  taxaRegistro.add(registrado);
  if (registrado) {
    votosRegistrados.add(1);
    duracaoRegistro.add(res.timings.duration);
  } else if (recusado) {
    votosRecusados.add(1, { status: String(res.status) });
    logarPrimeiraOcorrencia(res); // mostra o motivo (voto-duplicado / sessao-fechada / inelegivel)
  } else {
    votosErro.add(1, { status: String(res.status) });
    logarPrimeiraOcorrencia(res); // mostra o erro inesperado (5xx, 404, ou falha de rede status=0)
  }

  check(res, {
    'registrado (201)': () => registrado,
    'não foi erro de infra (2xx/4xx)': () => res.status < 500,
  });
}
