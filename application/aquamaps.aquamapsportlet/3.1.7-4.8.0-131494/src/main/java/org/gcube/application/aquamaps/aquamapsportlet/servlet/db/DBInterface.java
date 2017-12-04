package org.gcube.application.aquamaps.aquamapsportlet.servlet.db;

import java.util.List;

import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientObjectType;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientObject;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Area;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.PagedRequestSettings;






public interface DBInterface {

	
	public String getPhylogenyJSON(String level) throws Exception;
	

	//*****SPECIES
	public String getUserJSONBasket(String userName,int start, int limit,String sortColumn,String sortDirection) throws Exception;
	public List<String> getUserBasketIds(String userName) throws Exception;
		
	public int removeFromBasket(List<String> speciesIds, String userName) throws Exception;
	public int addToUserBasket(List<String> speciesIds, String userName)throws Exception;
	
	
	public String getPerturbation(String speciesId,String userName)throws Exception;
	public void setPerturbation(String speciesIds,String userName,String jsonString)throws Exception;
	public void clearPerturbation(String speciesId, String userName) throws Exception;
	
	//****** Objects Baskets
	public int addToObjectBasket(List<String> speciesIds, String userName,String title)throws Exception;
	public int removeFromObjectBasket(List<String> speciesIds, String userName,String title) throws Exception;
	public String getObjectJSONBasket(String userName, String title,int start,int limit,String sortColumn,String sortDirection)throws Exception;
	public List<String> getObjectBasketIds(String userName, String title)throws Exception;
	public int clearObjectBasket(String userName,String title)throws Exception;
	public int clearBasket(String userName)throws Exception;
	
	//***** Session
	
	public int removeSession(String userName)throws Exception;

	
	
	//***** Objects
	public int removeObjectByTitle(String userName,String title)throws Exception;
	public int removeObjectByType(String userName,ClientObjectType type)throws Exception;
	public int createObjectsBySelection(List<String> ids, String title, ClientObjectType type, float threshold, String bbox, String username)throws Exception;
	

	public int updateObject(String userName,String oldTitle,String title, ClientObjectType type,String bbox, float threshold,boolean gis)throws Exception;
	public int changeGis(String userName,List<String> titles)throws Exception;
	public List<ClientObject> getObjects(String userName)throws Exception;
	
	public String getJSONObjectsByType(String userName,ClientObjectType type,int start,int limit,String sortColumn,String sortDirection)throws Exception;
	
	
	//**** DB & Session stats

	public String getSpecies()throws Exception;	

	public long getBasketCount(String userName)throws Exception;
	public long getObjectBasketCount(String userName,String title)throws Exception;
	public long getObjectCount(String userName)throws Exception;
	public long getObjectCountByType(String userName,ClientObjectType type)throws Exception;
	
	//***** Object Details
	public int fetchGeneratedObjRelatedSpecies(int objId, List<String> speciesIds)throws Exception;
	public String getFetchedJSONBasket(int objId,int start, int limit,String sortColumn,String sortDirection)throws Exception;
	public int deleteFetched(String objId)throws Exception;
	
	
	//**** Area
	public int addToAreaSelection(String user,List<Area> selection)throws Exception;
	public String getJSONAreaSelection(String user,PagedRequestSettings settings)throws Exception;
	public List<Area> getAreaSelection(String user)throws Exception;
	public int removeFromAreaSelection(String user,List<Area> toRemove)throws Exception;
	public String getJSONAreasByType(boolean includeFAO,boolean includeEEZ, boolean includeLME, PagedRequestSettings settings)throws Exception;
	public List<Area> getAreasByType(boolean includeFAO,boolean includeEEZ, boolean includeLME)throws Exception;
	
	
	//***** Fetch Data Thread
//	public int insertSpecies(String JSONString)throws Exception,JSONException;
//	public int importSpeciesOccursumCSV(File csvFile) throws Exception;


	boolean isUpToDate();


	public int fetchSpecies() throws Exception;
	
	
	
}
