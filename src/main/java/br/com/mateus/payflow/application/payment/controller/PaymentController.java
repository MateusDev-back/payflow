package br.com.mateus.payflow.application.payment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.mateus.payflow.application.payment.dto.PaymentRequestDTO;
import br.com.mateus.payflow.application.payment.dto.PaymentResponseDTO;
import br.com.mateus.payflow.application.payment.service.PaymentService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/payflow/v1/users/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("payment_charge")
    public ResponseEntity<PaymentResponseDTO> pay(@Valid @RequestBody PaymentRequestDTO paymentRequestDTO) {
        try {
            paymentService.payCharge(paymentRequestDTO.getChargeId(), paymentRequestDTO.getPaymentMethod());
            PaymentResponseDTO response = new PaymentResponseDTO("success", "Pagamento realizado com sucesso!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            PaymentResponseDTO response = new PaymentResponseDTO("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
