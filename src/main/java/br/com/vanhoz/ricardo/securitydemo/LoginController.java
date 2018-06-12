package br.com.vanhoz.ricardo.securitydemo;

import br.com.vanhoz.ricardo.securitydemo.auth.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

@RestController
public class LoginController {

    @Autowired
    private AuthService authService;

    @Autowired
    private WebApplicationContext context;

    @RequestMapping(path = "/login", method = RequestMethod.POST, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginInfo) {
        if (authService.login(loginInfo.get("login"), loginInfo.get("password"))) {
            System.out.println("User authenticated");
            return ResponseEntity.ok(authService.getAuthToken(loginInfo.get("login")));
        }
        return ResponseEntity.status(401).body("You are not authenticated");
    }

    @RequestMapping(path = "logout", method = RequestMethod.GET)
    public ResponseEntity<?> logout() {
        User user = (User) context.getServletContext().getAttribute("session-user");
        authService.logout(user.getLogin());
        return ResponseEntity.ok().build();
    }

}
