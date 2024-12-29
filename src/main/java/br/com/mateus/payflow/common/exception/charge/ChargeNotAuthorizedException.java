package br.com.mateus.payflow.common.exception.charge;

public class ChargeNotAuthorizedException extends RuntimeException {
  public ChargeNotAuthorizedException() {
    super("You are not authorized to cancel this charge");
  }
}
