package com.tzg.ex.mvc.web.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.springframework.util.StringUtils;  
  
/**
 *  
 * Filename:    ForbiddenValidator.java  
 * Description: 敏感词验证器   
 * Copyright:   Copyright (c) 2015-2018 All Rights Reserved.
 * Company:     tzg.cn Inc.
 * @author:     heyiwu 
 * @version:    1.0  
 * Create at:   2016年4月11日 下午4:54:43  
 *
 */

public class ForbiddenValidator implements ConstraintValidator<Forbidden, String> {  
  
    private String[] forbiddenWords = {"admin"};  
  
    @Override  
    public void initialize(Forbidden constraintAnnotation) {  
        //初始化，得到注解数据  
    }  
  
    @Override  
    public boolean isValid(String value, ConstraintValidatorContext context) {  
        if(StringUtils.isEmpty(value)) {  
            return true;  
        }
        for(String word : forbiddenWords) {  
            if(value.contains(word)) {  
                //传入变量,资源文件直接使用${word}
                ((ConstraintValidatorContextImpl)context).addExpressionVariable("word", word);
                return false;//验证失败  
            }  
        }  
        return true;  
    }

    public String[] getForbiddenWords() {
        return forbiddenWords;
    }

    public void setForbiddenWords(String[] forbiddenWords) {
        this.forbiddenWords = forbiddenWords;
    }  
} 