package br.com.mateus.payflow.application.deposit.controller;

import br.com.mateus.payflow.application.deposit.dto.DepositDTO;
import br.com.mateus.payflow.application.deposit.dto.DepositResponseDTO;
import br.com.mateus.payflow.application.deposit.service.DepositService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payflow/v1/users/deposits")
public class DepositController {

    private final DepositService depositService;

    public DepositController(DepositService depositService) {
        this.depositService = depositService;
    }

    @PostMapping
    public ResponseEntity<DepositResponseDTO> createDeposit(
            @RequestBody DepositDTO dto,
            HttpServletRequest request) {

        try {
            String userId = (String) request.getAttribute("user_id");
            depositService.createDeposit(dto, Long.parseLong(userId));
            DepositResponseDTO response = new DepositResponseDTO("success", "Deposit created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            DepositResponseDTO response = new DepositResponseDTO("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
