package TtattaBackend.ttatta.Oidc;

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
//                    throw OtherServerUnauthorizedException.EXCEPTION;
                    throw new ExceptionHandler(ErrorStatus.ITEM_NO_MONEY); // 수정필요
                case 403:
//                    throw OtherServerForbiddenException.EXCEPTION;
                case 419:
//                    throw OtherServerExpiredTokenException.EXCEPTION;
                default:
//                    throw OtherServerBadRequestException.EXCEPTION;
            }
        }

        return FeignException.errorStatus(methodKey, response);
    }
}
