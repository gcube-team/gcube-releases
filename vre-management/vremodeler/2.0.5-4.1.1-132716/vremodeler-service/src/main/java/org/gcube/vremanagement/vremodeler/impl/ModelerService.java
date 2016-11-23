package org.gcube.vremanagement.vremodeler.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.faults.GCUBEUnrecoverableFault;
import org.gcube.common.core.types.VOID;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.vremodeler.db.DBInterface;
import org.gcube.vremanagement.vremodeler.impl.deploy.DeployVRE;
import org.gcube.vremanagement.vremodeler.impl.deploy.UndeployVRE;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.FunctionalityPersisted;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.Ghn;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.ResourceInterface;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.RunningInstance;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.VRE;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.VreFunctionalityRelation;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.VreGhnRelation;
import org.gcube.vremanagement.vremodeler.impl.util.Pair;
import org.gcube.vremanagement.vremodeler.impl.util.ServicePair;
import org.gcube.vremanagement.vremodeler.impl.util.Triple;
import org.gcube.vremanagement.vremodeler.impl.util.Util;
import org.gcube.vremanagement.vremodeler.resources.ResourceDefinition;
import org.gcube.vremanagement.vremodeler.stubs.FunctionalityItem;
import org.gcube.vremanagement.vremodeler.stubs.FunctionalityList;
import org.gcube.vremanagement.vremodeler.stubs.FunctionalityNodes;
import org.gcube.vremanagement.vremodeler.stubs.GHNArray;
import org.gcube.vremanagement.vremodeler.stubs.GHNList;
import org.gcube.vremanagement.vremodeler.stubs.GHNType;
import org.gcube.vremanagement.vremodeler.stubs.GHNTypeMemory;
import org.gcube.vremanagement.vremodeler.stubs.GHNTypeSite;
import org.gcube.vremanagement.vremodeler.stubs.GHNsPerFunctionality;
import org.gcube.vremanagement.vremodeler.stubs.ResourceDescriptionItem;
import org.gcube.vremanagement.vremodeler.stubs.ResourceItem;
import org.gcube.vremanagement.vremodeler.stubs.RunningInstanceMessage;
import org.gcube.vremanagement.vremodeler.stubs.SelectedResourceDescriptionType;
import org.gcube.vremanagement.vremodeler.stubs.SetFunctionality;
import org.gcube.vremanagement.vremodeler.stubs.VREDescription;
import org.gcube.vremanagement.vremodeler.utils.Utils;
import org.gcube.vremanagement.vremodeler.utils.reports.Status;
import org.globus.wsrf.ResourceException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;

public class ModelerService {
	
	private static final GCUBELog logger = new GCUBELog(ModelerService.class);

	protected ModelerResource getResource() throws ResourceException{
		return (ModelerResource) ModelerContext.getPortTypeContext().getWSHome().find();
	}

	
	/**
	 * 
	 * @param request void
	 * @return VoidType
	 * @throws RemoteException -
	 *   
	 */	
	public void setDescription(VREDescription request) throws GCUBEFault{
		try {
			
			Dao<VRE, String> vreDao =
		            DaoManager.createDao(DBInterface.connect(), VRE.class);
			String vreId = getResource().getId();
			if (!request.getEndTime().after(request.getStartTime())) throw new Exception("the end date is before or the same of the start date");
			if (vreDao.idExists(vreId)){
				VRE vre = vreDao.queryForId(vreId);
				vre.setName(request.getName());
				vre.setIntervalFrom(request.getStartTime());
				vre.setIntervalTo(request.getEndTime());
				vre.setDescription(request.getDescription());
				vre.setVreDesigner(request.getDesigner());
				vre.setVreManager(request.getManager());
				vreDao.update(vre);
			} else {
				VRE vre= new VRE(vreId, request.getName(), request.getDescription(),
						request.getDesigner(), request.getManager(), request.getStartTime(),request.getEndTime(), Status.Incomplete.name() ); 
				vreDao.create(vre);
			}
			
		} catch (Exception e) {
			logger.error("an error occurs setting the VRE Description",e);
			throw new GCUBEFault(e);
		}

	}



