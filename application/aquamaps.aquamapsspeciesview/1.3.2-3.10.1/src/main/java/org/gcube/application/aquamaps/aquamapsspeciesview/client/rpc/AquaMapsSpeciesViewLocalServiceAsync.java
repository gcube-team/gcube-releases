package org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc;

import java.util.List;

import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.ClientResource;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.Response;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.SettingsDescriptor;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.SpeciesFilter;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.SpeciesSearchDescriptor;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.save.SaveOperationProgress;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.save.SaveRequest;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AquaMapsSpeciesViewLocalServiceAsync {

	void setGenericSearchFilter(String descriptor,
			AsyncCallback<Response> callback);

	void getFilterSettings(AsyncCallback<SpeciesSearchDescriptor> callback);

	void setAdvancedSpeciesFilter(List<SpeciesFilter> updatedList,
			AsyncCallback<Response> callback);

	void dummyOperation(EnumSerializationForcer forcer,
			AsyncCallback<Response> callback);

	void getSessionSettings(AsyncCallback<SettingsDescriptor> callback);

	void setSource(ClientResource selected, AsyncCallback<Response> callback);

	void retrieveMapPerSpeciesList(List<String> species,
			AsyncCallback<Response> callback);

	void saveOperationRequest(SaveRequest request,
			AsyncCallback<SaveOperationProgress> callback);

	void getSaveProgress(AsyncCallback<SaveOperationProgress> callback);

	void getDefaultLayers(AsyncCallback<List<String>> callback);

	void loadSpeciesByMapsId(String id, AsyncCallback<String> callback);

}
