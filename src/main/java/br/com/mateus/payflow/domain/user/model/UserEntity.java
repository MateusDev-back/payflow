package br.com.mateus.payflow.domain.user.model;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CPF;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @CPF(message = "Invalid CPF")
    @Column(nullable = false, unique = true)
    private String cpf;

    @NotBlank
    @Email(message = "Invalid email")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Length(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;
}
