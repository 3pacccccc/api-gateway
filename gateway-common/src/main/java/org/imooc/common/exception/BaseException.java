package org.imooc.common.exception;

import org.imooc.common.enums.ResponseCode;

import java.io.Serial;

/**
 * @author maruimin
 * @date 2023/5/19 22:26
 */
public class BaseException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 673101647138887949L;


    public BaseException() {
    }

    protected ResponseCode code;

    public BaseException(String message, ResponseCode code) {
        super(message);
        this.code = code;
    }

    public BaseException(String message, Throwable cause, ResponseCode code) {
        super(message, cause);
        this.code = code;
    }

    public BaseException(ResponseCode code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public BaseException(String message, Throwable cause,
                         boolean enableSuppression, boolean writableStackTrace, ResponseCode code) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }

    public ResponseCode getCode() {
        return code;
    }


}
