package gr.cite.commons.util.datarepository.utils;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class InputstreamSerializer extends JsonSerializer<InputStream> {

	@Override
	public void serialize(InputStream value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException {
		gen.writeBinary(value, -1);
	}

}
