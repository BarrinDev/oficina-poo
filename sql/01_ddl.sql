SET NAMES utf8mb4;
SET sql_mode = 'STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

DROP DATABASE IF EXISTS ucb_turbo2;
CREATE DATABASE ucb_turbo2
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE ucb_turbo2;

CREATE TABLE cliente (
  id_cliente      INT UNSIGNED  NOT NULL AUTO_INCREMENT,
  nome            VARCHAR(120)  NOT NULL,
  email           VARCHAR(120)  NULL,
  tipo_cliente    ENUM('PF','PJ') NOT NULL,
  cpf_cnpj        VARCHAR(14)   NOT NULL,
  logradouro      VARCHAR(120)  NOT NULL,
  numero          VARCHAR(10)   NOT NULL,
  complemento     VARCHAR(60)   NULL,
  bairro          VARCHAR(60)   NOT NULL,
  cidade          VARCHAR(60)   NOT NULL,
  uf              CHAR(2)       NOT NULL,
  cep             CHAR(8)       NOT NULL,
  data_cadastro   DATE          NOT NULL DEFAULT (CURRENT_DATE),
  CONSTRAINT pk_cliente PRIMARY KEY (id_cliente),
  CONSTRAINT uq_cliente_email UNIQUE (email),
  CONSTRAINT ck_cliente_cep   CHECK (cep REGEXP '^[0-9]{8}$'),
  CONSTRAINT ck_cliente_uf CHECK (REGEXP_LIKE(uf, '^[A-Z]{2}$', 'c')),
  CONSTRAINT ck_cliente_email CHECK (email IS NULL OR email LIKE '%_@_%._%'),
  CONSTRAINT uq_cliente_cpf_cnpj UNIQUE (cpf_cnpj),
  CONSTRAINT ck_cliente_cpf_cnpj CHECK (
		(tipo_cliente = 'PF' AND cpf_cnpj REGEXP '^[0-9]{11}$')
		OR (tipo_cliente = 'PJ' AND cpf_cnpj REGEXP '^[0-9]{14}$')
    )
) ENGINE = InnoDB;

CREATE INDEX idx_cliente_nome ON cliente (nome);

CREATE TABLE funcionario (
  id_funcionario     INT UNSIGNED  NOT NULL AUTO_INCREMENT,
  nome               VARCHAR(120)  NOT NULL,
  cpf                CHAR(11)      NOT NULL,
  cargo              VARCHAR(40)   NOT NULL,
  data_admissao      DATE          NOT NULL,
  data_desligamento  DATE          NULL,
  salario            DECIMAL(10,2) NOT NULL,
  CONSTRAINT pk_funcionario PRIMARY KEY (id_funcionario),
  CONSTRAINT uq_funcionario_cpf UNIQUE (cpf),
  CONSTRAINT ck_funcionario_cpf     CHECK (cpf REGEXP '^[0-9]{11}$'),
  CONSTRAINT ck_funcionario_salario CHECK (salario > 0),
  CONSTRAINT ck_funcionario_deslig  CHECK (data_desligamento IS NULL OR data_desligamento >= data_admissao)
) ENGINE = InnoDB;

CREATE TABLE veiculo (
  id_veiculo      INT UNSIGNED      NOT NULL AUTO_INCREMENT,
  id_cliente      INT UNSIGNED      NOT NULL,
  placa           CHAR(7)           NOT NULL,
  marca           VARCHAR(40)       NOT NULL,
  modelo          VARCHAR(60)       NOT NULL,
  ano_fabricacao  SMALLINT UNSIGNED NOT NULL,
  ano_modelo      SMALLINT UNSIGNED NOT NULL,
  cor             VARCHAR(30)       NULL,
  km_atual        INT UNSIGNED      NOT NULL DEFAULT 0,
  CONSTRAINT pk_veiculo PRIMARY KEY (id_veiculo),
  CONSTRAINT uq_veiculo_placa UNIQUE (placa),
  CONSTRAINT ck_veiculo_placa CHECK (REGEXP_LIKE(placa, '^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$', 'c')),
  CONSTRAINT ck_veiculo_ano_fab    CHECK (ano_fabricacao >= 1950),
  CONSTRAINT ck_veiculo_ano_modelo CHECK (ano_modelo BETWEEN ano_fabricacao AND ano_fabricacao + 1),
  CONSTRAINT fk_veiculo_cliente FOREIGN KEY (id_cliente)
    REFERENCES cliente (id_cliente)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;

CREATE TABLE servico (
  id_servico               INT UNSIGNED      NOT NULL AUTO_INCREMENT,
  nome_servico             VARCHAR(100)      NOT NULL,
  descricao                VARCHAR(255)      NULL,
  preco_tabela             DECIMAL(10,2)     NOT NULL,
  tempo_estimado_minutos   SMALLINT UNSIGNED NOT NULL,
  garantia_dias            SMALLINT UNSIGNED NOT NULL DEFAULT 90,
  CONSTRAINT pk_servico PRIMARY KEY (id_servico),
  CONSTRAINT uq_servico_nome UNIQUE (nome_servico),
  CONSTRAINT ck_servico_preco    CHECK (preco_tabela > 0),
  CONSTRAINT ck_servico_tempo    CHECK (tempo_estimado_minutos > 0),
  CONSTRAINT ck_servico_garantia CHECK (garantia_dias <= 730)
) ENGINE = InnoDB;

CREATE TABLE ordem_servico (
  id_os                      INT UNSIGNED NOT NULL AUTO_INCREMENT,
  id_veiculo                 INT UNSIGNED NOT NULL,
  id_funcionario_responsavel INT UNSIGNED NOT NULL,
  data_abertura              DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  data_conclusao_prevista    DATE         NULL,
  data_conclusao_real        DATETIME     NULL,
  km_entrada                 INT UNSIGNED NOT NULL,
  status_atual  ENUM('aberta','em_execucao','concluida','cancelada') NOT NULL DEFAULT 'aberta',
  observacoes_gerais         VARCHAR(500) NULL,
  CONSTRAINT pk_ordem_servico PRIMARY KEY (id_os),
  CONSTRAINT ck_os_conclusao_real     CHECK (data_conclusao_real IS NULL OR data_conclusao_real >= data_abertura),
  CONSTRAINT ck_os_conclusao_prevista CHECK (data_conclusao_prevista IS NULL OR data_conclusao_prevista >= DATE(data_abertura)),
  CONSTRAINT ck_os_status_conclusao CHECK (status_atual <> 'concluida' OR data_conclusao_real IS NOT NULL),
  CONSTRAINT fk_os_veiculo FOREIGN KEY (id_veiculo)
    REFERENCES veiculo (id_veiculo)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_os_funcionario FOREIGN KEY (id_funcionario_responsavel)
    REFERENCES funcionario (id_funcionario)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;

CREATE TABLE os_servico (
  id_os                INT UNSIGNED      NOT NULL,
  id_servico           INT UNSIGNED      NOT NULL,
  valor_cobrado        DECIMAL(10,2)     NOT NULL,
  tempo_gasto_minutos  SMALLINT UNSIGNED NULL,
  CONSTRAINT pk_os_servico PRIMARY KEY (id_os, id_servico),
  CONSTRAINT ck_os_servico_valor CHECK (valor_cobrado >= 0),
  CONSTRAINT fk_os_servico_os FOREIGN KEY (id_os)
    REFERENCES ordem_servico (id_os)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_os_servico_servico FOREIGN KEY (id_servico)
    REFERENCES servico (id_servico)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;