package TtattaBackend.ttatta.apiPayload.exception.handler;

import TtattaBackend.ttatta.apiPayload.code.BaseErrorCode;
import TtattaBackend.ttatta.apiPayload.exception.GeneralException;

public class ExceptionHandler extends GeneralException {
    public ExceptionHandler(BaseErrorCode code) {
        super(code);
    }
}
