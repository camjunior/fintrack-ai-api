package com.carlos.fintrack.service;

import com.carlos.fintrack.dto.categoria.CategoriaRequest;
import com.carlos.fintrack.dto.categoria.CategoriaResponse;
import com.carlos.fintrack.entity.Categoria;
import com.carlos.fintrack.exception.RecursoNaoEncontradoException;
import com.carlos.fintrack.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<CategoriaResponse> listarTodos() {
        return categoriaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoriaResponse buscarPorId(Long id) {
        Categoria categoria = encontrarCategoriaPorId(id);
        return toResponse(categoria);
    }

    public CategoriaResponse salvar(CategoriaRequest request) {
        Categoria categoria = Categoria.builder()
                .nome(request.getNome())
                .tipo(request.getTipo())
                .build();

        Categoria salva = categoriaRepository.save(categoria);
        return toResponse(salva);
    }

    public CategoriaResponse atualizar(Long id, CategoriaRequest request) {
        Categoria categoria = encontrarCategoriaPorId(id);

        categoria.setNome(request.getNome());
        categoria.setTipo(request.getTipo());

        Categoria atualizada = categoriaRepository.save(categoria);
        return toResponse(atualizada);
    }

    public void excluir(Long id) {
        Categoria categoria = encontrarCategoriaPorId(id);
        categoriaRepository.delete(categoria);
    }

    private Categoria encontrarCategoriaPorId(Long id) {
        return categoriaRepository.findById(id)
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
