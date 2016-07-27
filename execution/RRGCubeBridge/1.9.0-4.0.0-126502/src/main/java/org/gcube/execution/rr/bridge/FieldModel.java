package org.gcube.execution.rr.bridge;

import gr.uoa.di.madgik.commons.utils.XMLUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.rest.commons.helpers.XPathEvaluator;
import org.gcube.rest.commons.resourceawareservice.resources.Resource;
import org.gcube.rest.resourcemanager.discovery.InformationCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class FieldModel 
{
	private static final Logger logger = LoggerFactory
			.getLogger(FieldModel.class);
	
	public static Resource mainResource;
	private static Map<String, Resource> fieldResources = new HashMap<String, Resource>();
	private static Resource metadataResource;
	private static Resource staticConfigResource;
	
	private static Set<String> fieldIds = new HashSet<String>();

//	private static Resource getMostRecentResource(List<Resource> resources) throws Exception
//	{
//		long mostRecentTime=-1;
//		Resource mostRecentResource=null;
//		for(Resource resource : resources)
//		{
//			long updateTime=0;
//			try{
//				Resource res = XMLConverter.fromXML(resource.getBodyAsString(), Resource.class);
//				
//				updateTime = Long.parseLong(res.getDescription());
//				
//			}catch(Exception ex){
//				continue;
//			}
//			if(updateTime> mostRecentTime) mostRecentResource=resource;
//		}
//		return mostRecentResource;
//	}
	
	private static Resource getMostRecentResource(List<Resource> resources) throws Exception
	{
		long mostRecentTime=-1;
		Resource mostRecentResource=null;
		for(Resource resource : resources)
		{
			long updateTime=0;
			try{
				updateTime = Long.parseLong(resource.getDescription());
			}catch(Exception ex){
				continue;
			}
			if(updateTime> mostRecentTime) mostRecentResource=resource;
		}
		return mostRecentResource;
	}
	
	private static void processResources(Map<String, Map<String, Resource>> resources) throws Exception
	{
		Set<String> fieldResourceIds = new HashSet<String>();
		Set<String> fieldIdsInDirectory = new HashSet<String>();
		
		List<Resource> mainResources = new ArrayList<Resource>();
		for(Map<String, Resource> resourcesInScope : resources.values())
		{
			Resource mainResource = resourcesInScope.get(GCubeRepositoryProvider.RRModelGenericResourceName);
			if(mainResource!=null) mainResources.add(mainResource);
		}
		mainResource = getMostRecentResource(mainResources);

		boolean foundDirectory = false;
		Document fieldsDOM=null;
		if(mainResource != null && 
			getStringOfElement(mainResource.getBody())!=null && 
			getStringOfElement(mainResource.getBody()).trim().length()!=0)
		{
			fieldsDOM = XMLUtils.Deserialize(getStringOfElement(mainResource.getBody()));
			foundDirectory = true;
		}
		
		if(foundDirectory == true)
		{
			XPathEvaluator xpath = new XPathEvaluator(FieldModel.mainResource.getBody());
			
			List<String> fieldIds = xpath.evaluate("//fieldId/text()");
			FieldModel.fieldIds.addAll(fieldIds);
			fieldIdsInDirectory.addAll(fieldIds);
//			boolean flatModel = false;
//			List<Element> xmlObjs = XMLUtils.GetChildElementsWithName(XMLUtils.GetChildElementWithName(fieldsDOM.getDocumentElement(), "fields") , "field");
//			if(xmlObjs.size()!=0)
//				flatModel = true;
//			else
//				xmlObjs =XMLUtils.GetChildElementsWithName(XMLUtils.GetChildElementWithName(fieldsDOM.getDocumentElement(), "fields"), "fieldId");
//				
//			for(Element elem : xmlObjs)
//			{
//				if(!flatModel)
//					fieldIdsInDirectory.add(elem.getFirstChild().getNodeValue());
//				else
//					fieldIdsInDirectory.add(XMLUtils.GetChildElementWithName(elem, "id").getFirstChild().getNodeValue());
//			}
//			FieldModel.fieldIds.addAll(fieldIdsInDirectory);
		}
		
		List<Resource> metadataResources = new ArrayList<Resource>();
		for(Map<String, Resource> resourcesInScope : resources.values())
		{
			Resource metadataResource = resourcesInScope.get(GCubeRepositoryProvider.RRModelGenericResourceName+".Metadata");
			if(metadataResource!=null) metadataResources.add(metadataResource);
		}
		metadataResource = getMostRecentResource(metadataResources);
		
		List<Resource> staticConfigResources = new ArrayList<Resource>();
		for(Map<String, Resource> resourcesInScope : resources.values())
		{
			Resource staticConfigResource = resourcesInScope.get(GCubeRepositoryProvider.RRModelGenericResourceName+".StaticConfig");
			if(staticConfigResource!=null) staticConfigResources.add(staticConfigResource);
		}
		logger.info("found : " + staticConfigResources.size() + " staticconfig resources ");
		//System.out.println("found : " + staticConfigResources.size() + " staticconfig resources ");
		staticConfigResource = getMostRecentResource(staticConfigResources);
		
		if(foundDirectory)
		{
			for(Map<String, Resource> resourcesInScope : resources.values())
			{
				for(Map.Entry<String, Resource> r : resourcesInScope.entrySet())
				{
					if(r.getKey().startsWith(GCubeRepositoryProvider.RRModelGenericResourceName+".")) fieldResourceIds.add(r.getValue().getName().substring(r.getValue().getName().indexOf(".")+1));
				}
			}
			
			for(String fieldResourceId : fieldResourceIds)
			{
				if(!fieldIdsInDirectory.contains(fieldResourceId)) continue;
				List<Resource> fieldResources = new ArrayList<Resource>();
				for(Map<String, Resource> resourcesInScope : resources.values())
				{
					Resource fieldResource = resourcesInScope.get(GCubeRepositoryProvider.RRModelGenericResourceName+"."+fieldResourceId);
					if(fieldResource!=null) fieldResources.add(fieldResource);
				}
				Resource fr = getMostRecentResource(fieldResources);
				if(fr!=null) FieldModel.fieldResources.put(fieldResourceId, fr);
			}
		}
		
	}
	
	public static void retrieve() throws Exception
	{
		InformationCollector icollector = BackendConnector.newICollector();
		mainResource = null;
		fieldResources = new HashMap<String, Resource>();
		metadataResource = null;
		staticConfigResource = null;
		fieldIds = new HashSet<String>();
			
		List<String> scopes = BridgeHelper.getFieldModelScopes();
		
		Map<String, Map<String, Resource>> resources=new HashMap<String, Map<String, Resource>>();
		for(String scope : scopes)
		{
			logger.info("Retrieving field model generic resources in scope " + scope.toString());
			
			List<Resource> res = icollector.getGenericResourcesByType(GCubeRepositoryProvider.RRModelGenericResourceName, scope);
			
			logger.info("number of generic resources ( " + GCubeRepositoryProvider.RRModelGenericResourceName + " ) : " + res.size());
			
			if(!resources.containsKey(scope.toString())) resources.put(scope.toString(), new HashMap<String, Resource>());
			for(Resource r : res)
				resources.get(scope.toString()).put(r.getName(), r);
		}
		
		logger.info("number of generic resource : " + resources.size());
		
		processResources(resources);
		logger.info("Processed field model generic resources: " + (mainResource != null ? "1" : "0") + " field directory resources containing " + fieldIds.size() + " field references, " + 
				fieldResources.keySet().size() + " field resources, " + (metadataResource != null ? "1" : "0") + " element metadata resources, " + 
				(staticConfigResource != null ? "1" : "0") + " static configuration resources.");
	}
	
	public static String getMainResource()
	{
		if(mainResource == null || 
				getStringOfElement(mainResource.getBody())==null ||
				getStringOfElement(mainResource.getBody()).trim().length()==0) return null;
		 return getStringOfElement(mainResource.getBody());
	}
	
	public static String getMetadataResource()
	{
		if(metadataResource == null || 
				getStringOfElement(metadataResource.getBody())==null ||
				getStringOfElement(metadataResource.getBody()).trim().length()==0) return null;
		 return getStringOfElement(metadataResource.getBody());
	}
	
	public static String getStaticConfigResource()
	{
		if(staticConfigResource == null ||
				getStringOfElement(staticConfigResource.getBody()) == null ||
				getStringOfElement(staticConfigResource.getBody()).trim().length()==0) return null;
		return getStringOfElement(staticConfigResource.getBody());
	}
	
	public static Set<String> getFieldIds()
	{
		return fieldIds;
	}
	
	public static String getFieldResource(String id)
	{
		Resource fieldResource = fieldResources.get(id);
		if(fieldResource == null || 
				getStringOfElement(fieldResource.getBody())==null ||
				getStringOfElement(fieldResource.getBody()).trim().length()==0) return null;
		 return getStringOfElement(fieldResource.getBody());
	}
	
	public static Resource getFieldResourceObj(String id)
	{
		Resource fieldResource = fieldResources.get(id);
		return fieldResource;
	}
	
	public static String getStringOfElement(Node node){
		XPathEvaluator eval = new XPathEvaluator(node);
		
		String text = eval.evaluate("/").get(0);
		text = text.substring("<doc>".length());
		text = text.substring(0, text.length() - "</doc>".length());
		
		return text;
	}
	
}
