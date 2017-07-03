package com.tzg.ex.mvc.web.support.convert;

import org.springframework.core.convert.converter.Converter;

import com.tzg.ex.mvc.web.model.PhoneNumberModel;

//PhoneNumberModel------>String
public class PhoneNumberToStringConverter implements Converter<PhoneNumberModel, String> {

	@Override
	public String convert(PhoneNumberModel source) {
		
		if(source == null) {
			return "";
		}
		
		return new StringBuilder()
					.append(source.getAreaCode()).append("-")
					.append(source.getPhoneNumber()).toString();

	}

}
