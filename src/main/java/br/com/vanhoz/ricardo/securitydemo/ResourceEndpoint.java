package br.com.vanhoz.ricardo.securitydemo;

import br.com.vanhoz.ricardo.securitydemo.auth.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URISyntaxException;
import java.util.Optional;

@RestController
@RequestMapping("/res")
public class ResourceEndpoint {

    private static final ResponseEntity<?> FORBIDDEN = ResponseEntity.status(403).body("You are not allowed to access this resource");

    @Autowired
    private ResourceRepository repository;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AuthService authService;

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getResource(@PathVariable("id") Long id) {
        if (id == null)
            return ResponseEntity.badRequest().build();

        Optional<ResourceObject> object = repository.findById(id);

        if (object.isPresent()) {
            if (!authService.canAccessResource("GET", ResourceObject.class, object.get())) {
                return FORBIDDEN;
            }

            return ResponseEntity.ok(object.get());
        } else
            return ResponseEntity.notFound().build();
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createResource(@RequestBody ResourceObject object) throws URISyntaxException {

        if (!authService.canAccessResource("CREATE", ResourceObject.class, null)) {
            return FORBIDDEN;
        }

        object.setId(null);
        User user = (User) context.getServletContext().getAttribute("session-user");
        object.setOwner(user);
        repository.save(object);
        return ResponseEntity.created(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(object.getId()).toUri()).build();
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        if (id == null)
            return ResponseEntity.badRequest().build();

        Optional<ResourceObject> object = repository.findById(id);

        if (object.isPresent()) {
            if (!authService.canAccessResource("DELETE", ResourceObject.class, object.get())) {
                return FORBIDDEN;
            }
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

}
