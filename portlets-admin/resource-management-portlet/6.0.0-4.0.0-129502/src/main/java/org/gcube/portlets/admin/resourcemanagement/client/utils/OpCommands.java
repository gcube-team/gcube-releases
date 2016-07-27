/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: CommonOperations.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.client.utils;

import java.util.List;
import java.util.Vector;

import org.gcube.portlets.admin.resourcemanagement.client.forms.genericresources.GenericResourceForm;
import org.gcube.portlets.admin.resourcemanagement.client.remote.ProxyRegistry;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.console.ConsoleMessageBroker;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.registry.UIIdentifiers;
import org.gcube.resourcemanagement.support.client.utils.StatusHandler;
import org.gcube.resourcemanagement.support.client.views.ResourceTypeDecorator;
import org.gcube.resourcemanagement.support.shared.exceptions.InvalidParameterException;
import org.gcube.resourcemanagement.support.shared.operations.SupportedOperations;
import org.gcube.resourcemanagement.support.shared.types.datamodel.ResourceDescriptor;
import org.gcube.resourcemanagement.support.shared.util.DelayedOperation;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;


class OpCallBacks {

	public static final AsyncCallback<Void> handleGenericOperation(final SupportedOperations op) {
		return new AsyncCallback<Void>() {
			public void onSuccess(final Void result) {
				MessageBox.info("Managing resources", "The operation " + op.name() + " has been <b>successfully</b> executed.", null);
				if (op == SupportedOperations.GENERIC_RESOURCE_DELETE ||
						op == SupportedOperations.COLLECTION_DELETE) {
					Commands.refreshResourceGrid();
				}
			}

			public void onFailure(final Throwable caught) {
				MessageBox.info("Managing resources", "The operation " + op.name() + " has <b>failed</b>." +
						((caught != null) ? "<br/>" + caught.getMessage() : "") +
						"<br/>See server log for further details.", null);
			}
		};
	}
}

