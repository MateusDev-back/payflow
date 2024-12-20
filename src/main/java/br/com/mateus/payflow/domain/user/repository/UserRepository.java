package br.com.mateus.payflow.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.mateus.payflow.domain.user.model.UserEntity;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    Optional<UserEntity> findByCpf(String cpf);

    Optional<UserEntity> findByEmail(String email);
}
