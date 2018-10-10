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
package org.gcube.common.core.security.utils;

import java.io.StringReader;
import java.util.List;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.globus.util.I18n;
import org.globus.wsrf.impl.security.descriptor.AuthMethod;
import org.globus.wsrf.impl.security.descriptor.AuthMethodParserCallback;
import org.globus.wsrf.impl.security.descriptor.GSIAuthMethodParser;
import org.globus.wsrf.impl.security.descriptor.GSIAuthMethodParserImpl;
import org.globus.wsrf.impl.security.descriptor.GSISecureMsgParserImpl;
import org.globus.wsrf.impl.security.descriptor.GSITransportParserImpl;
import org.globus.wsrf.impl.security.descriptor.NoneAuthMethod;
import org.globus.wsrf.impl.security.descriptor.SecurityDescriptor;
import org.globus.wsrf.impl.security.descriptor.SecurityDescriptorException;
import org.globus.wsrf.impl.security.descriptor.util.ElementHandler;
import org.globus.wsrf.impl.security.descriptor.util.ElementParser;
import org.globus.wsrf.impl.security.descriptor.util.ElementParserException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Handles &lt;auth-method&gt; element of the security descriptor
 */
public class AuthMethodParser implements ElementHandler {

    private static I18n i18n = I18n.getI18n(SecurityDescriptor.RESOURCE);
    public static final QName QNAME =
        new QName(SecurityDescriptor.NS, "auth-method");
    protected AuthMethodParserCallback callback;

    public static final String AUTH_GSI_SEC_MSG = "GSISecureMessage";
    public static final String AUTH_GSI_SEC_CONV = "GSISecureConversation";
    public static final String AUTH_GSI_TRANSPORT = "GSITransport";
    public static final String AUTH_NONE = "none";

    public AuthMethodParser(AuthMethodParserCallback callback) {
        super();
        this.callback = callback;
    }

    public void parse(Element elem) throws ElementParserException {
        ElementParser.checkElement(elem, QNAME);

        Vector methods = new Vector();
        boolean none = false;

        for (
            Node currentChild = elem.getFirstChild(); currentChild != null;
                currentChild = currentChild.getNextSibling()
        ) {
            if (currentChild.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            String name = currentChild.getLocalName();

            if (name.equalsIgnoreCase(AUTH_GSI_SEC_CONV)) {
                if (none) {
                    throw new SecurityDescriptorException(
                        i18n.getMessage("cannotMix")
                    );
                }

                QName qName = new QName(SecurityDescriptor.NS, name);
                GSIAuthMethodParser gsiParser =
                    new GSIAuthMethodParserImpl(qName);
                gsiParser.parse((Element) currentChild);
                methods.add(gsiParser.getMethod());
            } else if (name.equalsIgnoreCase(AUTH_GSI_SEC_MSG)) {
                if (none) {
                    throw new SecurityDescriptorException(
                        i18n.getMessage("cannotMix")
                    );
                }

                QName qName = new QName(SecurityDescriptor.NS, name);
                GSIAuthMethodParser secMsgParser =
                    new GSISecureMsgParserImpl(qName);
                secMsgParser.parse((Element) currentChild);
                methods.add(secMsgParser.getMethod());
            } else if (name.equalsIgnoreCase(AUTH_GSI_TRANSPORT)) {
                if (none) {
                    throw new SecurityDescriptorException(
                        i18n.getMessage("cannotMix")
                    );
                }

                QName qName = new QName(SecurityDescriptor.NS, name);
                GSIAuthMethodParser transParser =
                    new GSITransportParserImpl(qName);
                transParser.parse((Element) currentChild);
                methods.add(transParser.getMethod());
            } else if (name.equalsIgnoreCase(AUTH_NONE)) {
                if (methods.size() > 0) {
                    throw new SecurityDescriptorException(
                        i18n.getMessage("cannotMix")
                    );
                }

                none = true;
                methods.add(NoneAuthMethod.getInstance());
            } else {
                throw new SecurityDescriptorException(
                    i18n.getMessage("badAuthMethod", name)
                );
            }
        }

        //this.callback.setAuthMethods(methods);
    }

    public static String getAuthMethodsAsString(List methods) {
        // this is not quite i18n
        StringBuffer buf = new StringBuffer();
        int size = methods.size();
        AuthMethod method;

        for (int i = 0; i < size; i++) {
            method = (AuthMethod) methods.get(i);
            buf.append(method.getName());

            if ((i + 1) < size) {
                buf.append(i18n.getMessage("methodOr"));
            }
        }

        return buf.toString();
    }
    
	public static  Element stringToElement(String nodeAsString) throws Exception 
	{
		Document xml = string2Document(nodeAsString);
		Element element = xml.getDocumentElement();
		return element;

	}
	
	public static  Document string2Document(String xmlString) 
	{
		Document doc = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();

			doc = builder.parse(new InputSource(new StringReader(xmlString)));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return doc;
	}
	
	public static void main(String[] args) throws Exception{
		String prova = "<auth-metho xmlns=\"http://www.globus.org\"><GSITransport><protection-level><integrity/><privacy/></protection-level></GSITransport></auth-metho>";
		
		Element auth = stringToElement(prova);
		AuthMethodParser parser = new AuthMethodParser(null);
		parser.parse(auth);
		
		
	}
}
