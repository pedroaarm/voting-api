import http from 'k6/http';
import { check } from 'k6';

// Teste de carga do endpoint de voto (bônus 2 — cenário de centenas de milhares de votos).
//
// Pré-condição: uma pauta com sessão ABERTA e duração longa o suficiente para o teste.
//   1) POST /api/v1/pautas                      -> pega o {id}
//   2) POST /api/v1/pautas/{id}/sessoes  {"duracaoMinutos": 30}
//
// Execução:
//   k6 run -e BASE_URL=http://localhost:8080 -e PAUTA_ID=bc4b66c3-ea08-451f-9e5a-2626971e9102 perf/k6-votos.js
//
// O número de p95 (abaixo em thresholds) é OBTIDO ao rodar este script; não vem medido
// neste entregável. Ajuste vus/iterations conforme a máquina.

export const options = {
  scenarios: {
    votos: {
      executor: 'shared-iterations',
      vus: 100,
      iterations: 100000,
      maxDuration: '10m',
    },
  },
  thresholds: {
    http_req_duration: ['p(95)<500'], // meta: p95 < 500ms
    checks: ['rate>0.99'],
  },
};

const BASE = __ENV.BASE_URL || 'http://localhost:8080';
const PAUTA = __ENV.PAUTA_ID;

export default function () {
  // associadoId único por (VU, iteração) — a unicidade é por (pauta, associado),
  // então cada associado vota uma única vez (201). Associados repetidos dariam 409.
  const assoc = `assoc-${__VU}-${__ITER}`;
  const body = JSON.stringify({
    associadoId: assoc,
    cpf: '19839091069',
    opcao: __ITER % 2 ? 'SIM' : 'NAO',
  });
  const res = http.post(`${BASE}/api/v1/pautas/${PAUTA}/votos`, body, {
    headers: { 'Content-Type': 'application/json' },
  });
  check(res, { 'status 201': (r) => r.status === 201 });
}
