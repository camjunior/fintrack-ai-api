package com.carlos.fintrack.service;

import com.carlos.fintrack.dto.categoria.CategoriaRequest;
import com.carlos.fintrack.dto.categoria.CategoriaResponse;
import com.carlos.fintrack.entity.Categoria;
import com.carlos.fintrack.entity.Usuario;
import com.carlos.fintrack.exception.RecursoNaoEncontradoException;
import com.carlos.fintrack.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public CategoriaService(CategoriaRepository categoriaRepository, UsuarioAutenticadoService usuarioAutenticadoService) {
        this.categoriaRepository = categoriaRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    public List<CategoriaResponse> listarTodos() {
        Long usuarioId = usuarioAutenticadoService.obterUsuarioLogado().getId();

        return categoriaRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoriaResponse buscarPorId(Long id) {
        Categoria categoria = encontrarCategoriaDoUsuarioPorId(id);
        return toResponse(categoria);
    }

    public CategoriaResponse salvar(CategoriaRequest request) {
        Usuario usuarioLogado = usuarioAutenticadoService.obterUsuarioLogado();

        Categoria categoria = Categoria.builder()
                .nome(request.getNome())
                .tipo(request.getTipo())
                .usuario(usuarioLogado)
                .build();

        Categoria salva = categoriaRepository.save(categoria);
        return toResponse(salva);
    }

    public CategoriaResponse atualizar(Long id, CategoriaRequest request) {
        Categoria categoria = encontrarCategoriaDoUsuarioPorId(id);

        categoria.setNome(request.getNome());
        categoria.setTipo(request.getTipo());

        Categoria atualizada = categoriaRepository.save(categoria);
        return toResponse(atualizada);
    }

    public void excluir(Long id) {
        Categoria categoria = encontrarCategoriaDoUsuarioPorId(id);
        categoriaRepository.delete(categoria);
    }

    private Categoria encontrarCategoriaDoUsuarioPorId(Long id) {
        Long usuarioId = usuarioAutenticadoService.obterUsuarioLogado().getId();

        return categoriaRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada para o id: " + id));
    }

    private CategoriaResponse toResponse(Categoria categoria) {
        return CategoriaResponse.builder()
                .id(categoria.getId())
                .nome(categoria.getNome())
                .tipo(categoria.getTipo())
                .build();
    }
}
