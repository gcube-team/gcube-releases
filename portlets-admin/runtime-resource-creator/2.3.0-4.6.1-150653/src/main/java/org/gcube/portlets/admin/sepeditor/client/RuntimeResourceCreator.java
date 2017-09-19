package org.gcube.portlets.admin.sepeditor.client;

import java.util.ArrayList;

import org.gcube.portlets.admin.sepeditor.client.forms.RuntimeResourceForm;
import org.gcube.portlets.admin.sepeditor.shared.ClientScope;
import org.gcube.portlets.admin.sepeditor.shared.FilledRuntimeResource;
import org.gcube.portlets.admin.sepeditor.shared.InitInfo;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class RuntimeResourceCreator implements EntryPoint {
	public static final String CONTAINER_DIV = "RuntimeResourcePortletDIV";
	public static RuntimeResourceCreatorServiceAsync runtimeService = 
			(RuntimeResourceCreatorServiceAsync)GWT.create(RuntimeResourceCreatorService.class);

	final ContentPanel mainPanel = new ContentPanel();
	ArrayList<String> scopesCache;
	/**
	 * 
	 */
	public void onModuleLoad() {

		mainPanel.setSize("100%","600");
		mainPanel.mask("Fetching Scopes, please wait","loading-indicator");
		final String resourceToEdit = com.google.gwt.user.client.Window.Location.getParameter("rid");
		final String curscope = com.google.gwt.user.client.Window.Location.getParameter("curscope");

		runtimeService.getInitialInfo((resourceToEdit != null), resourceToEdit, curscope, new AsyncCallback<InitInfo>() {
			@Override
			public void onSuccess(InitInfo initialInfo) {	
				mainPanel.unmask();
				ArrayList<String> scopes = initialInfo.getScopes();
				if (scopes == null || scopes.isEmpty()) {
					MessageBox.alert("Service EndPoint Editor", "We're sorry, it seems you are not authorized to create this resource", null);	
				} 
				else {
					if (initialInfo.getRr2edit() != null) {	 //check if the resource to edit exists
						if (initialInfo.getRr2edit().getResourceId() == null) {
							MessageBox.alert("Service EndPoint Editor", "There is no Resource in the scope " + curscope + " having id: " + resourceToEdit, null);	
						}
						else //resource exists
							initialize(scopes, initialInfo.getRr2edit());	
					}
					else {
						//it is a create
						initialize(scopes, null);
					}
				}
			}

			@Override
			public void onFailure(Throwable arg0) {
				GWT.log("FAILED" + arg0.getMessage());
				mainPanel.unmask();
			}
		});

		RootPanel.get(CONTAINER_DIV).add(mainPanel);

	}

	public void initialize(ArrayList<String> scopes, FilledRuntimeResource toEdit) {
		if (scopesCache == null)
			scopesCache = scopes;
		ArrayList<ClientScope> scopesToPass = new ArrayList<ClientScope>();
		for (String scope : scopesCache) {
			scopesToPass.add(new ClientScope(scope));
		}
		RuntimeResourceForm form = null;
		if (toEdit == null) {
			form = new RuntimeResourceForm(this, scopesToPass);			
		}
		else {
			form = new RuntimeResourceForm(this, scopesToPass, toEdit);
		}


		RootPanel.get(CONTAINER_DIV).remove(0);
		RootPanel.get(CONTAINER_DIV).add(form);
	}
}