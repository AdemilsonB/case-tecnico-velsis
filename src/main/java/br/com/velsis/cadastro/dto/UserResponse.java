package br.com.velsis.cadastro.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Representação do usuário devolvida pela API. Existe para que a entidade
 * não vaze da camada de serviço para o controller.
 */
public record UserResponse(
        Long id,
        String name,
        LocalDate birthDate,
        String document,
        String addressLine,
        String addressNumber,
        String city,
        String state,
        String zip,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
