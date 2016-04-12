package org.gcube.portlets.admin.software_upload_wizard.client.view.card;

import java.util.ArrayList;
import java.util.List;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.dispatch.shared.BatchAction;
import net.customware.gwt.dispatch.shared.BatchAction.OnException;
import net.customware.gwt.dispatch.shared.BatchResult;

import org.gcube.portlets.admin.software_upload_wizard.client.event.GoAheadEvent;
import org.gcube.portlets.admin.software_upload_wizard.client.event.GoBackEvent;
import org.gcube.portlets.admin.software_upload_wizard.client.util.Resources;
import org.gcube.portlets.admin.software_upload_wizard.client.util.Util;
import org.gcube.portlets.admin.software_upload_wizard.client.view.WizardWindow;
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.InfoPanel;
import org.gcube.portlets.admin.software_upload_wizard.shared.IOperationProgress;
import org.gcube.portlets.admin.software_upload_wizard.shared.SoftwareFileDetail;
import org.gcube.portlets.admin.software_upload_wizard.shared.filetypes.FileType;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.DeletePackageFiles;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.DeletePackageFilesResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetAllowedFileTypes;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetAllowedFileTypesResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageFiles;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageFilesResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetUploadProgress;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetUploadProgressResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.ValidateUploadedFiles;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.ValidateUploadedFilesResult;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.HideMode;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class FileUploadCard extends WizardCard {

	private static final String DEFAULT_TITLE = "Upload files";

	private final static String STEP_INFO_TEXT = Resources.INSTANCE
			.stepInfo_Upload().getText();

	private static final String UPLOAD_SERVLET = "FileUploadServlet";

	private DispatchAsync dispatchAsync = Util.getDispatcher();
	private WizardWindow window = Util.getWindow();
	private HandlerManager eventBus = Util.getEventBus();

	// Widgets
	private FileUploadField fileUploadField = new FileUploadField();
	private FileTypeComboBox fileTypeComboBox = new FileTypeComboBox();
	private ProgressBar uploadProgressBar = new ProgressBar();
	private Button uploadButton = new Button("Upload");
	private FileTable fileTable = new FileTable();
	private InfoPanel stepInfoPanel = new InfoPanel();
	private InfoPanel filesInfoPanel = new InfoPanel();

	private FormButtonBinding formButtonBinding = new FormButtonBinding(this);

	private FileUploadStatusUpdater statusUpdater = null;

	public String packageId;

	public FileUploadCard(String packageId, String additionalTitle) {
		super("Upload files - " + additionalTitle);
		this.packageId = packageId;

		buildUI();

		bind();
	}

	public FileUploadCard(String packageId) {
		super(DEFAULT_TITLE);
		this.packageId = packageId;

		buildUI();

		bind();
	}

	private void buildUI() {

		this.setButtonAlign(HorizontalAlignment.CENTER);

		fileUploadField.setFieldLabel("Select the file to import*");
		fileUploadField.setName("uploadFormElement");
		fileUploadField.setAllowBlank(false);

		// fix for issue with label
		uploadProgressBar.setHideMode(HideMode.VISIBILITY);

		// bind form submit button
		formButtonBinding.addButton(uploadButton);

		stepInfoPanel.setText(STEP_INFO_TEXT);

		// Assemble UI
		this.add(stepInfoPanel);
		this.add(fileTypeComboBox);
		this.add(fileUploadField);
		this.add(uploadProgressBar);
		this.add(uploadButton);
		this.add(fileTable);
		this.add(filesInfoPanel);

	}

	private void bind() {

		uploadButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				FileUploadCard.this.doChecksAndStartUpload();
			}
		});

		// fileTypeComboBox
		// .addSelectionChangedListener(new
		// SelectionChangedListener<FileUploadCard.FileTypeComboBox.FileTypeModelData>()
		// {
		//
		// @Override
		// public void selectionChanged(
		// SelectionChangedEvent<org.gcube.portlets.user.softwaremanagementwidget.client.view.card.FileUploadCard.FileTypeComboBox.FileTypeModelData>
		// se) {
		// if (fileTypeComboBox.isValid(true))
		// fileUploadField.enable();
		// else
		// fileUploadField.disable();
		// }
		// });

		fileTypeComboBox.addListener(Events.Valid, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				fileUploadField.unmask();
			}

		});

		fileTypeComboBox.addListener(Events.Invalid,
				new Listener<FieldEvent>() {

					@Override
					public void handleEvent(FieldEvent be) {
						fileUploadField.mask();
					}

				});

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup() {

		if (!fileTypeComboBox.isValid(true)) {
			fileUploadField.mask();
		}
		window.setBackButtonEnabled(true);
		loadData();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performNextStepLogic() {
		eventBus.fireEvent(new GoAheadEvent(this));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performBackStepLogic() {
		eventBus.fireEvent(new GoBackEvent(this));
	}

	/** Disable upload controls */
	private void disableUpload() {
		formButtonBinding.stopMonitoring();
		fileUploadField.disable();
		uploadButton.disable();
		fileTypeComboBox.disable();
	}

	private void enableUpload() {
		fileUploadField.enable();
		uploadButton.enable();
		fileTypeComboBox.enable();
		formButtonBinding.startMonitoring();
	}

	private void doChecksAndStartUpload() {
		String filename = fileUploadField.getValue();
		Log.debug("Checking file: " + filename);

		// Check filename extension
		FileType fileType = fileTypeComboBox.getValue().getFileType();
		if (!fileType.isFilenameExtensionValid(filename)) {
			String message = "Filename extension is not valid for the selected file type.";
			Log.error(message);
			MessageBox.alert("Invalid data", message, null);
			return;
		}

		// Check if the file was already uploaded
		if (fileTable.getFiles().contains(filename)) {
			String message = "A file with the same filename is already uploaded.";
			Log.error(message);
			MessageBox.alert("Invalid data", message, null);
			return;
		}
		;

		startUpload();
	}

	private void startUpload() {
		Log.debug("Starting upload");
		disableUpload();

		// Set request parameters
		FileUploadCard.this.setEncoding(Encoding.MULTIPART);
		FileUploadCard.this.setMethod(Method.POST);

		// Set the request URL
		StringBuilder actionUrl = new StringBuilder();
		actionUrl.append(GWT.getModuleBaseURL());
		actionUrl.append(UPLOAD_SERVLET + "?");
		actionUrl.append("fileType="
				+ fileTypeComboBox.getValue().getFileTypeName());
		actionUrl.append("&packageId=" + packageId);
		setAction(actionUrl.toString());

		uploadInProgress(0);

		FileUploadCard.this.submit();

		statusUpdater = new FileUploadStatusUpdater();
		statusUpdater.scheduleRepeating();
	}

	private void loadData() {
		Log.debug("Loading data...");
		this.mask();
		dispatchAsync.execute(new BatchAction(OnException.CONTINUE,
				new GetAllowedFileTypes(packageId), new GetPackageFiles(
						packageId), new ValidateUploadedFiles(packageId)),
				new AsyncCallback<BatchResult>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(BatchResult result) {
						ArrayList<FileType> fileTypes = result.getResult(0,
								GetAllowedFileTypesResult.class).getFileTypes();
						ArrayList<SoftwareFileDetail> files = result.getResult(
								1, GetPackageFilesResult.class).getFiles();
						boolean isValid = result.getResult(2,
								ValidateUploadedFilesResult.class).isValid();

						fileTypeComboBox.setFileTypes(fileTypes);

						fileTable.setFiles(files);

						String text = "<h3>Allowed file types</h3>" + "<ul>";
						for (FileType fileType : fileTypes) {
							text += "<li><i>" + fileType.getName() + "</i>: ";
							if (fileType.isMandatory())
								text += "mandatory, ";
							else
								text += "optional, ";
							if (fileType.allowsMulti())
								text += "multiple instances allowed, ";
							else
								text += "only one instance allowed, ";
							if (fileType.getAllowedExtensions().size() == 0)
								text += "allows any file extension.";
							else {
								text += "allowed extensions: ";
								for (String ext : fileType
										.getAllowedExtensions()) {
									text += "\'." + ext + "\'";
									if (fileType.getAllowedExtensions()
											.indexOf(ext) == fileType
											.getAllowedExtensions().size() - 1)
										text += ".";
									else
										text += ", ";
								}
							}
							text += "</li>";
						}
						text += "</ul>" + "</p>";
						filesInfoPanel.setText(text);
						

						window.setNextButtonEnabled(isValid);
						FileUploadCard.this.unmask();
						
						Log.debug("Data loaded");
					}
				});
	}

	private void loadUploadedFiles() {
		dispatchAsync.execute(new GetPackageFiles(packageId),
				new AsyncCallback<GetPackageFilesResult>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(GetPackageFilesResult result) {
						fileTable.setFiles(result.getFiles());
						Log.debug("Uploaded files list retrieved");
					}
				});
	}

	private void validateUploadedFiles() {
		Log.debug("Validating uploaded files");
		dispatchAsync.execute(new ValidateUploadedFiles(packageId),
				new AsyncCallback<ValidateUploadedFilesResult>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(ValidateUploadedFilesResult result) {
						boolean isValid = result.isValid();
						window.setNextButtonEnabled(isValid);
						Log.debug("Valid set of uploaded files: " + isValid);
					}
				});

	}

	private void uploadInProgress(double progress) {
		uploadProgressBar.updateProgress(progress, "Uploading...");
		Log.info("Upload in progress: " + progress);
	}

	private void uploadCompleted(double progress) {
		uploadProgressBar.updateProgress(progress, "Upload completed");
		enableUpload();
		loadUploadedFiles();
		validateUploadedFiles();

		Log.info("Upload completed");
	}

	private void uploadFailed(double progress, String details) {
		uploadProgressBar.updateProgress(progress, "Upload failed");
		Log.error("Upload failed: " + details);
		MessageBox.alert("Upload failed", details, null);

		enableUpload();
		loadUploadedFiles();
	}

	private class FileUploadStatusUpdater extends Timer {

		private boolean executing = false;
		private static final int STATUS_POLLING_DELAY = 300;

		public void scheduleRepeating() {
			super.scheduleRepeating(STATUS_POLLING_DELAY);
		}

		@Override
		public synchronized void run() {
			if (executing)
				return;
			executing = true;

			Log.trace("Requesting upload progress");

			dispatchAsync.execute(new GetUploadProgress(),
					new AsyncCallback<GetUploadProgressResult>() {

						@Override
						public void onFailure(Throwable caught) {
						}

						@Override
						public void onSuccess(GetUploadProgressResult result) {
							IOperationProgress progress = result.getProgress();
							Log.trace("retrieved OperationProgress:\t"
									+ progress.getElaboratedLenght() + "/"
									+ progress.getTotalLenght() + "\t"
									+ progress.getState());
							switch (progress.getState()) {
							case IN_PROGRESS:
								FileUploadCard.this.uploadInProgress(progress
										.getProgress());
								break;
							case COMPLETED: {
								statusUpdater.cancel();

								FileUploadCard.this.uploadCompleted(progress
										.getProgress());

								break;
							}
							case FAILED: {
								statusUpdater.cancel();

								FileUploadCard.this.uploadFailed(0,
										progress.getDetails());

								break;
							}
							}
							executing = false;
						}
					});
		}
	}

	private class FileTypeComboBox extends
			ComboBox<FileTypeComboBox.FileTypeModelData> {

		ListStore<FileTypeModelData> fileTypeStore = new ListStore<FileTypeModelData>();

		public FileTypeComboBox() {
			this.setName("fileType");
			this.setAllowBlank(false);
			this.setFieldLabel("File type*");
			this.setEmptyText("Select a file type");
			this.setValueField(FileTypeModelData.FILETYPE_CODE);
			this.setDisplayField(FileTypeModelData.FILETYPENAME_CODE);
			this.setTypeAhead(true);
			this.setTriggerAction(TriggerAction.ALL);

			this.setStore(fileTypeStore);
		}

		public void setFileTypes(ArrayList<FileType> fileTypes) {
			ListStore<FileTypeModelData> store = new ListStore<FileTypeModelData>();
			for (FileType fileType : fileTypes) {
				store.add(new FileTypeModelData(fileType));
			}
			setStore(store);
		}

		private class FileTypeModelData extends BaseModelData {

			/**
			 * 
			 */
			private static final long serialVersionUID = 4731825803585583310L;

			public static final String FILETYPENAME_CODE = "FILETYPE_NAME";
			public static final String FILETYPE_CODE = "FILETYPE";

			public FileTypeModelData(FileType filetype) {
				set(FILETYPENAME_CODE, filetype.getName());
				set(FILETYPE_CODE, filetype);
			}

			public String getFileTypeName() {
				return get(FILETYPENAME_CODE);
			}

			public FileType getFileType() {
				return get(FILETYPE_CODE);
			}

		}
	}

	private class FileTable extends ContentPanel {

		ListStore<SoftwareFileDetailModelData> softwareDetailStore = new ListStore<SoftwareFileDetailModelData>();

		private Grid<FileTable.SoftwareFileDetailModelData> grid;
		private Button deleteFileButton = new Button("Remove",
				AbstractImagePrototype.create(Resources.INSTANCE.deleteIcon()));
		private Button refreshButton = new Button("Refresh",
				AbstractImagePrototype.create(Resources.INSTANCE
						.tableRefreshIcon()));
		private ColumnModel cm;

		public FileTable() {
			this.setHeading("Uploaded files**");

			// initToolTip();

			// Create configs
			ArrayList<ColumnConfig> configs = new ArrayList<ColumnConfig>();
			CheckBoxSelectionModel<SoftwareFileDetailModelData> sm = new CheckBoxSelectionModel<SoftwareFileDetailModelData>();

			configs.add(sm.getColumn());

			ColumnConfig column = new ColumnConfig("filename", "Filename", 300);
			configs.add(column);

			column = new ColumnConfig("filetype", "File Type", 200);
			configs.add(column);

			cm = new ColumnModel(configs);

			// create grid
			grid = new Grid<SoftwareFileDetailModelData>(softwareDetailStore,
					cm);

			grid.setHeight(150);
			grid.setSelectionModel(sm);
			grid.setAutoExpandColumn("filename");
			grid.setAutoExpandColumn("filetype");
			grid.setColumnReordering(false);
			grid.setBorders(true);
			grid.addPlugin(sm);

			ToolBar bottomBar = new ToolBar();

			this.add(grid);
			bottomBar.add(deleteFileButton);
			bottomBar.add(refreshButton);
			this.setBottomComponent(bottomBar);

			bind();
		}

		private void initToolTip() {
			// Tooltip
			ToolTipConfig toolTip = new ToolTipConfig();
			toolTip.setTitle("Uploaded files table");
			toolTip.setText("The table shows succesfully uploaded files. User can refresh the table content or remove any file if needed.");
			toolTip.setMouseOffset(new int[] { 0, 0 });
			toolTip.setAnchor("left");
			this.setToolTip(toolTip);
		}

		private void bind() {
			deleteFileButton
					.addSelectionListener(new SelectionListener<ButtonEvent>() {

						@Override
						public void componentSelected(ButtonEvent ce) {
							deleteSelectedFiles();
							FileUploadCard.this.validateUploadedFiles();
						}
					});

			refreshButton
					.addSelectionListener(new SelectionListener<ButtonEvent>() {

						@Override
						public void componentSelected(ButtonEvent ce) {
							loadUploadedFiles();
						}
					});
		}

		private void deleteSelectedFiles() {
			GridSelectionModel<SoftwareFileDetailModelData> selection = grid
					.getSelectionModel();
			List<SoftwareFileDetailModelData> files = selection
					.getSelectedItems();
			ArrayList<String> filenames = new ArrayList<String>();
			for (SoftwareFileDetailModelData model : files) {
				filenames.add(model.getFileName());
			}
			dispatchAsync.execute(new DeletePackageFiles(packageId, filenames),
					new AsyncCallback<DeletePackageFilesResult>() {

						@Override
						public void onFailure(Throwable caught) {
							loadUploadedFiles();
						}

						@Override
						public void onSuccess(DeletePackageFilesResult result) {
							Log.info("Selected files were deleted.");
							loadUploadedFiles();
						}
					});

		}

		public ArrayList<String> getFiles() {
			ArrayList<String> files = new ArrayList<String>();
			for (SoftwareFileDetailModelData model : softwareDetailStore
					.getModels()) {
				files.add(model.getFileName());
			}
			return files;
		}

		public void setFiles(ArrayList<SoftwareFileDetail> files) {
			ArrayList<SoftwareFileDetailModelData> data = new ArrayList<SoftwareFileDetailModelData>();
			for (SoftwareFileDetail detail : files) {
				data.add(new SoftwareFileDetailModelData(detail.getFilename(),
						detail.getType()));
			}
			softwareDetailStore.removeAll();
			softwareDetailStore.add(data);
			softwareDetailStore.commitChanges();
			grid.getSelectionModel().setSelection(
					softwareDetailStore.getModels());
		}

		private class SoftwareFileDetailModelData extends BaseModelData {

			/**
			 * 
			 */
			private static final long serialVersionUID = -5037766085621516136L;

			public static final String FILENAME_CODE = "filename";
			public static final String FILETYPE_CODE = "filetype";

			public SoftwareFileDetailModelData(String filename, String filetype) {
				set(FILENAME_CODE, filename);
				set(FILETYPE_CODE, filetype);
			}

			public String getFileName() {
				return get(FILENAME_CODE);
			}

			public String getFileType() {
				return get(FILETYPE_CODE);
			}

		}

	}

	@Override
	public String getHelpContent() {
		return Resources.INSTANCE.stepHelp_Upload().getText();
	}

}
