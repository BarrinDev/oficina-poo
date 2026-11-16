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
  tipo_cliente    ENUM('PF','PJ') NOT NULL  COMMENT 'PF = pessoa física, PJ = pessoa jurídica',
  cpf_cnpj        VARCHAR(14)   NOT NULL    COMMENT 'Só dígitos: 11 (PF) ou 14 (PJ)',
  logradouro      VARCHAR(120)  NOT NULL,
  numero          VARCHAR(10)   NOT NULL,
  complemento     VARCHAR(60)   NULL,
  bairro          VARCHAR(60)   NOT NULL,
  cidade          VARCHAR(60)   NOT NULL,
  uf              CHAR(2)       NOT NULL    COMMENT 'Sigla em maiúsculas (ex.: DF)',
  cep             CHAR(8)       NOT NULL    COMMENT 'Só dígitos',
  data_cadastro   DATE          NOT NULL DEFAULT (CURRENT_DATE),
  CONSTRAINT pk_cliente PRIMARY KEY (id_cliente),
  CONSTRAINT uq_cliente_email UNIQUE (email),
  CONSTRAINT ck_cliente_cep   CHECK (cep REGEXP '^[0-9]{8}$'),
  CONSTRAINT ck_cliente_uf    CHECK (REGEXP_LIKE(uf, '^[A-Z]{2}$', 'c')),
  CONSTRAINT ck_cliente_email CHECK (email IS NULL OR email LIKE '%_@_%._%'),
  CONSTRAINT uq_cliente_cpf_cnpj UNIQUE (cpf_cnpj),
  CONSTRAINT ck_cliente_cpf_cnpj CHECK (
       (tipo_cliente = 'PF' AND cpf_cnpj REGEXP '^[0-9]{11}$')
    OR (tipo_cliente = 'PJ' AND cpf_cnpj REGEXP '^[0-9]{14}$')
  )
) ENGINE = InnoDB COMMENT = 'Clientes da oficina (donos dos veículos)';

CREATE INDEX idx_cliente_nome ON cliente (nome);

CREATE TABLE funcionario (
  id_funcionario     INT UNSIGNED  NOT NULL AUTO_INCREMENT,
  nome               VARCHAR(120)  NOT NULL,
  cpf                CHAR(11)      NOT NULL  COMMENT 'Só dígitos',
  cargo              VARCHAR(40)   NOT NULL,
  data_admissao      DATE          NOT NULL,
  data_desligamento  DATE          NULL      COMMENT 'NULL = funcionário ativo',
  salario            DECIMAL(10,2) NOT NULL,
  CONSTRAINT pk_funcionario PRIMARY KEY (id_funcionario),
  CONSTRAINT uq_funcionario_cpf UNIQUE (cpf),
  CONSTRAINT ck_funcionario_cpf     CHECK (cpf REGEXP '^[0-9]{11}$'),
  CONSTRAINT ck_funcionario_salario CHECK (salario > 0),
  CONSTRAINT ck_funcionario_deslig  CHECK (data_desligamento IS NULL OR data_desligamento >= data_admissao)
) ENGINE = InnoDB COMMENT = 'Funcionários; responsáveis pelas ordens de serviço';

CREATE TABLE veiculo (
  id_veiculo      INT UNSIGNED      NOT NULL AUTO_INCREMENT,
  id_cliente      INT UNSIGNED      NOT NULL  COMMENT 'Dono do veículo',
  placa           CHAR(7)           NOT NULL  COMMENT 'Formato antigo (ABC1234) ou Mercosul (ABC1D23), maiúsculas',
  marca           VARCHAR(40)       NOT NULL,
  modelo          VARCHAR(60)       NOT NULL,
  ano_fabricacao  SMALLINT UNSIGNED NOT NULL,
  ano_modelo      SMALLINT UNSIGNED NOT NULL  COMMENT 'Igual ao ano de fabricação ou +1',
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
) ENGINE = InnoDB COMMENT = 'Veículos atendidos; cada um pertence a um cliente';

CREATE TABLE servico (
  id_servico               INT UNSIGNED      NOT NULL AUTO_INCREMENT,
  nome_servico             VARCHAR(100)      NOT NULL,
  descricao                VARCHAR(255)      NULL,
  preco_tabela             DECIMAL(10,2)     NOT NULL  COMMENT 'Preço sugerido; o valor cobrado fica em os_servico',
  tempo_estimado_minutos   SMALLINT UNSIGNED NOT NULL,
  garantia_dias            SMALLINT UNSIGNED NOT NULL DEFAULT 90  COMMENT 'Máximo de 730 dias',
  CONSTRAINT pk_servico PRIMARY KEY (id_servico),
  CONSTRAINT uq_servico_nome UNIQUE (nome_servico),
  CONSTRAINT ck_servico_preco    CHECK (preco_tabela > 0),
  CONSTRAINT ck_servico_tempo    CHECK (tempo_estimado_minutos > 0),
  CONSTRAINT ck_servico_garantia CHECK (garantia_dias <= 730)
) ENGINE = InnoDB COMMENT = 'Catálogo de serviços oferecidos pela oficina';

