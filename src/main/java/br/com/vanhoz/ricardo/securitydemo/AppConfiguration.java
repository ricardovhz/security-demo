package br.com.vanhoz.ricardo.securitydemo;

import br.com.vanhoz.ricardo.securitydemo.auth.User;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = {ResourceObject.class, User.class})
public class AppConfiguration {

}
