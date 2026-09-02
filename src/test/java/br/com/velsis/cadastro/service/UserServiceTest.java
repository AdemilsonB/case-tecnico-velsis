package br.com.velsis.cadastro.service;

import br.com.velsis.cadastro.dto.UserRequest;
import br.com.velsis.cadastro.dto.UserResponse;
import br.com.velsis.cadastro.exception.DuplicateDocumentException;
import br.com.velsis.cadastro.exception.UserNotFoundException;
import br.com.velsis.cadastro.model.User;
import br.com.velsis.cadastro.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Teste unitário do serviço, com o repositório mockado. Não sobe contexto do
 * Spring: o alvo aqui é a regra de negócio, não a infraestrutura.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    private UserService service;

    @BeforeEach
    void prepararServico() {
        // O mapper não tem dependência externa, então entra o objeto real
        service = new UserService(repository, new UserMapper());
    }

    private UserRequest requisicaoValida(String documento) {
        return new UserRequest("Maria Silva", LocalDate.of(1990, 5, 20), documento,
                "Rua das Flores", "150", "Curitiba", "PR", "80010000");
    }

    @Test
    void deveSalvarUsuarioValidoComOsDadosDoFormulario() {
        UserRequest request = requisicaoValida("52998224725");
        when(repository.existsByDocument("52998224725")).thenReturn(false);
        when(repository.save(any(User.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        UserResponse resposta = service.criar(request);

        ArgumentCaptor<User> capturado = ArgumentCaptor.forClass(User.class);
        verify(repository).save(capturado.capture());
        assertThat(capturado.getValue().getName()).isEqualTo("Maria Silva");
        assertThat(capturado.getValue().getDocument()).isEqualTo("52998224725");
        assertThat(capturado.getValue().getCity()).isEqualTo("Curitiba");
        assertThat(resposta.state()).isEqualTo("PR");
    }

    @Test
    void deveRecusarCadastroComDocumentoJaExistente() {
        when(repository.existsByDocument("52998224725")).thenReturn(true);

        assertThatThrownBy(() -> service.criar(requisicaoValida("52998224725")))
                .isInstanceOf(DuplicateDocumentException.class);

        verify(repository, never()).save(any(User.class));
    }

    @Test
    void deveRecusarEdicaoDeIdInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.atualizar(99L, requisicaoValida("52998224725")))
                .isInstanceOf(UserNotFoundException.class);

        verify(repository, never()).saveAndFlush(any(User.class));
    }

    @Test
    void deveAtualizarCamposPreservandoOId() {
        User existente = new User();
        existente.setId(7L);
        existente.setName("Nome Antigo");
        existente.setDocument("52998224725");
        when(repository.findById(7L)).thenReturn(Optional.of(existente));
        when(repository.existsByDocumentAndIdNot("11144477735", 7L)).thenReturn(false);
        when(repository.saveAndFlush(any(User.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        UserResponse resposta = service.atualizar(7L, requisicaoValida("11144477735"));

        assertThat(resposta.id()).isEqualTo(7L);
        assertThat(resposta.name()).isEqualTo("Maria Silva");
        assertThat(resposta.document()).isEqualTo("11144477735");
    }

    @Test
    void deveListarTudoQuandoOTermoVemVazio() {
        Page<User> pagina = new PageImpl<>(List.of(new User()));
        when(repository.findAll(any(Pageable.class))).thenReturn(pagina);

        service.listar("   ", 0, 20);

        verify(repository, never()).buscarPorTermo(any(), any(), any(Pageable.class));
    }

    @Test
    void deveFiltrarPeloTermoSemEspacosAoRedor() {
        Page<User> pagina = new PageImpl<>(List.of(new User()));
        when(repository.buscarPorTermo(eq("maria"), any(), any(Pageable.class))).thenReturn(pagina);

        service.listar("  maria  ", 0, 20);

        verify(repository, never()).findAll(any(Pageable.class));
    }

    /**
     * A tela mostra o documento com máscara, mas o banco guarda só os dígitos.
     * Copiar o CPF da listagem e colar na busca precisa encontrar o registro.
     */
    @Test
    void deveBuscarPeloDocumentoAindaQueOTermoVenhaComMascara() {
        Page<User> pagina = new PageImpl<>(List.of(new User()));
        when(repository.buscarPorTermo(any(), any(), any(Pageable.class))).thenReturn(pagina);

        service.listar("390.120.000-25", 0, 20);

        ArgumentCaptor<String> documento = ArgumentCaptor.forClass(String.class);
        verify(repository).buscarPorTermo(eq("390.120.000-25"), documento.capture(), any(Pageable.class));
        assertThat(documento.getValue()).isEqualTo("39012000025");
    }

    /**
     * Sem dígito nenhum no termo, a comparação do documento não pode virar
     * "like '%%'", que casaria com a base inteira.
     */
    @Test
    void naoDeveCompararDocumentoComTermoVazioQuandoABuscaEPorNome() {
        Page<User> pagina = new PageImpl<>(List.of(new User()));
        when(repository.buscarPorTermo(any(), any(), any(Pageable.class))).thenReturn(pagina);

        service.listar("maria", 0, 20);

        ArgumentCaptor<String> documento = ArgumentCaptor.forClass(String.class);
        verify(repository).buscarPorTermo(eq("maria"), documento.capture(), any(Pageable.class));
        assertThat(documento.getValue()).isNotEmpty();
    }
}
