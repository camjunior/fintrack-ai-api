package com.carlos.fintrack.specification;

import com.carlos.fintrack.entity.Lancamento;
import com.carlos.fintrack.enums.TipoLancamento;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class LancamentoSpecification {

    private LancamentoSpecification() {
    }

    public static Specification<Lancamento> comFiltros(Integer mes, Integer ano, TipoLancamento tipo, Long categoriaId) {
        return porPeriodo(mes, ano)
                .and(porTipo(tipo))
                .and(porCategoriaId(categoriaId));
    }

    public static Specification<Lancamento> porPeriodo(Integer mes, Integer ano) {
        return (root, query, criteriaBuilder) -> {
            if (mes == null && ano == null) {
                return criteriaBuilder.conjunction();
            }

            if (mes != null && ano != null) {
                LocalDate inicio = LocalDate.of(ano, mes, 1);
                LocalDate fim = inicio.withDayOfMonth(inicio.lengthOfMonth());
                return criteriaBuilder.between(root.get("dataLancamento"), inicio, fim);
            }

            if (ano != null) {
                LocalDate inicioAno = LocalDate.of(ano, 1, 1);
                LocalDate fimAno = LocalDate.of(ano, 12, 31);
                return criteriaBuilder.between(root.get("dataLancamento"), inicioAno, fimAno);
            }

            return criteriaBuilder.equal(
                    criteriaBuilder.function("MONTH", Integer.class, root.get("dataLancamento")),
                    mes
            );
        };
    }

    public static Specification<Lancamento> porTipo(TipoLancamento tipo) {
        return (root, query, criteriaBuilder) -> {
            if (tipo == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("tipo"), tipo);
        };
    }

    public static Specification<Lancamento> porCategoriaId(Long categoriaId) {
        return (root, query, criteriaBuilder) -> {
            if (categoriaId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("categoria").get("id"), categoriaId);
        };
    }
}
