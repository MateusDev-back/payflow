package br.com.mateus.payflow.application.charge.service;

import br.com.mateus.payflow.application.charge.dto.ChargeDTO;
import br.com.mateus.payflow.common.exception.charge.InvalidChargeAmountException;
import br.com.mateus.payflow.common.exception.user.PayerNotFoundException;
import br.com.mateus.payflow.common.exception.user.UserException;
import br.com.mateus.payflow.common.exception.user.UserNotFoundException;
import br.com.mateus.payflow.domain.user.model.UserEntity;
import br.com.mateus.payflow.domain.user.repository.UserRepository;
import br.com.mateus.payflow.domain.charge.model.ChargeEntity;
import br.com.mateus.payflow.enums.charge.ChargeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ChargeCreationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChargeCreationService chargeCreationService;

    private UserEntity payee;
    private UserEntity payer;
    private ChargeDTO chargeDTO;

    @BeforeEach
    void setUp() {
        payee = new UserEntity();
        payee.setId(1L);
        payee.setEmail("payee@example.com");
        payee.setCpf("12345678900");

        payer = new UserEntity();
        payer.setId(2L);
        payer.setEmail("payer@example.com");
        payer.setCpf("98765432100");

        chargeDTO = new ChargeDTO();
        chargeDTO.setAmount(BigDecimal.valueOf(100));
        chargeDTO.setDescription("Payment for service");
        chargeDTO.setPayerCpf(payer.getCpf());
    }

    @Test
    void testCreateCharge_whenValidData_shouldCreateCharge() {
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(payee));
        when(userRepository.findByCpf(payer.getCpf())).thenReturn(java.util.Optional.of(payer));

        ChargeEntity charge = chargeCreationService.createCharge(chargeDTO, 1L);

        assertNotNull(charge);
        assertEquals(payee, charge.getPayee());
        assertEquals(payer, charge.getPayer());
        assertEquals(chargeDTO.getAmount(), charge.getAmount());
        assertEquals(chargeDTO.getDescription(), charge.getDescription());
        assertEquals(ChargeStatus.PENDING, charge.getStatus());

        verify(userRepository).findById(1L);
        verify(userRepository).findByCpf(payer.getCpf());
    }

    @Test
    void testCreateCharge_whenPayerNotFound_shouldThrowPayerNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(payee));
        when(userRepository.findByCpf(payer.getCpf())).thenReturn(java.util.Optional.empty());

        assertThrows(PayerNotFoundException.class, () -> chargeCreationService.createCharge(chargeDTO, 1L));
    }

    @Test
    void testCreateCharge_whenUserCannotCreateChargeForThemselves_shouldThrowUserException() {
        chargeDTO.setPayerCpf(payee.getCpf());

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(payee));
        when(userRepository.findByCpf(payee.getCpf())).thenReturn(java.util.Optional.of(payee));

        assertThrows(UserException.class, () -> chargeCreationService.createCharge(chargeDTO, 1L));
    }

    @Test
    void testCreateCharge_whenChargeAmountIsZeroOrNegative_shouldThrowInvalidChargeAmountException() {
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(payee));
        when(userRepository.findByCpf(payer.getCpf())).thenReturn(java.util.Optional.of(payer));

        chargeDTO.setAmount(BigDecimal.valueOf(0));
        InvalidChargeAmountException exception = assertThrows(InvalidChargeAmountException.class,
                () -> chargeCreationService.createCharge(chargeDTO, 1L));
        assertEquals("Charge amount must be greater than zero", exception.getMessage());

        chargeDTO.setAmount(BigDecimal.valueOf(-1));
        exception = assertThrows(InvalidChargeAmountException.class,
                () -> chargeCreationService.createCharge(chargeDTO, 1L));
        assertEquals("Charge amount must be greater than zero", exception.getMessage());
    }


    @Test
    void testCreateCharge_whenPayeeNotFound_shouldThrowUserNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(UserNotFoundException.class, () -> chargeCreationService.createCharge(chargeDTO, 1L));
    }
}
