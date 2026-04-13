package com.loja99.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loja99.entity.Categoria;
import com.loja99.entity.Endereco;
import com.loja99.entity.Loja;
import com.loja99.repository.CategoriaRepository;
import com.loja99.repository.LojaRepository;
import com.loja99.repository.ProdutoImagemRepository;
import com.loja99.repository.ProdutoRepository;

@SpringBootTest
class ProdutoControllerTests {

    private static final Path TEST_UPLOAD_DIR = Path.of("target", "test-assets", "uploads");

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private LojaRepository lojaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ProdutoImagemRepository produtoImagemRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    private Loja loja;
    private Categoria categoria;

    @BeforeEach
    void setUp() throws IOException {
        limparUploads();
        produtoImagemRepository.deleteAll();
        produtoRepository.deleteAll();
        categoriaRepository.deleteAll();
        lojaRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        loja = lojaRepository.save(Loja.builder()
                .name("Aurora Atelier")
                .slug("aurora-atelier")
                .ownerName("Renata Sol")
                .ownerEmail("renata@ateliesolar.com")
                .passwordHash("hash-test")
                .whatsapp("11977779090")
                .cnpj("12345678000190")
                .pixKey("pix@ateliesolar.com")
                .status("active")
                .endereco(Endereco.builder()
                        .cep("01310-100")
                        .logradouro("Avenida Paulista")
                        .numero("1500")
                        .complemento("Sala 10")
                        .bairro("Bela Vista")
                        .cidade("Sao Paulo")
                        .estado("SP")
                        .build())
                .build());

        categoria = categoriaRepository.save(Categoria.builder()
                .nome("Vestidos")
                .descricao("Moda feminina premium.")
                .slug("vestidos")
                .image("/uploads/categorias/vestidos.png")
                .ativo(true)
                .loja(loja)
                .build());
    }

    @Test
    void deveCriarProdutoEEnviarImagens() throws Exception {
        Integer produtoId = criarProduto();

        mockMvc.perform(multipart("/api/produtos/{id}/images", produtoId)
                        .file(imageFile("look-1.png"))
                        .file(imageFile("look-2.webp")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].imageUrl", startsWith("/uploads/products/" + produtoId + "/")))
                .andExpect(jsonPath("$.data[0].isMain").value(true))
                .andExpect(jsonPath("$.data[1].isMain").value(false));

        mockMvc.perform(get("/api/produtos/{id}", produtoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Vestido Midi"))
                .andExpect(jsonPath("$.data.images", hasSize(2)))
                .andExpect(jsonPath("$.data.mainImageUrl", startsWith("/uploads/products/" + produtoId + "/")));
    }

    @Test
    void deveAtualizarProdutoETrocarImagemPrincipal() throws Exception {
        Integer produtoId = criarProduto();
        MvcResult uploadResult = mockMvc.perform(multipart("/api/produtos/{id}/images", produtoId)
                        .file(imageFile("look-1.png"))
                        .file(imageFile("look-2.webp")))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode uploadBody = objectMapper.readTree(uploadResult.getResponse().getContentAsString()).get("data");
        Integer segundaImagemId = uploadBody.get(1).get("id").asInt();

        mockMvc.perform(post("/api/produtos/{id}/images/{imageId}/set-main", produtoId, segundaImagemId))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/produtos/{id}", produtoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(produtoJson("Vestido Midi Premium", "vestido-midi-premium")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Vestido Midi Premium"))
                .andExpect(jsonPath("$.data.slug").value("vestido-midi-premium"));

        mockMvc.perform(get("/api/produtos/{id}", produtoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.images[0].isMain").value(false))
                .andExpect(jsonPath("$.data.images[1].isMain").value(true));
    }

    @Test
    void deveRemoverImagemESegurarUltimaImagem() throws Exception {
        Integer produtoId = criarProduto();
        MvcResult uploadResult = mockMvc.perform(multipart("/api/produtos/{id}/images", produtoId)
                        .file(imageFile("look-1.png"))
                        .file(imageFile("look-2.webp")))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode uploadBody = objectMapper.readTree(uploadResult.getResponse().getContentAsString()).get("data");
        Integer primeiraImagemId = uploadBody.get(0).get("id").asInt();
        Integer segundaImagemId = uploadBody.get(1).get("id").asInt();
        Path primeiraImagemPath = pathDaImagem(uploadBody.get(0).get("imageUrl").asText());

        mockMvc.perform(delete("/api/produtos/{id}/images/{imageId}", produtoId, primeiraImagemId))
                .andExpect(status().isOk());

        org.junit.jupiter.api.Assertions.assertFalse(Files.exists(primeiraImagemPath));

        mockMvc.perform(delete("/api/produtos/{id}/images/{imageId}", produtoId, segundaImagemId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("O produto precisa manter ao menos 1 imagem."));
    }

    @Test
    void deveExcluirProdutoELimparDiretorioDeImagens() throws Exception {
        Integer produtoId = criarProduto();
        MvcResult uploadResult = mockMvc.perform(multipart("/api/produtos/{id}/images", produtoId)
                        .file(imageFile("look-1.png")))
                .andExpect(status().isOk())
                .andReturn();

        String imageUrl = objectMapper.readTree(uploadResult.getResponse().getContentAsString())
                .get("data")
                .get(0)
                .get("imageUrl")
                .asText();

        Path productDir = pathDaImagem(imageUrl).getParent();

        mockMvc.perform(delete("/api/produtos/{id}", produtoId))
                .andExpect(status().isOk());

        org.junit.jupiter.api.Assertions.assertFalse(Files.exists(productDir));
    }

    @Test
    void deveListarProdutosPorLoja() throws Exception {
        Integer produtoId = criarProduto();
        mockMvc.perform(multipart("/api/produtos/{id}/images", produtoId)
                        .file(imageFile("look-1.png")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/produtos").param("store_id", String.valueOf(loja.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Vestido Midi"));
    }

    private Integer criarProduto() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(produtoJson("Vestido Midi", "vestido-midi")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/produtos/")))
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("data").get("id").asInt();
    }

    private String produtoJson(String name, String slug) {
        return """
                {
                  "store_id": %d,
                  "category_id": %d,
                  "name": "%s",
                  "slug": "%s",
                  "description_short": "Descricao curta do produto.",
                  "description_long": "Descricao longa do produto.",
                  "price_retail": 249.90,
                  "price_wholesale": 199.90,
                  "price_promotion": 229.90,
                  "stock": 12,
                  "min_stock": 2,
                  "status": "draft",
                  "is_featured": false,
                  "notes": null
                }
                """.formatted(loja.getId(), categoria.getId(), name, slug);
    }

    private MockMultipartFile imageFile(String filename) {
        return new MockMultipartFile(
                "images",
                filename,
                contentTypeFor(filename),
                "fake-image-content".getBytes()
        );
    }

    private String contentTypeFor(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".png")) {
            return "image/png";
        }
        if (lower.endsWith(".webp")) {
            return "image/webp";
        }
        return "image/jpeg";
    }

    private Path pathDaImagem(String imagePath) {
        String relative = imagePath.replace("/uploads/", "").replace('/', java.io.File.separatorChar);
        return TEST_UPLOAD_DIR.resolve(relative);
    }

    private void limparUploads() throws IOException {
        if (!Files.exists(TEST_UPLOAD_DIR)) {
            return;
        }

        try (var paths = Files.walk(TEST_UPLOAD_DIR)) {
            paths.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
        }
    }
}
