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
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "USER_1003", "닉네임은 필수 입니다."),
    EMAIL_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "USER_1004", "이미 존재하는 이메일 입니다."),
    CODE_NOT_EQUAL(HttpStatus.BAD_REQUEST, "USER_1005", "인증코드가 일치하지 않습니다."),
    NAME_NOT_EQUAL(HttpStatus.BAD_REQUEST, "USER_1006", "이름이 일치하지 않습니다."),
    ID_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER_1007", "해당하는 ID가 존재하지 않습니다."),
    ID_NOT_EQUAL(HttpStatus.BAD_REQUEST, "USER_1008", "ID가 일치하지 않습니다."),
    SAME_PASSWORD(HttpStatus.BAD_REQUEST, "USER_1009", "이전 비밀번호와 동일합니다."),

    // 일기 관련 응답 2000
    DIARY_NOT_FOUND(HttpStatus.NOT_FOUND, "DIARY_2001", "해당하는 일기가 존재하지 않습니다."),
    DIARY_IS_NULL(HttpStatus.BAD_REQUEST, "DIARY_2002", "일기는 필수 입니다."),

    // 일기 카테고리 관련 응답 3000
    DIARY_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "DIARY_CATEGORY_3001", "해당하는 일기 카테고리가 존재하지 않습니다."),
    DIARY_CATEGORY_IS_NULL(HttpStatus.BAD_REQUEST, "DIARY_CATEGORY_3002", "일기 카테고리는 필수 입니다."),
    DIARY_CATEGORY_COLOR_NOT_FOUND(HttpStatus.NOT_FOUND,"DIARY_CATEGORY_3003","해당하는 일기 카테고리 색상은 존재하지 않습니다."),
    DIARY_CATEGORY_COLOR_NULL(HttpStatus.BAD_REQUEST, "DIARY_CATEGORY_3004", "일기 카테고리 색상은 필수 입니다."),
    DIARY_CATEGORY_DELETE_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "DIARY_CATEGORY_3005","삭제하려고하는 카테고리 id에 해당하는 사용자가 존재하지 않습니다."),
    DIARY_CATEGORY_MODIFY_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "DIARY_CATEGORY_3006","수정하려는 카테고리 id에 해당하는 사용자가 존재하지 않습니다."),
    DIARY_CATEGORY_GET_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "DIARY_CATEGORY_3007","조회하려는 카테고리 id에 해당하는 사용자가 존재하지 않습니다."),
    DIARY_CATEGORY_DEFAULT_NOT_FOUND(HttpStatus.NOT_FOUND,"DIARY_CATEGORY_3008","일상 카테고리가 존재하지 않습니다."),

    // jwt 토큰 관련 응답 4000
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "TOKEN_4001", "토큰이 전달되지 않았습니다."),
    INVALID_TOKEN_PREFIX(HttpStatus.NOT_FOUND, "TOKEN_4002", "BEARER 로 시작하지 않는 올바르지 않은 토큰 형식입니다."),
    TOKEN_ERROR(HttpStatus.NOT_FOUND, "TOKEN_4003", "토큰관련 에러가 발생했습니다."),
    REFRESHTOKEN_NOT_EQUAL(HttpStatus.NOT_FOUND, "TOKEN_4004", "refresh token이 일치하지 않습니다."),
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST,"TOKEN_4005","토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST,"TOKEN_4006","유효하지 않은 토큰입니다."),
    INVALID_OPEN_ID(HttpStatus.BAD_REQUEST,"TOKEN_4007","유효하지 않은 open id 입니다"),

    // 아이템 관련 응답 5000
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND,"ITEM_5001","아이템이 없습니다."),
    ITEM_ALREADY_BOUGHT(HttpStatus.BAD_REQUEST,"ITEM_5002","이미 구매한 아이템 입니다."),
    ITEM_NO_MONEY(HttpStatus.BAD_REQUEST,"ITEM_5003","아이템을 구매할 돈이 부족합니다."),
    ITEM_NOT_EQUIPPED(HttpStatus.BAD_REQUEST,"ITEM_5004","아이템을 착용하고 있지 않습니다."),
    ITEM_ALREADY_EQUIPPED(HttpStatus.BAD_REQUEST,"ITEM_5005","동일한 아이템을 이미 착용하고 있습니다."),
    ITEM_NOT_BUY(HttpStatus.BAD_REQUEST,"ITEM_5006","구매한 아이템이 아닙니다."),

    // 챌린지 관련 응답 6000
    CHALLENGE_FULL(HttpStatus.BAD_REQUEST,"CHANLLENGE_6001","챌린지 3개가 이미 생성되어 있어 새로운 챌린지를 생성할 수 없습니다.");

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
