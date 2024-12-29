package br.com.mateus.payflow.application.auth.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

import br.com.mateus.payflow.common.exception.auth.CredentialsInvalidsException;
import br.com.mateus.payflow.common.exception.user.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import br.com.mateus.payflow.application.auth.dto.LoginResponseDTO;
import br.com.mateus.payflow.application.auth.dto.UserLoginRequestDTO;
import br.com.mateus.payflow.domain.user.model.UserEntity;
import br.com.mateus.payflow.domain.user.repository.UserRepository;

@Service
public class AuthService {

    @Value("${security.token.secret}")
    public String secretKey;

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponseDTO execute(UserLoginRequestDTO userLoginRequestDTO) {
        String identifier = userLoginRequestDTO.getLogin();

        UserEntity user = findUserByIdentifier(identifier);

        boolean isPasswordValid = passwordEncoder.matches(userLoginRequestDTO.getPassword(), user.getPassword());

        if (!isPasswordValid) {
            throw new CredentialsInvalidsException();
        }

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        var token = JWT.create()
                .withIssuer("user_id")
                .withExpiresAt(Instant.now().plus(Duration.ofHours(2)))
                .withClaim("roles", Arrays.asList("USER"))
                .withSubject(user.getId().toString())
                .sign(algorithm);

        return new LoginResponseDTO(token, "Login successful");
    }

    private UserEntity findUserByIdentifier(String identifier) {
        if (identifier.contains("@")) {
            return userRepository.findByEmail(identifier)
                    .orElseThrow(UserNotFoundException::new);
        } else {
            return userRepository.findByCpf(identifier)
                    .orElseThrow(UserNotFoundException::new);
        }
    }
}
