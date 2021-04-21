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
 * todo: 可能需要一个error code 这样可以方便你的查找日志
 */
@Getter
public class InfoMessages {
    private List<String> errorMsg;
    private List<String> infoMsg;

    public InfoMessages() {
        this.errorMsg = Lists.newArrayList();
        this.infoMsg = Lists.newArrayList();
    }

    public InfoMessages(@NonNull List<String> errorMsg) {
        this();
        this.errorMsg.addAll(errorMsg);
    }

    /**
     * todo: 注意，这个是添加errorMsg的，但在这里写可能会有问题，毕竟infoMsg也是需要可以这么处理的
     */
    public InfoMessages(String errorMsg) {
        this();
        this.errorMsg.add(errorMsg);
    }

    public void mergeMsg(InfoMessages msgToMerge) {
        errorMsg.addAll(msgToMerge.getErrorMsg());
        infoMsg.addAll(msgToMerge.infoMsg);
    }

    /**
     * 带有特定前缀的merge
     */
    public void mergeMsgWithExtraTag(InfoMessages msgToMerge, String extraTag) {
        msgToMerge.getErrorMsg().forEach(s -> errorMsg.add(extraTag + " " + s));
    }

    /**
     * 带有异常信息的merge
     */
    public static String buildExceptionMsg(String msgOrigin, Exception e) {
        return msgOrigin + "--" + e.getMessage();
    }

    public void addErrorMsg(String msg) {
        if (StringUtils.isNotBlank(msg)) {
            errorMsg.add(msg);
        }
    }

    public void addInfoMsg(String msg) {
        if (StringUtils.isNotBlank(msg)) {
            infoMsg.add(msg);
        }
    }

    public boolean hasError() {
        return !errorMsg.isEmpty();
    }

    public String toJson() {
        return JacksonUtils.errorMsgToObjectNode(this, new ObjectMapper()).toString();
    }

}
