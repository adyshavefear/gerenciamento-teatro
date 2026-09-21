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

## Evolução do Projeto

O projeto começou utilizando estruturas de dados em memória e foi posteriormente migrado para uma arquitetura baseada em **JPA/Hibernate**, com persistência em banco de dados relacional H2.

A evolução para BD2 também envolveu a criação da camada DAO, refatoração dos repositórios, mapeamento das entidades e integração das funcionalidades de relatórios e exportação.
