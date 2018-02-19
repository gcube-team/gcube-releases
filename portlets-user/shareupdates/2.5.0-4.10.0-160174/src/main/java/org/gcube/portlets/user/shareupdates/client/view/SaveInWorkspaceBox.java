package org.gcube.portlets.user.shareupdates.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class SaveInWorkspaceBox extends Composite {

	private static SaveInWorkspaceBoxUiBinder uiBinder = GWT
			.create(SaveInWorkspaceBoxUiBinder.class);

	interface SaveInWorkspaceBoxUiBinder extends
			UiBinder<Widget, SaveInWorkspaceBox> {
	}
	
	@UiField
	CheckBox saveCheckBox;

	public SaveInWorkspaceBox() {
		initWidget(uiBinder.createAndBindUi(this));
		
		// set as not visibile
		this.setVisible(false);
	}

	protected boolean getValue() {
		return saveCheckBox.getValue();
	}

}