	/**
	 * 
	 * 
	 * @param request void
	 * @return VoidType
	 * @throws RemoteException -
	 */
	public VREDescription getDescription(VOID var) throws GCUBEFault{
		try {
			Dao<VRE, String> vreDao =
		            DaoManager.createDao(DBInterface.connect(), VRE.class);
			String vreId = getResource().getId();
			VRE vre = vreDao.queryForId(vreId);
			if (vre==null) throw new Exception("the vre is not stored in the DB");
			return new VREDescription(vre.getDescription(), vre.getVreDesigner(),vre.getIntervalTo(), vre.getVreManager(),vre.getName(),vre.getIntervalFrom());
		} catch (Exception e) {
			logger.error("error getting VRE informations",e);
			throw new GCUBEFault(e);
		}
	}

	/**
	 * return an XML containing the GHN list
	 * 
	 * @param request void
	 * @return List of GHNs on the system
	 * @throws @throws GCUBEFault - if the cloud is selected as Architecture or something fails on db
	 */
	public FunctionalityNodes getFunctionalityNodes(VOID var) throws GCUBEFault{
		logger.trace("getFunctionalityNodes method");
		FunctionalityNodes functionalityNodes= new FunctionalityNodes();
		try{
			long start = System.currentTimeMillis();
			Dao<VreGhnRelation, String> vreGhnDao =
		            DaoManager.createDao(DBInterface.connect(), VreGhnRelation.class);
			Dao<Ghn, String> ghnDao =
		            DaoManager.createDao(DBInterface.connect(), Ghn.class);
			Dao<VreFunctionalityRelation, String> vreFunctionalityDao =
		            DaoManager.createDao(DBInterface.connect(), VreFunctionalityRelation.class);
			Dao<FunctionalityPersisted, Integer> functionalityDao =
		            DaoManager.createDao(DBInterface.connect(), FunctionalityPersisted.class);
			Dao<VRE, String> vreDao =
		            DaoManager.createDao(DBInterface.connect(), VRE.class);
		
			VRE vre=getCurrentVRE(vreDao);
			
			//retrieves all ghns
			List<Ghn> allGhn = ghnDao.queryForAll();
						
			Set<Ghn> recomendedGhn = new HashSet<Ghn>();
			List<GHNsPerFunctionality> ghnsPerFunctionalityList = new ArrayList<GHNsPerFunctionality>();
			for (FunctionalityPersisted selectedFunctionality:Util.getSelectedFunctionality(vreFunctionalityDao, functionalityDao, vre.getId())){
				logger.trace("found a selected functionality " +selectedFunctionality.getId());
				if (selectedFunctionality.getParent()!=null){
					Triple<Set<Ghn>, Set<RunningInstanceMessage>, Set<RunningInstanceMessage>> functionalityTriple = getRecomendedGhn(selectedFunctionality);
					logger.trace("FUNC: found services are "+functionalityTriple.getSecond().size());
					logger.trace("FUNC: missing services are "+functionalityTriple.getThird().size());
					logger.trace("FUNC: suggested are "+functionalityTriple.getThird().size());
					ArrayList<GHNType> ghnsType= new ArrayList<GHNType>();	
					for (Ghn ghn: functionalityTriple.getFirst()){
						Pair<RunningInstanceMessage[], Boolean> risPerGhn = retrieveRIsPerGhn(ghn);
						ghnsType.add(new GHNType(ghn.getHost(), ghn.getId(), new GHNTypeMemory(ghn.getDiskSpace(), ghn.getMemoryAvailable()),risPerGhn.getFirst(), ghn.isSecurityEnabled(),
								false, new GHNTypeSite(ghn.getCountry(), ghn.getDomain(), ghn.getLocation())));
					}
					GHNsPerFunctionality ghnPerFunctionality = new GHNsPerFunctionality(selectedFunctionality.getDescription(), functionalityTriple.getSecond().toArray(new RunningInstanceMessage[0]), new GHNList(ghnsType.toArray(new GHNType[ghnsType.size()])), 
							selectedFunctionality.getId(), functionalityTriple.getThird().toArray(new RunningInstanceMessage[0]));
					
					logger.debug("found services: "+Arrays.toString(ghnPerFunctionality.getFoundServices()));
					logger.debug("missing services: "+ghnPerFunctionality.getMissingServices().length);
					ghnsPerFunctionalityList.add(ghnPerFunctionality);
					recomendedGhn.addAll(functionalityTriple.getFirst());
				}
					
			}
			functionalityNodes.setFunctionalities(ghnsPerFunctionalityList.toArray(new GHNsPerFunctionality[ghnsPerFunctionalityList.size()]));
			
			logger.trace("fucntionality node has "+functionalityNodes.getFunctionalities().length+" elements");
			
			List<Ghn> selectedGhn = retrieveSelectedGhns(vreGhnDao, ghnDao);
			
			ArrayList<GHNType> selectableGhnTypes= new ArrayList<GHNType>();	
			for (Ghn ghn: allGhn){
				Pair<RunningInstanceMessage[], Boolean> risPerGhn = retrieveRIsPerGhn(ghn);
				logger.trace(ghn.getHost()+" has "+risPerGhn.getFirst().length + " runningInstances ");
				if(!recomendedGhn.contains(ghn)){
					if (risPerGhn.getSecond()) 
						selectableGhnTypes.add(new GHNType(ghn.getHost(), ghn.getId(), new GHNTypeMemory(ghn.getDiskSpace(), ghn.getMemoryAvailable()),risPerGhn.getFirst(), ghn.isSecurityEnabled(),
								selectedGhn.contains(ghn), new GHNTypeSite(ghn.getCountry(), ghn.getDomain(), ghn.getLocation())));
					else if (selectedGhn.contains(ghn)){
						DeleteBuilder<VreGhnRelation, String> deleteBuilder =vreGhnDao.deleteBuilder();
						deleteBuilder.setWhere(deleteBuilder.where().eq(VreGhnRelation.GHN_ID_FIELD, ghn).and().eq(VreGhnRelation.VRE_ID_FIELD, vre));
						vreGhnDao.delete(deleteBuilder.prepare());
					}
				}
			}
			logger.trace("selectable ghnType are "+selectableGhnTypes.size());
			functionalityNodes.setSelectableGHNs(new GHNList(selectableGhnTypes.toArray(new GHNType[selectableGhnTypes.size()])));
			logger.trace("method took "+(System.currentTimeMillis()-start));
		}catch(Exception e) {
			logger.error("error retreiving GHNs",e);
			throw new GCUBEFault(e);}
		return functionalityNodes;
	}

