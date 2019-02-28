package org.gcube.common.clients.stubs.jaxws.handlers;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.clients.stubs.jaxws.GCoreService;

public class AuthorizationHandler extends AbstractHandler {

	/** Namespace of scope-related headers */
	public static final String TOKEN_NS = "http://gcube-system.org/namespaces/gcube-token";
	
	/** Name of the scope call header. */
	public static final String TOKEN_HEADER_NAME = "gcube-token";
	public static final QName TOKEN_QNAME = new QName(TOKEN_NS,TOKEN_HEADER_NAME);
	
	@Override
	public void handleRequest(GCoreService<?> target, SOAPHeader header, SOAPMessageContext context) throws Exception {
		String token = SecurityTokenProvider.instance.get();
		if (token!=null)
			addHeader(header,TOKEN_QNAME, token);
	}
}

