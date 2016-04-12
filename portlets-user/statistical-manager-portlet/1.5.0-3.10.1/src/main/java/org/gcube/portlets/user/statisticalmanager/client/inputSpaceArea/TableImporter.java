/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.inputSpaceArea;

import org.gcube.portlets.user.csvimportwizard.client.ImportWizard;
import org.gcube.portlets.user.csvimportwizard.client.general.WizardListener;
import org.gcube.portlets.user.csvimportwizard.client.source.local.LocalSource;
import org.gcube.portlets.user.csvimportwizard.ws.client.WorkspaceSource;
import org.gcube.portlets.user.statisticalmanager.client.Constants;
import org.gcube.portlets.user.statisticalmanager.client.StatisticalManager;
import org.gcube.portlets.user.statisticalmanager.client.StatisticalManagerPortletServiceAsync;
import org.gcube.portlets.user.statisticalmanager.client.bean.CsvMetadata;
import org.gcube.portlets.user.statisticalmanager.client.events.ImportCreatedEvent;
import org.gcube.portlets.user.statisticalmanager.client.resources.Images;
import org.gcube.portlets.user.statisticalmanager.client.util.EventBusProvider;
import org.gcube.portlets.user.statisticalmanager.client.util.StringUtil;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
 
 
public class TableImporter extends LayoutContainer {
	protected static final String MESSAGE_IMPORT_START_SUCCESS = "The data set import processing has correctly started";
	protected static final String MESSAGE_IMPORT_START_FAIL = "Impossible to start the import of the data set";
	protected TextField<String> nameField;
//	protected String workspaceItemId;
	protected Button importButton;
	protected TextArea textArea;
	private TemplateSelector templateSelector = new TemplateSelector();
	private CsvMetadata currentCsvMetadata = new CsvMetadata();
	private Button importCsvWizardButton;

	private StatisticalManagerPortletServiceAsync service;
	private boolean isCsvImported = false;
 
	public TableImporter() 	{
		this.service = StatisticalManager.getService();
		this.addStyleName("tableImporter");
		
		Image img = new Image(StatisticalManager.resources.inputSpaceImporter());
		img.addStyleName("workflow-icon");
		this.add(img);

		Html title = new Html("Data Set Importer");
		title.addStyleName("tableImporter-title");
		this.add(title);

		Html description = new Html("Import a data set in CSV format from a local source or from the workspace. Then indicate the template this is compliant to. Set a Name for this data set and possibly a description. The system will validate the template and create a certified data set.");
		description.addStyleName("tableImporter-description");
		this.add(description);

		FormPanel formPanel = createFormPanel();
		add(formPanel);
 
		importButton.setStyleAttribute("margin", "auto");
 
		add(importButton);
	}
 
