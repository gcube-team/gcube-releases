/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: ServiceProxyImpl.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.server;

import static org.gcube.resources.discovery.icclient.ICFactory.client;

import java.io.File;
import java.io.StringReader;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.portlets.admin.resourcemanagement.client.remote.ServiceProxy;
import org.gcube.portlets.admin.resourcemanagement.server.gcube.services.StatusHandler;
import org.gcube.portlets.admin.resourcemanagement.server.gcube.services.configuration.ConfigurationLoader;
import org.gcube.resourcemanagement.support.client.utils.CurrentStatus;
import org.gcube.resourcemanagement.support.client.views.ResourceTypeDecorator;
import org.gcube.resourcemanagement.support.server.gcube.CacheManager;
import org.gcube.resourcemanagement.support.server.gcube.ISClientRequester;
import org.gcube.resourcemanagement.support.server.gcube.queries.QueryLoader;
import org.gcube.resourcemanagement.support.server.gcube.queries.QueryLocation;
import org.gcube.resourcemanagement.support.server.managers.resources.GenericResourceManager;
import org.gcube.resourcemanagement.support.server.managers.resources.ManagementUtils;
import org.gcube.resourcemanagement.support.server.managers.resources.ResourceFactory;
import org.gcube.resourcemanagement.support.server.managers.scope.ScopeManager;
import org.gcube.resourcemanagement.support.server.types.AllowedResourceTypes;
import org.gcube.resourcemanagement.support.server.utils.ServerConsole;
import org.gcube.resourcemanagement.support.shared.exceptions.InvalidPermissionsException;
import org.gcube.resourcemanagement.support.shared.operations.SupportedOperations;
import org.gcube.resourcemanagement.support.shared.plugins.GenericResourcePlugin;
import org.gcube.resourcemanagement.support.shared.types.Tuple;
import org.gcube.resourcemanagement.support.shared.types.UserGroup;
import org.gcube.resourcemanagement.support.shared.types.datamodel.CompleteResourceProfile;
import org.gcube.resourcemanagement.support.shared.types.datamodel.ResourceDescriptor;
import org.gcube.resourcemanagement.support.shared.util.Assertion;
import org.gcube.resourcemanagement.support.shared.util.Configuration;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.impl.QueryTemplate;
import org.gcube.vremanagement.resourcemanager.client.RMReportingLibrary;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.gcube.portlets.admin.resourcemanagement.shared.utils.BCrypt;

/**
 * @author Daniele Strollo
 * @author Massimiliano Assante (ISTI-CNR)
 * 
 *
 */
@SuppressWarnings("serial")
public class ServiceProxyImpl extends RemoteServiceServlet implements ServiceProxy {
	private static final String LOG_PREFIX = "[SERVICE-IMPL]";

	public final void emptyCache() {
		ISClientRequester.emptyCache();
	}

	public final void setUseCache(final boolean flag) {
		this.getCurrentStatus().setUseCache(flag);
	}

	public final void setSuperUser(final boolean superUser) {
		if (superUser && this.getCurrentStatus().getCredentials() == UserGroup.DEBUG) {
			this.getCurrentStatus().setCredentials(UserGroup.ADMIN);
		} else {
			this.getCurrentStatus().setCredentials(UserGroup.DEBUG);
		}
		initScopes(true);
	}

	/**
	 * Called by portlet at module instantiation
	 */
	public final CurrentStatus initStatus() {
		ServerConsole.trace(LOG_PREFIX, "[INIT-STATUS] initializing config parameters");

		ServerConsole.info(LOG_PREFIX, "Clearing status");
		StatusHandler.clearStatus(getHttpSession());

		ConfigurationLoader.setConfigurationFile(getPropertiesFSPath() + File.separator + "resourcemanagement.properties");
		return this.getCurrentStatus();
	}

	private HttpSession getHttpSession() {
		return this.getThreadLocalRequest().getSession();
	}

	public final void initScopes(final boolean doClean) {
		ServerConsole.trace(LOG_PREFIX, "[INIT-SCOPES] initializing scopes from: " + this.getScopeDataPath());

		// Updates the scopes
		try {
			ScopeManager.setScopeConfigFile(this.getScopeDataPath());
			if (doClean) {
				ScopeManager.clear();
			}
			ScopeManager.update();
		} catch (Exception e) {
			ServerConsole.error(LOG_PREFIX, "During scope caching", e);
		}

	}

	private String getServletFSPath() {
		return this.getServletContext().getRealPath("") + File.separator + "WEB-INF";
	}

	private String getPropertiesFSPath() {
		return this.getServletContext().getRealPath("") + File.separator + "conf";
	}

