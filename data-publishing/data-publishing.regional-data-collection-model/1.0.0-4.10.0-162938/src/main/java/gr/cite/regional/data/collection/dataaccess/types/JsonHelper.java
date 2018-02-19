package gr.cite.regional.data.collection.dataaccess.types;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JsonHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonHelper.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    private JsonHelper() {
        // hide constructor
    }

    public static String toJson(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LOGGER.error("toJson: could not convert object to json string", e);
            return null;
        }
    }

    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        try {
            return MAPPER.readValue(jsonString, clazz);
        } catch (IOException e) {
            LOGGER.error("fromJson: could not convert json string to object", e);
            return null;
        }
    }
}