	/**
	 * @return
	 */
	private FormPanel createFormPanel() {
		final FormPanel panel = new FormPanel() {
			@Override
			public boolean isValid(boolean preventMark) {
				boolean flag = super.isValid(preventMark);
				if (flag)
					return templateSelector.isValid() && isCsvImported;
				else
					return false;
			}
		};
		
		panel.setLabelWidth(100);
		panel.setStyleAttribute("margin-top", "5px");
		panel.setHeaderVisible(false);
		panel.setBorders(false);
		panel.setBodyStyle("background: none; padding: 5px");
		panel.addStyleName("tableImporter-form");
 
		nameField = new TextField<String>();
		nameField.setMessageTarget("none");
		nameField.setFieldLabel("Data Set Name");
		nameField.setAllowBlank(false);
		nameField.setEmptyText("Enter a Data Set name...");
		nameField.setMaxLength(255);
 
		textArea = new TextArea();
		textArea.setStyleAttribute("margin-top", "20px");
		textArea.setFieldLabel("Description");
 
		final Html hpText = new Html("<div class='tableImporter-fileName'>&nbsp;</div>");
		importCsvWizardButton = new Button("Open CSV Importer Wizard", Images.folderExplore());
		importCsvWizardButton.setStyleAttribute("margin", "auto");
		importCsvWizardButton.setStyleAttribute("margin-top", "10px");
		importCsvWizardButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				ImportWizard importWizard = new ImportWizard("StatisticalCSVTarget", LocalSource.INSTANCE, WorkspaceSource.INSTANCE);
				importWizard.show();
				importWizard.addListener(new WizardListener() {
					 
					@Override
					public void failed(Throwable throwable, String reason, String details) {
						MessageBox.alert("Error", "FAILED reason: "+reason+" details: "+details+" throwable: "+throwable, null);
						csvImported(null);
						hpText.setHtml("<div class='tableImporter-fileName'>&nbsp;</div>");
						importCsvWizardButton.setText("Import and set a CSV file");
					}
			 
					@Override
					public void completed() {
						StatisticalManager.getService().getCsvMetadataFromCsvImporterWizard(new AsyncCallback<CsvMetadata>() {
							@Override
							public void onFailure(Throwable caught) {
								csvImported(null);
								hpText.setHtml("<div class='tableImporter-fileName'>&nbsp;</div>");
								importCsvWizardButton.setText("Import and set a CSV file");
							}
							@Override
							public void onSuccess(CsvMetadata result) {
								csvImported(result);
								hpText.setHtml("<div class='tableImporter-fileName'><center>Csv file selected</center></div>");
								importCsvWizardButton.setText("Import and set another CSV file");
							}
						});
					}
			 
					@Override
					public void aborted() {
						csvImported(null);
						hpText.setHtml("<div class='tableImporter-fileName'>&nbsp;</div>");
						importCsvWizardButton.setText("Import and set a CSV file");
					}
				});
			}
		});

		importButton = new Button("Import");
		importButton.setIcon(Images.table());
		importButton.addSelectionListener(new SelectionListener<ButtonEvent>() { 
			@Override
			public void componentSelected(ButtonEvent ce) {
				String tableName = StringUtil.clean(nameField.getValue());
				String descr = StringUtil.clean(textArea.getValue());
				String template = templateSelector.getValue();
				importTable(tableName, descr, template);				
			}
		});
 
		FormButtonBinding binding = new FormButtonBinding(panel);		
		binding.addButton(importButton);

//		setFocusWidget(getButtonBar().getWidget(0));
 
		panel.add(nameField);
		panel.add(importCsvWizardButton);
		panel.add(hpText);
		panel.add(templateSelector);
		panel.add(textArea);
		return panel;
	}

	protected void importTable(String tableName, String descr, String template) {
		currentCsvMetadata.setTableName(tableName);
		currentCsvMetadata.setDescription(descr);
		currentCsvMetadata.setTemplate(template);
		service.importTable(currentCsvMetadata, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String importId) {
				Info.display("Import Started", MESSAGE_IMPORT_START_SUCCESS);
				EventBusProvider.getInstance().fireEvent(new ImportCreatedEvent(importId));
				resetFields();
				unmask();
			}
			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert("Error", MESSAGE_IMPORT_START_FAIL+"<br/>Cause: "+caught.getCause()+"<br/>Message: "+caught.getMessage(), null);
				resetFields();
				unmask();
			}
		});
		this.mask("Importing...", Constants.maskLoadingStyle);
	}
 
	private void csvImported(CsvMetadata result) {
		if (result==null)
			isCsvImported = false;
		else {	
			currentCsvMetadata.setDelimiterChar(result.getDelimiterChar());
			currentCsvMetadata.setFileAbsolutePath(result.getFileAbsolutePath());
			currentCsvMetadata.setCommentChar(result.getCommentChar());
			currentCsvMetadata.setHasHeader(result.isHasHeader());
			isCsvImported = true;
		}
	}

	private void resetFields() {
		textArea.setValue(null);
		nameField.setValue(null);
		isCsvImported = false;
		templateSelector.resetField();
	}
}