package br.com.velsis.cadastro.controller;

import br.com.velsis.cadastro.dto.PageResponse;
import br.com.velsis.cadastro.dto.UserRequest;
import br.com.velsis.cadastro.dto.UserResponse;
import br.com.velsis.cadastro.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * API REST consumida pelas telas em static/. O controller só conhece DTO;
 * quem fala com o repositório é o serviço.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponse<UserResponse> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.listar(page, size);
    }

    @GetMapping("/{id}")
    public UserResponse buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<UserResponse> criar(@Valid @RequestBody UserRequest request,
                                              UriComponentsBuilder uriBuilder) {
        UserResponse criado = service.criar(request);
        URI location = uriBuilder.path("/api/users/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public UserResponse atualizar(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return service.atualizar(id, request);
    }
}
