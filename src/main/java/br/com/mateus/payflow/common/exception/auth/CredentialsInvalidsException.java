package br.com.mateus.payflow.common.exception.auth;

public class CredentialsInvalidsException extends RuntimeException {
    public CredentialsInvalidsException() {
        super("Credentials invalids");
    }
}
