package br.com.mateus.payflow.application.payment.dto;

import br.com.mateus.payflow.enums.payment.PaymentMethod;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequestDTO {

    @NotBlank(message = "The charge ID cannot be empty.")
    private String chargeId;

    @NotNull(message = "The payment method cannot be null.")
    private PaymentMethod paymentMethod;

    private CreditCardDTO creditCard;
}
