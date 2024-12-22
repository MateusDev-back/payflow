package br.com.mateus.payflow.application.charge.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.mateus.payflow.application.charge.dto.ChargeDTO;
import br.com.mateus.payflow.domain.charge.model.ChargeEntity;
import br.com.mateus.payflow.domain.charge.repository.ChargeRepository;
import br.com.mateus.payflow.domain.user.model.UserEntity;
import br.com.mateus.payflow.domain.user.repository.UserRepository;
import br.com.mateus.payflow.enums.charge.ChargeStatus;

@Service
public class ChargeService {

    private final ChargeRepository chargeRepository;
    private final UserRepository userRepository;

    @Autowired
    public ChargeService(ChargeRepository chargeRepository, UserRepository userRepository) {
        this.chargeRepository = chargeRepository;
        this.userRepository = userRepository;
    }

    public ChargeDTO createCharge(ChargeDTO chargeDTO) {
        UserEntity payee = userRepository.findByCpf(chargeDTO.getPayee())
                .orElseThrow(() -> new RuntimeException("Payee not found"));

        UserEntity payer = userRepository.findByCpf(chargeDTO.getPayer())
                .orElseThrow(() -> new RuntimeException("Payer not found"));

        validateChargeAmount(chargeDTO.getAmount());

        ChargeEntity chargeEntity = buildChargeEntity(chargeDTO, payee, payer);

        chargeEntity = chargeRepository.save(chargeEntity);

        chargeDTO.setId(chargeEntity.getId());

        return new ChargeDTO(chargeEntity);
    }

    public List<ChargeDTO> checkChargesSend(UserEntity payee, ChargeStatus status) {
        List<ChargeEntity> charges = (status == null)
                ? chargeRepository.findByPayee(payee)
                : chargeRepository.findByPayeeAndStatus(payee, status);

        return mapToDTOList(charges);
    }

    public List<ChargeDTO> checkChargesReceived(UserEntity payer, ChargeStatus status) {
        List<ChargeEntity> charges = (status == null)
                ? chargeRepository.findByPayer(payer)
                : chargeRepository.findByPayerAndStatus(payer, status);

        return mapToDTOList(charges);
    }

    private void validateChargeAmount(double amount) {
        if (amount <= 0) {
            throw new RuntimeException("Amount must be greater than 0");
        }
    }

    private ChargeEntity buildChargeEntity(ChargeDTO chargeDTO, UserEntity payee, UserEntity payer) {
        ChargeEntity chargeEntity = new ChargeEntity();
        chargeEntity.setPayee(payee);
        chargeEntity.setPayer(payer);
        chargeEntity.setAmount(chargeDTO.getAmount());
        chargeEntity.setDescription(chargeDTO.getDescription());
        chargeEntity.setStatus(ChargeStatus.PENDING);
        return chargeEntity;
    }

    private List<ChargeDTO> mapToDTOList(List<ChargeEntity> chargeEntities) {
        return chargeEntities.stream()
                .map(ChargeDTO::new)
                .toList();
    }
}
