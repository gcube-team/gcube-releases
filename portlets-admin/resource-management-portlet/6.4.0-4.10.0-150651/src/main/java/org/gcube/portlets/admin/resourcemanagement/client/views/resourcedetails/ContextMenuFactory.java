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
 * Filename: ContextMenuFactory.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.client.views.resourcedetails;

import java.util.List;
import java.util.Vector;

import org.gcube.portlets.admin.resourcemanagement.client.forms.genericresources.GenericResourceForm;
import org.gcube.portlets.admin.resourcemanagement.client.remote.ProxyRegistry;
import org.gcube.portlets.admin.resourcemanagement.client.utils.Commands;
import org.gcube.portlets.admin.resourcemanagement.client.utils.OpCommands;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.console.ConsoleMessageBroker;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.registry.UIIdentifiers;
import org.gcube.resourcemanagement.support.client.utils.StatusHandler;
import org.gcube.resourcemanagement.support.client.views.ResourceTypeDecorator;
import org.gcube.resourcemanagement.support.shared.operations.SupportedOperations;
import org.gcube.resourcemanagement.support.shared.types.Tuple;
import org.gcube.resourcemanagement.support.shared.types.datamodel.ResourceDescriptor;
import org.gcube.resourcemanagement.support.shared.types.datamodel.ResourceDetailModel;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Daniele Strollo 
 * @author Massimiliano Assante (ISTI-CNR)
 * @version 2.0 Feb 2012
 */
public class ContextMenuFactory {
	private static final ContextMenuFactory INSTANCE = new ContextMenuFactory();

	public static ContextMenuFactory getInstance() {
		return INSTANCE;
	}

