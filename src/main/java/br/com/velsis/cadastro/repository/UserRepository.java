package br.com.velsis.cadastro.repository;

import br.com.velsis.cadastro.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Acesso à tabela users. O CRUD e a paginação já vêm do JpaRepository;
 * aqui ficam apenas as consultas da regra de documento único e do filtro
 * da listagem.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByDocument(String document);

    // Usado na edição: o próprio registro não pode acusar duplicidade contra ele mesmo
    boolean existsByDocumentAndIdNot(String document, Long id);

    // São dois parâmetros porque as colunas guardam formatos diferentes: o nome
    // é comparado com o texto digitado, e o documento com os dígitos extraídos
    // dele, já que a coluna document não tem máscara. Quem monta os dois é o
    // serviço; aqui a consulta só recebe os valores prontos.
    // LIKE com curinga à esquerda não aproveita índice. É aceitável no volume
    // deste cadastro; em base grande a saída seria busca full-text
    @Query("select u from User u "
            + "where lower(u.name) like lower(concat('%', :termo, '%')) "
            + "   or u.document like concat('%', :documento, '%')")
    Page<User> buscarPorTermo(@Param("termo") String termo,
                              @Param("documento") String documento,
                              Pageable pageable);
}
