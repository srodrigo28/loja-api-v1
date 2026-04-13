alter table categorias add column descricao varchar(255);
alter table categorias add column image varchar(255);

update categorias
set descricao = concat('Categoria ', nome)
where descricao is null;

update categorias
set image = image_id
where image is null
  and image_id is not null;

alter table categorias drop column image_id;
