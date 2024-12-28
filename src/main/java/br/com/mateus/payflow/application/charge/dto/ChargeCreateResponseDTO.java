package br.com.mateus.payflow.application.charge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChargeCreateResponseDTO {
    private String status;
    private String message;

}
