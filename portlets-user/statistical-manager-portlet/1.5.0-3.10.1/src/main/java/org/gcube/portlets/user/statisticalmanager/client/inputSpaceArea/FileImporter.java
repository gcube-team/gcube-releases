/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.inputSpaceArea;


import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.user.statisticalmanager.client.Constants;
import org.gcube.portlets.user.statisticalmanager.client.StatisticalManager;
import org.gcube.portlets.user.statisticalmanager.client.StatisticalManagerPortletServiceAsync;
import org.gcube.portlets.user.statisticalmanager.client.bean.FileMetadata;
import org.gcube.portlets.user.statisticalmanager.client.events.ImportCreatedEvent;
import org.gcube.portlets.user.statisticalmanager.client.resources.Images;
import org.gcube.portlets.user.statisticalmanager.client.util.EventBusProvider;
import org.gcube.portlets.user.statisticalmanager.client.util.StringUtil;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.ImportWizard;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.general.WizardListener;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.source.local.LocalSource;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.source.workspace.WorkspaceSource;

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
 
public class FileImporter extends LayoutContainer {
	protected static final String MESSAGE_IMPORT_START_SUCCESS = "The file import processing has correctly started";
	protected static final String MESSAGE_IMPORT_START_FAIL = "Impossible to start the import of the file";
	protected TextField<String> nameField;
	protected Button importButton;
	protected TextArea textArea;
	protected FileMetadata fileMetadata= new FileMetadata();
	
    Logger logger = Logger.getLogger("");

	private Button importWizardButton;

	private StatisticalManagerPortletServiceAsync service;
	private boolean isImported = false;
 
	public FileImporter() 	{
		this.service = StatisticalManager.getService();
		this.addStyleName("tableImporter");
		
		Image img = new Image(StatisticalManager.resources.inputSpaceImporter());
		img.addStyleName("workflow-icon");
		this.add(img);

		Html title = new Html("File Importer");
		title.addStyleName("tableImporter-title");
		this.add(title);

		Html description = new Html("Import file from a local source. Set a Name for this data set and possibly a description. The system will  create a certified file.");
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
					return isImported;// templateSelector.isValid() && isImported;
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
		nameField.setFieldLabel("File Name");
		nameField.setAllowBlank(false);
		nameField.setEmptyText("Enter a file name...");
		nameField.setMaxLength(255);
 
		textArea = new TextArea();
		textArea.setStyleAttribute("margin-top", "20px");
		textArea.setFieldLabel("Description");
 
		final Html hpText = new Html("<div class='tableImporter-fileName'>&nbsp;</div>");
		importWizardButton = new Button("Open  Importer Wizard", Images.folderExplore());
		importWizardButton.setStyleAttribute("margin", "auto");
		importWizardButton.setStyleAttribute("margin-top", "10px");
		importWizardButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
//				ImportWizard importWizard = new ImportWizard("StatisticalCSVTarget", LocalSource.INSTANCE, WorkspaceSource.INSTANCE);
//				importWizard.show();
			    logger.log(Level.SEVERE, "ImportWizart creation... ");

				ImportWizard importWizard = new ImportWizard("StatisticalFileTarget", LocalSource.INSTANCE,  WorkspaceSource.INSTANCE);
				importWizard.show();
				importWizard.addListener(new WizardListener() {
					 
					@Override
					public void failed(Throwable throwable, String reason, String details) {
						MessageBox.alert("Error", "FAILED reason: "+reason+" details: "+details+" throwable: "+throwable, null);
						fileImported(null);
						hpText.setHtml("<div class='tableImporter-fileName'>&nbsp;</div>");
						importWizardButton.setText("Import file");
					    logger.log(Level.SEVERE, "ImportWizart creation FAIL!!! ");

					}
			 
					@Override
					public void completed() {
						StatisticalManager.getService().getFilePathFromImporterWizard(new AsyncCallback<FileMetadata>() {
							@Override
							public void onFailure(Throwable caught) {
								fileImported(null);
								hpText.setHtml("<div class='tableImporter-fileName'>&nbsp;</div>");
								importWizardButton.setText("Import file");
							}
							@Override
							public void onSuccess(FileMetadata result) {
							    logger.log(Level.SEVERE, "Take parameters from wizard ");

								fileImported(result);
								hpText.setHtml("<div class='tableImporter-fileName'><center>File selected</center></div>");
								importWizardButton.setText("Import file");
							}
						});
					}
			 
					@Override
					public void aborted() {
						fileImported(null);
						hpText.setHtml("<div class='tableImporter-fileName'>&nbsp;</div>");
						importWizardButton.setText("Import file");
					}
				});
			}
		});

		importButton = new Button("Import");
		importButton.setIcon(Images.table());
		importButton.addSelectionListener(new SelectionListener<ButtonEvent>() { 
			@Override
			public void componentSelected(ButtonEvent ce) {
				String fileName = StringUtil.clean(nameField.getValue());
				String descr = StringUtil.clean(textArea.getValue());
//				String template = templateSelector.getValue();
				importFile(fileMetadata,fileName, descr);				
			}
		});
 
		FormButtonBinding binding = new FormButtonBinding(panel);		
		binding.addButton(importButton);

//		setFocusWidget(getButtonBar().getWidget(0));
 
		panel.add(nameField);
		panel.add(importWizardButton);
		panel.add(hpText);
//		panel.add(templateSelector);
		panel.add(textArea);
		return panel;
	}

	protected void importFile(FileMetadata fileMetadata, String fileName, String descr) {

		fileMetadata.setDescription(descr);
		fileMetadata.setFileName(fileName);
		service.importFile(fileMetadata,  new AsyncCallback<String>() {
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
 
	private void fileImported(FileMetadata result) {
		if (result==null){
			isImported = false;
		}
		else {	

			fileMetadata.setFileAbsolutePath(result.getFileAbsolutePath());
			fileMetadata.setTaxaFileAbsolutePath( result.getTaxaFileAbsolutePath());
			fileMetadata.setVernacularFileAbsolutePath(result.getVernacularFileAbsolutePath());
			fileMetadata.setType(result.getType());

			isImported = true;
		}
	}

	private void resetFields() {
		textArea.setValue(null);
		nameField.setValue(null);
		isImported = false;
//		templateSelector.resetField();
	}
	
}