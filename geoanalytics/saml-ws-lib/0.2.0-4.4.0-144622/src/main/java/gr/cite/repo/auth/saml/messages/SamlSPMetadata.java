package gr.cite.repo.auth.saml.messages;

import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml2.metadata.SingleLogoutService;
import org.opensaml.saml2.metadata.impl.AssertionConsumerServiceBuilder;
import org.opensaml.saml2.metadata.impl.EntityDescriptorBuilder;
import org.opensaml.saml2.metadata.impl.KeyDescriptorBuilder;
import org.opensaml.saml2.metadata.impl.SPSSODescriptorBuilder;
import org.opensaml.saml2.metadata.impl.SingleLogoutServiceBuilder;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.security.credential.UsageType;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.X509Certificate;
import org.opensaml.xml.signature.X509Data;
import org.opensaml.xml.signature.impl.KeyInfoBuilder;
import org.opensaml.xml.signature.impl.X509CertificateBuilder;
import org.opensaml.xml.signature.impl.X509DataBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SamlSPMetadata {

	private final String cert;
	private final String spHost;
	private final String entityId;
	
	private static final Logger logger = LoggerFactory
			.getLogger(SamlSPMetadata.class);

	
	public SamlSPMetadata(String entityId, String cert, String spHost) {
		super();
		logger.debug("Initializing SamlSPMetadata...");
		this.entityId = entityId;
		this.cert = cert;
		this.spHost = spHost;
		logger.debug("Initialized SamlSPMetadata");
	}

	public String getMetadata() throws MarshallingException{
		
		logger.debug("Getting Metadata...");
		
		EntityDescriptor entityDescriptor = new EntityDescriptorBuilder().buildObject();
		
		
		entityDescriptor.setEntityID(this.entityId);
		
		SPSSODescriptor spssoDescriptor = new SPSSODescriptorBuilder().buildObject();
		spssoDescriptor.addSupportedProtocol(SAMLConstants.SAML20P_NS);
		
		KeyDescriptor keyDescriptor = new KeyDescriptorBuilder().buildObject();
		keyDescriptor.setUse(UsageType.SIGNING);
		
		KeyInfo keyInfo = new KeyInfoBuilder().buildObject();
		X509Data x509Data = new X509DataBuilder().buildObject();
		
		
		X509Certificate cert = new X509CertificateBuilder().buildObject();
		cert.setValue(this.cert);
		
		x509Data.getX509Certificates().add(cert );
		
		keyInfo.getX509Datas().add(x509Data);
		
		keyDescriptor.setKeyInfo(keyInfo);
		
		
		
		KeyDescriptor keyDescriptor2 = new KeyDescriptorBuilder().buildObject();
		keyDescriptor2.setUse(UsageType.ENCRYPTION);
		
		KeyInfo keyInfo2 = new KeyInfoBuilder().buildObject();
		X509Data x509Data2 = new X509DataBuilder().buildObject();
		
		
		X509Certificate cert2 = new X509CertificateBuilder().buildObject();
		cert2.setValue(this.cert);
		
		x509Data2.getX509Certificates().add(cert2);
		
		keyInfo2.getX509Datas().add(x509Data2);
		
		keyDescriptor2.setKeyInfo(keyInfo2);
		
		spssoDescriptor.getKeyDescriptors().add(keyDescriptor);
		spssoDescriptor.getKeyDescriptors().add(keyDescriptor2);
		
		
		AssertionConsumerService assertionConsumerService1 = new AssertionConsumerServiceBuilder().buildObject();
		assertionConsumerService1.setIsDefault(true);
		assertionConsumerService1.setIndex(0);
		assertionConsumerService1.setBinding(SAMLConstants.SAML2_POST_BINDING_URI);
		assertionConsumerService1.setLocation(this.spHost + "/saml/consumer");
		
//		AssertionConsumerService assertionConsumerService2 = new AssertionConsumerServiceBuilder().buildObject();
//		assertionConsumerService2.setIsDefault(true);
//		assertionConsumerService2.setIndex(1);
//		assertionConsumerService2.setBinding("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST");
//		assertionConsumerService2.setLocation(SP_HOST + "/saml/consumer");
		
		SingleLogoutService sls = new SingleLogoutServiceBuilder().buildObject();
		sls.setBinding(SAMLConstants.SAML2_POST_BINDING_URI);
		sls.setLocation(this.spHost + "/saml/logoutConsumer");
		
		spssoDescriptor.getAssertionConsumerServices().add(assertionConsumerService1);
//		spssoDescriptor.getAssertionConsumerServices().add(assertionConsumerService2);
		
		spssoDescriptor.getSingleLogoutServices().add(sls);
		
		
		
		entityDescriptor.getRoleDescriptors().add(spssoDescriptor);
		
		String metadata = SamlMessagesHelpers.samlXmlObjToString(entityDescriptor);
		
		logger.trace("metadata : " + metadata);
		
		logger.debug("Got Metadata");
		
		return metadata;
	}
}
