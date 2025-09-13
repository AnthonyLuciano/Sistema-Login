package anthony.sistemalogin.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String email;
    Boolean emailVerificado;
    private String codigoVerificacao;
    private Boolean admin; // true = funcionário, false = cliente
    private String fotoUrl;
    private Boolean ativo = true; // Indica se o usuário está ativo ou não

    // Getters e setters

    
    public Boolean isAtivo() {
        return ativo;
    }
    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
    public Long getId() { return id; }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Boolean isEmailVerificado() {
        return emailVerificado;
    }
    public void setEmailVerificado(Boolean emailVerificado) {
        this.emailVerificado = emailVerificado;
    }
    public String getCodigoVerificacao() {
        return codigoVerificacao;
    }
    public void setCodigoVerificacao(String codigoVerificacao) {
        this.codigoVerificacao = codigoVerificacao;
    }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Boolean isAdmin() { return admin; }
    public void setAdmin(Boolean admin) { this.admin = admin; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
}