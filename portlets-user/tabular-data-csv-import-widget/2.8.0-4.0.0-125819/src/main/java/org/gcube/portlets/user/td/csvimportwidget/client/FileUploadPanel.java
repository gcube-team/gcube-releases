/**
 * 
 */
package org.gcube.portlets.user.td.csvimportwidget.client;

import org.gcube.portlets.user.td.csvimportwidget.client.progress.FileUploadProgressBarUpdater;
import org.gcube.portlets.user.td.csvimportwidget.client.progress.FileUploadProgressListener;
import org.gcube.portlets.user.td.csvimportwidget.client.progress.FileUploadProgressUpdater;
import org.gcube.portlets.user.td.gwtservice.shared.csv.CSVImportSession;
import org.gcube.portlets.user.td.wizardwidget.client.dataresource.ResourceBundle;
import org.gcube.portlets.user.td.wizardwidget.client.util.UtilsGXT3;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.sencha.gxt.core.client.Style.HideMode;
import com.sencha.gxt.core.client.util.Padding;
import com.sencha.gxt.widget.core.client.ProgressBar;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.ShowEvent;
import com.sencha.gxt.widget.core.client.event.ShowEvent.ShowHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FileUploadField;
import com.sencha.gxt.widget.core.client.form.FormPanel;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class FileUploadPanel extends FormPanel {

	private static final String UPLOAD_SERVLET = "LocalUploadServlet";

	private static final int STATUS_POLLING_DELAY = 1000;

	// private ResourceBundle res;
	private FileUploadField fUpField;
	private TextButton btnUpload;

	private FileUploadProgressUpdater progressUpdater;

	private ProgressBar uploadProgressBar;

	private TextButton btnCancel;

	private CSVImportWizardTDMessages msgs;

	public FileUploadPanel(ResourceBundle res, final CSVUploadFileCard card,
			final CSVImportSession importSession) {
		initMessages();
		setId("LocalUploadPanel");

		setLabelAlign(LabelAlign.TOP);
		getElement().setPadding(new Padding(5));

		addShowHandler(new ShowHandler() {

			public void onShow(ShowEvent event) {
				doLayout();

			}
		});

		setAction(GWT.getModuleBaseURL() + UPLOAD_SERVLET);
		setWidth("100%");

		setEncoding(Encoding.MULTIPART);
		setMethod(Method.POST);

		VerticalLayoutContainer content = new VerticalLayoutContainer();
		content.setWidth("100%");
		add(content);

		fUpField = new FileUploadField();
		fUpField.setName("uploadFormElement");
		fUpField.setWidth("100%");

		FieldLabel fUpFieldLabel = new FieldLabel(fUpField,
				msgs.fUpFieldLabel());

		content.add(fUpFieldLabel, new VerticalLayoutData(-2, -1));

		btnUpload = new TextButton(msgs.btnUploadText());
		content.add(btnUpload, new VerticalLayoutData(-1, -1));

		fUpField.addChangeHandler(new ChangeHandler() {

			public void onChange(ChangeEvent event) {
				btnUpload.setEnabled(fUpField.isValid());
				String path = fUpField.getValue();
				int punto = path.lastIndexOf(".");
				if (punto < 0) {
					punto = path.length();
				}
				int slash = path.lastIndexOf("/");
				int backslash = path.lastIndexOf("\\");
				String filename = "";
				if (slash > backslash) {
					if (slash != -1) {
						filename = path.substring(slash + 1, punto);
					}
				} else {
					if (backslash != -1) {
						filename = path.substring(backslash + 1, punto);
					}

				}
				importSession.setLocalFileName(filename);
			}
		});

		uploadProgressBar = new ProgressBar();
		uploadProgressBar.setHideMode(HideMode.VISIBILITY);
		uploadProgressBar.getElement().setPadding(new Padding(3, 0, 5, 0));
		content.add(uploadProgressBar, new VerticalLayoutData(-2, -1));
		uploadProgressBar.hide();

		btnCancel = new TextButton(msgs.btnCancelText());
		btnCancel.hide();
		content.add(btnCancel, new VerticalLayoutData(-1, -1));

		btnUpload.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.info("request upload");

				if (fUpField.getValue() == null
						|| fUpField.getValue().equals("")) {
					Log.info("fileUploadField is null or empty");
					UtilsGXT3.alert(msgs.csvFileMissingHead(),
							msgs.csvFileMissing());

					return;
				} else {
					Log.info("startUpload call");
					startUpload();
				}

			}
		});

		progressUpdater = new FileUploadProgressUpdater();
		progressUpdater.addListener(new FileUploadProgressBarUpdater(
				uploadProgressBar));

		progressUpdater.addListener(new FileUploadProgressListener() {

			public void operationUpdate(float elaborated) {
				//
			}

			public void operationInitializing() {

			}

			public void operationFailed(Throwable caught, String reason,
					String failureDetails) {
				card.showErrorAndHide(msgs.errorUploadingCSVFileHead(), reason,
						"", caught);
			}

			public void operationComplete() {
				card.setEnableNextButton(true);
				btnCancel.disable();

			}
		});

	}

	protected void initMessages() {
		msgs = GWT.create(CSVImportWizardTDMessages.class);
	}

	protected void startUpload() {
		disableUpload();

		StringBuilder actionUrl = new StringBuilder();
		actionUrl.append(GWT.getModuleBaseURL());
		actionUrl.append(UPLOAD_SERVLET);
		setAction(actionUrl.toString());
		Log.info("Start Upload action Url " + actionUrl.toString());
		submit();

		progressUpdater.scheduleRepeating(STATUS_POLLING_DELAY);
	}

	protected void disableUpload() {
		fUpField.disable();
		btnUpload.disable();

		uploadProgressBar.show();
		btnCancel.show();
	}

}
