package br.com.mateus.payflow.application.deposit.dto;

import lombok.Data;

@Data
public class DepositResponseDTO {
    private String status;
    private String message;

    public DepositResponseDTO(String status, String message) {
        this.status = status;
        this.message = message;
    }
}
