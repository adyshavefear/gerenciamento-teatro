package br.com.gerenciamentoteatro.dao;

import br.com.gerenciamentoteatro.model.PropostaAluguel;

public class PropostaAluguelDAO extends BaseJPADAO<PropostaAluguel, Long> {
    public PropostaAluguelDAO() {
        super(PropostaAluguel.class);
    }
}