	private List<Ghn> retrieveSelectedGhns(Dao<VreGhnRelation, String> vreGhnDao, Dao<Ghn, String> ghnDao) throws Exception{
		//retrieves all ghn selected in this vre
		QueryBuilder<VreGhnRelation, String> vreGhnQb = vreGhnDao.queryBuilder();
		vreGhnQb.selectColumns(VreGhnRelation.GHN_ID_FIELD);
		SelectArg userSelectArg = new SelectArg();
		vreGhnQb.where().eq(VreGhnRelation.VRE_ID_FIELD, userSelectArg);
		QueryBuilder<Ghn, String> ghnQb = ghnDao.queryBuilder();
		ghnQb.where().in(Ghn.ID_FIELDNAME, vreGhnQb);
		ghnQb.prepare().setArgumentHolderValue(0, getResource().getId());
		return ghnQb.query();
	}
	
	private Pair<RunningInstanceMessage[], Boolean> retrieveRIsPerGhn(Ghn ghn){
		boolean selectable = true;
		java.util.Collection<RunningInstance> runningInstances = ghn.getRunningInstances();
		int runningInstancesSize = runningInstances.size();
		RunningInstanceMessage[] relatedRis= new RunningInstanceMessage[runningInstancesSize];
		int i =0;
		for (RunningInstance ri: runningInstances){
			ServicePair sp = new ServicePair(ri.getServiceName(), ri.getServiceClass());
			if (!ServiceContext.getContext().getBaseServiceForGhn().contains(sp))
				selectable= false;
			relatedRis[i++]= new RunningInstanceMessage(ri.getServiceClass(), ri.getServiceName());
		}
		return new Pair<RunningInstanceMessage[], Boolean>(relatedRis, selectable);
	}
	

	
	/**
	 * 
	 * returns a Triple of: ghns selected for those functionalities, already found RIs and missing services
	 * 
	 * @param functionalities
	 * @return
	 * @throws Exception
	 */
	
