package org.gcube.application.aquamaps.aquamapsspeciesview.servlet.db;

import java.rmi.RemoteException;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.CompoundMapItem;






public interface DBInterface {

	
	public String getPhylogenyJSON(String level) throws Exception;
	

	public String getMaps(String mapRequestId,PagedRequestSettings settings)throws Exception;
	
	
	public Integer putMaps(String mapRequestId,List<CompoundMapItem> items) throws Exception;
	public void cleanMaps(String requestId)throws Exception;


	boolean isUpToDate();


	void fetchSpecies() throws RemoteException, Exception;
}
