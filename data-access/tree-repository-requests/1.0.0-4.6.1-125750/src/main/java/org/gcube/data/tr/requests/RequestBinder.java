package org.gcube.data.tr.requests;

import java.io.Serializable;

import javax.xml.bind.JAXBContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Binds requests to {@link Element}s and vice versa.
 * 
 * @author Fabio Simeoni
 *
 */
public class RequestBinder implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static JAXBContext jaxb;
	
	private static Class<?>[] paramTypes = new Class<?>[]{BindSource.class};
	
	private static DocumentBuilder builder;
	
	static {	
		try {
			jaxb = JAXBContext.newInstance(paramTypes);
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		}
		catch(Exception e) {
			throw new RuntimeException("startup problem",e);
		}
	}
	
	/**
	 * Binds an {@link Element} to a {@link Request}.
	 * @param element the element
	 * @param type the {@link Class} of a {@link Request} subtype
	 * @return the instance
	 * @throws Exception if the binding fails
	 */
	public <T extends Request> T bind(Element element, Class<T> type) throws Exception {
		return type.cast(jaxb.createUnmarshaller().unmarshal(element));	
	}
	
	/**
	 * Binds a {@link Request} to an {@link Element}.
	 * @param request the request
	 * @return the element
	 */
	public Element bind(Request request) {
		try {
			Document d = builder.newDocument();
			jaxb.createMarshaller().marshal(request,d);
			return d.getDocumentElement();
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}

	}
}
