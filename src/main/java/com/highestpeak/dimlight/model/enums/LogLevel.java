package com.highestpeak.dimlight.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogLevel {
    INFO(0, "info"),
    DEBUG(1, "debug"),
    WARNING(2, "warning"),
    ERROR(3, "error"),
    ;

    private Integer levelId;
    private String levelName;
}
