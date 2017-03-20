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
package org.gcube.security.soa3.connector.handlers;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.GenericHandler;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gcube.security.soa3.connector.integration.utils.Utils;
import org.gcube.soa3.connector.common.security.MessageConstants;
import org.globus.util.I18n;
import org.w3c.dom.Element;


/**
 * 
 * Handler that manages outgoing Security Token
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class SecurityTokenHandler extends GenericHandler {

    protected static I18n i18n = 
        I18n.getI18n("org.globus.wsrf.impl.security.authorization.errors");

    private static Log log = 
        LogFactory.getLog(SecurityTokenHandler.class.getName());

    static {
        org.globus.wsrf.impl.security.authentication.wssec.GSSConfig.init();
    }

    public boolean handleRequest(MessageContext context) {
        return handleMessage((SOAPMessageContext) context);
    }

    public boolean handleResponse(MessageContext context) {
        return handleMessage((SOAPMessageContext) context);
    }

    public boolean handleMessage(SOAPMessageContext ctx) {

        Object obj = ctx.getProperty(Utils.SECURITY_TOKEN);
        
        if (obj == null) {
            log.debug("No token configured");
            return true;
        }
        else 
        {
        	log.debug("Token Object ");
        }
        
        if (!(obj instanceof Element)) {
            log.warn("Property " + Utils.SECURITY_TOKEN + " does not"
                     + " have Security Token instance");
            return true;
        }

        Element token = (Element)obj;

		
        SOAPMessage msg = ctx.getMessage();
        
        if (msg == null) 
        {
            log.debug("No message");
            return true;
        }
        
		String type = token.getAttribute(MessageConstants.ENCODING_TYPE_LABEL);
		String binaryTokenValue = token.getAttribute(MessageConstants.ID_LABEL);
		log.debug("Token type = "+type);
		log.debug("Token value = "+binaryTokenValue);

        ctx.setProperty("axis.form.optimization",
                        Boolean.TRUE);

        try
        {
        	log.debug("Generating security token");
	        SOAPElement tokenElement = generateBinaryTokenElement(type, binaryTokenValue);
	        log.debug("Security token generated");
	        msg.getSOAPHeader().addChildElement(tokenElement);
	        msg.saveChanges();
	        log.debug("Security token generated added to the message");
        } catch (Exception e)
        {
        	log.error("Unable to add the security token to the message",e);
        }
        
        ctx.setMessage(msg);

        log.debug("Exit: SecurityTokenHandler");

        return true;
    }

    public QName[] getHeaders() {
        return null;
    }
    
    
	private SOAPElement generateBinaryTokenElement (String type,String binaryTokenValue) throws SOAPException
	{
		log.debug("Generating token SOAP element");
		log.debug("Type = "+type);
		log.debug("Value = "+binaryTokenValue);
		SOAPFactory sf = SOAPFactory.newInstance();
		SOAPElement tokenElement = sf.createElement(new QName(MessageConstants.WSSE_NAMESPACE,MessageConstants.BINARY_SECURITY_TOKEN_PREFIX+":"+MessageConstants.BINARY_SECURITY_TOKEN_LABEL));
		tokenElement.setAttribute(MessageConstants.VALUE_TYPE_LABEL, type);
		tokenElement.setAttribute(MessageConstants.ENCODING_TYPE_LABEL,MessageConstants.BASE64 );
		tokenElement.setAttribute(MessageConstants.ID_LABEL, MessageConstants.SECURITY_TOKEN);
		tokenElement.setValue(binaryTokenValue);
		log.debug("Header completed");
		return tokenElement;

	}
}
