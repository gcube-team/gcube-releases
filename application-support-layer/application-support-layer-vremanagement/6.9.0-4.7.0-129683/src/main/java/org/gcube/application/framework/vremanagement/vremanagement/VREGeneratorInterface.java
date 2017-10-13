package org.gcube.application.framework.vremanagement.vremanagement;

import java.rmi.RemoteException;
import java.util.List;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.vremanagement.vremodel.cl.stubs.types.FunctionalityItem;
import org.gcube.vremanagement.vremodel.cl.stubs.types.FunctionalityNodes;
import org.gcube.vremanagement.vremodel.cl.stubs.types.GHN;
import org.gcube.vremanagement.vremodel.cl.stubs.types.GHNsPerFunctionality;
import org.gcube.vremanagement.vremodel.cl.stubs.types.SelectedResourceDescriptionType;
import org.gcube.vremanagement.vremodel.cl.stubs.types.VREDescription;
import org.gcube.vremanagement.vremodeler.utils.reports.DeployReport;


public interface VREGeneratorInterface {


	/**
	 * @return what the vVRE modeler returns
	 * @throws RemoteException
	 */
	DeployReport checkVREStatus() throws RemoteException;

	/**
	 * @throws RemoteException
	 */
	void deployVRE() throws RemoteException;

	/**
	 * @return what the vVRE modeler returns
	 * @throws RemoteException
	 */
	List<GHN> getGHNs() throws RemoteException;

	/**
	 * @return what the vVRE modeler returns
	 * @throws RemoteException
	 */
	VREDescription getVREModel() throws RemoteException;

	/**
	 * @return what the vVRE modeler returns
	 * @throws RemoteException
	 */
	List<FunctionalityItem> getFunctionality() throws Exception;
	
	
	FunctionalityNodes getSelectedFunctionality() throws Exception;

	
	/**
	 * @param selectedGHNIds the GHNs selected
	 * @throws RemoteException
	 */
	void setGHNs(String[] selectedGHNIds) throws RemoteException;
	
	/**
	 * 
	 * @return GHNsPerFunctionality
	 */
	GHNsPerFunctionality[] getGHNsPerFunctionality() throws RemoteException;
	/**
	 * @param VREName the VRE name
	 * @param VREDescription a description for the VRE
	 * @param VREDesigner the VRE designer
	 * @param VREManager the VRE manager
	 * @param startTime start time
	 * @param endTime end time
	 * @throws RemoteException
	 */
	void setVREModel(String VREName, String VREDescription, String VREDesigner, String VREManager, long startTime, long endTime) throws RemoteException;

	/**
	 * @throws RemoteException
	 */
	 void setVREtoPendingState()
			throws RemoteException;

	/**
	 * @param csIDElement
	 * @param functionalityIDElement
	 * @throws RemoteException
	 */
	void setFunctionality(Integer[] funcIds, SelectedResourceDescriptionType[] selResDesc) throws RemoteException;

	/**
	 * @return
	 */
	String getVREepr(); 
	
	boolean isCloudAvailable();
	
	boolean setCloudDeploy(int virtualMachines);
	
	boolean isCloudSelected();
	
	void removeVRE(ASLSession session, String id);
	
	void undeployVRE(String id);
	
	int getCloudVMSelected();
}
