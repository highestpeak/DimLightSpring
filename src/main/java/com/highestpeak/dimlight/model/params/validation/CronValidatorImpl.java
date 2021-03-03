package com.highestpeak.dimlight.model.params.validation;

import org.quartz.CronExpression;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author highestpeak
 */
public class CronValidatorImpl implements ConstraintValidator<CronValidator, String> {
    @Override
    public boolean isValid(String cron, ConstraintValidatorContext context) {
        // 使用 quartz 验证 cron 表达式正确性
        return CronExpression.isValidExpression(cron);
    }
}
