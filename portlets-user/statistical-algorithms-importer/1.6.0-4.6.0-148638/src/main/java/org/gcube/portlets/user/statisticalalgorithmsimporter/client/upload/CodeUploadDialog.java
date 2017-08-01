package org.gcube.portlets.user.statisticalalgorithmsimporter.client.upload;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * Dialog for Code Upload
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class CodeUploadDialog extends Window {
	private static final String WIDTH = "650px";
	private static final String HEIGHT = "200px";

	public CodeUploadDialog(EventBus eventBus) {
		initWindow();
		CodeUploadPanel changeColumnTypePanel = new CodeUploadPanel(this, eventBus);
		add(changeColumnTypePanel);
	}

	protected void initWindow() {
		setModal(true);
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setBodyBorder(false);
		setResizable(false);
		setHeadingText("Import");
		//getHeader().setIcon(StatisticalRunnerResources.INSTANCE.upload16());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initTools() {
		super.initTools();

		closeBtn.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				close();
			}
		});

	}

	public void close() {
		hide();

	}

}
