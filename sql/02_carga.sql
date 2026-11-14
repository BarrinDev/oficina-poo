-- Carga de demonstração (dados fictícios)
-- Rodar depois do 01_ddl.sql
SET NAMES utf8mb4;
USE ucb_turbo2;

START TRANSACTION;

-- ---------- CLIENTES (7 PF + 3 PJ) ----------
INSERT INTO cliente (id_cliente, nome, email, tipo_cliente, cpf_cnpj, logradouro, numero, complemento, bairro, cidade, uf, cep, data_cadastro) VALUES
  (1,  'Maria Silva Souza',          'maria.souza@email.com',   'PF', '52998224725',    'QNM 12 Conjunto F',   '15',  NULL,        'Ceilândia Norte', 'Brasília',  'DF', '72215120', '2025-03-10'),
  (2,  'João Pedro Almeida',         'joao.almeida@email.com',  'PF', '11144477735',    'SQN 308 Bloco C',     '204', 'Apto 204',  'Asa Norte',       'Brasília',  'DF', '70747030', '2025-05-22'),
  (3,  'Ana Carolina Ribeiro',       NULL,                      'PF', '39053344705',    'Rua 12 Chácara 140',  '8',   NULL,        'Vicente Pires',   'Brasília',  'DF', '72007810', '2025-07-03'),
  (4,  'Carlos Eduardo Nunes',       'carlos.nunes@email.com',  'PF', '27819263090',    'QS 3 Rua 420',        '22',  'Casa 2',    'Águas Claras',    'Brasília',  'DF', '71940100', '2025-08-15'),
  (5,  'Fernanda Lima Costa',        'fernanda.lc@email.com',   'PF', '74682489070',    'SHIN QI 5 Conjunto 3','10',  NULL,        'Lago Norte',      'Brasília',  'DF', '71505030', '2025-10-01'),
  (6,  'Rafael Torres Mendes',       NULL,                      'PF', '84434895894',    'QR 404 Conjunto 7',   '31',  NULL,        'Samambaia Norte', 'Brasília',  'DF', '72318507', '2026-01-12'),
  (7,  'Juliana Martins Rocha',      'ju.rocha@email.com',      'PF', '63816239024',    'Rua das Pitangueiras','150', 'Bloco B',   'Taguatinga Sul',  'Brasília',  'DF', '72020150', '2026-02-20'),
  (8,  'Transportes Cerrado LTDA',   'frota@cerradotransp.com', 'PJ', '11222333000181', 'SIA Trecho 3 Lote 625','625',NULL,        'SIA',             'Brasília',  'DF', '71200030', '2025-04-02'),
  (9,  'Locadora Planalto ME',       'contato@locplanalto.com', 'PJ', '45723174000110', 'SCS Quadra 2 Bloco C','22',  'Sala 301',  'Asa Sul',         'Brasília',  'DF', '70302000', '2025-09-18'),
  (10, 'Entregas Rápidas Goiás EIRELI', NULL,                   'PJ', '34028316000103', 'Avenida T-63',        '1200',NULL,        'Setor Bueno',     'Goiânia',   'GO', '74230100', '2026-03-05');

-- ---------- FUNCIONÁRIOS (1 desligado) ----------
INSERT INTO funcionario (id_funcionario, nome, cpf, cargo, data_admissao, data_desligamento, salario) VALUES
  (1, 'Carlos Souza Pereira',   '98765432100', 'Chefe de oficina', '2015-02-02', NULL,         '7200.00'),
  (2, 'Luana Mendes Freitas',   '12312312387', 'Mecânico',         '2018-08-13', NULL,         '4300.00'),
  (3, 'Henrique Coelho Lima',   '45645645600', 'Mecânico',         '2020-04-06', NULL,         '3950.00'),
  (4, 'Bruno Araújo Dias',      '78978978911', 'Eletricista',      '2021-11-01', NULL,         '3800.00'),
  (5, 'Marcos Vieira Santos',   '32132132144', 'Mecânico',         '2017-05-15', '2026-04-30', '4100.00');

