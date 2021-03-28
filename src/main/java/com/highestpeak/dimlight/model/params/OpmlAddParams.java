package com.highestpeak.dimlight.model.params;

import lombok.Data;

import java.util.List;

@Data
public class OpmlAddParams {
    private String opmlString;
    private List<String> opmlUrls;
}
