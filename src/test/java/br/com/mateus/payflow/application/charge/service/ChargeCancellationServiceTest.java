package br.com.mateus.payflow.application.charge.service;

import br.com.mateus.payflow.application.balance.service.BalanceService;
import br.com.mateus.payflow.infrastructure.HttpExternalAuthorizerClient;
import br.com.mateus.payflow.common.exception.balance.BalanceInsufficientException;
import br.com.mateus.payflow.common.exception.charge.ChargeException;
import br.com.mateus.payflow.common.exception.charge.ChargeNotAuthorizedException;
import br.com.mateus.payflow.common.exception.charge.ChargeNotFoundException;
import br.com.mateus.payflow.domain.charge.model.ChargeEntity;
import br.com.mateus.payflow.domain.charge.repository.ChargeRepository;
import br.com.mateus.payflow.domain.user.model.UserEntity;
import br.com.mateus.payflow.enums.charge.ChargeStatus;
import br.com.mateus.payflow.enums.payment.PaymentMethod;
import com.github.javafaker.Faker;
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
    private HttpExternalAuthorizerClient httpExternalAuthorizerClient;

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
        Faker faker = new Faker();

        Long payeeId = faker.number().randomNumber();
        Long payerId = faker.number().randomNumber();

        String chargeId = faker.internet().uuid();

        payer = new UserEntity();
        payer.setId(payeeId);
        payer.setName(faker.name().fullName());
        payer.setCpf(faker.number().digits(11));
        payer.setEmail(faker.internet().emailAddress());
        payer.setPassword(faker.internet().password());
        payer.setBalance(BigDecimal.valueOf(1000.0));
        payer.setCreatedAt(LocalDateTime.now());


        payee = new UserEntity();
        payee.setId(payerId);
        payee.setName(faker.name().fullName());
        payee.setCpf(faker.number().digits(11));
        payee.setEmail(faker.internet().emailAddress());
        payee.setPassword(faker.internet().password());
        payer.setBalance(BigDecimal.valueOf(1000.0));
        payee.setCreatedAt(LocalDateTime.now());

        charge = new ChargeEntity();
        charge.setId(chargeId);
        charge.setAmount(BigDecimal.valueOf(100.0));
        charge.setPayee(payee);
        charge.setPayer(payer);
        charge.setStatus(ChargeStatus.PENDING);
        charge.setPaymentMethod(PaymentMethod.BALANCE);
    }

    @Test
    void testCancelCharge_whenChargeNotFound_shouldThrowChargeNotFoundException() {
        when(chargeRepository.findById("Teste123")).thenReturn(Optional.empty());

        assertThrows(ChargeNotFoundException.class, () -> chargeCancelationService.cancelCharge("Teste123", payer.getId()));
    }

    @Test
    void testCancelCharge_whenUserNotAuthorized_shouldThrowChargeException() {
        when(chargeRepository.findById(charge.getId())).thenReturn(Optional.of(charge));

        assertThrows(ChargeNotAuthorizedException.class, () -> chargeCancelationService.cancelCharge(charge.getId(), payer.getId()));
    }

    @Test
    void testCancelCharge_whenChargeAlreadyCanceled_shouldThrowChargeException() {
        charge.setStatus(ChargeStatus.CANCELED);
        when(chargeRepository.findById(charge.getId())).thenReturn(Optional.of(charge));

        assertThrows(ChargeException.class, () -> chargeCancelationService.cancelCharge(charge.getId(), payee.getId()));
    }


    @Test
    void testCancelCharge_whenPaymentMethodBalance_shouldCancelCorrectly() {
        payee.setBalance(BigDecimal.valueOf(1000.0));
        charge.setPaymentMethod(PaymentMethod.BALANCE);
        when(chargeRepository.findById(charge.getId())).thenReturn(Optional.of(charge));

        chargeCancelationService.cancelCharge(charge.getId(), payee.getId());

        assertEquals(ChargeStatus.CANCELED, charge.getStatus());
        assertThrows(ChargeException.class, () -> chargeCancelationService.cancelCharge(charge.getId(), payee.getId()));
    }

    @Test
    void testCancelCharge_whenPaymentMethodCreditCard_shouldCancelCorrectly() {
        charge.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        charge.setStatus(ChargeStatus.PAID);

        when(chargeRepository.findById(charge.getId())).thenReturn(Optional.of(charge));
        when(httpExternalAuthorizerClient.authorize()).thenReturn(true);

        doNothing().when(balanceService).debit(payee, charge.getAmount());
        doNothing().when(balanceService).credit(payer, charge.getAmount());

        chargeCancelationService.cancelCharge(charge.getId(), payee.getId());

        assertEquals(ChargeStatus.CANCELED, charge.getStatus());
        verify(httpExternalAuthorizerClient).authorize();
        verify(balanceService).debit(payee, charge.getAmount());
        verify(balanceService).credit(payer, charge.getAmount());
    }

    @Test
    void testCancelCharge_whenPaymentMethodCreditCard_shouldNotCancel_whenAuthorizationFails() {
        charge.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        charge.setStatus(ChargeStatus.PAID);

        when(chargeRepository.findById(charge.getId())).thenReturn(Optional.of(charge));
        when(httpExternalAuthorizerClient.authorize()).thenReturn(false);

        assertThrows(ChargeException.class, () -> chargeCancelationService.cancelCharge(charge.getId(), payee.getId()));
        assertEquals(ChargeStatus.PAID, charge.getStatus());

        verify(httpExternalAuthorizerClient).authorize();
        verify(balanceService, never()).debit(any(), any());
        verify(balanceService, never()).credit(any(), any());
    }


    @Test
    void testCancelCharge_whenBalanceFails_shouldThrowBalanceNoBalanceException() {
        payee.setBalance(BigDecimal.ZERO);
        charge.setPaymentMethod(PaymentMethod.BALANCE);
        charge.setStatus(ChargeStatus.PAID);

        when(chargeRepository.findById(charge.getId())).thenReturn(Optional.of(charge));

        assertThrows(BalanceInsufficientException.class, () -> chargeCancelationService.cancelCharge(charge.getId(), payee.getId()));
        verify(chargeRepository, times(1)).findById(charge.getId());
    }
}
