# Uploads da API

Este arquivo documenta como a API Java salva e serve arquivos enviados pelo painel, com foco em backup e restauracao.

## Objetivo

Os uploads da API ficam fora do banco de dados. O banco guarda apenas os caminhos publicos, como:

- `/uploads/categorias/<arquivo>`
- `/uploads/products/<produtoId>/<arquivo>`

Por isso, um backup completo da aplicacao precisa incluir:

1. o banco de dados
2. os arquivos fisicos de upload

Se apenas o banco for restaurado, os registros continuarao existindo, mas as imagens poderao quebrar.

## Diretorio usado pela API

Configuracao principal em `application.properties`:

```properties
app.upload.base-dir=${APP_UPLOAD_BASE_DIR:assets/uploads}
```

A API resolve esse caminho para uma pasta estavel do projeto usando `UploadPathResolver`.

Na pratica, o destino esperado neste projeto e:

```text
api/assets/uploads
```

Estrutura:

```text
api/assets/uploads/
  categorias/
    <arquivo-da-categoria>
  products/
    <produtoId>/
      <arquivo-do-produto>
```

## Como a API usa esse diretorio

- Categorias:
  Os arquivos ficam em `api/assets/uploads/categorias`.
- Produtos:
  Os arquivos ficam em `api/assets/uploads/products/<produtoId>`.
- Publicacao:
  A API expoe esses arquivos pela rota `/uploads/**`.

Exemplos:

- arquivo fisico: `api/assets/uploads/categorias/banner.png`
- URL publica: `/uploads/categorias/banner.png`

- arquivo fisico: `api/assets/uploads/products/10/foto-1.png`
- URL publica: `/uploads/products/10/foto-1.png`

## O que deve entrar no backup

Para restauracao completa do sistema, incluir:

1. backup do banco PostgreSQL
2. copia da pasta `api/assets/uploads`

Recomendacao minima:

```text
api/assets/uploads/categorias
api/assets/uploads/products
```

## Rotina sugerida de backup

### Opcao simples

Salvar periodicamente:

- dump do banco
- zip da pasta `api/assets/uploads`

Exemplo de estrategia:

1. gerar dump do banco
2. compactar `api/assets/uploads`
3. armazenar os dois arquivos com a mesma data

Exemplo de nomenclatura:

```text
backup-2026-04-14-db.sql
backup-2026-04-14-uploads.zip
```

## Restauracao

Para restaurar corretamente:

1. restaurar o banco
2. restaurar a pasta `api/assets/uploads`
3. subir a API apontando para o mesmo diretorio de upload

Se a variavel `APP_UPLOAD_BASE_DIR` estiver configurada em producao, ela precisa apontar para a pasta restaurada.

## Cuidados importantes

- Nao apagar `api/assets/uploads` durante deploy.
- Nao depender apenas de `target/` ou pastas temporarias.
- Nao versionar as imagens reais no git.
- Manter apenas arquivos de placeholder como `.gitkeep`.
- Se mudar `APP_UPLOAD_BASE_DIR`, mover tambem os arquivos antigos para o novo local.

## Arquivos do codigo relacionados

- `api/src/main/resources/application.properties`
- `api/src/main/java/com/loja99/config/UploadPathResolver.java`
- `api/src/main/java/com/loja99/config/WebConfig.java`
- `api/src/main/java/com/loja99/service/CategoriaImageStorageService.java`
- `api/src/main/java/com/loja99/service/ProdutoImageStorageService.java`

## Resumo rapido

Se voce quiser garantir backup futuro sem perder imagens, preserve sempre:

```text
Banco de dados + api/assets/uploads
```

## Exemplos em PowerShell

### Backup dos uploads em ZIP

Execute na raiz do projeto:

```powershell
$backupDir = "C:\backup\loja99"
$data = Get-Date -Format "yyyy-MM-dd-HHmm"
New-Item -ItemType Directory -Force -Path $backupDir | Out-Null
Compress-Archive -Path ".\api\assets\uploads\*" -DestinationPath "$backupDir\loja99-uploads-$data.zip" -Force
```

Isso gera um arquivo como:

```text
C:\backup\loja99\loja99-uploads-2026-04-14-1215.zip
```

### Backup simples por copia de pasta

Se voce preferir copiar a pasta inteira sem compactar:

```powershell
$backupDir = "C:\backup\loja99"
$data = Get-Date -Format "yyyy-MM-dd-HHmm"
$destino = Join-Path $backupDir "uploads-$data"
New-Item -ItemType Directory -Force -Path $destino | Out-Null
Copy-Item ".\api\assets\uploads" -Destination $destino -Recurse -Force
```

### Restauracao dos uploads a partir de ZIP

Remova ou substitua o conteudo atual com cuidado e depois extraia o arquivo:

```powershell
$zip = "C:\backup\loja99\loja99-uploads-2026-04-14-1215.zip"
$destino = "C:\dev\new\project-ecomerce\api\assets\uploads"
New-Item -ItemType Directory -Force -Path $destino | Out-Null
Expand-Archive -Path $zip -DestinationPath $destino -Force
```

Observacao:

- Esse ZIP foi pensado para restaurar o conteudo dentro de `api/assets/uploads`.
- Se o arquivo compactado tiver uma pasta `uploads` na raiz, ajuste o destino final conforme a estrutura do ZIP.

### Restauracao por copia de pasta

Se o backup foi feito como copia de diretorio:

```powershell
$origem = "C:\backup\loja99\uploads-2026-04-14-1215\uploads"
$destino = "C:\dev\new\project-ecomerce\api\assets\uploads"
New-Item -ItemType Directory -Force -Path $destino | Out-Null
Copy-Item "$origem\*" -Destination $destino -Recurse -Force
```

### Backup combinado com banco

O ideal e salvar banco e uploads na mesma janela de tempo.

Exemplo de organizacao:

```text
C:\backup\loja99\
  loja99-db-2026-04-14-1215.sql
  loja99-uploads-2026-04-14-1215.zip
```

Assim fica facil restaurar os dois artefatos do mesmo momento.
