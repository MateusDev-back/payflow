package br.com.mateus.payflow.application.charge.dto;

import lombok.Data;

@Data
public class ChargeCancelationResponseDTO {
    private String status;
    private String message;

    public ChargeCancelationResponseDTO(String status, String message) {
        this.status = status;
        this.message = message;
    }
}