	private String getScopeDataPath() { 
		String startDirectory = getServletFSPath();
		return startDirectory + File.separator + "xml" + File.separator +
				((this.getCurrentStatus().getCredentials() == UserGroup.ADMIN) ?
						"scopedata_admin.xml" :
						"scopedata.xml");
	}

	private String getXML2HTMLMapping() {
		String startDirectory = getServletFSPath();
		return startDirectory + File.separator + "xml" + File.separator + "xmlverbatim.xsl";
	}


	public final Tuple<String> addResourcesToScope(
			final String resType,
			final List<String> resourceIDs,
			final String scope)
					throws Exception {
		Assertion<Exception> checker = new Assertion<Exception>();

		CurrentStatus status = this.getCurrentStatus();

		checker.validate(SupportedOperations.ADD_TO_SCOPE.isAllowed(status.getCredentials()),
				new Exception("User not allowed to request this operation"));
		checker.validate(resType != null && resType.trim().length() > 0, new Exception("Invalid parameter type"));
		checker.validate(resourceIDs != null && resourceIDs.size() > 0, new Exception("Invalid parameter type"));
		checker.validate(scope != null && scope.trim().length() > 0, new Exception("Invalid parameter type"));

		AllowedResourceTypes type = AllowedResourceTypes.valueOf(resType);
		ScopeBean targetScope = new ScopeBean(scope);
		ScopeBean sourceScope = new ScopeBean(status.getCurrentScope());

		String reportID = ManagementUtils.addToExistingScope(type,
				resourceIDs.toArray(new String[]{}),
				sourceScope,
				targetScope);

		RMReportingLibrary manager =
				ResourceFactory.createResourceManager(type).getReportResourceManager(targetScope.toString());

		String xmlReport = manager.getReport(reportID);
		String mappingPath = this.getXML2HTMLMapping();
		String htmlReport = ISClientRequester.XML2HTML(xmlReport, mappingPath);
		return new Tuple<String>(
				reportID,
				resType,
				xmlReport,
				htmlReport);
	}
	/**
	 * REMOVE FROM SCOPE
	 */
	public final Tuple<String> removeResourcesFromScope(
			final String resType,
			final List<String> resourceIDs,
			final String scope)
					throws Exception {
		Assertion<Exception> checker = new Assertion<Exception>();

		CurrentStatus status = this.getCurrentStatus();

		checker.validate(SupportedOperations.ADD_TO_SCOPE.isAllowed(status.getCredentials()),
				new Exception("User not allowed to request this operation"));
		checker.validate(resType != null && resType.trim().length() > 0, new Exception("Invalid parameter type"));
		checker.validate(resourceIDs != null && resourceIDs.size() > 0, new Exception("Invalid parameter type"));
		checker.validate(scope != null && scope.trim().length() > 0, new Exception("Invalid parameter type"));

		AllowedResourceTypes type = AllowedResourceTypes.valueOf(resType);
		ScopeBean targetScope = new ScopeBean(scope);
		ScopeBean sourceScope = new ScopeBean(status.getCurrentScope());
		
		String reportID = ManagementUtils.removeFromExistingScope(type,
				resourceIDs.toArray(new String[]{}),
				sourceScope,
				targetScope);
		
		
		RMReportingLibrary manager =
				ResourceFactory.createResourceManager(type).getReportResourceManager(targetScope.toString());

		String xmlReport = manager.getReport(reportID);
		String mappingPath = this.getXML2HTMLMapping();
		String htmlReport = ISClientRequester.XML2HTML(xmlReport, mappingPath);

		return new Tuple<String>(
				reportID,
				resType,
				xmlReport,
				htmlReport);
	}

	public final String deploy(final List<String> ghnsID, final List<String> servicesID) throws Exception {
		try {
			ScopeBean sourceScope = new ScopeBean(this.getCurrentStatus().getCurrentScope());
			String[] param1 = ghnsID.toArray(new String[0]);
			String[] param2 = servicesID.toArray(new String[0]);
			return ManagementUtils.deploy(sourceScope, param1, param2);
		} catch (Exception e) {
			ServerConsole.error(LOG_PREFIX, "During service deployment", e);
			throw new Exception(e.getMessage());
		}
	}

	public final Tuple<String> checkDeployStatus(final String scope, final String deployID) throws Exception {
		if (SupportedOperations.SERVICE_GET_REPORT.isAllowed(this.getCurrentStatus().getCredentials())) {
			ScopeBean sourceScope = ScopeManager.getScope(scope);
			RMReportingLibrary manager =
					ResourceFactory.createResourceManager(AllowedResourceTypes.Service).getReportResourceManager(sourceScope.toString());
			String xmlReport = manager.getReport(deployID);
			String mappingPath = this.getXML2HTMLMapping();
			String htmlReport = ISClientRequester.XML2HTML(xmlReport, mappingPath);
			return new Tuple<String>(deployID, xmlReport, htmlReport);
		} else {
			throw new Exception("The current user is not allowed to request the operation");
		}
	}

