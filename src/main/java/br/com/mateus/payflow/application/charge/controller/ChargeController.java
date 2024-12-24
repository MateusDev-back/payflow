package br.com.mateus.payflow.application.charge.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.mateus.payflow.application.charge.dto.ChargeDTO;
import br.com.mateus.payflow.application.charge.service.ChargeService;
import br.com.mateus.payflow.enums.charge.ChargeStatus;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/payflow/v1/users/charges")
public class ChargeController {

    private final ChargeService chargeService;

    @Autowired
    public ChargeController(ChargeService chargeService) {
        this.chargeService = chargeService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ChargeDTO> createCharge(@RequestBody ChargeDTO chargeDTO, HttpServletRequest request) {
        try {
            var userId = Long.parseLong(request.getAttribute("user_id").toString());
            ChargeDTO charge = chargeService.createCharge(chargeDTO, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(charge);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/sent")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ChargeDTO>> checkSentCharges(HttpServletRequest request,
            @RequestParam(required = false) ChargeStatus status) {
        try {
            var userId = Long.parseLong(request.getAttribute("user_id").toString());
            List<ChargeDTO> charges = chargeService.checkChargesSend(userId, status);
            return ResponseEntity.ok(charges);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/received")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ChargeDTO>> checkReceivedCharges(HttpServletRequest request,
            @RequestParam(required = false) ChargeStatus status) {
        try {
            var userId = Long.parseLong(request.getAttribute("user_id").toString());
            List<ChargeDTO> charges = chargeService.checkChargesReceived(userId, status);
            return ResponseEntity.ok(charges);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/pay")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> payCharge(@RequestParam String chargeId, HttpServletRequest request) {
        try {
            var userId = Long.parseLong(request.getAttribute("user_id").toString());
            chargeService.payCharge(chargeId, userId); // Passando o userId para validar o pagamento
            return ResponseEntity.status(HttpStatus.OK).body("Payment successful.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment failed: " + e.getMessage());
        }
    }
}
