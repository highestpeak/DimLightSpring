package com.highestpeak.dimlight.model.pojo;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author highestpeak
 */
@Getter
public class ErrorMessages {
    private List<String> messages;

    public ErrorMessages() {
        messages = new ArrayList<>();
    }

    public ErrorMessages(List<String> messages) {
        this.messages = messages;
    }

    public void mergeMsg(ErrorMessages msgToMerge) {
        if (msgToMerge != null) {
            messages.addAll(msgToMerge.getMessages());
        }
    }

    public static String buildExceptionMsg(String msgOrigin, Exception e){
        return msgOrigin+"--"+e.getMessage();
    }

    public void addMsg(String msg) {
        messages.add(msg);
    }

    public boolean hasNoError() {
        return messages.isEmpty();
    }
}
