package org.gcube.vremanagement.vremodeler.impl.deploy;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.resources.kxml.KGCUBEResource;
import org.gcube.vremanagement.resourcemanager.stubs.binder.AddResourcesParameters;
import org.gcube.vremanagement.resourcemanager.stubs.binder.PackageItem;
import org.gcube.vremanagement.resourcemanager.stubs.binder.ResourceItem;
import org.gcube.vremanagement.resourcemanager.stubs.binder.ResourceList;
import org.gcube.vremanagement.resourcemanager.stubs.binder.SoftwareList;
import org.gcube.vremanagement.resourcemanager.stubs.scontroller.CreateScopeParameters;
import org.gcube.vremanagement.resourcemanager.stubs.scontroller.OptionsParameters;
import org.gcube.vremanagement.resourcemanager.stubs.scontroller.ScopeControllerPortType;
import org.gcube.vremanagement.resourcemanager.stubs.scontroller.ScopeOption;
import org.gcube.vremanagement.vremodeler.db.DBInterface;
import org.gcube.vremanagement.vremodeler.impl.ModelerContext;
import org.gcube.vremanagement.vremodeler.impl.ModelerResource;
import org.gcube.vremanagement.vremodeler.impl.ModelerService;
import org.gcube.vremanagement.vremodeler.impl.ServiceContext;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.FunctionalityPersisted;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.GenericResource;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.Ghn;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.ResourceInterface;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.RunningInstance;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.Service;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.VRE;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.VreFunctionalityRelation;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.VreGhnRelation;
import org.gcube.vremanagement.vremodeler.impl.util.ReportFiller;
import org.gcube.vremanagement.vremodeler.impl.util.ResourceManagerPorts;
import org.gcube.vremanagement.vremodeler.impl.util.ServicePair;
import org.gcube.vremanagement.vremodeler.impl.util.Util;
import org.gcube.vremanagement.vremodeler.resources.ResourceDefinition;
import org.gcube.vremanagement.vremodeler.utils.reports.DeployReport;
import org.gcube.vremanagement.vremodeler.utils.reports.Status;
import org.globus.wsrf.ResourceException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;


public class DeployVRE extends Thread{

	private static GCUBELog logger= new GCUBELog(ModelerService.class);
	private ModelerResource wsResource= null;
	private String resourceId;
	private GCUBEScope startingScope;
	private VRE vre=null;	
	//private CollectionResourceCreation collectionResourceCreation;
	private Dao<VRE, String> vreDao; 
	private String targetScope;
	private ResourceManagerPorts ports;
	
	public DeployVRE(String resourceId, GCUBEScope scope) throws GCUBEFault, Exception{
		
		this.resourceId=resourceId;
		this.startingScope= scope;
		try{
			
			vreDao =
		            DaoManager.createDao(DBInterface.connect(), VRE.class);
			
			vre= vreDao.queryForId(resourceId);
			//collectionResourceCreation = new CollectionResourceCreation(vre.getId(), vre.getName());
			targetScope = this.startingScope+"/"+vre.getName();
			logger.trace("target scope is "+targetScope);
			
		}catch (Exception e) {
			logger.error("error retrieving the VRE with id "+resourceId,e);
			throw e;
		}
	}
	
	private ModelerResource getResource() throws ResourceException{
		if (wsResource==null) wsResource= (ModelerResource)ModelerContext.getPortTypeContext().getWSHome().find(ModelerContext.getPortTypeContext().makeKey(resourceId));
		return wsResource;
	}
	