-- ---------- VEÍCULOS (placas antigas e Mercosul) ----------
INSERT INTO veiculo (id_veiculo, id_cliente, placa, marca, modelo, ano_fabricacao, ano_modelo, cor, km_atual) VALUES
  (1,  1,  'JKL4A21', 'Fiat',       'Argo',      2021, 2022, 'Branco',  48200),
  (2,  2,  'PAZ9B87', 'Volkswagen', 'Polo',      2020, 2020, 'Prata',   61500),
  (3,  3,  'JHG3344', 'Chevrolet',  'Onix',      2016, 2017, 'Preto',   112300),
  (4,  4,  'REC2C45', 'Toyota',     'Corolla',   2022, 2023, 'Cinza',   35100),
  (5,  5,  'OVF7D10', 'Honda',      'HR-V',      2019, 2019, NULL,      78900),
  (6,  6,  'JJA5512', 'Fiat',       'Uno',       2012, 2013, 'Vermelho',185400),
  (7,  7,  'SBR1E09', 'Hyundai',    'HB20',      2023, 2024, 'Azul',    22800),
  (8,  8,  'QWE3F56', 'Mercedes-Benz','Sprinter',2021, 2021, 'Branco',  154000),
  (9,  8,  'QWE3F57', 'Mercedes-Benz','Sprinter',2021, 2022, 'Branco',  139700),
  (10, 9,  'RTY8G12', 'Renault',    'Kwid',      2022, 2022, 'Branco',  57300),
  (11, 9,  'RTY8G13', 'Renault',    'Kwid',      2022, 2023, 'Prata',   49800),
  (12, 10, 'NGO4H77', 'Fiat',       'Fiorino',   2020, 2021, 'Branco',  98600);

-- ---------- SERVIÇOS ----------
INSERT INTO servico (id_servico, nome_servico, descricao, preco_tabela, tempo_estimado_minutos, garantia_dias) VALUES
  (1, 'Troca de óleo e filtro',      'Óleo sintético + filtro de óleo',           '180.00',  40,  90),
  (2, 'Alinhamento e balanceamento', 'Alinhamento 3D e balanceamento 4 rodas',    '150.00',  60,  30),
  (3, 'Revisão de freios',           'Troca de pastilhas e verificação de discos','320.00',  90, 180),
  (4, 'Troca de embreagem',          'Kit embreagem completo (mão de obra)',      '650.00', 240, 365),
  (5, 'Diagnóstico por scanner',     'Leitura de falhas via OBD2',                '120.00',  30,   0),
  (6, 'Higienização do ar-condicionado', 'Limpeza do sistema e troca do filtro de cabine', '160.00', 50, 90),
  (7, 'Troca de bateria',            'Substituição e teste do sistema de carga',  '90.00',   20, 365),
  (8, 'Revisão de suspensão',        'Amortecedores, pivôs e terminais',          '480.00', 180, 180);

