package org.gcube.application.aquamaps.aquamapsportlet.client.rpc;

import java.util.List;

import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientEnvelope;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientObject;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.Msg;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;



@RemoteServiceRelativePath("AquaMapsPortletRemoteService")
public interface AquaMapsPortletRemoteService extends RemoteService{

//	public Resource getResourceDetails(int id, Resource.Type type)throws Exception;

	public Msg submitJob(String title)throws Exception;
	public ClientObject getAquaMapsObject(int id,boolean fetchInDb)throws Exception;
	
	public ClientEnvelope getEnvelope(String speciesId,boolean loadCustomization)throws Exception;

	public Integer saveAquaMapsItem(List<Integer> objectId, String name, String destinationBasketId) throws Exception;
	public Msg saveLayerItem(String url ,String mimeType, String name, String destinationBasketId) throws Exception;
	
	public ClientEnvelope reCalculateEnvelopeFromCellIds(List<String> cellsId,String SpeciesId)throws Exception;
	public ClientEnvelope reCalculateGoodCells(String bb, String faoSelection,String speciesId, boolean useBottom, boolean useBounding,boolean useFAO)throws Exception;	
	
	public Integer deleteSubmittedById(List<Integer> submittedId)throws Exception;
}
