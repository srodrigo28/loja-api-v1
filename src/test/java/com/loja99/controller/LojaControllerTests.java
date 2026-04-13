package com.loja99.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        String payload = """
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
                  \"state\": \"sp\",
                  \"city\": \"Sao Paulo\",
                  \"district\": \"Bela Vista\",
                  \"street\": \"Avenida Paulista\",
                  \"number\": \"1500\",
                  \"complement\": \"Sala 10\",
                  \"status\": \"active\"
                }
                """;

        mockMvc.perform(post("/api/lojas")
                        .contentType("application/json")
                        .content(payload))
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
        String payload = """
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

        String payloadDuplicado = """
                {
                  \"name\": \"Atelie Lua\",
                  \"slug\": \"atelie-solar\",
                  \"ownerName\": \"Clara Lua\",
                  \"ownerEmail\": \"clara@atelielua.com\",
                  \"password\": \"123123@\",
                  \"whatsapp\": \"(11) 98888-8080\",
                  \"cnpj\": \"98.765.432/0001-10\",
                  \"pixKey\": \"pix@atelielua.com\",
                  \"zipCode\": \"01001-000\",
                  \"state\": \"SP\",
                  \"city\": \"Sao Paulo\",
                  \"district\": \"Se\",
                  \"street\": \"Praca da Se\",
                  \"number\": \"100\",
                  \"complement\": \"Sala 1\",
                  \"status\": \"draft\"
                }
                """;

        mockMvc.perform(post("/api/lojas")
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/lojas")
                        .contentType("application/json")
                        .content(payloadDuplicado))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ja existe uma loja cadastrada com este slug."));
    }

    @Test
    void deveListarLojasCriadas() throws Exception {
        String payload = """
                {
                  \"name\": \"Casa Serena\",
                  \"slug\": \"casa-serena\",
                  \"ownerName\": \"Marcos Luz\",
                  \"ownerEmail\": \"marcos@casaserena.com\",
                  \"password\": \"123123@\",
                  \"whatsapp\": \"(31) 98888-7070\",
                  \"cnpj\": \"44.555.666/0001-77\",
                  \"pixKey\": \"pix@casaserena.com\",
                  \"zipCode\": \"30130-110\",
                  \"state\": \"MG\",
                  \"city\": \"Belo Horizonte\",
                  \"district\": \"Centro\",
                  \"street\": \"Avenida Afonso Pena\",
                  \"number\": \"300\",
                  \"complement\": \"Loja 5\",
                  \"status\": \"draft\"
                }
                """;

        mockMvc.perform(post("/api/lojas")
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/lojas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Casa Serena"))
                .andExpect(jsonPath("$[0].slug").value("casa-serena"));
    }
}