	/**
	 * 
	 */
	public void run(){
		try {
			getResource().setDeployReport(new DeployReport());
			
			//reports initialization
			getResource().getDeployReport().setStatus(Status.Running);
					
			getResource().store();
			vre.setStatus(Status.Running.toString());
			vreDao.update(vre);
			
			ports=ResourceManagerPorts.get(this.startingScope);
			
			String[] ghnsToUse=null;
			if (getResource().isUseCloud()){
				DeployGHNsOnCloud ghnOnCloud = new DeployGHNsOnCloud(getResource().getNumberOfVMsForCloud(), vre.getName());
				getResource().getDeployReport().setCloudDeployingReport(ghnOnCloud.getReport());
				try{
					ghnsToUse = ghnOnCloud.run();
				}catch (Exception e) {
					logger.error("error creating VMs on cloud",e);
					getResource().getDeployReport().getCloudDeployingReport().setStatus(Status.Failed);
					throw new Exception("error creating VMs on cloud",e);
				}
			}else {
				getResource().getDeployReport().getCloudDeployingReport().setStatus(Status.Skipped);
				//retrieves all ghn selected in this vre
				Dao<Ghn, String> ghnDao =
			            DaoManager.createDao(DBInterface.connect(), Ghn.class);
				Dao<VreGhnRelation, String> vreGhnDao =
			            DaoManager.createDao(DBInterface.connect(), VreGhnRelation.class);
				
				QueryBuilder<VreGhnRelation, String> vreGhnQb = vreGhnDao.queryBuilder();
				vreGhnQb.selectColumns(VreGhnRelation.GHN_ID_FIELD);
				SelectArg userSelectArg = new SelectArg();
				vreGhnQb.where().eq(VreGhnRelation.VRE_ID_FIELD, userSelectArg);
				QueryBuilder<Ghn, String> ghnQb = ghnDao.queryBuilder();
				ghnQb.where().in(Ghn.ID_FIELDNAME, vreGhnQb);
				ghnQb.prepare().setArgumentHolderValue(0, vre.getId());
				List<Ghn> ghnList = ghnQb.query();
				ghnsToUse = new String[ghnList.size()];
				int i =0;
				for (Ghn selectedGHN: ghnQb.query())
					ghnsToUse[i++]= selectedGHN.getHost();
			}

			if (createVRE(ghnsToUse)){
					
					getResource().getDeployReport().getFunctionalityDeployingReport().setStatus(Status.Finished);
					getResource().getDeployReport().getResourceDeployingReport().setStatus(Status.Finished);
					getResource().getDeployReport().setStatus(Status.Finished);
					getResource().store();
					vre.setStatus(Status.Deployed.toString());
					vreDao.update(vre);
					logger.info("Deploying of the VRE with id "+this.resourceId+"  FINISHED");
					
			}
			else{
				logger.info("Deploying of the VRE with id "+this.resourceId+" FAILED");
				throw new Exception("Error Deployng the VRE");
			}
		}catch(Exception e){
			if (e instanceof ConnectException)
				ResourceManagerPorts.resetPorts();
			try {
				
				vre.setStatus(Status.Failed.toString());
				vreDao.update(vre);
				getResource().getDeployReport().setStatus(Status.Failed);
				getResource().store();
			} catch (Exception e1) {
				logger.error("impossible to update the VRE Status",e1);
			}
			try{
				logger.trace("trying to dispose the scope "+targetScope);
				ports.getScopeController().disposeScope(targetScope);
			}catch (Exception e2) {
				logger.error("error during rollback: cannot dispose the scope created "+targetScope,e2);
			}
			
			logger.error("Error deploying the VRE with id "+this.resourceId+" ",e);
		}
	}
	
		
	//@SuppressWarnings("static-access")
	private boolean createVRE(String[] ghnsToUse) throws Exception{
		logger.trace("running the deployVRE");
	
		Dao<VreFunctionalityRelation, String> vreFunctionalityDao =
	            DaoManager.createDao(DBInterface.connect(), VreFunctionalityRelation.class);
		Dao<FunctionalityPersisted, Integer> functionalityDao =
	            DaoManager.createDao(DBInterface.connect(), FunctionalityPersisted.class);
		
		//report initialization
		ReportFiller.initializeFunctionalityForReport(getResource().getDeployReport().getFunctionalityDeployingReport(),this.resourceId, vreFunctionalityDao, functionalityDao);
		ReportFiller.initializeResourcesForReport(getResource().getDeployReport().getResourceDeployingReport());
		createScope(ports.getScopeController());
		logger.trace("scope created");
		AddResourcesParameters arp= new AddResourcesParameters();
		ResourceList rl= new ResourceList();
		List<ResourceItem> resItemList= new ArrayList<ResourceItem>();
			
		//Adding the resources to the new VRE
	
		
		Dao<RunningInstance, String> runningInstanceDao =
	            DaoManager.createDao(DBInterface.connect(), RunningInstance.class);
		
		Set<ServicePair> missingServices= new HashSet<ServicePair>();
		
		List<FunctionalityPersisted> functs = Util.getSelectedFunctionality(vreFunctionalityDao, functionalityDao, resourceId);
		
		logger.trace("adding functionalities");
		
		for (FunctionalityPersisted functionality: functs){
			if (functionality.getParent()!=null){
				for (ResourceDefinition<?> resourceEntry : functionality.getSelectableResources()){
					for (ResourceInterface resource: resourceEntry.getResources()){
						if (vre.getSelectableResourcesMap().containsKey(resourceEntry.getId()) && 
								vre.getSelectableResourcesMap().get(resourceEntry.getId()).contains(resource.getId())){
							logger.trace("adding selectable "+resource.getId()+" "+resource.getResourceType());	
							resItemList.add(new ResourceItem(resource.getId(), resource.getResourceType()));
							ReportFiller.addResourceToReport(resource.getId(), resource.getResourceType(), getResource().getDeployReport().getResourceDeployingReport());
						}
					}
				}
				for (ResourceDefinition<?> resourceEntry : functionality.getMandatoryResources())
					for (ResourceInterface resource: resourceEntry.getResources()){
						logger.trace("adding mandatory "+resource.getId()+" "+resource.getResourceType());	
						resItemList.add(new ResourceItem(resource.getId(), resource.getResourceType()));
						ReportFiller.addResourceToReport(resource.getId(), resource.getResourceType(), getResource().getDeployReport().getResourceDeployingReport());
					}
				SelectArg serviceClassArg = new SelectArg();
				SelectArg serviceNameArg = new SelectArg();
				PreparedQuery<RunningInstance> preparedQuery = runningInstanceDao.queryBuilder().where().eq("serviceClass",serviceClassArg).and().eq("serviceName", serviceNameArg).prepare();

				for (ServicePair servicePair: functionality.getServices()){
					preparedQuery.setArgumentHolderValue(0, servicePair.getServiceClass());
					preparedQuery.setArgumentHolderValue(1, servicePair.getServiceName());
					logger.trace("prepared query is "+preparedQuery.getStatement());
					logger.trace("checking for service "+servicePair.getServiceName()+" "+servicePair.getServiceClass());
					List<RunningInstance> runningInstances= runningInstanceDao.query(preparedQuery);
					if (runningInstances.size()>0)
						for (RunningInstance ri: runningInstances){
							resItemList.add(new ResourceItem(ri.getId(), GCUBERunningInstance.TYPE));
							ReportFiller.addResourceToReport(ri.getId(), GCUBERunningInstance.TYPE, getResource().getDeployReport().getResourceDeployingReport());
							logger.trace("adding ris  "+ri.getClass()+" "+ri.getServiceName());
						}
					else missingServices.add(servicePair);
				}
			}
		}
		
				
		//adding the other needed resources
		resItemList.addAll(addGenericResources());
		
		rl.setResource(resItemList.toArray(new ResourceItem[0]));
		arp.setResources(rl);
		
		logger.trace("missing services are "+missingServices.size());
		
		SoftwareList serviceList= new SoftwareList();
		
		//retrieve services
		List<PackageItem> packageItems = retrieveSoftwareToDeploy(missingServices);
		
		//sets the GHNs
		if (packageItems.size()>0){
			serviceList.setSoftware(packageItems.toArray(new PackageItem[packageItems.size()]));
				
			logger.trace("adding hosts: "+Arrays.toString(ghnsToUse));
			//add GHNs
			serviceList.setSuggestedTargetGHNNames(ghnsToUse);
			arp.setSoftware(serviceList);
		}
		
		arp.setTargetScope(targetScope);
		
		String reportId=ports.getBinder().addResources(arp);
		
		String report=getDeployReport(reportId);		
	
		
		logger.info("is something failed ?"+Util.isSomethingFailed(report));
		logger.trace("final deploy report is :  "+report);
		return !Util.isSomethingFailed(report);
	
	}
	
	
	private String getDeployReport(String reportId) throws Exception{
		String report = null;
		do{
			try{
				Thread.sleep(20000);
			}catch (Exception e) {}
			report=ports.getReporter().getReport(reportId);
			getResource().getDeployReport().getFunctionalityDeployingReport().setResourceManagerReport(report);
			ReportFiller.reportElaboration(getResource().getDeployReport());
			getResource().store();
		}while (!(Util.isDeploymentStatusFinished(report)));
		return report;
		
	}
	
