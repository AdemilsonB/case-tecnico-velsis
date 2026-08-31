package br.com.velsis.cadastro.controller;

import br.com.velsis.cadastro.dto.ErrorResponse;
import br.com.velsis.cadastro.exception.DuplicateDocumentException;
import br.com.velsis.cadastro.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Traduz exceções em respostas com o mesmo formato de erro. Nenhuma stacktrace
 * chega ao navegador: ela fica no log do servidor.
 */
// Restrito ao pacote dos controllers para que requisições a arquivos estáticos
// inexistentes continuem devolvendo 404 do próprio Spring, e não o fallback 500
@RestControllerAdvice(basePackages = "br.com.velsis.cadastro.controller")
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> tratarValidacao(MethodArgumentNotValidException ex) {
        List<ErrorResponse.FieldError> campos = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> new ErrorResponse.FieldError(erro.getField(), erro.getDefaultMessage()))
                .toList();
        return montar(HttpStatus.BAD_REQUEST, "Dados inválidos", campos);
    }

    /** JSON malformado ou data em formato inválido chegam como erro de leitura do corpo. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> tratarCorpoInvalido(HttpMessageNotReadableException ex) {
        log.warn("Corpo da requisição inválido: {}", ex.getMessage());
        return montar(HttpStatus.BAD_REQUEST, "Dados inválidos", List.of());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> tratarNaoEncontrado(UserNotFoundException ex) {
        log.warn(ex.getMessage());
        return montar(HttpStatus.NOT_FOUND, "Usuário não encontrado.", List.of());
    }

    @ExceptionHandler(DuplicateDocumentException.class)
    public ResponseEntity<ErrorResponse> tratarDocumentoDuplicado(DuplicateDocumentException ex) {
        log.warn(ex.getMessage());
        List<ErrorResponse.FieldError> campos = List.of(new ErrorResponse.FieldError(
                "document", "Já existe usuário cadastrado com este documento."));
        return montar(HttpStatus.CONFLICT, "Já existe usuário cadastrado com este documento.", campos);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> tratarErroInesperado(Exception ex) {
        log.error("Erro inesperado", ex);
        return montar(HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro inesperado ao processar a requisição.", List.of());
    }

    private ResponseEntity<ErrorResponse> montar(HttpStatus status, String mensagem,
                                                 List<ErrorResponse.FieldError> campos) {
        ErrorResponse corpo = new ErrorResponse(LocalDateTime.now(), status.value(), mensagem, campos);
        return ResponseEntity.status(status).body(corpo);
    }
}
