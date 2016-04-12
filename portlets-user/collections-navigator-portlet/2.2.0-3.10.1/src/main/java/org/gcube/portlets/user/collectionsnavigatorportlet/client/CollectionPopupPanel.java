package org.gcube.portlets.user.collectionsnavigatorportlet.client;

import java.util.ArrayList;

import org.gcube.portlets.user.collectionsnavigatorportlet.shared.CollectionInfo;
import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class CollectionPopupPanel extends GCubeDialog implements ClickListener{

	private CollectionsNavigatorPortletG navigator = null;

	public CollectionPopupPanel(CollectionInfo[] colInfo, CollectionsNavigatorPortletG navigator) {
		// PopupPanel's constructor takes 'auto-hide' as its boolean parameter.
		// If this is set, the panel closes itself automatically when the user
		// clicks outside of it.
		super(true);
		setText(CollectionsConstants.popup_label);
		// keep reference to the "parent" collectionsNavigator
		this.navigator = navigator;

		DockPanel dPanel = new DockPanel();
		dPanel.setSpacing(2);
		FlexTable collectionsNames = new FlexTable();

		for (int i=0; i<colInfo.length; i++) {
			Label collectionLabel = new Label(colInfo[i].getName());
			CheckBox selectCollectioncheckBox = new SelectCollectionCheckBox(colInfo[i]);
			Image colImage = new Image();
			if (colInfo[i].isCollectionGroup()) {
				colImage.setUrl(GWT.getModuleBaseURL() + "../images/col_group.png");
				colImage.setTitle("Collection group");
				collectionLabel.setTitle("Collection Group");
			}
			else {
				colImage.setUrl(GWT.getModuleBaseURL() + "../images/col.png");
				colImage.setTitle("Individual collection");
				collectionLabel.setTitle("Individual collection");
			}
			collectionsNames.setWidget(i, 0, colImage);
			collectionsNames.setWidget(i, 1, collectionLabel);
			collectionsNames.setWidget(i, 2, selectCollectioncheckBox);
		}
		collectionsNames.setWidth("100%");
		dPanel.add(collectionsNames,DockPanel.CENTER);
		dPanel.setCellHorizontalAlignment(collectionsNames, DockPanel.ALIGN_LEFT);
		dPanel.setWidth("100%");
		add(dPanel);
	}

	public void onClick(Widget sender) {
		hide();
	}

	class SelectCollectionButton extends Button implements ClickListener {

		private CollectionInfo colInfo;

		SelectCollectionButton(CollectionInfo colInfo) {
			super("Select");
			this.setStyleName("select-button");
			this.addClickListener(this);
			this.colInfo = colInfo;
		}

		public void onClick(Widget sender) {
			ArrayList<String> collection = new ArrayList<String>();
			collection.add(this.colInfo.getId());
			navigator.selectCollection(colInfo.getId(), true);
		}
	}

	class SelectCollectionCheckBox extends CheckBox implements ClickListener {

		private CollectionInfo colInfo;

		SelectCollectionCheckBox(CollectionInfo colInfo) {
			super("");
			this.setEnabled(true);
			this.setTitle(CollectionsConstants.select_checkbox_info);
			this.addClickListener(this);
			this.colInfo = colInfo;
			AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>()
			{
				public void onFailure(Throwable caught)
				{
				}

				public void onSuccess(Boolean result)
				{
					setChecked(result.booleanValue());
				}
			};
			CollectionsNavigatorPortletG.collectionsService.isCollectionSelected(colInfo.getId(), callback);
		}

		public void onClick(Widget sender) {
			navigator.selectCollection(colInfo.getId(), this.isChecked());
		}
	}
}
