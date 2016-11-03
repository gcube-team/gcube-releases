package org.gcube.portlets.user.reportgenerator.client.uibinder;

import java.util.Arrays;

import org.gcube.portlets.user.reportgenerator.client.ReportServiceAsync;
import org.gcube.portlets.user.reportgenerator.client.ToolboxPanel;
import org.gcube.portlets.user.reportgenerator.client.Presenter.Presenter;
import org.gcube.portlets.widgets.exporter.shared.TypeExporter;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSaveNotification.WorskpaceExplorerSaveNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.save.WorkspaceExplorerSaveDialog;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class ExportOptions extends Composite {

	private static ExportOptionsUiBinder uiBinder = GWT
			.create(ExportOptionsUiBinder.class);

	interface ExportOptionsUiBinder extends UiBinder<Widget, ExportOptions> {
	}
	enum ExportMode {SAVE_OPEN, SAVE, SAVE_AS }

	ToolboxPanel tbp;
	@UiField HTML saveOpen;
	@UiField HTML save;
	@UiField HTML saveAs;
	@UiField HTML close;
	@UiField HTMLPanel myPanel;

	private String tempFileId;
	private String filePath;
	private String itemName;

	private  TypeExporter type;
	private ReportServiceAsync rpc;
	private Presenter p;

	public ExportOptions(Presenter p, ToolboxPanel tbp, final String filePath, final String itemName, final TypeExporter type, ReportServiceAsync rpc,  String tempFileId) {
		initWidget(uiBinder.createAndBindUi(this));
		this.tbp = tbp;
		this.filePath = filePath;
		this.itemName = itemName;
		this.type= type;
		this.rpc = rpc;
		this.p = p;
		this.tempFileId = tempFileId;
	}

	@UiHandler("close")
	void onSaveCancel(ClickEvent e) {
		myPanel.removeStyleName("exportPanel-show");
		//needed for applying the css3 transition effect
		final Timer t = new Timer() {
			@Override
			public void run() {
				p.clearExportPanel();
			}
		};
		t.schedule(500);		
	}

	@UiHandler("saveOpen")
	void onSaveOpenClick(ClickEvent e) {
		GWT.log("SaveOPen");
		doCallBack(ExportMode.SAVE_OPEN);
	}

	@UiHandler("save")
	void onSaveClick(ClickEvent e) {
		doCallBack(ExportMode.SAVE);
	}

	@UiHandler("saveAs")
	void onSaveAs(ClickEvent e) {
		doCallBack(ExportMode.SAVE_AS);
	}


	private void doCallBack(ExportMode mode) {
		switch(mode) {
		case SAVE:
			rpc.save(filePath, null, itemName, type, true, new AsyncCallback<String>() {			
				@Override
				public void onSuccess(String createdItemId) {
					p.clearExportPanel();
					tbp.refreshRoot();
				}

				@Override
				public void onFailure(Throwable caught) {		
					Window.alert("Error: " + caught.getMessage());
				}
			});
			break;
		case SAVE_AS:
			
			ItemType[] types = {ItemType.FOLDER};
			final WorkspaceExplorerSaveDialog navigator = new WorkspaceExplorerSaveDialog("Save Report, choose folder please:", Arrays.asList(types));
			WorskpaceExplorerSaveNotificationListener listener = new WorskpaceExplorerSaveNotificationListener(){
				 
				@Override
				public void onSaving(Item parent, String fileName) {
					rpc.save(filePath, parent.getId(), fileName, type, true, new AsyncCallback<String>() {			
						@Override
						public void onSuccess(String createdItemId) {
							p.clearExportPanel();
							tbp.refreshRoot();	
						}

						@Override
						public void onFailure(Throwable caught) {		
							Window.alert("Error: " + caught.getMessage());
						}
					});				
					navigator.hide();
				}
		 
				@Override
				public void onAborted() {
					GWT.log("onAborted");
				}
		 
				@Override
				public void onFailed(Throwable throwable) {
					GWT.log("onFailed");
				}
			};
			navigator.addWorkspaceExplorerSaveNotificationListener(listener);
		    navigator.show();				
			break;
		case
		SAVE_OPEN:
			/*
			 * Smart popup blockers (Chrome and Safari have them) will allow a popup if it is directly associated to a user’s action. 
			 * If it’s delayed in anyway, there’s a good chance it’s going to get blocked. 
			 * So the exported File needs to be there when clicking open, and I've saved a temp version previously.
			 */
			tbp.showExportedVersion(tempFileId, itemName);
			rpc.save(filePath, null, itemName, type, true, new AsyncCallback<String>() {			
				@Override
				public void onSuccess(String createdItemId) {
					p.clearExportPanel();
					tbp.refreshRoot();
				}

				@Override
				public void onFailure(Throwable caught) {		
					Window.alert("Error: " + caught.getMessage());
				}
			});
			break;	
		}
	}

	public HTMLPanel getMainPanel() {
		return myPanel;
	}

}
