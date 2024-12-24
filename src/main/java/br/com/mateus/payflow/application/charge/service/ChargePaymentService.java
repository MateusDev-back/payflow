package br.com.mateus.payflow.application.charge.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.mateus.payflow.application.balance.service.BalanceService;
import br.com.mateus.payflow.domain.charge.model.ChargeEntity;
import br.com.mateus.payflow.domain.charge.repository.ChargeRepository;
import br.com.mateus.payflow.enums.charge.ChargeStatus;
import jakarta.transaction.Transactional;

@Service
public class ChargePaymentService {

    private final BalanceService balanceService;
    private final ChargeRepository chargeRepository;

    @Autowired
    public ChargePaymentService(BalanceService balanceService, ChargeRepository chargeRepository) {
        this.balanceService = balanceService;
        this.chargeRepository = chargeRepository;
    }

    @Transactional
    public void payCharge(String chargeId, Long userId) {
        ChargeEntity charge = chargeRepository.findById(chargeId)
                .orElseThrow(() -> new RuntimeException("Charge not found"));

        if (!charge.getPayer().getId().equals(userId)) {
            throw new RuntimeException("You are not the payer of this charge");
        }

        if (charge.getStatus() == ChargeStatus.PAID) {
            throw new RuntimeException("Charge already paid");
        }

        balanceService.debit(charge.getPayer(), charge.getAmount());
        balanceService.credit(charge.getPayee(), charge.getAmount());

        charge.setStatus(ChargeStatus.PAID);
        charge.setPaymentDate(LocalDateTime.now());
        chargeRepository.save(charge);
    }
}
