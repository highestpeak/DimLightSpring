package com.highestpeak.dimlight.exception;

import com.highestpeak.dimlight.model.pojo.ErrorMessages;
import lombok.Getter;

@Getter
public class ErrorMsgException extends RuntimeException{
    private ErrorMessages errorMessages;

    public ErrorMsgException(ErrorMessages errorMessages) {
        this.errorMessages = errorMessages;
    }
}
