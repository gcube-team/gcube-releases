package org.gcube.portlets.user.templates.client;

import org.gcube.portlets.d4sreporting.common.shared.Model;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TemplateServiceAsync {
	/**
	 * 
	 * @param model ,
	 * @param callback ,
	 */
	void saveTemplate(String basketidToSaveIn, Model model, AsyncCallback<Void> callback);
	
	/**
	 * 
	 * @param callback .
	 */
	void getUserTemplateNames( AsyncCallback<String[]> callback);
	
	/**
	 * 
	 * @param templateName .
	 * @param callback .
	 */
	void readModel(String templateName, String TemplateObjectID, boolean isImporting, AsyncCallback<Model> callback);
	
	
	/**
	 * each portlet instance runs in a scope
	 * each portlet instance is used by a unique username from within the portal
	 * @param callback .
	 */
	void getUserAndScope(AsyncCallback<String[]> callback);
	
	/**
	 * 
	 * @param model .
	 * @param callback .
	 */
	@SuppressWarnings("unchecked")
	void storeTemplateInSession(Model model, AsyncCallback callback);
	/**
	 * 
	 * call for the model previously stored in the session 
	 * @param callback .
	 */
	void readTemplateFromSession( AsyncCallback<Model> callback);

	void renewHTTPSession(AsyncCallback<Void> callback);

}
