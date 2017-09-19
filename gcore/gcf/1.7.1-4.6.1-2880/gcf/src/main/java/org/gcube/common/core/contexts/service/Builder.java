package org.gcube.common.core.contexts.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.resources.GCUBEService;
import org.gcube.common.core.resources.GCUBEResource.InvalidScopeException;
import org.gcube.common.core.resources.runninginstance.AccessPoint;
import org.gcube.common.core.resources.runninginstance.AvailablePlugins;
import org.gcube.common.core.resources.runninginstance.DeploymentData;
import org.gcube.common.core.resources.runninginstance.Endpoint;
import org.gcube.common.core.resources.runninginstance.RunningInstanceInterfaces;
import org.gcube.common.core.resources.runninginstance.AvailablePlugins.AvailablePlugin;
import org.gcube.common.core.resources.service.MainPackage;
import org.gcube.common.core.resources.service.Package;
import org.gcube.common.core.resources.service.Plugin;
import org.gcube.common.core.resources.service.PortType;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;


/**
 * Builder for instance profile
 * 
 * @author Manuele Simi (ISTI-CNR)
 * 
 */
public class Builder {

	private GCUBELog logger;
	private GCUBEServiceContext context;
		
	public Builder(GCUBEServiceContext context) {
		this.context = context;
	}

	public void setLogger(GCUBELog logger) {
		this.logger = logger;
	}

	public void createRIResource() throws Exception {

		GCUBERunningInstance instance = context.getInstance();
		GCUBEService service = context.getService();
		GHNContext nodecontext = GHNContext.getContext();
		instance.setResourceVersion(instance.getLastResourceVersion());
		// set the same description as the service
		instance.setDescription(service.getDescription());

		// set the base unchangeable data
		instance.setGHNID(nodecontext.getGHNID());
		instance.setServiceClass(service.getServiceClass());
		instance.setServiceName(service.getServiceName());
		instance.setServiceID(service.getID());
		// the version of the instance is the version of the service's Main
		// Package
		List<org.gcube.common.core.resources.service.Package> pl = service.getPackages();
		for (org.gcube.common.core.resources.service.Package p : pl) {
			if (p.getClass().isAssignableFrom(org.gcube.common.core.resources.service.MainPackage.class)) {
				instance.setInstanceVersion(p.getVersion());
				break;
			}
		}

		// updates the endpoints
		updateEPRSection(instance, context.getService());

		// sets the actual state
		DeploymentData data = new DeploymentData();
		data.setActivationTime(Calendar.getInstance());
		data.setState(context.getStatus().toString());

		instance.setDeploymentData(data);

		// add start scopes to RI
		GCUBEScope[] scopes = context.getStartScopes();
		logger.info("starting in scopes " + Arrays.asList(scopes));
		if (instance.addScope(scopes).size() == 0)
			throw new InvalidScopeException();

	}

	public void updateRIResource() throws Exception {

		GCUBERunningInstance instance = context.getInstance();
		GCUBEService service = context.getService();
		GHNContext nodecontext = GHNContext.getContext();
		instance.setGHNID(nodecontext.getGHNID());
		instance.setServiceClass(service.getServiceClass());
		instance.setServiceName(service.getServiceName());
		instance.setServiceID(service.getID());
		// the version of the instance is the version of the service's Main
		// Package
		List<org.gcube.common.core.resources.service.Package> pl = service.getPackages();
		for (org.gcube.common.core.resources.service.Package p : pl) {
			if (p.getClass().isAssignableFrom(org.gcube.common.core.resources.service.MainPackage.class)) {
				instance.setInstanceVersion(p.getVersion());
				break;
			}
		}
		// updates the endpoints
		updateEPRSection(instance, context.getService());

		// updates the activation time
		instance.getDeploymentData().setActivationTime(Calendar.getInstance());

		// .. and the state
		instance.getDeploymentData().setState(context.getStatus().toString());

	}

