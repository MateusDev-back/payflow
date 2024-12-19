package br.com.mateus.payflow.application.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.mateus.payflow.application.user.dto.UserRegisterRequestDTO;
import br.com.mateus.payflow.application.user.mapper.UserMapper;
import br.com.mateus.payflow.domain.user.model.UserEntity;
import br.com.mateus.payflow.domain.user.service.UserService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/payflow/v1/users")
public class UserRegisterController {

    private final UserService userService;

    public UserRegisterController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public UserEntity register(@Valid @RequestBody UserRegisterRequestDTO dto) {
        var userEntity = UserMapper.toEntity(dto);
        var savedUser = userService.save(userEntity);
        return savedUser;
    }
}
