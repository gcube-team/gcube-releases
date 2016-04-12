package org.gcube.portlets.admin.software_upload_wizard.client.view.widget;

import java.util.ArrayList;

import org.gcube.portlets.admin.software_upload_wizard.client.util.Resources;
import org.gcube.portlets.admin.software_upload_wizard.shared.Maintainer;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
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

public class MaintainersFieldset extends FieldSet {
	
	private MaintainersPanel maintainersPanel = new MaintainersPanel();
	
	public MaintainersFieldset() {
		this.setHeading("Mantainers data");
		
//		initToolTip();
		
		this.add(maintainersPanel,new FormData("100%"));
	}
	
	private void initToolTip(){
		ToolTipConfig fieldSetToolTip = new DefaultTooltipConfig();  
	    fieldSetToolTip.setTitle("Maintainers list");  
	    String toolTipText = "<p>In this section the user enters a list of maintainers of the software package specifyng for each maintainer the first and last name, email and organization</p>" +
	    		"<p>At least one maintainer is required.</p>";	    		
	    fieldSetToolTip.setText(toolTipText);
	    this.setToolTip(fieldSetToolTip);
	}
	
	public ArrayList<Maintainer> getMaintainers() {
		return maintainersPanel.getMaintainers();
	}

	public void setMaintainers(ArrayList<Maintainer> maintainers) {
		maintainersPanel.setMaintainers(maintainers);
	}

	public boolean isValid() {
		return maintainersPanel.isValid();
	}
	
	private class MaintainersPanel extends ContentPanel {

		private ListStore<MaintainerModel> store = new ListStore<MaintainerModel>();
		private final CheckBoxSelectionModel<MaintainerModel> sm = new CheckBoxSelectionModel<MaintainerModel>();
		EditorGrid<MaintainerModel> grid;

		private Button addButton = new Button("Add",
				AbstractImagePrototype.create(Resources.INSTANCE
						.addIcon()));
		private Button removeButton = new Button("Remove",
				AbstractImagePrototype.create(Resources.INSTANCE
						.deleteIcon()));

		public MaintainersPanel() {
			this.setHeading("Maintainers list*");
			this.setLayout(new FitLayout());
			this.setHeight(150);

			FormData formData = new FormData("100%");

			// First name column
			ColumnConfig firstNameColumn = new ColumnConfig(
					MaintainerModel.FIRSTNAME_CODE, "First Name", 100);
			TextField<String> firstNameText = new TextField<String>();
			firstNameText.setAllowBlank(false);
			firstNameColumn.setEditor(new CellEditor(firstNameText));

			// Last name column
			ColumnConfig lastNameColumn = new ColumnConfig(
					MaintainerModel.LASTNAME_CODE, "Last Name", 100);
			TextField<String> lastNameField = new TextField<String>();
			lastNameField.setAllowBlank(false);
			lastNameColumn.setEditor(new CellEditor(lastNameField));

			// Email column
			ColumnConfig emailColumn = new ColumnConfig(
					MaintainerModel.EMAIL_CODE, "E-mail", 150);
			TextField<String> emailField = new TextField<String>();
			emailField.setAllowBlank(false);
			emailField.setRegex(".+@.+\\.[a-z]+");
			emailField.getMessages().setRegexText("Bad email address!!");
			emailField.setAutoValidate(true);
			emailColumn.setEditor(new CellEditor(emailField));

			// Organization name column
			ColumnConfig organizationColumn = new ColumnConfig(
					MaintainerModel.ORGANIZATION_CODE, "Organization", 200);
			TextField<String> organizationText = new TextField<String>();
			organizationText
					.setEmptyText("CNR Pisa - Istituto di Scienza e Tecnologie dell'Informazione A. Faedo");
			organizationText.setAllowBlank(false);
			organizationColumn.setEditor(new CellEditor(organizationText));

			ArrayList<ColumnConfig> columns = new ArrayList<ColumnConfig>();
			columns.add(sm.getColumn());
			columns.add(firstNameColumn);
			columns.add(lastNameColumn);
			columns.add(emailColumn);
			columns.add(organizationColumn);

			ColumnModel columnModel = new ColumnModel(columns);

			grid = new EditorGrid<MaintainerModel>(store, columnModel);
			grid.setSelectionModel(sm);
			grid.setAutoExpandColumn(MaintainerModel.ORGANIZATION_CODE);

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
							MaintainerModel entryPoint = new MaintainerModel(
									"", "", "", "");
							MaintainersPanel.this.grid.stopEditing(true);
							store.insert(entryPoint, 0);
							MaintainersPanel.this.grid.startEditing(
									store.indexOf(entryPoint), 1);
						}
					});

			removeButton
					.addSelectionListener(new SelectionListener<ButtonEvent>() {

						@Override
						public void componentSelected(ButtonEvent ce) {
							for (MaintainerModel m : sm.getSelectedItems())
								store.remove(m);
						}
					});

		}

		public ArrayList<Maintainer> getMaintainers() {
			ArrayList<Maintainer> maintainers = new ArrayList<Maintainer>();
			for (MaintainerModel mm : store.getModels()) {
				maintainers.add(new Maintainer(mm.getFirstName(), mm
						.getLastName(), mm.getEmail(), mm.getOrganization()));
			}
			return maintainers;
		}

		public void setMaintainers(ArrayList<Maintainer> maintainers) {
			store.removeAll();
			if (maintainers==null) return;
			for (Maintainer m : maintainers) {
				store.add(new MaintainerModel(m.getFirstName(),
						m.getLastName(), m.getEmail(), m.getOrganization()));
			}
			store.commitChanges();
		}

		public boolean isValid() {
			if (store.getModels().size() <= 0)
				return false;
			boolean result = true;
			for (MaintainerModel m : store.getModels()) {
				if (m.getFirstName() == null || m.getFirstName().isEmpty())
					return false;
				if (m.getLastName() == null || m.getLastName().isEmpty())
					return false;
				if (m.getEmail() == null || m.getEmail().isEmpty())
					return false;
				if (m.getOrganization() == null
						|| m.getOrganization().isEmpty())
					return false;
			}
			return result;
		}

		private class MaintainerModel extends BaseModelData {

			public final static String FIRSTNAME_CODE = "FIRSTNAME";
			public final static String LASTNAME_CODE = "LASTNAME";
			public final static String EMAIL_CODE = "EMAIL";
			public final static String ORGANIZATION_CODE = "ORGANIZATION";

			public MaintainerModel(String firstname, String lastname,
					String email, String organization) {
				set(FIRSTNAME_CODE, firstname);
				set(LASTNAME_CODE, lastname);
				set(EMAIL_CODE, email);
				set(ORGANIZATION_CODE, organization);
			}

			public String getFirstName() {
				return get(FIRSTNAME_CODE);
			}

			public String getLastName() {
				return get(LASTNAME_CODE);
			}

			public String getEmail() {
				return get(EMAIL_CODE);
			}

			public String getOrganization() {
				return get(ORGANIZATION_CODE);
			}

		}
	}
}
