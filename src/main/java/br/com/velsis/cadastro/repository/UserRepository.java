package br.com.velsis.cadastro.repository;

import br.com.velsis.cadastro.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acesso à tabela users. O CRUD e a paginação já vêm do JpaRepository;
 * aqui ficam apenas as consultas usadas pela regra de documento único.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByDocument(String document);

    // Usado na edição: o próprio registro não pode acusar duplicidade contra ele mesmo
    boolean existsByDocumentAndIdNot(String document, Long id);
}
