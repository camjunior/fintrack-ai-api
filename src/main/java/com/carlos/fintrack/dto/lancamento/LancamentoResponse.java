package com.carlos.fintrack.dto.lancamento;

import com.carlos.fintrack.enums.FormaPagamento;
import com.carlos.fintrack.enums.TipoLancamento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LancamentoResponse {

    private Long id;
    private String descricao;
    private BigDecimal valor;
    private TipoLancamento tipo;
    private LocalDate dataLancamento;
    private FormaPagamento formaPagamento;
    private String observacao;
    private Long categoriaId;
    private String categoriaNome;
    private LocalDateTime criadoEm;
}
