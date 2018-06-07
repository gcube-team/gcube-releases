package org.gcube.application.aquamaps.aquamapsportlet.client.rpc;

import java.util.List;

import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientEnvelope;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientObject;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.Msg;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AquaMapsPortletRemoteServiceAsync {
//	public void getResourceDetails(int id, Resource.Type type,AsyncCallback<Resource> result);	


public void getAquaMapsObject(int id,boolean fetchInDb,AsyncCallback<ClientObject> callback);
	

	public void getEnvelope(String speciesId,boolean loadCustomization,AsyncCallback<ClientEnvelope> callback);
	
	public void saveAquaMapsItem(List<Integer> objectId, String name, String destinationBasketId, AsyncCallback<Integer> callback);
	public void reCalculateEnvelopeFromCellIds(List<String> cellsId,String SpeciesId, AsyncCallback<ClientEnvelope> callback);
	public void reCalculateGoodCells(String bb, String faoSelection,String speciesId, boolean useBottom, boolean useBounding,boolean useFAO, AsyncCallback<ClientEnvelope> callback);
	public void deleteSubmittedById(List<Integer> submittedId,AsyncCallback<Integer> callback);
	void submitJob(String title, AsyncCallback<Msg> callback);
	void saveLayerItem(String url, String mimeType, String name,
			String destinationBasketId, AsyncCallback<Msg> callback);

}
