package org.gcube.vremanagement.softwaregateway.impl.packages;

import java.util.List;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.Coordinates;
import org.gcube.vremanagement.softwaregateway.impl.exceptions.ServiceNotAvaiableFault;
import org.gcube.vremanagement.softwaregateway.impl.is.ISProxy;
import org.gcube.vremanagement.softwaregateway.impl.porttypes.Access;
import org.gcube.vremanagement.softwaregateway.impl.porttypes.Registration;
import org.gcube.vremanagement.softwaregateway.impl.porttypes.ServiceContext;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.RepositoryManager;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.RepositoryManagerFactory;

public abstract class Package {
	
	Coordinates coordinates;
	Access access;
	Registration registration;
	protected ISProxy is=null;
	protected RepositoryManager rm;
	protected RepositoryManagerFactory rmf;
	protected GCUBEScope scope;
	protected final GCUBELog logger = new GCUBELog(Package.class);
	
	public abstract String getLocation() throws ServiceNotAvaiableFault;
	
	public abstract String getSALocation() throws ServiceNotAvaiableFault;
	
	public abstract String getDependencies() throws ServiceNotAvaiableFault;
	
	public abstract List<GCubePackage> getPackages() throws ServiceNotAvaiableFault;
	
	public abstract List<GCubePackage> getPlugins() throws ServiceNotAvaiableFault;
	
	public abstract String register() throws ServiceNotAvaiableFault;
	
	public abstract void unregister() throws ServiceNotAvaiableFault;
	
	public Coordinates getCoordinates() {
		return coordinates;
	}
	
	protected void initialize(){
		logger.debug("initialize method");
		scope=ServiceContext.getContext().getScope();
		logger.debug("..with scope: "+scope.getName());
		if(is == null)
			is=new ISProxy(scope, false); //TODO: true does not work
	}

}
