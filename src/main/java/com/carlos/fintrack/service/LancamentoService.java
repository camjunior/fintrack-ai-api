package com.carlos.fintrack.service;

import com.carlos.fintrack.dto.lancamento.LancamentoRequest;
import com.carlos.fintrack.dto.lancamento.LancamentoResponse;
import com.carlos.fintrack.entity.Categoria;
import com.carlos.fintrack.entity.Lancamento;
import com.carlos.fintrack.entity.Usuario;
import com.carlos.fintrack.enums.TipoLancamento;
import com.carlos.fintrack.exception.RecursoNaoEncontradoException;
import com.carlos.fintrack.repository.CategoriaRepository;
import com.carlos.fintrack.repository.LancamentoRepository;
import com.carlos.fintrack.specification.LancamentoSpecification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LancamentoService {

    private final LancamentoRepository lancamentoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public LancamentoService(
            LancamentoRepository lancamentoRepository,
            CategoriaRepository categoriaRepository,
            UsuarioAutenticadoService usuarioAutenticadoService
    ) {
        this.lancamentoRepository = lancamentoRepository;
        this.categoriaRepository = categoriaRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    public List<LancamentoResponse> listarTodos(Integer mes, Integer ano, TipoLancamento tipo, Long categoriaId) {
        Long usuarioId = usuarioAutenticadoService.obterUsuarioLogado().getId();

        List<Lancamento> lancamentos = lancamentoRepository.findAll(
                LancamentoSpecification.comFiltros(usuarioId, mes, ano, tipo, categoriaId)
        );

        return lancamentos.stream()
                .map(this::toResponse)
                .toList();
    }

    public LancamentoResponse buscarPorId(Long id) {
        Lancamento lancamento = encontrarLancamentoDoUsuarioPorId(id);
        return toResponse(lancamento);
    }

    public LancamentoResponse salvar(LancamentoRequest request) {
        Usuario usuarioLogado = usuarioAutenticadoService.obterUsuarioLogado();
        Categoria categoria = buscarCategoriaDoUsuarioPorId(request.getCategoriaId(), usuarioLogado.getId());

        Lancamento lancamento = Lancamento.builder()
                .descricao(request.getDescricao())
                .valor(request.getValor())
                .tipo(request.getTipo())
                .dataLancamento(request.getDataLancamento())
                .formaPagamento(request.getFormaPagamento())
                .observacao(request.getObservacao())
                .categoria(categoria)
                .usuario(usuarioLogado)
                .build();

        Lancamento salvo = lancamentoRepository.save(lancamento);
        return toResponse(salvo);
    }

    public LancamentoResponse atualizar(Long id, LancamentoRequest request) {
        Lancamento lancamento = encontrarLancamentoDoUsuarioPorId(id);
        Long usuarioId = usuarioAutenticadoService.obterUsuarioLogado().getId();
        Categoria categoria = buscarCategoriaDoUsuarioPorId(request.getCategoriaId(), usuarioId);

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
        Lancamento lancamento = encontrarLancamentoDoUsuarioPorId(id);
        lancamentoRepository.delete(lancamento);
    }

    private Lancamento encontrarLancamentoDoUsuarioPorId(Long id) {
        Long usuarioId = usuarioAutenticadoService.obterUsuarioLogado().getId();

        return lancamentoRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lançamento não encontrado para o id: " + id));
    }

    private Categoria buscarCategoriaDoUsuarioPorId(Long categoriaId, Long usuarioId) {
        return categoriaRepository.findByIdAndUsuarioId(categoriaId, usuarioId)
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
