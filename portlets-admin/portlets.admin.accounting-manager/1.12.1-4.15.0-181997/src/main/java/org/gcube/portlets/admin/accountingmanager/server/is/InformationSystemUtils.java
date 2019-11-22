package org.gcube.portlets.admin.accountingmanager.server.is;

import java.util.Iterator;
import java.util.List;

import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.ScopeGroup;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.admin.accountingmanager.shared.Constants;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.impl.JAXBParser;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class InformationSystemUtils {

	
	private static Logger logger = LoggerFactory
			.getLogger(InformationSystemUtils.class);

	public static EnableTabsJAXB retrieveEnableTab(String scope)
			throws ServiceException {
		try {

			if (scope == null || scope.length() == 0)
				return null;

			ScopeProvider.instance.set(scope);
			logger.debug("Retrieve enabletab configuration in scope: "+scope);
			
			
			SimpleQuery query = ICFactory.queryFor(GenericResource.class);
			query.addCondition(
					"$resource/Profile/SecondaryType/text() eq '"
							+ Constants.ACCOUNTING_CATEGORY + "'")
					.addCondition(
							"$resource/Profile/Name/text() eq '"
									+ Constants.ACCOUNTING_NAME + "'")
					.setResult("$resource");

			DiscoveryClient<GenericResource> client = ICFactory
					.clientFor(GenericResource.class);
			List<GenericResource> accountingResources = client.submit(query);
			logger.debug("Resources: " + accountingResources);

			EnableTabsJAXB enableTabs = null;

			for (GenericResource genericResource : accountingResources) {
				if (genericResource.scopes() != null) {
					ScopeGroup<String> scopes = genericResource.scopes();
					Iterator<String> iterator = scopes.iterator();
					String scopeFound = null;
					boolean found = false;
					while (iterator.hasNext() && !found) {
						scopeFound = iterator.next();
						if (scopeFound.compareTo(scope) == 0) {
							found = true;
						}
					}
					if (found) {
						try {
							JAXBParser<EnableTabsJAXB> parser = new JAXBParser<EnableTabsJAXB>(
									EnableTabsJAXB.class);
							logger.debug("Body: "
									+ genericResource.profile().bodyAsString());
							enableTabs = (EnableTabsJAXB) parser
									.parse(genericResource.profile()
											.bodyAsString());
							logger.debug("Enable: " + enableTabs);
						} catch (Throwable e) {
							String error = "Error in discovery Accounting Manager enabled tab in scope "+ scope+". "
									+ "Resource parsing failed!";
							logger.error(error);
							logger.error("Error {resource="+ genericResource + ", error="
									+ e.getLocalizedMessage() + "}");
							e.printStackTrace();
							throw new ServiceException(error, e);
						}
						break;

					}

				}
			}

			return enableTabs;

		} catch (ServiceException e) {
			throw e;
		} catch (Throwable e) {
			String error = "Error in discovery Accounting Manager enabled tab in scope: "
					+ scope;
			logger.error(error);
			logger.error("Error: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(error, e);
		}
	}
	
	
	
	public static ThreadPoolJAXB retrieveThreadPoolTimeout(String scope)
			throws ServiceException {
		try {

			if (scope == null || scope.length() == 0)
				return null;
			logger.debug("Retrieve threadpool configuration in scope: "+scope);
			
			ScopeProvider.instance.set(scope);

			SimpleQuery query = ICFactory.queryFor(GenericResource.class);
			query.addCondition(
					"$resource/Profile/SecondaryType/text() eq '"
							+ Constants.ACCOUNTING_CATEGORY + "'")
					.addCondition(
							"$resource/Profile/Name/text() eq '"
									+ Constants.ACCOUNTING_POOL_NAME + "'")
					.setResult("$resource");

			DiscoveryClient<GenericResource> client = ICFactory
					.clientFor(GenericResource.class);
			List<GenericResource> accountingResources = client.submit(query);
			logger.debug("Resources: " + accountingResources);

			ThreadPoolJAXB threadPool = null;

			for (GenericResource genericResource : accountingResources) {
				if (genericResource.scopes() != null) {
					ScopeGroup<String> scopes = genericResource.scopes();
					Iterator<String> iterator = scopes.iterator();
					String scopeFound = null;
					boolean found = false;
					while (iterator.hasNext() && !found) {
						scopeFound = iterator.next();
						if (scopeFound.compareTo(scope) == 0) {
							found = true;
						}
					}
					if (found) {
						try {
							JAXBParser<ThreadPoolJAXB> parser = new JAXBParser<ThreadPoolJAXB>(
									ThreadPoolJAXB.class);
							logger.debug("Body: "
									+ genericResource.profile().bodyAsString());
							threadPool = (ThreadPoolJAXB) parser
									.parse(genericResource.profile()
											.bodyAsString());
							logger.debug("ThreadPool: " + threadPool);
						} catch (Throwable e) {
							String error = "Error in discovery Accounting Manager thread pool in scope "+ scope+". "
									+ "Resource parsing failed!";
							logger.error(error);
							logger.error("Error {resource="+ genericResource + ", error="
									+ e.getLocalizedMessage() + "}");
							e.printStackTrace();
							throw new ServiceException(error, e);
						}
						break;

					}

				}
			}

			return threadPool;

		} catch (ServiceException e) {
			throw e;
		} catch (Throwable e) {
			String error = "Error in discovery Accounting Manager thread pool in scope: "
					+ scope;
			logger.error(error);
			logger.error("Error: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(error, e);
		}
	}

	

}
