package org.imooc.common.exception;

import org.imooc.common.enums.ResponseCode;

import java.io.Serial;

/**
 * @author maruimin
 * @date 2023/5/19 22:26
 */
public class ResponseException extends BaseException{
    @Serial
    private static final long serialVersionUID = 1078010673472589003L;

    public ResponseException() {
        this(ResponseCode.INTERNAL_ERROR);
    }

    public ResponseException(ResponseCode code) {
        super(code.getMessage(), code);
    }

    public ResponseException(Throwable cause, ResponseCode code) {
        super(code.getMessage(), cause, code);
        this.code = code;
    }

}
