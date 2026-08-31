package br.com.velsis.cadastro.service;

import br.com.velsis.cadastro.dto.PageResponse;
import br.com.velsis.cadastro.dto.UserRequest;
import br.com.velsis.cadastro.dto.UserResponse;
import br.com.velsis.cadastro.exception.DuplicateDocumentException;
import br.com.velsis.cadastro.exception.UserNotFoundException;
import br.com.velsis.cadastro.model.User;
import br.com.velsis.cadastro.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Regras de negócio do cadastro de usuários. Concentra a unicidade do
 * documento e a conversão entre DTO e entidade.
 */
@Service
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public UserService(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Lista os usuários paginados, do mais recente para o mais antigo.
     */
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> listar(int page, int size) {
        Page<User> pagina = repository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));

        List<UserResponse> conteudo = pagina.getContent().stream()
                .map(mapper::paraResposta)
                .toList();

        return new PageResponse<>(conteudo, pagina.getNumber(), pagina.getSize(),
                pagina.getTotalElements(), pagina.getTotalPages());
    }

    @Transactional(readOnly = true)
    public UserResponse buscarPorId(Long id) {
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return mapper.paraResposta(user);
    }

    /**
     * Cria um usuário novo, recusando documento já cadastrado.
     */
    @Transactional
    public UserResponse criar(UserRequest request) {
        if (repository.existsByDocument(request.document())) {
            throw new DuplicateDocumentException(request.document());
        }
        User user = new User();
        mapper.aplicar(request, user);
        return mapper.paraResposta(repository.save(user));
    }

    /**
     * Atualiza um usuário existente. O id e as datas de controle são preservados:
     * só os campos do formulário são sobrescritos.
     */
    @Transactional
    public UserResponse atualizar(Long id, UserRequest request) {
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        if (repository.existsByDocumentAndIdNot(request.document(), id)) {
            throw new DuplicateDocumentException(request.document());
        }
        mapper.aplicar(request, user);
        // Flush explícito para que o updated_at gerado pelo Hibernate já venha na resposta
        return mapper.paraResposta(repository.saveAndFlush(user));
    }
}
