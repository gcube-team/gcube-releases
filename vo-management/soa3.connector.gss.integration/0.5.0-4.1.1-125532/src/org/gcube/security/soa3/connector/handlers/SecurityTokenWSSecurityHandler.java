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


import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;

import org.gcube.security.soa3.connector.engine.SecurityTokenWSSecurityRequestEngine;
import org.globus.wsrf.impl.security.authentication.wssec.WSSecurityBasicHandler;

// server-side handler
/**
 * 
 * Server-side Security Token handler
 * 
 * @author Ciro Formisano (ENG)
 */
public class SecurityTokenWSSecurityHandler extends WSSecurityBasicHandler {

    // server
    public boolean handleRequest(MessageContext context) {
        return handleMessage(
            (SOAPMessageContext) context, SecurityTokenWSSecurityRequestEngine.getEngine()
        );
    }

    // client
    public boolean handleResponse(MessageContext context) {
        return true;
    }
}
