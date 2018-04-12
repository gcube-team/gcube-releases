package org.gcube.data.analysis.statisticalmanager.experimentspace.genericresources;

import static org.gcube.resources.discovery.icclient.ICFactory.client;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScope.Type;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.statisticalmanager.Configuration;
import org.gcube.data.analysis.statisticalmanager.ServiceContext;
import org.gcube.data.analysis.statisticalmanager.experimentspace.FactoryComputationParameter;
import org.gcube.data.analysis.statisticalmanager.stubs.SMAlgorithm;
import org.gcube.data.analysis.statisticalmanager.stubs.SMParameter;
import org.gcube.data.analysis.statisticalmanager.stubs.SMParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.informationsystem.publisher.ScopedPublisher;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericRGenerator {
	private static Logger logger = LoggerFactory.getLogger(GenericRGenerator.class);
	

//	private static int call = 0;
//	private static Lock l = new ReentrantLock();;


	public static int consecutiveCall = 0;
//	private static ArrayList<String> algoritmJustPublish = new ArrayList<String>();

	public GenericRGenerator() {

	}

	
	/**
	 * Create an xml representing a GR
	 * 
	 * @param a
	 * @param name
	 * @param description
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	protected StringBuilder createGR(SMAlgorithm a, String name,String description, List<StatisticalType> parameters)throws Exception {
		// logger.debug("inside create GR");
		StringBuilder xml = new StringBuilder();
		ArrayList<SMParameter> listParameters = new ArrayList<SMParameter>();
		xml.append("<Resource version=\"0.4.x\">");
		xml.append("<Type>GenericResource</Type>");
		xml.append("<Profile>");
		xml.append("<SecondaryType>");
		xml.append(Configuration.getProperty(Configuration.GR_SECONDARY_TYPE));
		xml.append("</SecondaryType>");
		xml.append("<Name>");
		xml.append(name);
		xml.append("</Name>");
		xml.append("<Description>");
		xml.append(description);
		xml.append("</Description>");
		xml.append("<Body>");
		xml.append("<category>");
		xml.append(a.getCategory());
		xml.append("</category>");
		for (StatisticalType param : parameters) {
			SMParameter smParameter = FactoryComputationParameter
					.createParameter(param);
			if (smParameter != null)
				listParameters.add(smParameter);
		}

		SMParameters smParameters= new SMParameters(
				listParameters.toArray(new SMParameter[listParameters.size()]));
		xml.append("<inputs>");
		for (SMParameter smParam : smParameters.getList()) {

			xml.append("<input>" + "<name>" + smParam.getName() + "</name>"
					+ "<description>" + smParam.getDescription()
					+ "</description>" + "<defaultValue>" + smParam.getDefaultValue()
					+ "</defaultValue>" +"<type>" + smParam.getType().getName()
					+ "</type>"+
					"</input>");
			//			
		}
		//		if (parameters.size() != 0) {
		//			xml.append("<inputs>");
		//			for (StatisticalType p : parameters)
		//				xml.append("<input>" + "<name>" + p.getName() + "</name>"
		//						+ "<description>" + p.getDescription()
		//						+ "</description>" + "</input>");
		//
		//		}

		xml.append("</inputs>");
		xml.append("</Body>");
		xml.append("</Profile>");
		xml.append("</Resource>");
		// logger.debug("return xml");
		return xml;

	}


	/**
	 * Query scope for algorithms
	 * 
	 * @return
	 * @throws Exception
	 */
//	public Set<String> getGenericResources() throws Exception {
//
//		logger.debug("Getting Generic resources under scope "+ScopeProvider.instance.get());
//
//		HashSet<String> algNames = new HashSet<String>();
//		SimpleQuery query = queryFor(GenericResource.class);
//		query.addCondition("$resource/Profile/SecondaryType eq '"+Configuration.getProperty(Configuration.GR_SECONDARY_TYPE)+"'");
//		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
//		for(GenericResource genRes:client.submit(query))
//			algNames.add(genRes.profile().name());
//		return algNames;
//	}

//	public boolean isInScope(String name) throws Exception {
//		logger.debug("Checking Algorithm "+name+" presence under scope "+ScopeProvider.instance.get());
//		SimpleQuery query = queryFor(GenericResource.class);
//		query.addCondition("$resource/Profile/SecondaryType eq '"+SECONDARY_TYPE+"'");
//		query.addCondition("$resource/Profile/Name eq '"+name+"'");
//		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
//		return client.submit(query).size()>0;
//	}

//	/*
//	 * If exist return Resource having name equal name otherwise throw excpetion
//	 */
//	public boolean existGR(List<String> listExistAlg, String name)throws Exception {
//		for (String algName : listExistAlg) {
//
//			if (name.equals(algName))
//				return true;
//
//		}
//		return false;
//		// return listExistAlg.contains(name);
//
//	}

	
	/**
	 * For each algorithm publish if not in current scope
	 * 
	 * @param algorithms
	 * @throws Exception
	 */	
//	public void publishGRNew(HashMap<SMAlgorithm, List<StatisticalType>> algorithms) throws Exception {
//		consecutiveCall++;	
//
//		Set<String> listExistAlg =getGenericResources();
//		// this hash in needed because are returned same algorithms more times
//		
//		if(consecutiveCall==1){
//			for (Entry<SMAlgorithm, List<StatisticalType>> alg : algorithms.entrySet()) {
//				List<StatisticalType> parameters = alg.getValue();
//				SMAlgorithm a = alg.getKey();
//				String name = alg.getKey().getName();
//				
//				
//				String description = alg.getKey().getDescription();
//				GenericResource resource;
//
//				if (!listExistAlg.contains(name)) {
//					
//					// TODO Create and publish GR Algorithm
//					
////					logger.debug("Create resource." + name);
////
////					StringBuilder xml = createGR(a, name, description,
////							parameters);
////
////					String xmlResource = xml.toString();
////					StringReader reader = new StringReader(xmlResource);
////
////					resource = (GenericResource) Resources.unmarshal(GenericResource.class, reader);
////					List<String> scopes = new ArrayList<String>();
////
////					for (GCUBEScope s : ServiceContext.getContext().getStartScopes()) {
////						Type type = s.getType();
////						String scope = s.toString();
////						if (type == Type.VO)
////							scopes.add(scope);
////						//						logger.debug("Add scope :" + scope);
////
////					}
////					//					logger.debug("Consecutive call number" + consecutiveCall);
////
////					logger.debug("Publish");
////					ScopedPublisher sp = RegistryPublisherFactory.scopedPublisher();
////					Resource r = sp.create(resource, scopes);
//
//				}
//
//			}
//
//		}
//		if(consecutiveCall>=3)	consecutiveCall=0;
//
//	}
}



