package gr.cite.repo.auth.saml.messages;

import java.util.UUID;

import org.joda.time.DateTime;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeQuery;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.Subject;
import org.opensaml.saml2.core.impl.AttributeBuilder;
import org.opensaml.saml2.core.impl.AttributeQueryBuilder;
import org.opensaml.saml2.core.impl.IssuerBuilder;
import org.opensaml.saml2.core.impl.NameIDBuilder;
import org.opensaml.saml2.core.impl.SubjectBuilder;
import org.opensaml.ws.soap.soap11.Body;
import org.opensaml.ws.soap.soap11.Envelope;
import org.opensaml.ws.soap.soap11.impl.BodyBuilder;
import org.opensaml.ws.soap.soap11.impl.EnvelopeBuilder;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.io.MarshallingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SamlQueryRequest {

	private final String issuer;
	
	private static final Logger logger = LoggerFactory
			.getLogger(SamlQueryRequest.class);

	public SamlQueryRequest(String issuer) {
		super();
		this.issuer = issuer;
	}

	public String getQueryRequest(String samlNameID) throws MarshallingException, ConfigurationException {
		
		
		
		NameID nameId = new NameIDBuilder().buildObject();
		nameId.setValue(samlNameID);
		nameId.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:transient");
		nameId.setNameQualifier("http://192.168.11.97:8080/idp/shibboleth");
//		nameId.setSPNameQualifier(this.issuer);
		
		
		String id = "_" + UUID.randomUUID().toString();
		
		AttributeQuery query = new AttributeQueryBuilder().buildObject();

		query.setID(id);
		query.setSchemaLocation("schemaLocation");
		
		Subject subject = new SubjectBuilder().buildObject();
		
		subject.setNameID(nameId);
		
		query.setSubject(subject);
		query.setIssueInstant(new DateTime());
		
		Issuer issuer = new IssuerBuilder().buildObject();
		issuer.setValue(this.issuer);
		query.setIssuer(issuer);
		
		
		query.setVersion(SAMLVersion.VERSION_20);
		Attribute attr = new AttributeBuilder().buildObject();
		attr.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");
		attr.setName("urn:oid:1.3.6.1.4.1.1466.115.121.1.26");
		query.getAttributes().add(attr );

//		String requestMessage = requestToString(query);
		
		
		Envelope env = new EnvelopeBuilder().buildObject();
		Body body = new BodyBuilder().buildObject();
		body.getUnknownXMLObjects().add(query);
		env.setBody(body);
		
		String envelopeStr  = SamlMessagesHelpers.samlXmlObjToString(env);
		logger.trace("envelope : " + envelopeStr);
		
		return envelopeStr;
	}

	public static void main(String[] args) throws MarshallingException, ConfigurationException {
		String nameId = "http://localhost:9180/saml/metadata";
		
		String xml = new SamlQueryRequest("http://localhost:9180/saml/metadata").getQueryRequest(nameId);
		
		System.out.println(xml);
		
	}
}
