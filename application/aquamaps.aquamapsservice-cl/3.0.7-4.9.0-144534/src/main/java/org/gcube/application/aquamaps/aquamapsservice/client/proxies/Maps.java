package org.gcube.application.aquamaps.aquamapsservice.client.proxies;

import java.io.File;
import java.rmi.RemoteException;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Area;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.BoundingBox;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Envelope;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Filter;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Job;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Species;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Submitted;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.PagedRequestSettings;

public interface Maps {

	public Envelope calculateEnvelope(BoundingBox bb,List<Area> areas,String speciesId,boolean useBottom, boolean useBounding, boolean useFAO) throws RemoteException,Exception;

	public Envelope calculateEnvelopeFromCellSelection(List<String> cellIds,String speciesId)throws RemoteException,Exception;

	public int deleteSubmitted(List<Integer> ids)throws RemoteException,Exception;

//	public String getJSONSubmitted(String userName,boolean showObjects,String date,Integer jobId,SubmittedStatus status,ObjectType objType, PagedRequestSettings settings)throws RemoteException,Exception;
//
//	public String getJSONOccurrenceCells(String speciesId, PagedRequestSettings settings)throws RemoteException,Exception;
//
//	public String getJSONPhilogeny(SpeciesOccursumFields level, ArrayList<Field> filters, PagedRequestSettings settings)throws RemoteException,Exception;

	/**wraps getProfile
	 * 
	 * @return
	 * @throws RemoteException,Exception
	 */
	public AquaMapsObject loadObject(int objectId)throws RemoteException,Exception;



//	public String getJSONResources(PagedRequestSettings settings, List<Field> filter)throws RemoteException,Exception;

	public String getJSONSpecies(int hspenId, List<Filter> genericSearch, List<Filter> advancedFilters, PagedRequestSettings settings)throws RemoteException,Exception;

	public File getCSVSpecies(int hspenId, List<Filter> genericSearch, List<Filter> advancedFilters,String userId)throws RemoteException,Exception;
	
	public Species loadEnvelope(String speciesId, int hspenId)throws RemoteException,Exception;

	public void markSaved(List<Integer> submittedIds)throws RemoteException,Exception;

	public void submitJob(Job toSubmit) throws RemoteException,Exception;

	public Submitted loadSubmittedById(int id)throws RemoteException,Exception;
	
}
