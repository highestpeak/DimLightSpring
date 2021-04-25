package com.highestpeak.dimlight.model.enums;

/**
 * @author highestpeak
 */
public enum TaskStatus implements ValueEnum<Integer> {
    /**
     * 未启用
     */
    DISABLE(1),

    /**
     * 启用
     */
    ENABLE(2),

    /**
     * 暂停
     */
    PAUSE(3);

    /**
     * 默认表示未启用
     * @see TaskStatus#DISABLE
     */
    public static final int DEFAULT_VALUE = 1;

    private final int value;

    TaskStatus(int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