	public final List<String> getAvailableScopes() {
		ServerConsole.trace(LOG_PREFIX, "[GET-SCOPES] getting available scopes");
		Vector<String> retval = new Vector<String>();
		try {
			Map<String, ScopeBean> scopes = ScopeManager.getAvailableScopes();
			for (ScopeBean scope : scopes.values()) 
				retval.add(scope.toString());
	
			return retval;
		} catch (Exception e) {
			retval.add("/gcube");
			retval.add("/gcube/devsec");
			e.printStackTrace();
		}
	
		return retval;
	}

	public final List<String> getAvailableAddScopes() {
		List<String> retval = new Vector<String>();
		try {
			ScopeBean currScope = ScopeManager.getScope(this.getCurrentStatus().getCurrentScope());
			List<String> scopes = this.getAvailableScopes();
			for (String scope : scopes) {
				if (scope.contains(currScope.toString())) {
					retval.add(scope);
				}
			}
			return retval;
		} catch (Exception e) {
			ServerConsole.error(LOG_PREFIX, e);
		}
		return null;
	}

	public final HashMap<String, ArrayList<String>> getResourceTypeTree(final String scope) throws Exception {
		try {
			ScopeBean gscope = new ScopeBean(scope);
			HashMap<String, ArrayList<String>>  results = ISClientRequester.getResourcesTree(getCacheManager(this.getCurrentStatus()), gscope);
			return results;
		} catch (Exception e) {
			ServerConsole.error(LOG_PREFIX, "applying resource get", e);
			throw e;
		}
	}

	public final List<String> getRelatedResources(final String type, final String id, final String scope)
	{
		try {
			return ISClientRequester.getRelatedResources(
					getCacheManager(this.getCurrentStatus()),
					type,
					id,
					new ScopeBean(scope)
					);
		} catch (Exception e) {
			ServerConsole.error(LOG_PREFIX, e);
			return null;
		}
	}

	public final List<String> getResourcesByType(final String scope, final String type)
	{
		try {
			return ISClientRequester.getResourcesByType(
					getCacheManager(this.getCurrentStatus()),
					new ScopeBean(scope), type, null);
		} catch (Exception e) {
			ServerConsole.error(LOG_PREFIX, e);
			return null;
		}
	}

	public final ResourceDescriptor getGenericResourceDescriptor(final String scope, final String resID) throws Exception {
		return this.getDescriptor(ScopeManager.getScope(scope), resID);
	}

	private ResourceDescriptor getDescriptor(final ScopeBean scope, final String resID)	throws Exception {


		//set the scope
		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope.toString());

		QueryTemplate isQuery = null;
		DiscoveryClient<String> client = client();	

		isQuery = new QueryTemplate(QueryLoader.getQuery(QueryLocation.GET_RESOURCE_BYID)); 

		isQuery.addParameter("RES_ID", resID);
		isQuery.addParameter("RES_TYPE", ResourceTypeDecorator.GenericResource.name());		

		List<String> results = client.submit(isQuery);

