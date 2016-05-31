/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;
import static org.gcube.resources.discovery.icclient.ICFactory.client;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.publisher.RegistryPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class ResourceManager {

	/** Logger */
	private static Logger logger = Logger.getLogger(ResourceManager.class);

	/**
	 * Retrieves all the resources in the given scope that match the defined attributes of
	 * the resource given as a template. The type of the returned resources is the same as
	 * that of the template. If the 'ignoreTemplateAttributes' parameter is set to true,
	 * then the attributes of the given template are not matched against the fetched resources.
	 * In this case, all resources whose type is that of the template are returned, regardless
	 * of their property values.
	 * @param <T>
	 * @param template
	 * @param ignoreTemplateAttributes
	 * @return
	 * @throws Exception
	 */
	public static <T extends Resource> List<T> retrieveResourcesFromIS(T template, boolean ignoreTemplateAttributes) throws Exception {
		List<T> ret = new LinkedList<T>();
		ScopeProvider.instance.set(template.getScope());
		
		/* Create the query expression. If only the resource type should be taken into account
		 * and not the template's attributes, just use the template's base query expression.
		 * Otherwise, construct a more complex expression based on the template's attributes */
		String queryExpression = null;
		if (!ignoreTemplateAttributes)
			queryExpression = constructQueryExpressionForResource(template);
		else
			queryExpression = template.getBaseISQuery().replace(Resource.QUERY_CONDITION_PLACEHOLDER.trim(), "");

		logger.debug("***Going to execute the following expression using the IC client*** and templates will be ignored?: " + ignoreTemplateAttributes);
		logger.debug(queryExpression);
		Query q = new QueryBox(queryExpression);
		DiscoveryClient<String> client = client();
		// This query returns the XML resource as it is on IS 
		List<String> result = client.submit(q);
		
		/* Retrieve the constructor of the resource class which we will creates instances of */
		Class<? extends Resource> rc = template.getClass();
		//TODO it was GCubeScope
		Constructor<? extends Resource> ctor = rc.getConstructor(String.class);
		for (String r : result) {
			Resource res = ctor.newInstance(template.getScope());
			res.fromXML(r);
			ret.add((T) res);
		}
		return ret;
	}

	/**
	 * Retrieves all the resources in the given scope that match the given {@link ResourceExpression}.
	 * @param <T>
	 * @param resExpression
	 * @return
	 * @throws Exception
	 */
	public static <T extends Resource> List<T> retrieveResourcesFromIS(ResourceExpression<T> resExpression) throws Exception {
		List<T> ret = new LinkedList<T>();

		ScopeProvider.instance.set(resExpression.getScope());
		Query q = new QueryBox(resExpression.getISQueryExpression());
		
		logger.debug("***Going to execute the following expression using the IC client");
		logger.debug(resExpression.getISQueryExpression());
		
		DiscoveryClient<String> client = client();
		// This query returns the XML resource as it is on IS 
		List<String> result = client.submit(q);
		for (String r : result) {
			Resource res = resExpression.createNewResource();
			res.fromXML(r);
			ret.add((T) res);
		}
		return ret;
	}

	/**
	 * Creates a {@link ResourceExpression} object, which represents a query for resources matching
	 * the given template resource. Once you create such an object, you can use it to retrieve resources
	 * through subsequent calls to retrieveResourcesFromIS(). It is highly recommended to use this
	 * technique when the same template will be used more than once for resource retrieval, because
	 * once the expression is generated, the retrieval will be a lot faster than using the form of
	 * retrieveResourcesFromIS() that does not accept a {@link ResourceExpression} many times.
	 * @param <T>
	 * @param template
	 * @param ignoreTemplateAttributes
	 * @return
	 * @throws Exception
	 */
	public static <T extends Resource> ResourceExpression<T> generateExpressionForResourceTempate(T template, boolean ignoreTemplateAttributes) throws Exception {

		/* Create the query expression. If only the resource type should be taken into account
		 * and not the template's attributes, just use the template's base query expression.
		 * Otherwise, construct a more complex expression based on the template's attributes */
		String queryExpression = null;
		if (!ignoreTemplateAttributes)
			queryExpression = constructQueryExpressionForResource(template);
		else 
			queryExpression = template.getBaseISQuery().replace(Resource.QUERY_CONDITION_PLACEHOLDER.trim(), "");

		/* Construct the ResourceExpression object */
		return new ResourceExpression<T>(template.getClass(), queryExpression, template.getScope());
	}

	/**
	 * Constructs the IS query to be sent to the ISClient, describing the given resource.
	 * The query will contain sub-conditions for every attribute that has a value in the
	 * given resource.
	 * @param template the resource which to construct a query for
	 * @return
	 */
	public static String constructQueryExpressionForResource(Resource template) {
		String templateQuery = template.getBaseISQuery();
		/* Find the last part of the template's query expression bound to '$result' in 
		 * the template query string. This part is the word that appears after the last
		 * '/' that appears before the condition placeholder.
		 * If there is no condition placeholder, then there is already a condition included
		 * in the expression. In this case, locate the "where" keyword of the query, which
		 * is the beginning of the current condition.
		 */
		boolean bAlreadyContainsCondition = false;
		int conditionStart = templateQuery.indexOf(Resource.QUERY_CONDITION_PLACEHOLDER);
		if (conditionStart == -1) {
			conditionStart = templateQuery.indexOf("where");
			bAlreadyContainsCondition = true;
		}
		int lastExpressionPartStart = templateQuery.lastIndexOf('/', conditionStart);
		int lastExpressionPartLength = conditionStart - lastExpressionPartStart - 1;
		String lastExpressionPart = templateQuery.substring(lastExpressionPartStart, conditionStart-1).trim();
		lastExpressionPartLength = lastExpressionPart.length();

		/* Construct the condition string. For each attribute that has a value in the given
		 * resource template, add a sub-condition in the form of (attrName eq "attrValue").
		 * If attrNames start with the last part of the template's query expression, remove
		 * that part before adding them as sub-conditions, because the query will not be
		 * correct otherwise.
		 */
		StringBuilder condBuilder = new StringBuilder();
		int numConds = 0;
		for (String attrName : template.getAttributeNames()) {
			List<String> attrVals = template.getAttributeValue(attrName);
			if (attrVals!=null && attrVals.size()>0) {
				for (String val : attrVals) {
					//String val = attrVals.get(0);
					boolean negation = val.startsWith("!");
					if (negation)
						val = val.substring(1);

					if (numConds > 0)
						condBuilder.append(" and ");

					if (attrName.startsWith(lastExpressionPart))
						attrName = attrName.substring(lastExpressionPartLength);
					condBuilder.append("($result");
					condBuilder.append(attrName);
					if (negation)
						//condBuilder.append(" ne '");
						condBuilder.append(" != '");
					else
						//condBuilder.append(" eq '");
						condBuilder.append(" = '");
					condBuilder.append(val);
					condBuilder.append("') ");
				}

				numConds++;
			}
		}

		/* If a condition has been generated (the given resource is not 'empty'), add the
		 * condition to the query expression. If the query already contains a condition,
		 * add an "and" in front of the newly created condition so as to AND the new and old
		 * conditions.
		 * If no condition was already there, add a "where" in front of the new condition.
		 */
		if (numConds > 0) {
			if (bAlreadyContainsCondition)
				condBuilder.insert(0, " and ");
			else
				condBuilder.insert(0, "where ");
		}

		/* Get the constructed condition as a string and replace any occurrences of 'text()' with 'string()' */
		String condition = condBuilder.toString();
		condition = condition.replace("text()", "string()");

		/* Now put the condition inside the query string, replacing the condition placeholder (if
		 * no condition existed before in the query) or adding it just before the "return" part
		 * (if a condition was already in the query) */
		if (!bAlreadyContainsCondition)
			templateQuery = templateQuery.replace(Resource.QUERY_CONDITION_PLACEHOLDER.trim(), condition);
		else {
			String queryParts[] = templateQuery.split("return");
			templateQuery = queryParts[0] + condition + " return" + queryParts[1];
		}
		
		//TODO remove this log message
		//logger.debug("Final xQuery --->>> " + templateQuery);
		
		return templateQuery;
	}

	/**
	 * Retrieves a specific generic resource from the IS and returns its payload as
	 * as string.
	 * 
	 * @param resourceID the ID of the generic resource. If resourceID starts with '$',
	 * then the name of the generic resource follows instead of its ID.
	 * @return an object representing the generic resource
	 */
	public static GenericResource retrieveGenericResource(String resourceID, String scope) throws Exception {
		try {
			ScopeProvider.instance.set(scope);
			SimpleQuery query = queryFor(GenericResource.class);
			if (resourceID.startsWith("$")) {
				String resourceName = resourceID.substring(1);
				query.addCondition("$resource/Profile/Name/text() eq '" + resourceName + "'");
			}
			else
				query.addCondition("$resource/ID/text() eq '" + resourceID +  "'");
		
			DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
			List<GenericResource> result = client.submit(query);
			
			if (result==null || result.size()==0)
				throw new Exception("Could not find generic resource with ID -> " + resourceID);
			return result.get(0);
		} catch (Exception e) {
			logger.error("Failed to retrieve the generic resource with ID = " + resourceID, e);
			throw new Exception("Failed to retrieve the generic resource with ID = " + resourceID);
		}
	}

	public static void updateGenericResource(GenericResource resource, String scope) throws Exception {
		ScopeProvider.instance.set(scope);
		RegistryPublisher publisher = RegistryPublisherFactory.create();
		publisher.update(resource);
	}
}
