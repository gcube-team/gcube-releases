package org.gcube.portlets.admin.fulltextindexportlet.gwt.client.ui.indexmanagement.index;

import java.util.ArrayList;

import org.gcube.portlets.admin.fulltextindexportlet.gwt.client.interfaces.ItemRemovedListener;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.client.interfaces.ManagementService;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.client.interfaces.ManagementServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;

/** The panel used to remove/destroy an Index */
public class RemovePanel extends Composite implements ClickHandler {

	/** An GWT RPC interface to the ManagementService */
	private ManagementServiceAsync mgmtService = null;

	/** the indexID of the Index to be removed */
	private String indexID = null;

	private String collectionID = null;

	/** The outer panel holding all the widgets of the RemovePanel */
	private VerticalPanel outerPanel;

	/** A button causing the removal warning DialogBox to be shown */
	final private Button removeButton;

	/** A Label informing the user that the Index was successfully removed */
	private Label successLabel;

	/** A Label informing the user that there was an error removing Index */
	private Label errorLabel;

	/**
	 * A Label informing the user of which Index this panel is currently
	 * managing
	 */
	private Label infoLabel;

	/**
	 * A Popup, warning the user and asking for confirmation before removing an
	 * Index
	 */
	private RemoveWarning warningPopup = null;

	/** A List of listeners needing information of removed Indices */
	private ArrayList listeners = new ArrayList();

	/** A constructor, initializing the panel */
	public RemovePanel() {
		outerPanel = new VerticalPanel();
		removeButton = new Button("REMOVE");
		removeButton.addClickHandler(this);
		successLabel = new Label();
		errorLabel = new Label();
		infoLabel = new Label();
		warningPopup = new RemoveWarning();

		mgmtService = (ManagementServiceAsync) GWT.create(ManagementService.class);

		successLabel.setVisible(false);
		successLabel.addStyleName("diligent-success");
		errorLabel.setVisible(false);
		errorLabel.addStyleName("diligent-error");
		infoLabel.addStyleName("diligent-index-info");

		Grid grid = new Grid(1, 2);
		grid.setWidget(0, 0, infoLabel);
		grid.setWidget(0, 1, removeButton);

		outerPanel.add(grid);
		outerPanel.add(successLabel);
		outerPanel.add(errorLabel);

		ServiceDefTarget endpoint = (ServiceDefTarget) mgmtService;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "/MgmtService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);

		initWidget(outerPanel);
	}

	/**
	 * A method which invokes the ManagementService asynchronously in order to
	 * remove the current Index.
	 */
	private void removeIndex(String indexID, String collectionID) {
		/** The indexID to be used by the callback inner class */
		final String id = indexID;
		final String colID = collectionID;
		boolean confirmed = Window.confirm("Are you sure you want to remove the index for the selected collection?");
		if (confirmed) {
			/**
			 * The callback which will handle the result of the ManagementService
			 * call in accordance with the GWT RPC framework
			 */
			AsyncCallback<Boolean> removeIndexCallback = new AsyncCallback<Boolean>() {
				/**
				 * {@inheritDoc}
				 */
				public void onSuccess(Boolean result) {
					if (result) {
						successLabel.setText("Index was removed: " + id + "for the collection with ID: " + colID);
						successLabel.setVisible(true);
						errorLabel.setVisible(false);
						for (int i = 0; i < listeners.size(); i++) {
							((ItemRemovedListener) listeners.get(i)).itemRemoved(id);
						}
					}
					removeButton.setEnabled(true);
				}

				/**
				 * {@inheritDoc}
				 */
				public void onFailure(Throwable caught) {
					errorLabel.setText("Error: " + caught.getMessage());
					successLabel.setVisible(false);
					errorLabel.setVisible(true);
					removeButton.setEnabled(true);
				}
			};mgmtService.removeIndex(indexID, collectionID, removeIndexCallback);
		}
	}

	/**
	 * Updates internal state and the GUI components when a new Index has been
	 * selected.
	 * 
	 * @param indexID -
	 *            the ID of the selected Index.
	 */
	public void setCurrentIndexID(String indexID, String collectionID) {
		if (this.indexID != indexID) {
			infoLabel.setText("Remove " + indexID + ":");
			this.indexID = indexID;
			this.collectionID = collectionID;
			warningPopup.hide();
			removeButton.setEnabled(true);
			successLabel.setVisible(false);
			errorLabel.setVisible(false);
		}
	}

	/**
	 * A method to add a listener needing information of removed Indices to the
	 * list of listeners.
	 * 
	 * @param listener -
	 *            the listener to be added
	 */
	public void addItemRemovedListener(ItemRemovedListener listener) {
		listeners.add(listener);
	}

	/** A DialogBox used to warn the user before removing an Index */
	private class RemoveWarning extends DialogBox implements ClickHandler {
		/** A label showing the ID of the Index about to be destroyed */
		private Label idxIDLabel;

		/** A button causing the index to be removed */
		private Button okButton;

		/** A button causing the removal process to be halted */
		private Button cancelButton;

		/** The ID of the Index to be removed */
		private String indexID = null;

		private String collectionID = null;

		/** A constructor, initializing the DialogBox */
		public RemoveWarning() {
			VerticalPanel msgPanel = new VerticalPanel();
			setText("WARNING!");

			Label warningLabel = new Label("Are you sure you want to remove the node and its indexes:", false);
			idxIDLabel = new Label();
			Label questionmarkLabel = new Label("?");
			msgPanel.add(warningLabel);
			msgPanel.add(idxIDLabel);
			msgPanel.add(questionmarkLabel);

			HorizontalPanel buttonPanel = new HorizontalPanel();
			okButton = new Button("OK", this);
			cancelButton = new Button("Cancel", this);
			buttonPanel.add(okButton);
			buttonPanel.add(cancelButton);

			DockPanel dock = new DockPanel();
			dock.setSpacing(4);
			dock.add(buttonPanel, DockPanel.SOUTH);
			dock.add(msgPanel, DockPanel.CENTER);
			dock.setWidth("100%");
			setWidget(dock);
			this.hide();
		}

		/**
		 * A Method used to set the IndexID of the Index about to be removed,
		 * before showing the DialogBox.
		 * 
		 * @param indexID -
		 *            The ID of the Index about to be removed.
		 */
		public void warn(String indexID, String collectionID) {
			idxIDLabel.setText(indexID);
			this.indexID = indexID;
			this.collectionID = collectionID;
			this.show();
		}

		public void onClick(ClickEvent arg0) {
			if (arg0.getSource() == okButton) {
				removeIndex(indexID, collectionID);
			} else {
				removeButton.setEnabled(true);
			}
			hide();
		}
	}

	@Override
	public void onClick(ClickEvent arg0) {
		try {
			removeButton.setEnabled(false);
			warningPopup.center();
			warningPopup.warn(indexID, collectionID);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
