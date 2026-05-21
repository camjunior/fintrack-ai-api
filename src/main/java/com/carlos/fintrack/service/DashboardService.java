package com.carlos.fintrack.service;

import com.carlos.fintrack.dto.dashboard.CategoriaResumoResponse;
import com.carlos.fintrack.dto.dashboard.ResumoResponse;
import com.carlos.fintrack.dto.lancamento.LancamentoResponse;
import com.carlos.fintrack.entity.Lancamento;
import com.carlos.fintrack.enums.TipoLancamento;
import com.carlos.fintrack.repository.LancamentoRepository;
import com.carlos.fintrack.specification.LancamentoSpecification;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DashboardService {

    private final LancamentoRepository lancamentoRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public DashboardService(LancamentoRepository lancamentoRepository, UsuarioAutenticadoService usuarioAutenticadoService) {
        this.lancamentoRepository = lancamentoRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    public ResumoResponse obterResumo(Integer mes, Integer ano) {
        Long usuarioId = usuarioAutenticadoService.obterUsuarioLogado().getId();
        LancamentoSpecification.Periodo periodo = LancamentoSpecification.construirPeriodo(mes, ano);

        BigDecimal totalReceitas = lancamentoRepository
                .somarPorTipoEPeriodo(usuarioId, TipoLancamento.RECEITA, periodo.dataInicio(), periodo.dataFim())
                .orElse(BigDecimal.ZERO);

        BigDecimal totalDespesas = lancamentoRepository
                .somarPorTipoEPeriodo(usuarioId, TipoLancamento.DESPESA, periodo.dataInicio(), periodo.dataFim())
                .orElse(BigDecimal.ZERO);

        BigDecimal saldo = totalReceitas.subtract(totalDespesas);

        return ResumoResponse.builder()
                .totalReceitas(totalReceitas)
                .totalDespesas(totalDespesas)
                .saldo(saldo)
                .build();
    }

    public List<CategoriaResumoResponse> obterGastosPorCategoria(Integer mes, Integer ano) {
        Long usuarioId = usuarioAutenticadoService.obterUsuarioLogado().getId();
        LancamentoSpecification.Periodo periodo = LancamentoSpecification.construirPeriodo(mes, ano);

        return lancamentoRepository.obterResumoPorCategoria(usuarioId, periodo.dataInicio(), periodo.dataFim())
                .stream()
                .map(item -> CategoriaResumoResponse.builder()
                        .nomeCategoria(item.getNomeCategoria())
                        .total(item.getTotal())
                        .build())
                .toList();
    }

    public List<LancamentoResponse> obterUltimosLancamentos() {
        Long usuarioId = usuarioAutenticadoService.obterUsuarioLogado().getId();
        List<Lancamento> recentes = lancamentoRepository.buscarRecentes(usuarioId, PageRequest.of(0, 10));

        return recentes.stream()
                .map(this::toResponse)
                .toList();
    }

    private LancamentoResponse toResponse(Lancamento lancamento) {
        return LancamentoResponse.builder()
                .id(lancamento.getId())
                .descricao(lancamento.getDescricao())
                .valor(lancamento.getValor())
                .tipo(lancamento.getTipo())
                .dataLancamento(lancamento.getDataLancamento())
                .formaPagamento(lancamento.getFormaPagamento())
                .observacao(lancamento.getObservacao())
                .categoriaId(lancamento.getCategoria().getId())
                .categoriaNome(lancamento.getCategoria().getNome())
                .criadoEm(lancamento.getCriadoEm())
                .build();
    }

}
