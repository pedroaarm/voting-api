package com.sicredi.vote.application.exception;

public class ElegibilidadeIndisponivelException extends RuntimeException {
    public ElegibilidadeIndisponivelException(String msg, Throwable causa) {
        super(msg, causa);
    }

    public ElegibilidadeIndisponivelException(String msg) {
        super(msg);
    }
}
