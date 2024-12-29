package br.com.mateus.payflow.application.user.service;

import br.com.mateus.payflow.application.user.dto.UserResponseDTO;
import br.com.mateus.payflow.domain.user.model.UserEntity;
import br.com.mateus.payflow.domain.user.repository.UserRepository;
import br.com.mateus.payflow.common.exception.user.CpfAlreadyExistsException;
import br.com.mateus.payflow.common.exception.user.EmailAlreadyExistsException;
import com.github.javafaker.Faker;
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
    private UserRegisterService userRegisterService;

    private UserEntity validUser;

    @BeforeEach
    void setUp() {
        Faker faker = new Faker();

        validUser = new UserEntity();
        validUser.setCpf(faker.number().digits(11));
        validUser.setEmail(faker.internet().emailAddress());
        validUser.setPassword(faker.internet().password());
    }

    @Test
    void testSaveUser_whenValidUser_shouldSaveUser() {
        when(userRepository.existsByCpf(validUser.getCpf())).thenReturn(false);
        when(userRepository.existsByEmail(validUser.getEmail())).thenReturn(false);
        when(userRepository.save(validUser)).thenReturn(validUser);
        when(passwordEncoder.encode(validUser.getPassword())).thenReturn("encodedPassword");

        UserResponseDTO savedUserResponse = userRegisterService.save(validUser);

        assertNotNull(savedUserResponse);

        assertEquals("User created successfully", savedUserResponse.getMessage());
        assertEquals("success", savedUserResponse.getStatus());

        verify(userRepository).save(validUser);
    }

    @Test
    void testSaveUser_whenCpfAlreadyExists_shouldThrowCpfAlreadyExistsException() {
        when(userRepository.existsByCpf(validUser.getCpf())).thenReturn(true);

        assertThrows(CpfAlreadyExistsException.class, () -> userRegisterService.save(validUser));
    }

    @Test
    void testSaveUser_whenEmailAlreadyExists_shouldThrowEmailAlreadyExistsException() {
        when(userRepository.existsByCpf(validUser.getCpf())).thenReturn(false);
        when(userRepository.existsByEmail(validUser.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userRegisterService.save(validUser));
    }
}
