package br.com.velsis.cadastro.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Cálculo do dígito verificador do CPF. A mesma lógica está em validacao.js:
 * a duplicação é proposital, porque o case exige validação no frontend e no
 * backend, e o backend não pode confiar no que chega da rede.
 */
public class CpfValidator implements ConstraintValidator<Cpf, String> {

    @Override
    public boolean isValid(String valor, ConstraintValidatorContext context) {
        // Campo vazio é problema do @NotBlank, não deste validador
        if (valor == null || valor.isBlank()) {
            return true;
        }
        if (!valor.matches("\\d{11}")) {
            return false;
        }
        // CPFs com todos os dígitos iguais passam no cálculo, mas não existem
        if (valor.chars().distinct().count() == 1) {
            return false;
        }
        return digitoVerificador(valor, 9) == charParaInt(valor, 9)
                && digitoVerificador(valor, 10) == charParaInt(valor, 10);
    }

    /**
     * Calcula um dígito verificador somando os primeiros dígitos com pesos
     * decrescentes, conforme a regra da Receita Federal.
     */
    private int digitoVerificador(String cpf, int quantidadeDigitos) {
        int soma = 0;
        int peso = quantidadeDigitos + 1;
        for (int i = 0; i < quantidadeDigitos; i++) {
            soma += charParaInt(cpf, i) * peso;
            peso--;
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    private int charParaInt(String cpf, int posicao) {
        return cpf.charAt(posicao) - '0';
    }
}
