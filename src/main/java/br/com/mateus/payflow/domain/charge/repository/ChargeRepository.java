package br.com.mateus.payflow.domain.charge.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.mateus.payflow.domain.charge.model.ChargeEntity;
import br.com.mateus.payflow.domain.user.model.UserEntity;
import br.com.mateus.payflow.enums.charge.ChargeStatus;

@Repository
public interface ChargeRepository extends JpaRepository<ChargeEntity, String> {
    List<ChargeEntity> findByPayerAndStatus(UserEntity payer, ChargeStatus status);

    List<ChargeEntity> findByPayeeAndStatus(UserEntity payee, ChargeStatus status);

    List<ChargeEntity> findByPayer(UserEntity payer);

    List<ChargeEntity> findByPayee(UserEntity payee);

    Optional<ChargeEntity> findById(String id);

    List<ChargeEntity> findByStatus(ChargeStatus status);
}
