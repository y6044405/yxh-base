package com.tzg.ex.mvc.web.support.bind;

import java.util.Date;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.WebRequest;

import com.tzg.ex.mvc.web.bind.support.DateTypeEditor;
import com.tzg.ex.mvc.web.model.PhoneNumberModel;

/**
 * 数据绑定初始化类
 *   可以批量注册自定义的PropertyEditor,在多个处理器中可共享
 *   
 */
public class WebBindingInitializer implements org.springframework.web.bind.support.WebBindingInitializer {
    /**
     * 初始化数据绑定
     */
    public void initBinder(WebDataBinder binder, WebRequest request) {
        binder.registerCustomEditor(Date.class, new DateTypeEditor());
        binder.registerCustomEditor(PhoneNumberModel.class, new PhoneNumberEditor());
    }
}