	private Triple<Set<Ghn>, Set<RunningInstanceMessage>, Set<RunningInstanceMessage>> getRecomendedGhn(FunctionalityPersisted ... functionalities) throws Exception{
		Dao<RunningInstance, String> runningInstanceDao =
	            DaoManager.createDao(DBInterface.connect(), RunningInstance.class);
		Set<ServicePair> servicePairSet= new HashSet<ServicePair>();
		
		for (FunctionalityPersisted functionality : functionalities)
			if (functionality.getParent()!=null)
				servicePairSet.addAll(functionality.getServices());
		
		SelectArg serviceClassArg = new SelectArg();
		SelectArg serviceNameArg = new SelectArg();
		PreparedQuery<RunningInstance> preparedQuery = runningInstanceDao.queryBuilder().where().eq("serviceClass",serviceClassArg).and().eq("serviceName", serviceNameArg).prepare();
		
		Set<Ghn> recomendedGhnSet = new HashSet<Ghn>();
		Set<RunningInstanceMessage> missingServices = new HashSet<RunningInstanceMessage>();
		Set<RunningInstanceMessage> foundServices = new HashSet<RunningInstanceMessage>();
		for (ServicePair servicePair: servicePairSet){
			preparedQuery.setArgumentHolderValue(0, servicePair.getServiceClass());
			preparedQuery.setArgumentHolderValue(1, servicePair.getServiceName());
			logger.trace("RI: checking for service "+servicePair.getServiceName()+" "+servicePair.getServiceClass());
			RunningInstanceMessage runningInstanceStub =new RunningInstanceMessage(servicePair.getServiceClass(), servicePair.getServiceName());
			List<RunningInstance> runningInstances= runningInstanceDao.query(preparedQuery);
			if (runningInstances.size()>0){
				logger.debug("RIS: running instance found");
				foundServices.add(runningInstanceStub);
				for (RunningInstance ri: runningInstances){
					logger.trace("adding recomendedGhn");
					recomendedGhnSet.add(ri.getGhn());
				}
			}else{
				logger.debug("RIS: running instance not found");
				missingServices.add(runningInstanceStub);
			}
		}
		
		return new Triple<Set<Ghn>, Set<RunningInstanceMessage>, Set<RunningInstanceMessage>>(recomendedGhnSet, foundServices,  missingServices);
	}
	
	/**
	 * Set the GHNs for deploying
	 *  
	 * @param request array of GHNs ids
	 * @return VoidType -
	 * @throws GCUBEFault - if the clud is selected as Architecture or something fails on db
	 */
	public void setGHNs(GHNArray request) throws GCUBEFault{
		try {
			if (getResource().isUseCloud()) throw new GCUBEFault("deploy on cloud is selected, the ghns cannot be set");
		} catch (ResourceException e1) {
			throw new GCUBEFault(e1);
		}
		try{

			String instanceID=(String) getResource().getId();

			Dao<VreGhnRelation, String> vreGhnDao =
					DaoManager.createDao(DBInterface.connect(), VreGhnRelation.class);
			Dao<Ghn, String> ghnDao =
		            DaoManager.createDao(DBInterface.connect(), Ghn.class);
			Dao<VRE, String> vreDao =
		            DaoManager.createDao(DBInterface.connect(), VRE.class);
			
			DeleteBuilder<VreGhnRelation, String> deleteBuilder = vreGhnDao.deleteBuilder();
			deleteBuilder.setWhere(deleteBuilder.where().eq(VreGhnRelation.VRE_ID_FIELD, instanceID ));
			vreGhnDao.delete(deleteBuilder.prepare());

			VRE vre= vreDao.queryForId(instanceID);
			for (String ghnId: request.getGHNElement())
				vreGhnDao.create(new VreGhnRelation(vre, ghnDao.queryForId(ghnId)));
			
		}catch(Exception e){
			logger.error("error setting GHNs ",e);
			throw new GCUBEFault(e);}

	}
	
