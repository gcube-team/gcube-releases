package org.gcube.portlets.user.gcubelogin.client.stubs;

import java.util.ArrayList;
import java.util.HashMap;

import org.gcube.portlets.user.gcubelogin.shared.CheckResult;
import org.gcube.portlets.user.gcubelogin.shared.ResearchEnvironment;
import org.gcube.portlets.user.gcubelogin.shared.SelectedTheme;
import org.gcube.portlets.user.gcubelogin.shared.VO;

import com.google.gwt.user.client.rpc.RemoteService;
/**
 * @author Massimiliano Assante ISTI-CNR
 */
public interface NewLoginService extends RemoteService {
	
	ResearchEnvironment getSelectedRE();

	Boolean isLayoutLoaded();
	
	ArrayList<VO> getInfrastructureVOs();
	
	VO getRootVO();
	
	void addMembershipRequest(String scope, String optionalMessage);
	
	void loadLayout(String scope, String URL);
	
	Boolean installPortalEnv(String infrastructure, String startScopes, SelectedTheme theme, boolean automaticRedirect);
	
	/**
	 * 
	 * @param infrastructure the infrastructure name
	 * @param startScopes the starting scopes
	 * @return an Hashmap containing for each scope, and array of <class>CheckResult</class>
	 */
	HashMap<String, ArrayList<CheckResult>> checkInfrastructure(String infrastructure, String startScopes);
	
	String[] getInfrastructureConfig();
	
	Boolean checkVresPresence(String infrastructure, String startScopes);
	/**
	 * 
	 * @param infrastructure the infrastructure name
	 * @param startScopes the starting scopes
	 * @return an arraylist of <class>VO</class> containing their child VREs
	 */
	ArrayList<VO> getVresFromInfrastructure(String infrastructure, String startScopes);
	
	Boolean installVREs(ArrayList<VO> parents);
	
}