package br.com.mateus.payflow.application.balance.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.mateus.payflow.domain.user.model.UserEntity;
import br.com.mateus.payflow.domain.user.repository.UserBalanceRepository;
import br.com.mateus.payflow.domain.user.repository.UserRepository;

@Service
public class BalanceService implements UserBalanceRepository {

    private final UserRepository userRepository;

    @Autowired
    public BalanceService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void debit(UserEntity user, BigDecimal amount) {

        user.setBalance(user.getBalance().subtract(amount));
        userRepository.save(user);
    }

    @Override
    public void credit(UserEntity user, BigDecimal amount) {
        user.setBalance(user.getBalance().add(amount));
        userRepository.save(user);
    }

}