package it.rdlab.asserts.soa3.validate;


import it.rdlab.soa3.asserts.configuration.ConfigurationBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.opensaml.common.xml.SAMLSchemaBuilder;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Conditions;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureValidator;
import org.opensaml.xml.validation.ValidationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Assertions validator - This class provides the functionality to validate the
 * signature of the assertions xmlobject
 * 
 * @author Kanchanna Ramasamy Balraj, Ciro Formisano
 * 
 */
public class Assertionsvalidation implements IAssertionValidator 
{
	private static Logger log = Logger.getLogger(Assertionsvalidation.class);
	
	static
	{
		try 
		{
			org.opensaml.DefaultBootstrap.bootstrap();
		} catch (ConfigurationException e) 
		{
			log.error("Unable to perform opensaml bootstrap", e);
		}
	}
	
	private ConfigurationBean configuration;

	/**
	 * This is a requirement for the Opensaml lib - public no arg constructor
	 */
	public Assertionsvalidation() 
	{

	}

	/* (non-Javadoc)
	 * @see it.rdlab.asserts.validate.IAssertionValidator#configure(it.rdlab.asserts.configuration.ConfigurationBean)
	 */
	@Override
	public void configure (ConfigurationBean configuration)
	{
		this.configuration = configuration;
	}
	
	/* (non-Javadoc)
	 * @see it.rdlab.asserts.validate.IAssertionValidator#validateAssertions(java.lang.String)
	 */
	@Override
	public Assertion validateAssertions(String xml)throws AssertionValidationException, ConfigurationException 
	{
		if (this.configuration == null) throw new ConfigurationException("Null configuration");		
		

			Assertion assertion = getAssertionObject(xml);
			
			if (assertion == null)
			{
				log.error("Invalid XML!!!");
			}
			else
			{
				if (!validateTimeInterval(assertion))
				{
					log.debug("Invalid time interval");
					assertion =  null;
				} else if (!validateSignature(assertion))
				{
						log.debug("Invalid signature");
						assertion = null;
				}
			}
			
			return assertion;
	}

