package cn.visolink.utils;

/**
 * Description:
 *
 * @author: 杨航行
 * @date: 2020.04.21
 */
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class JsonDealUtils extends ObjectMapper {

    public JsonDealUtils() {
        super();
        this.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {


            @Override
            public void serialize(Object arg0, JsonGenerator arg1, SerializerProvider arg2) throws IOException, JsonProcessingException {
                arg1.writeString("");
            }

        });
    }
}