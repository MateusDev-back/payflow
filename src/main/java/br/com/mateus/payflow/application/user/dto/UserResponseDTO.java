package br.com.mateus.payflow.application.user.dto;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String name;
    private String cpf;
    private String email;
    private String password;
}
