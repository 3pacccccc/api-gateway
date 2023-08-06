package org.imooc.common.exception;

import org.imooc.common.enums.ResponseCode;

/**
 * @author maruimin
 * @date 2023/7/9 11:09
 */
public class NotFoundException extends BaseException{
    private static final long serialVersionUID = -5534700534739261761L;

    public NotFoundException(ResponseCode code) {
        super(code.getMessage(), code);
    }

    public NotFoundException(Throwable cause, ResponseCode code) {
        super(code.getMessage(), cause, code);
    }

}
