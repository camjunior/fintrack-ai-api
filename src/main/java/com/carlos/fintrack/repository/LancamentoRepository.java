package com.carlos.fintrack.repository;

import com.carlos.fintrack.entity.Lancamento;
import com.carlos.fintrack.enums.TipoLancamento;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>, JpaSpecificationExecutor<Lancamento> {

    List<Lancamento> findByUsuarioId(Long usuarioId);

    Optional<Lancamento> findByIdAndUsuarioId(Long id, Long usuarioId);

    @Query("""
            select coalesce(sum(l.valor), 0)
            from Lancamento l
            where l.usuario.id = :usuarioId
              and l.tipo = :tipo
              and (:dataInicio is null or l.dataLancamento >= :dataInicio)
              and (:dataFim is null or l.dataLancamento <= :dataFim)
            """)
    Optional<BigDecimal> somarPorTipoEPeriodo(
            @Param("usuarioId") Long usuarioId,
            @Param("tipo") TipoLancamento tipo,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );

    @Query("""
            select c.nome as nomeCategoria, coalesce(sum(l.valor), 0) as total
            from Lancamento l
            join l.categoria c
            where l.usuario.id = :usuarioId
              and (:dataInicio is null or l.dataLancamento >= :dataInicio)
              and (:dataFim is null or l.dataLancamento <= :dataFim)
            group by c.nome
            order by total desc
            """)
    List<CategoriaResumoProjection> obterResumoPorCategoria(
            @Param("usuarioId") Long usuarioId,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );

    @Query("""
            select l from Lancamento l
            where l.usuario.id = :usuarioId
            order by l.dataLancamento desc, l.id desc
            """)
    List<Lancamento> buscarRecentes(@Param("usuarioId") Long usuarioId, Pageable pageable);

    interface CategoriaResumoProjection {
        String getNomeCategoria();

        BigDecimal getTotal();
    }
}
