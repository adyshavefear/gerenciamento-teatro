package br.com.adyshavefear.tdd;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculadoraDescontoTest {

    @Test
    void deveManterValorQuandoCompraNaoAtingeDesconto() {
        var calculadora = new CalculadoraDesconto();
        var total = calculadora.calcular(BigDecimal.valueOf(80));
        assertEquals(BigDecimal.valueOf(80), total);
    }

    @Test
    void deveAplicarDezPorCentoParaComprasAPartirDeCemReais() {
        var calculadora = new CalculadoraDesconto();
        var total = calculadora.calcular(BigDecimal.valueOf(100));
        assertEquals(BigDecimal.valueOf(90), total);
    }
}