	public final synchronized Menu buildContextMenu(final String resType, final ResourceDetailsPanel container) {

		Menu menu = new Menu();
		/*************************************************************
		 * FOR ALL RESOURCES
		 ************************************************************/

		MenuItem getProfile = new MenuItem("Retrieve Profile") {
			@Override
			protected void onClick(final ComponentEvent be) {
				super.onClick(be);
				List<ModelData> selectedElems = container.getSelection();
				for (ModelData selectedElem : selectedElems) {
					String type = ((Object) selectedElem.get("Type")).toString();
					String resID = ((Object) selectedElem.get("ID")).toString();
					String scope = StatusHandler.getStatus().getCurrentScope();
					Commands.doGetResourceProfile(this, scope, type, resID);
				}
			}
		};
		getProfile.setIconStyle("profile-icon");
		menu.add(getProfile);

		MenuItem getErrors = new MenuItem("Validate") {
			@Override
			protected void onClick(final ComponentEvent be) {
				super.onClick(be);
				List<ModelData> selectedElems = container.getSelection();
				for (ModelData selectedElem : selectedElems) {
					boolean isValid = true;
					String[] reqs = ResourceDetailModel.getRequiredFields(((Object) selectedElem.get("Type")).toString());

					for (String req : reqs) {
						if (selectedElem.get(req) == null || ((Object) selectedElem.get(req)).toString().trim().length() == 0) {
							isValid = false;
							// Adds to the model with problems the error code
							Commands.showPopup("Validation: Failure", "The value for field <b><i>" + req + "</i></b> is invalid");
						}
					}

					if (isValid) {
						Commands.showPopup("Validation: Success", "The current element is valid", 6000);
					}
				}
			}
		};
		getErrors.setIconStyle("validate-icon");
		menu.add(getErrors);



		MenuItem addToScope = new MenuItem("Add To Scope");
		final Menu addToScopeSub = new Menu();
		final Menu removeFromScopeSub = new Menu();

		ProxyRegistry.getProxyInstance().getAvailableAddScopes(new AsyncCallback<List<String>>() {

			public void onSuccess(final List<String> result) {
				for (final String scope : result) {
					/*
					 * REMOVE FROM SCOPE RPC
					 */
					removeFromScopeSub.add(new MenuItem(scope) {
						@Override
						protected void onClick(final ComponentEvent be) {
							super.onClick(be);
							Commands.mask("Applying remove scope", UIIdentifiers.MAIN_CONTAINER_VIEWPORT_ID);
							List<ModelData> selectedElems = container.getSelection();
							String resType = null;
							List<String> resourceIDs = new Vector<String>();

							// Builds the list of IDs to add to scope
							for (ModelData selectedElem : selectedElems) {
								if (resType == null) {
									resType = ((Object) selectedElem.get("Type")).toString();
								}
								resourceIDs.add(((Object) selectedElem.get("ID")).toString());

								ConsoleMessageBroker.info(this,
										"Removing ID: " + ((Object) selectedElem.get("ID")).toString() +
										" type: " + ((Object) selectedElem.get("Type")).toString() +
										" to scope: " + scope
										);
							}

							ProxyRegistry.getProxyInstance().removeResourcesFromScope(resType, resourceIDs, scope,
									new AsyncCallback<Tuple<String>>() {
								public void onSuccess(final Tuple<String> result) {
									Commands.unmask(UIIdentifiers.MAIN_CONTAINER_VIEWPORT_ID);
									if (result != null) {
										MessageBox.info("Remove from Scope",
												"The required remove from scope operation has been applied.<br/>" +
														"The generated report ID is:<br/>" +
														"<b>" + result.get(0) + "</b>",
														null);
										Commands.buildRemoveFromScopeReport(result);
									}
									else {										
										MessageBox.alert("Remove from Scope",
												"The required remove from scope operation has NOT been applied.<br/>" +
														"",
														null);
									}
				
								}
								public void onFailure(final Throwable caught) {
									Commands.unmask(UIIdentifiers.MAIN_CONTAINER_VIEWPORT_ID);
									MessageBox.alert(
											"Remove form Scope error",
											"The required operation has not been applied due to server error: <br/>" + caught.getMessage(),
											null);
								}
							});
						}
					});
					/*
					 * ADD TO SCOPE RPC
					 */

					addToScopeSub.add(new MenuItem(scope) {
						@Override
						protected void onClick(final ComponentEvent be) {
							super.onClick(be);
							Commands.mask("Applying add scope", UIIdentifiers.MAIN_CONTAINER_VIEWPORT_ID);
							List<ModelData> selectedElems = container.getSelection();
							String resType = null;
							List<String> resourceIDs = new Vector<String>();

							// Builds the list of IDs to add to scope
							for (ModelData selectedElem : selectedElems) {
								if (resType == null) {
									resType = ((Object) selectedElem.get("Type")).toString();
								}
								resourceIDs.add(((Object) selectedElem.get("ID")).toString());

								ConsoleMessageBroker.info(this,
										"Adding ID: " + ((Object) selectedElem.get("ID")).toString() +
										" type: " + ((Object) selectedElem.get("Type")).toString() +
										" to scope: " + scope
										);
							}

							ProxyRegistry.getProxyInstance().addResourcesToScope(resType, resourceIDs, scope,
									new AsyncCallback<Tuple<String>>() {
								public void onSuccess(final Tuple<String> result) {
									Commands.unmask(UIIdentifiers.MAIN_CONTAINER_VIEWPORT_ID);
									if (result != null) {
										MessageBox.info("Add to Scope",
												"The required add to scope operation has been applied.<br/>" +
														"The generated report ID is:<br/>" +
														"<b>" + result.get(0) + "</b>",
														null);
										Commands.buildAddToScopeReport(result);
									} else {										
										MessageBox.alert("Add to Scope",
												"The required add to scope operation has been applied.<br/>" +
														"But the resulting report ID is null or invalid.",
														null);
									}
								}
								public void onFailure(final Throwable caught) {
									Commands.unmask(UIIdentifiers.MAIN_CONTAINER_VIEWPORT_ID);
									MessageBox.alert(
											"Add to Scope error",
											"The required operation has not been applied: <br/>" + caught.getMessage(),
											null);
								}
							});
						}
					});
				}
			}

			public void onFailure(final Throwable caught) {
				Commands.unmask(UIIdentifiers.MAIN_CONTAINER_VIEWPORT_ID, UIIdentifiers.RESOURCE_DETAIL_GRID_PANEL);
			}
		});
		addToScope.setSubMenu(addToScopeSub);
		Commands.evaluateCredentials(
				addToScope,
				SupportedOperations.ADD_TO_SCOPE.getPermissions());
		addToScope.setIconStyle("addtoscope-icon");
		menu.add(addToScope);

		/*************************************************************
		 * REMOVE FROM SCOPE
		 ************************************************************/
		MenuItem removeFromScope = new MenuItem("Remove From Scope");
		removeFromScope.setSubMenu(removeFromScopeSub);
		Commands.evaluateCredentials(
				removeFromScope,
				SupportedOperations.ADD_TO_SCOPE.getPermissions());
		removeFromScope.setIconStyle("removefromscope-icon");
		menu.add(removeFromScope);

		/*************************************************************
		 * GHN
		 ************************************************************/
		if (resType.equals(ResourceTypeDecorator.GHN.name())) {

			// 0 - GETRELATED button
			MenuItem getRelated = new MenuItem("Get Related") {
				@Override
				protected void onClick(final ComponentEvent be) {
					super.onClick(be);
					Commands.doGetRelatedResources(ResourceTypeDecorator.GHN.name(),
							((Object) container.getSelection().get(0).get("ID")).toString(),
							StatusHandler.getStatus().getCurrentScope());
				}
			};
			getRelated.setIconStyle("link-icon");
			menu.add(getRelated);

			// 0 - DELETE button
			menu.add(this.createSeparator(SupportedOperations.GHN_DELETE));
			menu.add(this.createDeleteButton(
					container,
					ResourceTypeDecorator.GHN,
					SupportedOperations.GHN_DELETE));

			// 1 - Force DELETE button
			menu.add(this.createForceDeleteButton(
					container,
					ResourceTypeDecorator.GHN,
					SupportedOperations.GHN_FORCE_DELETE));

			// 1 - RESTART
			menu.add(this.createSeparator(SupportedOperations.GHN_RESTART));

			MenuItem restart = new MenuItem("Restart") {
				@Override
				protected void onClick(final ComponentEvent be) {
					super.onClick(be);
					MessageBox.confirm("GHN Restart", "Are you sure you want to restart the selected GHN(s)?",
							new Listener<MessageBoxEvent>() {
						public void handleEvent(final MessageBoxEvent be) {

							if (be.getButtonClicked().getItemId().equalsIgnoreCase("yes")) {
								// - THE OPERATION IF CONFIRMED
								for (ModelData e : container.getSelection()) {
									ConsoleMessageBroker.info(this, "Required restart for: " + e.get("Name") + " " + e.get("ID"));
								}
								OpCommands.doRestartGHNs(container.getSelection(), false);
							}
						}
					});
				}
			};
			Commands.evaluateCredentials(
					restart,
					SupportedOperations.GHN_RESTART.getPermissions());
			restart.setIconStyle("restart-icon");
			menu.add(restart);

			// 2 - CLEAN RESTART
			MenuItem cleanRestart = new MenuItem("Clean & Restart") {
				@Override
				protected void onClick(final ComponentEvent be) {
					super.onClick(be);
					MessageBox.confirm("GHN Restart", "Are you sure you want to clean and restart the selected GHN(s)?",
							new Listener<MessageBoxEvent>() {
						public void handleEvent(final MessageBoxEvent be) {

							if (be.getButtonClicked().getItemId().equalsIgnoreCase("yes")) {
								// - THE OPERATION IF CONFIRMED
								for (ModelData e : container.getSelection()) {
									ConsoleMessageBroker.info(this, "Required restart for: " + e.get("Name") + " " + e.get("ID"));
								}
								OpCommands.doRestartGHNs(container.getSelection(), true);
							}
						}
					});
				}
			};
			Commands.evaluateCredentials(
					cleanRestart,
					SupportedOperations.GHN_CLEAN_RESTART.getPermissions());
			cleanRestart.setIconStyle("clean-restart-icon");
			menu.add(cleanRestart);

			// 3 - SHUTDOWN
			MenuItem shutdown = new MenuItem("Shutdown") {
				@Override
				protected void onClick(final ComponentEvent be) {
					super.onClick(be);
					MessageBox.confirm("GHN Shutdown", "Are you sure you want to shut down the selected GHN(s)?",
							new Listener<MessageBoxEvent>() {
						public void handleEvent(final MessageBoxEvent be) {

							if (be.getButtonClicked().getItemId().equalsIgnoreCase("yes")) {
								// - THE OPERATION IF CONFIRMED
								for (ModelData e : container.getSelection()) {
									ConsoleMessageBroker.info(this, "Required shutdown for: " + e.get("Name") + " " + e.get("ID"));
								}
								OpCommands.doShutdownGHNs(container.getSelection());
							}
						}
					});
				}
			};
			Commands.evaluateCredentials(
					shutdown,
					SupportedOperations.GHN_SHUTDOWN.getPermissions());
			shutdown.setIconStyle("shutdown-icon");
			menu.add(shutdown);
		} // - ENDOF GHN


		/*************************************************************
		 * RUNNING INSTANCE
		 ************************************************************/
		if (resType.equals(ResourceTypeDecorator.RunningInstance.name())) {
			// 0 - GETRELATED button
			MenuItem getRelated = new MenuItem("Get Related") {
				@Override
				protected void onClick(final ComponentEvent be) {
					super.onClick(be);
					Commands.doGetRelatedResources(ResourceTypeDecorator.RunningInstance.name(),
							((Object) container.getSelection().get(0).get("ID")).toString(),
							StatusHandler.getStatus().getCurrentScope());
				}
			};
			getRelated.setIconStyle("link-icon");
			menu.add(getRelated);

			// 1 - undeploy
			MenuItem undeploy = new MenuItem("Undeploy") {
				@Override
				protected void onClick(final ComponentEvent be) {
					super.onClick(be);
					MessageBox.confirm("Running Instance Undeploy", "Are you sure you want to undeploy the selected RI(s)?",
							new Listener<MessageBoxEvent>() {
						public void handleEvent(final MessageBoxEvent be) {

							if (be.getButtonClicked().getItemId().equalsIgnoreCase("yes")) {
								Vector<ResourceDescriptor> resources = new Vector<ResourceDescriptor>();
								// - THE OPERATION IF CONFIRMED
								for (ModelData e : container.getSelection()) {
									ConsoleMessageBroker.info(this, "Required undeploy for: " + e.get("Name") + " " + e.get("ID"));
									try {
										resources.add(new ResourceDescriptor(
												ResourceTypeDecorator.RunningInstance.name(),
												null,
												((Object) e.get("ID")).toString(),
												((Object) e.get("Name")).toString()));
									} catch (Exception ex) {
										MessageBox.alert(
												"Generic Resource Edit",
												"Failure<br/>" + ex.getMessage(),
												null);
									}
								}

								try {
									ProxyRegistry.getProxyInstance().doOperation(
											SupportedOperations.RUNNING_INSTANCE_UNDEPLOY,
											StatusHandler.getStatus().getCurrentScope(),
											resources, //lista id risorse selzionati
											new AsyncCallback<Void>() {
												@Override
												public void onSuccess(final Void result) {
													Commands.showPopup("Undeployment", "success");
												}
												@Override
												public void onFailure(
														final Throwable caught) {
													Commands.showPopup("Undeployment", "failure");
												}
											});
								} catch (Exception e) {
									MessageBox.alert(
											"Generic Resource Edit",
											"Failure<br/>" + e.getMessage(),
											null);
								}
							}
						}
					});
				}
			};
			Commands.evaluateCredentials(
					undeploy,
					SupportedOperations.RUNNING_INSTANCE_UNDEPLOY.getPermissions());
			undeploy.setIconStyle("delete-icon");
			menu.add(undeploy);
		}

		/*************************************************************
		 * SERVICE
		 ************************************************************/
		if (resType.equals(ResourceTypeDecorator.Service.name())) {
			// 0 - GETRELATED button
			MenuItem getRelated = new MenuItem("Get Related") {
				@Override
				protected void onClick(final ComponentEvent be) {
					super.onClick(be);
					Commands.doGetRelatedResources(ResourceTypeDecorator.Service.name(),
							((Object) container.getSelection().get(0).get("ID")).toString(),
							StatusHandler.getStatus().getCurrentScope());
				}
			};
			getRelated.setIconStyle("link-icon");
			menu.add(getRelated);
		}

		/*************************************************************
		 * GENERIC RESOURCE
		 ************************************************************/
		if (resType.equals(ResourceTypeDecorator.GenericResource.name())) {

			// 0 - EDIT
			MenuItem edit = new MenuItem("Edit") {
				@Override
				protected void onClick(final ComponentEvent be) {
					final List<ModelData> selection = container.getSelection();
					if (selection != null && selection.size() == 1) {
						ProxyRegistry.getProxyInstance().getGenericResourceDescriptor(
								StatusHandler.getStatus().getCurrentScope(),
								((Object) selection.get(0).get("ID")).toString(),
								new AsyncCallback<ResourceDescriptor>() {
									public void onSuccess(final ResourceDescriptor result) {
										new GenericResourceForm(result).show();
									}
									public void onFailure(final Throwable caught) {
										MessageBox.alert(
												"Generic Resource Edit",
												"Failure<br/>" + caught.getMessage(),
												null);
									}
								});
					} else {
						MessageBox.info("Editing resource", "The editing is allowed on a single selected item", null);
					}
				}
			};
			Commands.evaluateCredentials(
					edit,
					SupportedOperations.GHN_SHUTDOWN.getPermissions());
			edit.setIconStyle("edit-icon");
			menu.add(edit);


			// 1 - DELETE button
			menu.add(this.createSeparator(SupportedOperations.GENERIC_RESOURCE_DELETE));
			menu.add(this.createDeleteButton(
					container,
					ResourceTypeDecorator.GenericResource,
					SupportedOperations.GENERIC_RESOURCE_DELETE));

			// 2 - Force DELETE button
			menu.add(this.createForceDeleteButton(
					container,
					ResourceTypeDecorator.GenericResource,
					SupportedOperations.GENERIC_RESOURCE_FORCE_DELETE));
		} // - ENDOF generic resource


		/*************************************************************
		 * RUNTIME RESOURCE
		 ************************************************************/

		if (resType.equals(ResourceTypeDecorator.RuntimeResource.name())) {
			// 0 - EDIT button
			menu.add(this.createSeparator(SupportedOperations.RUNTIME_RESOURCE_DELETE));
			menu.add(this.createEditButton(
					container,
					ResourceTypeDecorator.RuntimeResource,
					SupportedOperations.RUNTIME_RESOURCE_DELETE));

			// 1 - DELETE button
			menu.add(this.createSeparator(SupportedOperations.RUNTIME_RESOURCE_DELETE));
			menu.add(this.createDeleteButton(
					container,
					ResourceTypeDecorator.RuntimeResource,
					SupportedOperations.RUNTIME_RESOURCE_DELETE));

			// 2 - Force DELETE button
			menu.add(this.createForceDeleteButton(
					container,
					ResourceTypeDecorator.RuntimeResource,
					SupportedOperations.RUNTIME_RESOURCE_FORCE_DELETE));
		}

		/*************************************************************
		 * COLLECTION
		 ************************************************************/
		if (resType.equals(ResourceTypeDecorator.Collection.name())) {
			// 0 - DELETE button
			menu.add(this.createSeparator(SupportedOperations.COLLECTION_DELETE));
			menu.add(this.createDeleteButton(
					container,
					ResourceTypeDecorator.Collection,
					SupportedOperations.COLLECTION_DELETE));

			// 1 - Force DELETE button
			menu.add(this.createForceDeleteButton(
					container,
					ResourceTypeDecorator.Collection,
					SupportedOperations.COLLECTION_FORCE_DELETE));
		} // - ENDOF Collection

		/*************************************************************
		 * METADATA COLLECTION
		 ************************************************************/
		if (resType.equals(ResourceTypeDecorator.VIEW.name())) {
			// 0 - DELETE button
			menu.add(this.createSeparator(SupportedOperations.VIEW_DELETE));
			menu.add(this.createDeleteButton(
					container,
					ResourceTypeDecorator.VIEW,
					SupportedOperations.VIEW_DELETE));

			// 1 - Force DELETE button
			menu.add(this.createForceDeleteButton(
					container,
					ResourceTypeDecorator.VIEW,
					SupportedOperations.VIEW_FORCE_DELETE));
		} // - ENDOF Metadata Collection

		return menu;
	}

