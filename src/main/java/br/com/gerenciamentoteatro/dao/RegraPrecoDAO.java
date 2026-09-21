package br.com.gerenciamentoteatro.dao;

import br.com.gerenciamentoteatro.model.RegraPreco;

public class RegraPrecoDAO extends BaseJPADAO<RegraPreco, Long> {

    public RegraPrecoDAO() {
        super(RegraPreco.class);
    }
}