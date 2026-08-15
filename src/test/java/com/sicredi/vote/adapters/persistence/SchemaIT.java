package com.sicredi.vote.adapters.persistence;

import com.sicredi.vote.support.AbstractPostgresIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class SchemaIT extends AbstractPostgresIT {

    @Autowired
    JdbcTemplate jdbc;

    @Test
    void migracaoCriaTabelasEsperadas() {
        Integer tabelas = jdbc.queryForObject(
            "select count(*) from information_schema.tables " +
            "where table_schema = 'public' and table_name in ('pauta','sessao','voto')",
            Integer.class);
        assertThat(tabelas).isEqualTo(3);
    }

    @Test
    void votoTemConstraintUnicaPorPautaEAssociado() {
        Integer unique = jdbc.queryForObject(
            "select count(*) from information_schema.table_constraints " +
            "where table_name = 'voto' and constraint_type = 'UNIQUE'",
            Integer.class);
        assertThat(unique).isGreaterThanOrEqualTo(1);
    }
}
