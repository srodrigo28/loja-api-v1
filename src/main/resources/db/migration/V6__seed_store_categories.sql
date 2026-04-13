insert into categorias (nome, slug, image_id, ativo, loja_id)
select 'Vestidos', 'vestidos', 'img-vestidos-aurora', true, id
from lojas
where slug = 'aurora-atelier'
and not exists (
    select 1 from categorias c
    where c.loja_id = lojas.id and c.slug = 'vestidos'
);

insert into categorias (nome, slug, image_id, ativo, loja_id)
select 'Calcas', 'calcas', 'img-calcas-aurora', true, id
from lojas
where slug = 'aurora-atelier'
and not exists (
    select 1 from categorias c
    where c.loja_id = lojas.id and c.slug = 'calcas'
);

insert into categorias (nome, slug, image_id, ativo, loja_id)
select 'Blusas', 'blusas', 'img-blusas-aurora', true, id
from lojas
where slug = 'aurora-atelier'
and not exists (
    select 1 from categorias c
    where c.loja_id = lojas.id and c.slug = 'blusas'
);
