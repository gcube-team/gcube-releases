package org.gcube.portlets.widgets.dataminermanagerwidget.client.experiments;

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
public class LinkToMonitorComputationDialog extends Dialog {
	private ComputationId computationId;

	public LinkToMonitorComputationDialog(ComputationId computationId) {
		super();
		Log.debug("LinkToMonitorComputationDialog: " + computationId);
		this.computationId = computationId;
		init();
		create();
	}

	private void init() {
		setModal(true);
		setClosable(true);
		setHeadingText("Link To Monitor The Computation");
		setBodyBorder(true);
		setHideOnButtonClick(true);
		setPredefinedButtons(PredefinedButton.CLOSE);
		getButtonBar().setPack(BoxLayoutPack.CENTER);
	}

	private void create() {
		TextArea infoArea = new TextArea();
		infoArea.setHeight(200);
		infoArea.setWidth(640);
		infoArea.setValue(computationId.getUrlId());
		infoArea.setReadOnly(true);
		add(infoArea);
		
		
	}

}
