package br.com.vanhoz.ricardo.securitydemo;

import br.com.vanhoz.ricardo.securitydemo.auth.Permission;
import br.com.vanhoz.ricardo.securitydemo.auth.PermissionRepository;
import br.com.vanhoz.ricardo.securitydemo.auth.User;
import br.com.vanhoz.ricardo.securitydemo.auth.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

@Component
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private WebApplicationContext context;

    public boolean login(String login, String password) {
        Optional<User> u = userRepository.findById(login);
        if (u.isPresent()) {
            String encPassword = u.get().getPassword();

            if (BCrypt.checkpw(password, encPassword)) {
                u.get().genNewToken();
                userRepository.save(u.get());
                return true;
            }
        }

        return false;
    }

    public void logout(String login) {
        Optional<User> u = userRepository.findById(login);
        u.get().invalidateToken();
        userRepository.save(u.get());
    }

    public String getAuthToken(String login) {
        return userRepository.findById(login).get().getToken();
    }

    public Optional<User> getUserFromToken(String token) {
        return userRepository.findByToken(token);
    }

    public boolean authenticate(String token, HttpServletRequest request) {
        Optional<User> user = getUserFromToken(token);
        return user.isPresent();
    }

    public <T> boolean canAccessResource(String what, Class<T> resourceClass, T resource) {
        User user = (User) context.getServletContext().getAttribute("session-user");

        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            return Arrays.stream(user.getRoles().split(","))
                    .map(t -> canAccessResource(user, t, what, resourceClass, resource))
                    .filter(t -> t).count() > 0;
        }
        return false;
    }

    public <T> boolean canAccessResource(User user, String role, String what, Class<T> resourceClass, T resource) {
        if (resource == null) {
            Optional<Permission> permission = permissionRepository
                    .findByRoleAndResourceAndWhat(role, resourceClass.getName(), what);

            if (permission.isPresent()) {
                if (Permission.PermissionAction.ALLOW.equals(permission.get().getAction())) {
                    System.out.println(what + " permission for role [" + role + "] is allowed");
                    return true;
                }
            }
        }

        if (resourceClass.equals(ResourceObject.class)) {
            return canAccessResourceObject(user, role, what, (ResourceObject) resource);
        }
        return false;
    }

    private boolean canAccessResourceObject(User user, String role, String what, ResourceObject resource) {
        if ((user.getOrganization() + ":" + user.getLogin()).equals(resource.getOwner())) {
            System.out.println("Resource is owned by user");
            return true;
        }

        Optional<Permission> permission = permissionRepository
                .findByRoleAndResourceAndWhat(role, resource.getClass().getName(), what);

        if (permission.isPresent()) {
            if (Permission.PermissionAction.ALLOW.equals(permission.get().getAction()) &&
                    permission.get().canAccess(user, resource.getOwner())) {
                System.out.println(what + " permission for role [" + role + "] is allowed");
                return true;
            }
        }

        return false;
    }
}
