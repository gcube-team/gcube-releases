package org.gcube.portlets.admin.vredeployer.client;

import java.util.List;

import org.gcube.portlets.admin.vredeployer.client.model.VREFunctionalityModel;
import org.gcube.portlets.admin.vredeployer.shared.GHNProfile;
import org.gcube.portlets.admin.vredeployer.shared.VREDeployerStatusType;
import org.gcube.portlets.admin.vredeployer.shared.VREDescrBean;
import org.gcube.portlets.admin.vredeployer.shared.deployreport.ClientDeployReport;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * 
 * @author massi
 *
 */
@RemoteServiceRelativePath("VREDeployerServiceImpl")
public interface VredeployerService extends RemoteService {

	VREDescrBean getVRE() throws NullPointerException;
	
	VREDeployerStatusType isApprovingModeEnabled();
	
	int isCloudSelected();
	
	VREFunctionalityModel getFunctionality();
	
	List<GHNProfile> getAvailableGHNs();
	
	boolean setGHNsSelected(String[] selectedGHNIds);
	
	boolean setCloudDeploy(int virtualMachines);
	
	int getCloudVMSelected();
	
	boolean deployVRE();
	
	ClientDeployReport checkCreateVRE();
	
	String getHTMLReport();
	
	void getGHNPerFunctionality(String funcId);
}
