package com.tzg.ex.mvc.web.support.convert;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindingResult;

import com.tzg.ex.core.common.Result;
import com.tzg.ex.mvc.web.support.SpringContextHolder;
import com.tzg.ex.mvc.web.util.SpringUtils;
import com.tzg.ex.mvc.web.validator.ValidationUtils;

public class FastJsonHttpMessageConverter extends com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter {

    @Override
    protected void writeInternal(Object obj, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        //result支持从资源文件中根据code获取message  
        if ((obj instanceof Result)&&null!=getMessageSourceSupport()) {
            ReloadableResourceBundleMessageSource resource = getMessageSourceSupport();
            Map<String, String> map = ((Result<?>) obj).getMessages();
            for (Entry<String, String> entry : map.entrySet()) {
                map.put(entry.getKey(), resource.getMessage(entry.getKey(), null, entry.getValue(), Locale.CHINA));
            }
        }
        if (!(obj instanceof BindingResult)) {
            super.writeInternal(obj, outputMessage);
            return;
        }
        //控制器支持直接返回BindingResult
        super.writeInternal(ValidationUtils.getResult((BindingResult) obj), outputMessage);
    }

    private ReloadableResourceBundleMessageSource getMessageSourceSupport() {
        try {
            return SpringContextHolder.getBean(ReloadableResourceBundleMessageSource.class);
        } catch (Exception ignore) {
        }
        return null;
    }

}
