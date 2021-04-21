package com.highestpeak.dimlight.exception;

import com.highestpeak.dimlight.model.pojo.InfoMessages;
import lombok.Getter;

@Getter
public class ErrorMsgException extends RuntimeException{
    private InfoMessages infoMessages;

    public ErrorMsgException(InfoMessages infoMessages) {
        this.infoMessages = infoMessages;
    }

    public ErrorMsgException(String message) {
        this.infoMessages = new InfoMessages();
        this.infoMessages.addErrorMsg(message);
    }
}
