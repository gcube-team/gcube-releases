package org.gcube.portlets.user.gcubeloggedin.client.ui;

import org.gcube.portlets.user.gcubeloggedin.client.LoggedinService;
import org.gcube.portlets.user.gcubeloggedin.client.LoggedinServiceAsync;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class EditDescriptionModal extends Composite  {
	private final LoggedinServiceAsync loggedinService = GWT.create(LoggedinService.class);
	private static EditDescriptionModalUiBinder uiBinder = GWT
			.create(EditDescriptionModalUiBinder.class);

	interface EditDescriptionModalUiBinder extends
			UiBinder<Widget, EditDescriptionModal> {
	}

	private String currDescription;
	
	@UiField Modal modal;
	@UiField TextArea text2Edit;
	@UiField Button cancel;
	@UiField Button save;
	@UiField Icon loading;
	@UiField Paragraph loadingContainer;
	@UiField Paragraph loadingText;
	
	
	public EditDescriptionModal(String vreName, String currDescription) {
		initWidget(uiBinder.createAndBindUi(this));
		modal.setTitle(vreName + " description/abstract");
		text2Edit.setBlockLevel(true);
		text2Edit.setVisibleLines(10);	
		this.currDescription = currDescription;
	}

	public void show() {
		text2Edit.setText(transformDescription(currDescription));
		text2Edit.setVisible(true);
		loadingContainer.setVisible(false);
		save.setVisible(true);
		modal.show();
	}
	
	@UiHandler("cancel")
	void onCancelButton(ClickEvent e) {
		modal.hide();
	}
	

	@UiHandler("save")
	void onSaveButton(ClickEvent e) {
		final String vreDescription = text2Edit.getText();
		text2Edit.setText("");
		text2Edit.setVisible(false);
		loadingContainer.setVisible(true);
		doSave(vreDescription);
	}
	
	private void doSave(String toSave) {
		loggedinService.saveVREDescription(toSave, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				loadingText.setText("Ops, some problems occurred, please try again in a while or report the problem.");
			}

			@Override
			public void onSuccess(String result) {
				text2Edit.setText(result);
				loading.setIcon(IconType.CHECK_SIGN);
				loading.setSpin(false);
				loadingText.setText("Saving successful, please refresh the page to see your changes.");
				save.setVisible(false);				
			}
		});
	
	}
	
	private String transformDescription(String VREDescription) {
		String toReturn = VREDescription;
		// replace all the line breaks by <br/>
		toReturn = toReturn.replaceAll("<script","");
		toReturn = toReturn.replaceAll("</script","");
		return VREDescription;
	}	
}
