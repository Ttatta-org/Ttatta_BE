package TtattaBackend.ttatta.apiPayload.code;

import lombok.Builder;
import org.springframework.http.HttpStatus;

public class ErrorReasonDTO {
    private Boolean isSuccess;
    private String code;
    private String message;

    private HttpStatus httpStatus;

    public Boolean getSuccess() {
        return isSuccess;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

//    @Builder
//    public ErrorReasonDTO(Boolean isSuccess, String code, String message) {
//        this.isSuccess = isSuccess;
//        this.code = code;
//        this.message = message;
//    }

    @Builder
    public ErrorReasonDTO(Boolean isSuccess, String code, String message, HttpStatus httpStatus) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
