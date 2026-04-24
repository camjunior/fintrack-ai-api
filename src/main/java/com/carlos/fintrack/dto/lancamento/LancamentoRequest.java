package com.carlos.fintrack.dto.lancamento;

import com.carlos.fintrack.enums.FormaPagamento;
import com.carlos.fintrack.enums.TipoLancamento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LancamentoRequest {

    @NotNull(message = "Descrição é obrigatória")
    @Size(min = 1, max = 150, message = "Descrição deve ter entre 1 e 150 caracteres")
    private String descricao;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;

    @NotNull(message = "Tipo do lançamento é obrigatório")
    private TipoLancamento tipo;

    @NotNull(message = "Data do lançamento é obrigatória")
    private LocalDate dataLancamento;

    @NotNull(message = "Forma de pagamento é obrigatória")
    private FormaPagamento formaPagamento;

    @Size(max = 500, message = "Observação deve ter no máximo 500 caracteres")
    private String observacao;

    @NotNull(message = "ID da categoria é obrigatório")
    private Long categoriaId;
}
