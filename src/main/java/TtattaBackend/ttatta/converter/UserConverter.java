package TtattaBackend.ttatta.converter;

import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.web.dto.UserResponseDTO;

public class UserConverter {
    public static UserResponseDTO.UserSignUpResultDTO toUserSignUpResultDTO(Users users) {
        return UserResponseDTO.UserSignUpResultDTO.builder()
                .userId(users.getId())
                .createdAt(users.getCreatedAt())
                .build();
    }
}
