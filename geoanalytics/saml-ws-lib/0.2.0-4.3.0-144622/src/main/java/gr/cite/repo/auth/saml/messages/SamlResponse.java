package gr.cite.repo.auth.saml.messages;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Map;

import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.AuthnStatement;
import org.opensaml.saml2.core.EncryptedAssertion;
import org.opensaml.saml2.core.StatusCode;
import org.opensaml.saml2.core.impl.ResponseImpl;
import org.opensaml.saml2.encryption.Decrypter;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.encryption.DecryptionException;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.schema.impl.XSStringImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class SamlResponse {

	private ResponseImpl response;
	private byte[] privateKey;
	private Map<String, Object> attributes;
	private String nameId;
	private List<String> sessionIds;
	
	private static final Logger logger = LoggerFactory
			.getLogger(SamlResponse.class);

	
	public SamlResponse(byte[] privateKey, String xml) throws ConfigurationException, XMLParserException, UnmarshallingException, UnsupportedEncodingException {
		this(privateKey, xml, true);
		logger.debug("Initialized constructor of SamlResponse");
	}
	
	public SamlResponse(byte[] privateKey, String xml, boolean isBase64) throws ConfigurationException, XMLParserException, UnmarshallingException, UnsupportedEncodingException {
		logger.debug("Initializing SamlResponse...");
		this.privateKey = privateKey;
		
		if (isBase64)
			xml = SamlMessagesHelpers.base64Decode(xml);
		this.response = getResponseObj(xml);
		logger.debug("Initialized  SamlResponse");
	}
	
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
	public String getNameId() {
		return nameId;
	}

	public List<String> getSessionIds() {
		return sessionIds;
	}

	public void validate() throws Exception {
		
		// Security Checks
		// Check SAML version
		if (!this.response.getVersion().toString().equals("2.0")) {
			logger.error("Unsupported SAML Version.");
			throw new Exception("Unsupported SAML Version.");
		}

		// Check ID in the response
		if (this.response.getID().equals("")) {
			logger.error("Missing ID attribute on SAML Response.");
			throw new Exception("Missing ID attribute on SAML Response.");
		}

		if (!this.response.getStatus().getStatusCode().getValue().equalsIgnoreCase(StatusCode.SUCCESS_URI) ) {
			logger.error("SAML Response must contain 1 Assertion.");
			throw new Exception("SAML Response must contain 1 Assertion.");
		}
		
		//TODO: check validation period

		try {
			this.setAttributes(initAttributes());
		} catch (Exception e){
			logger.error("error setting the attributes", e);
			throw new Exception("error setting the attributes", e);
		}
		
	}

	private Map<String, Object> initAttributes() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException, DecryptionException {
		Map<String, Object> attributes = Maps.newHashMap();
		
		Decrypter decrypter = SamlMessagesHelpers.getDecrypter(this.privateKey);
		 
		// Decrypt the assertion.
		List<Assertion> decryptedAssertions = Lists.newArrayList();
		// this.response.get
		 
		logger.info("found : " + this.response.getEncryptedAssertions().size() + " encrypted assertions");
		for (EncryptedAssertion as : this.response.getEncryptedAssertions()){
			Assertion decrypted = decrypter.decrypt(as);
			
			nameId = decrypted.getSubject().getNameID().getValue();
			
			sessionIds = Lists.newArrayList();
			for (AuthnStatement authStatement : decrypted.getAuthnStatements())
				sessionIds.add(authStatement.getSessionIndex());
			
			for (AttributeStatement attributeStatement : decrypted.getAttributeStatements()){
				
				for (Attribute attribute : attributeStatement.getAttributes()){
					
					logger.info("name   : " + attribute.getName());
					logger.info("fname  : " + attribute.getFriendlyName());
					
					for (XMLObject val : attribute.getAttributeValues()){
						if (val instanceof XSStringImpl){
							XSStringImpl strVal = (XSStringImpl)val;
							
							logger.info(" ~> val : " +strVal.getValue());
							
							attributes.put(attribute.getFriendlyName(), strVal.getValue());
							
						}
					}
				}
			}
			decryptedAssertions.add(decrypted);
		}
		
		return attributes;
	}

	protected ResponseImpl getResponseObj(String xmlFile) throws ConfigurationException, XMLParserException, UnmarshallingException{
		return ResponseImpl.class.cast(SamlMessagesHelpers.getResponseObj(xmlFile));
	}
	
}
