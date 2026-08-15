package com.sicredi.vote.adapters.web.api;

final class OpenApiExamples {

  static final String JSON = "application/json";
  static final String PROBLEM_JSON = "application/problem+json";

  static final String VALIDACAO_TITULO =
      "{\"type\":\"urn:vote:validacao\",\"title\":\"Requisicao invalida\",\"status\":400,"
          + "\"errors\":[\"titulo: nao pode ser vazio\"]}";
  static final String VALIDACAO_DURACAO =
      "{\"type\":\"urn:vote:validacao\",\"title\":\"Requisicao invalida\",\"status\":400,"
          + "\"errors\":[\"duracaoMinutos: deve ser positivo\"]}";
  static final String VALIDACAO_CPF =
      "{\"type\":\"urn:vote:validacao\",\"title\":\"Requisicao invalida\",\"status\":400,"
          + "\"errors\":[\"cpf: nao pode ser vazio\"]}";
  static final String PAUTA_NAO_ENCONTRADA =
      "{\"type\":\"urn:vote:pauta-nao-encontrada\",\"title\":\"Pauta nao encontrada\","
          + "\"status\":404}";
  static final String SESSAO_JA_ABERTA =
      "{\"type\":\"urn:vote:sessao-ja-aberta\",\"title\":\"Sessao ja aberta\",\"status\":409}";
  static final String VOTO_DUPLICADO =
      "{\"type\":\"urn:vote:voto-duplicado\",\"title\":\"Voto duplicado\",\"status\":409}";
  static final String SESSAO_FECHADA =
      "{\"type\":\"urn:vote:sessao-fechada\",\"title\":\"Sessao fechada\",\"status\":422}";
  static final String ELEGIBILIDADE_INDISPONIVEL =
      "{\"type\":\"urn:vote:elegibilidade-indisponivel\","
          + "\"title\":\"Servico de elegibilidade indisponivel\",\"status\":503}";
  static final String RESULTADO =
      "{\"pautaId\":\"3fa85f64-5717-4562-b3fc-2c963f66afa6\",\"status\":\"APROVADA\","
          + "\"totalSim\":10,\"totalNao\":3,\"total\":13}";
  static final String SESSAO_EM_ANDAMENTO =
      "{\"type\":\"urn:vote:sessao-em-andamento\",\"title\":\"Sessao em andamento\","
          + "\"status\":409}";

  private OpenApiExamples() {}
}
