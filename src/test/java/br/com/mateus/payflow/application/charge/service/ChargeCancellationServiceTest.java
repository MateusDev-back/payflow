package br.com.mateus.payflow.application.charge.service;

import br.com.mateus.payflow.application.balance.service.BalanceService;
import br.com.mateus.payflow.application.payment.integration.ExternalAuthorizerClient;
import br.com.mateus.payflow.common.exception.balance.BalanceNoBalanceException;
import br.com.mateus.payflow.common.exception.charge.ChargeException;
import br.com.mateus.payflow.common.exception.charge.ChargeNotAuthorizedException;
import br.com.mateus.payflow.common.exception.charge.ChargeNotFoundException;
import br.com.mateus.payflow.domain.charge.model.ChargeEntity;
import br.com.mateus.payflow.domain.charge.repository.ChargeRepository;
import br.com.mateus.payflow.domain.user.model.UserEntity;
import br.com.mateus.payflow.enums.charge.ChargeStatus;
import br.com.mateus.payflow.enums.payment.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ChargeCancellationServiceTest {

    @Mock
    private ExternalAuthorizerClient externalAuthorizerClient;

    @Mock
    private BalanceService balanceService;

    @Mock
    private ChargeRepository chargeRepository;

    @InjectMocks
    private ChargeCancelationService chargeCancelationService;

    private ChargeEntity charge;
    private UserEntity payer;
    private UserEntity payee;

    @BeforeEach
    void setUp() {
        payer = new UserEntity();
        payer.setId(18L);
        payer.setName("Teste 123");
        payer.setCpf("12345678900");
        payer.setEmail("teste123@gmail.com");
        payer.setPassword("123456");
        payer.setBalance(BigDecimal.valueOf(1000.0));
        payer.setCreatedAt(LocalDateTime.now());


        payee = new UserEntity();
        payee.setId(50L);
        payee.setName("Teste 456");
        payee.setCpf("98765432100");
        payee.setEmail("teste321@hotmail.com");
        payee.setPassword("654321");
        payer.setBalance(BigDecimal.valueOf(1000.0));
        payee.setCreatedAt(LocalDateTime.now());

        charge = new ChargeEntity();
        charge.setId("charge123");
        charge.setAmount(BigDecimal.valueOf(100.0));
        charge.setPayee(payee);
        charge.setPayer(payer);
        charge.setStatus(ChargeStatus.PENDING);
        charge.setPaymentMethod(PaymentMethod.BALANCE);
    }

    @Test
    void testCancelCharge_whenChargeNotFound_shouldThrowChargeNotFoundException() {
        when(chargeRepository.findById("charge123")).thenReturn(Optional.empty());

        assertThrows(ChargeNotFoundException.class, () -> chargeCancelationService.cancelCharge("charge123", payer.getId()));
    }

    @Test
    void testCancelCharge_whenUserNotAuthorized_shouldThrowChargeException() {
        when(chargeRepository.findById("charge123")).thenReturn(Optional.of(charge));

        assertThrows(ChargeNotAuthorizedException.class, () -> chargeCancelationService.cancelCharge("charge123", payer.getId()));
    }

    @Test
    void testCancelCharge_whenChargeAlreadyCanceled_shouldThrowChargeException() {
        charge.setStatus(ChargeStatus.CANCELED);
        when(chargeRepository.findById("charge123")).thenReturn(Optional.of(charge));

        assertThrows(ChargeException.class, () -> chargeCancelationService.cancelCharge("charge123", payee.getId()));
    }


    @Test
    void testCancelCharge_whenPaymentMethodBalance_shouldCancelCorrectly() {
        payee.setBalance(BigDecimal.valueOf(1000.0));
        charge.setPaymentMethod(PaymentMethod.BALANCE);
        when(chargeRepository.findById("charge123")).thenReturn(Optional.of(charge));

        chargeCancelationService.cancelCharge("charge123", payee.getId());

        assertEquals(ChargeStatus.CANCELED, charge.getStatus());
        assertThrows(ChargeException.class, () -> chargeCancelationService.cancelCharge("charge123", payee.getId()));
    }

    @Test
    void testCancelCharge_whenPaymentMethodCreditCard_shouldCancelCorrectly() {
        charge.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        charge.setStatus(ChargeStatus.PAID);

        when(chargeRepository.findById("charge123")).thenReturn(Optional.of(charge));
        when(externalAuthorizerClient.authorize()).thenReturn(true);

        doNothing().when(balanceService).debit(payee, charge.getAmount());
        doNothing().when(balanceService).credit(payer, charge.getAmount());

        chargeCancelationService.cancelCharge("charge123", payee.getId());

        assertEquals(ChargeStatus.CANCELED, charge.getStatus());
        verify(externalAuthorizerClient).authorize();
        verify(balanceService).debit(payee, charge.getAmount());
        verify(balanceService).credit(payer, charge.getAmount());
    }

    @Test
    void testCancelCharge_whenPaymentMethodCreditCard_shouldNotCancel_whenAuthorizationFails() {
        charge.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        charge.setStatus(ChargeStatus.PAID);

        when(chargeRepository.findById("charge123")).thenReturn(Optional.of(charge));
        when(externalAuthorizerClient.authorize()).thenReturn(false);

        assertThrows(ChargeException.class, () -> chargeCancelationService.cancelCharge("charge123", payee.getId()));
        assertEquals(ChargeStatus.PAID, charge.getStatus());

        verify(externalAuthorizerClient).authorize();
        verify(balanceService, never()).debit(any(), any());
        verify(balanceService, never()).credit(any(), any());
    }


    @Test
    void testCancelCharge_whenBalanceFails_shouldThrowBalanceNoBalanceException() {
        payee.setBalance(BigDecimal.ZERO);
        charge.setPaymentMethod(PaymentMethod.BALANCE);
        charge.setStatus(ChargeStatus.PAID);

        when(chargeRepository.findById("charge123")).thenReturn(Optional.of(charge));

        assertThrows(BalanceNoBalanceException.class, () -> chargeCancelationService.cancelCharge("charge123", payee.getId()));
    }
}
