package com.highestpeak.dimlight.model.params.validation;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author highestpeak
 */
public class EnumValidatorImpl implements ConstraintValidator<EnumValidator, String> {
    /**
     * 枚举检验注解
     */
    private EnumValidator enumValidator;

    @Override
    public void initialize(EnumValidator constraintAnnotation) {
        enumValidator = constraintAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean result = false;

        Class<?> cls = enumValidator.target();
        boolean ignoreEmpty = enumValidator.ignoreEmpty();

        // target为枚举，并且value有值，或者不忽视空值，才进行校验
        boolean isOkToValid = cls.isEnum() && (StringUtils.isNotEmpty(value) || !ignoreEmpty);
        if (isOkToValid) {
            Object[] objects = cls.getEnumConstants();
            try {
                Method method = cls.getMethod("name");
                for (Object obj : objects) {
                    Object code = method.invoke(obj);
                    if (value.equals(code.toString())) {
                        result = true;
                        break;
                    }
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                // noinspection ConstantConditions
                result = false;
            }
        } else {
            result = true;
        }

        return result;
    }
}
