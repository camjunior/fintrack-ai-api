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

    public DashboardService(LancamentoRepository lancamentoRepository) {
        this.lancamentoRepository = lancamentoRepository;
    }

    public ResumoResponse obterResumo(Integer mes, Integer ano) {
        LancamentoSpecification.Periodo periodo = LancamentoSpecification.construirPeriodo(mes, ano);

        BigDecimal totalReceitas = lancamentoRepository
                .somarPorTipoEPeriodo(TipoLancamento.RECEITA, periodo.dataInicio(), periodo.dataFim())
                .orElse(BigDecimal.ZERO);

        BigDecimal totalDespesas = lancamentoRepository
                .somarPorTipoEPeriodo(TipoLancamento.DESPESA, periodo.dataInicio(), periodo.dataFim())
                .orElse(BigDecimal.ZERO);

        BigDecimal saldo = totalReceitas.subtract(totalDespesas);

        return ResumoResponse.builder()
                .totalReceitas(totalReceitas)
                .totalDespesas(totalDespesas)
                .saldo(saldo)
                .build();
    }

    public List<CategoriaResumoResponse> obterGastosPorCategoria(Integer mes, Integer ano) {
        LancamentoSpecification.Periodo periodo = LancamentoSpecification.construirPeriodo(mes, ano);

        return lancamentoRepository.obterResumoPorCategoria(periodo.dataInicio(), periodo.dataFim())
                .stream()
                .map(item -> CategoriaResumoResponse.builder()
                        .nomeCategoria(item.getNomeCategoria())
                        .total(item.getTotal())
                        .build())
                .toList();
    }

    public List<LancamentoResponse> obterUltimosLancamentos() {
        List<Lancamento> recentes = lancamentoRepository.buscarRecentes(PageRequest.of(0, 10));

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
