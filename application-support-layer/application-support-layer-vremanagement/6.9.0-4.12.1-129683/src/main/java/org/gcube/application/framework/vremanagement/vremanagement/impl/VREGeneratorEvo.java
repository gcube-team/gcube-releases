package org.gcube.application.framework.vremanagement.vremanagement.impl;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;
import static org.gcube.vremanagement.vremodel.cl.plugin.AbstractPlugin.factory;
import static org.gcube.vremanagement.vremodel.cl.plugin.AbstractPlugin.manager;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.vremanagement.vremanagement.VREGeneratorInterface;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.vremanagement.vremodel.cl.proxy.Manager;
import org.gcube.vremanagement.vremodel.cl.stubs.types.FunctionalityItem;
import org.gcube.vremanagement.vremodel.cl.stubs.types.FunctionalityNodes;
import org.gcube.vremanagement.vremodel.cl.stubs.types.GHN;
import org.gcube.vremanagement.vremodel.cl.stubs.types.GHNsPerFunctionality;
import org.gcube.vremanagement.vremodel.cl.stubs.types.Report;
import org.gcube.vremanagement.vremodel.cl.stubs.types.SelectedResourceDescriptionType;
import org.gcube.vremanagement.vremodel.cl.stubs.types.VREDescription;
import org.gcube.vremanagement.vremodeler.utils.reports.DeployReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author Massimiliano Assante - ISTI-CNR
 *
 */
public class VREGeneratorEvo implements VREGeneratorInterface {

	private static final Logger log = LoggerFactory.getLogger(VREGeneratorEvo.class);
	private static final String VRE_MODELER_SERVICE_NAME = "VREModeler";
	String scope;
	ASLSession session;
	Manager modelPortType;

protected static AtomicInteger vreId = new AtomicInteger(0);

	/**
	 * @param session the d4s session
	 * @param epr the epr
	 */
	public VREGeneratorEvo(ASLSession session, String id) {
		this(session);

		log.info("VREGeneratorEvo called on VRE id " + id + " scope: " + session.getScope());
		this.scope = session.getScope();
		this.session = session;
		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		modelPortType = manager().at(factory().build().getEPRbyId(id)).build();
		ScopeProvider.instance.set(currScope);
	}

	public boolean isVreModelerServiceUp() {
		ScopeProvider.instance.set(scope);
		SimpleQuery query = queryFor(GCoreEndpoint.class);
		query.addCondition("$resource/Profile/ServiceName/text() eq '"+ VRE_MODELER_SERVICE_NAME +"'");
		query.addCondition("$resource/Profile/DeploymentData/Status/text() eq 'ready'");

		DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);

