package it.eng.rdlab.soa3.connector.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * Utilities to manage soap messages
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class SoapUtils 
{
	/**
	 * 
	 * Translates a soap message object into string
	 * 
	 * @param soapMessage a SOAP message object
	 * @return a String representing the message
	 * @throws Exception if something goes wrong
	 */
	public static String soapMessage2String(SOAPMessage soapMessage) throws Exception 
	{
        ByteArrayOutputStream  baos=new ByteArrayOutputStream ();
        
        soapMessage.writeTo(baos);
        String soapMessageString=new String(baos.toByteArray());
        //String soapMessageString=new String(baos.toString("UTF-8"));

		return soapMessageString;
	}
	
	/**
	 * 
	 * Generates a SOAP Message object starting from a dom object
	 * 
	 * @param document the dom object
	 * @return the SOAP message object
	 * @throws SOAPException if it is impossible to generate the SOAP object
	 * @throws Exception if something goes wrong
	 */
	public static SOAPMessage generateSoapMessage (Document document) throws Exception 
	{
		Logger logger = LoggerFactory.getLogger(SoapUtils.class);
		logger.debug("Generating soap message");
		Element queryElement = document.getDocumentElement();
		SOAPMessage soapmsg  =MessageFactory.newInstance().createMessage();
		SOAPFactory sf = SOAPFactory.newInstance();
		SOAPElement bodyelement = sf.createElement(queryElement);
		logger.debug("Query Element added");
		soapmsg.getSOAPBody().addChildElement(bodyelement);
		soapmsg.saveChanges();
		logger.debug("Message completed");
		String soapString = SoapUtils.soapMessage2String(soapmsg);
		logger.debug("Message = "+soapString);
		return soapmsg;
	}
	
	/**
	 * 
	 * Executes a SOAP call
	 * 
	 * @param message the message
	 * @param url the target url
	 * @return the response message
	 * @throws UnsupportedOperationException
	 * @throws SOAPException
	 */
	public static  SOAPMessage performCall (SOAPMessage message, String url) throws UnsupportedOperationException, SOAPException 
	{
		return performCall(message, url, true);
	}

	
	/**
	 * 
	 * Executes a SOAP call
	 * 
	 * @param message the message
	 * @param url the target url
	 * @param useSecurityManager true if the securitymanager must be used, false otherwise
	 * @return the response message
	 * @throws UnsupportedOperationException
	 * @throws SOAPException
	 */
	public static  SOAPMessage performCall (SOAPMessage message, String url,boolean useSecurityManager) throws UnsupportedOperationException, SOAPException 
	{

		Logger logger = LoggerFactory.getLogger(SoapUtils.class);
		logger.debug("url = "+url);
		SOAPConnection conn  = SOAPConnectionFactory.newInstance().createConnection();
		return conn.call(message, url);

	}
	
	public static void main(String[] args) throws Exception{
		CertificateFactory factory = CertificateFactory.getInstance("X.509");
		FileInputStream is = new FileInputStream("/etc/argus-security/hostcert.pem");
		X509Certificate cert = (X509Certificate)factory.generateCertificate(is);
		System.out.println(cert.getIssuerDN());
	}

}
