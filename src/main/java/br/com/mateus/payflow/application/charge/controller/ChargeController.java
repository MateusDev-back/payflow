package br.com.mateus.payflow.application.charge.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.mateus.payflow.application.charge.dto.ChargeDTO;
import br.com.mateus.payflow.application.charge.service.ChargeService;
import br.com.mateus.payflow.domain.user.model.UserEntity;
import br.com.mateus.payflow.domain.user.repository.UserRepository;
import br.com.mateus.payflow.enums.charge.ChargeStatus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/payflow/v1/charges")
public class ChargeController {

    private final ChargeService chargeService;
    private final UserRepository userRepository;

    @Autowired
    public ChargeController(ChargeService chargeService, UserRepository userRepository) {
        this.chargeService = chargeService;
        this.userRepository = userRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<ChargeDTO> createCharge(@RequestBody ChargeDTO chargeDTO) {
        ChargeDTO charge = chargeService.createCharge(chargeDTO);
        return ResponseEntity.ok(charge);
    }

    @GetMapping("/sent")
    public ResponseEntity<List<ChargeDTO>> checkSentCharges(@RequestParam String cpf,
            @RequestParam(required = false) ChargeStatus status) {
        UserEntity payee = userRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("User with CPF " + cpf + " not found"));
        List<ChargeDTO> charges = chargeService.checkChargesSend(payee, status);
        return ResponseEntity.ok(charges);
    }

    @GetMapping("/received")
    public ResponseEntity<List<ChargeDTO>> checkReceivedCharges(@RequestParam String cpf,
            @RequestParam(required = false) ChargeStatus status) {
        UserEntity payer = userRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("User with CPF " + cpf + " not found"));
        List<ChargeDTO> charges = chargeService.checkChargesReceived(payer, status);
        return ResponseEntity.ok(charges);
    }

}
