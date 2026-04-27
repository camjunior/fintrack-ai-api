package com.carlos.fintrack.controller;

import com.carlos.fintrack.dto.lancamento.LancamentoResponse;
import com.carlos.fintrack.enums.TipoLancamento;
import com.carlos.fintrack.service.LancamentoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LancamentoController.class)
class LancamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LancamentoService lancamentoService;

    @Test
    void deveListarLancamentosComFiltrosNaOrdemSolicitada() throws Exception {
        when(lancamentoService.listarTodos(null, null, null, null)).thenReturn(List.<LancamentoResponse>of());
        when(lancamentoService.listarTodos(4, 2026, null, null)).thenReturn(List.<LancamentoResponse>of());
        when(lancamentoService.listarTodos(null, null, TipoLancamento.DESPESA, null)).thenReturn(List.<LancamentoResponse>of());
        when(lancamentoService.listarTodos(null, null, null, 1L)).thenReturn(List.<LancamentoResponse>of());
        when(lancamentoService.listarTodos(4, 2026, TipoLancamento.DESPESA, 1L)).thenReturn(List.<LancamentoResponse>of());

        mockMvc.perform(get("/api/lancamentos"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/lancamentos").param("mes", "4").param("ano", "2026"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/lancamentos").param("tipo", "DESPESA"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/lancamentos").param("categoriaId", "1"))
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/api/lancamentos")
                                .param("mes", "4")
                                .param("ano", "2026")
                                .param("tipo", "DESPESA")
                                .param("categoriaId", "1")
                )
                .andExpect(status().isOk());

        verify(lancamentoService, times(1)).listarTodos(null, null, null, null);
        verify(lancamentoService, times(1)).listarTodos(4, 2026, null, null);
        verify(lancamentoService, times(1)).listarTodos(null, null, TipoLancamento.DESPESA, null);
        verify(lancamentoService, times(1)).listarTodos(null, null, null, 1L);
        verify(lancamentoService, times(1)).listarTodos(eq(4), eq(2026), eq(TipoLancamento.DESPESA), eq(1L));
    }
}
