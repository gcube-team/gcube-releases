/**
 * 
 */
package org.gcube.portlets.user.workspace.client.util;

import org.gcube.portlets.user.workspace.client.AppControllerExplorer;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.workspace.client.view.windows.InfoDisplayMessage;
import org.gcube.portlets.user.workspace.client.view.windows.MessageBoxAlert;
import org.gcube.portlets.user.workspace.client.view.windows.NewBrowserWindow;
import org.gcube.portlets.user.workspace.shared.SessionExpiredException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 24, 2013
 * 
 */
public class RequestBuilderWorkspaceValidateItem {
	
	/**
	 * 
	 */
	protected static final int TIME_INFO_DISPLAY = 1500; //milliseconds
	private AsyncCallback<WindowOpenParameter> callback;
	private String parameters;
	
	/**
	 * 
	 * @param method
	 * @param servletName the name of the servlet that must be called
	 * @param params param=value&param1=value1&...
	 * @param targetWindow the target of the window (e.g. "_blank")
	 * @param callback
	 * @throws Exception
	 */
	public RequestBuilderWorkspaceValidateItem(RequestBuilder.Method method, String servletName, final String params, final String targetWindow, final AsyncCallback<WindowOpenParameter> callback) throws Exception{
		
		this.callback = callback;
		
		final NewBrowserWindow newBrowserWindow = NewBrowserWindow.open("", targetWindow, "");
		
		this.parameters = params;
		
		if(servletName==null)
			return;

		servletName = servletName.isEmpty()?"/":servletName;
		
		if(!servletName.contains("/"))
			servletName+="/"+servletName;
		
		if(parameters==null)
			parameters = ConstantsExplorer.VALIDATEITEM+"=true";
		else
			parameters +="&"+ConstantsExplorer.VALIDATEITEM+"=true";
		
		String urlRequest = servletName+"?"+parameters;
		
		GWT.log("request builder for: "+urlRequest);
		
		RequestBuilder requestBuilder = new RequestBuilder(method, urlRequest);
		new InfoDisplayMessage("Download", "Requesting...", TIME_INFO_DISPLAY);
		try {
			
			requestBuilder.sendRequest("", new RequestCallback() {

			    @Override
			    public void onResponseReceived(Request request,  Response response) {
			    	
			    	int status = response.getStatusCode();

//			    	System.out.println("status code is "+status);
			    	
			        if(!(status==200) && !(status==202)){ //NOT IS STATUS SC_ACCEPTED 
			        	
			        	if(status==401){ // SC_UNAUTHORIZED = 401;
			        		GWT.log("Session expired");
							AppControllerExplorer.getEventBus().fireEvent(new SessionExpiredEvent());
							return;
			        	} 
			        	newBrowserWindow.close();
			        	handleError("Sorry, an error occurred on retriving the file. "+response.getText()); //ERROR STATUS

				    }else { //OK STATUS
				    	
				    	if(callback!=null)
				    		callback.onSuccess(new WindowOpenParameter(targetWindow, "", params, true, newBrowserWindow));
				    }
			    }

			    @Override
			    public void onError(Request request, Throwable exception) {
			    	newBrowserWindow.close();
			    	
			    	if(exception instanceof SessionExpiredException){
						GWT.log("Session expired");
						AppControllerExplorer.getEventBus().fireEvent(new SessionExpiredEvent());
						return;
					}

//			    	System.out.println("exception message is "+exception.getMessage());
			    	handleError(exception.getMessage());
			    }
			});
			
		} catch (RequestException e) {
			newBrowserWindow.close();
			throw new Exception("Sorry, an error occurred while contacting server, try again");
		}
	}
	
	public void handleError(String message){

		if(callback!=null)
    		callback.onFailure(new Exception(message));
    	else
    		new MessageBoxAlert("Error", message, null);
		
	}
}
