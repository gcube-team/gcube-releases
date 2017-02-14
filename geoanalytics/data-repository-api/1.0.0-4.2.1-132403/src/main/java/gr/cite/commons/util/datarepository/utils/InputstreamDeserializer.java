package gr.cite.commons.util.datarepository.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class InputstreamDeserializer extends JsonDeserializer<InputStream> {

	@Override
	public InputStream deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		
		return new ByteArrayInputStream(p.getBinaryValue());
	}

}
