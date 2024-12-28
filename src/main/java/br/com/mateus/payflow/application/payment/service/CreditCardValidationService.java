package br.com.mateus.payflow.application.payment.service;

import br.com.mateus.payflow.application.payment.dto.CreditCardDTO;
import br.com.mateus.payflow.common.exception.payment.CreditCardValidationException;
import org.springframework.stereotype.Service;

@Service
public class CreditCardValidationService {

    public void validate(CreditCardDTO creditCard) {
        if (creditCard == null) {
            throw new CreditCardValidationException("Credit card information is required.");
        }

        if (creditCard.getCardNumber() == null || creditCard.getCardNumber().isEmpty()) {
            throw new CreditCardValidationException("Card number is required.");
        }

        if (creditCard.getCardNumber().length() != 16) {
            throw new CreditCardValidationException("Card number must be exactly 16 digits.");
        }

        if (creditCard.getCardExpirationDate() == null || creditCard.getCardExpirationDate().isEmpty()) {
            throw new CreditCardValidationException("Card expiration date is required.");
        }

        if (creditCard.getCardExpirationDate().length() != 7) {
            throw new CreditCardValidationException("Card expiration date must be exactly 7 characters (MM/YYYY).");
        }

        if (creditCard.getCardCVV() == null || creditCard.getCardCVV().isEmpty()) {
            throw new CreditCardValidationException("CVV is required.");
        }

        if (creditCard.getCardCVV().length() != 3) {
            throw new CreditCardValidationException("CVV must be exactly 3 digits.");
        }
    }
}
