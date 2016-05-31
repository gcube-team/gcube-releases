package org.gcube.vomanagement.vomsapi;

import java.lang.reflect.Method;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.RootLogger;

/**
 * This is the root class of VOMS-API tests. It provides some utility methods for VOMS-API unit tests.
 * 
 * @author roccetti
 *
 */
public class VOMSAPITest {

	static {
		ConsoleAppender appender = new ConsoleAppender(null,
				"Test Console Appender");
		BasicConfigurator.configure(appender);
		RootLogger.getLogger("org.gcube").setLevel(Level.DEBUG);
	}

	// log the method under test in the given logger using reflection
	protected void logMethod(Method method, Logger logger) {

		// logging the method under test
		String argStr = "";
		for (Class clazz : method.getParameterTypes()) {
			argStr += clazz.getSimpleName() + ", ";
		}
		argStr = (argStr.length() > 0 ? argStr
				.substring(0, argStr.length() - 2) : argStr);
		logger.info("Test synch of "
				+ method.getDeclaringClass().getSimpleName() + "."
				+ method.getName() + "(" + argStr + "):"
				+ method.getReturnType().getSimpleName());
	}

	// creates and return an argument array with default values
	protected Object[] getArguments(Method method) throws Exception {

		// create a parameters array
		Object[] args = new Object[method.getParameterTypes().length];

		// initialize the array
		for (int i = 0; i < args.length; i++) {

			// if the argument is primitive
			if (method.getParameterTypes()[i].isPrimitive()) {
				// set the parameter value to the correponding object
				args[i] = getPrimitive(method.getParameterTypes()[i]);
			} else if (method.getParameterTypes()[i].equals(String.class)) {
				args[i] = "Role=";
			} else {
				// otherwise set the parameter value to null
				args[i] = null;				
			}
		}

		return args;

	}

	// get an Object associated to the primitive class passed as parameter
	private Object getPrimitive(Class<?> clazz) {
		if (clazz.equals(boolean.class)) {
			return new Boolean(false);
		} else {
			throw new IllegalArgumentException(
					"Cannot get a primitive value for class " + clazz.getName());
		}
	}

}