		if (results != null && results.size() > 0) {
			ResourceDescriptor retval = new ResourceDescriptor();
	
			List<String> currValue = null;
			DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Node node = docBuilder.parse(new InputSource(new StringReader(results.get(0)))).getDocumentElement();
			XPathHelper helper = new XPathHelper(node);
			
			currValue = helper.evaluate("/Resource/ID/text()");
			if (currValue != null && currValue.size() > 0) {
				retval.setID(currValue.get(0));
			}
			currValue = helper.evaluate("/Resource/Profile/Name/text()");
			if (currValue != null && currValue.size() > 0) {
				retval.setName(currValue.get(0));
			}

			currValue = helper.evaluate("/Resource/Profile/Body/node()");
			if (currValue != null && currValue.size() > 0) {
				StringBuilder bodytext = new StringBuilder();
				for (String line : currValue) {
					bodytext.append(line);
				}
				retval.set("Body", bodytext.toString().trim());
			} else {
				retval.set("Body", "");
			}


			currValue = helper.evaluate("/Resource/Profile/Description/text()");
			if (currValue != null && currValue.size() > 0) {
				retval.set("Description", currValue.get(0));
			} else {
				retval.set("Description", "");
			}


			currValue = helper.evaluate("/Resource/Profile/SecondaryType/text()");
			if (currValue != null && currValue.size() > 0) {
				retval.set("SecondaryType", currValue.get(0));
			} else {
				retval.set("SecondaryType", "");
			}

			return retval;
		}
		return null;
	}

	public final List<ResourceDescriptor> getResourcesModel(final String scope, final String type, final String subType, final List<Tuple<String>> additionalMaps)
			throws Exception {
		if (scope == null || type == null) {
			return null;
		}
		return ISClientRequester.getResourceModels(new ScopeBean(scope), type, subType, additionalMaps);
	}


	public final List<String> getWSResources(final String scope) {
		try {
			return ISClientRequester.getWSResources(new ScopeBean(scope));
		} catch (Exception e) {
			ServerConsole.error(LOG_PREFIX, e);
			return null;
		}
	}

	public final List<String> getResourcesBySubType(final String scope, final String type, final String subtype) {
		try {
			return ISClientRequester.getResourcesByType(
					getCacheManager(this.getCurrentStatus()),
					new ScopeBean(scope), type, subtype);
		} catch (Exception e) {
			ServerConsole.error(LOG_PREFIX, e);
			return null;
		}
	}

	public final CompleteResourceProfile getResourceByID(final String scope, final String type, final String resID) {
		try {
			CompleteResourceProfile profile = ISClientRequester.getResourceByID(this.getXML2HTMLMapping(), new ScopeBean(scope), type, resID);
			System.out.println("****\n\n\n CompleteResourceProfile getResourceByID(final String scope, final String type, final String resID)  *****");
			System.out.println("****CompleteResourceProfile getResourceByID("+scope+","+type+","+resID+")  *****\n\n");
			return profile;
		} catch (Exception e) {
			ServerConsole.error(LOG_PREFIX, e);
			return null;
		}

	}

	public final String createGenericResource(
			final String ID,
			final String name,
			final String description,
			final String body,
			final String subType)
					throws Exception {
		// Check permissions
		Assertion<InvalidPermissionsException> checker = new Assertion<InvalidPermissionsException>();
		checker.validate(
				SupportedOperations.GENERIC_RESOURCE_CREATE.isAllowed(this.getCurrentStatus().getCredentials()),
				new InvalidPermissionsException("The user is not allowed to execute the following operation"));

		return GenericResourceManager.create(
				ID,
				new ScopeBean(this.getCurrentStatus().getCurrentScope()),
				name,
				description,
				body,
				subType);
	}

	public final void updateGenericResource(
			final String ID,
			final String name,
			final String description,
			final String body,
			final String subType)
					throws Exception {
		// Check permissions
		Assertion<InvalidPermissionsException> checker = new Assertion<InvalidPermissionsException>();
		checker.validate(
				SupportedOperations.GENERIC_RESOURCE_EDIT.isAllowed(this.getCurrentStatus().getCredentials()),
				new InvalidPermissionsException("The user is not allowed to execute the following operation"));

		ScopeBean sourceScope = new ScopeBean(this.getCurrentStatus().getCurrentScope());
		GenericResourceManager resource = new GenericResourceManager(ID);
		resource.update(name, description, body, subType, sourceScope);
	}

	public final void setCurrentScope(final String scope) {
		this.getCurrentStatus().setCurrentScope(scope);
	}

	public final Map<String, GenericResourcePlugin> getGenericResourcePlugins() throws Exception {
		return ISClientRequester.getGenericResourcePlugins(ScopeManager.getScope(this.getCurrentStatus().getCurrentScope()));		
	}

	private CurrentStatus getCurrentStatus() {
		return StatusHandler.getStatus(this.getHttpSession());
	}

	private CacheManager getCacheManager(CurrentStatus status) {
		CacheManager cm = new CacheManager();
		cm.setUseCache(status.useCache());
		return cm;
	}
	/***********************************************************
	 * RESOURCE OPERATIONS
	 * @throws InvalidParameterException
	 **********************************************************/
	public final void doOperation(
			final SupportedOperations opCode,
			final String scope,
			final List<ResourceDescriptor> resources)
					throws Exception {
		try {
			ResourceCommands.doOperation(
					this.getCurrentStatus(),
					opCode, scope, resources);
		} catch (final Exception e) {
			ServerConsole.error(LOG_PREFIX, e);
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public boolean enableSuperUserMode(String code) {
		String ipAddressCaller =  getThreadLocalRequest().getRemoteAddr();
		boolean matched = BCrypt.checkpw(code, Configuration.CODE);
		if (matched)
			ServerConsole.info(LOG_PREFIX, "AUTHORISED SUPER USER MODE, IP: " + ipAddressCaller);
		else
			ServerConsole.info(LOG_PREFIX, "FAILED ATTEMPT SUPER USER MODE FROM IP: " + ipAddressCaller);
		return matched;
	}
	
	
}
