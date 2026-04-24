package com.carlos.fintrack.dto.categoria;

import com.carlos.fintrack.enums.TipoCategoria;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaResponse {

    private Long id;
    private String nome;
    private TipoCategoria tipo;
}
