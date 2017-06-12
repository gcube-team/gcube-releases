package org.gcube.common.resources.gcore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * Utility methods over resources.
 * 
 * @author Fabio Simeoni
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public class Resources {

	private static SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

	//known schema files embedded as classpath resources
	private static Map<Class<?>, String> schemaResources = new HashMap<Class<?>, String>();

	//cached schemas
	private static Map<Class<?>,Schema> schemas = new HashMap<Class<?>, Schema>();

	private static Map<Class<?>, JAXBContext> contexts = new HashMap<Class<?>, JAXBContext>();


	static {

		schemaFactory.setResourceResolver(new SchemaResolver());

		//populates schema map
		schemaResources.put(GenericResource.class, "schema/generic.xsd");
		schemaResources.put(ServiceEndpoint.class, "schema/endpoint.xsd");
		schemaResources.put(Software.class, "schema/service.xsd");
		schemaResources.put(GCoreEndpoint.class, "schema/gcoreendpoint.xsd");
		schemaResources.put(HostingNode.class, "schema/node.xsd");

		//note: service instances have no schema so far
	}

	// helper: resolves common type declarations in schemas
	private static class SchemaResolver implements LSResourceResolver {

		private static LSInput input;

		static {
			try {
				input = ((DOMImplementationLS) DOMImplementationRegistry.newInstance().getDOMImplementation("LS"))
						.createLSInput();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId,
				String baseURI) {

			if (systemId.equals("CommonTypeDefinitions.xsd")) {
				input.setByteStream(Resources.class.getResourceAsStream("/schema/CommonTypeDefinitions.xsd"));
			}
			return input;
		}
	};

	/**
	 * Validates a resource against its own schema, if one exists.
	 * @param resource the resource
	 * @throws IllegalArgumentException if the resource has no associated scheme
	 * @throws Exception if the resource is not valid with respect to its own schema
	 */
	public static void validate(Resource resource) throws IllegalArgumentException, Exception {

		Schema schema = schema(resource.getClass());

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(marshal(resource,out).toByteArray());

		schema.newValidator().validate(new StreamSource(in));
		

	}

	//helper
	private static synchronized Schema schema(Class<?> resourceClass) throws Exception {

		String schemaResource = schemaResources.get(resourceClass);

		if (schemaResource==null)
			throw new IllegalArgumentException("no known schema for:\n "+resourceClass);

		Schema schema = schemas.get(resourceClass);
		if (schema==null) {
			InputStream stream = Resources.class.getClassLoader().getResourceAsStream(schemaResource);
			schema = schemaFactory.newSchema(new StreamSource(stream));
			schemas.put(resourceClass,schema);
		}
		return schema;
	}

	/**
	 * Write the serialisation of a given resource to a given stream.
	 * @param resource the resource
	 * @param stream the stream in input
	 */
	public static <T extends OutputStream> T marshal(Object resource,T stream) {

		marshal(resource, new StreamResult(stream));
		return stream;
	}

	/**
	 * Write the serialisation of a given resource to a given character stream.
	 * @param resource the resource
	 * @param stream the stream in input
	 */
	public static <T extends Writer> T marshal(Object resource,T stream) {

		marshal(resource,new StreamResult(stream));
		return stream;
	}

	/**
	 * Write the serialisation of a given resource to a {@link Result}.
	 * @param resource the resource
	 * @param stream the result
	 * @return the result in input
	 */
	public static <T extends Result> T marshal(Object resource,T result) {

		if (resource instanceof Resource)
			((Resource) resource).lock.lock();
		try {
			JAXBContext context = context(resource.getClass());
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			m.marshal(resource,result);

			return result;
		}
		catch(Exception e) {
			throw new RuntimeException("serialisation error",e);
		} finally{
			if (resource instanceof Resource)
				((Resource) resource).lock.unlock();
		}

	}

	/**
	 * Prints the serialisation of a given resource to {@link System#out}
	 * @param resource the resource
	 */
	public static void print(Object resource) {

		marshal(resource,new OutputStreamWriter(System.out));
	}

	/**
	 * Creates a resource of given class from its serialisation in a given {@link Reader}.
	 * @param resourceClass the class of the resource
	 * @param reader the reader
	 * @return the resource
	 */
	public static <T> T unmarshal(Class<T> resourceClass, Reader reader) {
		return unmarshal(resourceClass,new StreamSource(reader));
	}

	/**
	 * Creates a resource of given class from its serialisation in a given {@link InputStream}.
	 * @param resourceClass the class of the resource
	 * @param stream the stream
	 * @return the resource
	 */
	public static <T> T unmarshal(Class<T> resourceClass, InputStream stream) {
		return unmarshal(resourceClass,new StreamSource(stream));
	}

	/**
	 * Creates a resource of given class from its serialisation in a given {@link Source}.
	 * @param resourceClass the class of the resource
	 * @param source the source
	 * @return the resource
	 */
	public static <T> T unmarshal(Class<T> resourceClass,Source source) {
		try {
			JAXBContext ctx = context(resourceClass);
			Unmarshaller um = ctx.createUnmarshaller();
			return resourceClass.cast(um.unmarshal(source));
		}
		catch(Exception e) {
			throw new RuntimeException("deserialisation error",e);
		}
	}

	//helper
	private static synchronized JAXBContext context(Class<?> resourceClass) throws Exception {

		JAXBContext ctx = contexts.get(resourceClass);
		if (ctx==null) {
			ctx = JAXBContext.newInstance(resourceClass);
			contexts.put(resourceClass,ctx);
		}
		return ctx;

	}
}
