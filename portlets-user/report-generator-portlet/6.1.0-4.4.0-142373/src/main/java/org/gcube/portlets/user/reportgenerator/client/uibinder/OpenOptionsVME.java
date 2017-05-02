package org.gcube.portlets.user.reportgenerator.client.uibinder;

import org.gcube.portlets.user.reportgenerator.client.Presenter.CommonCommands;
import org.gcube.portlets.user.reportgenerator.client.Presenter.Presenter;
import org.gcube.portlets.user.reportgenerator.shared.VMETypeIdentifier;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class OpenOptionsVME extends Composite {

	private static OpenOptionsUiBinder uiBinder = GWT
			.create(OpenOptionsUiBinder.class);

	interface OpenOptionsUiBinder extends UiBinder<Widget, OpenOptionsVME> {
	}
	enum OpenMode {EDIT_VME, NEW_VME, DELETE_VME }

	@UiField HTML editVME;
	@UiField HTML createVME;
	@UiField HTML deleteVME;

	@UiField HTMLPanel myPanel;

	private Presenter p;

	public OpenOptionsVME(Presenter p) {
		initWidget(uiBinder.createAndBindUi(this));
		this.p = p;
	}

	public HTMLPanel getMainPanel() {
		return myPanel;
	}
	@UiHandler("editVME")
	void onEditVMEClick(ClickEvent e) {
		GWT.log("editVME");
		doAction(OpenMode.EDIT_VME);
	}

	@UiHandler("createVME")
	void onOpenTemplateClick(ClickEvent e) {
		doAction(OpenMode.NEW_VME);
	}

	@UiHandler("deleteVME")
	void unUploadClick(ClickEvent e) {
		doAction(OpenMode.DELETE_VME);
	}
	
	private void doAction(OpenMode mode) {
		CommonCommands cmd = new CommonCommands(p);
		switch (mode) {
		case EDIT_VME:
			p.showVMEImportDialog();
			break;
		case NEW_VME:
			p.importVMETemplate(VMETypeIdentifier.Vme);
			break;
		case DELETE_VME:
			p.showVMEDeleteDialog(VMETypeIdentifier.Vme);
			break;
		default:
			break;
		}
		
	}
}
