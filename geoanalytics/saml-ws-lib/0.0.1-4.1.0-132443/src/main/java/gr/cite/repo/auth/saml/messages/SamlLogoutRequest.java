package gr.cite.repo.auth.saml.messages;

import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.LogoutRequest;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.SessionIndex;
import org.opensaml.saml2.core.impl.IssuerBuilder;
import org.opensaml.saml2.core.impl.LogoutRequestBuilder;
import org.opensaml.saml2.core.impl.NameIDBuilder;
import org.opensaml.saml2.core.impl.SessionIndexBuilder;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.io.MarshallingException;

public class SamlLogoutRequest {

	private final String issuer;

	public SamlLogoutRequest(String issuer) {
		super();
		this.issuer = issuer;
	}

	public String getLogoutRequest(String samlNameID, List<String> samlSessionIds, String destinationUrl, String nameIdNameQualifier) throws MarshallingException, ConfigurationException {
		NameID nameId = new NameIDBuilder().buildObject();
		nameId.setValue(samlNameID);
		
		nameId.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:transient");
		nameId.setNameQualifier(nameIdNameQualifier);
		nameId.setSPNameQualifier(this.issuer);
		
		String id = "_" + UUID.randomUUID().toString();
		
		LogoutRequest logoutRequest = new LogoutRequestBuilder().buildObject();

		logoutRequest.setID(id);
		logoutRequest.setIssueInstant(new DateTime());
		
		logoutRequest.setDestination(destinationUrl);
		

		Issuer issuer = new IssuerBuilder().buildObject();
		issuer.setValue(this.issuer);
		logoutRequest.setIssuer(issuer);
		

		for (String samlSessionId : samlSessionIds){
			SessionIndex sessionIndexElement = new SessionIndexBuilder()
			.buildObject();
			
			sessionIndexElement.setSessionIndex(samlSessionId);
			logoutRequest.getSessionIndexes().add(sessionIndexElement);
		}

		logoutRequest.setNameID(nameId);
		
		String requestMessage = SamlMessagesHelpers.samlXmlObjToString(logoutRequest);
		
		requestMessage = SamlMessagesHelpers.base64Encode(requestMessage);
		
		return requestMessage;
	}

}
