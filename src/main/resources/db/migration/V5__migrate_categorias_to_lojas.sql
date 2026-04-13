alter table categorias add column slug varchar(120);
alter table categorias add column image_id varchar(120);
alter table categorias add column ativo boolean not null default true;
alter table categorias add column loja_id integer;

update categorias
set slug = case
    when lower(nome) = 'eletronicos' then 'eletronicos'
    when lower(nome) = 'moda' then 'moda'
    when lower(nome) = 'casa' then 'casa'
    else 'categoria-' || id
end;

update categorias
set loja_id = (select id from lojas where slug = 'aurora-atelier')
where loja_id is null;

alter table categorias alter column slug set not null;
alter table categorias alter column loja_id set not null;

alter table categorias drop constraint if exists fk_categorias_usuario;
alter table categorias add constraint fk_categorias_loja foreign key (loja_id) references lojas (id);
alter table categorias add constraint uk_categorias_loja_slug unique (loja_id, slug);

alter table categorias drop column descricao;
alter table categorias drop column usuario_id;