	/* (non-Javadoc)
	 * @see it.rdlab.asserts.validate.IAssertionValidator#getAssertionObject(java.lang.String)
	 */
	@Override
	public Assertion getAssertionObject (String assertionString)
	{
		log.debug("Generating assertion object...");
		
		try
		{
			// parse xml
			log.debug("Parsing XML...");
			Document document = validateAndParse(assertionString);
			Element metadataRoot = document.getDocumentElement();
			log.debug("XML parsed");
			QName qName = new QName(metadataRoot.getNamespaceURI(),metadataRoot.getLocalName(), metadataRoot.getPrefix());
			// get an unmarshaller
			log.debug("Getting unmarshaller...");
			Unmarshaller unmarshaller = Configuration.getUnmarshallerFactory().getUnmarshaller(qName);
			log.debug("Unmarshaller get, generating assertion...");
			Assertion response = (Assertion) unmarshaller.unmarshall(metadataRoot);
			log.debug("Assertion generated");
			return response;
			
		} catch (Exception e)
		{
			log.error("Unable to generate assertion object", e);
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see it.rdlab.asserts.validate.IAssertionValidator#validateTimeInterval(org.opensaml.saml2.core.Assertion)
	 */
	@Override
	public boolean validateTimeInterval (Assertion assertion)
	{
		log.debug("Evaluating timing conditions...");
		boolean response = true;
		Conditions conditions = assertion.getConditions();
		
		if (conditions != null)
		{
			log.debug("Comparing notBefore instant...");
			DateTime currentInstant = new DateTime ();
			log.debug("Current instant = "+currentInstant);
			DateTime notBefore = conditions.getNotBefore();
			log.debug("Not before = "+notBefore);
			
			if (notBefore != null && currentInstant.isBefore(notBefore)) response = false;
			else
			{
				log.debug("Comparing notOnOrAfter instant...");
				DateTime notOnOrAfter = conditions.getNotOnOrAfter();
				log.debug("Not on or after = "+notOnOrAfter);
				
				if (notOnOrAfter != null && !currentInstant.isBefore(notOnOrAfter)) response = false;
			}
			
		}
		
		log.debug("Time interval check completed with result "+response);
		return response;
	}
	



	/* (non-Javadoc)
	 * @see it.rdlab.asserts.validate.IAssertionValidator#validateSignature(org.opensaml.saml2.core.Assertion)
	 */
	@Override
	public boolean validateSignature(Assertion assertion) throws ConfigurationException
	{
		String confValue = this.configuration.getProperty(ConfigurationBean.SIGNATURE_VALIDATION_ENABLED);
		
		if (confValue != null && confValue.equalsIgnoreCase("false"))
		{
			log.debug("Signature validation disabled: the step will be skipped");
			return true;
		}
		else
		{
			log.debug("Signature validation enabled");
			String path = this.configuration.getProperty(ConfigurationBean.PUBLIC_KEY);
			
			if (path == null) throw new ConfigurationException("Invalid ca cert path");
			
			File signatureVerificationPublicKey = new File(path);
			List<File>  signatureVerificationPublicKeyFiles = new ArrayList<File>();
			
			if (signatureVerificationPublicKey.isDirectory())
			{
				log.debug("The inserted path is a directory");
				File [] certFiles = signatureVerificationPublicKey.listFiles();
				
				for (File certFile : certFiles)
				{
					log.debug("File found "+certFile.getAbsolutePath());
					signatureVerificationPublicKeyFiles.add(certFile);
				}
			}
			else
			{
				log.debug("the inserted path is a file "+path);
				signatureVerificationPublicKeyFiles.add(signatureVerificationPublicKey);
			}
			
			// get the signature to validate from the response object
			Signature signature = assertion.getSignature();
			log.debug("Signature Reference ID: "+ signature.toString());
			boolean valid = false;
			Iterator<File> signatureVerificationPublicKeyFilesIterator = signatureVerificationPublicKeyFiles.iterator();
			
			while (signatureVerificationPublicKeyFilesIterator.hasNext() && ! valid)
			{
				try
				{
					File signatureVerificationPublicKeyFile = signatureVerificationPublicKeyFilesIterator.next();
					// get the certificate from the file
					InputStream inputStream2 = new FileInputStream(signatureVerificationPublicKeyFile);
					CertificateFactory certificateFactory = CertificateFactory.getInstance(ConfigurationBean.CERTIFICATE_TYPE);
					X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(inputStream2);
					inputStream2.close();
					// pull out the public key part of the certificate into a KeySpec
					X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(certificate.getPublicKey().getEncoded());
					// get KeyFactory object that creates key objects, specifying RSA
					KeyFactory keyFactory = KeyFactory.getInstance(ConfigurationBean.KEYFACTORY_ALG);
					log.debug("Security Provider: " + keyFactory.getProvider().toString());
					// generate public key to validate signatures
					PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
					// we have the public key
					log.debug("Public Key created");
					// create credentials
					BasicX509Credential publicCredential = new BasicX509Credential();
					// add public key value
					publicCredential.setPublicKey(publicKey);
					// create SignatureValidator
					SignatureValidator signatureValidator = new SignatureValidator(publicCredential);
		
					// validate teh signature
					try 
					{
						signatureValidator.validate(signature);
						log.debug("signature is valid");
						valid = true;
					} 
					catch (ValidationException e) 
					{
						log.debug("signature is not valid");
					}
				}catch (Exception e)
				{
					log.error("Certificate file not valid",e);
				}
			}
			
			log.debug("Validation process finished with result "+valid);
			return valid;

		}
		
	}

	/**
	 * Validates and parses the xml file input
	 * 
	 * @param xmlFile
	 *            the input assertions file that needs to be validated
	 * @return DOM representation of the XML file
	 * @throws SAXException
	 * @throws XMLParserException
	 * @throws IOException
	 */

	private Document validateAndParse(String xml) throws SAXException,XMLParserException, IOException 
	{
		Schema schema = SAMLSchemaBuilder.getSAML11Schema();
		// get parser pool manager
		BasicParserPool parserPoolManager = new BasicParserPool();
		parserPoolManager.setNamespaceAware(true);
		parserPoolManager.setIgnoreElementContentWhitespace(true);
		parserPoolManager.setSchema(schema);
		// parse the input file
		Document document = parserPoolManager.getBuilder().parse(new org.xml.sax.InputSource(new java.io.StringReader(xml)));
		// validate the xml object against SAML 1.1 schema
		javax.xml.validation.Validator validator = schema.newValidator();
		validator.validate(new DOMSource(document));
		return document;

	}


//	public static void main(String[] args) 
//	{
//
//		DateTime dateTime = new DateTime ();
//		System.out.println(dateTime.getMillis());
//		DateTime dateTime2 = new DateTime (2012,8,30,15,28,0,0,DateTimeZone.forOffsetHours(2));
//		System.out.println(dateTime2.getMillis());
//		System.out.println(dateTime2.isBefore(dateTime));
//	}



}