	/**
	 * Adds a new plugin to the RI profile
	 * 
	 * @param service the plugin to add
	 */
	public void addPlugin(GCUBEService service)  {
		GCUBERunningInstance instance = context.getInstance();
		AvailablePlugins plugins = instance.getDeploymentData().getPlugins();		
		AvailablePlugin aplugin = plugins.new AvailablePlugin();
		aplugin.setClazz(service.getServiceClass());
		aplugin.setName(service.getServiceName());
		aplugin.setVersion(service.getVersion());
		for (Package pluginpackage : service.getPackages() ) {
			try {
				if (pluginpackage instanceof Plugin) {
					aplugin.setPluginPackage(pluginpackage.getName());
					aplugin.setPluginVersion(pluginpackage.getVersion());
					break;
				}
			} catch (Exception e) {/* whatever it happens doesn't matter*/}
		}
		plugins.getPlugins().add(aplugin);
	}

	/**
	 * Builds the endpoint section
	 * 
	 * @param instance
	 *            the instance to update
	 * @param service
	 *            the related service resource
	 * @throws IOException
	 */
	private void updateEPRSection(GCUBERunningInstance instance, GCUBEService service) throws Exception {

		String riBaseURL;
		try {
			riBaseURL = GHNContext.getContext().getBaseURLToPublish();
			if (this.context.getProperty(GCUBEServiceContext.PUBLISHED_HOST_JNDI_NAME, false) != null)
				riBaseURL = riBaseURL.replace(GHNContext.getContext().getHostname(), (String) this.context.getProperty(GCUBEServiceContext.PUBLISHED_HOST_JNDI_NAME, false));			
			if (this.context.getProperty(GCUBEServiceContext.PUBLISHED_PORT_JNDI_NAME, false) != null)
				riBaseURL = riBaseURL.replace(Integer.toString(GHNContext.getContext().getPort()), Integer.toString((Integer) this.context.getProperty(GCUBEServiceContext.PUBLISHED_PORT_JNDI_NAME, false)));
			
		} catch (IOException ioe) {
			logger.fatal("unable to detect the base URL", ioe);
			throw ioe;
		}
		AccessPoint access = new AccessPoint();
		RunningInstanceInterfaces ris = new RunningInstanceInterfaces();
		List<Endpoint> eprs = ris.getEndpoint();
		List<org.gcube.common.core.resources.service.Package> packages = service.getPackages();
		for (org.gcube.common.core.resources.service.Package pack : packages) {
			if (!(pack instanceof MainPackage)) {
				continue;
			}
			List<PortType> porttypes = ((MainPackage) pack).getPorttypes();
			for (PortType pt : porttypes) {
				Endpoint epr = new Endpoint();
				epr.setEntryName(pt.getName().trim());
				epr.setValue(riBaseURL + pt.getName().trim());
				eprs.add(epr);

			}
			break;
		}

		if (eprs.size() == 0)
			throw new Exception("No interface found for instance of " + service.getServiceName());
		access.setRunningInstanceInterfaces(ris);
		instance.setAccessPoint(access);
	}	

	/**
	 * Removes a plugin from the RI profile
	 * @param service the plugin to remove
	 */
	public void removePlugin(GCUBEService service) {
		GCUBERunningInstance instance = context.getInstance();
		AvailablePlugins plugins = instance.getDeploymentData().getPlugins();
		AvailablePlugin pluginToRemove = plugins.new AvailablePlugin();
		pluginToRemove.setClazz(service.getServiceClass());
		pluginToRemove.setName(service.getServiceName());
		pluginToRemove.setVersion(service.getVersion());
		for (Package pluginpackage : service.getPackages() ) {
			try {
				if (pluginpackage instanceof Plugin) {
					pluginToRemove.setPluginPackage(pluginpackage.getName());
					pluginToRemove.setPluginVersion(pluginpackage.getVersion());
					break;
				}
			} catch (Exception e) {/* whatever it happens doesn't matter*/}
		}
						
		plugins.getPlugins().remove(pluginToRemove);
		
	}

}