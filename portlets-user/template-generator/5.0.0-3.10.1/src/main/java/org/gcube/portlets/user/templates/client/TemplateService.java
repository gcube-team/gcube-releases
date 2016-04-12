package org.gcube.portlets.user.templates.client;

import org.gcube.portlets.d4sreporting.common.shared.Model;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("TemplateGenServlet")
public interface TemplateService extends RemoteService {

	/**
	 * 
	 * @param model .
	 */
	void saveTemplate(String basketidToSaveIn, Model model);
	/**
	 * 
	 * @return .
	 */
	String[] getUserTemplateNames();
	/**
	 * @param templateName .
	 * @return .
	 */
	Model readModel(String templateName, String templateObjectID,boolean isImporting);
	
	/**
	 * each portlet instance runs in a scope
	 * each portlet instance is used by a unique username from within the portal
	 * @return a String[2] containing the username in [0] and the scope in [1]
	 */
	String[] getUserAndScope();
	
	/**
	 * 
	 * @param model .
	 */
	void storeTemplateInSession(Model model);
	
	
	/**
	 * 
	 * @return the model previously stored in the session 
	 */
	Model readTemplateFromSession();
	
	void renewHTTPSession();
}
