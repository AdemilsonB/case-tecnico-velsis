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

São oito testes unitários sobre o `UserService`, com o repositório mockado. O
alvo é a regra de negócio e não a infraestrutura, então nenhum deles sobe
contexto do Spring nem precisa de banco no ar. Cobrem o cadastro válido, a
recusa de documento duplicado, a edição de um id inexistente, a edição
preservando o id, e quatro casos do filtro da listagem: termo vazio caindo em
`findAll`, termo com espaços em volta, busca por documento mascarado, e o
cuidado para que um termo sem dígitos não vire uma comparação que casa com a
base inteira.

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

Registro aqui o raciocínio por trás das escolhas que não eram óbvias, incluindo
as alternativas que considerei e descartei.

### Java 17, e não 8 nem 21

O enunciado aceita da 8 para cima. Fiquei no 17 por ser o LTS que a maior parte
dos ambientes corporativos já roda, e porque ele me dá `record` para os DTOs sem
trazer dependência nenhuma: os oito campos do formulário viram um tipo imutável
em poucas linhas. Não subi para o 21 porque o ganho seria pequeno e aumentaria a
chance de esbarrar em uma JDK mais antiga na máquina de quem for rodar o projeto.

### O schema é um script, e o Hibernate só confere

Coloquei a estrutura da tabela em `schema.sql` e deixei o Hibernate em
`ddl-auto=validate`. A alternativa cômoda seria `update`, que cria e altera a
tabela sozinho durante o desenvolvimento; descartei porque ela deixa o banco à
mercê de qualquer mudança de anotação na entidade, e esse tipo de erro costuma
aparecer tarde, quando já existe dado dentro. Com `validate` eu pago o preço de
escrever o DDL à mão e ganho em troca uma garantia: se a entidade e a tabela
divergirem, a aplicação nem sobe. Descubro na inicialização, não em produção.

Isso cobrou o preço logo de início. Declarei `state` como `CHAR(2)`, o Hibernate
esperava `VARCHAR` e a validação derrubava a subida. Resolvi com
`@JdbcTypeCode(SqlTypes.CHAR)` no campo da entidade, e não afrouxando a coluna
para `VARCHAR(2)`, porque prefiro que o banco continue descrevendo o dado como
ele é — uma sigla de exatamente dois caracteres.

### Endereço nas colunas da própria tabela `users`

Mantive `address_line`, `address_number`, `city`, `state` e `zip` dentro de
`users`, como o enunciado desenha. Cheguei a considerar uma tabela `address`
separada, que é o desenho certo quando um usuário pode ter vários endereços —
cobrança, entrega, e por aí vai. Aqui a relação é de um para um e não há
requisito de histórico, então a tabela separada acrescentaria um join a toda
consulta em troca de uma flexibilidade que ninguém pediu.

### CPF de verdade, e não um documento genérico

O enunciado fala em "Documento (CPF/ID)", o que permitiria aceitar qualquer
texto. Preferi implementar o CPF com o cálculo dos dois dígitos verificadores,
porque validar "o formato do documento" sem regra nenhuma vira só uma checagem
de campo preenchido, e deixaria passar erro de digitação que o sistema tinha
como pegar. A contrapartida é que hoje o cadastro não aceita outro tipo de
documento; sinalizo isso no rótulo do campo e no placeholder, para que não
pareça defeito. Se fosse preciso aceitar RG ou passaporte, o caminho seria um
campo de tipo de documento ao lado, escolhendo o validador conforme a opção.

### A mesma validação nos dois lados, com as mesmas mensagens

As regras de `validacao.js` e as anotações de `UserRequest` são espelhadas, e o
cálculo do dígito verificador está escrito duas vezes, em Java e em JavaScript.
Duplicar código incomoda, mas as duas cópias respondem a perguntas diferentes: o
frontend evita uma ida ao servidor por um erro que dá para apontar na hora, e o
backend não pode confiar no que chega pela rede, porque a API atende também fora
do navegador. Mantive as mensagens idênticas de propósito — o usuário lê a mesma
frase venha o erro de onde vier.

### O documento é gravado só com dígitos

