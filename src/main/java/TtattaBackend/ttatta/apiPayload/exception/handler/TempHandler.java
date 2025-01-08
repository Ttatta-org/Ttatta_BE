package TtattaBackend.ttatta.apiPayload.exception.handler;

import TtattaBackend.ttatta.apiPayload.code.BaseErrorCode;
import TtattaBackend.ttatta.apiPayload.exception.GeneralException;

public class TempHandler extends GeneralException {

    public TempHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
