package com.sicredi.vote.application.usecase;

import com.sicredi.vote.application.port.out.PautaRepository;
import com.sicredi.vote.domain.model.Pauta;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ListarPautas {
  private final PautaRepository pautas;

  public ListarPautas(PautaRepository pautas) {
    this.pautas = pautas;
  }

  public List<Pauta> executar() {
    return pautas.listarTodas();
  }
}
