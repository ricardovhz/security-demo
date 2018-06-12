package br.com.vanhoz.ricardo.securitydemo.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByRoleAndResourceAndWhat(String role, String resource, String what);

}
