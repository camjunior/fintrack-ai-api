package com.carlos.fintrack.repository;

import com.carlos.fintrack.entity.Lancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>, JpaSpecificationExecutor<Lancamento> {
}
