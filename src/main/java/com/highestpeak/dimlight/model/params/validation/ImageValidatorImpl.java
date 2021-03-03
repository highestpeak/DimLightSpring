package com.highestpeak.dimlight.model.params.validation;

import org.hibernate.validator.constraintvalidators.RegexpURLValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashMap;
import java.util.Map;

/**
 * @author highestpeak
 */
public class ImageValidatorImpl implements ConstraintValidator<ImageValidator, String> {
    public static final Map<String,PrefixValid> PREFIX = new HashMap<String, PrefixValid>(){{
        put("TEXT",new TextPrefixValid());
        put("URL",new URLPrefixValid());
        put("BOOTSTRAP", (value, context) -> true); // 这个不做校验
        put("FONTAWESOME", (value, context) -> true); // 这个不做校验
    }};
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        int splitIndex = value.indexOf(":");

        String prefix = value.substring(0,splitIndex);
        if (!PREFIX.containsKey(prefix)){
            return false;
        }

        String typeValue = value.substring(splitIndex+1);
        return PREFIX.get(prefix).isValid(typeValue,context);
    }

    public interface PrefixValid{
        /**
         * 判断解析后的值是否符合要求
         * @param value value
         * @param context need context to use existed validator
         * @return is valid result
         */
        boolean isValid(String value, ConstraintValidatorContext context);
    }

    public static class TextPrefixValid implements PrefixValid{
        public static final int MAX_TEXT_LEN = 4;

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            return value.length()<=MAX_TEXT_LEN;
        }
    }

    @SuppressWarnings("AlibabaClassNamingShouldBeCamel")
    public static class URLPrefixValid implements PrefixValid{
        public static RegexpURLValidator validator = new RegexpURLValidator();

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            return validator.isValid(value,context);
        }
    }
}
