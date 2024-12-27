package br.com.mateus.payflow.domain.charge.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.mateus.payflow.domain.charge.model.ChargeEntity;
import br.com.mateus.payflow.domain.user.model.UserEntity;
import br.com.mateus.payflow.enums.charge.ChargeStatus;

@Repository
public interface ChargeRepository extends JpaRepository<ChargeEntity, String> {
    @Query("SELECT c FROM ChargeEntity c WHERE c.payer = :payer AND c.status = :status ORDER BY c.createdAt DESC")
    List<ChargeEntity> findByPayerAndStatus(@Param("payer") UserEntity payer, @Param("status") ChargeStatus status);

    @Query("SELECT c FROM ChargeEntity c WHERE c.payee = :payee AND c.status = :status ORDER BY c.createdAt DESC")
    List<ChargeEntity> findByPayeeAndStatus(@Param("payee") UserEntity payee, @Param("status") ChargeStatus status);

    @Query("SELECT c FROM ChargeEntity c WHERE c.payer = :payer ORDER BY c.createdAt DESC")
    List<ChargeEntity> findByPayer(@Param("payer") UserEntity payer);

    @Query("SELECT c FROM ChargeEntity c WHERE c.payee = :payee ORDER BY c.createdAt DESC")
    List<ChargeEntity> findByPayee(@Param("payee") UserEntity payee);

    Optional<ChargeEntity> findById(String id);

    List<ChargeEntity> findByStatus(ChargeStatus status);
}
