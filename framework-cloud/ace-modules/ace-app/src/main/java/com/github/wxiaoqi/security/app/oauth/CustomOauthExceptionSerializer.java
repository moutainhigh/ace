package com.github.wxiaoqi.security.app.oauth;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * @Author: Lzx
 * @Description:
 * @Date: Created in 13:57 2018/12/28
 * @Modified By:
 */
public class CustomOauthExceptionSerializer extends StdSerializer<CustomOauthException> {
	public CustomOauthExceptionSerializer() {
		super(CustomOauthException.class);
	}

	@Override
	public void serialize(CustomOauthException value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

		gen.writeStartObject();
		gen.writeStringField("error", String.valueOf(value.getHttpErrorCode()));
//		gen.writeStringField("message", value.getMessage());
        gen.writeStringField("message", "用户名或密码错误");
		gen.writeStringField("path", request.getServletPath());
		gen.writeStringField("timestamp", String.valueOf(new Date().getTime()));
		if (value.getAdditionalInformation()!=null) {
			for (Map.Entry<String, String> entry : value.getAdditionalInformation().entrySet()) {
				String key = entry.getKey();
				String add = entry.getValue();
				gen.writeStringField(key, add);
			}
		}
		gen.writeEndObject();
	}
}