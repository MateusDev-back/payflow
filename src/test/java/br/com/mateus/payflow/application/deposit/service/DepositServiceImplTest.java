package br.com.mateus.payflow.application.deposit.service;

import br.com.mateus.payflow.application.deposit.dto.DepositDTO;
import br.com.mateus.payflow.common.exception.deposit.DepositAmountInvalidException;
import br.com.mateus.payflow.common.exception.authorizer.ExternalAuthorizerDepositException;
import br.com.mateus.payflow.common.exception.user.UserNotFoundException;
import br.com.mateus.payflow.domain.user.model.UserEntity;
import br.com.mateus.payflow.domain.user.repository.UserRepository;
import br.com.mateus.payflow.application.payment.integration.ExternalAuthorizerClient;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepositServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExternalAuthorizerClient externalAuthorizerClient;

    private DepositServiceImpl depositService;

    private UserEntity user;
    private DepositDTO depositDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        depositService = new DepositServiceImpl(userRepository, externalAuthorizerClient);

        Faker faker = new Faker();

        Long userId = faker.number().randomNumber();

        user = new UserEntity();
        user.setId(userId);
        user.setBalance(BigDecimal.valueOf(100.0));

        depositDTO = new DepositDTO();
        depositDTO.setAmount(BigDecimal.valueOf(50.0));
        depositDTO.setUserId(user.getId());
    }

    @Test
    void testCreateDeposit_Success() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(externalAuthorizerClient.authorize()).thenReturn(true);

        DepositDTO result = depositService.createDeposit(depositDTO, user.getId());

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(150.0), result.getAmount());
        verify(userRepository).save(user);
    }

    @Test
    void testCreateDeposit_UserNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> depositService.createDeposit(depositDTO, user.getId()));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testCreateDeposit_InvalidAmount() {
        depositDTO.setAmount(BigDecimal.valueOf(-50.0));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        DepositAmountInvalidException exception = assertThrows(DepositAmountInvalidException.class,
                () -> depositService.createDeposit(depositDTO, user.getId()));
        assertEquals("Deposit amount must be greater than zero", exception.getMessage());
    }


    @Test
    void testCreateDeposit_AuthorizationFailed() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(externalAuthorizerClient.authorize()).thenReturn(false);

        ExternalAuthorizerDepositException exception = assertThrows(ExternalAuthorizerDepositException.class, () -> depositService.createDeposit(depositDTO, user.getId()));
        assertEquals("Error authorizing deposit", exception.getMessage());
    }
}