CREATE TABLE ordem_servico (
  id_os                      INT UNSIGNED NOT NULL AUTO_INCREMENT,
  id_veiculo                 INT UNSIGNED NOT NULL,
  id_funcionario_responsavel INT UNSIGNED NOT NULL,
  data_abertura              DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  data_conclusao_prevista    DATE         NULL,
  data_conclusao_real        DATETIME     NULL  COMMENT 'Obrigatória quando status = concluida',
  km_entrada                 INT UNSIGNED NOT NULL  COMMENT 'Quilometragem na entrada do veículo',
  status_atual  ENUM('aberta','em_execucao','concluida','cancelada') NOT NULL DEFAULT 'aberta'
                COMMENT 'Situação atual da OS',
  observacoes_gerais         VARCHAR(500) NULL,
  CONSTRAINT pk_ordem_servico PRIMARY KEY (id_os),
  CONSTRAINT ck_os_conclusao_real     CHECK (data_conclusao_real IS NULL OR data_conclusao_real >= data_abertura),
  CONSTRAINT ck_os_conclusao_prevista CHECK (data_conclusao_prevista IS NULL OR data_conclusao_prevista >= DATE(data_abertura)),
  CONSTRAINT ck_os_status_conclusao   CHECK (status_atual <> 'concluida' OR data_conclusao_real IS NOT NULL),
  CONSTRAINT fk_os_veiculo FOREIGN KEY (id_veiculo)
    REFERENCES veiculo (id_veiculo)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_os_funcionario FOREIGN KEY (id_funcionario_responsavel)
    REFERENCES funcionario (id_funcionario)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB COMMENT = 'Ordens de serviço (rotina principal do sistema)';

-- Acelera o relatório por período (filtro em data_abertura)
CREATE INDEX idx_os_data_abertura ON ordem_servico (data_abertura);

CREATE TABLE os_servico (
  id_os                INT UNSIGNED      NOT NULL,
  id_servico           INT UNSIGNED      NOT NULL,
  valor_cobrado        DECIMAL(10,2)     NOT NULL  COMMENT 'Valor efetivamente cobrado (pode ter desconto)',
  tempo_gasto_minutos  SMALLINT UNSIGNED NULL      COMMENT 'NULL = serviço ainda não executado',
  CONSTRAINT pk_os_servico PRIMARY KEY (id_os, id_servico),
  CONSTRAINT ck_os_servico_valor CHECK (valor_cobrado >= 0),
  CONSTRAINT fk_os_servico_os FOREIGN KEY (id_os)
    REFERENCES ordem_servico (id_os)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_os_servico_servico FOREIGN KEY (id_servico)
    REFERENCES servico (id_servico)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB COMMENT = 'Serviços de cada OS (itens); PK composta impede serviço repetido na mesma OS';

-- V01. Resumo de cada OS com cliente, veículo, responsável e valor total (base do relatório)
-- Uso no Java: SELECT * FROM vw_os_resumo WHERE data_abertura >= ? AND data_abertura < ? ORDER BY data_abertura
CREATE OR REPLACE VIEW vw_os_resumo AS
SELECT o.id_os,
       o.data_abertura,                 -- sem DATE_FORMAT: o filtro por período usa esta coluna
       o.status_atual,
       c.nome  AS cliente,
       v.placa,
       f.nome  AS responsavel,
       COALESCE(SUM(os.valor_cobrado), 0) AS valor_total
  FROM ordem_servico o
  JOIN veiculo v     ON v.id_veiculo = o.id_veiculo
  JOIN cliente c     ON c.id_cliente = v.id_cliente
  JOIN funcionario f ON f.id_funcionario = o.id_funcionario_responsavel
  LEFT JOIN os_servico os ON os.id_os = o.id_os
 GROUP BY o.id_os, o.data_abertura, o.status_atual, c.nome, v.placa, f.nome;

-- Usuário da aplicação (menor privilégio: só lê e grava dados, não altera estrutura)
-- Senha só de desenvolvimento; é a mesma para todo o grupo (usar na classe Conexao)
DROP USER IF EXISTS 'oficina_app'@'localhost';
CREATE USER 'oficina_app'@'localhost' IDENTIFIED BY 'Oficina@2026';
GRANT SELECT, INSERT, UPDATE, DELETE ON ucb_turbo2.* TO 'oficina_app'@'localhost';