	private List<PackageItem> retrieveSoftwareToDeploy(Set<ServicePair> missingServices) throws Exception{
		List<PackageItem> packageItems = new ArrayList<PackageItem>();

		Dao<Service, String> serviceDao =
				DaoManager.createDao(DBInterface.connect(), Service.class);
		SelectArg serviceClassArg = new SelectArg();
		SelectArg serviceNameArg = new SelectArg();
		PreparedQuery<Service> preparedQuery = serviceDao.queryBuilder().where().eq("serviceClass",serviceClassArg).and().eq("serviceName", serviceNameArg).prepare();

		for (final ServicePair sp : missingServices){
			preparedQuery.setArgumentHolderValue(0, sp.getServiceClass());
			preparedQuery.setArgumentHolderValue(1, sp.getServiceName());
			List<Service> services = serviceDao.query(preparedQuery);
			if (services.size()==0) throw new Exception("service "+sp.getServiceClass()+" "+sp.getServiceName()+" not found in the IS");
			Service latestService = null;
			for (Service service : services){
				if (latestService !=null){
					if (Util.isVersionGreater(service.getPackageVersion(), latestService.getPackageVersion()))
						latestService = service;
				}else latestService = service;
			}
			PackageItem pi = new PackageItem();
			pi.setPackageName(latestService.getPackageName());
			pi.setPackageVersion(latestService.getPackageVersion());
			pi.setServiceClass(latestService.getServiceClass());
			pi.setServiceName(latestService.getServiceName());
			pi.setServiceVersion(latestService.getVersion());
			logger.trace("adding missing ri "+latestService.getServiceClass()+" "+latestService.getServiceName());
			packageItems.add(pi);
		}


		return packageItems;
	}
	
