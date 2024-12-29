package br.com.mateus.payflow.application.balance.service;

import br.com.mateus.payflow.common.exception.balance.BalanceInsufficientException;
import br.com.mateus.payflow.domain.user.model.UserEntity;
import br.com.mateus.payflow.domain.user.repository.UserRepository;
import com.github.javafaker.Faker;
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
class BalanceServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BalanceService balanceService;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        Faker faker = new Faker();

        Long userId = faker.number().randomNumber();

        user = new UserEntity();
        user.setId(userId);
        user.setBalance(BigDecimal.valueOf(1000));
    }

    @Test
    void testDebit() {
        BigDecimal amount = BigDecimal.valueOf(200);
        BigDecimal expectedBalance = BigDecimal.valueOf(800);

        balanceService.debit(user, amount);

        assertEquals(expectedBalance, user.getBalance());
        verify(userRepository, times(1)).save(user);

    }

    @Test
    void testCredit() {
        BigDecimal amount = BigDecimal.valueOf(300);
        BigDecimal expectedBalance = BigDecimal.valueOf(1300);

        balanceService.credit(user, amount);

        assertEquals(expectedBalance, user.getBalance());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testDebitInsufficientFunds() {
        BigDecimal amount = BigDecimal.valueOf(2000);
        BigDecimal initialBalance = user.getBalance();

        assertThrows(BalanceInsufficientException.class, () -> balanceService.debit(user, amount));
        assertEquals(initialBalance, user.getBalance());
    }
}
