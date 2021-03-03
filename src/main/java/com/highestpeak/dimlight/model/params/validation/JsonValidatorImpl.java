package com.highestpeak.dimlight.model.params.validation;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.IOException;

/**
 * @author highestpeak
 */
public class JsonValidatorImpl implements ConstraintValidator<JsonValidator, String> {
    @Override
    public boolean isValid(String jsonInString, ConstraintValidatorContext context) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(jsonInString);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
