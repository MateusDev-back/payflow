package br.com.mateus.payflow.common.exception.authorizer;

public class ExternalAuthorizerPaymentException extends RuntimeException {
    public ExternalAuthorizerPaymentException() {
        super("Payment not authorized");
    }
}
