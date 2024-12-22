package br.com.mateus.payflow.application.charge.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.mateus.payflow.domain.charge.model.ChargeEntity;
import br.com.mateus.payflow.enums.charge.ChargeStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChargeDTO {

    @JsonProperty("id")
    private String id;

    @NotBlank
    @JsonProperty("payee")
    private String payee;

    @NotBlank
    @JsonProperty("payer")
    private String payer;

    @NotNull
    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("description")
    private String description;

    @JsonProperty("status")
    private ChargeStatus status;

    @JsonCreator
    public ChargeDTO(@JsonProperty("payee") String payee,
            @JsonProperty("payer") String payer,
            @JsonProperty("amount") Double amount,
            @JsonProperty("description") String description,
            @JsonProperty("status") ChargeStatus status) {
        this.payee = payee;
        this.payer = payer;
        this.amount = amount;
        this.description = description;
        this.status = status;
    }

    public ChargeDTO(ChargeEntity charge) {
        this.id = charge.getId();
        this.payee = charge.getPayee().getCpf();
        this.payer = charge.getPayer().getCpf();
        this.amount = charge.getAmount();
        this.description = charge.getDescription();
        this.status = charge.getStatus();
    }

}
