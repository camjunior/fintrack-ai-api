package com.carlos.fintrack.service;

import com.carlos.fintrack.dto.lancamento.LancamentoRequest;
import com.carlos.fintrack.dto.lancamento.LancamentoResponse;
import com.carlos.fintrack.entity.Categoria;
import com.carlos.fintrack.entity.Lancamento;
import com.carlos.fintrack.exception.RecursoNaoEncontradoException;
import com.carlos.fintrack.repository.CategoriaRepository;
import com.carlos.fintrack.repository.LancamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LancamentoService {

    private final LancamentoRepository lancamentoRepository;
    private final CategoriaRepository categoriaRepository;

    public LancamentoService(LancamentoRepository lancamentoRepository, CategoriaRepository categoriaRepository) {
        this.lancamentoRepository = lancamentoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<LancamentoResponse> listarTodos() {
        return lancamentoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public LancamentoResponse buscarPorId(Long id) {
        Lancamento lancamento = encontrarLancamentoPorId(id);
        return toResponse(lancamento);
    }

    public LancamentoResponse salvar(LancamentoRequest request) {
        Categoria categoria = buscarCategoriaPorId(request.getCategoriaId());

        Lancamento lancamento = Lancamento.builder()
                .descricao(request.getDescricao())
                .valor(request.getValor())
                .tipo(request.getTipo())
                .dataLancamento(request.getDataLancamento())
                .formaPagamento(request.getFormaPagamento())
                .observacao(request.getObservacao())
                .categoria(categoria)
                .build();

        Lancamento salvo = lancamentoRepository.save(lancamento);
        return toResponse(salvo);
    }

    public LancamentoResponse atualizar(Long id, LancamentoRequest request) {
        Lancamento lancamento = encontrarLancamentoPorId(id);
        Categoria categoria = buscarCategoriaPorId(request.getCategoriaId());

        lancamento.setDescricao(request.getDescricao());
        lancamento.setValor(request.getValor());
        lancamento.setTipo(request.getTipo());
        lancamento.setDataLancamento(request.getDataLancamento());
        lancamento.setFormaPagamento(request.getFormaPagamento());
        lancamento.setObservacao(request.getObservacao());
        lancamento.setCategoria(categoria);

        Lancamento atualizado = lancamentoRepository.save(lancamento);
        return toResponse(atualizado);
    }

    public void excluir(Long id) {
        Lancamento lancamento = encontrarLancamentoPorId(id);
        lancamentoRepository.delete(lancamento);
    }

    private Lancamento encontrarLancamentoPorId(Long id) {
        return lancamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lançamento não encontrado para o id: " + id));
    }

    private Categoria buscarCategoriaPorId(Long categoriaId) {
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada para o id: " + categoriaId));
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
