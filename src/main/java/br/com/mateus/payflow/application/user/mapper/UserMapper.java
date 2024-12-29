package br.com.mateus.payflow.application.user.mapper;

import br.com.mateus.payflow.application.user.dto.UserRegisterRequestDTO;
import br.com.mateus.payflow.domain.user.model.UserEntity;

public class UserMapper {

    public static UserEntity toEntity(UserRegisterRequestDTO dto) {
        UserEntity user = new UserEntity();
        user.setName(dto.getName());
        user.setCpf(dto.getCpf());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        return user;
    }

}
