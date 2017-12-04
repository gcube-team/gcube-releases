package org.gcube.portlets.widgets.exporter.client;




import org.gcube.portlets.d4sreporting.common.shared.Model;
import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;
import org.gcube.portlets.widgets.exporter.client.event.ExportingCompletedEvent;
import org.gcube.portlets.widgets.exporter.client.event.ReportExporterEvent;
import org.gcube.portlets.widgets.exporter.client.event.ReportExporterEvent.OperationResult;
import org.gcube.portlets.widgets.exporter.shared.SaveReportFileExistException;
import org.gcube.portlets.widgets.exporter.shared.TypeExporter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;


public class ReportExporterPopup {
	
	
	private ReportExporterServiceAsync rpc = GWT.create(ReportExporterService.class);
	private HandlerManager handler;

	private VerticalPanel mainPanel = new VerticalPanel();
	
	public ReportExporterPopup(final HandlerManager handler) {
		this.handler = handler;
	}
	
	public void export(final Model model, final TypeExporter type) {
		
		final GCubeDialog window = new GCubeDialog();  
	    window.setSize("250px", "110px");  
	    
	    window.setText("Export to " + type.toString());  
	    
	    final CheckBox instructionsCheckBox = new CheckBox();
	    instructionsCheckBox.setText("Include Instructions");
	    
	    final CheckBox commentsCheckBox = new CheckBox();
	    commentsCheckBox.setText("Include Comments");
	    
	       
	    Button cancel = new Button("Cancel", new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				window.hide();				
			}
		});
	    
	    
	    Button ok = new Button("Start export", new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				mainPanel.clear();
				mainPanel.add(new HTML("Processing your data, please wait..."));
	
				rpc.convert(model, instructionsCheckBox.getValue(),
						commentsCheckBox.getValue(), type, new AsyncCallback<String>() {
							@Override
							public void onFailure(Throwable caught) {											
								Window.alert("Error: " + caught.getMessage());								
							}

							@Override
							public void onSuccess(final String result) {
								window.hide();		
								showSaveDialog(result, model.getTemplateName(), type);								    								
							}
				});		
			}
		});
	    
	    HorizontalPanel hp = new HorizontalPanel();
	    hp.setWidth("250px");
	    hp.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
	    
	    HorizontalPanel hp2 = new HorizontalPanel();
	  
	    window.add(hp);
	    hp.add(hp2);
	    hp2.add(cancel);
	    hp2.add(ok);

	   hp.getElement().getStyle().setMarginTop(10, Unit.PX);
	    
	    mainPanel.add(instructionsCheckBox);
	    mainPanel.add(commentsCheckBox);
	    mainPanel.add(hp);
	    
	    window.setWidget(mainPanel);
	    window.center();
	    window.show();
	 
	}
	
	public class SaveDialogCallBack implements AsyncCallback<String> {

		private final GCubeDialog saveDialog;
		private final OperationResult operation;
		
		public SaveDialogCallBack(final GCubeDialog saveDialog, final OperationResult operation) {
			this.saveDialog = saveDialog;
			this.operation = operation;
		}
		
		@Override
		public void onFailure(
				Throwable caught) {
			
			handler.fireEvent(new ReportExporterEvent(OperationResult.FAILURE, null));
			
			if (caught instanceof SaveReportFileExistException) {
				Window.alert("A file with the same name exists in your workspace root folder," +
						" please use \"Save As\" or remove it first.");  
			} else {
				Window.alert("Error saving file, please report an issue");
			}
				
		}

		@Override
		public void onSuccess(String result) {			
			saveDialog.hide();			
			handler.fireEvent(new ReportExporterEvent(operation, result));
			Window.alert("File has been saved successfully");
		}
		
	}
	/**
	 * 
	 * @param filePath
	 * @param itemName
	 * @param type
	 */
	private void showSaveDialog(final String filePath, final String itemName, final TypeExporter type) {		
		handler.fireEvent(new ExportingCompletedEvent(filePath, itemName, type));
		
//		final Window saveDialog = new Window();  
//		saveDialog.setHeading("Save dialog"); 
//		saveDialog.addText("Click Save to save in your root folder," +
//				" Save As to specify a location.");
//		
//		saveDialog.setSize(300, 200);
//		
//		saveDialog.addButton(new Button("Save & Open", new SelectionListener<ButtonEvent>() {  
//			public void componentSelected(ButtonEvent ce) {  
//				
//				//saveDialog.hide();
//				rpc.save(filePath, null, itemName, type,new SaveDialogCallBack(saveDialog,OperationResult.SAVED_OPEN));
//			}  
//		})); 
//		
//		saveDialog.addButton(new Button("Save", new SelectionListener<ButtonEvent>() {  
//			public void componentSelected(ButtonEvent ce) {  
//				
//				//saveDialog.hide();
//				rpc.save(filePath, null, itemName, type,new SaveDialogCallBack(saveDialog,OperationResult.SAVED));
//			}  
//		})); 
//		
//		saveDialog.addButton(new Button("Save as", new SelectionListener<ButtonEvent>() {  
//			public void componentSelected(ButtonEvent ce) { 
//				
//				//saveDialog.hide();
//				GWT.runAsync(WorkspaceLightTreeSavePopup.class, new RunAsyncCallback() {
//					public void onSuccess() {
//						WorkspaceLightTreeSavePopup wpTree = new WorkspaceLightTreeSavePopup("Save file, choose folder please:", true);
//						wpTree.setSelectableTypes(ItemType.FOLDER, ItemType.ROOT);
//						wpTree.setShowEmptyFolders(true);
//						wpTree.addPopupHandler(new PopupHandler(){
//
//							@Override
//							public void onPopup(
//									PopupEvent event) {
//									
//								rpc.save(filePath, event.getSelectedItem().getId(),
//										event.getName(),type, new SaveDialogCallBack(saveDialog, OperationResult.SAVED));								
//							}});	
//						
//						wpTree.show();
//						wpTree.center();
//					}
//
//					public void onFailure(Throwable reason) {
//						      
//						MessageBox.alert("Alert", reason.getMessage(), null);  
//					}
//				});
//				
//			}  
//		})); 
//
//		saveDialog.show();
	}
	
	  
	
}