	private void createScope(ScopeControllerPortType scopeControllerPT) throws Exception{
		
		CreateScopeParameters scopeParameter=new CreateScopeParameters();
			
		logger.trace("create scope with target scope "+targetScope);
		
		//TODO: set security Enabled or Not
		OptionsParameters optionParameters = new OptionsParameters(new ScopeOption[]{new ScopeOption("designer", vre.getVreDesigner()), new ScopeOption("manager", vre.getVreManager()),
				new ScopeOption("description", vre.getDescription()), new ScopeOption("endTime", KGCUBEResource.toXMLDateAndTime(vre.getIntervalTo().getTime())),
				new ScopeOption("startTime", KGCUBEResource.toXMLDateAndTime(vre.getIntervalFrom().getTime()))}, targetScope);
		
				
		scopeParameter.setOptionsParameters(optionParameters);
		scopeParameter.setTargetScope(targetScope);
		scopeControllerPT.createScope(scopeParameter);
		
	}
	


	
	
	
	private List<ResourceItem> addGenericResources() throws Exception{
		List<ResourceItem> resItemList= new ArrayList<ResourceItem>();
		ResourceItem resItem;
		
		Dao<GenericResource, String> grDao = DaoManager.createDao(DBInterface.connect(), GenericResource.class);
				
		for (GenericResource gr: grDao.queryForAll()){
			if (ServiceContext.getContext().getSecondaryTypeGenericResourceRequired().contains(gr.getType())){
				resItem= new ResourceItem();
				resItem.setID(gr.getId());
				resItem.setType(gr.getResourceType());
				resItemList.add(resItem);
				ReportFiller.addResourceToReport(gr.getId(), gr.getResourceType(), getResource().getDeployReport().getResourceDeployingReport());
				logger.trace("adding generic resource: "+gr.getId()+" "+gr.getResourceType());
			}
		}
		return resItemList;
	}
	
	
}
