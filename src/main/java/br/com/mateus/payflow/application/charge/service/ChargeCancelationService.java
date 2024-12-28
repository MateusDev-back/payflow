package br.com.mateus.payflow.application.charge.service;

import br.com.mateus.payflow.application.balance.service.BalanceService;
import br.com.mateus.payflow.application.charge.dto.ChargeDTO;
import br.com.mateus.payflow.application.payment.integration.ExternalAuthorizerClient;
import br.com.mateus.payflow.domain.charge.model.ChargeEntity;
import br.com.mateus.payflow.domain.charge.repository.ChargeRepository;
import br.com.mateus.payflow.enums.charge.ChargeStatus;
import br.com.mateus.payflow.enums.payment.PaymentMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChargeCancelationService {

    private final ExternalAuthorizerClient externalAuthorizerClient;
    private final BalanceService balanceService;
    private final ChargeRepository chargeRepository;

    @Autowired
    public ChargeCancelationService(ExternalAuthorizerClient externalAuthorizerClient, BalanceService balanceService, ChargeRepository chargeRepository) {
        this.externalAuthorizerClient = externalAuthorizerClient;
        this.balanceService = balanceService;
        this.chargeRepository = chargeRepository;
    }

    public ChargeDTO cancelCharge(String chargeId, Long userId) {
        ChargeEntity charge = chargeRepository.findById(chargeId)
                .orElseThrow(() -> new RuntimeException("Charge not found"));

        if (charge.getPayer().getId().equals(userId)) {
            throw new RuntimeException("You are not authorized to cancel this charge");
        }

        if (ChargeStatus.CANCELED == charge.getStatus()) {
            throw new RuntimeException("Charge already canceled");
        }

        switch (charge.getStatus()) {
            case PENDING:
                cancelPendingCharge(charge);
                break;
            case PAID:
                cancelPaidCharges(charge);
                break;
            default:
                throw new RuntimeException("Charge cannot be canceled");
        }
        charge.setUpdatedAt(LocalDateTime.now());
        chargeRepository.save(charge);
        return new ChargeDTO(charge);
    }

    public void cancelPendingCharge(ChargeEntity charge) {
        charge.setStatus(ChargeStatus.CANCELED);
    }

    public void cancelPaidCharges(ChargeEntity charge) {
        if (charge.getPaymentMethod().equals(PaymentMethod.BALANCE)) {
            cancelChargePaidBalance(charge);
        }else if (charge.getPaymentMethod().equals(PaymentMethod.CREDIT_CARD)) {
            cancelChargePaidCreditCard(charge);
        }
        charge.setStatus(ChargeStatus.CANCELED);
    }

    public void cancelChargePaidBalance(ChargeEntity charge) {
       try {
           balanceService.debit(charge.getPayer(), charge.getAmount());
           balanceService.credit(charge.getPayee(), charge.getAmount());
       } catch (Exception ex) {
           throw new RuntimeException("Error canceling charge");
       }
    }

    public void cancelChargePaidCreditCard(ChargeEntity charge) {
        if (!externalAuthorizerClient.authorize()) {
            throw new RuntimeException("Error authorizing cancellation");
        }
        balanceService.debit(charge.getPayee(), charge.getAmount());
        balanceService.credit(charge.getPayer(), charge.getAmount());
    }
}
