package org.gcube.common.clients.stubs.jaxws.handlers;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.gcube.common.clients.stubs.jaxws.GCoreService;

/**
 * A {@link CallHandler} that transforms WS-Addressing information in outgoing calls into MemberSubmission form.
 * @author Fabio Simeoni
 *
 */
public class LegacyWSAddressingHandler extends AbstractHandler {
	
	//helper: adapts ws-addressing headers to member submission's. brutal but there is no support for member submission in
	//jdk 1.6
	@Override
	public void handleRequest(GCoreService<?> target, SOAPHeader header, SOAPMessageContext context) throws Exception {
		Iterator<?> it = header.examineAllHeaderElements();
		while (it.hasNext()) {
			SOAPHeaderElement e = (SOAPHeaderElement) it.next();
			if (e.getElementQName().getNamespaceURI().equals("http://www.w3.org/2005/08/addressing")) {
				e.detachNode();
				addHeader(header,new QName("http://schemas.xmlsoap.org/ws/2004/03/addressing",e.getElementQName().getLocalPart()), e.getTextContent());
			}
				
		}
	}
}
