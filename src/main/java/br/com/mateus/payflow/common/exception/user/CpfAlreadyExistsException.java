package br.com.mateus.payflow.common.exception.user;

public class CpfAlreadyExistsException extends RuntimeException {
    public CpfAlreadyExistsException() {
        super("CPF already exists");
    }
}
