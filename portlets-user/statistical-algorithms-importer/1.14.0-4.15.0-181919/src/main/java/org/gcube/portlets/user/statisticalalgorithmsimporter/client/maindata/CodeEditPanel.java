package org.gcube.portlets.user.statisticalalgorithmsimporter.client.maindata;

import java.util.ArrayList;

import org.gcube.portlets.user.statisticalalgorithmsimporter.client.CommonMessages;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.codeparser.CodeParser;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.event.NewSelectedRowsVariableEvent;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.event.NewCodeEvent;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.monitor.StatAlgoImporterMonitor;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.resource.StatAlgoImporterResources;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.rpc.StatAlgoImporterServiceAsync;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.type.SessionExpiredType;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.utils.UtilsGXT3;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.code.CodeData;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.code.CodeContentType;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterSessionExpiredException;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.IOType;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.InputOutputVariables;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.Project;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.ProjectSupportBashEdit;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.ProjectSupportBlackBox;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.ProjectSupportREdit;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.workspace.ItemDescription;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.cell.core.client.ButtonCell.ButtonScale;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;
import com.sencha.gxt.widget.core.client.toolbar.SeparatorToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import edu.ycp.cs.dh.acegwt.client.ace.AceEditor;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorCursorPosition;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorMode;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorTheme;
import edu.ycp.cs.dh.acegwt.client.ace.AceSelection;
import edu.ycp.cs.dh.acegwt.client.ace.AceSelectionListener;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class CodeEditPanel extends ContentPanel {

	private EventBus eventBus;
	private AceEditor editor;

	private TextButton btnSave;
	private TextButton btnAddInput;
	private TextButton btnAddOutput;
	private Project project;
	private TextField mainCodeField;
	private CodeEditMessages msgs;
	private CommonMessages msgsCommon;

	public CodeEditPanel(Project project, EventBus eventBus) {
		super();
		Log.debug("CodeEditPanel");
		this.eventBus = eventBus;
		this.project = project;
		this.msgs = GWT.create(CodeEditMessages.class);
		this.msgsCommon = GWT.create(CommonMessages.class);
		init();
		create();

	}

	private void init() {
		forceLayoutOnResize = true;
		setBodyBorder(false);
		setBorders(false);
		setHeaderVisible(false);
		setResize(true);

	}

	private void create() {
		btnSave = new TextButton(msgs.btnSaveText());
		btnSave.setIcon(StatAlgoImporterResources.INSTANCE.save16());
		btnSave.setScale(ButtonScale.SMALL);
		btnSave.setIconAlign(IconAlign.LEFT);
		btnSave.setToolTip(msgs.btnSaveToolTip());
		btnSave.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				saveCode();
			}

		});

		btnAddInput = new TextButton(msgs.btnAddInputText());
		btnAddInput.setIcon(StatAlgoImporterResources.INSTANCE.add16());
		btnAddInput.setScale(ButtonScale.SMALL);
		btnAddInput.setIconAlign(IconAlign.LEFT);
		btnAddInput.setToolTip(msgs.btnAddInputToolTip());
		btnAddInput.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				saveVariable(IOType.INPUT);
			}

		});
		btnAddInput.disable();

		btnAddOutput = new TextButton(msgs.btnAddOutputText());
		btnAddOutput.setIcon(StatAlgoImporterResources.INSTANCE.add16());
		btnAddOutput.setScale(ButtonScale.SMALL);
		btnAddOutput.setIconAlign(IconAlign.LEFT);
		btnAddOutput.setToolTip(msgs.btnAddOutputToolTip());
		btnAddOutput.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				saveVariable(IOType.OUTPUT);
			}

		});
		btnAddOutput.disable();

		mainCodeField = new TextField();
		mainCodeField.setEmptyText("");
		mainCodeField.setReadOnly(true);

		ToolBar toolBar = new ToolBar();
		toolBar.add(btnSave, new BoxLayoutData(new Margins(0)));
		toolBar.add(btnAddInput, new BoxLayoutData(new Margins(0)));
		toolBar.add(btnAddOutput, new BoxLayoutData(new Margins(0)));
		toolBar.add(new SeparatorToolItem());
		toolBar.add(new LabelToolItem(msgs.mainCodeFiledLabel()));
		toolBar.add(mainCodeField, new BoxLayoutData(new Margins(0)));

		editor = new AceEditor();

		VerticalLayoutContainer v = new VerticalLayoutContainer();
		v.add(toolBar, new VerticalLayoutData(1, -1, new Margins(0)));
		v.add(editor, new VerticalLayoutData(1, 1, new Margins(0)));

		add(v);
		editor.startEditor();
		editor.setShowPrintMargin(false);

		if (project != null && project.getMainCode() != null && project.getMainCode().getItemDescription() != null) {
			loadCode();
		}

		editor.getSelection().addSelectionListener(new AceSelectionListener() {

			@Override
			public void onChangeSelection(AceSelection selection) {
				if (selection != null && !selection.isEmpty()) {
					btnAddInput.enable();
					btnAddOutput.enable();
				} else {
					btnAddInput.disable();
					btnAddOutput.disable();
				}

			}
		});
		
		initCode();

	}
	
	public void codeUpdate(Project project) {
		this.project = project;
		initCode();
	}
	
	
	private void initCode(){
		if (project != null) {
			if (project.getProjectConfig() != null) {
				if (project.getProjectConfig().getProjectSupport() != null) {
					if (project.getProjectConfig().getProjectSupport() instanceof ProjectSupportREdit) {
						if (project.getMainCode() != null && project.getMainCode().getItemDescription() != null) {
							loadCode();
						}
					} else {
						if (project.getProjectConfig().getProjectSupport() instanceof ProjectSupportBashEdit) {
							ProjectSupportBashEdit projectSupportBashEdit = (ProjectSupportBashEdit) project
									.getProjectConfig().getProjectSupport();
							if (projectSupportBashEdit != null && projectSupportBashEdit.getBinaryItem() != null) {
								loadCode();
							}
						} else {
							if (project.getProjectConfig().getProjectSupport() instanceof ProjectSupportBlackBox) {
								ProjectSupportBlackBox projectSupportBlackBox = (ProjectSupportBlackBox) project
										.getProjectConfig().getProjectSupport();
									if (projectSupportBlackBox.getBinaryItem() != null
											&& projectSupportBlackBox.getBinaryItem().getName() != null) {
										mainCodeField.setValue(projectSupportBlackBox.getBinaryItem().getName());
									} else {
										mainCodeField.setValue("");
									}
									editor.setText("");
							} else {
								mainCodeField.setValue("");
								editor.setText("");
								
							}
						}
					}
				} else {
					mainCodeField.setValue("");
					editor.setText("");
				}
			}
		} else {
			mainCodeField.setValue("");
			editor.setText("");
		}
	}
	

	private void saveVariable(IOType ioType) {
		String parameter = getSelectedText();
		Log.debug("Save Variable: " + ioType + ", " + parameter);
		if (parameter == null) {
			Log.debug("No text selected");
			UtilsGXT3.alert(msgsCommon.attention(), msgs.attentionSelectParameterInTheCode());
		} else {
			createInputOutputVariable(parameter, ioType);
		}

	}

	private void createInputOutputVariable(String parameter, IOType ioType) {
		CodeParser codeParser = new CodeParser();
		InputOutputVariables selectedRowsVariable = codeParser.parse(parameter, ioType);
		if (selectedRowsVariable == null) {
			Log.debug("No valid selection, change selection and try again!");
			UtilsGXT3.alert(msgsCommon.attention(), msgs.attentionNoValidSelectedRow());
		} else {
			if (selectedRowsVariable.getDefaultValue() == null || selectedRowsVariable.getDefaultValue().isEmpty()
					|| selectedRowsVariable.getDefaultValue().trim().isEmpty()) {
				Log.debug("No valid selection, omitted a valid default value, change selection and try again!");
				UtilsGXT3.alert(msgsCommon.attention(), msgs.attentionNoValidSelectedRowOmittedDefaultValue());
			} else {
				NewSelectedRowsVariableEvent newSelectedRowsVariableEvent = new NewSelectedRowsVariableEvent(
						selectedRowsVariable);
				eventBus.fireEvent(newSelectedRowsVariableEvent);
				Log.debug("Fire: " + newSelectedRowsVariableEvent);
			}
		}
	}

	private void saveCode() {
		if (project != null) {
			if (project.getProjectConfig() != null) {
				if (project.getProjectConfig().getProjectSupport() != null) {
					if (project.getProjectConfig().getProjectSupport() instanceof ProjectSupportREdit) {
						if (project.getMainCode() != null && project.getMainCode().getItemDescription() != null) {

							saveCodeOnServer();
						} else {
							saveNewCode();
						}
					} else {
						if (project.getProjectConfig().getProjectSupport() instanceof ProjectSupportBashEdit) {
							ProjectSupportBashEdit projectSupportBashEdit = (ProjectSupportBashEdit) project
									.getProjectConfig().getProjectSupport();

							if (projectSupportBashEdit != null && projectSupportBashEdit.getBinaryItem() != null) {

								saveCodeOnServer();

							} else {
								saveNewCode();
							}
						} else {
							saveNewCode();
						}
					}
				} else {
					Log.error("Error in Project configuration: project support is null");
					UtilsGXT3.alert(msgsCommon.error(), "Error in Project configuration: project support is null");
				}
			} else {
				Log.error("Error in Project configuration: project configuration is null");
				UtilsGXT3.alert(msgsCommon.error(), "Error in Project configuration: project configuration is null");
			}
		} else {
			Log.error("Error in Project configuration: project is null");
			UtilsGXT3.alert(msgsCommon.error(), "Error in Project configuration: project is null");
		}
	}

	private void saveCodeOnServer() {
		final StatAlgoImporterMonitor monitor = new StatAlgoImporterMonitor();

		String code = editor.getText();
		StatAlgoImporterServiceAsync.INSTANCE.saveCode(code, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				monitor.hide();
				if (caught instanceof StatAlgoImporterSessionExpiredException) {
					eventBus.fireEvent(new SessionExpiredEvent(SessionExpiredType.EXPIREDONSERVER));
				} else {
					Log.error("Error on save code: " + caught.getLocalizedMessage());
					UtilsGXT3.alert(msgsCommon.error(), caught.getLocalizedMessage());
				}
				caught.printStackTrace();

			}

			@Override
			public void onSuccess(Void result) {
				monitor.hide();
				Log.debug("Code is saved!");
				UtilsGXT3.info(msgs.codeSavedHead(), msgs.codeSaved());
			}

		});
	}

	private void loadCode() {
		StatAlgoImporterServiceAsync.INSTANCE.getCode(new AsyncCallback<ArrayList<CodeData>>() {

			public void onFailure(Throwable caught) {
				if (caught instanceof StatAlgoImporterSessionExpiredException) {
					eventBus.fireEvent(new SessionExpiredEvent(SessionExpiredType.EXPIREDONSERVER));
				} else {
					Log.error("Error retrieving code: " + caught.getLocalizedMessage());
					UtilsGXT3.alert(msgsCommon.error(), caught.getLocalizedMessage());
				}
				caught.printStackTrace();

			}

			public void onSuccess(ArrayList<CodeData> result) {
				Log.debug("loaded " + result.size() + " code lines");
				String text = new String();
				if (result != null && result.size() > 0) {

					for (CodeData codeData : result) {
						// Log.debug("Read: " + codeData);
						text += codeData.getCodeLine() + "\r\n";
					}

				} else {

				}
				showCode(text);
				forceLayout();
			}

		});

	}

	private void showCode(String text) {
		if (project != null) {
			if (project.getProjectConfig() != null && project.getProjectConfig().getProjectSupport() != null) {
				if (project.getProjectConfig().getProjectSupport() instanceof ProjectSupportREdit) {
					if (project.getMainCode().getItemDescription().getName() != null) {
						mainCodeField.setValue(project.getMainCode().getItemDescription().getName());
					} else {
						mainCodeField.setValue("");
					}
					editor.setShowPrintMargin(false);
					editor.setMode(AceEditorMode.R);
					editor.setTheme(AceEditorTheme.ECLIPSE);
					editor.setText(text);
				} else {
					if (project.getProjectConfig().getProjectSupport() instanceof ProjectSupportBashEdit) {
						ProjectSupportBashEdit projectSupportBashEdit = (ProjectSupportBashEdit) project
								.getProjectConfig().getProjectSupport();
						if (projectSupportBashEdit.getBinaryItem() != null
								&& projectSupportBashEdit.getBinaryItem().getName() != null) {
							mainCodeField.setValue(projectSupportBashEdit.getBinaryItem().getName());
						} else {
							mainCodeField.setValue("");
						}

						editor.setShowPrintMargin(false);
						editor.setMode(AceEditorMode.SH);
						editor.setTheme(AceEditorTheme.ECLIPSE);
						editor.setText(text);
					} else {
						if (project.getProjectConfig().getProjectSupport() instanceof ProjectSupportBlackBox) {
							Log.debug("No editing enabled for BlackBox project");
							ProjectSupportBlackBox projectSupportBlackBox = (ProjectSupportBlackBox) project
									.getProjectConfig().getProjectSupport();
							if (projectSupportBlackBox.getBinaryItem() != null
									&& projectSupportBlackBox.getBinaryItem().getName() != null) {
								mainCodeField.setValue(projectSupportBlackBox.getBinaryItem().getName());
							} else {
								mainCodeField.setValue("");
							}
							editor.setShowPrintMargin(false);
							editor.setMode(AceEditorMode.SH);
							editor.setTheme(AceEditorTheme.ECLIPSE);
							editor.setText("");
						} else {
							Log.debug("No editing enabled");
							mainCodeField.setValue("");
							editor.setText("");
						}
					}
				}
			} else {
				Log.debug("No editing enabled");
				mainCodeField.setValue("");
				editor.setText("");

			}
		} else {
			Log.debug("No editing enabled");
			mainCodeField.setValue("");
			editor.setText("");
		}
	}

	


	private String getSelectedText() {
		String selectedText = null;

		AceSelection editorSelection = editor.getSelection();
		if (!editorSelection.isEmpty()) {
			AceEditorCursorPosition startPosition;
			AceEditorCursorPosition endPosition;
			if (editorSelection.isBackwards()) {
				startPosition = editorSelection.getSelectionLead();
				endPosition = editorSelection.getSelectionAnchor();
				editorSelection.select(startPosition.getRow(), startPosition.getColumn(), endPosition.getRow(),
						endPosition.getColumn());
			} else {
				startPosition = editorSelection.getSelectionAnchor();
				endPosition = editorSelection.getSelectionLead();

			}

			if (editorSelection.isMultiLine()) {
				String tempTest;
				for (int i = startPosition.getRow(); i < endPosition.getRow(); i++) {
					if (i == startPosition.getRow()) {
						tempTest = editor.getLine(i);
						tempTest.substring(startPosition.getColumn());
						selectedText = tempTest;
					} else {
						if (i == endPosition.getRow()) {
							tempTest = editor.getLine(i);
							tempTest.substring(0, endPosition.getColumn());
							selectedText += tempTest;
						} else {
							selectedText += editor.getLine(i);
						}
					}
				}
			} else {
				String tempTest;
				tempTest = editor.getLine(startPosition.getRow());
				selectedText = tempTest.substring(startPosition.getColumn(), endPosition.getColumn());
			}
		}

		Log.debug("Selected Text: " + selectedText);
		return selectedText;
	}

	private void saveNewCode() {
		final PromptMessageBox messageBox = new PromptMessageBox("Code", "File name:");
		// First option: Listening for the hide event and then figuring which
		// button was pressed.
		messageBox.addDialogHideHandler(new DialogHideHandler() {
			@Override
			public void onDialogHide(DialogHideEvent event) {
				if (event.getHideButton() == PredefinedButton.OK) {
				} else {
				}
			}
		});

		// Second option: Listen for a button click
		messageBox.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				String fileName = messageBox.getTextField().getCurrentValue();
				if (fileName != null && !fileName.isEmpty()) {
					saveNewCodeOnServer(fileName);
				} else {
					Log.debug("Attention invalid file name for Code!");
					UtilsGXT3.alert(msgsCommon.attention(), msgs.attentionInvalidFileNameForMainCode());
				}

			}
		});
		messageBox.show();
	}

	private void saveNewCodeOnServer(String fileName) {
		ItemDescription file = new ItemDescription();
		file.setName(fileName);
		String code = editor.getText();
		CodeContentType codeType;
		if (project != null) {
			if (project.getProjectConfig() != null) {
				if (project.getProjectConfig().getProjectSupport() != null) {
					if (project.getProjectConfig().getProjectSupport() instanceof ProjectSupportBashEdit) {
						codeType = CodeContentType.Binary;
					} else {
						if (project.getProjectConfig().getProjectSupport() instanceof ProjectSupportREdit) {
							codeType = CodeContentType.Main;
						} else {
							codeType = CodeContentType.Main;
						}
					}
				} else {
					codeType = CodeContentType.Main;
				}
			} else {
				codeType = CodeContentType.Main;
			}
		} else {
			codeType = CodeContentType.Main;
		}

		NewCodeEvent saveNewCodeEvent = new NewCodeEvent(codeType, file, code);
		eventBus.fireEvent(saveNewCodeEvent);
		Log.debug("Fired: " + saveNewCodeEvent);

	}

}