A máscara é apresentação e ficou no frontend; no banco `52998224725` é gravado
sem pontuação. Isso mantém a restrição de unicidade consistente
independentemente de como o valor foi digitado, e evita ter que eleger qual das
formas mascaradas seria a canônica.

Essa escolha teve uma consequência que só apareceu no uso. Quem copia o CPF da
listagem, onde ele aparece formatado, e cola no campo de busca, estaria
procurando `390.120.000-25` numa coluna que guarda `39012000025` — e não
encontrava nada. O `UserService` passou a extrair os dígitos do termo antes de
consultar: o nome é comparado com o texto digitado e o documento com os dígitos
extraídos dele. O cuidado extra ali é que um termo sem dígito algum não pode
virar uma comparação vazia, que casaria com a base inteira.

### O cadastro sai da tela, a edição fica

São operações com ritmos diferentes. Cadastrar encerra uma tarefa: depois do 201
o formulário ficaria exibindo um documento que acabou de ser gravado, e um
segundo Salvar esbarraria na unicidade — então a tela volta para a listagem
levando a confirmação. Editar é outra coisa: quem abriu um registro para
corrigir um campo costuma querer ajustar mais um em seguida, e ser devolvido à
listagem a cada gravação obrigaria a navegar de novo. Por isso a edição
permanece onde está, mostra a confirmação ali mesmo, e o botão ao lado — que era
"Cancelar" enquanto ainda havia o que cancelar — passa a ser "Voltar".

### A confirmação some sozinha, o erro não

A faixa de sucesso desaparece depois de alguns segundos, porque confirma algo
que já terminou e não precisa seguir ocupando o topo da tela. A de erro fica até
a pessoa agir: uma mensagem de validação que evapora no meio da leitura esconde
justamente a informação de que o usuário precisa. As duas passam pelo mesmo
`feedback.js`, que marca a faixa com `role="status"` para que um leitor de tela
anuncie o texto antes de ele sumir.

### Restrição de unicidade no documento

O enunciado lista as colunas da tabela, mas não fala de restrições. Acrescentei
`uk_users_document` porque dois cadastros com o mesmo CPF seriam um defeito
funcional, não uma liberdade. A violação também não vaza como erro de banco: o
serviço verifica antes de gravar e o `GlobalExceptionHandler` traduz para 409
com uma mensagem que a tela sabe exibir no campo certo.

### Filtro na listagem

A paginação já pressupõe mais de 20 registros, e a partir desse volume uma
listagem sem busca perde utilidade — achar alguém vira uma caçada de página em
página. Preferi refinar uma tela que já era requisito a acrescentar operações
fora do escopo. A busca é um `LIKE` com curinga dos dois lados, que não aproveita
índice; no volume deste cadastro isso é irrelevante, e deixei anotado no
repositório que a saída em base grande seria busca full-text.

### Busca de CEP pelo ViaCEP

A tela de cadastro consulta o ViaCEP para preencher rua, cidade e estado a
partir do CEP digitado. É uma conveniência, não uma dependência: a chamada sai
do próprio navegador e, se o serviço estiver fora do ar ou o CEP não existir, os
campos continuam editáveis e o cadastro segue normalmente. A aplicação não
precisa de acesso externo para funcionar.

### Sem Lombok

Getters e setters escritos à mão na entidade, `record` nos DTOs. Lombok
economizaria linhas, mas exige plugin instalado na IDE, e não quis que a
primeira impressão do projeto fosse um erro de compilação em uma máquina que não
tem esse plugin.

## 8. O que não entrou

**Exclusão de usuário.** O escopo é criar, listar e editar, então não há `DELETE`
na API nem botão na listagem. Se fosse necessário, eu iria de exclusão lógica —
uma coluna de data de desativação e um filtro na consulta de listagem — em vez
de apagar a linha, para preservar o histórico do cadastro.

**Autenticação.** Fora do escopo do case, e fazer pela metade seria pior do que
não fazer.

**Flyway, Docker, cache.** São coisas que eu levaria para um projeto que vai
crescer e ser mantido por várias pessoas. Aqui acrescentariam configuração e
passos de execução sem resolver nenhum problema que o case apresenta, e
atrapalhariam justamente o "rodar localmente" que o README precisa entregar.
