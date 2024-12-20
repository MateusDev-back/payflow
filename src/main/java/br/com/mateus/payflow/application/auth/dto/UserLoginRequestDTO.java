package br.com.mateus.payflow.application.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLoginRequestDTO {
    @NotBlank
    private String login;

    @NotBlank
    private String password;
}
