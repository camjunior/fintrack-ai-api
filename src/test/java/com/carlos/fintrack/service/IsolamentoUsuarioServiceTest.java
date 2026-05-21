package com.carlos.fintrack.service;

import com.carlos.fintrack.dto.categoria.CategoriaRequest;
import com.carlos.fintrack.dto.dashboard.ResumoResponse;
import com.carlos.fintrack.dto.lancamento.LancamentoRequest;
import com.carlos.fintrack.entity.Categoria;
import com.carlos.fintrack.entity.Lancamento;
import com.carlos.fintrack.entity.Usuario;
import com.carlos.fintrack.enums.FormaPagamento;
import com.carlos.fintrack.enums.TipoCategoria;
import com.carlos.fintrack.enums.TipoLancamento;
import com.carlos.fintrack.exception.RecursoNaoEncontradoException;
import com.carlos.fintrack.repository.CategoriaRepository;
import com.carlos.fintrack.repository.LancamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IsolamentoUsuarioServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private LancamentoRepository lancamentoRepository;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @InjectMocks
    private CategoriaService categoriaService;

    @InjectMocks
    private LancamentoService lancamentoService;

    @InjectMocks
    private DashboardService dashboardService;

    private Usuario usuarioA;
    private Usuario usuarioB;

    @BeforeEach
    void setup() {
        usuarioA = Usuario.builder().id(1L).nome("A").email("a@a.com").senha("x").build();
        usuarioB = Usuario.builder().id(2L).nome("B").email("b@b.com").senha("x").build();
    }

    @Test
    void usuarioA_criaCategoriaELancamento_eUsuarioBNaoAcessa() {
        when(usuarioAutenticadoService.obterUsuarioLogado()).thenReturn(usuarioA);

        CategoriaRequest categoriaRequest = CategoriaRequest.builder().nome("Salário").tipo(TipoCategoria.RECEITA).build();
        Categoria categoriaA = Categoria.builder().id(10L).nome("Salário").tipo(TipoCategoria.RECEITA).usuario(usuarioA).build();

        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaA);
        when(categoriaRepository.findByIdAndUsuarioId(10L, 1L)).thenReturn(Optional.of(categoriaA));

        categoriaService.salvar(categoriaRequest);

        LancamentoRequest lancamentoRequest = LancamentoRequest.builder()
                .descricao("Pagamento")
                .valor(BigDecimal.valueOf(1000))
                .tipo(TipoLancamento.RECEITA)
                .dataLancamento(LocalDate.of(2026, 5, 1))
                .formaPagamento(FormaPagamento.PIX)
                .categoriaId(10L)
                .build();

        when(categoriaRepository.findByIdAndUsuarioId(10L, 1L)).thenReturn(Optional.of(categoriaA));
        when(lancamentoRepository.save(any(Lancamento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        lancamentoService.salvar(lancamentoRequest);

        verify(categoriaRepository).save(any(Categoria.class));
        verify(lancamentoRepository).save(any(Lancamento.class));

        when(usuarioAutenticadoService.obterUsuarioLogado()).thenReturn(usuarioB);
        when(categoriaRepository.findByUsuarioId(2L)).thenReturn(List.of());

        assertEquals(0, categoriaService.listarTodos().size());

        when(categoriaRepository.findByIdAndUsuarioId(10L, 2L)).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class, () -> categoriaService.buscarPorId(10L));

        when(lancamentoRepository.findByIdAndUsuarioId(99L, 2L)).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class, () -> lancamentoService.buscarPorId(99L));
    }

    @Test
    void dashboardDoUsuarioB_naoIncluiDadosDoUsuarioA() {
        when(usuarioAutenticadoService.obterUsuarioLogado()).thenReturn(usuarioB);
        when(lancamentoRepository.somarPorTipoEPeriodo(eq(2L), eq(TipoLancamento.RECEITA), any(), any()))
                .thenReturn(Optional.of(BigDecimal.ZERO));
        when(lancamentoRepository.somarPorTipoEPeriodo(eq(2L), eq(TipoLancamento.DESPESA), any(), any()))
                .thenReturn(Optional.of(BigDecimal.ZERO));

        ResumoResponse resumo = dashboardService.obterResumo(5, 2026);
        assertEquals(BigDecimal.ZERO, resumo.getTotalReceitas());
        assertEquals(BigDecimal.ZERO, resumo.getTotalDespesas());

        verify(lancamentoRepository, never()).somarPorTipoEPeriodo(eq(1L), any(), any(), any());
    }
}
