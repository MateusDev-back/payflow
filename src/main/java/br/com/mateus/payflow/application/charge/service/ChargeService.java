package br.com.mateus.payflow.application.charge.service;

import java.util.List;

import br.com.mateus.payflow.application.charge.dto.ChargeDTO;
import br.com.mateus.payflow.enums.charge.ChargeStatus;

public interface ChargeService {
    ChargeDTO createCharge(ChargeDTO chargeDTO, Long userId);

    List<ChargeDTO> checkChargesSend(Long userId, ChargeStatus status);

    List<ChargeDTO> checkChargesReceived(Long userId, ChargeStatus status);

    ChargeDTO getDetails(String chargeId);

}
