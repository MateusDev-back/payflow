package br.com.mateus.payflow.application.payment.controller;

import br.com.mateus.payflow.common.exception.CreditCardValidationException;
import br.com.mateus.payflow.enums.payment.PaymentMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.mateus.payflow.application.payment.dto.PaymentRequestDTO;
import br.com.mateus.payflow.application.payment.dto.PaymentResponseDTO;
import br.com.mateus.payflow.application.payment.service.PaymentService;
import br.com.mateus.payflow.application.payment.service.CreditCardValidationService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/payflow/v1/users/payments")
@Validated
public class PaymentController {

    private final PaymentService paymentService;
    private final CreditCardValidationService creditCardValidationService;

    public PaymentController(PaymentService paymentService, CreditCardValidationService creditCardValidationService) {
        this.paymentService = paymentService;
        this.creditCardValidationService = creditCardValidationService;
    }

    @PostMapping("/payment_charge")
    public ResponseEntity<PaymentResponseDTO> pay(@Valid @RequestBody PaymentRequestDTO paymentRequestDTO) {
        try {
            if (paymentRequestDTO.getPaymentMethod() == PaymentMethod.CREDIT_CARD) {
                creditCardValidationService.validate(paymentRequestDTO.getCreditCard());
            }

            paymentService.payCharge(paymentRequestDTO.getChargeId(), paymentRequestDTO.getPaymentMethod());

            PaymentResponseDTO response = new PaymentResponseDTO("success", "Payment processed successfully");
            return ResponseEntity.ok(response);
        } catch (CreditCardValidationException e) {
            PaymentResponseDTO response = new PaymentResponseDTO("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            PaymentResponseDTO response = new PaymentResponseDTO("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
