package gr.cite.commons.inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class JacksonJsonProviderProvider implements Provider<JacksonJsonProvider> {

	final private ObjectMapper mapper;
	
	@Inject
	public JacksonJsonProviderProvider(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public JacksonJsonProvider get() {
		return new JacksonJsonProvider(mapper);
	}

}
