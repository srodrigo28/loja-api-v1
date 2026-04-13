package com.loja99.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loja99.entity.Endereco;
import com.loja99.entity.Loja;
import com.loja99.repository.CategoriaRepository;
import com.loja99.repository.LojaRepository;

@SpringBootTest
class CategoriaControllerTests {

    private static final Path TEST_UPLOAD_DIR = Path.of("target", "test-assets", "uploads");

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private LojaRepository lojaRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    private Loja loja;

    @BeforeEach
    void setUp() throws IOException {
        limparUploads();
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
    }

    @Test
    void deveCriarCategoriaParaLojaComSucesso() throws Exception {
        MvcResult result = mockMvc.perform(criarMultipartCategoria("Vestidos", "Moda feminina para eventos e ocasioes especiais.", loja.getId(), true, imageFile("vestidos.png")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/categorias/")))
                .andExpect(jsonPath("$.nome").value("Vestidos"))
                .andExpect(jsonPath("$.descricao").value("Moda feminina para eventos e ocasioes especiais."))
                .andExpect(jsonPath("$.slug").value("vestidos"))
                .andExpect(jsonPath("$.image", startsWith("/uploads/categorias/")))
                .andExpect(jsonPath("$.ativo").value(true))
                .andExpect(jsonPath("$.lojaId").value(loja.getId()))
                .andExpect(jsonPath("$.lojaNome").value("Aurora Atelier"))
                .andReturn();

        assertImagemFoiSalva(result);
    }

    @Test
    void deveRejeitarCriacaoSemImagem() throws Exception {
        mockMvc.perform(criarMultipartCategoria("Vestidos", "Moda feminina para eventos e ocasioes especiais.", loja.getId(), true, null))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("A imagem da categoria e obrigatoria."));
    }

    @Test
    void deveRejeitarCriacaoComTipoDeArquivoInvalido() throws Exception {
        mockMvc.perform(criarMultipartCategoria("Vestidos", "Moda feminina para eventos e ocasioes especiais.", loja.getId(), true, invalidImageFile()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("A imagem da categoria deve estar em JPG, JPEG, PNG ou WEBP."));
    }

    @Test
    void deveListarCategoriasFiltrandoPorLoja() throws Exception {
        Loja segundaLoja = lojaRepository.save(Loja.builder()
                .name("Casa Horizonte")
                .slug("casa-horizonte")
                .ownerName("Paulo Castro")
                .ownerEmail("paulo@casahorizonte.com")
                .passwordHash("hash-test")
                .whatsapp("31988887070")
                .cnpj("98765432000110")
                .pixKey("pix@casahorizonte.com")
                .status("draft")
                .endereco(Endereco.builder()
                        .cep("30130-110")
                        .logradouro("Avenida Afonso Pena")
                        .numero("300")
                        .complemento("Loja 5")
                        .bairro("Centro")
                        .cidade("Belo Horizonte")
                        .estado("MG")
                        .build())
                .build());

        mockMvc.perform(criarMultipartCategoria("Vestidos", "Moda feminina para eventos.", loja.getId(), true, imageFile("vestidos.png")))
                .andExpect(status().isCreated());

        mockMvc.perform(criarMultipartCategoria("Decoracao", "Objetos decorativos da loja.", segundaLoja.getId(), true, imageFile("decoracao.png")))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/categorias").param("lojaId", String.valueOf(loja.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("Vestidos"));
    }

    @Test
    void deveAtualizarCategoriaDaLoja() throws Exception {
        Integer categoriaId = criarCategoriaERetornarId("Vestidos", "Moda feminina para eventos.", loja.getId(), true, imageFile("vestidos.png"));
        Path imagemAnterior = pathDaImagem(buscarImagemDaCategoria(categoriaId));

        MvcResult result = mockMvc.perform(atualizarMultipartCategoria(categoriaId, "Vestidos de Festa", "Modelos premium para festas e cerimonias.", loja.getId(), false, imageFile("vestidos-festa.webp")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Vestidos de Festa"))
                .andExpect(jsonPath("$.descricao").value("Modelos premium para festas e cerimonias."))
                .andExpect(jsonPath("$.slug").value("vestidos-de-festa"))
                .andExpect(jsonPath("$.image", startsWith("/uploads/categorias/")))
                .andExpect(jsonPath("$.ativo").value(false))
                .andReturn();

        assertImagemFoiSalva(result);
        org.junit.jupiter.api.Assertions.assertFalse(Files.exists(imagemAnterior));
    }

    @Test
    void deveExcluirCategoriaDaLoja() throws Exception {
        Integer categoriaId = criarCategoriaERetornarId("Blusas", "Blusas leves para o dia a dia.", loja.getId(), true, imageFile("blusas.jpg"));
        Path imagemSalva = pathDaImagem(buscarImagemDaCategoria(categoriaId));

        mockMvc.perform(delete("/api/categorias/{id}", categoriaId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/categorias").param("lojaId", String.valueOf(loja.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        org.junit.jupiter.api.Assertions.assertFalse(Files.exists(imagemSalva));
    }

    private Integer criarCategoriaERetornarId(String nome, String descricao, Integer lojaId, boolean ativo, MockMultipartFile image) throws Exception {
        MvcResult result = mockMvc.perform(criarMultipartCategoria(nome, descricao, lojaId, ativo, image))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("id").asInt();
    }

    private String buscarImagemDaCategoria(Integer categoriaId) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/categorias/{id}", categoriaId))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("image").asText();
    }

    private Path pathDaImagem(String imagePath) {
        String filename = imagePath.substring(imagePath.lastIndexOf('/') + 1);
        return TEST_UPLOAD_DIR.resolve("categorias").resolve(filename);
    }

    private MockMultipartHttpServletRequestBuilder criarMultipartCategoria(String nome, String descricao, Integer lojaId, boolean ativo, MockMultipartFile image) {
        MockMultipartHttpServletRequestBuilder builder = multipart("/api/categorias")
                .param("nome", nome)
                .param("descricao", descricao)
                .param("lojaId", String.valueOf(lojaId))
                .param("ativo", String.valueOf(ativo));

        if (image != null) {
            builder.file(image);
        }

        return builder;
    }

    private MockMultipartHttpServletRequestBuilder atualizarMultipartCategoria(Integer id, String nome, String descricao, Integer lojaId, boolean ativo, MockMultipartFile image) {
        MockMultipartHttpServletRequestBuilder builder = multipart("/api/categorias/{id}", id)
                .file(image)
                .param("nome", nome)
                .param("descricao", descricao)
                .param("lojaId", String.valueOf(lojaId))
                .param("ativo", String.valueOf(ativo));

        return builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });
    }

    private MockMultipartFile imageFile(String filename) {
        return new MockMultipartFile(
                "image",
                filename,
                contentTypeFor(filename),
                "fake-image-content".getBytes()
        );
    }

    private MockMultipartFile invalidImageFile() {
        return new MockMultipartFile(
                "image",
                "categoria.txt",
                "text/plain",
                "arquivo-invalido".getBytes()
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

    private void assertImagemFoiSalva(MvcResult result) throws Exception {
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        String imagePath = body.get("image").asText();
        Path savedFile = pathDaImagem(imagePath);
        org.junit.jupiter.api.Assertions.assertTrue(Files.exists(savedFile));
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
