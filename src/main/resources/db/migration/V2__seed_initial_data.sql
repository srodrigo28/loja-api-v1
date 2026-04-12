insert into usuarios (nome, email, telefone, cep, logradouro, numero, complemento, bairro, cidade, estado)
values
    ('Ana Souza', 'ana@loja99.com', '(11) 98888-1001', '01001-000', 'Praca da Se', '100', 'Sala 1', 'Se', 'Sao Paulo', 'SP'),
    ('Bruno Lima', 'bruno@loja99.com', '(21) 97777-2002', '20040-020', 'Rua da Assembleia', '200', null, 'Centro', 'Rio de Janeiro', 'RJ'),
    ('Carla Mendes', 'carla@loja99.com', '(31) 96666-3003', '30130-110', 'Avenida Afonso Pena', '300', 'Loja 5', 'Centro', 'Belo Horizonte', 'MG');

insert into categorias (nome, descricao, usuario_id)
values
    ('Eletronicos', 'Produtos e acessorios de tecnologia.', (select id from usuarios where email = 'ana@loja99.com')),
    ('Moda', 'Roupas, calcados e acessorios.', (select id from usuarios where email = 'bruno@loja99.com')),
    ('Casa', 'Itens para casa e decoracao.', (select id from usuarios where email = 'carla@loja99.com'));
