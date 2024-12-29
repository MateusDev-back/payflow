package br.com.mateus.payflow.application.auth.service;

import br.com.mateus.payflow.application.auth.dto.LoginResponseDTO;
import br.com.mateus.payflow.application.auth.dto.UserLoginRequestDTO;
import br.com.mateus.payflow.common.exception.auth.CredentialsInvalidsException;
import br.com.mateus.payflow.common.exception.user.UserNotFoundException;
import br.com.mateus.payflow.domain.user.model.UserEntity;
import br.com.mateus.payflow.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private UserEntity validUser;
    private UserLoginRequestDTO validLoginRequest;

    @BeforeEach
    void setUp() {
        authService.secretKey = "PAYFLOW@!@#";

        validUser = new UserEntity();
        validUser.setId(1L);
        validUser.setEmail("test@example.com");
        validUser.setCpf("46871326855");
        validUser.setPassword("password123");

        validLoginRequest = new UserLoginRequestDTO();
        validLoginRequest.setLogin(validUser.getEmail());
        validLoginRequest.setPassword(validUser.getPassword());
    }

    @Test
    void testExecute_whenValidLogin_shouldReturnToken() {
        when(userRepository.findByEmail(validUser.getEmail())).thenReturn(java.util.Optional.of(validUser));
        when(passwordEncoder.matches(validLoginRequest.getPassword(), validUser.getPassword())).thenReturn(true);

        LoginResponseDTO loginResponse = authService.execute(validLoginRequest);

        assertNotNull(loginResponse);
        assertNotNull(loginResponse.getToken());
        assertEquals("Login successful", loginResponse.getMessage());
        verify(userRepository).findByEmail(validUser.getEmail());
        verify(passwordEncoder).matches(validLoginRequest.getPassword(), validUser.getPassword());
    }

    @Test
    void testExecute_whenInvalidPassword_shouldThrowCredentialsInvalidsException() {
        when(userRepository.findByEmail(validUser.getEmail())).thenReturn(java.util.Optional.of(validUser));
        when(passwordEncoder.matches(validLoginRequest.getPassword(), validUser.getPassword())).thenReturn(false);

        assertThrows(CredentialsInvalidsException.class, () -> authService.execute(validLoginRequest));
    }

    @Test
    void testExecute_whenUserNotFound_shouldThrowUserNotFoundException() {
        when(userRepository.findByEmail(validUser.getEmail())).thenReturn(java.util.Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.execute(validLoginRequest));
    }

    @Test
    void testExecute_whenUserNotFoundByCpf_shouldThrowUserNotFoundException() {
        validLoginRequest.setLogin(validUser.getCpf());
        when(userRepository.findByCpf(validUser.getCpf())).thenReturn(java.util.Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.execute(validLoginRequest));
    }
}
