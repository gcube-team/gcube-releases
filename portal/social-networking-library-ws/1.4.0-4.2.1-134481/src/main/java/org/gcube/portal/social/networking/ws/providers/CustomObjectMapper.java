package org.gcube.portal.social.networking.ws.providers;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;

@Provider
/**
 * Custom mapper with property CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES to perform serialization/deserialization
 *  with snake case over camel case for json beans.
 *  TODO check https://github.com/FasterXML/jackson-docs/wiki/JacksonMixInAnnotations for applying such transformation
 *  only to some classes.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class CustomObjectMapper implements ContextResolver<ObjectMapper> {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CustomObjectMapper.class);
	private final ObjectMapper mapper;

	public CustomObjectMapper() {
		logger.debug("new ObjectMapperResolver()");
		mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
	}

	@Override
	public ObjectMapper getContext(Class<?> type) {
		logger.debug("ObjectMapperResolver.getContext(...)");
		return mapper;
	}
}