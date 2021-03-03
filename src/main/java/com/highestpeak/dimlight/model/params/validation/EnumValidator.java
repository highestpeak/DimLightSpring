package com.highestpeak.dimlight.model.params.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author highestpeak
 * 枚举检验注解
 * 检验是否是某个枚举值的字符串
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {EnumValidatorImpl.class})
public @interface EnumValidator {
    // 默认错误消息
    String message() default "不符合选项的可选择值";

    // 分组
    Class<?>[] groups() default {};

    // 负载
    Class<? extends Payload>[] payload() default {};

    /**
     * 目标枚举类
     */
    Class<?> target() default Class.class;

    /**
     * 是否忽略空值
     */
    boolean ignoreEmpty() default true;
}
