package br.com.mateus.payflow.domain.user.repository;

import java.math.BigDecimal;

import br.com.mateus.payflow.domain.user.model.UserEntity;

public interface UserBalanceRepository {

    void debit(UserEntity user, BigDecimal amount);

    void credit(UserEntity user, BigDecimal amount);

}
