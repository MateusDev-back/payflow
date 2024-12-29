package br.com.mateus.payflow.presetantion;

import br.com.mateus.payflow.application.user.dto.UserResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.mateus.payflow.application.user.dto.UserRegisterRequestDTO;
import br.com.mateus.payflow.application.user.mapper.UserMapper;
import br.com.mateus.payflow.application.user.service.UserRegisterService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/payflow/v1/users")
public class UserRegisterController {

    private final UserRegisterService userRegisterService;

    public UserRegisterController(UserRegisterService userRegisterService) {
        this.userRegisterService = userRegisterService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRegisterRequestDTO dto) {
        UserResponseDTO response = userRegisterService.save(UserMapper.toEntity(dto));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
