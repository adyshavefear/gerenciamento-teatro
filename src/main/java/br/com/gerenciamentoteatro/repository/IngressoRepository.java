package br.com.gerenciamentoteatro.repository;

import br.com.gerenciamentoteatro.dao.BaseJPADAO;
import br.com.gerenciamentoteatro.model.Ingresso;

public class IngressoRepository extends BaseJPADAO<Ingresso, Long> {

    public IngressoRepository() {
        super(Ingresso.class);
    }
}