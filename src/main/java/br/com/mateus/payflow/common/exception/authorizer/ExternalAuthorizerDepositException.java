package br.com.mateus.payflow.common.exception.authorizer;

public class ExternalAuthorizerDepositException extends RuntimeException {
    public ExternalAuthorizerDepositException() {
        super("Error authorizing deposit");
    }
}
