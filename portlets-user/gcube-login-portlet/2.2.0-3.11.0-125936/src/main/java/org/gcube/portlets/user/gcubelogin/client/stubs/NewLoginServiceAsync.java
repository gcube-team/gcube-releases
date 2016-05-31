package org.gcube.portlets.user.gcubelogin.client.stubs;

import java.util.ArrayList;
import java.util.HashMap;

import org.gcube.portlets.user.gcubelogin.shared.CheckResult;
import org.gcube.portlets.user.gcubelogin.shared.ResearchEnvironment;
import org.gcube.portlets.user.gcubelogin.shared.SelectedTheme;
import org.gcube.portlets.user.gcubelogin.shared.VO;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Massimiliano Assante ISTI-CNR
 * @author Rena Tsantouli, NKUA
 * 
 * @version 2.0 Jan 10th 2012
 */
public interface NewLoginServiceAsync {
	
	void getSelectedRE(AsyncCallback<ResearchEnvironment> callback); 
	
	void isLayoutLoaded(AsyncCallback<Boolean> callback);
	
	void getInfrastructureVOs(AsyncCallback<ArrayList<VO>> callback);
	
	void addMembershipRequest(String scope, String optionalMessage, AsyncCallback<Void> callback);
	
	void loadLayout(String scope, String URL, AsyncCallback<Void> callback);
	
	void getRootVO(AsyncCallback<VO> callback);

	void installPortalEnv(String infrastructure, String startScopes,
			SelectedTheme theme, boolean automaticRedirect,
			AsyncCallback<Boolean> callback);

	void getConfigFromGCore(AsyncCallback<String[]> callback);

	void checkInfrastructure(String infrastructure, String startScopes,
			AsyncCallback<HashMap<String, ArrayList<CheckResult>>> callback);

	void checkVresPresence(String infrastructure, String startScopes,
			AsyncCallback<Boolean> callback);

	void getVresFromInfrastructure(String infrastructure, String startScopes,
			AsyncCallback<ArrayList<VO>> callback);

	void installVREs(ArrayList<VO> parents, AsyncCallback<Boolean> callback);

	void createAdministratorAccount(String email, String password,
			String firstname, String lastname, AsyncCallback<Boolean> callback);

}
