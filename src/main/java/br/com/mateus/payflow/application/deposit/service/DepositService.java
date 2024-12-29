package br.com.mateus.payflow.application.deposit.service;

import br.com.mateus.payflow.application.deposit.dto.DepositDTO;

public interface DepositService {
    DepositDTO createDeposit(DepositDTO dto, Long userId);
}
