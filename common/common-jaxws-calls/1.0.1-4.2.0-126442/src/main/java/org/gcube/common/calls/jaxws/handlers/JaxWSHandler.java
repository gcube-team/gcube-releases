package org.gcube.common.calls.jaxws.handlers;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.gcube.common.calls.Interceptors;
import org.gcube.common.calls.Request;
import org.gcube.common.calls.jaxws.GcubeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JaxWSHandler implements SOAPHandler<SOAPMessageContext>{

	private Logger logger = LoggerFactory.getLogger(JaxWSHandler.class);
	
	protected GcubeService<?> context;
	
	public JaxWSHandler(GcubeService<?> context) {
		super();
		this.context = context;
	}

	@Override
	public void close(MessageContext arg0) {}

	@Override
	public boolean handleFault(SOAPMessageContext arg0) {
		return true;
	}
	
	@Override
	public boolean handleMessage(SOAPMessageContext messageContext) {
		Boolean outbound = (Boolean) messageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		
		logger.trace("handling message");
		
		if (outbound){
			Request requestContext = Interceptors.executeRequestChain(null);
			
			@SuppressWarnings("unchecked")
			Map<String, List<String>> headers = (Map<String, List<String>>) messageContext.get(MessageContext.HTTP_REQUEST_HEADERS);
            
			if (headers==null) 
				headers = new HashMap<String, List<String>>();
			
			for (Entry<String, String> entry: requestContext.getHeaders())
				headers.put(entry.getKey(), Collections.singletonList(entry.getValue()));
			
			messageContext.put(MessageContext.HTTP_REQUEST_HEADERS, headers);
		}
		/*else{
			Response context = Interceptors.executeResponseChain();
		}*/
		return true;
	};

	@Override
	public Set<QName> getHeaders() {
		return null;
	}

}
