package org.gcube.portlets.admin.searchmanagerportlet.gwt.client.widgets;

import org.gcube.portlets.admin.searchmanagerportlet.gwt.shared.FieldInfoBean;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * This widget represents a field cell with all the needed information that represents it
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class FieldCell extends Composite {

	private HorizontalPanel mainPanel = new HorizontalPanel();
	private Label fieldLabel = new Label();
	private Button deleteFieldBtn = new Button();
	private FieldInfoBean field;

	private HTML emptyField = new HTML("<span style=\"color: darkblue\">(empty)</span>", true);
	private HTML searchableOnly = new HTML("<span style=\"color: darkblue\">(s)</span>", true);
	private HTML presentableOnly = new HTML("<span style=\"color: darkblue\">(p)</span>", true);
	private HTML spField = new HTML("<span style=\"color: darkblue\">(s/p)</span>", true);

	public FieldCell(FieldInfoBean field, boolean isReadOnly) {
		this.field = field;

		mainPanel.setSpacing(5);
		deleteFieldBtn.setStyleName("deleteButton");
		deleteFieldBtn.setTitle("Deletes the corresponding field");	
		fieldLabel.setText(field.getLabel());
		createFieldCellLabel(isReadOnly);
		initWidget(mainPanel);
	}

	public void setDeleteBtnClickHandler(ClickHandler handler) {
		deleteFieldBtn.addClickHandler(handler);
	}

	public FieldInfoBean getField() {
		return this.field;
	}

	public void updateField(FieldInfoBean updatedField, boolean isReadOnly) {
		field = updatedField;
		mainPanel.clear();
		createFieldCellLabel(isReadOnly);
	}

	private void createFieldCellLabel(boolean isReadOnly) {
		fieldLabel.setText(field.getLabel());
		mainPanel.add(fieldLabel);

		if (!isReadOnly) {
			if (field.getPresentableFields().isEmpty() && field.getSearchableFields().isEmpty())
				mainPanel.add(emptyField);
			else if (!field.getPresentableFields().isEmpty() && !field.getSearchableFields().isEmpty())
				mainPanel.add(spField);
			else if (!field.getSearchableFields().isEmpty())
				mainPanel.add(searchableOnly);
			else
				mainPanel.add(presentableOnly);

			mainPanel.add(deleteFieldBtn);
		}
	}
}
