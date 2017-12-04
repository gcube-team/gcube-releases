package org.gcube.application.aquamaps.aquamapsservice.client.proxies;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.client.Constants;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.AquaMap;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.File;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.PublisherStub;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.FieldArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.StringArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.GetBulkUpdatesStatusResponseType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.GetJSONSubmittedByFiltersRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.PrepareBulkUpdatesFileRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.RetrieveMapsByCoverageRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.BulkStatus;
import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.gis.datamodel.enhanced.LayerInfo;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultPublisher implements Publisher{

	private static final Logger logger = LoggerFactory.getLogger(DefaultPublisher.class);
	
	private final ProxyDelegate<PublisherStub> delegate;
	

	public DefaultPublisher(ProxyDelegate<PublisherStub> delegate) {
		this.delegate=delegate;
	}
	
	@Override
	public List<File> getFileSetById(final String fileSetId) throws RemoteException,
			Exception {
		Call<PublisherStub,List<File>> call=new Call<PublisherStub, List<File>>() {
			
			@Override
			public List<File> call(PublisherStub endpoint) throws Exception {
				return File.load(endpoint.GetFileSetById(fileSetId));
			}
		};
		return delegate.make(call);
	}
	
	@Override
	public List<File> getFileSetsByCoverage(Resource source, String parameters)
			throws RemoteException, Exception {
		throw new Exception ("Feature not available");
	}
	
	@Override
	public String getJsonSubmittedByFilters(List<Field> filters,
			PagedRequestSettings settings) throws RemoteException, Exception {
		final GetJSONSubmittedByFiltersRequestType request=new GetJSONSubmittedByFiltersRequestType();
		request.filters(new FieldArray(filters));
		request.settings(settings);
		Call<PublisherStub,String> call=new Call<PublisherStub, String>() {
			
			@Override
			public String call(PublisherStub endpoint) throws Exception {
				return endpoint.GetJSONSubmittedByFilters(request);
			}
		};
		return delegate.make(call);
	}
	
	@Override
	public LayerInfo getLayerById(final String layerId) throws RemoteException,
			Exception {
		Call<PublisherStub,LayerInfo> call=new Call<PublisherStub, LayerInfo>() {
			
			@Override
			public LayerInfo call(PublisherStub endpoint) throws Exception {
				return new LayerInfo(endpoint.GetLayerById(layerId));
			}
		};
		return delegate.make(call);
	}
	
	@Override
	public List<LayerInfo> getLayersByCoverage(Resource source,
			String parameters) throws RemoteException, Exception {
		throw new Exception ("Feature not available");
	}
	
	@Override
	public List<AquaMap> getMapsBySpecies(String[] speciesIds,
			boolean includeGis, boolean includeCustom, List<Resource> resources)
			throws RemoteException, Exception {
		final RetrieveMapsByCoverageRequestType request=new RetrieveMapsByCoverageRequestType();
		request.includeCustomMaps(includeCustom);
		request.includeGisLayers(includeGis);
		request.resourceList(Resource.toStubsVersion(resources));
		request.speciesList(new StringArray(Arrays.asList(speciesIds)));
		Call<PublisherStub,List<AquaMap>> call=new Call<PublisherStub, List<AquaMap>>() {
			
			@Override
			public List<AquaMap> call(PublisherStub endpoint)
					throws Exception {
				return AquaMap.load(endpoint.RetrieveMapsByCoverage(request));
			}
		};
		return delegate.make(call);
	}
	
	@Override
	public java.io.File getBulkUpdates(boolean includeGis,
			boolean includeCustom, List<Resource> resources, long fromTime)
			throws RemoteException, Exception {
		final PrepareBulkUpdatesFileRequestType request=new PrepareBulkUpdatesFileRequestType(
				Resource.toStubsVersion(resources),includeCustom,includeGis,fromTime);
		
		Call<PublisherStub,String> prepareCall=new Call<PublisherStub, String>() {
			
			@Override
			public String call(PublisherStub endpoint) throws Exception {
				return endpoint.PrepareBulkUpdatesFile(request);
			}
		};
		final String requestId=delegate.make(prepareCall);
		
		Call<PublisherStub,GetBulkUpdatesStatusResponseType> statusCall=new Call<PublisherStub, GetBulkUpdatesStatusResponseType>() {
			@Override
			public GetBulkUpdatesStatusResponseType call(PublisherStub endpoint)
					throws Exception {
				return endpoint.GetBulkUpdatesStatus(requestId);
			}
		};
		
		GetBulkUpdatesStatusResponseType resp=null;
		int multiplier=1;
		resp=delegate.make(statusCall);
		while(!resp.status().equals(BulkStatus.COMPLETED)&&!resp.status().equals(BulkStatus.ERROR)){
			logger.debug(resp.toString());
			try{
				Thread.sleep(Constants.POLL_WAIT_TIME*multiplier);
			}catch(InterruptedException e){}
			if(multiplier<Constants.MAX_POLL_MULTIPLIER)multiplier++;
			resp=delegate.make(statusCall);
		}
		if(resp.status().equals(BulkStatus.ERROR)) throw new Exception ("Bulk failed, check serverside");
		
		IClient client=new StorageClient(Constants.SERVICE_CLASS, Constants.SERVICE_NAME, Constants.SERVICE_NAME, AccessType.SHARED, MemoryType.VOLATILE).getClient();
		java.io.File temp=java.io.File.createTempFile("bulk", ".xml");
		client.get().LFile(temp.getAbsolutePath()).RFileById(resp.rsLocator());
		return temp;	
	}
}
