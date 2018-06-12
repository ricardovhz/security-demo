package br.com.vanhoz.ricardo.securitydemo.auth;

import javax.persistence.*;
import java.util.Arrays;
import java.util.regex.Pattern;

@Entity
@Table(name = "permission")
public class Permission {

    public enum PermissionAction {
        ALLOW, DENY
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String role;

    private String resource;
    private String fromOwner;
    private String what;

    @Enumerated(EnumType.STRING)
    private PermissionAction action;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFromOwner() {
        return fromOwner;
    }

    public void setFromOwner(String fromOwner) {
        this.fromOwner = fromOwner;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    public PermissionAction getAction() {
        return action;
    }

    public void setAction(PermissionAction action) {
        this.action = action;
    }

    public boolean canAccess(User user, String resourceOwner) {
        return this.fromOwner == null || testPattern(user, resourceOwner);
    }

    private boolean testPattern(User user, String resourceOwner) {
        String pattern = this.fromOwner.replaceAll("\\{me}", user.getLogin()).replaceAll("\\{org}", user.getOrganization());

        System.out.println("Testing against pattern [" + pattern + "] resourceOwner [" + resourceOwner + "]");

        return Arrays.stream(pattern.split(","))
                .map(t -> Pattern.compile(t).matcher(resourceOwner).matches())
                .filter(t -> t)
                .count() > 0;
    }

}
