package br.com.mateus.payflow.application.charge.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.mateus.payflow.application.charge.dto.ChargeDTO;
import br.com.mateus.payflow.domain.charge.model.ChargeEntity;
import br.com.mateus.payflow.domain.user.model.UserEntity;
import br.com.mateus.payflow.domain.user.repository.UserRepository;
import br.com.mateus.payflow.enums.charge.ChargeStatus;

@Service
public class ChargeCreationService {

    private final UserRepository userRepository;

    @Autowired
    public ChargeCreationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ChargeEntity createCharge(ChargeDTO dto, String cpf) {
        UserEntity payee = userRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Payee not found"));

        dto.setPayee(payee.getCpf());

        UserEntity payer = userRepository.findByCpf(dto.getPayer())
                .orElseThrow(() -> new RuntimeException("Payer not found"));

        if (payee.getCpf().equals(payer.getCpf())) {
            throw new RuntimeException("User cannot create a charge for themselves");
        }

        validateChargeAmount(dto.getAmount());

        return buildCharge(dto, payee, payer);
    }

    private void validateChargeAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Charge amount must be greater than zero");
        }
    }

    private ChargeEntity buildCharge(ChargeDTO dto, UserEntity payee, UserEntity payer) {
        ChargeEntity charge = new ChargeEntity();
        charge.setPayee(payee);
        charge.setPayer(payer);
        charge.setAmount(dto.getAmount());
        charge.setDescription(dto.getDescription());
        charge.setStatus(ChargeStatus.PENDING);
        return charge;
    }
}
