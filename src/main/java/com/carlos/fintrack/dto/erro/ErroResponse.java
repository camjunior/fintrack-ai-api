package com.carlos.fintrack.dto.erro;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErroResponse {

    private String mensagem;
    private int status;
    private LocalDateTime timestamp;
    private String path;
}
