package br.com.mateus.payflow.domain.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.mateus.payflow.common.exception.CpfAlreadyExistsException;
import br.com.mateus.payflow.common.exception.EmailAlreadyExistsException;
import br.com.mateus.payflow.domain.user.model.UserEntity;
import br.com.mateus.payflow.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserEntity save(UserEntity user) {
        if (userRepository.existsByCpf(user.getCpf())) {
            throw new CpfAlreadyExistsException("CPF already exists");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
