package com.carlos.fintrack.controller;

import com.carlos.fintrack.dto.categoria.CategoriaResponse;
import com.carlos.fintrack.dto.lancamento.LancamentoRequest;
import com.carlos.fintrack.exception.RecursoNaoEncontradoException;
import com.carlos.fintrack.service.CategoriaService;
import com.carlos.fintrack.service.LancamentoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiEndpointsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoriaService categoriaService;

    @MockBean
    private LancamentoService lancamentoService;

    @Test
    void deveExecutarFluxoDeTestesNaOrdemSolicitada() throws Exception {
        CategoriaResponse categoriaResponse = CategoriaResponse.builder()
                .id(1L)
                .nome("Alimentação")
                .tipo(com.carlos.fintrack.enums.TipoCategoria.DESPESA)
                .build();

        when(categoriaService.salvar(any())).thenReturn(categoriaResponse);
        when(categoriaService.listarTodos()).thenReturn(List.of(categoriaResponse));
        when(categoriaService.buscarPorId(999L))
                .thenThrow(new RecursoNaoEncontradoException("Categoria não encontrada para o id: 999"));
        when(lancamentoService.salvar(any()))
                .thenThrow(new RecursoNaoEncontradoException("Categoria não encontrada para o id: 999"));

        // 1) POST /api/categorias
        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Alimentação",
                                  "tipo": "DESPESA"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Alimentação"))
                .andExpect(jsonPath("$.tipo").value("DESPESA"));

        // 2) GET /api/categorias
        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Alimentação"))
                .andExpect(jsonPath("$[0].tipo").value("DESPESA"));

        // 3) GET /api/categorias/999
        mockMvc.perform(get("/api/categorias/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Categoria não encontrada para o id: 999"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value("/api/categorias/999"));

        // 4) POST /api/lancamentos com categoriaId inexistente
        LancamentoRequest lancamentoRequest = LancamentoRequest.builder()
                .descricao("Supermercado")
                .valor(BigDecimal.valueOf(150.00))
                .tipo(com.carlos.fintrack.enums.TipoLancamento.DESPESA)
                .dataLancamento(LocalDate.of(2026, 4, 24))
                .formaPagamento(com.carlos.fintrack.enums.FormaPagamento.PIX)
                .observacao("Compra semanal")
                .categoriaId(999L)
                .build();

        mockMvc.perform(post("/api/lancamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lancamentoRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Categoria não encontrada para o id: 999"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value("/api/lancamentos"));
    }
}
