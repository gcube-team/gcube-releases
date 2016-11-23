package it.eng.rdlab.soa3.pm.connector.javaapi.impl.engine;

import it.eng.rdlab.soa3.connector.utils.SecurityManager;
import it.eng.rdlab.soa3.pm.connector.javaapi.beans.AuthZRequestBean;
import it.eng.rdlab.soa3.pm.connector.javaapi.configuration.ConfigurationManagerBuilder;
import it.eng.rdlab.soa3.pm.connector.javaapi.engine.PolicyDecisionEngine;
import it.eng.rdlab.soa3.pm.connector.javaapi.impl.utils.AttributeLoader;
import it.eng.rdlab.soa3.pm.connector.javaapi.impl.utils.Utils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.glite.authz.common.model.Action;
import org.glite.authz.common.model.Attribute;
import org.glite.authz.common.model.Request;
import org.glite.authz.common.model.Resource;
import org.glite.authz.common.model.Response;
import org.glite.authz.common.model.Result;
import org.glite.authz.common.model.Subject;
import org.glite.authz.pep.client.PEPClient;
import org.glite.authz.pep.client.config.PEPClientConfiguration;
import org.glite.authz.pep.profile.AuthorizationProfile;
import org.glite.authz.pep.profile.GridCEAuthorizationProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class PolicyDecisionEngineImpl implements PolicyDecisionEngine 
{
	private Logger logger;
	
	private PEPClient client;
	private final String 	SUBJECT_CATEGORY = "urn:oasis:names:tc:xacml:1.0:subject-category:access-subject",
							DATA_TYPE = "http://www.w3.org/2001/XMLSchema#string";
	private boolean indeterminateDecision;
	
	
	public PolicyDecisionEngineImpl() throws Exception
	{
		this.logger = LoggerFactory.getLogger(this.getClass());
		PEPClientConfiguration pepConfiguration = new PEPClientConfiguration();
		String endpoint = ConfigurationManagerBuilder.getConfigurationManager().getAuthQueryEndpoint();
		this.indeterminateDecision = ConfigurationManagerBuilder.getConfigurationManager().getIndeterminateDecision();
		this.logger.debug("Authorization query endpoint "+endpoint);
		pepConfiguration.addPEPDaemonEndpoint(endpoint);
		pepConfiguration.setTrustMaterial(SecurityManager.getInstance().getKeyStore());
		pepConfiguration.setKeyMaterial(SecurityManager.getInstance().getKeyStore(), SecurityManager.KEYSTORE_PWD);
		this.client = new PEPClient(pepConfiguration);
		logger.debug("Configuration completed");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getDecision(AuthZRequestBean bean) 
	{
		boolean decision = false;
		AuthorizationProfile profile = GridCEAuthorizationProfile.getInstance();
		Subject subject = new Subject();
		subject.setCategory(SUBJECT_CATEGORY);
		Map<String, List<String>> attributeMap = Utils.generateAttributeMap(bean.getAttributes());
		Iterator<String> keys = attributeMap.keySet().iterator();
		
		while (keys.hasNext())
		{
			String id = keys.next();
			String attributeId = AttributeLoader.getInstance().getAttribute(id);
			logger.debug("Attribute id "+attributeId);
			Attribute attribute = new Attribute();
			attribute.setDataType(DATA_TYPE);
			attribute.setId(attributeId);
			List<String> values = attributeMap.get(id);
			
			for (String value : values)
			{
				logger.debug("Attribute value "+value);
				attribute.getValues().add(value);
			}

			subject.getAttributes().add(attribute);
		}
	
		Action action = profile.createActionId(bean.getAction());
		Resource resource = profile.createResourceId(bean.getResource());
		Request request = profile.createRequest(subject, resource, action);
		
		try
		{
			Response response = client.authorize(request);
			logger.debug("Argus Response "+response);
			List<Result> results = response.getResults();
			
			if (results.size()>0)
			{
				Result result = results.get(0);
				int policyDecision = result.getDecision();
				logger.debug("Policy decision = "+policyDecision);
				decision = (Result.DECISION_PERMIT == policyDecision) || (indeterminateDecision && Result.DECISION_DENY != policyDecision);
			}
			else
			{
				logger.error("No results!!!");
			}
			
		} catch (Exception e)
		{
			logger.debug("Policy query error",e);
		}
		
		logger.debug("Permit = "+decision);
		return decision;

	}
	
//	public static void main(String[] args) throws Exception {
//		DefaultBootstrap.bootstrap();
//		ConfigurationManagerBuilder.setConfigurationManagerInstance(new TestConfigurationManager());
//		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
//		PolicyDecisionEngine engine = new PolicyDecisionEngineImpl();
//		AuthZRequestBean bean = new AuthZRequestBean();
//		//bean.getAttributes().put("urn:oasis:names:tc:xacml:1.0:subject:subject-id", "Ciro");
//		//bean.getAttributes().put("subject", "urn:d4science:roles:role-values:cirone2");
//		//bean.getAttributes().add(new it.eng.rdlab.soa3.pm.connector.javaapi.beans.Attribute("role", "urn:d4science:roles:role-values:capoccia"));
//		bean.getAttributes().add(new it.eng.rdlab.soa3.pm.connector.javaapi.beans.Attribute("role", "urn:d4science:roles:role-values:capoccia"));
//		bean.setAction("playror");
//		bean.setResource("footballlierio");
//		System.out.println(engine.getDecision(bean));
//	}

}
