package org.gcube.portlets.user.dataminermanager.client.experiments;

import org.gcube.portlets.user.dataminermanager.shared.data.computations.ComputationId;

import com.allen_sauer.gwt.log.client.Log;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.form.TextArea;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class EquivalentRequestDialog extends Dialog {
	private ComputationId computationId;

	public EquivalentRequestDialog(ComputationId computationId) {
		super();
		Log.debug("EquivalentRequestDialog: " + computationId);
		this.computationId = computationId;
		init();
		create();
	}

	private void init() {
		setModal(true);
		setClosable(true);
		setHeadingText("Equivalent Get Request");
		setBodyBorder(true);
		setHideOnButtonClick(true);
		setPredefinedButtons(PredefinedButton.CLOSE);
		getButtonBar().setPack(BoxLayoutPack.CENTER);
	}

	private void create() {
		TextArea equivalentRequestArea = new TextArea();
		equivalentRequestArea.setHeight(200);
		equivalentRequestArea.setWidth(640);
		equivalentRequestArea.setValue(computationId.getEquivalentRequest());
		equivalentRequestArea.setReadOnly(true);
		add(equivalentRequestArea);
	}

}
