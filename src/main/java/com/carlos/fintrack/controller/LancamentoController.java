package com.carlos.fintrack.controller;

import com.carlos.fintrack.dto.lancamento.LancamentoRequest;
import com.carlos.fintrack.dto.lancamento.LancamentoResponse;
import com.carlos.fintrack.enums.TipoLancamento;
import com.carlos.fintrack.service.LancamentoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lancamentos")
@Validated
public class LancamentoController {

    private final LancamentoService lancamentoService;

    public LancamentoController(LancamentoService lancamentoService) {
        this.lancamentoService = lancamentoService;
    }

    @GetMapping
    public List<LancamentoResponse> listarTodos(
            @RequestParam(required = false) @Min(1) @Max(12) Integer mes,
            @RequestParam(required = false) @Min(1900) Integer ano,
            @RequestParam(required = false) TipoLancamento tipo,
            @RequestParam(required = false) Long categoriaId
    ) {
        return lancamentoService.listarTodos(mes, ano, tipo, categoriaId);
    }

    @GetMapping("/{id}")
    public LancamentoResponse buscarPorId(@PathVariable Long id) {
        return lancamentoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LancamentoResponse salvar(@Valid @RequestBody LancamentoRequest request) {
        return lancamentoService.salvar(request);
    }

    @PutMapping("/{id}")
    public LancamentoResponse atualizar(@PathVariable Long id, @Valid @RequestBody LancamentoRequest request) {
        return lancamentoService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        lancamentoService.excluir(id);
    }
}
