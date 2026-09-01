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

- `/` listagem de usuários (página principal, com busca por nome ou documento
  e paginação de 20 registros)
- `/novo.html` cadastro
- `/editar.html?id=N` edição

## 5. Testes

```bash
./mvnw test
```

Cobrem as regras do `UserService`: cadastro válido, documento duplicado,
edição de id inexistente, edição preservando o id e o desvio entre listagem
completa e listagem filtrada.

## 6. Endpoints

| Método | Rota | Sucesso | Erros |
|---|---|---|---|
| GET | `/api/users?termo=&page=0&size=20` | 200 com a página | |
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

A exclusão de registros não foi implementada por não constar no escopo
definido (criar, listar e editar). Caso fosse requisito, a abordagem proposta
seria exclusão lógica, com uma coluna de data de desativação e filtro na
consulta de listagem, preservando o histórico do cadastro em vez de remover a
linha fisicamente.

Acrescentei restrição de unicidade na coluna document. O enunciado especifica
as colunas da tabela, mas não as restrições; considerei que permitir dois
cadastros com o mesmo CPF seria um defeito funcional. A violação é tratada e
devolve mensagem clara ao usuário.

Acrescentei um filtro por nome ou documento na listagem. O próprio enunciado
prevê o cenário de mais de 20 registros ao pedir paginação, e a partir desse
volume a tela de listagem perde utilidade sem uma forma de localizar o
cadastro. Optei por refinar uma tela que já era requisito, e não por
acrescentar operações fora do escopo definido.

A tela de cadastro consulta o serviço público ViaCEP para preencher
logradouro, cidade e estado a partir do CEP informado. A consulta é feita
diretamente pelo navegador e é não bloqueante: se o serviço estiver
indisponível ou o CEP não for encontrado, os campos permanecem editáveis e o
cadastro segue normalmente. A aplicação não depende de acesso externo para
funcionar.

## 8. O que ficou de fora, de propósito

- **Exclusão de usuário.** O enunciado pede criar, listar e editar. Não existe
  `DELETE` na API nem botão de excluir na listagem.
- **Autenticação.** Fora do escopo do case.
- **Flyway/Liquibase, Docker, cache.** Somariam configuração sem atender a
  nenhum requisito do enunciado.
