/**
 * 
 */
package org.gcube.dataaccess.spd.havingengine.exl;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.ObjectContext;
import org.apache.commons.jexl2.ReadonlyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * Enhanced {@link JexlContext} that let retrieve XML version of the object and the original object. 
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 */
public class HavingContext<T> implements JexlContext {
	
	protected static Logger logger = LoggerFactory.getLogger(HavingContext.class);

	protected static XStream XSTREAM = new XStream() {
		protected MapperWrapper wrapMapper(MapperWrapper next) {
			return new HavingMapper(next);
		}
	};

	protected static DocumentBuilder builder;

	static {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Error initializing the builder", e);
		}
	}

	protected JexlContext context;
	protected JexlEngine engine;
	protected T wrapped;
	protected Document doc;

	public HavingContext(JexlEngine engine, T wrapped)
	{
		context = new ReadonlyContext(new ObjectContext<T>(engine, wrapped));
		this.wrapped = wrapped;
		this.engine = engine;
	}

	/**
	 * @return the engine
	 */
	public JexlEngine getEngine() {
		return engine;
	}

	/**
	 * @return the wrapped
	 */
	public T getWrapped() {
		return wrapped;
	}


	public Document getDocument() {
		if (doc == null) doc = buildDocument();
		return doc;
	}

	/**
	 * Build XML document and parse it.
	 * @return
	 */
	protected Document buildDocument()
	{
		try {
			String xml = XSTREAM.toXML(wrapped);
			logger.trace("xml {}", xml);
			ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes());
			Document doc = builder.parse(stream);
			return doc;
		} catch(Exception e)
		{
			logger.error("Error converting item to XML", e);
			throw new RuntimeException("An error occurred building the document", e);
		}
	}

	/**
	 * @param name
	 * @return
	 * @see org.apache.commons.jexl2.ObjectContext#get(java.lang.String)
	 */
	public Object get(String name) {
		return context.get(name);
	}

	/**
	 * @param name
	 * @param value
	 * @see org.apache.commons.jexl2.ObjectContext#set(java.lang.String, java.lang.Object)
	 */
	public void set(String name, Object value) {
		context.set(name, value);
	}

	/**
	 * @param name
	 * @return
	 * @see org.apache.commons.jexl2.ObjectContext#has(java.lang.String)
	 */
	public boolean has(String name) {
		return context.has(name);
	}

}
