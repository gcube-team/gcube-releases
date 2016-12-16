package org.gcube.application.aquamaps.aquamapsportlet.client.rpc;

import java.util.List;
import java.util.Map;

import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.SpeciesFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientObjectType;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientResourceType;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientArea;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientField;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientFilter;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.Msg;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.SettingsDescriptor;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AquaMapsPortletLocalServiceAsync {

  
	public void setSpeciesFilter(List<ClientFilter> Filter,String filterSummary,AsyncCallback<Msg> result);
	public void clearSpeciesFilter(AsyncCallback<Msg> result);
	

	
	
	public void addToSpeciesSelection(List<String> speciesIds,AsyncCallback<Msg> callback);
	public void addAllFilteredSpecies(int totalCount,String sortColumn,String sortDir,AsyncCallback<Msg> callback);
	public void addToObjectBasket(List<String> speciesIds,String objTitle,AsyncCallback<Msg> callback);
	

	
	public void setEnvelopeCustomization(String id,Map<SpeciesFields,Float> perturbations,AsyncCallback<Msg> callback);	
	public void addToAreaSelection(List<ClientArea> areas,AsyncCallback<Msg> callback);
	public void removeFromAreaSelection(List<ClientArea> toRemove,AsyncCallback<Msg> callback);
	

	public void removeObject(String title,ClientObjectType type, AsyncCallback<Msg> callback);
	
	public void filterSubmitted(String parameter,String value, AsyncCallback<Msg> callback);
		
	
	void createObjectsBySelection(ClientObjectType type, List<String> ids, String title,AsyncCallback<Msg> callback);
	
	void updateObject(String oldTitle, String title, ClientObjectType type, String bBox,
			Float threshold, Boolean gis, AsyncCallback<Msg> callback);
	void getStats(AsyncCallback<SettingsDescriptor> callback);

	
	void changeGisById(List<String> ids, AsyncCallback<Msg> callback);
	void changeGisByType(String type, AsyncCallback<Msg> callback);
	void setSource(int id, ClientResourceType type, AsyncCallback<Msg> callback);
	void filterAreas(Boolean enableFao, Boolean enableLME, Boolean enableEEZ,
			AsyncCallback<Msg> callback);
	void removeSelectionFromBasket(String title, List<String> speciesIds,
			AsyncCallback<Msg> callback);
	void clearEnvelopeCustomization(String id, AsyncCallback<Msg> callback);
	void getImportProgress(AsyncCallback<Integer> callback);
	
}
