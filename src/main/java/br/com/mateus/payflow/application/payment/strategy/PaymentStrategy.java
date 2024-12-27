package br.com.mateus.payflow.application.payment.strategy;

import br.com.mateus.payflow.domain.charge.model.ChargeEntity;
import br.com.mateus.payflow.domain.user.model.UserEntity;

public interface PaymentStrategy {
    boolean pay(ChargeEntity charge, UserEntity payer);
}
