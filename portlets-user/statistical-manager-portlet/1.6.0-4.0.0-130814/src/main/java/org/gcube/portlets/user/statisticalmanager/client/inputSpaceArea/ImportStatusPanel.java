/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.inputSpaceArea;

import org.gcube.portlets.user.statisticalmanager.client.StatisticalManager;
import org.gcube.portlets.user.statisticalmanager.client.bean.ComputationStatus.Status;
import org.gcube.portlets.user.statisticalmanager.client.bean.ImportStatus;
import org.gcube.portlets.user.statisticalmanager.client.bean.output.Resource;
import org.gcube.portlets.user.statisticalmanager.client.events.ImportRemovedEvent;
import org.gcube.portlets.user.statisticalmanager.client.events.ImportTerminatedEvent;
import org.gcube.portlets.user.statisticalmanager.client.events.SessionExpiredEvent;
import org.gcube.portlets.user.statisticalmanager.client.util.EventBusProvider;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * @author ceras
 * 
 */
public class ImportStatusPanel extends HorizontalPanel {

	private ImportStatus importStatus;
	private Image loadingImg = new Image(StatisticalManager.resources.loader());
	private Image completedImg = new Image(StatisticalManager.resources.loadingComplete());
	private Image failedImg = new Image(StatisticalManager.resources.error());
	private Image removeImg = new Image(StatisticalManager.resources.cancel());
	private Status actualStatus = null;
	private String removeAlertMessage = "Do you want to stop this import?";

	public ImportStatusPanel(ImportStatus importStatus) {
		this.importStatus = importStatus;

		removeImg.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (ImportStatusPanel.this.importStatus.isTerminated())
					removeImport();
				else {
					MessageBox.confirm("Confirm removing", removeAlertMessage,
							new Listener<MessageBoxEvent>() {
								@Override
								public void handleEvent(MessageBoxEvent be) {
									if (be.getButtonClicked().getText()
											.contentEquals("Yes"))
										removeImport();
								}
							});
				}
			}
		});
		removeImg.setStyleName("imgCursor");

		Status status = importStatus.getStatus();
		updateStatus(status, importStatus.getResoruce());
	}

	private void updateStatus(Status status, Resource resource) {
		importStatus.setStatus(status);
		if (status != actualStatus) {
			this.clear();
			Html html = new Html("<div class='uploadMonitor-text'>Upload of "
					+ importStatus.getFileName() + " [" + importStatus.getId()
					+ "]</div>");
			Image img = null;

			switch (status) {
			case COMPLETE:
				img = completedImg;
				EventBusProvider.getInstance().fireEvent(
						new ImportTerminatedEvent(importStatus));
				break;
			case FAILED:
				img = failedImg;
				if (resource != null) {
					System.out.println("resource is not null");
					importStatus.setResource(resource);
				EventBusProvider.getInstance().fireEvent(
						new ImportTerminatedEvent(importStatus));
				
				}
				break;
			case PENDING:
				img = loadingImg;
				break;
			case RUNNING:
				img = loadingImg;
				break;
			}

			this.add(img);
			this.add(html);
			this.add(removeImg);
			this.setCellWidth(img, "25px");
			this.setCellWidth(html, "350px");
			this.setCellWidth(removeImg, "25px");

			actualStatus = status;
		}
	}

	public ImportStatus getImportStatus() {
		return importStatus;
	}

	public void updateStatus() {
		// System.out.println("[UPLOAD MONITOR] Update STATUS "+importStatus.getFileName()+"... ");
		StatisticalManager.getService().getImportStatusById(
				importStatus.getId(), new AsyncCallback<ImportStatus>() {
					@Override
					public void onSuccess(ImportStatus result) {
						updateStatus(result.getStatus(), result.getResoruce());
					}

					@Override
					public void onFailure(Throwable caught) {
						EventBusProvider.getInstance().fireEvent(
								new SessionExpiredEvent());
					}
				});
	}

	private void removeImport() {
		this.importStatus.isComplete();
		StatisticalManager.getService().removeImport(importStatus.getId(),
				new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						EventBusProvider.getInstance().fireEvent(
								new ImportRemovedEvent(ImportStatusPanel.this));
					}

					@Override
					public void onFailure(Throwable caught) {
						// MessageBox.alert("Error",
						// "Failed to import the data set. Maybe the template or the  configuration was wrong.",
						// null);
						MessageBox
								.alert("Error:",
										"Failed to import the data set. Maybe the template or the  configuration was wrong.",
										null);

					}
				});
	}

}
