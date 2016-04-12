package org.gcube.portlets.admin.software_upload_wizard.client.view.widget;

import java.util.ArrayList;

import org.gcube.portlets.admin.software_upload_wizard.client.util.Resources;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class WebAppInfoFieldSet extends GenericComponentInfoFieldSet {

	ContainerComboBox containerComboBox = new ContainerComboBox();
	PlatformComboBox platformComboBox = new PlatformComboBox();
	EntryPointsPanel entryPointsPanel = new EntryPointsPanel();

	public WebAppInfoFieldSet() {
		super("Web Application Package");
		
		//initToolTip();

		this.add(containerComboBox);
		this.add(platformComboBox);
		this.add(entryPointsPanel, new FormData("100%"));
	}
	
	private void initToolTip(){
		ToolTipConfig containerToolTip = new DefaultTooltipConfig();
	    containerToolTip.setTitle("Web Application Package data");
	    String toolTipText = "<p>In this section the user enters data related to the Package section of the Service Profile. All fields are mandatory if not otherwise declared.</p>" +
	    		"<ul>" +
	    		"<li><b>Package name</b>: <i>Name</i> assigned to the <i>Package</i> in the Service Profile. Only alphanumeric chars and '-' symbol are allowed.</li>" +
	    		"<li><b>Package description</b> (optional): <i>Description</i> assigned to the <i>Package</i> in the Service Profile.</li>" +
	    		"<li><b>Package version</b>: <i>Version</i> assigned to the <i>Package</i> in the Service Profile.</li>" +
	    		"<li><b>Servlet container</b>: Application container required to run the application.</li>" +
	    		"<li><b>Application platform</b>: Software-compatible application platform.</li>" +
	    		"<li><b>Entrypoints</b>: List of entrypoints from which the web application will be accessed. The entrypoints must be entered as relative urls starting with a forward slash '/'. At least one entrypoint must be declared.</li>" +
	    		"</ul>";
	    containerToolTip.setText(toolTipText);  
	    this.setToolTip(containerToolTip);
	}

	public ArrayList<String> getEntryPoints() {
		return entryPointsPanel.getEntryPoints();
	}

	public void setEntryPoints(ArrayList<String> entryPoints) {
		entryPointsPanel.setEntryPoints(entryPoints);
	}

	public boolean isValid() {
		return entryPointsPanel.isValid();
	}

	private class ContainerComboBox extends SimpleComboBox<String> {
		
		public ContainerComboBox() {
			this.setFieldLabel("Servlet Container*");
			this.setAllowBlank(false);
			this.setForceSelection(true);
			this.setTypeAhead(true);
			this.setTriggerAction(TriggerAction.ALL);
			this.add("Tomcat 6.0");
			this.setValue(this.getStore().getAt(0));
		}
	}
	
private class PlatformComboBox extends SimpleComboBox<String> {
	
	
		
		public PlatformComboBox() {
			this.setFieldLabel("Platform*");
			this.setAllowBlank(false);
			this.setForceSelection(true);
			this.setTypeAhead(true);
			this.setTriggerAction(TriggerAction.ALL);
			this.add("JRE 1.6");
			this.setValue(this.getStore().getAt(0));
		}
	}

	private class EntryPointsPanel extends ContentPanel {
		
		private final static String ENTRYPOINT_REGEXP = "^/.+";
		
		private ListStore<EntryPointModel> store = new ListStore<EntryPointModel>();
		private final CheckBoxSelectionModel<EntryPointModel> sm = new CheckBoxSelectionModel<EntryPointModel>();
		EditorGrid<EntryPointModel> entryPointGrid;

		private Button addButton = new Button("Add",
				AbstractImagePrototype.create(Resources.INSTANCE.addIcon()));
		private Button removeButton = new Button("Remove",
				AbstractImagePrototype.create(Resources.INSTANCE.deleteIcon()));

		public EntryPointsPanel() {
			// Create Entry points widgets
			FormData formData = new FormData("-20");

			this.setLayout(new FitLayout());
			this.setHeading("Entry Points**");
			this.setHeight(200);

			ColumnConfig entrypointColumn = new ColumnConfig(
					EntryPointModel.ENTRYPOINT_CODE, "Entry Point", 100);
			TextField<String> text = new TextField<String>();
			text.setRegex(ENTRYPOINT_REGEXP);
			text.getMessages().setRegexText("Entrypoint URL must begin with a forward slash");
			text.setAllowBlank(false);
			text.setEmptyText("e.g. /stocks/codes");
			entrypointColumn.setEditor(new CellEditor(text));

			ArrayList<ColumnConfig> columns = new ArrayList<ColumnConfig>();
			columns.add(sm.getColumn());
			columns.add(entrypointColumn);

			ColumnModel columnModel = new ColumnModel(columns);

			entryPointGrid = new EditorGrid<EntryPointModel>(store, columnModel);
			entryPointGrid.setSelectionModel(sm);
			entryPointGrid.setAutoExpandColumn(EntryPointModel.ENTRYPOINT_CODE);
			// entryPointGrid.setHeight(130);
			entryPointGrid.addPlugin(sm);
			entryPointGrid.getView().setShowDirtyCells(false);

			// Create toolbar
			ToolBar toolBar = new ToolBar();
			toolBar.setAlignment(HorizontalAlignment.RIGHT);

			toolBar.add(addButton);
			toolBar.add(removeButton);

			// Assemble UI
			this.add(entryPointGrid, formData);
			this.setBottomComponent(toolBar);

			bind();
		}

		public boolean isValid() {
			if (store.getCount() <= 0)
				return false;
			for (EntryPointModel m : store.getModels()) {
				if (m.getEntryPoint() == null || m.getEntryPoint().isEmpty() || !m.getEntryPoint().matches(ENTRYPOINT_REGEXP) )
					return false;
			}
			return true;
		}

		private void bind() {
			addButton
					.addSelectionListener(new SelectionListener<ButtonEvent>() {

						@Override
						public void componentSelected(ButtonEvent ce) {
							EntryPointModel entryPoint = new EntryPointModel("/");
							EntryPointsPanel.this.entryPointGrid
									.stopEditing(true);
							store.insert(entryPoint, 0);
							entryPointGrid.startEditing(
									store.indexOf(entryPoint), 1);
						}
					});

			removeButton
					.addSelectionListener(new SelectionListener<ButtonEvent>() {

						@Override
						public void componentSelected(ButtonEvent ce) {
							for (EntryPointModel entryPoint : sm
									.getSelectedItems())
								store.remove(entryPoint);
						}
					});
		}

		public ArrayList<String> getEntryPoints() {
			ArrayList<String> result = new ArrayList<String>();
			for (EntryPointModel entryPoint : store.getModels()) {
				result.add(entryPoint.getEntryPoint());
			}
			return result;
		}

		public void setEntryPoints(ArrayList<String> entryPoints) {
			store.removeAll();
			for (String ep : entryPoints)
				store.add(new EntryPointModel(ep));
			store.commitChanges();
		}

		private class EntryPointModel extends BaseModelData {

			private static final long serialVersionUID = -5919213535841177519L;

			private static final String ENTRYPOINT_CODE = "ENTRYPOINT";

			public EntryPointModel(String value) {
				setEntryPoint(value);
			}

			/**
			 * Returns the value.
			 * 
			 * @return the value
			 */
			public String getEntryPoint() {
				return (String) get(ENTRYPOINT_CODE);
			}

			/**
			 * Sets the value.
			 * 
			 * @param value
			 *            the value
			 */
			public void setEntryPoint(String value) {
				set(ENTRYPOINT_CODE, value);
			}
		}

	}
}
