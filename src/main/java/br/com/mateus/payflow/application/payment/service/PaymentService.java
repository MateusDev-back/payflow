package br.com.mateus.payflow.application.payment.service;

import java.time.LocalDateTime;

import br.com.mateus.payflow.common.exception.charge.ChargeNotFoundException;
import br.com.mateus.payflow.common.exception.charge.ChargeStatusException;
import org.springframework.stereotype.Service;

import br.com.mateus.payflow.application.payment.factory.PaymentStrategyFactory;
import br.com.mateus.payflow.application.payment.strategy.PaymentStrategy;
import br.com.mateus.payflow.domain.charge.model.ChargeEntity;
import br.com.mateus.payflow.domain.charge.repository.ChargeRepository;
import br.com.mateus.payflow.domain.user.repository.UserRepository;
import br.com.mateus.payflow.enums.charge.ChargeStatus;
import br.com.mateus.payflow.enums.payment.PaymentMethod;
import jakarta.transaction.Transactional;

@Service
public class PaymentService {

    private final UserRepository userRepository;
    private final ChargeRepository chargeRepository;
    private final PaymentStrategyFactory paymentStrategyFactory;

    public PaymentService(UserRepository userRepository, ChargeRepository chargeRepository,
            PaymentStrategyFactory paymentStrategyFactory) {
        this.userRepository = userRepository;
        this.chargeRepository = chargeRepository;
        this.paymentStrategyFactory = paymentStrategyFactory;
    }

    @Transactional
    public void payCharge(String chargeId, PaymentMethod method) {
        ChargeEntity charge = chargeRepository.findById(chargeId)
                .orElseThrow(ChargeNotFoundException::new);

        if (charge.getStatus() == ChargeStatus.PAID) {
            throw new ChargeStatusException("Charge already paid");
        }

        if (charge.getStatus() == ChargeStatus.CANCELED) {
            throw new ChargeStatusException("Charge already canceled");
        }

        PaymentStrategy strategy = paymentStrategyFactory.getPaymentStrategy(method);
        if (strategy.pay(charge, charge.getPayer())) {
            userRepository.save(charge.getPayer());
            userRepository.save(charge.getPayee());
            charge.setStatus(ChargeStatus.PAID);
            charge.setPaymentMethod(PaymentMethod.valueOf(method.name()));
            charge.setPaymentDate(LocalDateTime.now());
            charge.setUpdatedAt(LocalDateTime.now());
            chargeRepository.save(charge);
        }
    }
}
