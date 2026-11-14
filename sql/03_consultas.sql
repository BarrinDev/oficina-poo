USE ucb_turbo2;

-- C01. Listagem de OS com veículo, cliente e responsável (tela "Consultar OS")
SELECT o.id_os,
       DATE_FORMAT(o.data_abertura, '%d/%m/%Y %H:%i') AS abertura,
       o.status_atual,
       v.placa,
       v.modelo,
       c.nome AS nome_cliente,
       f.nome AS nome_funcionario_responsavel
  FROM ordem_servico o
  JOIN veiculo     v ON v.id_veiculo = o.id_veiculo
  JOIN cliente     c ON c.id_cliente = v.id_cliente
  JOIN funcionario f ON f.id_funcionario = o.id_funcionario_responsavel
 ORDER BY o.data_abertura DESC;


-- C02. Serviços de uma OS, com subtotal (tela de detalhe da OS)
-- Parâmetro: id_os  (no Java: ?)
SELECT s.nome_servico,
       os.valor_cobrado,
       os.tempo_gasto_minutos
  FROM os_servico os
  JOIN servico s ON s.id_servico = os.id_servico
 WHERE os.id_os = 3;          -- teste com 3


-- R01. Relatório: OS abertas em um período, com valor total
-- Parâmetros: data inicial, data final  (no Java: ?, ?)
SELECT o.id_os,
       DATE_FORMAT(o.data_abertura, '%d/%m/%Y') AS abertura,
       o.status_atual,
       c.nome  AS cliente,
       v.placa,
       COALESCE(SUM(os.valor_cobrado), 0) AS valor_total
  FROM ordem_servico o
  JOIN veiculo v        ON v.id_veiculo = o.id_veiculo
  JOIN cliente c        ON c.id_cliente = v.id_cliente
  LEFT JOIN os_servico os ON os.id_os = o.id_os
 WHERE o.data_abertura >= '2026-10-01'
   AND o.data_abertura <  '2026-11-01'
 GROUP BY o.id_os, o.data_abertura, o.status_atual, c.nome, v.placa
 ORDER BY o.data_abertura;
 

-- R02. Resumo por status: quantidade de OS e faturamento (opcional)
SELECT o.status_atual,
       COUNT(DISTINCT o.id_os)   AS qtd_os,
       COALESCE(SUM(CASE WHEN o.status_atual = 'cancelada' THEN 0
                  ELSE os.valor_cobrado END), 0) AS total
  FROM ordem_servico o
  LEFT JOIN os_servico os ON os.id_os = o.id_os
 GROUP BY o.status_atual;