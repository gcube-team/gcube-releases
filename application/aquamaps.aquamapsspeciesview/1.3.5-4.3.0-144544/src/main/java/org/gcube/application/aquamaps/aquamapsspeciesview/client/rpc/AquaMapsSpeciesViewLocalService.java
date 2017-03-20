package org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc;



import java.util.List;

import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.ClientResource;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.Response;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.SettingsDescriptor;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.SpeciesFilter;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.SpeciesSearchDescriptor;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.save.SaveOperationProgress;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.save.SaveRequest;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath(Tags.localService)
public interface AquaMapsSpeciesViewLocalService extends RemoteService {

	public Response setGenericSearchFilter(String descriptor) throws Exception;
	public SpeciesSearchDescriptor getFilterSettings()throws Exception;
	public Response setAdvancedSpeciesFilter(List<SpeciesFilter> updatedList) throws Exception;
	
	public Response dummyOperation(EnumSerializationForcer forcer) throws Exception;
	public SettingsDescriptor getSessionSettings()throws Exception;
	public Response setSource(ClientResource selected)throws Exception;
	
	public Response retrieveMapPerSpeciesList(List<String> species)throws Exception;
	public SaveOperationProgress saveOperationRequest(SaveRequest request)throws Exception;
	public SaveOperationProgress getSaveProgress()throws Exception;
	
//	public List<String> getDefaultLayers()throws Exception;
	
	public String loadSpeciesByMapsId(String id)throws Exception;
}
