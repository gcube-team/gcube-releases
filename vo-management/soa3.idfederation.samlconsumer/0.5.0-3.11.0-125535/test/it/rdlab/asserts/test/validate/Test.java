package it.rdlab.asserts.test.validate;

import it.rdlab.asserts.configuration.ConfigurationManager;
import it.rdlab.asserts.validate.AssertionValidationException;
import it.rdlab.asserts.validate.Assertionsvalidation;
import it.rdlab.asserts.validate.IAssertionValidator;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.Subject;
import org.opensaml.saml2.core.SubjectConfirmation;
import org.opensaml.saml2.core.SubjectConfirmationData;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.Namespace;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.KeyName;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureConstants;
import org.opensaml.xml.signature.SignatureException;
import org.opensaml.xml.signature.Signer;
import org.w3c.dom.Element;

/**
 * Assertions validator - This class provides the functionality to validate the
 * signature of the assertions xmlobject
 * 
 * @author Kanchanna Ramasamy Balraj, Ciro Formisano
 * 
 */
public class Test 
{

	private static Logger log = Logger.getLogger(Test.class);



	public static void main(String[] args) 
	{
		final String START_XML = "";
		Assertion assertions;
		
		try 
		{
			// create assertions and sign them
			String signed_assertion = new Test().sign(START_XML, false);
			log.debug(signed_assertion);
			// File f = new File("/home/user810/tmp");
			// f.createNewFile();
			// BufferedWriter out = new BufferedWriter(new FileWriter(f));
			// out.write(signed_assertion);
			// out.close();

			// validate the signed assertions
			IAssertionValidator assertValidation = new Assertionsvalidation();
			assertValidation.configure(ConfigurationManager.getInstance());
			assertions = assertValidation.validateAssertions(signed_assertion);
			if (assertions != null) 
			{
				log.debug(assertions.getSubject().getNameID().getValue());
			} 
			else 
			{
				log.debug(assertions);
			}

		} 
		catch (AssertionValidationException e) 
		{
			log.error("Invalid assertion",e);
		} 
		catch (Exception e) 
		{
			log.error("Invalid assertion",e);
		}
	}


	/***************************************************************************************************************
	 * Methods below this are needed only for testing purpose to create the SAML
	 * assertions and sign them. The validation part above is the final
	 * delivered product
	 * 
	 * 
	 ******************************************************************************************************************/
	/**
	 * THIS METHOD IS JUST FOR TETSING PURPOSE TO GENERATE SAML ASSERTIONS WITH SIGNATURE
	 * Generate saml assertions and sign using the private key in the keystore
	 * 
	 * @param xml This can be an empty string
	 * @param fail
	 * @return signed saml assertions in string format
	 * @throws Exception
	 */

