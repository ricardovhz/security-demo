package br.com.vanhoz.ricardo.securitydemo.auth;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Entity
@Table(name = "usuario")
public class User {

    @Id
    private String login;
    private String password;
    private String name;
    private String organization;
    private String roles;
    private String token;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void genNewToken() {
        long random = new Double(Math.random() * 1000.0D).longValue();
        try {
            byte[] t = MessageDigest.getInstance("SHA-256")
                    .digest(ByteBuffer.allocate(Long.BYTES).putLong(random).array());
            this.token = Base64.getEncoder().encodeToString(t);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void invalidateToken() {
        this.token = null;
    }
}
