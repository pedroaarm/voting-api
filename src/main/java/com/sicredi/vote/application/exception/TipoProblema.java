package com.sicredi.vote.application.exception;

public enum TipoProblema {
  PAUTA_NAO_ENCONTRADA(
      "pauta-nao-encontrada",
      "problem.pauta-nao-encontrada.title",
      "problem.pauta-nao-encontrada.detail"),
  SESSAO_JA_ABERTA(
      "sessao-ja-aberta", "problem.sessao-ja-aberta.title", "problem.sessao-ja-aberta.detail"),
  VOTO_DUPLICADO("voto-duplicado", "problem.voto-duplicado.title", "problem.voto-duplicado.detail"),
  SESSAO_FECHADA("sessao-fechada", "problem.sessao-fechada.title", "problem.sessao-fechada.detail"),
  SESSAO_EM_ANDAMENTO(
      "sessao-em-andamento",
      "problem.sessao-em-andamento.title",
      "problem.sessao-em-andamento.detail"),
  ELEGIBILIDADE_INDISPONIVEL(
      "elegibilidade-indisponivel",
      "problem.elegibilidade-indisponivel.title",
      "problem.elegibilidade-indisponivel.detail"),
  ASSOCIADO_INELEGIVEL(
      "associado-inelegivel",
      "problem.associado-inelegivel.title",
      "problem.associado-inelegivel.detail");

  private final String slug;
  private final String titleKey;
  private final String detailKey;

  TipoProblema(String slug, String titleKey, String detailKey) {
    this.slug = slug;
    this.titleKey = titleKey;
    this.detailKey = detailKey;
  }

  public String slug() {
    return slug;
  }

  public String titleKey() {
    return titleKey;
  }

  public String detailKey() {
    return detailKey;
  }
}
