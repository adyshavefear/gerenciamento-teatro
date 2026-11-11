package br.com.gerenciamentoteatro.service;

import br.com.gerenciamentoteatro.model.Administrador;
import br.com.gerenciamentoteatro.repository.AdministradorRepository;

import java.util.Optional;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class AdministradorService {

    private final AdministradorRepository repository;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String HASH_PREFIX = "pbkdf2-sha256$600000$";

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
        Administrador admin = new Administrador(email, hashSenha(senha));
        repository.salvar(admin);
    }

    public boolean realizarLogin(String email, String senha) {
        Optional<Administrador> adminOpt = repository.buscarUnico();
        if (adminOpt.isEmpty() || senha == null) {
            return false;
        }
        Administrador admin = adminOpt.get();
        if (!admin.getEmail().equals(email) || !confereSenha(senha, admin.getSenha())) {
            return false;
        }
        // Upgrade legacy plaintext passwords only after a successful login.
        if (!admin.getSenha().startsWith(HASH_PREFIX)) {
            admin.setSenha(hashSenha(senha));
            repository.salvar(admin);
        }
        return true;
    }

    public String gerarCodigoRecuperacao(String email) {
        Optional<Administrador> adminOpt = repository.buscarUnico();
        if (adminOpt.isEmpty() || !adminOpt.get().getEmail().equals(email)) {
            throw new IllegalArgumentException("E-mail não encontrado.");
        }

        // Gera um código numérico aleatório de 6 dígitos
        String codigo = String.format("%06d", RANDOM.nextInt(1_000_000));
        adminOpt.get().setCodigoRecuperacao(codigo);
        adminOpt.get().setCodigoRecuperacaoExpiraEm(Instant.now().plusSeconds(900));
        repository.salvar(adminOpt.get());

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
        if (admin.getCodigoRecuperacao() == null || !admin.getCodigoRecuperacao().equals(codigoInformado)
                || admin.getCodigoRecuperacaoExpiraEm() == null
                || !Instant.now().isBefore(admin.getCodigoRecuperacaoExpiraEm())) {
            throw new IllegalArgumentException("Código de recuperação inválido.");
        }

        if (novaSenha == null || novaSenha.isBlank()) {
            throw new IllegalArgumentException("A nova senha não pode ser vazia.");
        }

        admin.setSenha(hashSenha(novaSenha));
        admin.setCodigoRecuperacao(null); // Limpa o código após o uso
        admin.setCodigoRecuperacaoExpiraEm(null);
        repository.salvar(admin);
    }

    private static String hashSenha(String senha) {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return HASH_PREFIX + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(derivarSenha(senha, salt));
    }

    private static boolean confereSenha(String senha, String salvo) {
        if (!salvo.startsWith("pbkdf2-sha256$")) {
            return MessageDigest.isEqual(senha.getBytes(StandardCharsets.UTF_8), salvo.getBytes(StandardCharsets.UTF_8));
        }
        if (!salvo.startsWith(HASH_PREFIX)) return false;
        String[] partes = salvo.substring(HASH_PREFIX.length()).split("\\$", -1);
        if (partes.length != 2) return false;
        try {
            byte[] salt = Base64.getDecoder().decode(partes[0]);
            byte[] hash = Base64.getDecoder().decode(partes[1]);
            return salt.length == 16 && hash.length == 32 && MessageDigest.isEqual(derivarSenha(senha, salt), hash);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] derivarSenha(String senha, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(), salt, 600_000, 256);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Não foi possível proteger a senha.", e);
        } finally {
            spec.clearPassword();
        }
    }
}
