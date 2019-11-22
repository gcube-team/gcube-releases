package org.gcube.portlets.user.dataminermanager.client.experiments;

import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationId;

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
		TextArea infoArea = new TextArea();
		infoArea.setHeight(200);
		infoArea.setWidth(640);
		infoArea.setValue(computationId.getEquivalentRequest());
		infoArea.setReadOnly(true);
		add(infoArea);
	}

}
