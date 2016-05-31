/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.client;

import java.util.ArrayList;

import org.gcube.portlets.user.csvimportwizard.client.general.WizardCard;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.MultiField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Timer;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class CSVInformationCard extends WizardCard {

	protected CSVImportSession session;

	protected String applicationId;
	protected TextField<String> csvFileName;
	protected TextField<String> categoryName;
	protected TextArea categoryDescription;
	protected TextField<String> applicationName;
	protected TextArea applicationDescription;
	protected NumberField majorVersion;
	protected NumberField minorVersion;
	protected NumberField ageVersion;
	protected ListStore<EntryPointModel> store;

	protected FormPanel configPanel;
	protected Timer timer;


	public CSVInformationCard(CSVImportSession session)
	{
		//FIXME step message calculated
		super("CSV Configuration", "Step 3 of 4");

		this.session = session;

		setContent(getPanel());

		timer = new Timer() {
			@Override
			public void run() {
				checkConfiguration();
			}
		};
	}

	public FormPanel getPanel()
	{
		FormData formData = new FormData("-20");
		configPanel = new FormPanel();
		configPanel.setHeaderVisible(false);

		csvFileName = new TextField<String>();
		csvFileName.setFieldLabel("CSV file");
		csvFileName.setReadOnly(true);
		configPanel.add(csvFileName, formData);

		FieldSet applicationCategory = new FieldSet();
		applicationCategory.setHeading("Application Category");

		FormLayout categoryLayout = new FormLayout();  
		categoryLayout.setLabelWidth(75);  
		applicationCategory.setLayout(categoryLayout);

		categoryName = new TextField<String>();
		categoryName.setFieldLabel("Name");
		categoryName.setEmptyText("e.g. ACME");
		categoryName.setRegex("[a-zA-Z0-9]+");
		categoryName.getMessages().setRegexText("Only alphanumeric chars allowed (no space or special chars)");
		categoryName.setAllowBlank(false);
		applicationCategory.add(categoryName, formData);

		categoryDescription = new TextArea();
		categoryDescription.setFieldLabel("Description");
		categoryDescription.setEmptyText("e.g. WebApp");
		categoryDescription.setAllowBlank(false);
		applicationCategory.add(categoryDescription, formData);

		configPanel.add(applicationCategory, formData);

		FieldSet applicationInformation = new FieldSet();
		applicationInformation.setHeading("Application Information");

		FormLayout applicationLayout = new FormLayout();  
		applicationLayout.setLabelWidth(75);  
		applicationInformation.setLayout(applicationLayout);

		applicationName = new TextField<String>();
		applicationName.setFieldLabel("Name");
		applicationName.setEmptyText("e.g. ACME-web");
		applicationName.setRegex("[a-zA-Z0-9]+");
		applicationName.getMessages().setRegexText("Only alphanumeric chars allowed (no space or special chars)");
		applicationName.setAllowBlank(false);
		applicationInformation.add(applicationName, formData);

		applicationDescription = new TextArea();
		applicationDescription.setFieldLabel("Description");
		applicationDescription.setEmptyText("e.g. ACME data center web access");
		applicationInformation.add(applicationDescription, formData);

		majorVersion = new NumberField();
		majorVersion.setMessageTarget("tooltip");
		majorVersion.setAllowBlank(false);
		majorVersion.setAllowDecimals(false);
		majorVersion.setAllowNegative(false);
		majorVersion.setWidth(30);

		minorVersion = new NumberField();
		minorVersion.setMessageTarget("tooltip");
		minorVersion.setAllowBlank(false);
		minorVersion.setAllowDecimals(false);
		minorVersion.setAllowNegative(false);
		minorVersion.setWidth(30);

		ageVersion = new NumberField();
		ageVersion.setMessageTarget("tooltip");
		ageVersion.setAllowBlank(false);
		ageVersion.setAllowDecimals(false);
		ageVersion.setAllowNegative(false);
		ageVersion.setWidth(30);

		MultiField<Integer> applicationVersion = new MultiField<Integer>("Version", majorVersion, minorVersion, ageVersion);
		applicationVersion.setSpacing(5);
		applicationInformation.add(applicationVersion, formData);

		ContentPanel cp = new ContentPanel(new FitLayout());
		cp.setHeading("Entry Points");
		cp.setHeight(110);

		final CheckBoxSelectionModel<EntryPointModel> sm = new CheckBoxSelectionModel<EntryPointModel>();

		ColumnConfig entrypointColumn = new ColumnConfig(EntryPointModel.PROPERTY_NAME, "Entry Point", 100);
		TextField<String> text = new TextField<String>();  
		text.setAllowBlank(false);
		text.setEmptyText("e.g. /stocks/codes");
		entrypointColumn.setEditor(new CellEditor(text));  

		ArrayList<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		columns.add(sm.getColumn());
		columns.add(entrypointColumn);

		ColumnModel columnModel = new ColumnModel(columns);

		store = new ListStore<EntryPointModel>();

		final EditorGrid<EntryPointModel> entryPointGrid = new EditorGrid<EntryPointModel>(store, columnModel);
		entryPointGrid.setSelectionModel(sm);
		entryPointGrid.setAutoExpandColumn(EntryPointModel.PROPERTY_NAME);
		entryPointGrid.setHeight(100);
		entryPointGrid.addPlugin(sm);
		entryPointGrid.getView().setShowDirtyCells(false);

		cp.add(entryPointGrid, formData);

		ToolBar toolBar = new ToolBar();
		toolBar.setAlignment(HorizontalAlignment.RIGHT);

		cp.setBottomComponent(toolBar);

		applicationInformation.add(cp, formData);

		configPanel.add(applicationInformation, formData);

		return configPanel;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup() {
		setEnableBackButton(false);
		
		mask("Retrieving file informations...");

		timer.run();
		timer.scheduleRepeating(500);
	}

	protected void checkConfiguration()
	{
		setEnableNextButton(validateConfiguration());
	}

	protected boolean validateConfiguration()
	{
		boolean valid =  configPanel.isValid(true);
		valid &= isEntryListValid();
		return valid;
	}

	protected boolean isEntryListValid()
	{
		for (EntryPointModel entryPoint:store.getModels()) if (entryPoint.getValue().equals("")) return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		timer.cancel();
	}


	protected class EntryPointModel extends BaseModelData {

		private static final long serialVersionUID = -5919213535841177519L;

		public static final String PROPERTY_NAME = "ENTRYPOINT";

		public EntryPointModel(String value)
		{
			setValue(value);
		}

		/**
		 * Returns the value.
		 * 
		 * @return the value
		 */
		public String getValue() {
			return (String) get(PROPERTY_NAME);
		}

		/**
		 * Sets the value.
		 * 
		 * @param value the value
		 */
		public void setValue(String value) {
			set(PROPERTY_NAME, value);
		}		
	}

}