		List<GCoreEndpoint> r = client.submit(query);
		if (r == null || r.isEmpty())
			return false;
		return true;
	}

	/**
	 * @param session the session
	 */
	public VREGeneratorEvo(ASLSession session) {
		super();
		log.info("VREGeneratorEvo scope: " + session.getScope().toString());
		this.scope = session.getScopeName();
		this.session = session;
		modelPortType =  manager().at(factory().build().createResource()).build();
	}
	
	@Override
	public void setVREModel(String name, String desc, String designer, String manager, long startTime, long endTime) throws RemoteException {
		Calendar start = Calendar.getInstance();
		start.setTimeInMillis(startTime);

		Calendar end = Calendar.getInstance();
		end.setTimeInMillis(endTime);

		log.debug("StartTime = " + start.getTime());
		log.debug("EndTime = " + end.getTime());

		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		modelPortType.setDescription(name, desc, designer, manager, start, end);
		ScopeProvider.instance.set(currScope);
	}
	
	@Override
	public String getVREepr() {
		factory().build();
		return null;
	}
	/**
	 * @param session the d4s session
	 * @return the VRE names
	 * 
	 */
	public List<Report> getAllVREs(ASLSession session) {
		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		List<Report> toReturn = factory().build().getAllVREs();
		ScopeProvider.instance.set(currScope);
		return toReturn;
	}

	/**
	 * @param session the d4s session
	 * @param id the id of the VRE to be removed
	 */
	@Override
	public void removeVRE(ASLSession session, String id) {
		log.info("ID RECEIVED TO REMOVE:" + id);
		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		factory().build().removeVRE(id);
		ScopeProvider.instance.set(currScope);

	}
	
	/**
	 * @param session the d4s session
	 * @param id the id of the VRE to be removed
	 */
	@Override
	public void undeployVRE(String id) {
		System.out.println("ID RECEIVED TO UNDEPLOY:" + id);
		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		modelPortType = manager().at(factory().build().getEPRbyId(id)).build();
		log.info("Trying UNDEPLOY:" + id + " name="+ modelPortType.getDescription().name());
		modelPortType.undeployVRE();
		ScopeProvider.instance.set(currScope);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public DeployReport checkVREStatus() throws RemoteException {
		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		DeployReport toReturn = modelPortType.checkStatus();
		ScopeProvider.instance.set(currScope);
		return toReturn;
	}

	/**
	 * 
	 */
	@Override
	public void deployVRE() throws RemoteException {
		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		modelPortType.deployVRE();
		ScopeProvider.instance.set(currScope);

	}

	public String[] getExistingNamesVREs() {
		// TODO Auto-generated method stub
		return null;
	}

	public FunctionalityNodes getSelectedFunctionality() throws Exception {
		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		FunctionalityNodes list = modelPortType.getFunctionalityNodes();
		ScopeProvider.instance.set(currScope);
		return list;
	}

	@Override
	public List<GHN> getGHNs() throws RemoteException {

		log.debug("Asking gHN list to service");
		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		List<GHN> toReturn = new ArrayList<GHN>();
		FunctionalityNodes list = modelPortType.getFunctionalityNodes();

		List<GHN> types = list.selectableGHNs(); //selezionabili per le missing func.

		for (int i = 0; i < types.size(); i++) {
			try {
				log.debug("returned GHN: " + types.get(i).host());
				toReturn.add(new GHN(	types.get(i).id(), 
						types.get(i).host(), 
						types.get(i).securityEnabled(), 
						types.get(i).memory(), 
						types.get(i).site(),
						types.get(i).relatedRIs(),
						types.get(i).selected()));

			} catch (NullPointerException e) {
				e.printStackTrace();
				return toReturn;
			}
		}
		ScopeProvider.instance.set(currScope);
		return toReturn;
	}

	@Override
	public GHNsPerFunctionality[] getGHNsPerFunctionality() throws RemoteException {
		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		FunctionalityNodes list = modelPortType.getFunctionalityNodes();
		ScopeProvider.instance.set(currScope);
		return list.functionalities().toArray(new GHNsPerFunctionality[0]);

	}
	//	RunningInstanceMessage[] ris = list.getFunctionalities()[1].getMissingServices(); //ci sono n RunningInstances (Service) Mancanti
	//	ris[0].
	//	list.getFunctionalities()[1].getGhns(); //verranno aggiunti per la funzionalit


	@SuppressWarnings("unchecked")
	@Override
	public void setFunctionality(Integer[] funcIds, SelectedResourceDescriptionType[] selResDesc) throws RemoteException {
		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		modelPortType.setFunctionality(Arrays.asList(funcIds), Arrays.asList(selResDesc));
		ScopeProvider.instance.set(currScope);
	}

	@Override
	public List<FunctionalityItem> getFunctionality() throws Exception {
		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		List<FunctionalityItem> list = modelPortType.getFunctionalities();
		ScopeProvider.instance.set(currScope);
		return list;
	}

	public String getMetadataRelatedToCollection() throws RemoteException {
		return null;
	}

	/**
	 * first call
	 */
	@Override
	public VREDescription getVREModel() throws RemoteException {
		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		VREDescription desc = modelPortType.getDescription();
		ScopeProvider.instance.set(currScope);
		return desc;
	}


	@SuppressWarnings("unchecked")
	@Override
	public void setGHNs(String[] selectedGHNIds) throws RemoteException {
		modelPortType.setUseCloud(false);
		modelPortType.setGHNs(Arrays.asList(selectedGHNIds));		
	}

	

	public void setVREtoPendingState() throws RemoteException {
		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		modelPortType.setVREtoPendingState();
		ScopeProvider.instance.set(currScope);
	}


	public boolean isCloudAvailable() {
		//TODO: check actual availability
		return true;
	}


	/**
	 * 
	 */
	public boolean isCloudSelected() {
		log.info("isCloudSelected()");
		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		boolean toReturn = modelPortType.isUseCloud();
		ScopeProvider.instance.set(currScope);
		return toReturn;
	}


	public boolean setCloudDeploy(int virtualMachines) {
		log.debug("setUseCloud(true)");
		modelPortType.setUseCloud(true);
		log.debug("setCloudVMs #: " + virtualMachines);
		modelPortType.setCloudVMs(virtualMachines);
		return true;
	}

	/**
	 * 	
	 */
	public int getCloudVMSelected() {
		int toReturn = -1;
		toReturn = modelPortType.getCloudVMs();
		return toReturn;
	}

	
}
