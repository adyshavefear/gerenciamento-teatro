# Gerenciamento do Teatro

Projeto desenvolvido originalmente na disciplina de **POO (Programação Orientada a Objetos)** e posteriormente aprimorado na disciplina de **BD2 (Banco de Dados 2)**.

## Tecnologias

* Java
* Maven
* Swing
* JPA
* Hibernate
* H2 Database

## Persistência e Arquitetura

* Persistência de dados utilizando **JPA/Hibernate**
* Banco de dados **H2**
* Camada DAO para acesso aos dados
* Repositórios integrados com JPA
* Gerenciamento do `EntityManager` através do `JPAUtil`

## Funcionalidades

* Cadastro e autenticação do administrador
* Cadastro e gerenciamento de regras de preço
* Cadastro e gerenciamento de propostas de aluguel
* Conversão de propostas em contratos
* Venda de ingressos
* Geração de lista de presença em CSV
* Geração de relatório financeiro por peça em PDF
* Geração de relatório geral de receitas do teatro em PDF
* Atualização dos dados das telas diretamente a partir do banco de dados
* Encerramento e extensão de contratos de aluguel

## Estrutura

O projeto utiliza uma arquitetura dividida em:

* `model` — entidades e enums do sistema
* `dao` — camada de acesso aos dados
* `repository` — persistência e gerenciamento do JPA
* `service` — regras de negócio
* `ui` — interfaces gráficas desenvolvidas com Swing

## Execução e testes

Requer Java 21 e Maven. Execute `mvn test` para testar com H2 em memória,
separado de `banco_teatro`. Na IDE, configure a opção de VM
`-Dteatro.persistenceUnit=TeatroTestPU` ao executar testes.

Na primeira abertura, cadastre seu administrador na tela de login. Não há conta
ou senha padrão. Senhas antigas em texto são convertidas para PBKDF2 no primeiro
login válido; as novas já são armazenadas com salt e hash. Faça backup do banco
antes de atualizar. Códigos de recuperação duram 15 minutos e são de uso único.
O envio por e-mail ainda é uma simulação no console, destinada ao uso local.

O valor do aluguel é calculado pela duração de cada exibição e pelas regras de
preço cadastradas. Cadastre regras que cubram todo o período antes de criar ou
alterar contratos. A alteração não recalcula contratos antigos automaticamente.

## Evolução do Projeto

O projeto começou utilizando estruturas de dados em memória e foi posteriormente migrado para uma arquitetura baseada em **JPA/Hibernate**, com persistência em banco de dados relacional H2.

A evolução para BD2 também envolveu a criação da camada DAO, refatoração dos repositórios, mapeamento das entidades e integração das funcionalidades de relatórios e exportação.
