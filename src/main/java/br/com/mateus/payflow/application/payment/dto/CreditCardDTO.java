package br.com.mateus.payflow.application.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreditCardDTO {
    @NotBlank(message = "The card number cannot be empty.")
    private String cardNumber;
    @NotBlank(message = "The card expiration date cannot be empty.")
    private String cardExpirationDate;
    @NotBlank(message = "The CVV cannot be empty.")
    private String cardCVV;
}
