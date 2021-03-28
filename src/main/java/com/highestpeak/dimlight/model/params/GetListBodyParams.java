package com.highestpeak.dimlight.model.params;

import lombok.Data;

import java.util.Map;

@Data
public class GetListBodyParams {
    private int pageNum;
    private int pageSize;
    private int type;
    private Map<String, Object> typeValue;
}
