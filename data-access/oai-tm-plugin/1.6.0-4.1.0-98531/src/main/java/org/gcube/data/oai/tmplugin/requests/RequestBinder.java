package org.gcube.data.oai.tmplugin.requests;


import java.io.Serializable;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author "Valentina Marioli valentina.marioli@isti.cnr.it"
 *
 */
public class RequestBinder implements Serializable {

//	private static Logger log = LoggerFactory.getLogger(RequestBinder.class);
	private static final long serialVersionUID = 1L;

	private static JAXBContext jaxb;
	private static Class<?>[] paramTypes = new Class<?>[]{WrapRepositoryRequest.class,WrapSetsRequest.class};

	private synchronized JAXBContext jaxb() throws Exception {

		if (jaxb==null) 
			jaxb = JAXBContext.newInstance(paramTypes);
		return jaxb;
	}

	public <T extends Request> T bind(Element e, Class<T> type) throws Exception {
		
		try {		
			return type.cast(jaxb().createUnmarshaller().unmarshal(e));	
		}
		catch(Exception ex) {
			throw new Exception("unknown request "+e.getNodeName(),ex);
		}
	}

	//useful for testing
	public Element bind(Object request) throws Exception {
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		jaxb().createMarshaller().marshal(request,d);
		return d.getDocumentElement();

	}


	//	public Element bind(Element e) throws Exception {
	//		try {
	//			return (Element) jaxb().createUnmarshaller().unmarshal(e);	
	//		}
	//		catch(Exception ex) {
	//			throw new Exception("unknown request "+e.getNodeName(),ex);
	//		}
	//	}

	public String toXML(Object request) throws Exception {
		StringWriter writer = new StringWriter();
		jaxb().createMarshaller().marshal(request, writer);
		return writer.toString();
	}

}
