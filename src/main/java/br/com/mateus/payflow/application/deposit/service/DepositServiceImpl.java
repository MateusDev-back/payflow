package br.com.mateus.payflow.application.deposit.service;

import br.com.mateus.payflow.application.deposit.dto.DepositDTO;
import br.com.mateus.payflow.application.payment.integration.ExternalAuthorizerClient;
import br.com.mateus.payflow.common.exception.deposit.DepositException;
import br.com.mateus.payflow.common.exception.user.UserException;
import br.com.mateus.payflow.domain.user.model.UserEntity;
import br.com.mateus.payflow.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DepositServiceImpl implements DepositService {

    private final UserRepository userRepository;
    private final ExternalAuthorizerClient externalAuthorizerClient;

    @Autowired
    public DepositServiceImpl(UserRepository userRepository, ExternalAuthorizerClient externalAuthorizerClient) {
        this.userRepository = userRepository;
        this.externalAuthorizerClient = externalAuthorizerClient;
    }

    @Override
    public DepositDTO createDeposit(DepositDTO dto, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found"));

        validateDepositAmount(dto.getAmount());

        if (!externalAuthorizerClient.authorize()) {
            throw new DepositException("Error authorizing deposit");
        }

        user.setBalance(user.getBalance().add(dto.getAmount()));
        userRepository.save(user);
        return new DepositDTO(user);
    }

    private void validateDepositAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DepositException("Deposit amount must be greater than zero");
        }
    }
}
