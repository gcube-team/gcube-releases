package org.gcube.application.aquamaps.aquamapsservice.client.proxies;

import java.io.File;
import java.nio.charset.Charset;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.client.Constants;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Analysis;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.EnvironmentalExecutionReportItem;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.SourceGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.DataManagementStub;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.CustomQueryDescriptorStubs;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.ExportCSVSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.FieldArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.StringArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.ExportTableRequest;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.ExportTableStatusType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.GenerateMapsRequest;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.GetGenerationLiveReportResponseType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.ImportResourceRequest;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.RemoveHSPECGroupGenerationRequestResponseType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.SetCustomQueryRequest;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.ViewCustomQueryRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.ViewTableRequest;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ExportOperation;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ExportStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.SystemTable;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ViewTableFormat;
import org.gcube.application.aquamaps.aquamapsservice.stubs.utils.Storage;
import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultDataManagement implements DataManagement{

	private static final Logger logger = LoggerFactory.getLogger(DefaultDataManagement.class);

	

	private final ProxyDelegate<DataManagementStub> delegate;

	public DefaultDataManagement(ProxyDelegate<DataManagementStub> delegate) {
		this.delegate=delegate;
	}

	@Override
	public String analyzeTables(final Analysis request) throws RemoteException,
	Exception {
		Call<DataManagementStub,String> call=new Call<DataManagementStub, String>() {

			@Override
			public String call(DataManagementStub endpoint) throws Exception {
				return endpoint.AnalyzeTables(request.toStubsVersion());
			}
		};
		return delegate.make(call);
	}


	@Override
	public String submitRequest(final SourceGenerationRequest request)
			throws RemoteException, Exception {
		Call<DataManagementStub,String> call=new Call<DataManagementStub, String>() {

			@Override
			public String call(DataManagementStub endpoint) throws Exception {
				return endpoint.GenerateHSPECGroup(request.toStubsVersion());
			}
		};
		return delegate.make(call);
	}

	@Override
	public EnvironmentalExecutionReportItem getReport(final List<String> ids)
			throws RemoteException, Exception {
		Call<DataManagementStub,EnvironmentalExecutionReportItem> call=new Call<DataManagementStub, EnvironmentalExecutionReportItem>() {

			@Override
			public EnvironmentalExecutionReportItem call(DataManagementStub endpoint)
					throws Exception {
				GetGenerationLiveReportResponseType report= endpoint.GetGenerationLiveReportGroup(new StringArray(ids));
				return new EnvironmentalExecutionReportItem(report.percent(), report.resourceLoad(), report.resourceMap(), report.elaboratedSpecies());
			}
		};
		return delegate.make(call);
	}


	@Override
	public Integer generateMaps(String author, boolean enableGIS,
			Integer hspecId, List<Field> speciesFilter,
			boolean forceRegeneration) throws RemoteException, Exception {
		final GenerateMapsRequest request= new GenerateMapsRequest();
		request.author(author);
		request.generateLayers(enableGIS);
		request.HSPECId(hspecId);
		request.speciesFilter(new FieldArray(speciesFilter));
		request.forceRegeneration(forceRegeneration);
		Call<DataManagementStub,Integer> call=new Call<DataManagementStub, Integer>() {

			@Override
			public Integer call(DataManagementStub endpoint) throws Exception {
				return endpoint.generateMaps(request);
			}
		};
		return delegate.make(call);
	}


	@Override
	public String removeRequest(String id, boolean deleteData,
			boolean deleteJobs) throws RemoteException, Exception {
		final RemoveHSPECGroupGenerationRequestResponseType request=new RemoveHSPECGroupGenerationRequestResponseType();
		request.requestId(id);
		request.removeTables(deleteData);
		request.removeJobs(deleteJobs);
		Call<DataManagementStub,String> call=new Call<DataManagementStub, String>() {

			@Override
			public String call(DataManagementStub endpoint) throws Exception {
				endpoint.RemoveHSPECGroup(request);
				return "Done";
			}
		};
		return delegate.make(call);
	}

	@Override
	public List<Field> getDefaultSources() throws RemoteException, Exception {
		Call<DataManagementStub,List<Field>> call=new Call<DataManagementStub, List<Field>>() {

			@Override
			public List<Field> call(DataManagementStub pt) throws Exception {
				return pt.GetDefaultSources(new Empty()).theList();
			}
		}; 
		return delegate.make(call);
	}


	@Override
	public Resource updateResource(final Resource toUpdate) throws RemoteException,
	Exception {
		Call<DataManagementStub,Resource> call=new Call<DataManagementStub, Resource>() {

			@Override
			public Resource call(DataManagementStub pt) throws Exception {
				return new Resource(pt.EditResource(toUpdate.toStubsVersion()));
			}
		};
		return delegate.make(call);
	}

	@Override
	public void deleteResource(final int resourceId) throws RemoteException,
	Exception {
		Call<DataManagementStub,Object> call=new Call<DataManagementStub, Object>() {

			@Override
			public Object call(DataManagementStub endpoint) throws Exception {
				endpoint.RemoveResource(resourceId);
				return null;
			}
		};
		delegate.make(call);
	}



	@Override
	public CustomQueryDescriptorStubs setCustomQuery(String userId, String queryString)
			throws RemoteException, Exception {
		final SetCustomQueryRequest request=new SetCustomQueryRequest(userId, queryString);
		Call<DataManagementStub,CustomQueryDescriptorStubs> call=new Call<DataManagementStub, CustomQueryDescriptorStubs>() {

			@Override
			public CustomQueryDescriptorStubs call(DataManagementStub endpoint) throws Exception {
				String id=endpoint.SetCustomQuery(request);
				CustomQueryDescriptorStubs desc=null;
				desc=endpoint.GetCustomQueryDescriptor(id);
				logger.debug("Sent request, response is "+desc);
				int multiplier=1;
				while(((!desc.status().equals(ExportStatus.COMPLETED))&&(!desc.status().equals(ExportStatus.ERROR)))){
					long waitTime=Constants.POLL_WAIT_TIME*multiplier;
					try{
						Thread.sleep(waitTime);
					}catch(InterruptedException e){}
					if(multiplier<Constants.MAX_POLL_MULTIPLIER)multiplier++;
					desc=endpoint.GetCustomQueryDescriptor(id);
					logger.debug("Sent request, response is "+desc);
				}
				logger.debug("Finished polling, result : "+desc);
				return desc;
			}
		};
		return delegate.make(call);
	}


	@Override
	public String viewCustomQuery(String userId, PagedRequestSettings settings)
			throws RemoteException, Exception {
		final ViewCustomQueryRequestType request=new ViewCustomQueryRequestType(userId, settings);

		Call<DataManagementStub,String> call=new Call<DataManagementStub, String>() {

			@Override
			public String call(DataManagementStub endpoint) throws Exception {
				return endpoint.ViewCustomQuery(request);
			}
		};
		return delegate.make(call);
	}



	@Override
	public Integer asyncImportResource(File toImport, String userId,
			ResourceType type, String encoding, Boolean[] fieldsMask,
			boolean hasHeader, char delimiter) throws RemoteException,
			Exception {

		
		String remoteFileId=Storage.storeFile(toImport.getAbsolutePath(), false,userId);
		ExportCSVSettings settings=new ExportCSVSettings(encoding, delimiter+"", hasHeader, Arrays.asList(fieldsMask));
		final ImportResourceRequest request=new ImportResourceRequest(remoteFileId,settings,userId,type);
		Call<DataManagementStub,Integer> call=new Call<DataManagementStub, Integer>() {

			@Override
			public Integer call(DataManagementStub endpoint) throws Exception {				
				return endpoint.ImportResource(request);
			}
		};
		return delegate.make(call);

	}

	
	@Override
	public Resource syncImportResource(File toImport, String userId,
			ResourceType type, String encoding, Boolean[] fieldsMask,
			boolean hasHeader, char delimiter) throws RemoteException,
			Exception {
		Integer resId=this.asyncImportResource(toImport, userId, type, encoding, fieldsMask, hasHeader, delimiter);
		logger.debug("Sent import request, resource Id is : "+resId);
		int multiplier=1;
		Resource res=loadResource(resId);
		while(!res.getStatus().equals(ResourceStatus.Completed)&&!res.getStatus().equals(ResourceStatus.Error)){
			long waitTime=Constants.POLL_WAIT_TIME*multiplier;
			try{
				Thread.sleep(waitTime);
			}catch(InterruptedException e){}
			if(multiplier<Constants.MAX_POLL_MULTIPLIER)multiplier++;
			res=loadResource(resId);
			logger.debug("Importing resource "+res);
		}
		return res;
	}

	
	@Override
	public File exportTableAsCSV(String table,String basketId,
			String user,String toSaveName,ExportOperation operationType) throws RemoteException,Exception{
		
		final ExportTableRequest request=new ExportTableRequest(
				table,
				operationType,
				user,
				basketId,
				toSaveName,
				new ExportCSVSettings(Charset.defaultCharset().toString(),",",true,Arrays.asList(new Boolean[]{true})));
		
		Call<DataManagementStub,String> idCall=new Call<DataManagementStub, String>() {
					
					@Override
					public String call(DataManagementStub endpoint) throws Exception {
						return endpoint.ExportTableAsCSV(request);
					}
				};
		final String exportId= delegate.make(idCall);
		
		Call<DataManagementStub,ExportTableStatusType> statusCall=new Call<DataManagementStub, ExportTableStatusType>() {
			@Override
			public ExportTableStatusType call(DataManagementStub endpoint)
					throws Exception {				
				return endpoint.GetExportStatus(exportId);
			}
			
		}; 
		ExportTableStatusType desc=null;
		int multiplier=1;
		desc=delegate.make(statusCall);
		while(((!desc.status().equals(ExportStatus.COMPLETED))&&(!desc.status().equals(ExportStatus.ERROR)))){
			try{
				Thread.sleep(Constants.POLL_WAIT_TIME*multiplier);
			}catch(InterruptedException e){}
			if(multiplier<Constants.MAX_POLL_MULTIPLIER)multiplier++;
			desc=delegate.make(statusCall);
		}
		if(desc.status().equals(ExportStatus.ERROR)) throw new Exception (desc.errors());
		if(operationType.equals(ExportOperation.TRANSFER))return Storage.getFileById(desc.rsLocator(), true,user);
		else return null;
		
	}



	@Override
	public File loadAnalysisResults(final String id,final String userId) throws RemoteException,
	Exception {
		Call<DataManagementStub,File> call=new Call<DataManagementStub, File>() {

			@Override
			public File call(DataManagementStub endpoint) throws Exception {
				String locator=endpoint.LoadAnalysis(id);						
				return Storage.getFileById(id, true,userId);	
			}
		};
		return delegate.make(call);
	}

	@Override
	public String resubmitGeneration(final String id) throws RemoteException,
	Exception {
		Call<DataManagementStub,String> call=new Call<DataManagementStub, String>() {

			@Override
			public String call(DataManagementStub endpoint) throws Exception {
				return endpoint.ResubmitGeneration(id);
			}
		};
		return delegate.make(call);
	}


	@Override
	public void deleteAnalysis(final String id) throws RemoteException, Exception {
		Call<DataManagementStub,Object> call=new Call<DataManagementStub, Object>() {

			@Override
			public Object call(DataManagementStub endpoint) throws Exception {
				endpoint.DeleteAnalysis(id);
				return null;
			}
		};
		delegate.make(call);
	}

	@Override
	public String getJSONView(final PagedRequestSettings settings, final String tableName,
			final List<Field> filters) throws RemoteException, Exception {
		Call<DataManagementStub,String> call=new Call<DataManagementStub, String>() {

			@Override
			public String call(DataManagementStub endpoint) throws Exception {
				ViewTableRequest request=new ViewTableRequest();
				request.filter(new FieldArray(filters));
				request.format(ViewTableFormat.JSON);
				request.settings(settings);
				request.tablename(tableName);
				return endpoint.viewTable(request);
			}
		};
		return delegate.make(call);
	}

	@Override
	public String getSystemTableName(SystemTable tableType) throws RemoteException,
			Exception {
		switch(tableType){
			case ANALYSIS_REQUESTS : return "analysis_table";
			case DATASOURCES_METADATA : return "meta_sources";
			case OCCURRENCE_CELLS : return "occurrencecells";
			case SPECIES_SUMMARY : return "speciesoccursum";
			case SUBMITTED_MAP_REQUESTS : return "submitted";
			case DATASOURCE_GENERATION_REQUESTS : return "source_generation_requests";
			default : throw new Exception ("Invalid table type "+tableType);
		}
	}
	
	@Override
	public Resource loadResource(final int resId) throws RemoteException, Exception {
		Call<DataManagementStub,Resource> call=new Call<DataManagementStub, Resource>() {
			
			@Override
			public Resource call(DataManagementStub endpoint) throws Exception {
				Resource request=new Resource(ResourceType.HCAF,resId);
				return	new Resource(endpoint.getResourceInfo(request.toStubsVersion()));
			}
		};
		
		return delegate.make(call);
	}
}
