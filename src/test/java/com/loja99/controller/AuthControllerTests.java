package com.loja99.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loja99.repository.CategoriaRepository;
import com.loja99.repository.LojaRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class AuthControllerTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private LojaRepository lojaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        categoriaRepository.deleteAll();
        lojaRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void deveAutenticarLojaComEmailESenha() throws Exception {
        criarLojaPadrao();

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(loginPayload("renata@ateliesolar.com", "123123@")))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", containsString("loja99_auth=")))
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.user.role").value("lojista"))
                .andExpect(jsonPath("$.user.ownerEmail").value("renata@ateliesolar.com"))
                .andExpect(jsonPath("$.store.slug").value("atelie-solar"))
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void deveRejeitarLoginComSenhaInvalida() throws Exception {
        criarLojaPadrao();

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(loginPayload("renata@ateliesolar.com", "senha-errada")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Email ou senha invalidos."));
    }

    @Test
    void deveRetornarSessaoAtualPorBearerToken() throws Exception {
        criarLojaPadrao();
        String token = autenticarERetornarToken();

        mockMvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.store.name").value("Atelie Solar"))
                .andExpect(jsonPath("$.user.ownerName").value("Renata Sol"));
    }

    @Test
    void deveEncerrarSessaoComLogout() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", containsString("Max-Age=0")))
                .andExpect(jsonPath("$.authenticated").value(false))
                .andExpect(jsonPath("$.message").value("Sessao encerrada com sucesso."));
    }

    @Test
    void deveRetornarSessaoAtualPorCookie() throws Exception {
        criarLojaPadrao();
        String token = autenticarERetornarToken();

        mockMvc.perform(get("/auth/me")
                        .cookie(new Cookie("loja99_auth", token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.store.slug").value("atelie-solar"));
    }

    private String autenticarERetornarToken() throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(loginPayload("renata@ateliesolar.com", "123123@")))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode payload = objectMapper.readTree(result.getResponse().getContentAsString());
        return payload.get("token").asText();
    }

    private void criarLojaPadrao() throws Exception {
        mockMvc.perform(post("/api/lojas")
                        .contentType("application/json")
                        .content(lojaPayload()))
                .andExpect(status().isCreated());
    }

    private String loginPayload(String email, String password) {
        return """
                {
                  \"email\": \"%s\",
                  \"password\": \"%s\"
                }
                """.formatted(email, password);
    }

    private String lojaPayload() {
        return """
                {
                  \"name\": \"Atelie Solar\",
                  \"slug\": \"atelie-solar\",
                  \"ownerName\": \"Renata Sol\",
                  \"ownerEmail\": \"renata@ateliesolar.com\",
                  \"password\": \"123123@\",
                  \"whatsapp\": \"(11) 97777-9090\",
                  \"cnpj\": \"12.345.678/0001-90\",
                  \"pixKey\": \"pix@ateliesolar.com\",
                  \"zipCode\": \"01310-100\",
                  \"state\": \"SP\",
                  \"city\": \"Sao Paulo\",
                  \"district\": \"Bela Vista\",
                  \"street\": \"Avenida Paulista\",
                  \"number\": \"1500\",
                  \"complement\": \"Sala 10\",
                  \"status\": \"active\"
                }
                """;
    }
}
