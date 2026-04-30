package com.carlos.fintrack.controller;

import com.carlos.fintrack.dto.dashboard.CategoriaResumoResponse;
import com.carlos.fintrack.dto.dashboard.ResumoResponse;
import com.carlos.fintrack.dto.lancamento.LancamentoResponse;
import com.carlos.fintrack.service.DashboardService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@Validated
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/resumo")
    public ResumoResponse obterResumo(
            @RequestParam(required = false) @Min(1) @Max(12) Integer mes,
            @RequestParam(required = false) @Min(1900) Integer ano
    ) {
        return dashboardService.obterResumo(mes, ano);
    }

    @GetMapping("/categorias")
    public List<CategoriaResumoResponse> obterGastosPorCategoria(
            @RequestParam(required = false) @Min(1) @Max(12) Integer mes,
            @RequestParam(required = false) @Min(1900) Integer ano
    ) {
        return dashboardService.obterGastosPorCategoria(mes, ano);
    }

    @GetMapping("/recentes")
    public List<LancamentoResponse> obterUltimosLancamentos() {
        return dashboardService.obterUltimosLancamentos();
    }
}
