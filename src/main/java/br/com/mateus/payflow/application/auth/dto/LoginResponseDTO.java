package br.com.mateus.payflow.application.auth.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String token;
    private String message;

    public LoginResponseDTO(String token, String message) {
        this.token = token;
        this.message = message;
    }
}
