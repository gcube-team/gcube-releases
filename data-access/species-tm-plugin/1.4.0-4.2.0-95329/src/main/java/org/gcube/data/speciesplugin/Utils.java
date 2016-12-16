package org.gcube.data.speciesplugin;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * @author "Valentina Marioli valentina.marioli@isti.cnr.it"
 *
 */
public class Utils {

	public static String toSchema(Class<?> clazz) {

		final StringWriter writer = new StringWriter();

		SchemaOutputResolver resolver = new SchemaOutputResolver() {

			public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
				Result result = new StreamResult(writer);
				result.setSystemId("anything");
				return result;
			}
		};

		try {
			JAXBContext ctxt = JAXBContext.newInstance(clazz);
			ctxt.generateSchema(resolver);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		return writer.toString();
	}

	public static File createTempDirectory() throws IOException
	{
		final File temp;

		temp = File.createTempFile("temp", Long.toString(System.nanoTime()));

		if(!temp.delete())
		{
			throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
		}

		if(!temp.mkdir())
		{
			throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
		}

		return temp;
	}
}
