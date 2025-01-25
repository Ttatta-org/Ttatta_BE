package TtattaBackend.ttatta.converter;

import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.domain.enums.IsAvailable;
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

    public static UserResponseDTO.UserSignInResultDTO toUserSignInResultDTO(Users users, String accessToken, String refreshToken) {
        return UserResponseDTO.UserSignInResultDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(users.getId())
                .nickname(users.getNickname())
                .loginType(users.getLoginType())
                .createdAt(users.getCreatedAt())
                .build();
    }

    public static UserResponseDTO.VerifyUsernameOverlapResultDTO toVerifyUsernameOverlapResultDTO(IsAvailable isAvailable) {
        return UserResponseDTO.VerifyUsernameOverlapResultDTO.builder()
                .isAvailable(isAvailable)
                .build();
    }

    public static UserResponseDTO.RefreshResultDTO toRefreshResultDTO(Long userId, String accessToken, String refreshToken) {
        return UserResponseDTO.RefreshResultDTO.builder()
                .userId(userId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
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
