create table pauta (
    id          uuid primary key,
    titulo      varchar(200) not null,
    descricao   varchar(2000),
    criada_em   timestamp with time zone  not null
);

create table sessao (
    id          uuid primary key,
    pauta_id    uuid not null references pauta(id),
    abertura    timestamp with time zone not null,
    fechamento  timestamp with time zone not null,
    criada_em   timestamp with time zone not null
);
create unique index uk_sessao_pauta on sessao(pauta_id);

create table voto (
    id            uuid primary key,
    pauta_id      uuid not null references pauta(id),
    associado_id  varchar(100) not null,
    opcao         varchar(3)  not null,
    criado_em     timestamp with time zone not null,
    constraint uk_voto_pauta_associado unique (pauta_id, associado_id)
);
create index idx_voto_pauta_opcao on voto(pauta_id, opcao);
