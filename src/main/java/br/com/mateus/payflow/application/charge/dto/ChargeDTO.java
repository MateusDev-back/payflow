package br.com.mateus.payflow.application.charge.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.mateus.payflow.domain.charge.model.ChargeEntity;
import br.com.mateus.payflow.enums.charge.ChargeStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChargeDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("payerCpf")
    @NotBlank
    private String payerCpf;

    @JsonProperty("amount")
    @NotNull
    private BigDecimal amount;

    private String description;

    private LocalDateTime paymentDate;

    @JsonProperty("status")
    private ChargeStatus status;

    @JsonProperty("paymentMethod")
    private String paymentMethod;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @JsonCreator
    public ChargeDTO(
            @JsonProperty("payerCpf") String payerCpf,
            @JsonProperty("amount") BigDecimal amount,
            @JsonProperty("description") String description) {
        this.payerCpf = payerCpf;
        this.amount = amount;
        this.description = description;
    }

    public ChargeDTO(ChargeEntity charge) {
        this.id = charge.getId();
        this.payerCpf = charge.getPayer().getCpf();
        this.amount = charge.getAmount();
        if (charge.getPaymentDate() != null) {
            this.paymentDate = charge.getPaymentDate();
        }
        this.description = charge.getDescription();
        this.status = charge.getStatus();
        if (charge.getPaymentMethod() != null) {
            this.paymentMethod = charge.getPaymentMethod().name();
        }
        this.createdAt = charge.getCreatedAt();
        this.updatedAt = charge.getUpdatedAt();
    }

}
