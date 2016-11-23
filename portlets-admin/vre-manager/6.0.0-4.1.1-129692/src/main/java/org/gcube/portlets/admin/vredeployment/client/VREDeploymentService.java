package org.gcube.portlets.admin.vredeployment.client;

import java.util.ArrayList;

import org.gcube.portlets.admin.vredeployment.shared.VREDefinitionBean;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("vreDeploymentServlet")
public interface VREDeploymentService extends RemoteService {
	ArrayList<VREDefinitionBean> getVREDefinitions();
	
	boolean doApprove(String vreId);
	
	boolean doRemove(String vreId);
	
	boolean doUndeploy(String vreId);
	
	boolean doEdit(String vreId);
	
	String doViewDetails(String vreId);
	
	boolean doViewReport(String vreId);
	
	String getHTMLReport(String vreId);
	
	boolean postPone(String vreId);
}
