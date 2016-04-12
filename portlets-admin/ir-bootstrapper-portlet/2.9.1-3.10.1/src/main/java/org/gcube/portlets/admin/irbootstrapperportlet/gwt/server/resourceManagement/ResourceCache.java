/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class ResourceCache {
	
	/** Logger */
	private static Logger logger = Logger.getLogger(ResourceCache.class);
	
	/** The list of resource types to retrieve */
	private List<Resource> resourceTypes;
	
	/** The scope in which this {@link ResourceCache} operates */
	private String scope;
	
	private Map<String,List<Resource>> fetchedResources;
	
	/**
	 * Class constructor
	 * @param scope the scope in which this {@link ResourceCache} operates
	 */
	public ResourceCache(String scope) {
		this.resourceTypes = new LinkedList<Resource>();
		this.scope = scope;
		this.fetchedResources = new HashMap<String,List<Resource>>();
	}
	
	/**
	 * Adds a new resource type to the list of types to retrieve
	 * @param resourceTypeName the resource type name
	 */
	public void addResourceToFetchedResourceTypesList(Resource res) {
		String resTypeName = res.getResourceTypeName();
		for (Resource r : resourceTypes) {
			if (r.getResourceTypeName().equals(resTypeName))
				return;
		}
		resourceTypes.add(res);
	}
	
	/**
	 * Removes everything from the list of resource types to retrieve
	 */
	public void clearResourceTypes() {
		resourceTypes.clear();
	}
	
	/**
	 * Retrieves and returns the resources which belong to the previously
	 * defined resource types.
	 */
	public void populateCacheFromIS() throws Exception {
		logger.debug("Populating resource cache...");
		for (Resource resourceType : resourceTypes) {
			try {
				// TODO Giota changed at Nov 7
				//List<Resource> resourceList = ResourceManager.retrieveResourcesFromIS(resourceType, true);
				List<Resource> resourceList = ResourceManager.retrieveResourcesFromIS(resourceType, false);
				fetchedResources.put(resourceType.getResourceTypeName(), resourceList);
				logger.debug("Fetched " + resourceList.size() + " resources of type " + resourceType.getResourceTypeName());
				
				/*
				if (resourceType.getResourceTypeName().equals(ForwardIndexManagementWSResource.TYPE_NAME)) {
					for (Resource r : resourceList) {
						for (String attrName : r.getAttributeNames()) {
							List<String> attrVals = r.getAttributeValue(attrName);
							String s = attrName + ": ";
							if (attrVals != null) {
								for (String aVal : attrVals)
									s = s + " " + aVal;
							}
							logger.info(s);
						}
						logger.info("Fwd resource expression: " + ResourceManager.constructQueryExpressionForResource(r));
						logger.info("---------------------------------------------------------------------------------");
					}
				}
				*/
				
			} catch (Exception e) {
				logger.error("Failed to retrieve the resources in scope: " + scope, e);
				throw new Exception("Failed to retrieve the resources in scope: " + scope);
			}
		}
	}
	
	/**
	 * Returns the resources having the given property values
	 * @param resourceType the type of resources to get
	 * @param attributes the list of attributes that the requested resources should have
	 * @return
	 */
	public <T extends Resource> List<T> getResourcesWithGivenAttributes(T resource) {
		List<T> ret = new LinkedList<T>();
		boolean bAddToResult;
		
		/* Loop through the resources for the given resource type */
		for (T r : (List<T>) fetchedResources.get(resource.getResourceTypeName())) {
			bAddToResult = true;
			
			/* Try to evaluate each of the given attributes against the resource.
			 * If at least one attribute does not exist in this resource, don't
			 * add the resource to the result.
			 */
			for (String attrName : resource.getAttributeNames()) {
				List<String> attrVals = resource.getAttributeValue(attrName);
				if (attrVals != null) {
					List<String> containedValues = new LinkedList<String>();
					List<String> notContainedValues = new LinkedList<String>();
					for (String s : attrVals) {
						if (!s.startsWith("!"))
							containedValues.add(s);
						else
							notContainedValues.add(s.substring(1));
					}

					/* Check if the resource's attr values list is set, and if it contains all
					 * values of the given resource's respective attribute.
					 */
					List<String> rAttrVals = r.getAttributeValue(attrName);
					
					
					if (rAttrVals==null) {
						if (containedValues.size() > 0) {
							bAddToResult = false;
							break;
						}
					}
					else {
						if (!rAttrVals.containsAll(containedValues)) {					
							bAddToResult = false;
							break;
						}
						
						/* Now check if the resource's attr values list contains any of the
						 * "not equal to" values of the given resource's respective attribute.
						 * If it does, this is not a match.
						 */
						for (String s : notContainedValues) {
							if (rAttrVals.contains(s)) {
								bAddToResult = false;
								break;
							}
						}
					}
				}
			}

			/* If this resource should be added to the results, add it */
			if (bAddToResult)
				ret.add(r);
		}
		return ret;
	}
}
