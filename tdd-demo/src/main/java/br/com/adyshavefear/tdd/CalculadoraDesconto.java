package br.com.adyshavefear.tdd;

import java.math.BigDecimal;

public class CalculadoraDesconto {
    public BigDecimal calcular(BigDecimal valor) {
        if (valor.compareTo(BigDecimal.valueOf(100)) >= 0) {
            return valor.multiply(BigDecimal.valueOf(90)).divide(BigDecimal.valueOf(100));
        }
        return valor;
    }
}
