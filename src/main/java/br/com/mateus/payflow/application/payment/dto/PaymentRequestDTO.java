package br.com.mateus.payflow.application.payment.dto;

import br.com.mateus.payflow.enums.payment.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentRequestDTO {

    @NotBlank(message = "O id da cobrança não pode ser vazio.")
    private String chargeId;

    private PaymentMethod paymentMethod;
}
