package com.highestpeak.dimlight.model.pojo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.highestpeak.dimlight.utils.JacksonUtils;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author highestpeak
 */
@Getter
public class ErrorMessages {
    private List<String> messages;
    private List<String> noErrorMsg;

    public ErrorMessages() {
        messages = Lists.newArrayList();
        noErrorMsg = Lists.newArrayList();
    }

    public ErrorMessages(@NonNull List<String> messages) {
        this.messages = messages.stream().filter(StringUtils::isBlank).collect(Collectors.toList());
    }

    public ErrorMessages(String msg) {
        this();
        messages.add(msg);
    }

    public void mergeMsg(ErrorMessages msgToMerge) {
        if (mergeAble(msgToMerge)) {
            messages.addAll(msgToMerge.getMessages());
        }
    }

    public void mergeMsgWithExtraTag(ErrorMessages msgToMerge, String extraTag) {
        if (mergeAble(msgToMerge)) {
            msgToMerge.getMessages().forEach(s -> messages.add(extraTag+" "+s));
        }
    }

    public static String buildExceptionMsg(String msgOrigin, Exception e){
        return msgOrigin+"--"+e.getMessage();
    }

    public void addMsg(String msg) {
        if (StringUtils.isBlank(msg)){
            return;
        }
        messages.add(msg);
    }

    public void addNoErrorMsg(String msg){
        if (StringUtils.isBlank(msg)){
            return;
        }
        noErrorMsg.add(msg);
    }

    public boolean hasError() {
        return !messages.isEmpty();
    }

    public String toJson() {
        return JacksonUtils.errorMsgToObjectNode(this, new ObjectMapper()).toString();
    }

    private boolean mergeAble(ErrorMessages msgToMerge) {
        return msgToMerge != null && !msgToMerge.messages.isEmpty() && msgToMerge.hasMsgNotBlank();
    }

    private boolean hasMsgNotBlank() {
        if (messages == null) {
            return false;
        }
        return messages.stream().anyMatch(StringUtils::isNotBlank);
    }
}
