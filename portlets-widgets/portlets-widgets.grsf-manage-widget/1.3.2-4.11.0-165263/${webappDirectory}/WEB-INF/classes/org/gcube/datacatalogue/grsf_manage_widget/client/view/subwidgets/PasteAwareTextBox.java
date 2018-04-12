package org.gcube.datacatalogue.grsf_manage_widget.client.view.subwidgets;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.Tooltip;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Event;

/**
 * A paste aware textbox widget.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class PasteAwareTextBox extends TextBox {

	private Button toBeEnabled;
	private Tooltip wrapperTip;
	private static final String REGEX_UUID = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

	public PasteAwareTextBox(Button b, Tooltip tip) {
		super();
		toBeEnabled = b;
		wrapperTip = tip;
		sinkEvents(Event.ONPASTE);
		sinkEvents(Event.ONCHANGE);
		sinkEvents(Event.ONKEYPRESS);
	}

	@Override
	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);
		switch (event.getTypeInt()) {
		case Event.ONPASTE:
			onEvent(getClipboardData(event));
			break;
		case Event.ONCHANGE:
		case Event.ONKEYPRESS:
			onEvent(this.getText());
			break;
		}

	}

	private void onEvent(String clipboardData) {
		GWT.log("Current text is:" + clipboardData);
		toBeEnabled.setEnabled(false);
		toBeEnabled.setText("Validate");
		toBeEnabled.setTitle("");
		toBeEnabled.setType(ButtonType.DEFAULT);
		wrapperTip.hide();
		if(clipboardData != null && !clipboardData.isEmpty()){
			final String currentText = clipboardData.trim();
			if(!currentText.matches(REGEX_UUID))
				return;
			else
				toBeEnabled.setEnabled(true);
		}

	}

	/**
	 * In case of PASTE event
	 * @param event
	 * @return
	 */
	private static native String getClipboardData(Event event) /*-{
        return event.clipboardData.getData('text/plain'); 
    }-*/;

}