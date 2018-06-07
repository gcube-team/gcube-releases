package org.gcube.portlets.admin.vredeployer.client;

import org.gcube.portlets.admin.vredeployer.client.model.VREFunctionalityModel;
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
	
	VREFunctionalityModel getFunctionality();
		
	boolean deployVRE();
	
	ClientDeployReport checkCreateVRE();
	
	String getHTMLReport();
	
	void getGHNPerFunctionality(String funcId);
}
