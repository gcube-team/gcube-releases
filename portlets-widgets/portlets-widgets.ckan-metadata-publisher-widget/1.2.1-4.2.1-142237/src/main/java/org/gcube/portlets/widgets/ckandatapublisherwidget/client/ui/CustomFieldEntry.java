package org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui;

import org.gcube.portlets.widgets.ckandatapublisherwidget.client.events.DeleteCustomFieldEvent;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.InputAddOn;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * A custom field entry that has two textboxes, one for the key value and the other for the value.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class CustomFieldEntry extends Composite {

	private static CustomFieldEntryUiBinder uiBinder = GWT
			.create(CustomFieldEntryUiBinder.class);

	interface CustomFieldEntryUiBinder extends
	UiBinder<Widget, CustomFieldEntry> {
	}

	@UiField InputAddOn keyFieldPrepend;
	@UiField InputAddOn valueFieldPrepend;
	@UiField Button removeCustomField;

	//inserted values
	private String value;
	private String key;
	private boolean isCustomCreatedByUser;

	// event bus
	private HandlerManager eventBus;

	public CustomFieldEntry(HandlerManager eventBus, String key, String value, boolean isCustomCreatedByUser) {
		initWidget(uiBinder.createAndBindUi(this));

		// save information
		this.eventBus = eventBus;
		this.value = value;
		this.key = key;
		this.isCustomCreatedByUser = isCustomCreatedByUser;

		// remove the first appendbox
		if(!isCustomCreatedByUser){
			this.valueFieldPrepend.removeFromParent();
			this.keyFieldPrepend.setPrependText(key + ":");
			((TextBox)this.keyFieldPrepend.getWidget(1)).setText(value);
		}
	}

	/**
	 * Retrieve the key value
	 * @return
	 */
	public String getKey(){

		if(isCustomCreatedByUser){

			return ((TextBox)this.keyFieldPrepend.getWidget(1)).getText();

		}

		return key;

	}

	/**
	 * Retrieve the value
	 * @return
	 */
	public String getValue(){

		if(isCustomCreatedByUser){

			return ((TextBox)this.valueFieldPrepend.getWidget(1)).getText();

		}

		return value;
	}

	@UiHandler("removeCustomField")
	void onRemoveCustomField(ClickEvent e){

		// fire event
		eventBus.fireEvent(new DeleteCustomFieldEvent(this));

	}

	/**
	 * Remove delete button
	 */
	public void freeze() {

		removeCustomField.setEnabled(false);

	}

}
