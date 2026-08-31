package br.com.velsis.cadastro.service;

import br.com.velsis.cadastro.dto.UserRequest;
import br.com.velsis.cadastro.dto.UserResponse;
import br.com.velsis.cadastro.model.User;
import org.springframework.stereotype.Component;

/**
 * Conversão entre DTO e entidade. O mapeamento é manual e fica na camada de
 * serviço, que é onde o case coloca as regras de transformação.
 */
@Component
public class UserMapper {

    /** Aplica os dados do formulário sobre a entidade, usada tanto na criação quanto na edição. */
    public void aplicar(UserRequest request, User destino) {
        destino.setName(request.name().trim());
        destino.setBirthDate(request.birthDate());
        destino.setDocument(request.document());
        destino.setAddressLine(request.addressLine().trim());
        destino.setAddressNumber(request.addressNumber().trim());
        destino.setCity(request.city().trim());
        destino.setState(request.state().toUpperCase());
        destino.setZip(request.zip());
    }

    public UserResponse paraResposta(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getBirthDate(),
                user.getDocument(),
                user.getAddressLine(),
                user.getAddressNumber(),
                user.getCity(),
                user.getState(),
                user.getZip(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
