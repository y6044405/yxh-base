package com.tzg.ex.mvc.web.support.bind;

import java.beans.PropertyEditorSupport;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import com.tzg.ex.mvc.web.model.PhoneNumberModel;

/**
 * 电话号码编辑器
 */
public class PhoneNumberEditor extends PropertyEditorSupport {

    Pattern pattern = Pattern.compile("^(\\d{3,4})-(\\d{7,8})$");

    /**
     * 将String转换为PhoneNumberModel模型对象
     */
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (text == null || !StringUtils.hasLength(text)) {
            setValue(null);
        }
        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            PhoneNumberModel phoneNumber = new PhoneNumberModel();
            phoneNumber.setAreaCode(matcher.group(1));
            phoneNumber.setPhoneNumber(matcher.group(2));
            setValue(phoneNumber);
            return;
        }
        throw new IllegalArgumentException(String.format("类型转换失败，需要格式[010-12345678]，但格式是[%s]", text));
    }

    /**
     * 将PhoneNumberModel模型对象转换为String
     */
    @Override
    public String getAsText() {
        PhoneNumberModel phoneNumber = ((PhoneNumberModel) getValue());
        return phoneNumber == null ? "" : phoneNumber.getAreaCode() + "-" + phoneNumber.getPhoneNumber();
    }

}
