package org.gcube.data.tr;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

/**
 * Plugin utilities.
 * 
 * @author Fabio Simeoni
 *
 */
public class Utils {

	/**
	 * Converts a JAXB bound class to the corresponding schema.
	 * 
	 * @param clazz the class
	 * @return the schema
	 */
	public static String toSchema(Class<?> clazz) {

		final StringWriter writer = new StringWriter();

		SchemaOutputResolver resolver = new SchemaOutputResolver() {

			public Result createOutput(String namespaceUri,
					String suggestedFileName) throws IOException {
				Result result = new StreamResult(writer);
				result.setSystemId("anything");
				return result;
			}
		};

		try {
			JAXBContext ctxt = JAXBContext.newInstance(clazz);
			ctxt.generateSchema(resolver);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return writer.toString();
	}
}