	/**
	 * Return an xml with the selectable functionalities
	 * String
	 * 
	 * @param request void
	 * @return an xml String containing selectable functionalities
	 * @throws GCUBEFault -
	 */
	public FunctionalityList getFunctionality(VOID var) throws GCUBEFault {

		List<FunctionalityItem> functionalityItemList= new ArrayList<FunctionalityItem>();
		try {
			Dao<VreFunctionalityRelation, String> vreFunctionalityDao =
		            DaoManager.createDao(DBInterface.connect(), VreFunctionalityRelation.class);
			Dao<FunctionalityPersisted, Integer> functionalityDao =
		            DaoManager.createDao(DBInterface.connect(), FunctionalityPersisted.class);
			Dao<VRE, String> vreDao =
		            DaoManager.createDao(DBInterface.connect(), VRE.class);
			
			
			
			
			VRE vre=getCurrentVRE(vreDao);
			//retrieves root functionalities
			List<FunctionalityPersisted> rootFunctionalities =functionalityDao.query(functionalityDao.queryBuilder().where().isNull(FunctionalityPersisted.PARENT_FIELDNAME).prepare());
			
			List<FunctionalityPersisted> selectedFunctionalities = Util.getSelectedFunctionality(vreFunctionalityDao, functionalityDao, getResource().getId());
			
			for (FunctionalityPersisted rootFuncionality: rootFunctionalities)
				functionalityItemList.add(retrieveFunctionalityItems(rootFuncionality, selectedFunctionalities, functionalityDao, vre));
			
			
		} catch (Exception e) {
			logger.error("error retrieving functionality",e);
			throw new GCUBEFault(e);
		}

		return new FunctionalityList(functionalityItemList.toArray(new FunctionalityItem[functionalityItemList.size()]));
	
	}

	
	

