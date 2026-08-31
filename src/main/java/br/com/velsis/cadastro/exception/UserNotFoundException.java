package br.com.velsis.cadastro.exception;

/**
 * Lançada quando o id informado não corresponde a nenhum usuário.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("Usuário não encontrado. Id: " + id);
    }
}
