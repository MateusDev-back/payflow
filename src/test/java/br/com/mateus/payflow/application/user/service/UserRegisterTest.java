package br.com.mateus.payflow.application.user.service;

import br.com.mateus.payflow.domain.user.model.UserEntity;
import br.com.mateus.payflow.domain.user.repository.UserRepository;
import br.com.mateus.payflow.domain.user.service.UserService;
import br.com.mateus.payflow.common.exception.user.CpfAlreadyExistsException;
import br.com.mateus.payflow.common.exception.user.EmailAlreadyExistsException;
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
class UserRegisterTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserEntity validUser;

    @BeforeEach
    void setUp() {
        validUser = new UserEntity();
        validUser.setCpf("46871326855");
        validUser.setEmail("test@example.com");
        validUser.setPassword("password123");
    }

    @Test
    void testSaveUser_whenValidUser_shouldSaveUser() {
        when(userRepository.existsByCpf(validUser.getCpf())).thenReturn(false);
        when(userRepository.existsByEmail(validUser.getEmail())).thenReturn(false);
        when(userRepository.save(validUser)).thenReturn(validUser);
        when(passwordEncoder.encode(validUser.getPassword())).thenReturn("encodedPassword");

        UserEntity savedUser = userService.save(validUser);

        assertNotNull(savedUser);
        assertEquals("encodedPassword", savedUser.getPassword());
        verify(userRepository).save(validUser);
    }

    @Test
    void testSaveUser_whenCpfAlreadyExists_shouldThrowCpfAlreadyExistsException() {
        when(userRepository.existsByCpf(validUser.getCpf())).thenReturn(true);

        assertThrows(CpfAlreadyExistsException.class, () -> userService.save(validUser));
    }

    @Test
    void testSaveUser_whenEmailAlreadyExists_shouldThrowEmailAlreadyExistsException() {
        when(userRepository.existsByCpf(validUser.getCpf())).thenReturn(false);
        when(userRepository.existsByEmail(validUser.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.save(validUser));
    }
}
