package br.com.mateus.payflow.application.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.mateus.payflow.application.auth.dto.LoginResponseDTO;
import br.com.mateus.payflow.application.auth.dto.UserLoginRequestDTO;
import br.com.mateus.payflow.application.auth.service.AuthService;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/payflow/v1/auth")
public class AuthUserController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signin")
    public LoginResponseDTO login(@RequestBody UserLoginRequestDTO userLoginRequestDTO) throws AuthenticationException {
        return this.authService.execute(userLoginRequestDTO);
    }

}
