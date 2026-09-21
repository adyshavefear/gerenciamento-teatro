package br.com.gerenciamentoteatro.dao;

import br.com.gerenciamentoteatro.model.Peca;

public class PecaDAO extends BaseJPADAO<Peca, Long> {
    public PecaDAO() {
        super(Peca.class);
    }
}