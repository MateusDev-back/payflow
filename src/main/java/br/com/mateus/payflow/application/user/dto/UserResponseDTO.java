package br.com.mateus.payflow.application.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponseDTO {
    private String status;
    private String message;

    public UserResponseDTO() {
        this.message = getMessage();
        this.status = getStatus();
    }
}
