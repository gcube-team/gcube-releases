//package org.gcube.portlets.user.workspace.client.uploader;
//
//
//import org.gcube.portlets.user.workspace.client.AppControllerExplorer;
//import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
//import org.gcube.portlets.user.workspace.client.event.CompletedFileUploadEvent;
//import org.gcube.portlets.user.workspace.client.model.FileModel;
//import org.gcube.portlets.user.workspace.client.view.windows.InfoDisplayMessage;
//import org.gcube.portlets.user.workspace.client.view.windows.MessageBoxAlert;
//import org.gcube.portlets.user.workspace.client.view.windows.MessageBoxConfirm;
//import org.gcube.portlets.user.workspace.client.view.windows.MessageBoxWait;
//import org.gcube.portlets.user.workspace.shared.HandlerResultMessage;
//
//import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
//import com.extjs.gxt.ui.client.event.ButtonEvent;
//import com.extjs.gxt.ui.client.event.Events;
//import com.extjs.gxt.ui.client.event.FormEvent;
//import com.extjs.gxt.ui.client.event.Listener;
//import com.extjs.gxt.ui.client.event.MessageBoxEvent;
//import com.extjs.gxt.ui.client.event.SelectionListener;
//import com.extjs.gxt.ui.client.widget.Dialog;
//import com.extjs.gxt.ui.client.widget.Info;
//import com.extjs.gxt.ui.client.widget.Window;
//import com.extjs.gxt.ui.client.widget.button.Button;
//import com.extjs.gxt.ui.client.widget.form.FileUploadField;
//import com.extjs.gxt.ui.client.widget.form.FormPanel;
//import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
//import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
//import com.google.gwt.user.client.Timer;
//import com.google.gwt.user.client.rpc.AsyncCallback;
//import com.google.gwt.user.client.ui.HTML;
//import com.google.gwt.user.client.ui.Hidden;
//
//
///**
// * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
// *
// */
//public class DialogUpload extends Window {  
//
//	private final FormPanel formPanel = new FormPanel(); 
//	private FileUploadField fileUploadField = new FileUploadField();  
//	private MessageBoxWait messageBoxWait = null;
//	private boolean isStatusCompleted = false;
//	private Button btnSubmit  = new Button("Submit"); 
//	private Button btnCancel = new Button("Cancel");  
//	private Hidden hiddenOverwrite = new Hidden(ConstantsExplorer.IS_OVERWRITE,"false");
//
//
//	private String parentIdentifier = "";
//	private String parentName = "";
//
//	public DialogUpload(String headerTitle, String parentName, final FileModel parent, String fieldLabel){
//
//		this.setHeaderVisible(true);
//		this.setHeading(headerTitle + parentName);
//		this.parentIdentifier = parent.getIdentifier();
//		this.parentName = parentName;
//		this.setStyleAttribute("margin", "10px");  
//
//		// Create a FormPanel and point it at a service.
//
//		// Create a FormPanel and point it at a service.
//		formPanel.setHeaderVisible(false);
//		formPanel.setFrame(true);  
//		formPanel.setAction(ConstantsExplorer.UPLOAD_WORKSPACE_SERVICE);  
//		formPanel.setEncoding(Encoding.MULTIPART);  
//		formPanel.setMethod(Method.POST);  
//
//		formPanel.setButtonAlign(HorizontalAlignment.CENTER);  
//		formPanel.setWidth(400);  
//
//		//	    TextField<String> name = new TextField<String>();  
//		//	    name.setFieldLabel("Name");  
//		//	    formPanel.add(name);  
//
//		fileUploadField.setAllowBlank(false);  
//		fileUploadField.setName(ConstantsExplorer.UPLOAD_FORM_ELEMENT);  
//
//		// Add hidden parameters
//		formPanel.add(new Hidden(ConstantsExplorer.ID_FOLDER,parent.getIdentifier()));
//		formPanel.add(new Hidden(ConstantsExplorer.UPLOAD_TYPE,fieldLabel));
//		formPanel.add(hiddenOverwrite);
//		
//		//	    fileUploadField.setFieldLabel(ConstantsExplorer.FILE);    
//		fileUploadField.setFieldLabel(fieldLabel);  
//		formPanel.add(fileUploadField);  
//		formPanel.addButton(btnSubmit); 
//
//		formPanel.addButton(btnCancel);  
//
//
//		// handle the post  
//		formPanel.addListener(Events.Submit, new Listener<FormEvent>() { 
//
//			public void handleEvent(FormEvent event) {
//				// When the form submission is successfully completed, this
//				// event is
//				// fired. Assuming the service returned a response of type
//				// text/html,
//
//				isStatusCompleted = true;
//
//
//				//				Log.trace("onSubmitComplete");
//				String result = event.getResultHtml();
//
//				//				Log.trace("Result "+result);
//				hide();
//
//				messageBoxWait.getMessageBoxWait().close();
//
//				if (result == null)	{
//					//					MessageUtil.showErrorMessage("Error during upload", "An error occurred during file upload.");
//					new MessageBoxAlert("Error during upload", "An error occurred during file upload.", null); 
//					return;
//				}
//
//				//expected <pre>200:Upload complete</pre>
//				/*we strip tags added by webserver, 
//				 * 
//				 * Massi fix because webkit returns 
//				 * <pre style="word-wrap: break-word; white-space: pre-wrap;">OK:File france_flag.png(0) imported correctly in /Workspace</pre>
//				 * 
//				 * TODO: recall it next time
//				 */
//				//String strippedResult = result.replace("<pre>", "").replace("</pre>", ""); //this won't work for webkit
//				//replaced by new HTML(result).getText()
//				String strippedResult = new HTML(result).getText();
//
//				
////				com.google.gwt.user.client.Window.alert(result);
////				com.google.gwt.user.client.Window.alert("Stripped: " + strippedResult);
//
//				final HandlerResultMessage resultMessage = HandlerResultMessage.parseResult(strippedResult);
//
//				switch (resultMessage.getStatus()) {
//				case ERROR: 
//					new MessageBoxAlert("Error during upload", resultMessage.getMessage(), null); 
//					break;
//				case UNKNOWN: 
//					new MessageBoxAlert("Error during upload", resultMessage.getMessage(), null); 
//					break;
//				case WARN: {
//					new MessageBoxAlert("Upload completed with warnings", resultMessage.getMessage(), null); 
//					break;
//				}
//				case OK: {
//					Timer t = new Timer() {
//						public void run() {
//							AppControllerExplorer.getEventBus().fireEvent(new CompletedFileUploadEvent(parent, null));
//							new InfoDisplayMessage("Upload completed successfully", resultMessage.getMessage());
//						}
//					};
//
//					t.schedule(250);
//					
//				}
//				}
//			}
//		});
//
//		add(formPanel);  
//
//		this.addListeners();
//
//		this.setAutoWidth(true);
//		this.setAutoHeight(true);
//
////		this.show();
//
//	}
//
//	private void addListeners() {
//
//
//		btnSubmit.addSelectionListener(new SelectionListener<ButtonEvent>() {  
//			@Override  
//			public void componentSelected(ButtonEvent ce) {  
//				if (fileUploadField.getValue()==null || !(fileUploadField.getValue().length()>2)) {  
//					new MessageBoxAlert(ConstantsExplorer.ERROR, ConstantsExplorer.NOFILESPECIFIED, null);
//					return;  
//				}  
//
////				com.google.gwt.user.client.Window.alert("parentIdentifier "+parentIdentifier);
////				com.google.gwt.user.client.Window.alert("fileUploadField.getValue() "+fileUploadField.getValue());
////				com.google.gwt.user.client.Window.alert("parentName "+parentName);
//				/*
//				 * TODO: recall: Some browser would write in fileUploadField.getValue() C:\fakepath\$fileName
//				 */
//				String normalizedFileName = fileUploadField.getValue();
//				
////				com.google.gwt.user.client.Window.alert("fileUploadField.getValue() "+fileUploadField.getValue());
//				if (normalizedFileName.contains("\\")) {
//					normalizedFileName = normalizedFileName.substring(normalizedFileName.lastIndexOf("\\")+1); //remove C:\fakepath\ if exists
////					com.google.gwt.user.client.Window.alert("normalizedFileName= "+normalizedFileName);
//				}
//				final String label = normalizedFileName;
//				AppControllerExplorer.rpcWorkspaceService.itemExistsInWorkpaceFolder(parentIdentifier, normalizedFileName, new AsyncCallback<String>() {
//
//					@Override
//					public void onSuccess(final String itemId) {
//
//						if(itemId!=null){
//
//							MessageBoxConfirm msg = new MessageBoxConfirm("Replace "+label+"?", label + " exists in folder "+parentName + ". Overwrite?");
//
//
//							msg.getMessageBoxConfirm().addCallback(new Listener<MessageBoxEvent>() {
//
//								public void handleEvent(MessageBoxEvent be) {
//
//									//IF NOT CANCELLED
//									String clickedButton = be.getButtonClicked().getItemId();
//
//									if(clickedButton.equals(Dialog.YES)){
//
////										removeItemAndSubmitForm(itemId);
//										updateItemSubmitForm(itemId);
//									}
//
//
//								}
//							});	 
//						}else
//							submitForm(); //ITEM does NOT EXIST SO SUBMIT FORM;
//
//					}
//
//					@Override
//					public void onFailure(Throwable caught) {
//						Info.display("Error", "Sorry an error occurred on the server "+caught.getLocalizedMessage() + ". Please try again later");
//
//					}
//
//				});
//
//			}  
//		});  
//
//
//		btnCancel.addSelectionListener(new SelectionListener<ButtonEvent>() {  
//			@Override  
//			public void componentSelected(ButtonEvent ce) {  
//				hide();  
//			}  
//		});  
//	}
//
//
//	public void submitForm(){
//
//		messageBoxWait = new MessageBoxWait(ConstantsExplorer.PROGRESS, ConstantsExplorer.SAVINGYOURFILE, fileUploadField.getValue());
//
//		//Progress bar for upload
//		final Timer t = new Timer()
//		{
//			public void run()
//			{
//				if (isStatusCompleted)
//				{
//					cancel();
//					messageBoxWait.getMessageBoxWait().close();
//				}
//			}
//		};
//		t.scheduleRepeating(500);
//		formPanel.submit();  
//
//	}
//
//
//	private void removeItemAndSubmitForm(String itemId){
//
//		AppControllerExplorer.rpcWorkspaceService.removeItem(itemId, new AsyncCallback<Boolean>() {
//
//			@Override
//			public void onFailure(Throwable caught) {
//				Info.display("Error", caught.getMessage());
//
//
//			}
//
//			@Override
//			public void onSuccess(Boolean result) {
//				if(result){
//					hiddenOverwrite.setValue("true");
//					submitForm();
//				}
//
//			}
//
//		});
//	}
//	
//	
//	private void updateItemSubmitForm(String itemId){
//
//
//		hiddenOverwrite.setValue("true");
//		submitForm();
//	}
//}  
//
