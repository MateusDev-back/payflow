package br.com.mateus.payflow.presetantion;

import java.util.List;

import br.com.mateus.payflow.application.charge.dto.ChargeCancelationResponseDTO;
import br.com.mateus.payflow.application.charge.dto.ChargeCancelationRequestDTO;
import br.com.mateus.payflow.application.charge.dto.ChargeCreateResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import br.com.mateus.payflow.application.charge.dto.ChargeDTO;
import br.com.mateus.payflow.application.charge.service.ChargeService;
import br.com.mateus.payflow.enums.charge.ChargeStatus;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/payflow/v1/users/charges")
public class ChargeController {

    private static final String SUCCESS = "success";
    private static final String ERROR = "error";
    private static final String CHARGE_CREATED_SUCCESSFULLY = "Charge created successfully";
    private static final String CHARGE_CANCELED_SUCCESSFULLY = "Charge canceled successfully";
    private static final String USER_ID = "user_id";

    private final ChargeService chargeService;

    @Autowired
    public ChargeController(ChargeService chargeService) {
        this.chargeService = chargeService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ChargeCreateResponseDTO> createCharge(@Valid @RequestBody ChargeDTO chargeDTO, HttpServletRequest request) {
        try {
            String userId = (String) request.getAttribute(USER_ID);
            chargeService.createCharge(chargeDTO, Long.parseLong(userId));
            ChargeCreateResponseDTO response = new ChargeCreateResponseDTO(SUCCESS, CHARGE_CREATED_SUCCESSFULLY);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ChargeCreateResponseDTO response = new ChargeCreateResponseDTO( ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ChargeCancelationResponseDTO> cancelCharge(@Valid @RequestBody ChargeCancelationRequestDTO request, HttpServletRequest httpRequest) {
        try {
            String userId = (String) httpRequest.getAttribute(USER_ID);
            chargeService.cancelCharge(request.getChargeId(), Long.parseLong(userId));
            ChargeCancelationResponseDTO response = new ChargeCancelationResponseDTO(SUCCESS, CHARGE_CANCELED_SUCCESSFULLY);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ChargeCancelationResponseDTO response = new ChargeCancelationResponseDTO(ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


    @GetMapping("/sent")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ChargeDTO>> checkSentCharges(HttpServletRequest request,
            @RequestParam(required = false) ChargeStatus status) {
        try {
            var userId = Long.parseLong(request.getAttribute(USER_ID).toString());
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
            var userId = Long.parseLong(request.getAttribute(USER_ID).toString());
            List<ChargeDTO> charges = chargeService.checkChargesReceived(userId, status);
            return ResponseEntity.ok(charges);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{chargeId}")
    public ResponseEntity<ChargeDTO> getCharge(@PathVariable String chargeId) {
        try {
            ChargeDTO charge = chargeService.getDetails(chargeId);
            return ResponseEntity.ok(charge);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}