	public  String sign(String xml, boolean fail) throws Exception 
	{
		SOAPMessage message = createSoapMessage(xml);
		SOAPPart soapPart = message.getSOAPPart();
		Assertion assertion = (Assertion) createSamlObject(Assertion.DEFAULT_ELEMENT_NAME);
		Namespace dsns = new Namespace("http://www.w3.org/2000/09/xmldsig#","ds");
		assertion.getNamespaceManager().registerNamespace(dsns);
		Namespace xsins = new Namespace("http://www.w3.org/2001/XMLSchema-instance", "xsi");
		assertion.getNamespaceManager().registerNamespace(xsins);
		assertion.setVersion(SAMLVersion.VERSION_20);
		assertion.setID("_12374938750"); // in reality, must be unique for all
		// assertions
		assertion.setIssueInstant(new DateTime());
		Issuer issuer = (Issuer) createSamlObject(Issuer.DEFAULT_ELEMENT_NAME);
		issuer.setValue("http://some.issuer.here");
		assertion.setIssuer(issuer);
		Subject subj = (Subject) createSamlObject(Subject.DEFAULT_ELEMENT_NAME);
		assertion.setSubject(subj);
		NameID nameId = (NameID) createSamlObject(NameID.DEFAULT_ELEMENT_NAME);
		nameId.setValue("ifauser");
		subj.setNameID(nameId);
		SubjectConfirmation subjConf = (SubjectConfirmation) createSamlObject(SubjectConfirmation.DEFAULT_ELEMENT_NAME);
		subjConf.setMethod("urn:oasis:names:tc:2.0:cm:holder-of-key");
		subj.getSubjectConfirmations().add(subjConf);
		SubjectConfirmationData subjData = (SubjectConfirmationData) createSamlObject(SubjectConfirmationData.DEFAULT_ELEMENT_NAME);
		subjData.getUnknownAttributes().put(
				new QName("http://www.w3.org/2001/XMLSchema-instance", "type",
				"xsi"), "saml2:KeyInfoConfirmationDataType");
		subjConf.setSubjectConfirmationData(subjData);
		KeyInfo ki = (KeyInfo) createSamlObject(KeyInfo.DEFAULT_ELEMENT_NAME);
		subjData.getUnknownXMLObjects().add(ki);
		KeyName kn = (KeyName) createSamlObject(KeyName.DEFAULT_ELEMENT_NAME);
		kn.setValue("idpkey");
		ki.getKeyNames().add(kn);
		// AttributeStatement as = (AttributeStatement)
		// createSamlObject(AttributeStatement.DEFAULT_ELEMENT_NAME);
		// assertion.getAttributeStatements().add(as);
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		char[] password = ConfigurationManager.getInstance().getProperty("keystore.passwd").toCharArray();
		FileInputStream fis = new FileInputStream(ConfigurationManager.getInstance().getProperty("keystore.path"));
		ks.load(fis, password);
		fis.close();
		KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(ConfigurationManager.getInstance().getProperty("private.key.alias"),
				new KeyStore.PasswordProtection(ConfigurationManager.getInstance().getProperty("private.key.passwd")
						.toCharArray()));
		PrivateKey pk = pkEntry.getPrivateKey();
		X509Certificate certificate = (X509Certificate) pkEntry.getCertificate();
		BasicX509Credential credential = new BasicX509Credential();
		credential.setEntityCertificate(certificate);
		credential.setPrivateKey(pk);
		Signature signature = (Signature) createSamlObject(Signature.DEFAULT_ELEMENT_NAME);
		signature.setSigningCredential(credential);
		signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);
		signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
		// KeyInfo keyinfo = (KeyInfo)
		// createSamlObject(KeyInfo.DEFAULT_ELEMENT_NAME);
		// signature.setKeyInfo(keyinfo);
		assertion.setSignature(signature);
		// marshall Assertion Java class into XML
		MarshallerFactory marshallerFactory = Configuration
		.getMarshallerFactory();
		Marshaller marshaller = marshallerFactory.getMarshaller(assertion);
		Element assertionElement = marshaller.marshall(assertion);

		try 
		{
			Signer.signObject(signature);
		} 
		catch (SignatureException e) 
		{
			e.printStackTrace();
		}

		soapPart.appendChild(soapPart.importNode(assertionElement, true));
		String m = nodeToString(soapPart, fail);
		return m;

	}

	private String nodeToString(Node node, boolean indent) throws Exception 
	{
		StringWriter sw = new StringWriter();
		TransformerFactory tfactory = createTransformerFactory();
		Transformer transformer = tfactory.newTransformer();
		
		if (indent) 
		{
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		}
		transformer.transform(new DOMSource(node), new StreamResult(sw));
		sw.close();
		return sw.toString();

	}

	private TransformerFactory createTransformerFactory() 
	{
		TransformerFactory tfactory = TransformerFactory.newInstance();
		return tfactory;
	}

	private XMLObject createSamlObject(QName qname) 
	{
		return Configuration.getBuilderFactory().getBuilder(qname)
		.buildObject(qname);
	}

	private SOAPMessage createSoapMessage(String xml) throws SOAPException,IOException 
	{
		MessageFactory factory = MessageFactory.newInstance();
		ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());
		return factory.createMessage(null, bais);
	}

}
