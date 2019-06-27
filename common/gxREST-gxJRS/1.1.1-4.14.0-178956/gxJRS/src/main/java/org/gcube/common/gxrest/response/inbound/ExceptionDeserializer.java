package org.gcube.common.gxrest.response.inbound;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import org.gcube.common.gxrest.response.entity.StackTraceEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Deserializer for {@link Exception}.
 * 
 * @author Manuele Simi (ISTI CNR)
 *
 */
final class ExceptionDeserializer {

	/**
	 * 
	 */
	private ExceptionDeserializer() {}
	
	private static final Logger logger = LoggerFactory.getLogger(ExceptionDeserializer.class);

	/**
	 * Deserializes the exception.
	 * 
	 * @param exceptionClass the full qualified class name of the exception to deserialize
	 * @param message the error message to associate to the exception
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected static <E extends Exception> E deserialize(String exceptionClass, String message) {
		try {
			final Class<?>[] ctorParams = {String.class};
			return (E) Class.forName(exceptionClass).getConstructor(ctorParams).newInstance(message);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			logger.error("Failed to deserialize: " + exceptionClass);
			return null;
		}
	}
	
	/**
	 * Enrich the exception with the stacktrace elements encoded with {@link StackTraceEncoder}
	 * @param exception
	 * @param joinedTrace
	 */
	protected static <E extends Exception> void addStackTrace(E exception, String joinedTrace) { 
		if (Objects.nonNull(exception))
			exception.setStackTrace(StackTraceEncoder.decodeTrace(joinedTrace));
	}
}
