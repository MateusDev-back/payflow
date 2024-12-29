package br.com.mateus.payflow.application.payment.service;

import br.com.mateus.payflow.common.exception.authorizer.ExternalAuthorizerPaymentException;
import org.springframework.stereotype.Service;

import br.com.mateus.payflow.application.payment.integration.ExternalAuthorizerClient;
import br.com.mateus.payflow.application.payment.strategy.PaymentStrategy;
import br.com.mateus.payflow.domain.charge.model.ChargeEntity;
import br.com.mateus.payflow.domain.user.model.UserEntity;

@Service
public class CreditCardPaymentStrategy implements PaymentStrategy {

    private final ExternalAuthorizerClient externalAuthorizerClient;

    public CreditCardPaymentStrategy(ExternalAuthorizerClient externalAuthorizerClient) {
        this.externalAuthorizerClient = externalAuthorizerClient;
    }

    @Override
    public boolean pay(ChargeEntity charge, UserEntity payee) {
        boolean authorized = externalAuthorizerClient.authorize();
        if (authorized) {
            charge.getPayee().setBalance(charge.getPayee().getBalance().add(charge.getAmount()));
            return true;
        }
        throw new ExternalAuthorizerPaymentException();
    }

}
