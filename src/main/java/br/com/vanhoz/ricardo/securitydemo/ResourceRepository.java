package br.com.vanhoz.ricardo.securitydemo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends JpaRepository<ResourceObject, Long> {
}
