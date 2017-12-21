package org.gcube.spatial.data.sdi.engine.impl.cluster;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.data.transfer.library.DataTransferClient;
import org.gcube.data.transfer.library.client.AuthorizationFilter;
import org.gcube.data.transfer.library.faults.DestinationNotSetException;
import org.gcube.data.transfer.library.faults.FailedTransferException;
import org.gcube.data.transfer.library.faults.InitializationException;
import org.gcube.data.transfer.library.faults.InvalidDestinationException;
import org.gcube.data.transfer.library.faults.InvalidSourceException;
import org.gcube.data.transfer.library.faults.SourceNotSetException;
import org.gcube.data.transfer.model.Destination;
import org.gcube.data.transfer.model.DestinationClashPolicy;
import org.gcube.data.transfer.model.PluginInvocation;
import org.gcube.data.transfer.model.TransferTicket;
import org.gcube.data.transfer.model.plugins.thredds.ThreddsCatalog;
import org.gcube.data.transfer.model.plugins.thredds.ThreddsInfo;
import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.NetUtils;
import org.gcube.spatial.data.sdi.engine.impl.faults.InvalidServiceEndpointException;
import org.gcube.spatial.data.sdi.engine.impl.faults.OutdatedServiceEndpointException;
import org.gcube.spatial.data.sdi.engine.impl.faults.ThreddsOperationFault;
import org.gcube.spatial.data.sdi.engine.impl.is.ISUtils;
import org.gcube.spatial.data.sdi.engine.impl.metadata.GenericTemplates;
import org.gcube.spatial.data.sdi.model.CatalogDescriptor;
import org.gcube.spatial.data.sdi.model.service.ThreddsDescriptor;
import org.glassfish.jersey.client.ClientConfig;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ThreddsController extends GeoServiceController<ThreddsDescriptor> {

	@Override
	protected ThreddsDescriptor getLiveDescriptor() {
		return new ThreddsDescriptor(version,baseURL,Collections.EMPTY_LIST);
	}
	
	@Override
	protected AccessPoint getTheRightAccessPoint(ServiceEndpoint endpoint) {
		for(AccessPoint declaredPoint:endpoint.profile().accessPoints().asCollection()) {
			if(declaredPoint.name().equals(LocalConfiguration.getProperty(LocalConfiguration.THREDDS_SE_REMOTE_MANAGEMENT_ACCESS))) {
				return declaredPoint;				
			}
		}		
		return null;
	}

	public ThreddsController(ServiceEndpoint serviceEndpoint) throws InvalidServiceEndpointException {
		super(serviceEndpoint);		
	}
	
	@Override
	protected void initServiceEndpoint() throws OutdatedServiceEndpointException {
		// TODO Auto-generated method stub
		
	}

	
	public ThreddsInfo getThreddsInfo() {
		String infoPath=getThreddsInfoPath();
		log.info("Loading thredds info from {} ",infoPath);
		WebTarget target=getWebClient().target(infoPath);
		return target.request(MediaType.APPLICATION_JSON).get(ThreddsInfo.class);
	}
	
	
	private void reloadCatalog() throws IOException {
		AccessPoint ap=getTheRightAccessPoint(serviceEndpoint);
		NetUtils.makeAuthorizedCall(ap.address(), ap.username(), ISUtils.decryptString(ap.password()));
		
	}
	
	private String getHostName() {
		return getServiceEndpoint().profile().runtime().hostedOn();
	}
	
	private String getThreddsInfoPath() {
		return "https://"+getHostName()+"/data-transfer-service/gcube/service/Capabilities/pluginInfo/REGISTER_CATALOG";
	}
	
	private Client getWebClient() {
		
		return ClientBuilder.newClient(new ClientConfig().register(AuthorizationFilter.class));		
	}
	
	public ThreddsCatalog publishCatalog(File catalogFile, String reference) throws ThreddsOperationFault {
		
		log.trace("Registering Thredds catalog with reference {} ",reference);
		try {
			AccessPoint ap=getTheRightAccessPoint(getServiceEndpoint());
			
			log.debug("AP address is {} ",ap.address());
			
			DataTransferClient client=DataTransferClient.getInstanceByEndpoint(ap.address());
		
			Destination dest=new Destination();
			dest.setPersistenceId("thredds");
			dest.setDestinationFileName(reference.replace(" ", "_")+".xml");
			dest.setOnExistingFileName(DestinationClashPolicy.REWRITE);
			
			PluginInvocation invocation=new PluginInvocation("REGISTER_CATALOG");
			invocation.setParameters(Collections.singletonMap("CATALOG_REFERENCE", reference));
		
			log.debug("Sending catalog file to Thredds for registration");
			
			client.localFile(catalogFile, dest,invocation);
			
			log.debug("Catalog registered, calling reload.. ");
			
			reloadCatalog();
			ThreddsInfo info=getThreddsInfo();
			log.debug("returned ThreddsInfo is {} ",info);
			return info.getById(reference);
			
		} catch (InvalidSourceException | SourceNotSetException | FailedTransferException | InitializationException
				| InvalidDestinationException | DestinationNotSetException e) {
			throw new ThreddsOperationFault("Unable to register catalog "+reference, e);
		}catch(Exception e) {
			throw new ThreddsOperationFault("Unable to reload catalog "+reference,e);
		}
		
	}
}
