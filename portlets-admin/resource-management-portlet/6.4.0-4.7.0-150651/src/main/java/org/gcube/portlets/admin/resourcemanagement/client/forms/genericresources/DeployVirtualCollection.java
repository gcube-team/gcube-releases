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
 * Filename: DeployVirtualCollection.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.client.forms.genericresources;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.gcube.portlets.admin.resourcemanagement.client.remote.ProxyRegistry;
import org.gcube.portlets.admin.resourcemanagement.client.utils.Commands;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.console.ConsoleMessageBroker;

import org.gcube.resourcemanagement.support.client.utils.StatusHandler;
import org.gcube.resourcemanagement.support.client.views.ResourceTypeDecorator;
import org.gcube.resourcemanagement.support.shared.exceptions.InvalidParameterException;
import org.gcube.resourcemanagement.support.shared.types.Tuple;
import org.gcube.resourcemanagement.support.shared.types.datamodel.AtomicTreeNode;
import org.gcube.resourcemanagement.support.shared.types.datamodel.ResourceDescriptor;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class DeployVirtualCollection extends Dialog {
	private Grid<ResourceDescriptor> grid = null;

	public DeployVirtualCollection() {
		this.setLayout(new FitLayout());
		this.setHeading("Deploy Virtual Collections");
		this.setModal(true);
		this.setWidth(700);
		this.setHeight(500);
		this.setResizable(false);
		//this.getButtonBar().removeAll();
		this.setHideOnButtonClick(true);
	}

	protected final void onRender(final Element parent, final int index) {
		super.onRender(parent, index);
		initForm();
	}

	private void closeDialog() {
		this.hide();
	}

	private void initForm() {
		String scope = StatusHandler.getStatus().getCurrentScope();
		FormPanel form = new FormPanel();
		form.setFrame(true);
		form.setAutoWidth(true);

		initModel(scope);
		this.add(grid);

		this.getButtonBar().removeAll();
		this.getButtonBar().add(new FillToolItem());
		this.getButtonBar().add(new Button("Cancel") {
			@Override
			protected void onClick(final ComponentEvent ce) {
				super.onClick(ce);
				closeDialog();
			}
		});
		this.getButtonBar().add(new Button("Submit") {
			@Override
			protected void onClick(final ComponentEvent ce) {
				super.onClick(ce);
				for (ResourceDescriptor elem : grid.getSelectionModel().getSelectedItems()) {
					ConsoleMessageBroker.info(this, "Selected: " + elem.getID());

					String body = "";
					if (elem.getProperty("body") != null) {
						body = ((Object) elem.getProperty("body")).toString().trim();
						if (body.startsWith("<Body>")) {
							body = body.replace("<Body>", "").trim();
							body = body.substring(0, body.lastIndexOf("</Body>")).trim();
						}
					}

					ProxyRegistry.getProxyInstance().createGenericResource(
							null, // id auto-provided
							"CMSRecord", // name fixed
							"Activation Record for collection " + elem.getName(), // description
							body, // the body
							"ActivationRecord",    // the subtype
							new AsyncCallback<String>() {
								public void onSuccess(final String result) {
									Commands.showPopup("Deploy Virtual Collection", "Deployed virtual collection with id " + result);
								}
								public void onFailure(final Throwable caught) {
									Commands.showPopup("Deploy Virtual Collection", "Creation failed " + caught.getMessage());
								}
							});
				}
				closeDialog();
			}
		});

		updateGrid(scope);
	}

	private void initModel(final String scope) {
		// loads the model of generic resources
		final List<ColumnConfig> modelColumns = new ArrayList<ColumnConfig>();
		modelColumns.add(new ColumnConfig("name", "Name", 300));
		//modelColumns.add(new ColumnConfig("subtype", "Secondary Type", 170));
		modelColumns.add(new ColumnConfig("ID", "ID", 300));
		modelColumns.add(new ColumnConfig("description", "Description", 300));
		final ColumnModel cm = new ColumnModel(modelColumns);

		final CheckBoxSelectionModel<ModelData> sm = new CheckBoxSelectionModel<ModelData>();
		// adds the checkbox at the beginning
		modelColumns.add(0, sm.getColumn());
		ListStore<ResourceDescriptor> store = new ListStore<ResourceDescriptor>();
		this.grid = new Grid<ResourceDescriptor>(store, cm);
	}

	private void updateGrid(final String scope) {
		final List<Tuple<String>> additionalFields = new Vector<Tuple<String>>();
		additionalFields.add(new Tuple<String>("description", "//Profile/Description/text()"));
		additionalFields.add(new Tuple<String>("body", "//Profile/Body"));

		ProxyRegistry.getProxyInstance().getResourcesModel(scope,
				ResourceTypeDecorator.GenericResource.name(),
				"VirtualCollection",
				additionalFields,
				new AsyncCallback<List<ResourceDescriptor>>() {
			public void onSuccess(final List<ResourceDescriptor> result) {
				if (result == null || result.size() == 0) {
					MessageBox.alert("Deploy Virtual Collections", "No deployable resources found in the current scope",
							null);
					closeDialog();
				} else {
					grid.getStore().removeAll();
					grid.getStore().add(result);
				}
			}
			public void onFailure(final Throwable caught) {
				ConsoleMessageBroker.error(this, caught.getMessage());
			}
		});
	}
}
