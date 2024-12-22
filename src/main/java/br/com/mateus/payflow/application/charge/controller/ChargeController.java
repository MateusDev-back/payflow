package br.com.mateus.payflow.application.charge.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.mateus.payflow.application.charge.dto.ChargeDTO;
import br.com.mateus.payflow.application.charge.service.ChargeService;
import br.com.mateus.payflow.domain.user.repository.UserRepository;
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
    public ChargeController(ChargeService chargeService, UserRepository userRepository) {
        this.chargeService = chargeService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ChargeDTO> createCharge(@RequestBody ChargeDTO chargeDTO, HttpServletRequest request) {
        try {
            var userId = Long.parseLong(request.getAttribute("user_id").toString());
            ChargeDTO charge = chargeService.createCharge(chargeDTO, userId);
            return ResponseEntity.ok(charge);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
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
            return ResponseEntity.badRequest().build();
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
            return ResponseEntity.badRequest().build();
        }
    }

}
