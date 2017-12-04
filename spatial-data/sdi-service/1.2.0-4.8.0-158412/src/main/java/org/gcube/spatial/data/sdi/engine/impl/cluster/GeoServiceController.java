package org.gcube.spatial.data.sdi.engine.impl.cluster;

import java.util.Map;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Profile;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.resources.gcore.common.Platform;
import org.gcube.spatial.data.sdi.engine.impl.faults.InvalidServiceEndpointException;
import org.gcube.spatial.data.sdi.engine.impl.faults.OutdatedServiceEndpointException;
import org.gcube.spatial.data.sdi.engine.impl.faults.ServiceInteractionException;
import org.gcube.spatial.data.sdi.engine.impl.is.CachedObject;
import org.gcube.spatial.data.sdi.engine.impl.is.ISUtils;
import org.gcube.spatial.data.sdi.model.credentials.AccessType;
import org.gcube.spatial.data.sdi.model.credentials.Credentials;
import org.gcube.spatial.data.sdi.model.service.GeoServiceDescriptor;
import org.gcube.spatial.data.sdi.model.service.Version;

import lombok.extern.slf4j.Slf4j;



@Slf4j
public abstract class GeoServiceController<T extends GeoServiceDescriptor> {

	protected ServiceEndpoint serviceEndpoint;	
	protected AccessPoint accessPoint;
	protected Map<String,Property> propertyMap;
	protected String baseURL;
	protected Credentials adminAccount;
	protected Version version;
	protected CachedObject<T> cachedDescriptor=null;
	
	public synchronized T getDescriptor() {
		if(cachedDescriptor==null||cachedDescriptor.isValid(500)) {
			cachedDescriptor=new CachedObject<T>(getLiveDescriptor());
		}
		return cachedDescriptor.getTheObject();
	}
	
	protected abstract T getLiveDescriptor();
		
	
	protected abstract AccessPoint getTheRightAccessPoint(ServiceEndpoint endpoint);
	
	public GeoServiceController(ServiceEndpoint serviceEndpoint) throws InvalidServiceEndpointException{
		super();
		log.debug("Instantiating controller for SE {} ",serviceEndpoint);
		setServiceEndpoint(serviceEndpoint);		
	}
	
	public void onUpdateServiceEndpoint() {
		setServiceEndpoint(ISUtils.updateAndWait(serviceEndpoint));
		cachedDescriptor.invalidate();
	}
	
	protected void setServiceEndpoint(ServiceEndpoint toSet) {
		this.serviceEndpoint = toSet;
		
		Profile profile=serviceEndpoint.profile();
		
		accessPoint=getTheRightAccessPoint(serviceEndpoint);
		if(accessPoint!=null) {
		propertyMap=this.accessPoint.propertyMap();
		baseURL=accessPoint.address();
		adminAccount=new Credentials(accessPoint.username(),ISUtils.decryptString(accessPoint.password()),AccessType.ADMIN);		
		}
		Platform platform=profile.platform();
		version=new Version(platform.version(),platform.minorVersion(),platform.revisionVersion());
	}
	
	
	protected abstract void initServiceEndpoint() throws OutdatedServiceEndpointException, ServiceInteractionException;
	
	public void configure() throws ServiceInteractionException {
		try {
			initServiceEndpoint();
		}catch(OutdatedServiceEndpointException e) {
			onUpdateServiceEndpoint();
		}		
	}
	
	
	protected String getSEProperty(String property, boolean mandatory) throws InvalidServiceEndpointException{		
		if(!propertyMap.containsKey(property))
			if(mandatory)throw new InvalidServiceEndpointException("Expected property "+property+" was not found.");
			else return null;
		else {
			Property prop=propertyMap.get(property);
			if(prop.isEncrypted()) return ISUtils.decryptString(prop.value());
			else return prop.value();
		}		
	}
		
	public ServiceEndpoint getServiceEndpoint() {
		return serviceEndpoint;
	}
}
