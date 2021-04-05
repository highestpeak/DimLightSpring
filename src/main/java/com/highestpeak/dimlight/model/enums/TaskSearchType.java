package com.highestpeak.dimlight.model.enums;

public enum TaskSearchType {
    NORMAL_LIST(1, "普通分页查找list");
    private final int num;
    private final String desc;

    TaskSearchType(int num, String desc) {
        this.num = num;
        this.desc = desc;
    }

    public int getValue() {
        return num;
    }
}
