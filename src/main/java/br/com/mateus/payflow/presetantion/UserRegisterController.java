package br.com.mateus.payflow.presetantion;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.mateus.payflow.application.user.dto.UserRegisterRequestDTO;
import br.com.mateus.payflow.application.user.mapper.UserMapper;
import br.com.mateus.payflow.domain.user.model.UserEntity;
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
    public UserEntity register(@Valid @RequestBody UserRegisterRequestDTO dto) {
        return userRegisterService.save(UserMapper.toEntity(dto));
    }
}
