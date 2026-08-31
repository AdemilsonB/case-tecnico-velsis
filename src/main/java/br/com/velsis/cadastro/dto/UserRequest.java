package br.com.velsis.cadastro.dto;

import br.com.velsis.cadastro.validation.Cpf;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

/**
 * Dados que chegam do formulário de cadastro e de edição. As regras aqui são
 * as mesmas do validacao.js, com as mesmas mensagens, porque o case pede
 * validação nas duas camadas.
 */
public record UserRequest(

        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 120, message = "Nome deve ter entre 3 e 120 caracteres")
        String name,

        @NotNull(message = "Data de nascimento é obrigatória")
        @Past(message = "Data de nascimento não pode ser futura")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate birthDate,

        @NotBlank(message = "Documento é obrigatório")
        @Cpf
        String document,

        @NotBlank(message = "Rua é obrigatória")
        @Size(max = 150, message = "Rua deve ter no máximo 150 caracteres")
        String addressLine,

        @NotBlank(message = "Número é obrigatório")
        @Size(max = 10, message = "Número deve ter no máximo 10 caracteres")
        String addressNumber,

        @NotBlank(message = "Cidade é obrigatória")
        @Size(max = 80, message = "Cidade deve ter no máximo 80 caracteres")
        String city,

        @NotBlank(message = "Estado é obrigatório")
        @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres")
        String state,

        @NotBlank(message = "CEP é obrigatório")
        @Pattern(regexp = "\\d{8}", message = "CEP deve ter 8 dígitos")
        String zip
) {
}