	private FunctionalityItem retrieveFunctionalityItems(FunctionalityPersisted mainFunctionality, List<FunctionalityPersisted> selectedFunctionality,
			Dao<FunctionalityPersisted, Integer> functionalityDao, VRE vre) throws Exception{
		FunctionalityItem functionalityItem= new FunctionalityItem();
		functionalityItem.setId(mainFunctionality.getId());
		functionalityItem.setName(mainFunctionality.getName());
		functionalityItem.setMandatory(mainFunctionality.isMandatory());
		
		if (mainFunctionality.isMandatory()) 
			functionalityItem.setSelected(true);
		else functionalityItem.setSelected(selectedFunctionality.contains(mainFunctionality));
		
		functionalityItem.setDescription(mainFunctionality.getDescription());
		List<FunctionalityItem> subFunctionalitiesItems= new ArrayList<FunctionalityItem>();
		for (FunctionalityPersisted subfunctionality: functionalityDao.query(functionalityDao.queryBuilder().where().eq(FunctionalityPersisted.PARENT_FIELDNAME, mainFunctionality).prepare()) ){
			FunctionalityItem subFunctionalityItem= new FunctionalityItem();
			subFunctionalityItem.setId(subfunctionality.getId());
			subFunctionalityItem.setName(subfunctionality.getName());
			subFunctionalityItem.setDescription(subfunctionality.getDescription());
			subFunctionalityItem.setMandatory(subfunctionality.isMandatory());
			
			if (subfunctionality.isMandatory()) 
				subFunctionalityItem.setSelected(true);
			else subFunctionalityItem.setSelected(selectedFunctionality.contains(subfunctionality));
			
			List<ResourceDescriptionItem> resourcesItemsList = new ArrayList<ResourceDescriptionItem>();
			for (ResourceDefinition<?> resourceEntry :subfunctionality.getSelectableResources()){
				ResourceDescriptionItem resourcesItem = new ResourceDescriptionItem();
				resourcesItem.setId(resourceEntry.getId());
				resourcesItem.setDescription(resourceEntry.getDescription());
				resourcesItem.setMaxSelectable(resourceEntry.getMaxSelectable());
				resourcesItem.setMinSelectable(resourceEntry.getMinSelectable());
				List<ResourceItem> resourceItemList = new ArrayList<ResourceItem>();
				for (ResourceInterface resource: resourceEntry.getResources()){
					boolean selected = false;
					if (vre!=null && vre.getSelectableResourcesMap().containsKey(resourceEntry.getId()))
						selected = vre.getSelectableResourcesMap().get(resourceEntry.getId()).contains(resource.getId());
					resourceItemList.add(new ResourceItem(resource.getDescription(), resource.getId(),
							resource.getName(), selected));
				}
				
				resourcesItem.setResource(resourceItemList.toArray(new ResourceItem[resourceItemList.size()]));
				resourcesItemsList.add(resourcesItem);
			}
			subFunctionalityItem.setSelectableResourcesDescription(resourcesItemsList.toArray(new ResourceDescriptionItem[resourcesItemsList.size()]));
			subFunctionalitiesItems.add(subFunctionalityItem);
		}
		functionalityItem.setChilds(subFunctionalitiesItems.toArray(new FunctionalityItem[subFunctionalitiesItems.size()]));
		return functionalityItem;
	}
	
	
	/**
	 * 
	 * 
	 * 
	 * @return void
	 * @throws GCUBEFault -
	 */
	public void setFunctionality(SetFunctionality request) throws GCUBEFault {
		logger.trace("Set Functionality called");
		try{
			String instanceID=(String) getResource().getId();

			Dao<VreFunctionalityRelation, String> vreFunctionalityDao =
		            DaoManager.createDao(DBInterface.connect(), VreFunctionalityRelation.class);
			Dao<FunctionalityPersisted, Integer> functionalityDao =
		            DaoManager.createDao(DBInterface.connect(), FunctionalityPersisted.class);
			Dao<VRE, String> vreDao =
		            DaoManager.createDao(DBInterface.connect(), VRE.class);
			
			DeleteBuilder<VreFunctionalityRelation, String> deleteBuilder = vreFunctionalityDao.deleteBuilder();
			deleteBuilder.setWhere(deleteBuilder.where().eq(VreFunctionalityRelation.VRE_ID_FIELD, instanceID ));
			vreFunctionalityDao.delete(deleteBuilder.prepare());
			
			VRE vre= getCurrentVRE(vreDao);
			
			//set selected resources
			HashMap<String, ArrayList<String>> selectedMap = new HashMap<String, ArrayList<String>>();
			if (request.getResourcesDescription()!=null){
				for (SelectedResourceDescriptionType resourceDescriptionItem : request.getResourcesDescription()){
					ArrayList<String> resourcesId = new ArrayList<String>();
					if (resourceDescriptionItem.getResourceId()!=null){
						for (String id :resourceDescriptionItem.getResourceId())
							resourcesId.add(id);
					}
					selectedMap.put(resourceDescriptionItem.getDescriptionId(), resourcesId);
				}
			}
			vre.setSelectableResourcesMap(selectedMap);
			vreDao.update(vre);
			for (int functionalityId: request.getFunctionalityIds())
				vreFunctionalityDao.create(new VreFunctionalityRelation(vre, functionalityDao.queryForId(functionalityId)));
			
		}catch(Exception e){
			logger.error("error setting functionalities",e);
			throw new GCUBEFault(e);
		}
	}

	/**
	 * Return an XML with Quality parameter to insert
	 * 
	 * @param request void
	 * @return a XML format String
	 * @throws GCUBEFault -
	 */
	public String getQuality(VOID var) throws GCUBEFault{
		return Util.prepareQualityXML(); 
	}


	/**
	 * Sets Quality parameters
	 * 
	 * @param request
	 * @throws GCUBEFault -
	 */
	public void setQuality(String request) throws GCUBEFault{
		//TODO
	}

	/**
	 * 
	 * set the DL state to Pending
	 * 
	 * @param request
	 * @return
	 * @throws GCUBEFault -
	 */
	public void setVREtoPendingState(VOID var) throws GCUBEFault{
		try{
			Dao<VRE, String> vreDao =
	            DaoManager.createDao(DBInterface.connect(), VRE.class);
			VRE vre = getCurrentVRE(vreDao);
			vre.setStatus(Status.Pending.name());
			vreDao.update(vre);
		}catch(Exception e){throw new GCUBEFault(e);}
	}

