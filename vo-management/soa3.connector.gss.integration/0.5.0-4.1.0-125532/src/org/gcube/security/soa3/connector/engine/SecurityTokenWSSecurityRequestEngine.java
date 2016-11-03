/*
 * Portions of this file Copyright 1999-2005 University of Chicago
 * Portions of this file Copyright 1999-2005 The University of Southern California.
 *
 * This file or a portion of this file is licensed under the
 * terms of the Globus Toolkit Public License, found at
 * http://www.globus.org/toolkit/download/license.html.
 * If you redistribute this file, with or without
 * modifications, you must include this notice in the file.
 */
package org.gcube.security.soa3.connector.engine;

import javax.xml.rpc.handler.MessageContext;
import javax.xml.soap.SOAPHeaderElement;

import org.apache.ws.security.WSSecurityException;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.security.soa3.connector.integration.utils.Utils;
import org.gcube.soa3.connector.common.security.MessageConstants;
import org.globus.wsrf.impl.security.authentication.Constants;
import org.globus.wsrf.impl.security.authentication.wssec.WSConstants;
import org.globus.wsrf.impl.security.authentication.wssec.WSSecurityRequestEngine;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class SecurityTokenWSSecurityRequestEngine extends WSSecurityRequestEngine {

    private GCUBELog log;

    private static SecurityTokenWSSecurityRequestEngine engine;

    public synchronized static SecurityTokenWSSecurityRequestEngine getEngine() {
        if (engine == null) {
            engine = new SecurityTokenWSSecurityRequestEngine();
        }

        return engine;
    }

    private SecurityTokenWSSecurityRequestEngine ()
    {
    	super ();
    	this.log = new GCUBELog(this);
    }

	@Override
    public void processSecurityHeader(Element securityHeader, MessageContext msgCtx, String actor, SOAPHeaderElement messageIdHeader,boolean request) 
	throws Exception 
	{
		if (log.isDebugEnabled()) 
		{
			log.debug("Processing WS-Security header for '" + actor + "' actor."
					+ " request (so process timestamp) " + request);
		}
		// Get timestamp header if it needed and Normalize it.
		Element timestampElem = null;
		NodeList list = securityHeader.getChildNodes();
		int len = list.getLength();
		String ns = null;
		String ln = null;
		Node elem;
		for (int i = 0; i < len; i++) 
		{
			elem = list.item(i);
			ns = elem.getNamespaceURI();
			ln = elem.getLocalName();
			log.debug("Local Name = "+ln);
		
			if (WSConstants.SIG_NS.equalsIgnoreCase(ns) && SIG_LN.equalsIgnoreCase(ln)) 
			{
				// found SignedInfo element
				log.debug("Found signature element");
				if(handleSignatureElement((Element) elem, msgCtx, request) == false) 
				{
					//TODO: better exception
					throw new WSSecurityException(WSSecurityException.FAILED_CHECK);
				}
			} else if (WSConstants.ENC_NS.equalsIgnoreCase(ns)) 
			{
				// found _some_ element in xml encryption namespace
				log.debug("Found encryption element");
				handleEncryptionElement((Element) elem, msgCtx);
				// FIXME: shoudl ideally check for NS too, but
				// username ns is not used by WSS4J - need to confirm
			} else if (org.apache.ws.security.WSConstants.USERNAME_TOKEN_LN.equalsIgnoreCase(ln)) 
			{
				log.debug("Found user name token");
				handleUsernameElement((Element)elem, msgCtx);
			}  else if (MessageConstants.BINARY_SECURITY_TOKEN_LABEL.equalsIgnoreCase(ln)&&MessageConstants.WSSE_NAMESPACE.equalsIgnoreCase(ns)) 
			{
				log.debug("Found Security header");
				handleSecTokenElement((Element)elem, msgCtx);
			}else if (WSConstants.WSU_NS.equalsIgnoreCase(ns) && WSConstants.WS_SEC_TS_LN.equalsIgnoreCase(ln)) 
			{
				log.debug("Found timestamp element");
				timestampElem = (Element)elem;
				normalize(timestampElem);
			} else if (elem.getNodeType() == Node.ELEMENT_NODE) 
			{
				log.debug(elem.getLocalName() + " " + elem.getNamespaceURI());
			}
		}
			// At this point signature has been processed. Handle
			// timestamp, if need be.
			if (request) 
			{
				log.debug("Secure message, timestamp might be required");
				if ((constantSet(msgCtx.getProperty(Constants.GSI_SEC_MSG),Constants.SIGNATURE)) || (constantSet(msgCtx.getProperty(Constants.GSI_SEC_MSG),
						Constants.ENCRYPTION))) 
				{
					processTimestampHeader(timestampElem, msgCtx, messageIdHeader);
				}
			}
		
			boolean route = ("".equals(actor) && Boolean.TRUE.equals(msgCtx.getProperty(Constants.ROUTED)));
		
			// delete processed header
			if (!route) 
			{
				securityHeader.getParentNode().removeChild(securityHeader);
				log.debug("Security Header removed");
			} 
			else 
			{
				log.debug("Header not removed");
			}
		}
	
    private boolean constantSet(Object msgVal, Object propValue) {
        if ((msgVal != null) && (msgVal.equals(propValue)))
            return true;
        return false;
    }

    private void handleSecTokenElement (Element secTokenElement, MessageContext context) throws Exception
    {
    	if (secTokenElement != null)
    	{
    		log.debug("Inserting security token...");
    		context.setProperty(Utils.SECURITY_TOKEN, secTokenElement);
    		log.debug("Security token inserted");
    	}
    	else 
    	{
    		log.error("Security token element null");
    	}
    }

    

}
