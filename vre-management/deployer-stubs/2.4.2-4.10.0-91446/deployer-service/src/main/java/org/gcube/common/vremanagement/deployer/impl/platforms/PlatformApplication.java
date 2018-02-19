package org.gcube.common.vremanagement.deployer.impl.platforms;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.gcube.common.core.contexts.GCUBEServiceContext.Status;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBERIQuery;
import org.gcube.common.core.informationsystem.publisher.ISPublisher;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.resources.GCUBEResource.InvalidScopeException;
import org.gcube.common.core.resources.common.PlatformDescription;
import org.gcube.common.core.resources.runninginstance.AccessPoint;
import org.gcube.common.core.resources.runninginstance.DeploymentData;
import org.gcube.common.core.resources.runninginstance.Endpoint;
import org.gcube.common.core.resources.runninginstance.RunningInstanceInterfaces;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.virtualplatform.image.VirtualPlatform;
import org.gcube.vremanagement.virtualplatform.model.DeployedPackage;
import org.gcube.vremanagement.virtualplatform.model.Package;
import org.gcube.vremanagement.virtualplatform.model.TargetPlatform;
import org.gcube.vremanagement.virtualplatform.model.UndeployedPackage;

/**
 * An application hosted on a {@link TargetPlatform}
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class PlatformApplication {

	private DeployedPackage dpackage;
	private UndeployedPackage upackage;
	private GCUBERunningInstance instance;
	private GCUBELog logger = new GCUBELog(this);
	
	protected PlatformApplication(String riid, GCUBEScope scope) throws Exception {
		ISClient client = GHNContext.getImplementation(ISClient.class);
		GCUBERIQuery query = client.getQuery(GCUBERIQuery.class);
		query.addAtomicConditions(new AtomicCondition("/ID", riid));
		List<GCUBERunningInstance> results = client.execute(query,scope);
		if (results.size() == 0)
			throw new Exception("Unable to find a RI with id=" +riid );
		instance = results.iterator().next();
	}

	protected PlatformApplication(GCUBERunningInstance instance) throws Exception {
		this.instance = instance;
	}
	
	protected PlatformApplication(DeployedPackage dpackage, VirtualPlatform platform) throws Exception {
		this.dpackage = dpackage;
		this.instance = GHNContext.getImplementation(GCUBERunningInstance.class);
		GHNContext nodecontext = GHNContext.getContext();
		Package source = this.dpackage.getSourcePackage();
		instance.setLogger(PlatformApplication.this.logger);
		instance.setResourceVersion(instance.getLastResourceVersion());
		instance.setDescription(source.getDescription());
		instance.setGHNID(nodecontext.getGHNID());
		instance.setServiceClass(source.getServiceClass());
		instance.setServiceName(source.getServiceName());
		instance.setServiceID(dpackage.getSourcePackage().getServiceID());
		PlatformDescription platformDesc = new PlatformDescription();
		platformDesc.setName(platform.getName());
		platformDesc.setVersion(platform.getVersion());
		platformDesc.setMinorVersion(platform.getMinorVersion());
		instance.setPlatform(platformDesc);
		instance.setInstanceVersion(source.getVersion());
		
		// sets the actual state
		DeploymentData data = new DeploymentData();
		data.setActivationTime(Calendar.getInstance());
		data.setLocalPath(source.getTargetPath());
		data.setInstanceName(source.getName());
		instance.setDeploymentData(data);
		AccessPoint access = new AccessPoint();
		RunningInstanceInterfaces ris = new RunningInstanceInterfaces();
		List<Endpoint> eprs = ris.getEndpoint();
		int index = 1;
		for (String ep : dpackage.getEndpoints()) {
			Endpoint epr = new Endpoint();
			epr.setEntryName("endpoint-"+index);
			epr.setValue(ep);
			eprs.add(epr);
			index++;
		}
		access.setRunningInstanceInterfaces(ris);
		instance.setAccessPoint(access);
		
	}

	protected PlatformApplication(UndeployedPackage upackage) throws Exception {
		this.upackage = upackage;
		Package dpackage = this.upackage.getSourcePackage();
		//Query for the RI representing the package in the ID
		ISClient client = GHNContext.getImplementation(ISClient.class);
		GCUBERIQuery query = client.getQuery(GCUBERIQuery.class);
		query.addAtomicConditions(new AtomicCondition("//GHN/@UniqueID", GHNContext.getContext().getGHNID()), 
				new AtomicCondition("//ServiceClass", dpackage.getServiceClass()),
				new AtomicCondition("//ServiceName", dpackage.getServiceName()));
		logger.trace("Looking for the package to unregister in the following scope " + dpackage.getScope());
		List<GCUBERunningInstance> results = client.execute(query,dpackage.getScope());
		if (results.size() == 0)
			throw new Exception("Unable to find a RI for " + dpackage.getServiceClass() + ", " + dpackage.getServiceName());
		instance = results.iterator().next();
	}
	
	/**
	 * 
	 * @param scopes
	 * @param manager
	 * @param status
	 * @return the ID of the instance published
	 * @throws Exception
	 */
	public GCUBERunningInstance publish(Set<GCUBEScope> scopes, GCUBESecurityManager manager, Status ... status) throws Exception {
		if (instance.addScope(scopes.toArray(new GCUBEScope[]{})).size() == 0)
			throw new InvalidScopeException();
		//change the status of the instance if passed
		if ((status != null) && (status.length > 0))
			this.instance.getDeploymentData().setState(status[0].toString());
		ISPublisher publisher = null;//fetch publisher implementation
		try {
			publisher = GHNContext.getImplementation(ISPublisher.class); 
			for (GCUBEScope scope : scopes)
				publisher.registerGCUBEResource(instance, scope, manager);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}

		return instance;
	}

	public GCUBERunningInstance unpublish(Set<GCUBEScope> scopes, GCUBESecurityManager manager) throws Exception {
		ISPublisher publisher = null;//fetch publisher implementation
		try {
			publisher = GHNContext.getImplementation(ISPublisher.class); 
			for (GCUBEScope scope : scopes) {
				instance.removeScope(scope);
				publisher.removeGCUBEResource(instance.getID(), GCUBERunningInstance.TYPE, scope, manager);
			}
			//updated the instance in all the remaining scopes (if any)
			if (instance.getScopes().values().size() > 0) {
				for (GCUBEScope scope : instance.getScopes().values()) 
					publisher.removeGCUBEResource(instance.getID(), instance.getType(), scope, manager);
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		return this.instance;
	}
}


