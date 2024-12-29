package br.com.mateus.payflow.common.exception.authorizer;

public class ExternalAuthorizerException extends RuntimeException {
    public ExternalAuthorizerException() {
        super("Error communicating with external authorizer");
    }
}
