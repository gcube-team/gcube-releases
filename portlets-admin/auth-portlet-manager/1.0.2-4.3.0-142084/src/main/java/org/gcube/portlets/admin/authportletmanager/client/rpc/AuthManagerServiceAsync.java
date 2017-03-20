/**
 * 
 */
package org.gcube.portlets.admin.authportletmanager.client.rpc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.admin.authportletmanager.shared.Caller;
import org.gcube.portlets.admin.authportletmanager.shared.PolicyAuth;
import org.gcube.portlets.admin.authportletmanager.shared.Quote;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author "Alessandro Pieve " <a
 *         href="mailto:alessandro.pieve@isti.cnr.it">alessandro.pieve@isti.cnr.it</a>
 * 
 */
public interface AuthManagerServiceAsync {

	public static AuthManagerServiceAsync INSTANCE = (AuthManagerServiceAsync) GWT
			.create(AuthManagerService.class);
	
	//load init value
	
	void loadRetrieveListContexts(AsyncCallback<ArrayList<String>> callback);
	
	void loadListPolicy(String context,AsyncCallback<ArrayList<PolicyAuth>> callback);
	
	void loadListCaller(String context,AsyncCallback<ArrayList<Caller>> callback);

	void loadListService(String context,AsyncCallback<Map<String, List<String>>> asyncCallback);

	void loadListAccess(AsyncCallback<ArrayList<String>> callback);
	

	/*
	void deletePolicy(Long identifier,
			AsyncCallback<Long> callback);
	*/
	/*
	void addPolicy(String caller, String typeCaller, String serviceClass,
			String serviceName, String serviceId, String access,
			AsyncCallback<Long> callback);
	*/
	
	void updatePolicy(String context,PolicyAuth policies, AsyncCallback<Void> callback);

	void addPolicies(String context,List<PolicyAuth> identifier,AsyncCallback<Void> asyncCallback);

	void deletePolicies(List<Long> identifier,AsyncCallback<List<Long>> callback);

	/*
	 * SECTION QUOTA
	 */
	
	void loadListQuota(AsyncCallback<ArrayList<Quote>> asyncCallback);

	void deleteQuote(List<Long> identifier, AsyncCallback<List<Long>> callback);

	void addQuote(List<Quote> quote, AsyncCallback<List<Quote>> callback);

	void updateQuote(Quote quote, AsyncCallback<Quote> asyncCallback);

	

	
	
	
	
}
