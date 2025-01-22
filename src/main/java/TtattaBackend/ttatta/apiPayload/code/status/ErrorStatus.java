package TtattaBackend.ttatta.apiPayload.code.status;

import TtattaBackend.ttatta.apiPayload.code.BaseErrorCode;
import TtattaBackend.ttatta.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // 회원 관련 응답 1000
    USER_ID_NULL(HttpStatus.BAD_REQUEST, "USER_1001", "사용자 아이디는 필수 입니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER_1002", "해당하는 사용자가 존재하지 않습니다."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "USER1003", "닉네임은 필수 입니다."),

    // 일기 관련 응답 2000
    DIARY_NOT_FOUND(HttpStatus.NOT_FOUND, "DIARY_2001", "해당하는 일기가 존재하지 않습니다."),
    DIARY_IS_NULL(HttpStatus.BAD_REQUEST, "DIARY_2002", "일기는 필수 입니다."),

    // 일기 카테고리 관련 응답 3000
    DIARY_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "DIARY_CATEGORY_3001", "해당하는 일기 카테고리가 존재하지 않습니다."),
    DIARY_CATEGORY_IS_NULL(HttpStatus.BAD_REQUEST, "DIARY_CATEGORY_3002", "일기 카테고리는 필수 입니다."),
    DIARY_CATEGORY_COLOR_NOT_FOUND(HttpStatus.NOT_FOUND,"DIARY_CATEGORY_3003","해당하는 일기 카테고리 색상은 존재하지 않습니다."),
    DIARY_CATEGORY_COLOR_NULL(HttpStatus.BAD_REQUEST, "DIARY_CATEGORY_3004", "일기 카테고리 색상은 필수 입니다.");
    // ~~~ 관련 응답 ....


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
