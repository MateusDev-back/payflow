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
public class ChargeServiceImpl implements ChargeService {

    private final ChargeRepository chargeRepository;
    private final ChargeCreationService chargeCreationService;
    private final UserRepository userRepository;

    @Autowired
    public ChargeServiceImpl(ChargeRepository chargeRepository,
            ChargeCreationService chargeCreationService,
            UserRepository userRepository) {
        this.chargeRepository = chargeRepository;
        this.chargeCreationService = chargeCreationService;
        this.userRepository = userRepository;
    }

    @Override
    public ChargeDTO createCharge(ChargeDTO chargeDTO, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChargeEntity charge = chargeCreationService.createCharge(chargeDTO, user.getCpf());
        ChargeEntity savedCharge = chargeRepository.save(charge);
        return new ChargeDTO(savedCharge);
    }

    @Override
    public List<ChargeDTO> checkChargesSend(Long userId, ChargeStatus status) {
        UserEntity payee = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ChargeEntity> charges = (status == null)
                ? chargeRepository.findByPayee(payee)
                : chargeRepository.findByPayeeAndStatus(payee, status);

        return mapToDTOList(charges);
    }

    @Override
    public List<ChargeDTO> checkChargesReceived(Long userId, ChargeStatus status) {
        UserEntity payer = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ChargeEntity> charges = (status == null)
                ? chargeRepository.findByPayer(payer)
                : chargeRepository.findByPayerAndStatus(payer, status);

        return mapToDTOList(charges);
    }

    @Override
    public ChargeDTO getDetails(String chargeId) {
        ChargeEntity charge = chargeRepository.findById(chargeId)
                .orElseThrow(() -> new RuntimeException("Charge not found"));

        return new ChargeDTO(charge);
    }

    private List<ChargeDTO> mapToDTOList(List<ChargeEntity> chargeEntities) {
        return chargeEntities.stream()
                .map(ChargeDTO::new)
                .toList();
    }

}
