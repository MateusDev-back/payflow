package br.com.mateus.payflow.application.payment.factory;

import org.springframework.stereotype.Component;

import br.com.mateus.payflow.application.payment.integration.ExternalAuthorizerClient;
import br.com.mateus.payflow.application.payment.service.BalancePaymentStrategy;
import br.com.mateus.payflow.application.payment.service.CreditCardPaymentStrategy;
import br.com.mateus.payflow.application.payment.strategy.PaymentStrategy;
import br.com.mateus.payflow.enums.payment.PaymentMethod;

@Component
public class PaymentStrategyFactory {

    private final ExternalAuthorizerClient externalAuthorizerClient;

    public PaymentStrategyFactory(ExternalAuthorizerClient externalAuthorizerClient) {
        this.externalAuthorizerClient = externalAuthorizerClient;
    }

    public PaymentStrategy getPaymentStrategy(PaymentMethod method) {
        return switch (method) {
            case BALANCE -> new BalancePaymentStrategy();
            case CREDIT_CARD -> new CreditCardPaymentStrategy(externalAuthorizerClient);
            default -> throw new IllegalArgumentException("Método de pagamento inválido!");
        };
    }
}
