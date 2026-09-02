-- Massa de dados ficticios para avaliacao do case.
--
-- Sao 25 usuarios, o suficiente para a listagem passar de uma pagina e
-- deixar a paginacao (20 por pagina) visivel sem precisar cadastrar nada
-- a mao. O arquivo roda automaticamente na subida da aplicacao, junto com
-- o schema.sql, por causa de spring.sql.init.mode=always.
--
-- Os CPFs sao ficticios, mas com digito verificador valido: sem isso um
-- registro aberto na tela de edicao seria recusado pela validacao ao salvar.
--
-- O ON CONFLICT deixa o script repetivel. Subir a aplicacao de novo nao
-- duplica os registros nem quebra na restricao de unicidade do documento,
-- e nao mexe em quem ja foi cadastrado pela tela.

INSERT INTO users (name, birth_date, document, address_line, address_number,
                   city, state, zip, created_at, updated_at) VALUES
    ('Adriana Peixoto', '1985-03-14', '31247072819', 'Rua XV de Novembro', '1240', 'Curitiba', 'PR', '80020310', '2026-01-01 09:10:00', '2026-01-01 09:10:00'),
    ('Bruno Tavares', '1979-11-02', '41539111431', 'Avenida Paulista', '900', 'Sao Paulo', 'SP', '01310100', '2026-02-02 09:11:00', '2026-02-02 09:11:00'),
    ('Camila Rezende', '1992-07-25', '52839069067', 'Rua da Bahia', '77', 'Belo Horizonte', 'MG', '30160010', '2026-03-03 09:12:00', '2026-03-03 09:12:00'),
    ('Diego Nogueira', '1988-01-09', '63131107650', 'Avenida Atlantica', '1702', 'Rio de Janeiro', 'RJ', '22021001', '2026-04-04 09:13:00', '2026-04-04 09:13:00'),
    ('Eduarda Vasconcelos', '1995-05-30', '74431065210', 'Rua dos Andradas', '533', 'Porto Alegre', 'RS', '90020004', '2026-05-05 09:14:00', '2026-05-05 09:14:00'),
    ('Fabio Quintela', '1983-09-18', '85731022844', 'Avenida Sete de Setembro', '212', 'Salvador', 'BA', '40060000', '2026-06-06 09:15:00', '2026-06-06 09:15:00'),
    ('Gabriela Sanches', '1990-12-07', '96023061494', 'Rua Domingos Martins', '48', 'Vitoria', 'ES', '29010030', '2026-01-07 09:16:00', '2026-01-07 09:16:00'),
    ('Henrique Bastos', '1976-04-21', '17310309081', 'Avenida Beira Mar', '1855', 'Florianopolis', 'SC', '88015700', '2026-02-08 09:17:00', '2026-02-08 09:17:00'),
    ('Isadora Campelo', '1998-08-13', '28610266699', 'Rua Barao do Rio Branco', '96', 'Juiz de Fora', 'MG', '36010000', '2026-03-09 09:18:00', '2026-03-09 09:18:00'),
    ('Joaquim Ferraz', '1971-02-26', '39910224275', 'Avenida Goias', '440', 'Goiania', 'GO', '74005010', '2026-04-10 09:19:00', '2026-04-10 09:19:00'),
    ('Karina Andrade', '1993-06-11', '50202262898', 'Rua Marechal Deodoro', '1315', 'Londrina', 'PR', '86020030', '2026-05-11 09:20:00', '2026-05-11 09:20:00'),
    ('Leonardo Pacheco', '1986-10-04', '61502220474', 'Avenida Boa Viagem', '2020', 'Recife', 'PE', '51011000', '2026-06-12 09:21:00', '2026-06-12 09:21:00'),
    ('Mariana Duarte', '1994-03-29', '72802178008', 'Rua Vinte e Quatro de Maio', '61', 'Sao Paulo', 'SP', '01041001', '2026-01-13 09:22:00', '2026-01-13 09:22:00'),
    ('Nelson Villares', '1968-07-16', '83194216678', 'Avenida Amazonas', '870', 'Belo Horizonte', 'MG', '30180001', '2026-02-14 09:23:00', '2026-02-14 09:23:00'),
    ('Olivia Marchetti', '1997-11-23', '94494174238', 'Rua Comendador Araujo', '499', 'Curitiba', 'PR', '80420000', '2026-03-15 09:24:00', '2026-03-15 09:24:00'),
    ('Patricia Lobato', '1982-05-08', '15781421807', 'Avenida Independencia', '1204', 'Santa Maria', 'RS', '97050000', '2026-04-16 09:25:00', '2026-04-16 09:25:00'),
    ('Rafael Siqueira', '1975-09-01', '26073460457', 'Rua Governador Valadares', '25', 'Divinopolis', 'MG', '35500000', '2026-05-17 09:26:00', '2026-05-17 09:26:00'),
    ('Simone Vasques', '1991-01-19', '37373418007', 'Avenida Ipiranga', '640', 'Sao Paulo', 'SP', '01046010', '2026-06-18 09:27:00', '2026-06-18 09:27:00'),
    ('Tiago Monteiro', '1989-04-27', '48673375606', 'Rua Sete de Abril', '112', 'Sao Paulo', 'SP', '01043000', '2026-01-19 09:28:00', '2026-01-19 09:28:00'),
    ('Ursula Bittencourt', '1996-08-05', '59973333292', 'Avenida Cristovao Colombo', '2300', 'Porto Alegre', 'RS', '90560002', '2026-02-20 09:29:00', '2026-02-20 09:29:00'),
    ('Vinicius Aragao', '1980-12-15', '60286181878', 'Rua Chile', '31', 'Salvador', 'BA', '40020000', '2026-03-21 09:30:00', '2026-03-21 09:30:00'),
    ('Wanda Ferreira', '1973-06-22', '71586139410', 'Avenida Rio Branco', '156', 'Rio de Janeiro', 'RJ', '20040901', '2026-04-22 09:31:00', '2026-04-22 09:31:00'),
    ('Xenia Prado', '1999-02-03', '82886097080', 'Rua Quinze de Novembro', '300', 'Blumenau', 'SC', '89010001', '2026-05-23 09:32:00', '2026-05-23 09:32:00'),
    ('Yuri Delgado', '1987-10-11', '93178135693', 'Avenida Afonso Pena', '1270', 'Belo Horizonte', 'MG', '30130003', '2026-06-24 09:33:00', '2026-06-24 09:33:00'),
    ('Zilda Fontoura', '1970-03-06', '14465383207', 'Rua Padre Anchieta', '2050', 'Curitiba', 'PR', '80730000', '2026-01-25 09:34:00', '2026-01-25 09:34:00')
ON CONFLICT (document) DO NOTHING;
