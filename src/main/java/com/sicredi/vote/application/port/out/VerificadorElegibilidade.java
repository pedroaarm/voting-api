package com.sicredi.vote.application.port.out;

public interface VerificadorElegibilidade {
    Elegibilidade verificar(String cpf);   // lança ElegibilidadeIndisponivelException se o serviço não responder
}
