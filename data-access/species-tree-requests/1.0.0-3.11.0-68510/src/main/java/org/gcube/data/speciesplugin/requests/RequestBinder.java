package org.gcube.data.speciesplugin.requests;

import java.io.Serializable;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * @author "Valentina Marioli valentina.marioli@isti.cnr.it"
 *
 */
public class RequestBinder implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static JAXBContext jaxb;
	private static Class<?>[] paramTypes = new Class<?>[]{SpeciesRequest.class};
	
	private synchronized JAXBContext jaxb() throws Exception {
		
		if (jaxb==null) 
			jaxb = JAXBContext.newInstance(paramTypes);
		return jaxb;
	}
	
	public SpeciesRequest bind(Element e) throws Exception {
		try {
			return (SpeciesRequest) jaxb().createUnmarshaller().unmarshal(e);	
		}
		catch(Exception ex) {
			throw new Exception("unknown request "+e.getNodeName(),ex);
		}
	}
	
	//useful for testing
	public Element bind(Object o) throws Exception {
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		jaxb().createMarshaller().marshal(o,d);
		return d.getDocumentElement();
	}
	
	public String toXML(SpeciesRequest request) throws Exception {
		StringWriter writer = new StringWriter();
		jaxb().createMarshaller().marshal(request, writer);
		return writer.toString();
	}
}
