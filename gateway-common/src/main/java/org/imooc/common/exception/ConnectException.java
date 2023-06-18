package org.imooc.common.exception;

import lombok.Getter;
import org.imooc.common.enums.ResponseCode;

import java.io.Serial;

/**
 * @author maruimin
 * @date 2023/5/19 22:28
 */
public class ConnectException extends BaseException{
    @Serial
    private static final long serialVersionUID = -6242013681357574540L;

    @Getter
    private final String uniqueId;

    @Getter
    private final String requestUrl;

    public ConnectException(String uniqueId, String requestUrl) {
        this.uniqueId = uniqueId;
        this.requestUrl = requestUrl;
    }

    public ConnectException(Throwable cause, String uniqueId, String requestUrl, ResponseCode code) {
        super(code.getMessage(), cause, code);
        this.uniqueId = uniqueId;
        this.requestUrl = requestUrl;
    }

}
