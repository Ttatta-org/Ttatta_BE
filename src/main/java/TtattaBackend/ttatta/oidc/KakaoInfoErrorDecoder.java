package TtattaBackend.ttatta.oidc;

import TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus;
import TtattaBackend.ttatta.apiPayload.exception.handler.ExceptionHandler;
import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class KakaoInfoErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() >= 400) {
            switch (response.status()) {
                case 401:
                    throw new ExceptionHandler(ErrorStatus.INVALID_TOKEN);
//                    throw OtherServerUnauthorizedException.EXCEPTION;
                case 403:
                    throw new ExceptionHandler(ErrorStatus.INVALID_TOKEN);
//                    throw OtherServerForbiddenException.EXCEPTION;
                case 419:
                    throw new ExceptionHandler(ErrorStatus.INVALID_TOKEN);
//                    throw OtherServerExpiredTokenException.EXCEPTION;
                default:
                    throw new ExceptionHandler(ErrorStatus.INVALID_TOKEN);
//                    throw OtherServerBadRequestException.EXCEPTION;
            }
        }
        return FeignException.errorStatus(methodKey, response);
    }
}