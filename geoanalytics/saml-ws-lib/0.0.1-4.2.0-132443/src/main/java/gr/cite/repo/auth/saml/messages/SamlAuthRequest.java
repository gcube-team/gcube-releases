package gr.cite.repo.auth.saml.messages;

import java.util.TimeZone;
import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.opensaml.common.SAMLVersion;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameIDPolicy;
import org.opensaml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml2.core.impl.AuthnContextClassRefBuilder;
import org.opensaml.saml2.core.impl.AuthnRequestBuilder;
import org.opensaml.saml2.core.impl.IssuerBuilder;
import org.opensaml.saml2.core.impl.NameIDPolicyBuilder;
import org.opensaml.saml2.core.impl.RequestedAuthnContextBuilder;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.io.MarshallingException;

public class SamlAuthRequest {

	private final String issuer;
	private final String assertionConsumerServiceUrl;

	public SamlAuthRequest(String issuer, String assertionConsumerServiceUrl) {
		super();
		this.issuer = issuer;
		this.assertionConsumerServiceUrl = assertionConsumerServiceUrl;
	}


	public String getAuthReq() throws MarshallingException, ConfigurationException {
		String id = "_" + UUID.randomUUID().toString();
		
		AuthnRequest authRequest = new AuthnRequestBuilder().buildObject();

		authRequest.setID(id);
		authRequest.setVersion(SAMLVersion.VERSION_20);
		authRequest.setIssueInstant(new DateTime().withZone(DateTimeZone
				.forTimeZone(TimeZone.getDefault())));
		authRequest
				.setProtocolBinding(SAMLConstants.SAML2_POST_BINDING_URI);

		authRequest.setAssertionConsumerServiceURL(this.assertionConsumerServiceUrl);

		Issuer issuer = new IssuerBuilder().buildObject();//new QName("urn:oasis:names:tc:SAML:2.0:assertion"));
		issuer.setValue(this.issuer);
		authRequest.setIssuer(issuer);

		NameIDPolicy nameIDPolicy = new NameIDPolicyBuilder()
				.buildObject();//new QName("urn:oasis:names:tc:SAML:2.0:protocol"));
		nameIDPolicy
				.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:transient");
		nameIDPolicy.setAllowCreate(true);

		authRequest.setNameIDPolicy(nameIDPolicy);

		RequestedAuthnContext requestedAuthnContext = new RequestedAuthnContextBuilder()
				.buildObject();//new QName("urn:oasis:names:tc:SAML:2.0:protocol"));

		requestedAuthnContext
				.setComparison(AuthnContextComparisonTypeEnumeration.EXACT);

		AuthnContextClassRef authnContextClassRef = new AuthnContextClassRefBuilder()
				.buildObject();
		authnContextClassRef
				.setAuthnContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport");

		requestedAuthnContext.getAuthnContextClassRefs().add(
				authnContextClassRef);
		authRequest.setRequestedAuthnContext(requestedAuthnContext);

		
		String requestMessage = SamlMessagesHelpers.samlXmlObjToString(authRequest);
		requestMessage = SamlMessagesHelpers.base64Encode(requestMessage);
		
		
		return requestMessage;
	}
	
}