	private SeparatorMenuItem createSeparator(final SupportedOperations operationID) {
		// Builds the separator
		SeparatorMenuItem separator = new SeparatorMenuItem();
		if (operationID != null) {
			Commands.evaluateCredentials(
					separator,
					operationID.getPermissions());
		}
		return separator;
	}

	private MenuItem createDeleteButton(
			final ResourceDetailsPanel container,
			final ResourceTypeDecorator resType,
			final SupportedOperations operationID) {

		// Builds the delete button
		MenuItem delete = new MenuItem("Delete") {
			@Override
			protected void onClick(final ComponentEvent be) {
				super.onClick(be);
				MessageBox.confirm(resType.getLabel() + " Delete",
						"Are you sure you want to delete the selected " + resType.getLabel() + "(s)?",
						new Listener<MessageBoxEvent>() {
					public void handleEvent(final MessageBoxEvent be) {
						if (be.getButtonClicked().getItemId().equalsIgnoreCase("yes")) {
							// - THE OPERATION IF CONFIRMED
							try {
								for (ModelData e : container.getSelection()) {
									ConsoleMessageBroker.info(this, "Required force delete for: " + e.get("Name") + " " + e.get("ID"));
								}
							} catch (Exception e) {
								ConsoleMessageBroker.error(this, e.getMessage());
							}
							OpCommands.doDeleteItems(resType, container.getSelection(), operationID);
						}
					}
				});


			}
		};
		// Checks the permissions on the operation
		Commands.evaluateCredentials(
				delete,
				operationID.getPermissions());
		delete.setIconStyle("delete-icon");
		return delete;
	}

