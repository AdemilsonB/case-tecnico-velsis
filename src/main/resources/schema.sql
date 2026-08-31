-- Schema controlado por script. O Hibernate sobe com ddl-auto=validate e
-- apenas confere a entidade contra estas colunas.
CREATE TABLE IF NOT EXISTS users (
    id             BIGSERIAL    PRIMARY KEY,
    name           VARCHAR(120) NOT NULL,
    birth_date     DATE         NOT NULL,
    document       VARCHAR(14)  NOT NULL,
    address_line   VARCHAR(150) NOT NULL,
    address_number VARCHAR(10)  NOT NULL,
    city           VARCHAR(80)  NOT NULL,
    state          CHAR(2)      NOT NULL,
    zip            VARCHAR(8)   NOT NULL,
    created_at     TIMESTAMP    NOT NULL,
    updated_at     TIMESTAMP    NOT NULL,
    CONSTRAINT uk_users_document UNIQUE (document)
);
