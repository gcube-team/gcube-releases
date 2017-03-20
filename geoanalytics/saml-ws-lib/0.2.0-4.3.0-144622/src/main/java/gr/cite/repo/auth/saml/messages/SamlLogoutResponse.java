package gr.cite.repo.auth.saml.messages;

import java.io.UnsupportedEncodingException;

import org.opensaml.saml2.core.StatusCode;
import org.opensaml.saml2.core.impl.LogoutResponseImpl;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.XMLParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SamlLogoutResponse {

	private LogoutResponseImpl response;
	
	@SuppressWarnings("unused")
	private byte[] privateKey;
	
	private static final Logger logger = LoggerFactory
			.getLogger(SamlLogoutResponse.class);

	public SamlLogoutResponse(byte[] privateKey, String xml) throws ConfigurationException, XMLParserException, UnmarshallingException, UnsupportedEncodingException {
		this(privateKey, xml, true);
		logger.debug("Initialized constructor of SamlLogoutResponse");
	}
	
	public SamlLogoutResponse(byte[] privateKey, String xml, boolean isBase64) throws ConfigurationException, XMLParserException, UnmarshallingException, UnsupportedEncodingException {
		logger.debug("Initializing constructor of SamlLogoutResponse...");
		this.privateKey = privateKey;
		
		if (isBase64)
			xml = SamlMessagesHelpers.base64Decode(xml);
		this.response = getResponseObj(xml);
		logger.debug("Initialized constructor of SamlLogoutResponse");
	}
	
	protected LogoutResponseImpl getResponseObj(String xmlFile) throws ConfigurationException, XMLParserException, UnmarshallingException{
		return LogoutResponseImpl.class.cast(SamlMessagesHelpers.getResponseObj(xmlFile));
	}

	public boolean validate() throws Exception {
		logger.debug("Entering validate...");
		if (!this.response.getVersion().toString().equals("2.0")) {
			logger.error("Unsupported SAML Version.");
			throw new Exception("Unsupported SAML Version.");
		}
		if (this.response.getID().equals("")) {
			logger.error("Missing ID attribute on SAML Response.");
			throw new Exception("Missing ID attribute on SAML Response.");
		}

		if (!this.response.getStatus().getStatusCode().getValue().equalsIgnoreCase(StatusCode.SUCCESS_URI) ) {
			
			logger.warn("status code : " + this.response.getStatus().getStatusCode().getValue());
			logger.warn("status msg  : " + this.response.getStatus().getStatusCode().getStatusCode().getValue());
			
			return false;
		}
		logger.debug("Validate has ended...");
		return true;
	}

}
