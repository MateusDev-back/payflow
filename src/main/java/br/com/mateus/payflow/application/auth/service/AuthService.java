package br.com.mateus.payflow.application.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import br.com.mateus.payflow.application.auth.dto.LoginResponseDTO;
import br.com.mateus.payflow.application.auth.dto.UserLoginRequestDTO;
import br.com.mateus.payflow.common.exception.ValidationException;
import br.com.mateus.payflow.domain.user.model.UserEntity;
import br.com.mateus.payflow.domain.user.repository.UserRepository;

@Service
public class AuthService {

    @Value("${security.token.secret}")
    private String secretKey;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponseDTO execute(UserLoginRequestDTO userLoginRequestDTO) {
        String identifier = userLoginRequestDTO.getLogin();

        UserEntity user = findUserByIdentifier(identifier);

        boolean isPasswordValid = passwordEncoder.matches(userLoginRequestDTO.getPassword(), user.getPassword());

        if (!isPasswordValid) {
            throw new BadCredentialsException("Invalid credentials");
        }

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        var token = JWT.create()
                .withIssuer("identifier")
                .withSubject(user.getId().toString())
                .sign(algorithm);

        return new LoginResponseDTO(token, "Login successful");
    }

    private UserEntity findUserByIdentifier(String identifier) {
        if (identifier.contains("@")) {
            return userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new ValidationException("User not found"));
        } else {
            return userRepository.findByCpf(identifier)
                    .orElseThrow(() -> new ValidationException("User not found"));
        }
    }
}
