package br.com.velsis.cadastro.exception;

/**
 * Lançada quando o documento informado já pertence a outro usuário.
 */
public class DuplicateDocumentException extends RuntimeException {

    public DuplicateDocumentException(String documento) {
        super("Documento já cadastrado: " + documento);
    }
}
