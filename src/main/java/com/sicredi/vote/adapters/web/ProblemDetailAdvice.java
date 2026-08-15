package com.sicredi.vote.adapters.web;

import com.sicredi.vote.application.exception.*;
import java.net.URI;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ProblemDetailAdvice {

  private static final String VALIDACAO_SLUG = "validacao";
  private static final String VALIDACAO_TITLE_KEY = "problem.validacao.title";
  private static final String VALIDACAO_DETAIL_KEY = "problem.validacao.detail";

  private final MessageSource messages;
  private final VoteMetrics metrics;

  public ProblemDetailAdvice(MessageSource messages, VoteMetrics metrics) {
    this.messages = messages;
    this.metrics = metrics;
  }

  private ProblemDetail problem(HttpStatus status, AplicacaoException exception) {
    return problem(status, exception.tipoProblema());
  }

  private ProblemDetail problem(HttpStatus status, TipoProblema tipoProblema) {
    return problem(status, tipoProblema.slug(), tipoProblema.titleKey(), tipoProblema.detailKey());
  }

  private ProblemDetail validacaoProblem(HttpStatus status) {
    return problem(status, VALIDACAO_SLUG, VALIDACAO_TITLE_KEY, VALIDACAO_DETAIL_KEY);
  }

  private ProblemDetail problem(HttpStatus status, String slug, String titleKey, String detailKey) {
    var locale = LocaleContextHolder.getLocale();
    String title = messages.getMessage(titleKey, null, locale);
    String detail = messages.getMessage(detailKey, null, locale);
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
    pd.setType(URI.create("urn:vote:" + slug));
    pd.setTitle(title);
    return pd;
  }

  @ExceptionHandler(PautaNaoEncontradaException.class)
  ProblemDetail handle(PautaNaoEncontradaException e) {
    return problem(HttpStatus.NOT_FOUND, e);
  }

  @ExceptionHandler(SessaoJaAbertaException.class)
  ProblemDetail handle(SessaoJaAbertaException e) {
    return problem(HttpStatus.CONFLICT, e);
  }

  @ExceptionHandler(VotoDuplicadoException.class)
  ProblemDetail handle(VotoDuplicadoException e) {
    metrics.votoRecusado("duplicado");
    return problem(HttpStatus.CONFLICT, e);
  }

  @ExceptionHandler(SessaoFechadaException.class)
  ProblemDetail handle(SessaoFechadaException e) {
    metrics.votoRecusado("sessao_fechada");
    return problem(HttpStatus.UNPROCESSABLE_ENTITY, e);
  }

  @ExceptionHandler(SessaoEmAndamentoException.class)
  ProblemDetail handle(SessaoEmAndamentoException e) {
    return problem(HttpStatus.CONFLICT, e);
  }

  @ExceptionHandler(AssociadoInelegivelException.class)
  ProblemDetail handle(AssociadoInelegivelException e) {
    metrics.votoRecusado("inelegivel");
    return problem(HttpStatus.UNPROCESSABLE_ENTITY, e);
  }

  @ExceptionHandler(ElegibilidadeIndisponivelException.class)
  ProblemDetail handle(ElegibilidadeIndisponivelException e) {
    metrics.votoRecusado("indisponivel");
    return problem(HttpStatus.SERVICE_UNAVAILABLE, e);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ProblemDetail handle(MethodArgumentNotValidException e) {
    ProblemDetail pd = validacaoProblem(HttpStatus.BAD_REQUEST);
    var erros =
        e.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .toList();
    pd.setProperty("errors", erros);
    return pd;
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  ProblemDetail handleUnreadable(HttpMessageNotReadableException e) {
    return validacaoProblem(HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException e) {
    return validacaoProblem(HttpStatus.BAD_REQUEST);
  }
}
