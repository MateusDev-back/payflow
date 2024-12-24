package br.com.mateus.payflow.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.mateus.payflow.domain.user.model.UserEntity;

import java.math.BigDecimal;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    Optional<UserEntity> findByCpf(String cpf);

    Optional<UserEntity> findByEmail(String email);

    @Modifying
    @Query("UPDATE UserEntity u SET u.balance = :balance WHERE u.id = :userId")
    void updateBalance(@Param("userId") Long userId, @Param("balance") BigDecimal balance);
}
