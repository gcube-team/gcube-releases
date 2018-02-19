package org.gcube.portlets.user.socialprofile.client.ui;

import org.gcube.portlets.user.socialprofile.client.SocialService;
import org.gcube.portlets.user.socialprofile.client.SocialServiceAsync;

import com.github.gwtbootstrap.client.ui.AlertBlock;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class DisplaySummary extends Composite {

	private static DisplaySummaryUiBinder uiBinder = GWT
			.create(DisplaySummaryUiBinder.class);

	private final SocialServiceAsync socialService = GWT.create(SocialService.class);

	interface DisplaySummaryUiBinder extends UiBinder<Widget, DisplaySummary> {
	}

	@UiField HTML summary;
	@UiField TextArea summaryEditingArea;
	@UiField Button saveSummary;
	@UiField Button cancelEditSummary;
	@UiField AlertBlock alertBlock;

	public DisplaySummary() {
		initWidget(uiBinder.createAndBindUi(this));
		saveSummary.getElement().getStyle().setMarginRight(5, Unit.PX);
	}

	/**
	 * Set the summary text
	 * @param summaryText
	 */
	public void setSummary(String summaryText){
		summary.setHTML(summaryText);

		// convert back to text
		String text = fromEscapedHTMLToString(summaryText);
		summaryEditingArea.setText(text);
	}

	/**
	 * Hide the summary field and show the summaryEditingArea one
	 */
	public void enableEditing(){
		summary.setVisible(false);
		summaryEditingArea.setVisible(true);
		saveSummary.setVisible(true);
		cancelEditSummary.setVisible(true);
	}

	@UiHandler("saveSummary")
	void onSaveClick(ClickEvent e){

		// show alert block
		alertBlock.setText("Changing backgroud summary information please wait...");
		alertBlock.setType(AlertType.INFO);

		// disable save button
		saveSummary.setEnabled(false);

		socialService.saveProfessionalBackground(summaryEditingArea.getText(), new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {

				// save the new sanitizedHtml html as summary
				if(result != null){
					setSummary(result);
					summary.setVisible(true);
					summaryEditingArea.setVisible(false);
					saveSummary.setVisible(false);
					cancelEditSummary.setVisible(false);
					alertBlock.setText("Background Summary successfully updated");
					alertBlock.setType(AlertType.SUCCESS);
				}else{
					alertBlock.setText("Background Summary not updated sorry");
					alertBlock.setType(AlertType.ERROR);
				}

				alertBlock.setVisible(true);

			}

			@Override
			public void onFailure(Throwable caught) {

				summary.setVisible(true);
				summaryEditingArea.setVisible(false);
				saveSummary.setVisible(false);
				alertBlock.setText("Background Summary not updated sorry");
				alertBlock.setType(AlertType.ERROR);
				alertBlock.setVisible(true);
				cancelEditSummary.setVisible(false);

			}
		});

		// hide alert after a while
		Timer t = new Timer() {

			@Override
			public void run() {
				alertBlock.setVisible(false);
			}
		};
		t.schedule(2000);

		// enable save button
		saveSummary.setEnabled(true);
	}

	@UiHandler("cancelEditSummary")
	void onCancelClick(ClickEvent e){

		// reset changes and exit
		summary.setVisible(true);
		summaryEditingArea.setVisible(false);
		saveSummary.setVisible(false);
		cancelEditSummary.setVisible(false);
	}
	
	/**
	 * Convert back escaped html to text
	 * @param htmlEscaped
	 * @return
	 */
	private static String fromEscapedHTMLToString(String htmlEscaped){
		String descWithoutHTML = htmlEscaped;		
		descWithoutHTML = descWithoutHTML.replaceAll("&nbsp;&nbsp;","  ");
		descWithoutHTML = descWithoutHTML.replaceAll(" <br/> ","\r\n");
		descWithoutHTML = descWithoutHTML.replaceAll("&lt;","<").replaceAll("&gt;",">");
		descWithoutHTML = descWithoutHTML.replaceAll("&amp;","&");
		return descWithoutHTML;
	}
}