/**
 * Here the commands for operations on resources.
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class OpCommands {

	/*************************************************************
	 * GHN
	 ************************************************************/

	public static final void doShutdownGHNs(final List<ModelData> ghns) {
		List<ResourceDescriptor> toSend = new Vector<ResourceDescriptor>();
		for (ModelData rawData : ghns) {
			try {
				toSend.add(new ResourceDescriptor(
						((Object) rawData.get("Type")).toString(),
						null, // The subType is not required here
						((Object) rawData.get("ID")).toString(),
						((Object) rawData.get("Name")).toString()));
			} catch (InvalidParameterException e) {
				Commands.showPopup("Cannot shutdown GHN", e.getMessage());
			}
		}
		try {
			ProxyRegistry.getProxyInstance().doOperation(
					SupportedOperations.GHN_SHUTDOWN,
					StatusHandler.getStatus().getCurrentScope(),
					toSend,
					OpCallBacks.handleGenericOperation(SupportedOperations.GHN_SHUTDOWN)
					);
		} catch (Exception e) {
			ConsoleMessageBroker.error(OpCommands.class, e.getMessage());
			MessageBox.info(
					"Failure",
					"the GHNs cannot be shut down",
					null);
		}
	}

	public static final void doRestartGHNs(final List<ModelData> ghns, final boolean clean) {
		List<ResourceDescriptor> toSend = new Vector<ResourceDescriptor>();
		for (ModelData rawData : ghns) {
			try {
				toSend.add(new ResourceDescriptor(
						((Object) rawData.get("Type")).toString(),
						null, // The subType is not required here
						((Object) rawData.get("ID")).toString(),
						((Object) rawData.get("Name")).toString()));
			} catch (InvalidParameterException e) {
				Commands.showPopup("Cannot restart GHN", e.getMessage());
			}
		}
		try {
			ProxyRegistry.getProxyInstance().doOperation(
					((clean) ? SupportedOperations.GHN_CLEAN_RESTART : SupportedOperations.GHN_RESTART),
					StatusHandler.getStatus().getCurrentScope(),
					toSend,
					OpCallBacks.handleGenericOperation(SupportedOperations.GHN_RESTART)
					);
		} catch (Exception e) {
			ConsoleMessageBroker.error(OpCommands.class, e.getMessage());
			MessageBox.info(
					"Failure",
					"the GHNs cannot be restarted",
					null);
		}
	}


	/*************************************************************
	 * DELETE RESOURCE
	 ************************************************************/
	public static final void doDeleteItems(
			final ResourceTypeDecorator type,
			final List<ModelData> items,
			final SupportedOperations requiredPermissions) {
		List<ResourceDescriptor> toSend = new Vector<ResourceDescriptor>();
		for (ModelData rawData : items) {
			try {
				toSend.add(new ResourceDescriptor(
						((Object) rawData.get("Type")).toString(),
						null, // The subType is not required here
						((Object) rawData.get("ID")).toString(),
						((Object) rawData.get("Name")).toString()));
			} catch (InvalidParameterException e) {
				Commands.showPopup("Cannot delete " + type.name(), e.getMessage());
			}
		}
		try {
			ProxyRegistry.getProxyInstance().doOperation(
					requiredPermissions,
					StatusHandler.getStatus().getCurrentScope(),
					toSend,
					OpCallBacks.handleGenericOperation(requiredPermissions)
					);
		} catch (Exception e) {
			ConsoleMessageBroker.error(OpCommands.class, e.getMessage());
			MessageBox.info(
					"Failure",
					"the selected " + type.name() + "(s) cannot be deleted",
					null);
		}
	}

	/*************************************************************
	 * SERVICE ENDPOINT
	 ************************************************************/
	public static final void doOpenServiceEndpointForm() {
		if (SupportedOperations.GENERIC_RESOURCE_CREATE.isAllowed(StatusHandler.getStatus().getCredentials())) {
			loadServiceEndopointEditor(null);
		} else {
			MessageBox.alert("Service Endpoint Creation", "You are not allowed to execute this operation", null);
		}
	}


	/*************************************************************
	 * GENERIC RESOURCE
	 ************************************************************/
	public static final void doOpenGenericResourceForm() {
		if (SupportedOperations.GENERIC_RESOURCE_CREATE.isAllowed(StatusHandler.getStatus().getCredentials())) {
			new GenericResourceForm().show();
		} else {
			MessageBox.alert("Generic Resource Creation", "You are not allowed to execute this operation", null);
		}
	}



	public static final void doCreateGenericResource(
			final String id,
			final String name,
			final String description,
			final String body,
			final String subType) {
		if (SupportedOperations.GENERIC_RESOURCE_CREATE.isAllowed(StatusHandler.getStatus().getCredentials())) {
			ProxyRegistry.getProxyInstance().createGenericResource(id, name, description, body, subType,
					new AsyncCallback<String>() {
				public void onSuccess(final String result) {
					MessageBox.info("Generic Resouce Creation",
							"The generic resource " + name + " has been created with ID: <br/>" + result,
							null);
					new DelayedOperation() {
						@Override
						public void doJob() {
							Commands.refreshResourceGrid();
						}
					}
					.start(3000);

				}
				public void onFailure(final Throwable caught) {
					MessageBox.alert("Generic Resouce Creation",
							"Generic Resource Creation failure: <br/>" +
									((caught != null && caught.getMessage() != null) ?
											caught.getMessage() :
											"See server log for further details."),
											null);
				}
			});
		} else {
			MessageBox.alert("Generic Resource Creation", "You are not allowed to execute this operation", null);
		}
	}

	public static final void doEditGenericResource(
			final String id,
			final String name,
			final String description,
			final String body,
			final String subType) {
		if (SupportedOperations.GENERIC_RESOURCE_EDIT.isAllowed(StatusHandler.getStatus().getCredentials())) {
			ProxyRegistry.getProxyInstance().updateGenericResource(id, name, description, body, subType,
					new AsyncCallback<Void>() {
				public void onSuccess(final Void result) {
					MessageBox.info("Generic Resouce Creation",
							"The generic resource " + name + " has been updated",
							null);
					Commands.refreshResourceGrid();
				}
				public void onFailure(final Throwable caught) {
					MessageBox.alert("Generic Resouce Creation",
							"Generic Resource Creation failure: <br/>" +
									((caught != null && caught.getMessage() != null) ?
											caught.getMessage() :
											"See server log for further details."),
											null);
				}
			});
		} else {
			MessageBox.alert("Generic Resource Creation", "You are not allowed to execute this operation", null);
		}
	}


	/*************************************************************
	 * SERVICE
	 ************************************************************/
	public static final void doOpenServiceForm() {
		if (SupportedOperations.SERVICE_CREATE.isAllowed(StatusHandler.getStatus().getCredentials())) {
			MessageBox.alert("Service Creation", "To implement", null);
		} else {
			MessageBox.alert("Service Creation", "You are not allowed to execute this operation", null);
		}
	}

	public static final void doGetDeployReport() {
		if (SupportedOperations.SERVICE_GET_REPORT.isAllowed(StatusHandler.getStatus().getCredentials())) {
			Commands.mask("Requesting deployment report", UIIdentifiers.MAIN_CONTAINER_VIEWPORT_ID);
			MessageBox.prompt("Deployment Report Request", "Insert Report ID", false, new Listener<MessageBoxEvent>() {
				public void handleEvent(final MessageBoxEvent be) {
					String reportID = be.getValue();
					if (reportID != null && reportID.trim().length() > 0) {
						ProxyRegistry.getProxyInstance().checkDeployStatus(
								reportID.trim(),
								StatusHandler.getStatus().getCurrentScope(),
								Callbacks.handleGetDeploymentReport);
					} else {
						Commands.unmask(UIIdentifiers.MAIN_CONTAINER_VIEWPORT_ID);
						Commands.showPopup("Wrong input", "Invalid input.", 4000);
					}
				}
			});
		}
	}

	public static final void doGetResourceByID() {
		if (SupportedOperations.SERVICE_GET_REPORT.isAllowed(StatusHandler.getStatus().getCredentials())) {
			MessageBox.prompt("Lookup Resource", "Insert Resource ID", false, new Listener<MessageBoxEvent>() {
				public void handleEvent(final MessageBoxEvent be) {
					String reportID = be.getValue();
					if (reportID != null && reportID.trim().length() > 0) {
						Commands.doGetResourceProfileByID(this, StatusHandler.getStatus().getCurrentScope(), reportID.trim());
					} else {
						Commands.unmask(UIIdentifiers.MAIN_CONTAINER_VIEWPORT_ID);
						Commands.showPopup("Wrong input", "Invalid input.", 4000);
					}
				}
			});
		}
	}


	/**
	 * Redirect to Service Endpoint Editor
	 * @param idToEdit 
	 */
	public static void loadServiceEndopointEditor(String idToEdit) {

		String location = Window.Location.getHref();
		String res2EditParam = "";
		if (idToEdit != null) {
			res2EditParam = "&rid=" + idToEdit;
		}

		if (location.endsWith("/") || location.endsWith("#")) { //standalone
			location = location.substring(0, location.length()-2);
		}
	
		location += "/../runtime-resource?pid=126&lifecycle=0&state=maximized&modes=view&doAsId=10136&refererPlid=10139&authzToken=101334249"
					+ "&curscope="+StatusHandler.getStatus().getCurrentScope()+res2EditParam;		
	
		//Window.open(location, "_blank", "");	
		openNewWindow("Service Endpoint Editor", location);
	}

	/**
	* Opens a new windows with a specified URL..
	* 
	* @param name String with the name of the window.
	* @param url String with your URL.
	*/
	public static void openNewWindow(String name, String url) {
	    com.google.gwt.user.client.Window.open(url, name.replace(" ", "_"),
	           "menubar=no," + 
	           "location=false," + 
	           "resizable=yes," + 
	           "scrollbars=yes," + 
	           "status=no," + 
	           "dependent=true");
	}


}
