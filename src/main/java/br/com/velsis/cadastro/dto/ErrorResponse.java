package br.com.velsis.cadastro.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Formato único de erro da API. Manter um só formato permite que o frontend
 * trate 400, 404, 409 e 500 com o mesmo código.
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        List<FieldError> fields
) {

    /** Erro associado a um campo específico do formulário. */
    public record FieldError(String field, String message) {
    }
}
