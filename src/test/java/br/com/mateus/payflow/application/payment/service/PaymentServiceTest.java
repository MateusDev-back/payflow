package br.com.mateus.payflow.application.payment.service;

import br.com.mateus.payflow.application.payment.factory.PaymentStrategyFactory;
import br.com.mateus.payflow.application.payment.integration.ExternalAuthorizerClient;
import br.com.mateus.payflow.application.payment.strategy.PaymentStrategy;
import br.com.mateus.payflow.common.exception.authorizer.ExternalAuthorizerPaymentException;
import br.com.mateus.payflow.common.exception.balance.BalanceInsufficientException;
import br.com.mateus.payflow.domain.charge.model.ChargeEntity;
import br.com.mateus.payflow.domain.charge.repository.ChargeRepository;
import br.com.mateus.payflow.domain.user.model.UserEntity;
import br.com.mateus.payflow.domain.user.repository.UserRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private ChargeRepository chargeRepository;

    @Mock
    private PaymentStrategyFactory paymentStrategyFactory;

    @Mock
    private PaymentStrategy paymentStrategy;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExternalAuthorizerClient externalAuthorizerClient;

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
        payer.setId(payerId);
        payer.setBalance(BigDecimal.valueOf(1000.0));

        payee = new UserEntity();
        payee.setId(payeeId);
        payee.setBalance(BigDecimal.valueOf(500.0));

        charge = new ChargeEntity();
        charge.setId(chargeId);
        charge.setAmount(BigDecimal.valueOf(200.0));
        charge.setPayee(payee);
        charge.setPayer(payer);
        charge.setStatus(ChargeStatus.PENDING);
        charge.setPaymentMethod(PaymentMethod.BALANCE);
    }

    @Test
    void testPayCharge_WithSufficientBalance_ShouldPaySuccessfully() {
        when(chargeRepository.findById(charge.getId())).thenReturn(Optional.of(charge));
        when(paymentStrategyFactory.getPaymentStrategy(PaymentMethod.BALANCE)).thenReturn(new BalancePaymentStrategy());

        paymentService.payCharge(charge.getId(), PaymentMethod.BALANCE);

        assertEquals(ChargeStatus.PAID, charge.getStatus());
        assertEquals(BigDecimal.valueOf(800.0), payer.getBalance());
        assertEquals(BigDecimal.valueOf(700.0), payee.getBalance());
    }

    @Test
    void testPayCharge_WithInsufficientBalance_ShouldThrowBalanceInsufficientException() {
        charge.setAmount(BigDecimal.valueOf(1200.0));

        when(chargeRepository.findById(charge.getId())).thenReturn(Optional.of(charge));
        when(paymentStrategyFactory.getPaymentStrategy(PaymentMethod.BALANCE)).thenReturn(new BalancePaymentStrategy());

        assertThrows(BalanceInsufficientException.class, () -> paymentService.payCharge(charge.getId(), PaymentMethod.BALANCE));
    }

    @Test
    void testPayCharge_WithCreditCard_ShouldPaySuccessfully() {
        when(chargeRepository.findById(charge.getId())).thenReturn(Optional.of(charge));
        when(paymentStrategyFactory.getPaymentStrategy(PaymentMethod.CREDIT_CARD)).thenReturn(new CreditCardPaymentStrategy(externalAuthorizerClient));
        when(externalAuthorizerClient.authorize()).thenReturn(true);

        paymentService.payCharge(charge.getId(), PaymentMethod.CREDIT_CARD);

        assertEquals(ChargeStatus.PAID, charge.getStatus());
        assertEquals(BigDecimal.valueOf(700.0), payee.getBalance());
    }

    @Test
    void testPayCharge_WithCreditCard_AuthFailure_ShouldThrowRuntimeException() {
        when(chargeRepository.findById(charge.getId())).thenReturn(Optional.of(charge));
        when(paymentStrategyFactory.getPaymentStrategy(PaymentMethod.CREDIT_CARD)).thenReturn(new CreditCardPaymentStrategy(externalAuthorizerClient));
        when(externalAuthorizerClient.authorize()).thenReturn(false);

        assertThrows(ExternalAuthorizerPaymentException.class, () -> paymentService.payCharge(charge.getId(), PaymentMethod.CREDIT_CARD));
    }
}
