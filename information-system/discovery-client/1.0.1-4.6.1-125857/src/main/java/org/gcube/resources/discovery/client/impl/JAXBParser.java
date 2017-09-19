package org.gcube.resources.discovery.client.impl;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.gcube.resources.discovery.client.api.ResultParser;

/**
 * A {@link ResultParser} that parses query results into JAXB annotated classes.
 * 
 * @author Fabio Simeoni
 *
 * @param <R> the type of parsed results
 */
public class JAXBParser<R> implements ResultParser<R> {

	private final Class<R> type;
	private final Unmarshaller um;
	
	//caches contexts per type
	private static Map<Class<?>,JAXBContext> ctxts = new HashMap<Class<?>, JAXBContext>();
	
	/**
	 * Creates an instance with a JAXB-annotated class.
	 * @param type the class
	 */
	public JAXBParser(Class<R> type) {
		
		this.type=type;
		
		//lazily create unmarshaller for this type
		try {
			JAXBContext ctx = ctxts.get(type);
			if (ctx==null) {
				ctx = JAXBContext.newInstance(type);
				ctxts.put(type,ctx);
			}
			this.um=ctx.createUnmarshaller();
		}
		catch(Exception e) {
			throw new RuntimeException("error with parser",e);
		}
	}
	
	public R parse(String result) throws Exception {
		return type.cast(um.unmarshal(new StringReader(result)));
		
	}
}
