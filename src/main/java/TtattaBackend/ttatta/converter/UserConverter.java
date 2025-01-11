package TtattaBackend.ttatta.converter;

import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.domain.enums.LoginType;
import TtattaBackend.ttatta.web.dto.UserRequestDTO;
import TtattaBackend.ttatta.web.dto.UserResponseDTO;

public class UserConverter {
    public static Users toUsers(UserRequestDTO.SignUpRequestDTO request) {
        return Users.builder()
                .nickname(request.getNickname())
                .username(request.getUsername())
                .loginType(LoginType.REGULAR)
                .build();
    }

    public static UserResponseDTO.UserSignUpResultDTO toUserSignUpResultDTO(Users users) {
        return UserResponseDTO.UserSignUpResultDTO.builder()
                .userId(users.getId())
                .nickname(users.getNickname())
                .loginType(users.getLoginType())
                .createdAt(users.getCreatedAt())
                .build();
    }

    public static UserResponseDTO.UserSignInResultDTO toUserSignInResultDTO(Users users) {
        return UserResponseDTO.UserSignInResultDTO.builder()
                .userId(users.getId())
                .nickname(users.getNickname())
                .loginType(users.getLoginType())
                .createdAt(users.getCreatedAt())
                .build();
    }

    public static UserResponseDTO.UserInfoResultDTO toUserInfoResultDTO(Users users) {
        return UserResponseDTO.UserInfoResultDTO.builder()
                .userId(users.getId())
                .nickname(users.getNickname())
                .loginType(users.getLoginType())
                .email(users.getEmail())
                .profileImg(users.getProfileImage())
                .point(users.getPoint())
                .status(users.getStatus())
                .build();
    }
}
