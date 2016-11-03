package org.gcube.portlets.admin.authportletmanager.client.rpc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.admin.authportletmanager.shared.Caller;
import org.gcube.portlets.admin.authportletmanager.shared.PolicyAuth;
import org.gcube.portlets.admin.authportletmanager.shared.Quote;
import org.gcube.portlets.admin.authportletmanager.shared.exceptions.ServiceException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;



/**
 * 
 * @author "Alessandro Pieve " <a
 *         href="mailto:alessandro.pieve@isti.cnr.it">alessandro
 *         .pieve@isti.cnr.it</a>
 * 
 */
@RemoteServiceRelativePath("authm")
public interface AuthManagerService extends RemoteService {
	/**
	 * Get informations on the current user
	 * 
	 * @return
	 * @throws AccountingManagerServiceException
	 */
	
	// Service for load Policy
	public ArrayList<PolicyAuth> loadListPolicy() throws ServiceException;

	// Service for load Caller
	public ArrayList<Caller> loadListCaller() throws ServiceException;

	//Service for load Service
	public Map<String, List<String>> loadListService()throws ServiceException;

	//Service for load Access
	public ArrayList<String> loadListAccess() throws ServiceException;

	
	// Service for delete multiple policy
	List<Long> deletePolicies(List<Long>identifier) throws ServiceException;


	public void addPolicies(List<PolicyAuth>identifier) throws ServiceException;

	//Service for update  policy
	public void updatePolicy(PolicyAuth policies) throws ServiceException;

	
	
	/*
	* SECTION QUOTA
	*/
	//Service for load list quota
	public ArrayList<Quote> loadListQuota() throws ServiceException;
	// Service for delete multiple policy
	public List<Long> deleteQuote(List<Long>identifier) throws ServiceException;

	//Service for add multiple quote
	public List<Quote> addQuote(List<Quote> quote) throws ServiceException;
	//Service for update  quote
	public Quote updateQuote(Quote quote) throws ServiceException;

	

	
	
}
