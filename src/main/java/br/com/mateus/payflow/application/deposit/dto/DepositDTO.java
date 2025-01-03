package br.com.mateus.payflow.application.deposit.dto;

import br.com.mateus.payflow.domain.user.model.UserEntity;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositDTO {

    @NotNull(message = "The amount cannot be empty.")
    private BigDecimal amount;


    public DepositDTO(UserEntity user) {
        this.amount = user.getBalance();
    }
}
