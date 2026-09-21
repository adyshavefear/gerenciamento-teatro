package br.com.gerenciamentoteatro.service;

import br.com.gerenciamentoteatro.model.Administrador;
import br.com.gerenciamentoteatro.repository.AdministradorRepository;

import java.util.Optional;
import java.util.Random;

public class AdministradorService {

    private final AdministradorRepository repository;

    public AdministradorService(AdministradorRepository repository) {
        this.repository = repository;
    }

    public boolean existeAdministrador() {
        return repository.buscarUnico().isPresent();
    }

    public void cadastrarAdministrador(String email, String senha) {
        if (existeAdministrador()) {
            throw new IllegalStateException("O administrador já foi cadastrado.");
        }
        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("E-mail e senha são obrigatórios.");
        }
        Administrador admin = new Administrador(System.currentTimeMillis(), email, senha);
        repository.salvar(admin);
    }

    public boolean realizarLogin(String email, String senha) {
        Optional<Administrador> adminOpt = repository.buscarUnico();
        if (adminOpt.isEmpty()) {
            return false;
        }
        Administrador admin = adminOpt.get();
        return admin.getEmail().equals(email) && admin.getSenha().equals(senha);
    }

    public String gerarCodigoRecuperacao(String email) {
        Optional<Administrador> adminOpt = repository.buscarUnico();
        if (adminOpt.isEmpty() || !adminOpt.get().getEmail().equals(email)) {
            throw new IllegalArgumentException("E-mail não encontrado.");
        }

        // Gera um código numérico aleatório de 6 dígitos
        String codigo = String.format("%06d", new Random().nextInt(999999));
        adminOpt.get().setCodigoRecuperacao(codigo);

        // Simula o envio por e-mail (a ser integrado com JavaMail futuramente)
        System.out.println("[EMAIL SIMULADO] Código de recuperação para " + email + ": " + codigo);

        return codigo;
    }

    public void alterarSenhaComCodigo(String email, String codigoInformado, String novaSenha) {
        Optional<Administrador> adminOpt = repository.buscarUnico();
        if (adminOpt.isEmpty() || !adminOpt.get().getEmail().equals(email)) {
            throw new IllegalArgumentException("Administrador não encontrado.");
        }

        Administrador admin = adminOpt.get();
        if (admin.getCodigoRecuperacao() == null || !admin.getCodigoRecuperacao().equals(codigoInformado)) {
            throw new IllegalArgumentException("Código de recuperação inválido.");
        }

        if (novaSenha == null || novaSenha.isBlank()) {
            throw new IllegalArgumentException("A nova senha não pode ser vazia.");
        }

        admin.setSenha(novaSenha);
        admin.setCodigoRecuperacao(null); // Limpa o código após o uso
        repository.salvar(admin);
    }
}