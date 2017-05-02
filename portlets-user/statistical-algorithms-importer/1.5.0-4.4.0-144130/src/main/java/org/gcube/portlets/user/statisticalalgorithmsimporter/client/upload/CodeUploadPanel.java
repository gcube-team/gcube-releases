package org.gcube.portlets.user.statisticalalgorithmsimporter.client.upload;

import org.gcube.portal.clientcontext.client.GCubeClientContext;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.resource.StatAlgoImporterResources;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.upload.progress.FileUploadProgressBarUpdater;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.upload.progress.FileUploadProgressListener;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.upload.progress.FileUploadProgressUpdater;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.utils.UtilsGXT3;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.Constants;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Hidden;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ProgressBar;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.FileUploadField;
import com.sencha.gxt.widget.core.client.form.FormPanel;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class CodeUploadPanel extends FormPanel {

	

	private static final int STATUS_POLLING_DELAY = 1000;

	//private EventBus eventBus;
	private CodeUploadDialog parent;
	private FileUploadField fileUploadField;

	private TextButton btnUploadCode;
	private TextButton cancelBtn;

	//private FileUploadProgressUpdater progressUpdater;

	private ProgressBar uploadProgressBar;
	//private String fileName;

	private FileUploadProgressUpdater progressUpdater;

	public CodeUploadPanel(CodeUploadDialog parent, EventBus eventBus) {
		super();
		this.parent = parent;
		//this.eventBus = eventBus;
		init();
		create();

	}

	protected void init() {
		forceLayoutOnResize = true;
		setBorders(false);
		setResize(true);

	}

	private void create() {
		setId("CodeUploadPanel");
		String path = GWT.getModuleBaseURL()
				+ Constants.LOCAL_UPLOAD_SERVLET + "?"
				+ Constants.CURR_GROUP_ID + "="
				+ GCubeClientContext.getCurrentContextId();
		setAction(path);
		setWidth("100%");

		setEncoding(Encoding.MULTIPART);
		setMethod(Method.POST);

		btnUploadCode = new TextButton("Upload");
		btnUploadCode.setIcon(StatAlgoImporterResources.INSTANCE.upload24());
		btnUploadCode.setIconAlign(IconAlign.RIGHT);
		btnUploadCode.setToolTip("Upload Code");

		btnUploadCode.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.info("request upload");

				if (fileUploadField.getValue() == null
						|| fileUploadField.getValue().equals("")) {
					Log.info("fileUploadField is null or empty");
					UtilsGXT3.alert("Code file missing",
							"Please specify a Code file.");

					return;
				} else {
					Log.info("startUpload call");
					startUpload();
				}

			}

		});

		cancelBtn = new TextButton("Cancel");
		cancelBtn.setIcon(StatAlgoImporterResources.INSTANCE.cancel24());
		cancelBtn.setIconAlign(IconAlign.RIGHT);
		cancelBtn.setEnabled(false);

		fileUploadField = new FileUploadField();
		fileUploadField.setName(Constants.FILE_UPLOADED_FIELD);
		
		fileUploadField.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				Log.debug("File Changed", "Selected " + fileUploadField.getValue());
				String path = fileUploadField.getValue();
				int punto = path.lastIndexOf(".");
				if (punto < 0) {
					punto = path.length();
				}
				int slash = path.lastIndexOf("/");
				int backslash = path.lastIndexOf("\\");
				String fname = "";
				if (slash > backslash) {
					if (slash != -1) {
						fname = path.substring(slash + 1, punto);
					}
				} else {
					if (backslash != -1) {
						fname = path.substring(backslash + 1, punto);
					}

				}
				@SuppressWarnings("unused")
				String fileName = fname;
			}
		});
		fileUploadField.setAllowBlank(false);

		FieldLabel fileUploadFieldLabel = new FieldLabel(fileUploadField, "File");


		uploadProgressBar = new ProgressBar();
		//uploadProgressBar.setVisible(false);
		//uploadProgressBar.getElement().setPadding(new Padding(3, 0, 5, 0));
			

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
				UtilsGXT3.alert("Error uploading file", caught.getLocalizedMessage());
			}

			public void operationComplete() {
				//TODO now import is only on by workspace widget
				/*
				ImportCodeDescription desc=new ImportCodeDescription(ImportCodeType.FILE, fileName);
				ImportCodeEvent importEvent=new ImportCodeEvent(desc);
				eventBus.fireEvent(importEvent);
				*/
				parent.close();
			}
		});
		
		
		parent.addButton(btnUploadCode);
		parent.addButton(cancelBtn);
		parent.setButtonAlign(BoxLayoutPack.CENTER);
		
		Hidden currGroupId=new Hidden(Constants.CURR_GROUP_ID, GCubeClientContext.getCurrentContextId());
		
		VerticalLayoutContainer vlc = new VerticalLayoutContainer();
		vlc.add(currGroupId);
		vlc.add(fileUploadFieldLabel, new VerticalLayoutData(1, -1, new Margins(0)));
		vlc.add(uploadProgressBar, new VerticalLayoutData(1, -1, new Margins(5,0,0,0)));
		uploadProgressBar.setVisible(false);
		
		
		FieldSet fieldSet = new FieldSet();
		fieldSet.setHeadingHtml("<b>Upload Code</b>");
		fieldSet.setCollapsible(false);
		fieldSet.add(vlc);

		
		add(fieldSet, new MarginData(new Margins(5, 7, 2, 7)));
		
	}

	protected void startUpload() {
		disableUpload();
		submit();

		progressUpdater.scheduleRepeating(STATUS_POLLING_DELAY);
	}

	protected void disableUpload() {
		fileUploadField.disable();
		btnUploadCode.disable();
		uploadProgressBar.setVisible(true);;	
		cancelBtn.setEnabled(true);
		
		parent.forceLayout();
	}

		
	

	/*private void uploadCode() {
		if (fileName != null && !fileName.isEmpty()) {
			if (parent != null) {
				parent.close();
			}
			ImportCodeDescription importDesc = new ImportCodeDescription(
					ImportCodeType.FILE, fileName);
			ImportCodeEvent importCodeEvent = new ImportCodeEvent(importDesc);
			eventBus.fireEvent(importCodeEvent);

		}

	}*/

}
