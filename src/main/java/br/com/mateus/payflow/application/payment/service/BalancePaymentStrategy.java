package br.com.mateus.payflow.application.payment.service;

import org.springframework.stereotype.Service;
import br.com.mateus.payflow.application.payment.strategy.PaymentStrategy;
import br.com.mateus.payflow.domain.charge.model.ChargeEntity;
import br.com.mateus.payflow.domain.user.model.UserEntity;
import br.com.mateus.payflow.enums.charge.ChargeStatus;

@Service
public class BalancePaymentStrategy implements PaymentStrategy {

    @Override
    public boolean pay(ChargeEntity charge, UserEntity payer) {
        if (payer.getBalance().compareTo(charge.getAmount()) >= 0) {
            payer.setBalance(payer.getBalance().subtract(charge.getAmount()));
            charge.getPayee().setBalance(charge.getPayee().getBalance().add(charge.getAmount()));
            charge.setStatus(ChargeStatus.PAID);
            return true;
        }
        throw new RuntimeException("Insufficient balance");
    }
}
