package br.com.mateus.payflow.application.payment.integration;

import br.com.mateus.payflow.common.exception.authorizer.ExternalAuthorizerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import br.com.mateus.payflow.application.payment.dto.AuthorizerResponseDTO;

@Component
public class ExternalAuthorizerClient {

    @Value("${external.authorizer.url}")
    private String authorizer;

    public boolean authorize() {
        RestTemplate restTemplate = new RestTemplate();
        try {
            AuthorizerResponseDTO response = restTemplate.getForObject(authorizer, AuthorizerResponseDTO.class);
            return response != null && response.isAuthorized();
        } catch (HttpClientErrorException e) {
            throw new ExternalAuthorizerException();
        }
    }
}
