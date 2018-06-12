package br.com.vanhoz.ricardo.securitydemo;

import br.com.vanhoz.ricardo.securitydemo.auth.User;

import javax.persistence.*;

@Entity
@Table(name = "resource_object")
public class ResourceObject {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String message;

    private String owner;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setOwner(User owner) {
        this.owner = owner.getOrganization() + ":" + owner.getLogin();
    }

}
