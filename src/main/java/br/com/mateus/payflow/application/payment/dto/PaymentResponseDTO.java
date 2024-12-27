package br.com.mateus.payflow.application.payment.dto;

import lombok.Data;

@Data
public class PaymentResponseDTO {
    private String status;
    private String message;

    public PaymentResponseDTO(String status, String message) {
        this.status = status;
        this.message = message;
    }
}