-- ---------- ORDENS DE SERVIÇO (jun–nov/2026, 4 status) ----------
-- concluida: data_conclusao_real preenchida | cancelada/aberta/em_execucao: NULL
INSERT INTO ordem_servico (id_os, id_veiculo, id_funcionario_responsavel, data_abertura, data_conclusao_prevista, data_conclusao_real, km_entrada, status_atual, observacoes_gerais) VALUES
  (1,  1,  2, '2026-06-03 08:30:00', '2026-06-03', '2026-06-03 11:10:00', 41000,  'concluida',   NULL),
  (2,  6,  3, '2026-06-10 09:15:00', '2026-06-12', '2026-06-12 16:40:00', 180200, 'concluida',   'Cliente relatou barulho ao pisar na embreagem'),
  (3,  8,  1, '2026-06-18 07:50:00', '2026-06-19', '2026-06-19 15:00:00', 146500, 'concluida',   'Revisão preventiva da frota'),
  (4,  3,  3, '2026-06-25 10:00:00', '2026-06-26', NULL,                  108900, 'cancelada',   'Cliente desistiu após o orçamento'),
  (5,  2,  2, '2026-07-07 08:00:00', '2026-07-07', '2026-07-07 10:30:00', 57000,  'concluida',   NULL),
  (6,  9,  1, '2026-07-15 13:20:00', '2026-07-16', '2026-07-16 17:00:00', 132100, 'concluida',   NULL),
  (7,  5,  4, '2026-07-28 09:40:00', '2026-07-28', '2026-07-28 12:00:00', 74300,  'concluida',   'Luz de injeção acesa no painel'),
  (8,  10, 3, '2026-08-05 08:10:00', '2026-08-06', '2026-08-06 11:20:00', 52000,  'concluida',   NULL),
  (9,  4,  2, '2026-08-19 14:00:00', '2026-08-20', '2026-08-20 16:30:00', 31000,  'concluida',   NULL),
  (10, 12, 1, '2026-08-27 07:30:00', '2026-08-29', NULL,                  95100,  'cancelada',   'Veículo levado para concessionária'),
  (11, 7,  4, '2026-09-09 10:10:00', '2026-09-09', '2026-09-09 11:00:00', 19500,  'concluida',   'Bateria não segurava carga'),
  (12, 11, 3, '2026-09-22 08:45:00', '2026-09-23', '2026-09-23 10:15:00', 45200,  'concluida',   NULL),
  (13, 1,  2, '2026-10-06 09:00:00', '2026-10-07', '2026-10-07 14:30:00', 47100,  'concluida',   'Pastilhas no limite'),
  (14, 8,  1, '2026-10-20 08:20:00', '2026-10-22', '2026-10-22 17:45:00', 152800, 'concluida',   NULL),
  (15, 3,  3, '2026-10-29 15:30:00', '2026-10-30', '2026-10-30 12:00:00', 111800, 'concluida',   NULL),
  (16, 6,  2, '2026-11-04 09:30:00', '2026-11-06', NULL,                  185100, 'em_execucao', 'Aguardando amortecedores do fornecedor'),
  (17, 2,  4, '2026-11-10 10:00:00', '2026-11-11', NULL,                  61400,  'em_execucao', NULL),
  (18, 9,  1, '2026-11-12 08:00:00', '2026-11-14', NULL,                  139600, 'aberta',      'Barulho na suspensão dianteira'),
  (19, 4,  3, '2026-11-13 08:40:00', '2026-11-13', NULL,                  35100,  'aberta',      NULL);

-- ---------- ITENS DAS OS ----------
-- valor_cobrado = preço de tabela, com alguns descontos (OS 3, 6, 14: frota PJ)
INSERT INTO os_servico (id_os, id_servico, valor_cobrado, tempo_gasto_minutos) VALUES
  (1,  1, '180.00',  40),
  (1,  2, '150.00',  55),
  (2,  4, '650.00', 260),
  (3,  1, '162.00',  45),
  (3,  3, '288.00',  95),
  (3,  8, '432.00', 170),
  (4,  4, '650.00', NULL),
  (5,  1, '180.00',  35),
  (5,  6, '160.00',  50),
  (6,  1, '162.00',  40),
  (6,  2, '135.00',  60),
  (7,  5, '120.00',  30),
  (8,  1, '180.00',  40),
  (8,  2, '150.00',  65),
  (9,  1, '180.00',  40),
  (9,  6, '160.00',  45),
  (10, 8, '480.00', NULL),
  (11, 7,  '90.00',  20),
  (11, 5, '120.00',  25),
  (12, 1, '180.00',  40),
  (13, 3, '320.00',  90),
  (13, 2, '150.00',  60),
  (14, 1, '162.00',  40),
  (14, 3, '288.00', 100),
  (15, 1, '180.00',  45),
  (15, 6, '160.00',  50),
  (16, 8, '480.00', NULL),
  (16, 2, '150.00', NULL),
  (17, 5, '120.00', NULL),
  (18, 5, '120.00', NULL),
  (19, 1, '180.00', NULL);

COMMIT;