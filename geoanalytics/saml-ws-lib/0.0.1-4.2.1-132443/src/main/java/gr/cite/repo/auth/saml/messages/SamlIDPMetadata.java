package gr.cite.repo.auth.saml.messages;

import java.util.List;
import java.util.Map;

import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.NameIDFormat;
import org.opensaml.saml2.metadata.SingleLogoutService;
import org.opensaml.saml2.metadata.SingleSignOnService;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.XMLParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


public class SamlIDPMetadata {
	public static final String AUTH_REQUEST = "urn:mace:shibboleth:1.0:profiles:AuthnRequest";
	private static final Logger logger = LoggerFactory
			.getLogger(SamlIDPMetadata.class);

	Map<String, String> slsServiceBindings;
	Map<String, String> ssoServiceBindings;
	List<String> nameIdFormats;
	String entityId;
	
	public SamlIDPMetadata(String metadataXML) throws ConfigurationException, XMLParserException, UnmarshallingException {
		super();
		
		EntityDescriptor ed = getResponseObj(metadataXML);
		
		List<SingleLogoutService> slsServices = ed.getIDPSSODescriptor(SAMLConstants.SAML20P_NS).getSingleLogoutServices();
		
		List<SingleSignOnService> ssoServices = ed.getIDPSSODescriptor(SAMLConstants.SAML20P_NS).getSingleSignOnServices();
		
		slsServiceBindings = Maps.newHashMap();
		ssoServiceBindings = Maps.newHashMap();
		
		for (SingleLogoutService sls : slsServices)
			slsServiceBindings.put(sls.getBinding(), sls.getLocation());
		
		logger.info("slsServiceBindings : " + slsServiceBindings);
		
		for (SingleSignOnService sso : ssoServices)
			ssoServiceBindings.put(sso.getBinding(), sso.getLocation());
		
		logger.info("ssoServiceBindings : " + ssoServiceBindings);
		
		
		List<NameIDFormat> nidF = ed.getIDPSSODescriptor(SAMLConstants.SAML20P_NS).getNameIDFormats();
		nameIdFormats = Lists.newArrayList();
		for (NameIDFormat nid : nidF)
			nameIdFormats.add(nid.getFormat());
		
		logger.info("nameIdFormats : " + nameIdFormats);
		
		entityId = ed.getEntityID();
		
		logger.info("entityId : " + entityId);
	}
	
	public String getSSOAuthRequestLocation(){
		return ssoServiceBindings.get(AUTH_REQUEST);
	}
	
	public String getSSOHttpPostEndpoint(){
		return ssoServiceBindings.get(SAMLConstants.SAML2_POST_BINDING_URI);
	}
	
	public String getSSOHttpPostSimpleSignEndpoint(){
		return ssoServiceBindings.get(SAMLConstants.SAML2_POST_SIMPLE_SIGN_BINDING_URI);
	}
	
	public String getSSOHttpRedirectEndpoint(){
		return ssoServiceBindings.get(SAMLConstants.SAML2_REDIRECT_BINDING_URI);
	}
	
	public String getSLSHttpPostEndpoint(){
		return slsServiceBindings.get(SAMLConstants.SAML2_POST_BINDING_URI);
	}
	
	public String getSLSSoapEndpoint(){
		return slsServiceBindings.get(SAMLConstants.SAML2_SOAP11_BINDING_URI);
	}
	
	public String getSLSHttpRedirectEndpoint(){
		return slsServiceBindings.get(SAMLConstants.SAML2_REDIRECT_BINDING_URI);
	}
	
	public List<String> getNameIdFormats(){
		return nameIdFormats;
	}
	
	public String getEntityId(){
		return entityId;
	}

	protected EntityDescriptor getResponseObj(String xmlFile) throws ConfigurationException, XMLParserException, UnmarshallingException{
		return EntityDescriptor.class.cast(SamlMessagesHelpers.getResponseObj(xmlFile));
	}
	
}
