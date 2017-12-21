package org.gcube.portlets.widgets.widgettour.client.extendedclasses;

import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;
import org.gcube.portlets.widgets.widgettour.client.TourManagerServices;
import org.gcube.portlets.widgets.widgettour.client.TourManagerServicesAsync;

import com.ait.toolkit.core.client.Function;
import com.ait.toolkit.hopscotch.client.HopScotch;
import com.ait.toolkit.hopscotch.client.Tour;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Extended version of the Tour class by Alain Ekambi.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class GCubeTour extends Tour {

	//Create a remote service proxy to talk to the server-side statistics service.
	private final TourManagerServicesAsync tourServices = GWT.create(TourManagerServices.class);

	// the dialogbox to show on exit
	private final GCubeDialog dialogBox = new GCubeDialog();

	// question to show
	private final String DEFAULT_QUESTION = "Would you like to see again this tour next time?";

	// other info
	private String portletId;
	private int versionNumber;

	// this instance
	private GCubeTour tourInstance;

	// as default, show next time
	protected boolean showNextTime = true;

	// buttons' labels
	private static final String okButtonLabel = "Yes, please";
	private static final String noButtonLabel = "No, I got it";

	/**
	 * Create a tour with a unique identifier. Please note that you have to increase your versionNumber
	 * if you want to show a new version of the tour to the user.
	 * @param id unique identifier for the tour
	 * @param portletId the identifier of the portlet
	 * @param versionNumber the version of this tour
	 * @param question the question (please note that buttons 'ok', 'no' will be shown)
	 * @param title the title of the dialog-box
	 */
	public GCubeTour(String id, final String portletId, final int versionNumber, String question, String title) {

		// call father's constructor
		super(id);

		// save info
		this.portletId = portletId;
		this.versionNumber = versionNumber;

		// save this instance
		tourInstance = this;

		// build the dialogbox
		buildDialog(question, title);

		// set this dialog to be shown on exit
		showDialogOnExit();

		// on done, tell the tour-manager to not show again the tour to the user
		callTourManagerOnDone();

	}

	/**
	 * Start this tour if possible (it won't start if the user said that he/she does not want to see it again).
	 */
	public void startTour() {

		tourServices.setShowNextTime(portletId, versionNumber, showNextTime, new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {

				// start the tour if possible
				if(result)
					HopScotch.startTour(tourInstance);
			}

			@Override
			public void onFailure(Throwable caught) {

			}
		});

	}

	/**
	 * Build the dialogbox
	 */
	private void buildDialog(String question, String title) {

		dialogBox.setText(title);
		dialogBox.setAnimationEnabled(true);

		// buttons panel
		HorizontalPanel dialogHPanel = new HorizontalPanel();
		dialogHPanel.setWidth("100%");
		Button noButton = new Button(noButtonLabel);
		Button okButton = new Button(okButtonLabel);

		dialogHPanel.add(noButton);
		dialogHPanel.add(okButton);

		// set buttons' style
		noButton.setStyleName("button prev");
		okButton.setStyleName("button next");

		dialogHPanel.setCellHorizontalAlignment(noButton, HasHorizontalAlignment.ALIGN_LEFT);
		dialogHPanel.setCellHorizontalAlignment(okButton, HasHorizontalAlignment.ALIGN_RIGHT);

		// main panel
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.setWidth("100%");
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		String finalQuestion = (question == null? DEFAULT_QUESTION : question);
		dialogVPanel.add(new HTML(finalQuestion));

		// buttons' handlers
		okButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				showNextTime = true;
				dialogBox.hide();

			}
		});

		noButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				dialogBox.hide();

				showNextTime = false;

				// invoke the tour manager
				tourServices.setShowNextTime(portletId, versionNumber, showNextTime, new AsyncCallback<Boolean>() {

					@Override
					public void onSuccess(Boolean result) {

					}

					@Override
					public void onFailure(Throwable caught) {

					}
				});
			}
		});

		// add the horizontal panel to the vertical one
		dialogVPanel.add(dialogHPanel);

		// Set the contents of the Widget and show it
		dialogBox.setWidget(dialogVPanel);

		// set its style
		dialogBox.setStyleName("gcube_DialogBox_tour");

	}

	/**
	 * Show this dialog on exit.
	 * @param dialog the dialog to show
	 */
	private void showDialogOnExit(){

		onClose(new Function() {

			@Override
			public void execute() {
				dialogBox.center();

				if(!dialogBox.isVisible())
					dialogBox.show();
			}
		});
	}

	/**
	 * When the user finishes the tour (i.e. he is at the last step), show the dialog box
	 */
	private void callTourManagerOnDone() {
		onEnd(new Function() {

			@Override
			public void execute() {
				dialogBox.center();
				dialogBox.show();
			}
		});
	}
}
