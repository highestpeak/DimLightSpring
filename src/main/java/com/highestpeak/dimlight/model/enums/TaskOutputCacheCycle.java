package com.highestpeak.dimlight.model.enums;

import lombok.Getter;

/**
 * todo 需要返回前端
 */
@Getter
public enum TaskOutputCacheCycle {
    FOREVER(-1, "永久"),
    ONE_DAY(1, "1天"),
    TWO_DAY(2, "2天"),
    THREE_DAY(3, "3天"),
    FOUR_DAY(4, "4天"),
    FIVE_DAY(5, "5天"),
    SIX_DAY(6, "6天"),
    SEVEN_DAY(7, "7天"),
    FOURTEEN_DAY(14, "14天"),
    TWENTY_ONE_DAY(21, "21天"),
    THIRTY_DAY(30, "30天"),
    FORTY_FIVE_DAY(45, "45天"),
    NINETY_DAY(90, "90天"),
    ONE_HUNDRED_AND_EIGHTY_DAY(150, "150天"),
    THREE_HUNDRED_AND_SIXTY_FIVE_DAY(365, "365天"),
    ;

    /**
     * 默认7天
     * @see TaskOutputCacheCycle#SEVEN_DAY
     */
    public static final int DEFAULT_VALUE = 7;

    private int days;
    private String desc;

    TaskOutputCacheCycle(int days, String desc) {
        this.days = days;
        this.desc = desc;
    }
}
