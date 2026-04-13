package com.loja99.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.loja99.repository.CategoriaRepository;
import com.loja99.repository.LojaRepository;

@SpringBootTest
class LojaControllerTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private LojaRepository lojaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        categoriaRepository.deleteAll();
        lojaRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void deveCriarLojaComSucesso() throws Exception {
        mockMvc.perform(post("/api/lojas")
                        .contentType("application/json")
                        .content(payloadLoja("Atelie Solar", "atelie-solar", "renata@ateliesolar.com", "12.345.678/0001-90", "active")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/lojas/")))
                .andExpect(jsonPath("$.name").value("Atelie Solar"))
                .andExpect(jsonPath("$.slug").value("atelie-solar"))
                .andExpect(jsonPath("$.ownerEmail").value("renata@ateliesolar.com"))
                .andExpect(jsonPath("$.cnpj").value("12345678000190"))
                .andExpect(jsonPath("$.state").value("SP"))
                .andExpect(jsonPath("$.status").value("active"));
    }

    @Test
    void deveImpedirSlugDuplicado() throws Exception {
        mockMvc.perform(post("/api/lojas")
                        .contentType("application/json")
                        .content(payloadLoja("Atelie Solar", "atelie-solar", "renata@ateliesolar.com", "12.345.678/0001-90", "active")))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/lojas")
                        .contentType("application/json")
                        .content(payloadLoja("Atelie Lua", "atelie-solar", "clara@atelielua.com", "98.765.432/0001-10", "draft")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ja existe uma loja cadastrada com este slug."));
    }

    @Test
    void deveListarLojasCriadas() throws Exception {
        mockMvc.perform(post("/api/lojas")
                        .contentType("application/json")
                        .content(payloadLoja("Casa Serena", "casa-serena", "marcos@casaserena.com", "44.555.666/0001-77", "draft")))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/lojas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Casa Serena"))
                .andExpect(jsonPath("$[0].slug").value("casa-serena"));
    }

    @Test
    void deveAtualizarStatusDaLojaComSucesso() throws Exception {
        mockMvc.perform(post("/api/lojas")
                        .contentType("application/json")
                        .content(payloadLoja("Casa Serena", "casa-serena", "marcos@casaserena.com", "44.555.666/0001-77", "active")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("active"));

        Integer lojaId = lojaRepository.findAll().stream()
                .filter(loja -> "casa-serena".equals(loja.getSlug()))
                .findFirst()
                .orElseThrow()
                .getId();

        mockMvc.perform(patch("/api/lojas/" + lojaId + "/status")
                        .contentType("application/json")
                        .content("{\"status\":\"inactive\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(lojaId))
                .andExpect(jsonPath("$.status").value("inactive"));
    }

    private String payloadLoja(String name, String slug, String ownerEmail, String cnpj, String status) {
        return """
                {
                  "name": "%s",
                  "slug": "%s",
                  "ownerName": "Responsavel Teste",
                  "ownerEmail": "%s",
                  "password": "123123@",
                  "whatsapp": "(11) 97777-9090",
                  "cnpj": "%s",
                  "pixKey": "pix@teste.com",
                  "zipCode": "01310-100",
                  "state": "SP",
                  "city": "Sao Paulo",
                  "district": "Bela Vista",
                  "street": "Avenida Paulista",
                  "number": "1500",
                  "complement": "Sala 10",
                  "status": "%s"
                }
                """.formatted(name, slug, ownerEmail, cnpj, status);
    }
}