	private MenuItem createEditButton(
			final ResourceDetailsPanel container,
			final ResourceTypeDecorator resType,
			final SupportedOperations operationID) {

		// Builds the delete button
		MenuItem edit = new MenuItem("Edit Runtime Resource") {
			@Override
			protected void onClick(final ComponentEvent be) {
				String idToEdit = "";
				for (ModelData e : container.getSelection()) {
					idToEdit = e.get("ID");					
				}
				OpCommands.loadServiceEndopointEditor(idToEdit);
			}
		};
		// Checks the permissions on the operation
		Commands.evaluateCredentials(
				edit,
				operationID.getPermissions());
		edit.setIconStyle("edit-icon");
		return edit;
	}

	private MenuItem createForceDeleteButton(
			final ResourceDetailsPanel container,
			final ResourceTypeDecorator resType,
			final SupportedOperations operationID) {

		// Builds the delete button
		MenuItem delete = new MenuItem("Force Delete") {
			@Override
			protected void onClick(final ComponentEvent be) {
				super.onClick(be);
				MessageBox.confirm(resType.getLabel() + " Force Delete",
						"Are you sure you want to delete the selected " + resType.getLabel() + "(s)?",
						new Listener<MessageBoxEvent>() {
					public void handleEvent(final MessageBoxEvent be) {
						if (be.getButtonClicked().getItemId().equalsIgnoreCase("yes")) {
							// - THE OPERATION IF CONFIRMED
							try {
								for (ModelData e : container.getSelection()) {
									ConsoleMessageBroker.info(this, "Required force delete for: " + e.get("Name") + " " + e.get("ID"));
								}
							} catch (Exception e) {
								ConsoleMessageBroker.error(this, e.getMessage());
							}
							OpCommands.doDeleteItems(resType, container.getSelection(), operationID);
						}
					}
				});


			}
		};
		// Checks the permissions on the operation
		Commands.evaluateCredentials(
				delete,
				operationID.getPermissions());
		delete.setIconStyle("force-delete-icon");
		return delete;
	}



}
