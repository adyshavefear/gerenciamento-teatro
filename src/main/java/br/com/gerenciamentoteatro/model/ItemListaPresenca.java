package br.com.gerenciamentoteatro.model;

public class ItemListaPresenca {

    private String nomeCliente;
    private String cpfCliente;
    private String emailCliente;
    private int totalIngressosComprados;

    public ItemListaPresenca(String nomeCliente, String cpfCliente, String emailCliente, int totalIngressosComprados) {
        this.nomeCliente = nomeCliente;
        this.cpfCliente = cpfCliente;
        this.emailCliente = emailCliente;
        this.totalIngressosComprados = totalIngressosComprados;
    }

    public String getNomeCliente() { return nomeCliente; }
    public String getCpfCliente() { return cpfCliente; }
    public String getEmailCliente() { return emailCliente; }
    public int getTotalIngressosComprados() { return totalIngressosComprados; }
}