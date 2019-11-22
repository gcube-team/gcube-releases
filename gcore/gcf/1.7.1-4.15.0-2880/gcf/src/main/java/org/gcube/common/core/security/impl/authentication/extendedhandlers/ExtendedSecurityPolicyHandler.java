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
package org.gcube.common.core.security.impl.authentication.extendedhandlers;

import org.globus.wsrf.impl.security.authentication.AuthHandler;
import org.globus.wsrf.impl.security.authentication.Constants;
import org.globus.wsrf.impl.security.authentication.DescriptorHandler;
import org.globus.wsrf.impl.security.authentication.RunAsHandler;
import org.globus.wsrf.impl.security.descriptor.ContainerSecurityConfig;
import org.globus.wsrf.impl.security.descriptor.ContainerSecurityDescriptor;
import org.globus.wsrf.impl.security.descriptor.ResourceSecurityDescriptor;
import org.globus.wsrf.impl.security.descriptor.ServiceSecurityDescriptor;

import org.globus.wsrf.config.ConfigException;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;

import java.util.List;
import java.util.Vector;

import org.globus.gsi.gssapi.GSSConstants;

import org.gridforum.jgss.ExtendedGSSContext;

import org.globus.util.I18n;

import org.ietf.jgss.GSSException;

/**
 * Handler that enforces security policy on server side.
 */
// GT3-specific handler
public class ExtendedSecurityPolicyHandler extends ExtendedDescriptorHandler {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static List<DescriptorHandler> handlers = new Vector<DescriptorHandler>();

    protected static I18n i18n = 
        I18n.getI18n("org.globus.wsrf.impl.security.authentication.errors");

    static {
        handlers.add(new AuthHandler());
        handlers.add(new RunAsHandler());
    }

    public void handle(MessageContext msgCtx, 
                       ResourceSecurityDescriptor resDesc,
                       ServiceSecurityDescriptor desc, 
                       String servicePath) 
        throws AxisFault {

        // if GSI_TRANSPORT and reject limited proxy, check if limited
        // proxy is used.  

        ExtendedGSSContext context = (ExtendedGSSContext)msgCtx
            .getProperty(Constants.TRANSPORT_SECURITY_CONTEXT);
        // if not null, it is transport security context
        if (context != null) {
            String rejectLimProxy = null;
            if (resDesc != null) {
                rejectLimProxy = resDesc.getRejectLimitedProxyState();
            } 

            if (rejectLimProxy == null) {
                if (desc != null) {
                    rejectLimProxy = desc.getRejectLimitedProxyState();
                }
            }
            
            if (rejectLimProxy == null) {
                ContainerSecurityDescriptor containerDesc = null;
                try {
                    containerDesc = 
                        ContainerSecurityConfig.getConfig()
                        .getSecurityDescriptor();
                } catch (ConfigException exp) {
                    throw AxisFault.makeFault(exp);
                }
                if (containerDesc != null) {
                    rejectLimProxy = 
                        containerDesc.getRejectLimitedProxyState();   
                }
            }

            if (rejectLimProxy != null) {
                if (rejectLimProxy.equals("true")) {
                    Boolean receivedLimited = null;
                    try {
                        receivedLimited = (Boolean)context
                            .inquireByOid(GSSConstants
                                          .RECEIVED_LIMITED_PROXY);
                    } catch (GSSException exp) {
                        AxisFault.makeFault(exp);
                    }
                    if (Boolean.TRUE.equals(receivedLimited)) {
                        throw new AxisFault(i18n
                                            .getMessage("rejectLimitedProxy"));
                    }
                }
            }
        }

        int size = handlers.size();
        DescriptorHandler handler;
        
        for (int i = 0; i < size; i++) {
            handler = (DescriptorHandler) handlers.get(i);
            handler.handle(msgCtx, resDesc, desc, servicePath);
        }
    }

}
