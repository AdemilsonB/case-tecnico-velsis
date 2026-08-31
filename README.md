# Cadastro de Usuários

Aplicação web para cadastrar, listar e editar usuários (nome, data de nascimento,
documento e endereço). O backend é uma API REST em Spring Boot com persistência
em PostgreSQL via JPA/Hibernate; o frontend são três páginas HTML/CSS/JS que
consomem essa API.

## Stack

| Item | Versão |
|---|---|
| Java | 17 (LTS) |
| Spring Boot | 3.5.5 |
| PostgreSQL | 16 ou superior |
| Maven | wrapper incluído (`mvnw`) |

IDE: o projeto é um Maven padrão e abre direto no IntelliJ IDEA
(`File > Open` na pasta do projeto) ou no NetBeans (`File > Open Project`).
Não usa Lombok nem plugin algum, então compila na IDE sem configuração extra.

## 1. Criação do banco

Com o PostgreSQL em execução, crie o banco:

```sql
CREATE DATABASE cadastro_usuarios;
```

Pelo terminal:

```bash
psql -U postgres -c "CREATE DATABASE cadastro_usuarios;"
```

A tabela `users` **não** precisa ser criada à mão: o arquivo
`src/main/resources/schema.sql` é executado na subida da aplicação.

## 2. Configuração da conexão

Ajuste, se necessário, `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/cadastro_usuarios
spring.datasource.username=postgres
spring.datasource.password=postgres
```

Usuário, senha e porta devem bater com a instalação local do PostgreSQL.
Como alternativa a editar o arquivo, os mesmos valores podem vir de variáveis
de ambiente: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER` e `DB_PASSWORD`.

## 3. Como rodar

```bash
./mvnw spring-boot:run
```

No Windows (cmd/PowerShell):

```
mvnw.cmd spring-boot:run
```

## 4. Acesso

http://localhost:8080

Telas disponíveis:

- `/` listagem de usuários (página principal, com paginação de 20 registros)
- `/novo.html` cadastro
- `/editar.html?id=N` edição

## 5. Testes

```bash
./mvnw test
```

Cobrem as regras do `UserService`: cadastro válido, documento duplicado,
edição de id inexistente e edição preservando o id.

## 6. Endpoints

| Método | Rota | Sucesso | Erros |
|---|---|---|---|
| GET | `/api/users?page=0&size=20` | 200 com a página | |
| GET | `/api/users/{id}` | 200 com o usuário | 404 |
| POST | `/api/users` | 201 com header `Location` | 400, 409 |
| PUT | `/api/users/{id}` | 200 com o usuário | 400, 404, 409 |

Todo erro devolve o mesmo formato, o que permite ao frontend tratar 400, 404,
409 e 500 com um único caminho de código:

```json
{
  "timestamp": "2026-08-31T14:22:10",
  "status": 400,
  "error": "Dados inválidos",
  "fields": [
    { "field": "document", "message": "CPF inválido" }
  ]
}
```

## 7. Decisões técnicas

**Java 17 em vez de 21.** O enunciado pede Java 8 ou superior. 17 é o LTS
efetivamente presente nos parques corporativos hoje, o que faz mais sentido
para um sistema que vai conviver com aplicações existentes.

**Schema por script, com `ddl-auto=validate`.** O `schema.sql` é a fonte da
estrutura da tabela e o Hibernate apenas confere a entidade contra ela na
subida. Assim o banco nunca é alterado sozinho por causa de uma mudança de
código, e uma divergência aparece na inicialização, não em produção.

**Endereço achatado na tabela `users`.** As colunas são exatamente as do
enunciado (`address_line`, `address_number`, `city`, `state`, `zip`). Uma
tabela `address` separada só faria sentido se um usuário pudesse ter mais de
um endereço, o que não foi pedido.

**Validação duplicada de propósito.** A mesma regra existe em
`validacao.js` e em `UserRequest`, com as mesmas mensagens. O frontend evita
uma ida ao servidor; o backend não pode confiar no que chega da rede, porque a
API também responde fora do navegador. Vale inclusive para o dígito
verificador do CPF, implementado nos dois lados.

**Documento gravado só com dígitos.** A máscara é apresentação e fica no
frontend. No banco fica `52998224725`, o que mantém a chave única consistente
independente de como o valor foi digitado.

**Sem Lombok.** Getters e setters escritos à mão na entidade e `record` nos
DTOs. O projeto compila em qualquer IDE sem instalar plugin.

## 8. O que ficou de fora, de propósito

- **Exclusão de usuário.** O enunciado pede criar, listar e editar. Não existe
  `DELETE` na API nem botão de excluir na listagem.
- **Busca e filtro na listagem.** Não foram pedidos.
- **Autenticação.** Fora do escopo do case.
- **Flyway/Liquibase, Docker, cache.** Somariam configuração sem atender a
  nenhum requisito do enunciado.
