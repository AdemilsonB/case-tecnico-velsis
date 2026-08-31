package br.com.velsis.cadastro.dto;

import java.util.List;

/**
 * Envelope de paginação. Devolver este record em vez do Page do Spring evita
 * expor a estrutura interna do framework no contrato da API.
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
