package org.gcube.portlets.admin.software_upload_wizard.client.view.widget;

import java.util.ArrayList;

import org.gcube.portlets.admin.software_upload_wizard.client.util.Resources;
import org.gcube.portlets.admin.software_upload_wizard.shared.SoftwareChange;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class ChangesFieldSet extends FieldSet {
	
	private ComponentNameField componentNameField = new ComponentNameField();
	private ChangesPanel changesPanel = new ChangesPanel();
	
	public ChangesFieldSet() {
		FormLayout softwareLayout = new FormLayout();
		softwareLayout.setLabelWidth(200);

		// Create software info fieldset
		this.setHeading("Sofware modifications");
		this.setLayout(softwareLayout);
		
//		initToolTip();
		
		this.add(componentNameField);
		this.add(changesPanel);
	}
	
	private void initToolTip(){
		ToolTipConfig fieldSetToolTip = new DefaultTooltipConfig();
	    fieldSetToolTip.setTitle("Software modifications");  
	    String toolTipText = "<p>In this section the user enters a list of software changes which applies to the uploaded version of the software.</p>" +
	    		"<ul>" +
	    		"<li><b>Component name</b>: System generated component name.</li>" +
	    		"<li><b>Changes list</b>: List of software modifications. For each software change the user must enter a description and may provide a ticket reference number.</li>" +
	    		"</ul>"+
	    		"<p>At least one software modification is required.</p>";
	    
	    fieldSetToolTip.setText(toolTipText);
	    this.setToolTip(fieldSetToolTip);
	}
	
	public boolean isValid(){
		return changesPanel.isValid();
	}

	private class ComponentNameField extends TextField<String>{
		public ComponentNameField() {
			this.setEnabled(false);
			this.setFieldLabel("Component name");
			this.setAllowBlank(false);
		}
	}
	
	public String getComponentName(){
		return componentNameField.getValue();
	}
	
	public void setComponentName(String value){
		componentNameField.setRawValue(value);
	}

	public ArrayList<SoftwareChange> getChanges(){
		return changesPanel.getChanges();
	}

	public void setChanges(ArrayList<SoftwareChange> value){
		changesPanel.setChanges(value);
	}

	private class ChangesPanel extends ContentPanel {
	
		private ListStore<SoftwareChangeModel> store = new ListStore<SoftwareChangeModel>();
		private final CheckBoxSelectionModel<SoftwareChangeModel> sm = new CheckBoxSelectionModel<SoftwareChangeModel>();
		EditorGrid<SoftwareChangeModel> grid;
	
		private Button addButton = new Button("Add",
				AbstractImagePrototype.create(Resources.INSTANCE
						.addIcon()));
		private Button removeButton = new Button("Remove",
				AbstractImagePrototype.create(Resources.INSTANCE
						.deleteIcon()));
	
		public ChangesPanel() {
			this.setHeading("Changes list*");
			this.setLayout(new FitLayout());
			this.setHeight(150);
	
			FormData formData = new FormData("100%");
	
			ColumnConfig ticketColumn = new ColumnConfig(SoftwareChangeModel.TICKET_CODE, "Ticket number", 100);
			NumberField ticketField = new NumberField();
			ticketField.setAllowBlank(true);
			ticketField.setAllowDecimals(false);
			ticketField.setPropertyEditorType(Integer.class);
			ticketField.setAllowNegative(false);
			ticketColumn.setEditor(new CellEditor(ticketField));
			
			ColumnConfig descriptionColumn = new ColumnConfig(
					SoftwareChangeModel.DESCRIPTION_CODE, "Modification description", 100);
			
			
			TextField<String> descriptionText = new TextField<String>();
			descriptionText.setAllowBlank(false);
			descriptionColumn.setEditor(new CellEditor(descriptionText));
	
			ArrayList<ColumnConfig> columns = new ArrayList<ColumnConfig>();
			columns.add(sm.getColumn());
			columns.add(ticketColumn);
			columns.add(descriptionColumn);
	
			ColumnModel columnModel = new ColumnModel(columns);
	
			grid = new EditorGrid<SoftwareChangeModel>(store, columnModel);
			grid.setSelectionModel(sm);
			grid.setAutoExpandColumn(SoftwareChangeModel.DESCRIPTION_CODE);
	
			grid.addPlugin(sm);
			grid.getView().setShowDirtyCells(false);
	
			this.add(grid, formData);
	
			ToolBar toolBar = new ToolBar();
			toolBar.setAlignment(HorizontalAlignment.RIGHT);
	
			toolBar.add(addButton);
			toolBar.add(removeButton);
	
			this.setBottomComponent(toolBar);
	
			bind();
		}
	
		private void bind() {
			addButton
					.addSelectionListener(new SelectionListener<ButtonEvent>() {
	
						@Override
						public void componentSelected(ButtonEvent ce) {
							SoftwareChangeModel entryPoint = new SoftwareChangeModel("");
							ChangesPanel.this.grid.stopEditing(true);
							store.insert(entryPoint, 0);
							ChangesPanel.this.grid.startEditing(
									store.indexOf(entryPoint), 1);
						}
					});
	
			removeButton
					.addSelectionListener(new SelectionListener<ButtonEvent>() {
	
						@Override
						public void componentSelected(ButtonEvent ce) {
							for (SoftwareChangeModel entryPoint : sm.getSelectedItems())
								store.remove(entryPoint);
						}
					});
		}
	
		public ArrayList<SoftwareChange> getChanges() {
			ArrayList<SoftwareChange> changes = new ArrayList<SoftwareChange>();
			for (SoftwareChangeModel cm : store.getModels()) {
				changes.add(new SoftwareChange(cm.getTicketNumber(), cm.getDescription()));
			}
			return changes;
		}
	
		public void setChanges(ArrayList<SoftwareChange> changes) {
			store.removeAll();
			for (SoftwareChange c : changes) {
				store.add(new SoftwareChangeModel(c.getTicketNumber(), c.getDescription()));
			}
			store.commitChanges();
		}
	
		public boolean isValid() {
			if (store.getModels().size() <= 0)
				return false;
			for (SoftwareChangeModel m : store.getModels()) {
				if (m.getDescription() == null || m.getDescription().isEmpty()) return false;
			}
			return true;
		}
	
		private class SoftwareChangeModel extends BaseModelData {
	
			public final static String DESCRIPTION_CODE = "CHANGE";
			public final static String TICKET_CODE = "TICKET";
	
			public SoftwareChangeModel(Integer ticket, String change) {
				set(TICKET_CODE,ticket);
				set(DESCRIPTION_CODE, change);
			}
			
			public SoftwareChangeModel(String change){
				set(TICKET_CODE,null);
				set(DESCRIPTION_CODE, change);
			}
	
			public String getDescription() {
				return get(DESCRIPTION_CODE);
			}
			
			public Integer getTicketNumber(){
				return get(TICKET_CODE);
			}
	
		}
	}
}
