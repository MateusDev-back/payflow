package br.com.mateus.payflow.application.payment.factory;

import org.springframework.stereotype.Component;

import br.com.mateus.payflow.infrastructure.HttpExternalAuthorizerClient;
import br.com.mateus.payflow.application.payment.service.BalancePaymentStrategy;
import br.com.mateus.payflow.application.payment.service.CreditCardPaymentStrategy;
import br.com.mateus.payflow.application.payment.strategy.PaymentStrategy;
import br.com.mateus.payflow.enums.payment.PaymentMethod;

@Component
public class PaymentStrategyFactory {

    private final HttpExternalAuthorizerClient httpExternalAuthorizerClient;

    public PaymentStrategyFactory(HttpExternalAuthorizerClient httpExternalAuthorizerClient) {
        this.httpExternalAuthorizerClient = httpExternalAuthorizerClient;
    }

    public PaymentStrategy getPaymentStrategy(PaymentMethod method) {
        return switch (method) {
            case BALANCE -> new BalancePaymentStrategy();
            case CREDIT_CARD -> new CreditCardPaymentStrategy(httpExternalAuthorizerClient);
            default -> throw new IllegalArgumentException("Método de pagamento inválido!");
        };
    }
}
