package com.loja99.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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
    void setUp() {
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
        String payload = payloadCategoria("Vestidos", "vestidos", "img-vestidos-01", loja.getId(), true);

        mockMvc.perform(post("/api/categorias")
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/categorias/")))
                .andExpect(jsonPath("$.nome").value("Vestidos"))
                .andExpect(jsonPath("$.slug").value("vestidos"))
                .andExpect(jsonPath("$.imageId").value("img-vestidos-01"))
                .andExpect(jsonPath("$.ativo").value(true))
                .andExpect(jsonPath("$.lojaId").value(loja.getId()))
                .andExpect(jsonPath("$.lojaNome").value("Aurora Atelier"));
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

        mockMvc.perform(post("/api/categorias")
                        .contentType("application/json")
                        .content(payloadCategoria("Vestidos", "vestidos", "img-vestidos-01", loja.getId(), true)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/categorias")
                        .contentType("application/json")
                        .content(payloadCategoria("Decoracao", "decoracao", "img-decoracao-01", segundaLoja.getId(), true)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/categorias").param("lojaId", String.valueOf(loja.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("Vestidos"));
    }

    @Test
    void deveAtualizarCategoriaDaLoja() throws Exception {
        Integer categoriaId = criarCategoriaERetornarId(payloadCategoria("Vestidos", "vestidos", "img-vestidos-01", loja.getId(), true));

        mockMvc.perform(put("/api/categorias/{id}", categoriaId)
                        .contentType("application/json")
                        .content(payloadCategoria("Vestidos de Festa", "vestidos-festa", "img-vestidos-02", loja.getId(), false)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Vestidos de Festa"))
                .andExpect(jsonPath("$.slug").value("vestidos-festa"))
                .andExpect(jsonPath("$.imageId").value("img-vestidos-02"))
                .andExpect(jsonPath("$.ativo").value(false));
    }

    @Test
    void deveExcluirCategoriaDaLoja() throws Exception {
        Integer categoriaId = criarCategoriaERetornarId(payloadCategoria("Blusas", "blusas", "img-blusas-01", loja.getId(), true));

        mockMvc.perform(delete("/api/categorias/{id}", categoriaId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/categorias").param("lojaId", String.valueOf(loja.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    private Integer criarCategoriaERetornarId(String payload) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/categorias")
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("id").asInt();
    }

    private String payloadCategoria(String nome, String slug, String imageId, Integer lojaId, boolean ativo) {
        return """
                {
                  \"nome\": \"%s\",
                  \"slug\": \"%s\",
                  \"imageId\": \"%s\",
                  \"lojaId\": %d,
                  \"ativo\": %s
                }
                """.formatted(nome, slug, imageId, lojaId, ativo);
    }
}
