package br.com.vanhoz.ricardo.securitydemo.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByToken(String token);
}
