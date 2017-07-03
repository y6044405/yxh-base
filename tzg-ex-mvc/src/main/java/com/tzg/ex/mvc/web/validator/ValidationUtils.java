package com.tzg.ex.mvc.web.validator;

import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.tzg.ex.core.common.Result;


public class ValidationUtils extends org.springframework.validation.ValidationUtils {

    public static Result getResult(BindingResult obj) {
        List<ObjectError> errors = obj.getAllErrors();
        Result object = new Result(errors.isEmpty());
        if (errors.isEmpty()) {
            return object;
        }
        for (ObjectError error : errors) {
            if (error instanceof FieldError) {
                FieldError fieldError = (FieldError) error;
                object.addMessage(fieldError.getObjectName() + "." + fieldError.getField(), fieldError.getDefaultMessage());
                continue;
            }
            object.addMessage(error.getObjectName(), error.getDefaultMessage());
        }
        return object;
    }
}
