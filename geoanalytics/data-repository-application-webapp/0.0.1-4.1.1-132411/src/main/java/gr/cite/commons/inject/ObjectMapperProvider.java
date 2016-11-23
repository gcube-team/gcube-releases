package gr.cite.commons.inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.inject.Provider;

public class ObjectMapperProvider implements Provider<ObjectMapper> {
	private ObjectMapper mapper;

	public ObjectMapperProvider() {
		mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
				.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false).registerModule(new JodaModule());
	}

	@Override
	public ObjectMapper get() {
		return mapper;
	}

}
