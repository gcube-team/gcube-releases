package org.gcube.portlets.user.reportgenerator.client.uibinder;

import org.gcube.portlets.user.reportgenerator.client.Presenter.CommonCommands;
import org.gcube.portlets.user.reportgenerator.client.Presenter.Presenter;
import org.gcube.portlets.user.reportgenerator.client.uibinder.ExportOptions.ExportMode;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class OpenOptions extends Composite {

	private static OpenOptionsUiBinder uiBinder = GWT
			.create(OpenOptionsUiBinder.class);

	interface OpenOptionsUiBinder extends UiBinder<Widget, OpenOptions> {
	}
	enum OpenMode {OPEN_REPORT, OPEN_TEMPLATE, UPLOAD }

	@UiField HTML openReport;
	@UiField HTML openTemplate;
	@UiField HTML uploadReport;

	@UiField HTMLPanel myPanel;

	private Presenter p;

	public OpenOptions(Presenter p) {
		initWidget(uiBinder.createAndBindUi(this));
		this.p = p;
	}

	public HTMLPanel getMainPanel() {
		return myPanel;
	}
	@UiHandler("openReport")
	void onOpenReportClick(ClickEvent e) {
		GWT.log("editVME");
		doAction(OpenMode.OPEN_REPORT);
	}

	@UiHandler("openTemplate")
	void onOpenTemplateClick(ClickEvent e) {
		GWT.log("openTemplate");
		doAction(OpenMode.OPEN_TEMPLATE);
	}

//	@UiHandler("uploadReport")
//	void unUploadClick(ClickEvent e) {
//		doAction(OpenMode.UPLOAD);
//	}
	
	private void doAction(OpenMode mode) {
		CommonCommands cmd = new CommonCommands(p);
		switch (mode) {
		case OPEN_REPORT:
			cmd.openReport.execute();
			break;
		case OPEN_TEMPLATE:
			cmd.openTemplate.execute();
			break;
		case UPLOAD:
			//nothing to do, reminded to workspace
			break;
		default:
			break;
		}
		
	}
}
