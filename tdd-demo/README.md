# Demonstração de TDD

Exemplo pequeno de **Test-Driven Development (TDD)** usando Java 21, Maven e JUnit 5.

## Objetivo

Demonstrar na prática o ciclo **Red → Green → Refactor**.

1. **Red:** escrever um teste para um comportamento que ainda não existe.
2. **Green:** implementar apenas o necessário para fazer o teste passar.
3. **Refactor:** melhorar o código mantendo os testes passando.

## Exemplo

A classe `CalculadoraDesconto` calcula o valor final de uma compra:

- abaixo de R$ 100: nenhum desconto;
- a partir de R$ 100: 10% de desconto.

## Executar

```bash
mvn test
```

## Demonstração

O histórico mostra a evolução do desenvolvimento orientado por testes. Para a apresentação, destaque que o segundo teste é escrito antes da implementação do desconto.

A ideia central é que o teste defina o comportamento esperado e forneça feedback rápido durante a implementação.
