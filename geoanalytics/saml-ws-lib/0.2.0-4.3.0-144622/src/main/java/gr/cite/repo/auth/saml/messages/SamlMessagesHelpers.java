package gr.cite.repo.auth.saml.messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.encryption.Decrypter;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.encryption.InlineEncryptedKeyResolver;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.security.keyinfo.StaticKeyInfoCredentialResolver;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.util.Base64;
import org.opensaml.xml.util.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SamlMessagesHelpers {
	
	private static final Logger logger = LoggerFactory
			.getLogger(SamlMessagesHelpers.class);
					
	static 
	{
		try {
			DefaultBootstrap.bootstrap();
		} catch (ConfigurationException e) {
			logger.error("error while initializing default bootstrap", e);
		}
	}
	
	public static String samlXmlObjToString(XMLObject obj) throws MarshallingException{
		 
		MarshallerFactory marshallerFactory = Configuration.getMarshallerFactory();
		Marshaller marshaller = marshallerFactory.getMarshaller(obj);
		
		Element authDOM = marshaller.marshall(obj);
		
		StringWriter requestWriter = new StringWriter();
        XMLHelper.writeNode(authDOM, requestWriter);
        String requestMessage = requestWriter.toString().trim();
        
		return requestMessage; 
	}
	
	public static String base64Encode(String input){
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		Deflater deflater = new Deflater(Deflater.DEFLATED, true);
		DeflaterOutputStream deflaterStream = new DeflaterOutputStream(
				bytesOut, deflater);
		try {
			deflaterStream.write(input.getBytes("UTF-8"));
			deflaterStream.finish();
		} catch (IOException e) {
			logger.error("Error in base64Encoding",e);
			throw new RuntimeException(e);
		}
		return Base64.encodeBytes(bytesOut.toByteArray(),
				Base64.DONT_BREAK_LINES).trim();
	}
	
	public static String base64Decode(String input) throws UnsupportedEncodingException{
		org.apache.commons.codec.binary.Base64 base64 = new org.apache.commons.codec.binary.Base64();
		byte[] decodedB = base64.decode(input.getBytes("UTF-8"));
		String decodedS = new String(decodedB).trim();

		return decodedS;
	}
	
	
	public static XMLObject getResponseObj(String xmlFile) throws ConfigurationException, XMLParserException, UnmarshallingException{
		// Get parser pool manager
		BasicParserPool ppMgr = new BasicParserPool();
		ppMgr.setNamespaceAware(true);
		
		Document inCommonMDDoc = ppMgr.parse(new ByteArrayInputStream(xmlFile.getBytes()));
		Element metadataRoot = inCommonMDDoc.getDocumentElement();
		
		UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
		Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(metadataRoot);
		
		return unmarshaller.unmarshall(metadataRoot);
	}
	
	public static Decrypter getDecrypter(byte[] privateKeyString) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException{
		// Create the private key.
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyString);
		
		//PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyString.getBytes());
		RSAPrivateKey privateKey = (RSAPrivateKey)KeyFactory.getInstance("RSA").generatePrivate(privateKeySpec);

		// Create the credentials.
		BasicX509Credential decryptionCredential = new BasicX509Credential();
		decryptionCredential.setPrivateKey(privateKey);
		 
		// Create a decrypter.
		Decrypter decrypter = new Decrypter(null, new StaticKeyInfoCredentialResolver(decryptionCredential), new InlineEncryptedKeyResolver());
		
		return decrypter;
	}
}
