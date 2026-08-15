package com.sicredi.vote.adapters.web;

import com.sicredi.vote.application.exception.*;
import java.net.URI;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProblemDetailAdvice {

  private final MessageSource messages;
  private final VoteMetrics metrics;

  public ProblemDetailAdvice(MessageSource messages, VoteMetrics metrics) {
    this.messages = messages;
    this.metrics = metrics;
  }

  private ProblemDetail problem(HttpStatus status, String slug) {
    String title =
        messages.getMessage("problem." + slug + ".title", null, LocaleContextHolder.getLocale());
    String detail =
        messages.getMessage("problem." + slug + ".detail", null, LocaleContextHolder.getLocale());
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
    pd.setType(URI.create("urn:vote:" + slug));
    pd.setTitle(title);
    return pd;
  }

  @ExceptionHandler(PautaNaoEncontradaException.class)
  ProblemDetail handle(PautaNaoEncontradaException e) {
    return problem(HttpStatus.NOT_FOUND, "pauta-nao-encontrada");
  }

  @ExceptionHandler(SessaoJaAbertaException.class)
  ProblemDetail handle(SessaoJaAbertaException e) {
    return problem(HttpStatus.CONFLICT, "sessao-ja-aberta");
  }

  @ExceptionHandler(VotoDuplicadoException.class)
  ProblemDetail handle(VotoDuplicadoException e) {
    return problem(HttpStatus.CONFLICT, "voto-duplicado");
  }

  @ExceptionHandler(SessaoFechadaException.class)
  ProblemDetail handle(SessaoFechadaException e) {
    return problem(HttpStatus.UNPROCESSABLE_ENTITY, "sessao-fechada");
  }

  @ExceptionHandler(SessaoEmAndamentoException.class)
  ProblemDetail handle(SessaoEmAndamentoException e) {
    return problem(HttpStatus.CONFLICT, "sessao-em-andamento");
  }

  @ExceptionHandler(AssociadoInelegivelException.class)
  ProblemDetail handle(AssociadoInelegivelException e) {
    metrics.votoRecusado("inelegivel");
    return problem(HttpStatus.UNPROCESSABLE_ENTITY, "associado-inelegivel");
  }

  @ExceptionHandler(ElegibilidadeIndisponivelException.class)
  ProblemDetail handle(ElegibilidadeIndisponivelException e) {
    metrics.votoRecusado("indisponivel");
    return problem(HttpStatus.SERVICE_UNAVAILABLE, "elegibilidade-indisponivel");
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ProblemDetail handle(MethodArgumentNotValidException e) {
    ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "validacao");
    var erros =
        e.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .toList();
    pd.setProperty("errors", erros);
    return pd;
  }

  @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
  ProblemDetail handleUnreadable(
      org.springframework.http.converter.HttpMessageNotReadableException e) {
    return problem(org.springframework.http.HttpStatus.BAD_REQUEST, "validacao");
  }

  @ExceptionHandler(
      org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
  ProblemDetail handleTypeMismatch(
      org.springframework.web.method.annotation.MethodArgumentTypeMismatchException e) {
    return problem(org.springframework.http.HttpStatus.BAD_REQUEST, "validacao");
  }
}
