/**
 * 
 */
package org.gcube.portlets.user.workspace.client.util;

import org.gcube.portlets.user.workspace.client.view.windows.NewBrowserWindow;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 25, 2013
 *
 */
public class WindowOpenParameter {
	
	private String option;
	private String parameters;
	private String itemName;
	private boolean redirectOnError;
	private NewBrowserWindow browserWindow;
	
	/**
	 * 
	 */
	public WindowOpenParameter() {
	}
	

	/**
	 * 
	 * @param target the target of the window (e.g. "_blank")
	 * @param itemName 
	 * @param parameters param=value&param1=value1&...
	 * @param redirectOnError if true execute a redirect on fake URL
	 * @param browserWindow an instance of NewBrowserWindow
	 */
	public WindowOpenParameter(String target, String itemName, String parameters, boolean redirectOnError, NewBrowserWindow browserWindow) {
		super();
		this.option = target;
		this.itemName = itemName;
		this.parameters = parameters;
		this.redirectOnError = redirectOnError;
		this.browserWindow = browserWindow;
	}


	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}


	public String getItemName() {
		return itemName;
	}


	public void setItemName(String itemName) {
		this.itemName = itemName;
	}


	public boolean isRedirectOnError() {
		return redirectOnError;
	}


	public void setRedirectOnError(boolean redirectOnError) {
		this.redirectOnError = redirectOnError;
	}


	public NewBrowserWindow getBrowserWindow() {
		return browserWindow;
	}


	public void setBrowserWindow(NewBrowserWindow browserWindow) {
		this.browserWindow = browserWindow;
	}
	
	
	
}