	/**
	 * 
	 * @param request
	 * @return
	 * @throws GCUBEFault -
	 */
	public void deployVRE(VOID var) throws GCUBEFault{
		String resourceID; 
		ModelerResource resource;
		VRE vre;
		try {
			resource= getResource();
			resourceID = resource.getId();
			Dao<VRE, String> vreDao =
		            DaoManager.createDao(DBInterface.connect(), VRE.class);
			vre = getCurrentVRE(vreDao);
		} catch (Exception e) {
			logger.error("Error retrieving the Resource Requested",e); throw new GCUBEUnrecoverableFault(e); 
		}
		
		if(Status.valueOf(vre.getStatus())!=Status.Pending) throw new GCUBEFault("cannot deploy this vre, the status of "+vre.getName()+" is "+vre.getStatus());
		
		try{
			DeployVRE deployVREThread= new DeployVRE(resourceID, ServiceContext.getContext().getScope() );
			ServiceContext.getContext().setScope(deployVREThread, ServiceContext.getContext().getScope());		
			logger.trace("Deploy VRE thread started");
			deployVREThread.start();
		}catch (Exception e) {
			logger.error("error trying to deploy ",e);
			throw new GCUBEFault(e);
		}
	}

	/**
	 * 
	 * @param var
	 * @return
	 * @throws Exception
	 */
	public String checkStatus(VOID var) throws Exception{
		logger.trace("is deploy Report null?"+(getResource().getDeployReport()==null));
		return Utils.toXML(getResource().getDeployReport());
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 * @throws RemoteException -
	 */
	public void undeployVRE(VOID var) throws GCUBEFault{
		try{
			UndeployVRE undeploy = new UndeployVRE(getResource().getId());
			ServiceContext.getContext().setScope(undeploy, ServiceContext.getContext().getScope());
			undeploy.start();
		}catch (Exception e) {
			logger.error("error undeploying vre", e);
			throw new GCUBEFault(e);
		}
	}
	
	
	/**
	 * 
	 * @param val boolean
	 */
	public void setUseCloud(boolean val) throws Exception{
		getResource().setUseCloud(val);
		getResource().store();
	}

	/**
	 * 
	 * @param r VOID
	 * @return
	 * @throws Exception
	 */
	public boolean isUseCloud(VOID r) throws Exception{
		return getResource().isUseCloud();
	}
	
	/**
	 * 
	 * @param numberOfVMs
	 * @throws Exception
	 */
	public void setCloudVMs(int numberOfVMs) throws Exception{
		ModelerResource resource = getResource();
		if (!resource.isUseCloud()) throw new Exception("the number of VMs cannot be set, you are not using cloud deployement");
		resource.setNumberOfVMsForCloud(numberOfVMs);
		resource.store();
	}
	
	/**
	 * 
	 * @param r VOID
	 * @return
	 * @throws Exception
	 */
	public int getCloudVMs(VOID r) throws Exception{
		ModelerResource resource = getResource();
		if (!resource.isUseCloud()) throw new Exception("the number of VMs cannot be returned, you are not using cloud deployement");
		return resource.getNumberOfVMsForCloud();
	}
	
	public void renewVRE(Calendar request)throws GCUBEFault {
		try{
			Dao<VRE, String> vreDao =
		            DaoManager.createDao(DBInterface.connect(), VRE.class);
			VRE vre= getCurrentVRE(vreDao);
			if (!request.after(vre.getIntervalTo())) throw new Exception("the new date is before the exising expiring date");
			vre.setIntervalTo(request);
			vreDao.update(vre);
		}catch (Exception e) {
			logger.error("error renewing the VRE expire time",e);
			throw new GCUBEFault("error renewing the VRE expire time");
		}
	}
	
	private VRE getCurrentVRE(Dao<VRE, String> vreDao) throws Exception{
		String vreId = getResource().getId();
		VRE vre = vreDao.queryForId(vreId);
		if (vre == null) {
			logger.warn("vre with id "+vreId+" has not been created");
			throw new GCUBEFault("vre with id "+vreId+" has not been created");
		}
		return vre;
	}
}
