package org.gcube.datacatalogue.grsf_manage_widget.client.view.subwidgets;

import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;


/**
 * A dynamic ControlGroup to add to a form
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class FormEntryModel extends Composite{

	private static FormEntryModelUiBinder uiBinder = GWT
			.create(FormEntryModelUiBinder.class);

	interface FormEntryModelUiBinder extends UiBinder<Widget, FormEntryModel> {
	}

	@UiField
	ControlLabel labelEntry;

	@UiField
	TextBox entryValue;

	public FormEntryModel(String label, String value) {
		initWidget(uiBinder.createAndBindUi(this));
		labelEntry.add(new HTML("<b> " + label.substring(0, 1).toUpperCase() + label.substring(1) + ":</b>"));
		entryValue.setText(value);
	}	

}
