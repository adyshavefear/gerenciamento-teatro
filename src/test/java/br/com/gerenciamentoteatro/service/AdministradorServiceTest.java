package br.com.gerenciamentoteatro.service;

import br.com.gerenciamentoteatro.model.Administrador;
import br.com.gerenciamentoteatro.repository.AdministradorRepository;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AdministradorServiceTest {
    private AdministradorRepository repository;
    private AdministradorService service;

    @BeforeEach
    void setUp() {
        TestDatabase.reset();
        repository = new AdministradorRepository();
        service = new AdministradorService(repository);
    }

    @Test
    void cadastraHashEValidaCredenciais() {
        service.cadastrarAdministrador("admin@example.invalid", "test-password");
        assertNotEquals("test-password", repository.buscarUnico().orElseThrow().getSenha());
        assertTrue(service.realizarLogin("admin@example.invalid", "test-password"));
        assertFalse(service.realizarLogin("admin@example.invalid", "wrong-password"));
        assertFalse(service.realizarLogin("admin@example.invalid", null));
    }

    @Test
    void migraSenhaAntigaSomenteAposLoginValido() {
        repository.salvar(new Administrador("legacy@example.invalid", "old-password"));
        assertFalse(service.realizarLogin("legacy@example.invalid", "wrong-password"));
        assertEquals("old-password", repository.buscarUnico().orElseThrow().getSenha());
        assertTrue(service.realizarLogin("legacy@example.invalid", "old-password"));
        assertTrue(repository.buscarUnico().orElseThrow().getSenha().startsWith("pbkdf2-sha256$"));
        assertTrue(service.realizarLogin("legacy@example.invalid", "old-password"));
    }

    @Test
    void persisteCodigoTrocaSenhaEImpedeReuso() {
        service.cadastrarAdministrador("admin@example.invalid", "old-password");
        String codigo = service.gerarCodigoRecuperacao("admin@example.invalid");
        assertEquals(codigo, repository.buscarUnico().orElseThrow().getCodigoRecuperacao());
        service.alterarSenhaComCodigo("admin@example.invalid", codigo, "new-password");
        assertTrue(service.realizarLogin("admin@example.invalid", "new-password"));
        assertFalse(service.realizarLogin("admin@example.invalid", "old-password"));
        assertThrows(IllegalArgumentException.class,
                () -> service.alterarSenhaComCodigo("admin@example.invalid", codigo, "another-password"));
    }

    @Test
    void rejeitaCodigoExpirado() {
        service.cadastrarAdministrador("admin@example.invalid", "test-password");
        String codigo = service.gerarCodigoRecuperacao("admin@example.invalid");
        Administrador admin = repository.buscarUnico().orElseThrow();
        admin.setCodigoRecuperacaoExpiraEm(Instant.now().minusSeconds(1));
        repository.salvar(admin);
        assertThrows(IllegalArgumentException.class,
                () -> service.alterarSenhaComCodigo("admin@example.invalid", codigo, "new-password"));
    }
}
