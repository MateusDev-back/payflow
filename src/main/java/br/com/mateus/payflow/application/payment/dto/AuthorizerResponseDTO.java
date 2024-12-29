package br.com.mateus.payflow.application.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AuthorizerResponseDTO {
    @JsonProperty("status")
    private String status;

    @JsonProperty("data")
    private AuthorizerData data;

    public boolean isAuthorized() {
        return "success".equalsIgnoreCase(status) && data.isAuthorized();
    }

    @Data
    public static class AuthorizerData {
        @JsonProperty("authorized")
        private boolean authorized;

    }
}
