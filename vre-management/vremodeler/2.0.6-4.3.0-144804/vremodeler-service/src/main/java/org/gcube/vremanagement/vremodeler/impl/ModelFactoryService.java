package org.gcube.vremanagement.vremodeler.impl;

import java.util.List;
import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.types.VOID;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.vremodeler.db.DBInterface;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.VRE;
import org.gcube.vremanagement.vremodeler.stubs.GetExistingNamesResponseMessage;
import org.gcube.vremanagement.vremodeler.stubs.Report;
import org.gcube.vremanagement.vremodeler.stubs.ReportList;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

public class ModelFactoryService{
	
	private GCUBELog logger = new GCUBELog(ModelFactoryService.class);
	private static final UUIDGen uuidGen = UUIDGenFactory.getUUIDGen();
	
	protected ServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}
	
	/**
	 * implementation of createResource method 
	 * @param request creation request	
	 * @return the EndpointReference pointing to the resource
	 * @throws GCUBEFault if something fails
	 */
	public EndpointReferenceType createResource() throws GCUBEFault {
		String id=uuidGen.nextUUID();
		logger.trace("resource "+id+" created");
		ModelerResource mr;
		try{
			ModelerContext pctx= ModelerContext.getPortTypeContext();
			mr=(ModelerResource)pctx.getWSHome().create(pctx.makeKey(id), id);
			mr.store();
			return mr.getEPR();
		}catch (Exception e){logger.error("error creating resource",e); throw new GCUBEFault(e);}
	}
	
	
	
	/**
	 * return the existing DL Name
	 * 
	 * @param request void
	 * @return array of string with existing VRE names
	 * @throws RemoteException -
	 */
	public GetExistingNamesResponseMessage getExistingNamesVREs(VOID arg) throws GCUBEFault {
		try{
			Dao<VRE, String> vreDao =
		            DaoManager.createDao(DBInterface.connect(), VRE.class);
			List<VRE> vres =vreDao.query(vreDao.queryBuilder().selectColumns("name").prepare());
			String[] toReturn= new String[vres.size()]; 
			for (int i =0 ; i<vres.size(); i++){
				logger.trace("found name "+vres.get(i).getName());
				toReturn[i]= vres.get(i).getName();
			}
			GetExistingNamesResponseMessage response = new GetExistingNamesResponseMessage();
			response.setNames(toReturn);
			return response;
		}catch(Exception e) {logger.error("error on DB",e); throw new GCUBEFault(e);}
	}
	
	
	
	
	/**
	 * Initialize the DB
	 * 
	 * @param request void
	 * @return void
	 * @throws GCUBEFault thrown if something fails 
	 */
	public void initDB(VOID arg) throws GCUBEFault {
		logger.debug("initDB method");
		
		Thread t= new Thread(){
			public void run(){
				try{
					ServiceContext.getContext().intializeDB();
				}catch(Exception e){
					logger.error("DB inizialization failed",e);
				}
			}
		};
			
		t.start();
	}
	
	
	/**
	 * 
	 * @param request void
	 * @return String
	 * @throws RemoteException -
	 */
	public ReportList getAllVREs(VOID arg)  throws GCUBEFault{
		
		try{
			Dao<VRE, String> vreDao =
		            DaoManager.createDao(DBInterface.connect(), VRE.class);
			List<VRE> vres =vreDao.queryForAll();
			Report[] toReturn= new Report[vres.size()]; 
			for (int i =0 ; i<vres.size(); i++){
				VRE vreItem = vres.get(i); 
				Report reportItem= new Report(vreItem.getDescription(), vreItem.getId(), vreItem.getName(), vreItem.getStatus());
				logger.trace("found vre "+vreItem.getName()+" with state "+vreItem.getStatus());
				toReturn[i] = reportItem;				
			}
			return new ReportList(toReturn);
		}catch(Exception e) {logger.error("error on DB",e); throw new GCUBEFault(e);}
		
		
	}

		
	/**
	 * 
	 * remove the DL instance
	 * 
	 * @param request the id of VRE to remove
	 * @return void
	 * @throws RemoteException -
	 */
	public VOID removeVRE(String id) throws GCUBEFault{
		logger.trace("Deleting resource with id "+id);
		try{
			ModelerContext pctx= ModelerContext.getPortTypeContext();
			//destroy the resource;
			Dao<VRE, String> vreDao =
		            DaoManager.createDao(DBInterface.connect(), VRE.class);
			vreDao.deleteById(id);
			pctx.getWSHome().remove(pctx.makeKey(id));
			return new VOID();
		}catch(Exception e){
			logger.error("error removing resource",e);
			throw new GCUBEFault(e);
		}
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws GCUBEFault
	 */
	public EndpointReferenceType getEPRbyId(String id) throws GCUBEFault{
		ModelerContext pctx= ModelerContext.getPortTypeContext();
		try {
			return pctx.getWSHome().find(pctx.makeKey(id)).getEPR();
		} catch (Exception e) {
			logger.error("resource with id "+id+" not found",e);
			throw new GCUBEFault(e,"resource with id "+id+" not found");
		}
	}


	
	
}
