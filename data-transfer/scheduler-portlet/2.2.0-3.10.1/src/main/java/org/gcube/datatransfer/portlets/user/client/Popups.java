package org.gcube.datatransfer.portlets.user.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.datatransfer.portlets.user.client.obj.AgentStat;
import org.gcube.datatransfer.portlets.user.client.obj.Outcomes;
import org.gcube.datatransfer.portlets.user.client.obj.TreeOutcomes;
import org.gcube.datatransfer.portlets.user.client.obj.Uri;
import org.gcube.datatransfer.portlets.user.shared.obj.BaseDto;
import org.gcube.datatransfer.portlets.user.shared.obj.FolderDto;
import org.gcube.portlets.widgets.guidedtour.client.GCUBEGuidedTour;
import org.gcube.portlets.widgets.guidedtour.client.steps.GCUBETemplate1Text1Image;
import org.gcube.portlets.widgets.guidedtour.client.steps.GCUBETemplate2Text2Image;
import org.gcube.portlets.widgets.guidedtour.client.steps.TourStep;
import org.gcube.portlets.widgets.guidedtour.client.types.ThemeColor;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.CollapseEvent;
import com.sencha.gxt.widget.core.client.event.CollapseEvent.CollapseHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.RowDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.RowDoubleClickEvent.RowDoubleClickHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.PasswordField;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.CellSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.grid.RowExpander;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeNode;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class Popups extends Common{

	/* ------------------------------------------------------------- */
	/* ----------------------- POPUP WIDGETS ----------------------- */

	/*
	 * asPopUpAgents input: Nothing -- returns: Widget Creates a pop up panel so
	 * that user can choose between several agents. The client can also select
	 * to see the agent statistcs
	 */
	public Widget asPopUpAgents(final FolderToRetrieve folderToRetrieve) {
		FramedPanel panel = new FramedPanel();
		panel.setHeadingText("Select Agent");
		panel.setWidth(500);
		panel.setBodyStyle("background: none; padding: 5px");

		VerticalLayoutContainer p = new VerticalLayoutContainer();
		panel.setWidget(p);

		// Add a list box with multiple selection enabled
		if (multiBoxAgents == null) {
			multiBoxAgents = new ListBox(false);
			multiBoxAgents.setWidth("460px");
			multiBoxAgents.setVisibleItemCount(5);
		}
		VerticalPanel multiBoxPanel = new VerticalPanel();
		multiBoxPanel.setSpacing(4);
		multiBoxPanel.add(multiBoxAgents);

		VerticalLayoutContainer con = new VerticalLayoutContainer();
		con.setBorders(true);
		con.add(multiBoxPanel, new VerticalLayoutData(1, 1));

		panel.setWidget(con);
		// cp.setButtonAlign(BoxLayoutPack.CENTER);
		panel.setButtonAlign(BoxLayoutPack.START);

		TextButton cancelButton = new TextButton("Cancel");
		cancelButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				dialogBoxGen.hide();
				functions.setDialogBoxForMessages();
				destCombo.setValue(lastDestComboValue);
			}
		});
		TextButton okButton = new TextButton("OK");
		okButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				if (comboAgent.getCurrentValue() == null
						&& multiBoxAgents.getSelectedIndex() == -1) {
					Info.display("Warning", "You have not selected Agent ! ");
				} else {
					dialogBoxGen.hide();
					functions.setDialogBoxForMessages();
					lastDestComboValue = destCombo.getCurrentValue();
					String agentName = multiBoxAgents.getValue(multiBoxAgents
							.getSelectedIndex());
			//		Info.display("Selected", "You selected " + agentName);
					agentHostname = agentName;
					agentIsSelectedFromStatsPanel=false;
					//setting tool tip
					if(agentStats!=null){
						if(agentStatsTooltip==null)agentStatsTooltip=functions.createAnchor("");
						agentStatsTooltip.setBodyText("Selected agent:"+agentHostname);
						agentStats.setToolTipConfig(agentStatsTooltip);
					}

					comboAgent.setValue(agentName);
					if(folderToRetrieve.equals(FolderToRetrieve.NONE)){
						portlet.redrawEast();
					}
					else if(folderToRetrieve.equals(FolderToRetrieve.MongoDBStorageFolder)){
						rpCalls.getMongoDBFolderDest("/");
					}
					else if(folderToRetrieve.equals(FolderToRetrieve.AgentDestFolder)){
						String[] agentsArray = stringOfAgents.split("\n");
						for (String tmp : agentsArray) {
							// tmp contains: id--name--hostName--port
							String[] partsOfInfo = tmp.split("--");
							if (partsOfInfo[2].compareTo(agentName) == 0) {
								selectedAgentDestinationPort = partsOfInfo[3];
							}
						}
						selectedAgentDestination = agentName;					
						rpCalls.getAgentFolderDest("");
					}		
					else if(folderToRetrieve.equals(FolderToRetrieve.DataStorageFolder)){
						rpCalls.getDatastorageFolder(selectedDataStorageId, "./");
					}								
				}
			}
		});
		TextButton refreshButton = new TextButton("Refresh");
		refreshButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				// loading items
				rpCalls.getAgents();
			}
		});
		TextButton statsButton = new TextButton("View Statistics");
		statsButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				// loading items
				dialogBoxGen.hide();
				functions.setDialogBoxForMessages();
				//show statistics
				dialogBoxGen = functions.createDialogBox(asPopUpAgentStats());
				dialogBoxGen.center();
				focusTimer.schedule(200);
			}
		});

		panel.addButton(cancelButton);
		panel.addButton(refreshButton);
		panel.addButton(statsButton);
		panel.addButton(okButton);

		// adding items for the first time
		if (multiBoxAgents.getItemCount() < 1) {
			// loading items
			rpCalls.getAgents();
		}

		// key handlers -------------
		foc = new FocusPanel();
		foc.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					dialogBoxGen.hide();
					functions.setDialogBoxForMessages();
					destCombo.setValue(lastDestComboValue);
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					if (comboAgent.getCurrentValue() == null
							&& multiBoxAgents.getSelectedIndex() == -1) {
						Info.display("Warning",
								"You have not selected Agent ! ");
					} else {
						dialogBoxGen.hide();
						functions.setDialogBoxForMessages();
						lastDestComboValue = destCombo.getCurrentValue();
						String agentName = multiBoxAgents
								.getValue(multiBoxAgents.getSelectedIndex());
				//		Info.display("Selected", "You selected " + agentName);
						agentHostname = agentName;
						agentIsSelectedFromStatsPanel=false;
						//setting tool tip
						if(agentStats!=null){
							if(agentStatsTooltip==null)agentStatsTooltip=functions.createAnchor("");
							agentStatsTooltip.setBodyText("Selected agent:"+agentHostname);
							agentStats.setToolTipConfig(agentStatsTooltip);
						}

						comboAgent.setValue(agentName);
						if(folderToRetrieve.equals(FolderToRetrieve.NONE)){
							portlet.redrawEast();
						}
						else if(folderToRetrieve.equals(FolderToRetrieve.MongoDBStorageFolder)){
							rpCalls.getMongoDBFolderDest("/");
						}
						else if(folderToRetrieve.equals(FolderToRetrieve.AgentDestFolder)){
							String[] agentsArray = stringOfAgents.split("\n");
							for (String tmp : agentsArray) {
								// tmp contains: id--name--hostName--port
								String[] partsOfInfo = tmp.split("--");
								if (partsOfInfo[2].compareTo(agentName) == 0) {
									selectedAgentDestinationPort = partsOfInfo[3];
								}
							}
							selectedAgentDestination = agentName;					
							rpCalls.getAgentFolderDest("");
						}		
						else if(folderToRetrieve.equals(FolderToRetrieve.DataStorageFolder)){
							rpCalls.getDatastorageFolder(selectedDataStorageId, "./");
						}		
					}
				}
			}
		});
		foc.add(panel);
		// --------------
		return foc;
	}

	/*
	 * asPopUpMongoDBStorage input: Nothing -- returns: Widget Creates a pop up
	 * panel so that user can fill all the appropriate information about Storage
	 * Manager
	 */
	public Widget asPopUpMongoDBStorage() {
		FramedPanel panel = new FramedPanel();
		panel.setHeadingText("MongoDBStorage Details");
		panel.setWidth(300);
		panel.setBodyStyle("background: none; padding: 5px");
		VerticalLayoutContainer p = new VerticalLayoutContainer();
		// fields:
		// TextFields smServiceName,smServiceClass,smOwner
		// ComboBoxes smAccessType;
		if (smServiceName == null) {
			smServiceName = new TextField();
			smServiceName.setAllowBlank(false);
			
		}
		p.add(new FieldLabel(smServiceName, "Service Name"),
				new VerticalLayoutData(1, -1));

		if (smServiceClass == null) {
			smServiceClass = new TextField();
			smServiceClass.setAllowBlank(false);
			
		}
		p.add(new FieldLabel(smServiceClass, "Service Class"),
				new VerticalLayoutData(1, -1));

		if (smOwner == null) {
			smOwner = new TextField();
			smOwner.setAllowBlank(false);
			
		}
		p.add(new FieldLabel(smOwner, "Owner"), new VerticalLayoutData(1, -1));

		if (smAccessType == null) {
			smAccessType = new SimpleComboBox<String>(
					new LabelProvider<String>() {
						public String getLabel(String item) {
							return item.toString().substring(0, 1)
									+ item.toString().substring(1)
									.toLowerCase();
						}
					});
			smAccessType.setTriggerAction(TriggerAction.ALL);
			smAccessType.setEditable(false);
			smAccessType.add("SHARED");
			smAccessType.add("PUBLIC");
			smAccessType.add("PRIVATE");
			// Add a handler to change the data source
			smAccessType.addCollapseHandler(new CollapseHandler() {
				public void onCollapse(CollapseEvent event) {
					// set the same values in the main form
					if (smAccessType.getCurrentValue() == null)
						return;

					String v = smAccessType.getCurrentValue() == null ? "nothing"
							: smAccessType.getCurrentValue();
					//Info.display("Selected", "You selected " + v);
				}
			});

			smAccessType.setAllowBlank(true);
			smAccessType.setForceSelection(true);
		}
		p.add(new FieldLabel(smAccessType, "Access Type"),
				new VerticalLayoutData(1, -1));

		//check box for default values
		defaultValuesStorage = new CheckBox();
		defaultValuesStorage.setBoxLabel("");
		defaultValuesStorage.setValue(false);
		defaultValuesStorage.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				boolean checked = event.getValue();
				if(checked){
					smServiceName.setValue("scheduler-portlet");
					smServiceClass.setValue("data-transfer");
					smOwner.setValue(ResourceName.getCurrentValue());
					smAccessType.setValue("SHARED");
				//	Info.display("MongoDBStorage", "Default parameters");
				}
				else{
					smServiceName.setValue("");
					smServiceClass.setValue("");
					smOwner.setValue("");
					smAccessType.setValue(null);
				}
			}
		});		
		p.add(new FieldLabel(defaultValuesStorage, "Default parameters"));

		// ************** buttons **************

		TextButton cancelButton = new TextButton("Cancel");
		cancelButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				dialogBoxGen.hide();
				functions.setDialogBoxForMessages();
				destCombo.setValue(lastDestComboValue);
			}
		});
		TextButton nextButton = new TextButton("Next");
		nextButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				if (functions.checkMongoDBFields())
					return;
				dialogBoxGen.hide();
				functions.setDialogBoxForMessages();
				if (combo1==null || combo1.getCurrentValue()==null ||
						!combo1.isAgentSource()
						|| selectedAgentSource == null) {
					//selection of agent should be transparent based on statistics
					functions.transparentSelectionOfAgent(FolderToRetrieve.MongoDBStorageFolder);
					//changed ...
					//dialogBoxGen = functions.createDialogBox(asPopUpAgents(FolderToRetrieve.MongoDBStorageFolder));
					//dialogBoxGen.center();
					//focusTimer.schedule(200);
				} else {
					rpCalls.getMongoDBFolderDest("/");
				//	Info.display("Selected", "You selected "
				//			+ selectedAgentSource);
				}
			}
		});
		panel.setWidget(p);
		panel.setButtonAlign(BoxLayoutPack.START);
		panel.addButton(cancelButton);
		panel.addButton(nextButton);

		// key handlers -------------
		foc = new FocusPanel();
		foc.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					dialogBoxGen.hide();
					functions.setDialogBoxForMessages();
					destCombo.setValue(lastDestComboValue);
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					if (functions.checkMongoDBFields())
						return;
					dialogBoxGen.hide();
					functions.setDialogBoxForMessages();
					if (combo1==null || combo1.getCurrentValue()==null ||
							!combo1.isAgentSource()
							|| selectedAgentSource == null) {
						//selection of agent should be transparent based on statistics
						functions.transparentSelectionOfAgent(FolderToRetrieve.MongoDBStorageFolder);
						//changed ...
						//dialogBoxGen = functions.createDialogBox(asPopUpAgents(FolderToRetrieve.MongoDBStorageFolder));
						//dialogBoxGen.center();
						//focusTimer.schedule(200);
					} else {
						rpCalls.getMongoDBFolderDest("/");
					//	Info.display("Selected", "You selected "
					//			+ selectedAgentSource);
					}
				}
			}
		});
		foc.add(panel);
		// --------------
		return foc;
	}


	/*
	 * asPopUpMongoDBSource input: Nothing -- returns: Widget Creates a pop up
	 * panel so that user can fill all the appropriate information about Storage
	 * Manager for browsing the source
	 */
	public Widget asPopUpMongoDBSource() {
		FramedPanel panel = new FramedPanel();
		panel.setHeadingText("Source Type MongoDB - Details:");
		panel.setWidth(300);
		panel.setBodyStyle("background: none; padding: 5px");
		VerticalLayoutContainer p = new VerticalLayoutContainer();
		// fields:
		// TextFields smServiceName,smServiceClass,smOwner
		// ComboBoxes smAccessType;
		if (smServiceNameSource == null) {
			smServiceNameSource = new TextField();
			smServiceNameSource.setAllowBlank(false);
			
		}
		p.add(new FieldLabel(smServiceNameSource, "Service Name"),
				new VerticalLayoutData(1, -1));

		if (smServiceClassSource == null) {
			smServiceClassSource = new TextField();
			smServiceClassSource.setAllowBlank(false);
			
		}
		p.add(new FieldLabel(smServiceClassSource, "Service Class"),
				new VerticalLayoutData(1, -1));

		if (smOwnerSource == null) {
			smOwnerSource = new TextField();
			smOwnerSource.setAllowBlank(false);
			
		}
		p.add(new FieldLabel(smOwnerSource, "Owner"), new VerticalLayoutData(1,
				-1));

		if (smAccessTypeSource == null) {
			smAccessTypeSource = new SimpleComboBox<String>(
					new LabelProvider<String>() {
						public String getLabel(String item) {
							return item.toString().substring(0, 1)
									+ item.toString().substring(1)
									.toLowerCase();
						}
					});
			smAccessTypeSource.setTriggerAction(TriggerAction.ALL);
			smAccessTypeSource.setEditable(false);
			smAccessTypeSource.add("SHARED");
			smAccessTypeSource.add("PUBLIC");
			smAccessTypeSource.add("PRIVATE");
			// Add a handler to change the data source
			smAccessTypeSource.addCollapseHandler(new CollapseHandler() {
				public void onCollapse(CollapseEvent event) {
					// set the same values in the main form
					if (smAccessTypeSource.getCurrentValue() == null)
						return;

					String v = smAccessTypeSource.getCurrentValue() == null ? "nothing"
							: smAccessTypeSource.getCurrentValue();
			//		Info.display("Selected", "You selected " + v);
				}
			});

			smAccessTypeSource.setAllowBlank(true);
			smAccessTypeSource.setForceSelection(true);
		}
		p.add(new FieldLabel(smAccessTypeSource, "Access Type"),
				new VerticalLayoutData(1, -1));

		//check box for default values
		defaultValuesSource = new CheckBox();
		defaultValuesSource.setBoxLabel("");
		defaultValuesSource.setValue(false);
		defaultValuesSource.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				boolean checked = event.getValue();
				if(checked){
					smServiceNameSource.setValue("scheduler-portlet");
					smServiceClassSource.setValue("data-transfer");
					smOwnerSource.setValue(ResourceName.getCurrentValue());
					smAccessTypeSource.setValue("SHARED");
				//	Info.display("MongoDBSource", "Default parameters");
				}
				else{
					smServiceNameSource.setValue("");
					smServiceClassSource.setValue("");
					smOwnerSource.setValue("");
					smAccessTypeSource.setValue(null);
				}
			}
		});		
		p.add(new FieldLabel(defaultValuesSource, "Default parameters"));

		// ************** buttons **************

		TextButton cancelButton = new TextButton("Cancel");
		cancelButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				dialogBoxGen.hide();
				functions.setDialogBoxForMessages();
				combo1.setValue(lastCombo1Value);
			}
		});

		TextButton nextButton = new TextButton("Next");
		nextButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				if (functions.checkMongoDBSourceFields())
					return;
				// TO DO
				// call service get source folderSource (root)..
				dialogBoxGen.hide();
				functions.setDialogBoxForMessages();
				rpCalls.getMongoDBFolder("/");
			}
		});
		panel.setWidget(p);
		panel.setButtonAlign(BoxLayoutPack.START);
		panel.addButton(cancelButton);
		panel.addButton(nextButton);

		// key handlers -------------
		foc = new FocusPanel();
		foc.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					dialogBoxGen.hide();
					functions.setDialogBoxForMessages();
					combo1.setValue(lastCombo1Value);
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					if (functions.checkMongoDBSourceFields())
						return;
					// TO DO
					// call service get source folderSource (root) ..
					dialogBoxGen.hide();
					functions.setDialogBoxForMessages();
					rpCalls.getMongoDBFolder("/");
				}
			}
		});
		foc.add(panel);
		// --------------
		return foc;
	}

	/*
	 * asPopUpAuthenticationWorkspace input: Nothing -- returns: Widget Creates
	 * a pop up panel so that user can fill the authentication details for
	 * transferring files from workspace
	 */
	public Widget asPopUpAuthenticationWorkspace() {
		FramedPanel panel = new FramedPanel();
		panel.setHeadingText("Workspace Authentication");
		panel.setWidth(300);
		panel.setBodyStyle("background: none; padding: 5px");
		VerticalLayoutContainer p = new VerticalLayoutContainer();
		// fields:
		// TextFields smServiceName,smServiceClass,smOwner
		// ComboBoxes smAccessType;
		p.add(new Label(" "), new VerticalLayoutData(1, -1));

		if (passWorkspace == null) {
			passWorkspace = new PasswordField();
			passWorkspace.setAllowBlank(false);
		}
		p.add(new FieldLabel(passWorkspace, "Password"),
				new VerticalLayoutData(1, -1));
		passWorkspace.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					dialogBoxGen.hide();
					functions.setDialogBoxForMessages();
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					if (passWorkspace.getCurrentValue() == null) {
						functions.printMsgInDialogBox("You must give your password! ");
						return;
					}

					dialogBoxGen.hide();
					functions.setDialogBoxForMessages();

					commands.setValue("schedule");
					if (functions.checkFields())
						return;

					if (toBeTransferredStore == null) {
						Info.display("","You have not added any files for transfer");
						return;
					} else if (toBeTransferredStore.getAll() == null) {
						Info.display("","You have not added any files for transfer");
						return;
					} else if (toBeTransferredStore.getAll().size() < 1) {
						Info.display("","You have not added any files for transfer");
						return;
					}
					rpCalls.schedule();
				}
			}
		});

		// ************** buttons **************
		TextButton cancelButton = new TextButton("Cancel");
		cancelButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				dialogBoxGen.hide();
				functions.setDialogBoxForMessages();
			}
		});

		TextButton scheduleButton = new TextButton("Schedule");
		scheduleButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				if (passWorkspace.getCurrentValue() == null) {
					functions.printMsgInDialogBox("You must give your password! ");
					return;
				}

				dialogBoxGen.hide();
				functions.setDialogBoxForMessages();

				commands.setValue("schedule");
				if (functions.checkFields())
					return;

				if (toBeTransferredStore == null) {
					Info.display("","You have not added any files for transfer");
					return;
				} else if (toBeTransferredStore.getAll() == null) {
					Info.display("","You have not added any files for transfer");
					return;
				} else if (toBeTransferredStore.getAll().size() < 1) {
					Info.display("","You have not added any files for transfer");
					return;
				}
				rpCalls.schedule();
			}
		});

		panel.setWidget(p);
		panel.setButtonAlign(BoxLayoutPack.START);
		panel.addButton(cancelButton);
		panel.addButton(scheduleButton);

		// key handlers -------------
		foc = new FocusPanel();
		foc.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					dialogBoxGen.hide();
					functions.setDialogBoxForMessages();
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					if (passWorkspace.getCurrentValue() == null) {
						functions.printMsgInDialogBox("You must give your password! ");
						return;
					}

					dialogBoxGen.hide();
					functions.setDialogBoxForMessages();

					commands.setValue("schedule");
					if (functions.checkFields())
						return;

					if (toBeTransferredStore == null) {
						Info.display("","You have not added any files for transfer");
						return;
					} else if (toBeTransferredStore.getAll() == null) {
						Info.display("","You have not added any files for transfer");
						return;
					} else if (toBeTransferredStore.getAll().size() < 1) {
						Info.display("","You have not added any files for transfer");
						return;
					}
					rpCalls.schedule();
				}
			}
		});
		foc.add(panel);
		// --------------
		return foc;
	}

	/*
	 * asPopUpDataStorage input: Nothing -- returns: Widget Creates a pop up
	 * panel so that user can choose the remote node
	 */
	public Widget asPopUpDataStorage() {
		FramedPanel panel = new FramedPanel();
		panel.setHeadingText("Select DataStorage");
		panel.setWidth(300);
		panel.setBodyStyle("background: none; padding: 5px");

		VerticalLayoutContainer p = new VerticalLayoutContainer();
		panel.setWidget(p);

		// Add a list box with multiple selection enabled
		if (multiBoxDataStorages == null) {
			multiBoxDataStorages = new ListBox(false);
			multiBoxDataStorages.setWidth("150px");
			multiBoxDataStorages.setVisibleItemCount(5);
		}
		VerticalPanel multiBoxPanel = new VerticalPanel();
		multiBoxPanel.setSpacing(4);
		multiBoxPanel.add(multiBoxDataStorages);

		VerticalLayoutContainer con = new VerticalLayoutContainer();
		con.setBorders(true);
		con.add(multiBoxPanel, new VerticalLayoutData(1, 1));

		panel.setWidget(con);
		// cp.setButtonAlign(BoxLayoutPack.CENTER);
		panel.setButtonAlign(BoxLayoutPack.START);

		TextButton cancelButton = new TextButton("Cancel");
		cancelButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				dialogBoxGen.hide();
				functions.setDialogBoxForMessages();
				destCombo.setValue(lastDestComboValue);
			}
		});
		TextButton refreshButton = new TextButton("Refresh");
		refreshButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				// loading items
				rpCalls.getDataStorages();
			}
		});
		TextButton nextButton = new TextButton("Next");
		nextButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				if (multiBoxDataStorages.getSelectedIndex() == -1) {
					Info.display("Warning",
							"You have not selected DataStorage ! ");
				} else {
					dialogBoxGen.hide();
					functions.setDialogBoxForMessages();
					String dataStorageName = multiBoxDataStorages
							.getValue(multiBoxDataStorages.getSelectedIndex());
			//		Info.display("Selected", "You selected " + dataStorageName);
					selectedDatastorageName = dataStorageName;
					// searching and storing the id of the selected DataStorage
					selectedDataStorageId = null;
					if (dataStoragesList != null) {
						for (String tmp : dataStoragesList) {
							// tmp contains:
							// resultIdOfIS--name--description--endpoint--username--password--propertyFolders
							String[] parts = tmp.split("--");
							if (dataStorageName.compareTo(parts[1]) == 0) {
								selectedDataStorageId = parts[0];
							}
						}
					}
			//		Info.display("Selected", "You selected " + dataStorageName);

					if (combo1==null || combo1.getCurrentValue()==null ||
							!combo1.isAgentSource()
							|| selectedAgentSource == null) {
						//selection of agent should be transparent based on statistics
						functions.transparentSelectionOfAgent(FolderToRetrieve.DataStorageFolder);
						//changed ...
						//dialogBoxGen = functions.createDialogBox(asPopUpAgents(FolderToRetrieve.DataStorageFolder));
						//dialogBoxGen.center();
						//focusTimer.schedule(200);
					} else {
						rpCalls.getDatastorageFolder(selectedDataStorageId, "./");
				//		Info.display("Selected", "You selected "
				//				+ selectedAgentSource);
					}

				}
			}
		});
		panel.addButton(cancelButton);
		panel.addButton(refreshButton);
		panel.addButton(nextButton);

		// adding items for the first time
		if (multiBoxDataStorages.getItemCount() < 1) {
			// loading items
			rpCalls.getDataStorages();
		}

		// key handlers -------------
		foc = new FocusPanel();
		foc.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					dialogBoxGen.hide();
					functions.setDialogBoxForMessages();
					destCombo.setValue(lastDestComboValue);
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					if (multiBoxDataStorages.getSelectedIndex() == -1) {
						Info.display("Warning",
								"You have not selected DataStorage ! ");
					} else {
						dialogBoxGen.hide();
						functions.setDialogBoxForMessages();
						String dataStorageName = multiBoxDataStorages
								.getValue(multiBoxDataStorages
										.getSelectedIndex());
						selectedDatastorageName = dataStorageName;
						// searching and storing the id of the selected
						// DataStorage
						selectedDataStorageId = null;
						if (dataStoragesList != null) {
							for (String tmp : dataStoragesList) {
								// tmp contains:
								// resultIdOfIS--name--description--endpoint--username--password--propertyFolders
								String[] parts = tmp.split("--");
								if (dataStorageName.compareTo(parts[1]) == 0) {
									selectedDataStorageId = parts[0];
								}
							}
						}
			//			Info.display("Selected", "You selected "
			//					+ dataStorageName);

						if (combo1==null || combo1.getCurrentValue()==null ||
								!combo1.isAgentSource()
								|| selectedAgentSource == null) {
							//selection of agent should be transparent based on statistics
							functions.transparentSelectionOfAgent(FolderToRetrieve.DataStorageFolder);
							//changed ...
							//dialogBoxGen = functions.createDialogBox(asPopUpAgents(FolderToRetrieve.DataStorageFolder));
							//dialogBoxGen.center();
							//focusTimer.schedule(200);
						} else {
							rpCalls.getDatastorageFolder(selectedDataStorageId, "./");
				//			Info.display("Selected", "You selected "
				//					+ selectedAgentSource);
						}
					}
				}
			}
		});
		foc.add(panel);
		// --------------
		return foc;
	}
	/*
	 * asPopUpDataStorage input: Nothing -- returns: Widget Creates a pop up
	 * panel so that user can choose the remote node
	 */
	public Widget asPopUpTreeWriteSources() {
		FramedPanel panel = new FramedPanel();
		panel.setHeadingText("Select Collection for Destination");
		panel.setWidth(300);
		panel.setBodyStyle("background: none; padding: 5px");

		VerticalLayoutContainer p = new VerticalLayoutContainer();
		panel.setWidget(p);

		// Add a list box with multiple selection enabled
		if (multiBoxTreeWriteSources== null) {
			multiBoxTreeWriteSources = new ListBox(false);
			multiBoxTreeWriteSources.setWidth("150px");
			multiBoxTreeWriteSources.setVisibleItemCount(5);
		}
		VerticalPanel multiBoxPanel = new VerticalPanel();
		multiBoxPanel.setSpacing(4);
		multiBoxPanel.add(multiBoxTreeWriteSources);

		VerticalLayoutContainer con = new VerticalLayoutContainer();
		con.setBorders(true);
		con.add(multiBoxPanel, new VerticalLayoutData(1, 1));

		panel.setWidget(con);
		// cp.setButtonAlign(BoxLayoutPack.CENTER);
		panel.setButtonAlign(BoxLayoutPack.START);

		TextButton cancelButton = new TextButton("Cancel");
		cancelButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				dialogBoxGen.hide();
				functions.setDialogBoxForMessages();
				combo1.setValue(lastCombo1Value);
			}
		});
		TextButton refreshButton = new TextButton("Refresh");
		refreshButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				// loading items
				rpCalls.getTreeWriteSources(false);
			}
		});
		TextButton nextButton = new TextButton("Next");
		nextButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				asPopUpTreeWriteSourcesOnSelect();
			}
		});
		panel.addButton(cancelButton);
		panel.addButton(refreshButton);
		panel.addButton(nextButton);

		//CHANGED - Now it is called right after we got the tree write source
		// adding items for the first time
		//		if (multiBoxTreeWriteSources.getItemCount() < 1) {
		//			// loading items
		//			rpCalls.getTreeWriteSources();
		//		}

		// key handlers -------------
		foc = new FocusPanel();
		foc.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					dialogBoxGen.hide();
					functions.setDialogBoxForMessages();
					combo1.setValue(lastCombo1Value);
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					asPopUpTreeWriteSourcesOnSelect();
				}
			}
		});
		foc.add(panel);
		// --------------
		return foc;
	}

	public void asPopUpTreeWriteSourcesOnSelect(){
		if (multiBoxTreeWriteSources.getSelectedIndex() == -1) {
			Info.display("Warning",
					"You have not selected TreeSource ! ");
		} else {
			dialogBoxGen.hide();
			functions.setDialogBoxForMessages();
			String id = multiBoxTreeWriteSources
					.getValue(multiBoxTreeWriteSources.getSelectedIndex());
		//	Info.display("Selected", "You selected " + id);

			makeNewFolder.hide();
			if(isAdmin){						
				deleteCurrentTreeSource.show();
			}
			makeNewTreeSource.show();
			deleteCurrentFolder.hide();
			

			destCombo.setValue(DESTTYPE.TreeBased.toString());
			selectedDestCollection = id;

			if (combo1==null || combo1.getCurrentValue()==null ||
					!combo1.isAgentSource()
					|| selectedAgentSource == null) {
				//selection of agent should be transparent based on statistics
				functions.transparentSelectionOfAgent(FolderToRetrieve.NONE);
				//changed ...
				//dialogBoxGen = functions.createDialogBox(asPopUpAgents(FolderToRetrieve.DataStorageFolder));
				//dialogBoxGen.center();
				//focusTimer.schedule(200);
			} 
		}
	}

	/*
	 * asPopUpDataSource input: Nothing -- returns: Widget Creates a pop up
	 * panel so that user can choose the data source
	 */
	public Widget asPopUpDataSource() {
		FramedPanel panel = new FramedPanel();
		panel.setHeadingText("Select DataSource");
		panel.setWidth(300);
		panel.setBodyStyle("background: none; padding: 5px");

		VerticalLayoutContainer p = new VerticalLayoutContainer();
		panel.setWidget(p);

		// Add a list box with multiple selection enabled
		if (multiBoxDataSources == null) {
			multiBoxDataSources = new ListBox(false);
			multiBoxDataSources.setWidth("150px");
			multiBoxDataSources.setVisibleItemCount(5);
		}
		VerticalPanel multiBoxPanel = new VerticalPanel();
		multiBoxPanel.setSpacing(4);
		multiBoxPanel.add(multiBoxDataSources);

		VerticalLayoutContainer con = new VerticalLayoutContainer();
		con.setBorders(true);
		con.add(multiBoxPanel, new VerticalLayoutData(1, 1));

		panel.setWidget(con);
		panel.setButtonAlign(BoxLayoutPack.START);

		TextButton cancelButton = new TextButton("Cancel");
		cancelButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				dialogBoxGen.hide();
				functions.setDialogBoxForMessages();
				Info.display("Warning", "You have not selected DataSource ! ");
				combo1.setValue(lastCombo1Value);
			}
		});
		TextButton okButton = new TextButton("OK");
		okButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				if (multiBoxDataSources.getSelectedIndex() == -1) {
					Info.display("Warning",
							"You have not selected DataSource ! ");
				} else {
					dialogBoxGen.hide();
					functions.setDialogBoxForMessages();
					String name = multiBoxDataSources
							.getValue(multiBoxDataSources.getSelectedIndex());
					// searching and storing the id of the selected DataSource
					selectedDataSourceId = null;
					if (dataSourcesList != null) {
						for (String tmp : dataSourcesList) {
							// tmp contains:
							// resultIdOfIS--name--description--endpoint--username--password--propertyFolders
							String[] parts = tmp.split("--");
							if (name.compareTo(parts[1]) == 0) {
								selectedDataSourceId = parts[0];
							}
						}
					}
			//		Info.display("Selected", "You selected " + name);
					rpCalls.getDatasourceFolder(selectedDataSourceId, "");
				}
			}
		});

		TextButton refreshButton = new TextButton("Refresh");
		refreshButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				// loading items
				rpCalls.getDataSources();
			}
		});

		panel.addButton(cancelButton);
		panel.addButton(refreshButton);
		panel.addButton(okButton);

		// adding items for the first time
		if (multiBoxDataSources.getItemCount() < 1) {
			// loading items
			rpCalls.getDataSources();
		}

		// key handlers -------------
		foc = new FocusPanel();
		foc.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					dialogBoxGen.hide();
					functions.setDialogBoxForMessages();
					Info.display("Warning",
							"You have not selected DataSource ! ");
					combo1.setValue(lastCombo1Value);
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					if (multiBoxDataSources.getSelectedIndex() == -1) {
						Info.display("Warning",
								"You have not selected DataSource ! ");
					} else {
						String name = multiBoxDataSources
								.getValue(multiBoxDataSources
										.getSelectedIndex());
						// searching and storing the id of the selected
						// DataSource
						selectedDataSourceId = null;
						if (dataSourcesList != null) {
							for (String tmp : dataSourcesList) {
								// tmp contains:
								// resultIdOfIS--name--description--endpoint--username--password--propertyFolders
								String[] parts = tmp.split("--");
								if (name.compareTo(parts[1]) == 0) {
									selectedDataSourceId = parts[0];
								}
							}
						}
				//		Info.display("Selected", "You selected " + name);
						rpCalls.getDatasourceFolder(selectedDataSourceId, "");
					}
				}
			}
		});
		foc.add(panel);
		// --------------
		return foc;
	}

	/*
	 * asPopUpDataSource input: Nothing -- returns: Widget Creates a pop up
	 * panel so that user can choose the data source
	 */
	public Widget asPopUpAgentSource() {
		FramedPanel panel = new FramedPanel();
		panel.setHeadingText("Select Agent");
		panel.setWidth(300);
		panel.setBodyStyle("background: none; padding: 5px");

		VerticalLayoutContainer p = new VerticalLayoutContainer();
		panel.setWidget(p);

		// Add a list box with multiple selection enabled
		if (multiBoxAgents == null) {
			multiBoxAgents = new ListBox(false);
			multiBoxAgents.setWidth("260px");
			multiBoxAgents.setVisibleItemCount(5);
		}
		VerticalPanel multiBoxPanel = new VerticalPanel();
		multiBoxPanel.setSpacing(4);
		multiBoxPanel.add(multiBoxAgents);

		VerticalLayoutContainer con = new VerticalLayoutContainer();
		con.setBorders(true);
		con.add(multiBoxPanel, new VerticalLayoutData(1, 1));

		panel.setWidget(con);
		panel.setButtonAlign(BoxLayoutPack.START);

		TextButton cancelButton = new TextButton("Cancel");
		cancelButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				dialogBoxGen.hide();
				functions.setDialogBoxForMessages();
				Info.display("Warning", "You have not selected Agent ! ");
				combo1.setValue(lastCombo1Value);
			}
		});

		TextButton okButton = new TextButton("OK");
		okButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				if (multiBoxAgents.getSelectedIndex() == -1) {
					Info.display("Warning", "You have not selected Agent ! ");
				} else {
					dialogBoxGen.hide();
					functions.setDialogBoxForMessages();

					String agentName = multiBoxAgents.getValue(multiBoxAgents
							.getSelectedIndex());
			//		Info.display("Selected", "You selected " + agentName);
					agentHostname = agentName;
					//setting tool tip
					if(agentStats!=null){
						if(agentStatsTooltip==null)agentStatsTooltip=functions.createAnchor("");
						agentStatsTooltip.setBodyText("Selected agent:"+agentHostname);
						agentStats.setToolTipConfig(agentStatsTooltip);
					}

					String[] agentsArray = stringOfAgents.split("\n");
					for (String tmp : agentsArray) {
						// tmp contains: id--name--hostName--port
						String[] partsOfInfo = tmp.split("--");
						if (partsOfInfo[2].compareTo(agentName) == 0) {
							selectedAgentSourcePort = partsOfInfo[3];
						}
					}
					selectedAgentSource = agentName;
					agentIsSelectedFromStatsPanel=false;

					comboAgent.setValue(agentName);
					//functions.redrawEast();
					rpCalls.getAgentFolder("");
					if(destCombo==null || destCombo.getCurrentValue()==null ||
							!destCombo.isAgentDest()){
						//nothing
					}else{
						selectedAgentDestination = selectedAgentSource;
						selectedAgentDestinationPort = selectedAgentSourcePort;
						rpCalls.getAgentFolderDest("");
					}					
				}
			}
		});

		TextButton refreshButton = new TextButton("Refresh");
		refreshButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				// loading items
				rpCalls.getAgents();
			}
		});

		panel.addButton(cancelButton);
		panel.addButton(refreshButton);
		panel.addButton(okButton);

		// adding items for the first time
		if (multiBoxAgents.getItemCount() < 1) {
			// loading items
			rpCalls.getAgents();
		}

		// key handlers -------------
		foc = new FocusPanel();
		foc.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					dialogBoxGen.hide();
					functions.setDialogBoxForMessages();
					Info.display("Warning", "You have not selected Agent ! ");
					combo1.setValue(lastCombo1Value);
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					if (multiBoxAgents.getSelectedIndex() == -1) {
						Info.display("Warning",
								"You have not selected Agent ! ");
					} else {
						dialogBoxGen.hide();
						functions.setDialogBoxForMessages();

						String agentName = multiBoxAgents
								.getValue(multiBoxAgents.getSelectedIndex());
			//			Info.display("Selected", "You selected " + agentName);
						agentHostname = agentName;
						//setting tool tip
						if(agentStats!=null){
							if(agentStatsTooltip==null)agentStatsTooltip=functions.createAnchor("");
							agentStatsTooltip.setBodyText("Selected agent:"+agentHostname);
							agentStats.setToolTipConfig(agentStatsTooltip);
						}

						String[] agentsArray = stringOfAgents.split("\n");
						for (String tmp : agentsArray) {
							// tmp contains: id--name--hostName--port
							String[] partsOfInfo = tmp.split("--");
							if (partsOfInfo[2].compareTo(agentName) == 0) {
								selectedAgentSourcePort = partsOfInfo[3];
							}
						}
						selectedAgentSource = agentName;
						agentIsSelectedFromStatsPanel=false;

						comboAgent.setValue(agentName);
						portlet.redrawEast();

						rpCalls.getAgentFolder("");
					}
				}
			}
		});
		foc.add(panel);
		// --------------
		return foc;
	}

	/*
	 * asPopUpSourceType input: Nothing -- returns: Widget Creates a pop up
	 * panel so that user can choose the source type
	 */
	public Widget asPopUpSourceType() {
		FramedPanel panel = new FramedPanel();
		panel.setHeadingText("Select Source Type");
		panel.setWidth(300);
		panel.setBodyStyle("background: none; padding: 5px");

		VerticalLayoutContainer p = new VerticalLayoutContainer();
		panel.setWidget(p);
		// Add a list box with multiple selection enabled
		if (multiBoxSourceType == null) {
			multiBoxSourceType = new ListBox(false);
			multiBoxSourceType.setWidth("150px");
			multiBoxSourceType.setVisibleItemCount(5);
		}
		VerticalPanel multiBoxPanel = new VerticalPanel();
		multiBoxPanel.setSpacing(4);
		multiBoxPanel.add(multiBoxSourceType);

		VerticalLayoutContainer con = new VerticalLayoutContainer();
		con.setBorders(true);
		con.add(multiBoxPanel, new VerticalLayoutData(1, 1));

		panel.setWidget(con);
		panel.setButtonAlign(BoxLayoutPack.START);

		TextButton cancelButton = new TextButton("Cancel");
		cancelButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				dialogBoxGen.hide();
				functions.setDialogBoxForMessages();
			}
		});

		TextButton nextButton = new TextButton("Next");
		nextButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				asPopUpSourceTypeOnSelect();
			}
		});
		panel.addButton(cancelButton);
		panel.addButton(nextButton);

		// adding items for the first time
		if (multiBoxSourceType.getItemCount() < 1) {
			multiBoxSourceType.addItem(SOURCETYPE.TreeBased.toString());
			multiBoxSourceType.addItem(SOURCETYPE.Workspace.toString());
			multiBoxSourceType.addItem(SOURCETYPE.DataSource.toString());
			multiBoxSourceType.addItem(SOURCETYPE.URI.toString());
			multiBoxSourceType.addItem(SOURCETYPE.MongoDB.toString());
			multiBoxSourceType.addItem(SOURCETYPE.AgentSource.toString());
		}

		// key handlers -------------
		foc = new FocusPanel();
		foc.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					dialogBoxGen.hide();
					functions.setDialogBoxForMessages();
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					asPopUpSourceTypeOnSelect();
				}
			}
		});
		foc.add(panel);
		// --------------
		return foc;
	}

	public void asPopUpSourceTypeOnSelect(){
		if (multiBoxSourceType.getSelectedIndex() == -1) {
			Info.display("Warning",
					"You have not selected DataSource ! ");
		} else {
			dialogBoxGen.hide();
			functions.setDialogBoxForMessages();
			String selected = multiBoxSourceType
					.getValue(multiBoxSourceType.getSelectedIndex());
			combo1.setValue(selected);
			//	Info.display("Warning", selected);
			String v = selected == null ? "nothing" : combo1
					.getCurrentValue();
		//	Info.display("Selected", "You selected " + v);

			targetStore = null;
			toBeTransferredStore.clear();
			toBeTransferredStore.commitChanges();

			if (combo1.isTreeBased()) {
				rpCalls.getTreeReadSources(true);
			} 
			if (combo1.isWorkspace()) {
				rpCalls.getWorkspaceFolder(null, false);
			} else if (combo1.isDatasource()) {
				dialogBoxGen = functions.createDialogBox(asPopUpDataSource());
				dialogBoxGen.center();
				focusTimer.schedule(200);
			} else if (combo1.isMongoDB()) {
				if (isAdmin) {
					dialogBoxGen = functions.createDialogBox(asPopUpMongoDBSource());
					dialogBoxGen.center();
					focusTimer.schedule(200);
				} else {
					// initialization - default values
					initializeMongoDBSource();
					rpCalls.getMongoDBFolder("/");
				}
			} else if (combo1.isAgentSource()) {
				dialogBoxGen = functions.createDialogBox(asPopUpAgentSource());
				dialogBoxGen.center();
				focusTimer.schedule(200);
			} else if (combo1.isTreeBased()) {
				//TO-DO
			} else {
				dialogBoxGen = functions.createDialogBox(asPopUpUris());
				dialogBoxGen.center();
				focusTimer.schedule(200);
			}

			// get the agents now ...
			if (ResourceName.getCurrentValue() == null) {
				functions.printMsgInDialogBox("You should have a Resource Name !");
			}
			if (scope.getCurrentValue() == null) {
				functions.printMsgInDialogBox("You should have a Scope !");
			} else {
				rpCalls.getAgents();
			}
		}
	}
	public void initializeMongoDBSource() {
		smServiceNameSource = new TextField();
		smServiceNameSource.setValue("scheduler-portlet");
		smServiceClassSource = new TextField();
		smServiceClassSource.setValue("data-transfer");
		smOwnerSource = new TextField();
		smOwnerSource.setValue(ResourceName.getCurrentValue());
		smAccessTypeSource = new SimpleComboBox<String>(
				new LabelProvider<String>() {
					public String getLabel(String item) {
						return item.toString().substring(0, 1)
								+ item.toString().substring(1).toLowerCase();
					}
				});
		smAccessTypeSource.setTriggerAction(TriggerAction.ALL);
		smAccessTypeSource.setEditable(false);
		smAccessTypeSource.add("SHARED");
		smAccessTypeSource.add("PUBLIC");
		smAccessTypeSource.add("PRIVATE");
		smAccessTypeSource.setForceSelection(true);
		smAccessTypeSource.setValue("SHARED");
	}

	/*
	 * asPopUpDestinationType input: Nothing -- returns: Widget Creates a pop up
	 * panel so that user can choose the destination type
	 */
	public Widget asPopUpDestinationType() {
		FramedPanel panel = new FramedPanel();
		panel.setHeadingText("Select Destination Type");
		panel.setWidth(300);
		panel.setBodyStyle("background: none; padding: 5px");

		VerticalLayoutContainer p = new VerticalLayoutContainer();
		panel.setWidget(p);
		// Add a list box with multiple selection enabled
		if (multiBoxDestinationType == null) {
			multiBoxDestinationType = new ListBox(false);
			multiBoxDestinationType.setWidth("150px");
			multiBoxDestinationType.setVisibleItemCount(5);
		}
		VerticalPanel multiBoxPanel = new VerticalPanel();
		multiBoxPanel.setSpacing(4);
		multiBoxPanel.add(multiBoxDestinationType);

		VerticalLayoutContainer con = new VerticalLayoutContainer();
		con.setBorders(true);
		con.add(multiBoxPanel, new VerticalLayoutData(1, 1));

		panel.setWidget(con);
		panel.setButtonAlign(BoxLayoutPack.START);

		TextButton cancelButton = new TextButton("Cancel");
		cancelButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				dialogBoxGen.hide();
				functions.setDialogBoxForMessages();
			}
		});

		TextButton nextButton = new TextButton("Next");
		nextButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				if (multiBoxDestinationType.getSelectedIndex() == -1) {
					Info.display("Warning",
							"You have not selected Destination Type ! ");
				} 
				else {
					
					String selected = multiBoxDestinationType
							.getValue(multiBoxDestinationType
									.getSelectedIndex());
					if(selected.compareTo(Common.DESTTYPE.TreeBased.toString())==0){
						Info.display("Warning",
								"The source is not tree based ! You cannot select tree based as a destination ");
						return ;
					}
					
					dialogBoxGen.hide();
					functions.setDialogBoxForMessages();
					destCombo.setValue(selected);
					String v = destCombo.getCurrentValue() == null ? "nothing"
							: destCombo.getCurrentValue();
		//			Info.display("Selected", "You selected " + v);
					if (destCombo.getCurrentValue() == null)
						return;


					if (destCombo.isMongoDBStorage()) {
						targetStore = null;
						toBeTransferredStore.clear();
						toBeTransferredStore.commitChanges();
						if (isAdmin) {
							dialogBoxGen = functions.createDialogBox(asPopUpMongoDBStorage());
							dialogBoxGen.center();
							focusTimer.schedule(200);
						} else {
							// initialization - default values
							initializeMongoDBStorage();
							if (combo1==null || combo1.getCurrentValue()==null ||
									!combo1.isAgentSource()
									|| selectedAgentSource == null) {

								//selection of agent should be transparent based on statistics
								functions.transparentSelectionOfAgent(FolderToRetrieve.MongoDBStorageFolder);
								//changed ...
								//dialogBoxGen = functions.createDialogBox(asPopUpAgents(FolderToRetrieve.MongoDBStorageFolder));
								//dialogBoxGen.center();
								//focusTimer.schedule(200);
							} else {
								rpCalls.getMongoDBFolderDest("/");
						//		Info.display("Selected", "You selected "
						//				+ selectedAgentSource);
							}
						}
					} else if (destCombo.isDataStorage()) {
						dialogBoxGen = functions.createDialogBox(asPopUpDataStorage());
						dialogBoxGen.center();
						focusTimer.schedule(200);
					} else if (destCombo.isAgentDest()) {
						if (combo1==null || combo1.getCurrentValue()==null ||
								!combo1.isAgentSource()
								|| selectedAgentSource == null) {

							dialogBoxGen = functions.createDialogBox(asPopUpAgents(FolderToRetrieve.AgentDestFolder));
							dialogBoxGen.center();
							focusTimer.schedule(200);
						} else {
							selectedAgentDestination=selectedAgentSource;
							selectedAgentDestinationPort=selectedAgentSourcePort;
							rpCalls.getAgentFolderDest("");
						//	Info.display("Selected", "You selected "
						//			+ selectedAgentSource);
						}
					}
				}
			}
		});
		panel.addButton(cancelButton);
		panel.addButton(nextButton);

		// adding items for the first time
		if (multiBoxDestinationType.getItemCount() < 1) {
			multiBoxDestinationType.addItem(DESTTYPE.TreeBased.toString());
			multiBoxDestinationType.addItem(DESTTYPE.MongoDBStorage.toString());
			multiBoxDestinationType.addItem(DESTTYPE.DataStorage.toString());
			multiBoxDestinationType.addItem(DESTTYPE.AgentDest.toString());
		}

		// key handlers -------------
		foc = new FocusPanel();
		foc.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					dialogBoxGen.hide();
					functions.setDialogBoxForMessages();
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					if (multiBoxDestinationType.getSelectedIndex() == -1) {
						Info.display("Warning",
								"You have not selected Destination Type ! ");
					} else {
						dialogBoxGen.hide();
						functions.setDialogBoxForMessages();
						String selected = multiBoxDestinationType
								.getValue(multiBoxDestinationType
										.getSelectedIndex());
						destCombo.setValue(selected);
						String v = destCombo.getCurrentValue() == null ? "nothing"
								: destCombo.getCurrentValue();
					//	Info.display("Selected", "You selected " + v);
						if (destCombo.getCurrentValue() == null)
							return;

						if (destCombo.isMongoDBStorage()) {
							targetStore = null;
							toBeTransferredStore.clear();
							toBeTransferredStore.commitChanges();
							if (isAdmin) {
								dialogBoxGen = functions.createDialogBox(asPopUpMongoDBStorage());
								dialogBoxGen.center();
								focusTimer.schedule(200);
							} else {
								// initialization - default values
								initializeMongoDBStorage();
								if (combo1==null || combo1.getCurrentValue()==null ||
										!combo1.isAgentSource()
										|| selectedAgentSource == null) {
									//selection of agent should be transparent based on statistics
									functions.transparentSelectionOfAgent(FolderToRetrieve.MongoDBStorageFolder);
									//changed ...
									//dialogBoxGen = functions.createDialogBox(asPopUpAgents(FolderToRetrieve.MongoDBStorageFolder));
									//dialogBoxGen.center();
									//focusTimer.schedule(200);
								} else {
									rpCalls.getMongoDBFolderDest("/");
						//			Info.display("Selected", "You selected "
						//					+ selectedAgentSource);
								}
							}
						} else if (destCombo.isDataStorage()) {
							dialogBoxGen = functions.createDialogBox(asPopUpDataStorage());
							dialogBoxGen.center();
							focusTimer.schedule(200);
						} else if (destCombo.isAgentDest()) {
							if (combo1==null || combo1.getCurrentValue()==null ||
									!combo1.isAgentSource()
									|| selectedAgentSource == null) {
								//selection of agent should be transparent based on statistics
								functions.transparentSelectionOfAgent(FolderToRetrieve.AgentDestFolder);
								//changed ...
								//dialogBoxGen = functions.createDialogBox(asPopUpAgents(FolderToRetrieve.AgentDestFolder));
								//dialogBoxGen.center();
								//focusTimer.schedule(200);
							} else {
								selectedAgentDestination=selectedAgentSource;
								selectedAgentDestinationPort=selectedAgentSourcePort;
								rpCalls.getAgentFolderDest("");
						//		Info.display("Selected", "You selected "
						//				+ selectedAgentSource);
							}
						}
					}
				}
			}
		});
		foc.add(panel);
		// --------------
		return foc;
	}

	public void initializeMongoDBStorage() {
		smServiceName = new TextField();
		smServiceName.setValue("scheduler-portlet");
		smServiceClass = new TextField();
		smServiceClass.setValue("data-transfer");
		smOwner = new TextField();
		smOwner.setValue(ResourceName.getCurrentValue());
		smAccessType = new SimpleComboBox<String>(new LabelProvider<String>() {
			public String getLabel(String item) {
				return item.toString().substring(0, 1)
						+ item.toString().substring(1).toLowerCase();
			}
		});
		smAccessType.setTriggerAction(TriggerAction.ALL);
		smAccessType.setEditable(false);
		smAccessType.add("SHARED");
		smAccessType.add("PUBLIC");
		smAccessType.add("PRIVATE");
		smAccessType.setForceSelection(true);
		smAccessType.setValue("SHARED");
	}

	/*
	 * asPopUpUris input: Nothing -- returns: Widget Creates a pop up panel so
	 * that user can choose the URI's he wants to transfer
	 */
	public Widget asPopUpUris() {
		FramedPanel panel = new FramedPanel();
		panel.setHeadingText("URI's");
		panel.addStyleName("margin-10");
		panel.setPixelSize(400, 250);

		cc1Uris = new ColumnConfig<Uri, String>(uriProp.name(), 60, "Name");
		cc2Uris = new ColumnConfig<Uri, String>(uriProp.URI(), 150, "URI");

		List<ColumnConfig<Uri, ?>> l = new ArrayList<ColumnConfig<Uri, ?>>();
		l.add(cc1Uris);
		l.add(cc2Uris);
		ColumnModel<Uri> cm = new ColumnModel<Uri>(l);
		if (storeForUris == null)
			storeForUris = new ListStore<Uri>(uriProp.key());

		uriGrid = new Grid<Uri>(storeForUris, cm);
		uriGrid.getView().setAutoExpandColumn(cc1Uris);
		editing = createGridEditing(uriGrid);
		editing.addEditor(cc1Uris, new TextField());
		editing.addEditor(cc2Uris, new TextField());

		ToolBar toolBar = new ToolBar();
		TextButton clear = new TextButton("Clear all");
		clear.setBorders(true);
		clear.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				editing.cancelEditing();
				storeForUris.clear();
				storeForUris.commitChanges();
			}
		});
		toolBar.add(clear);

		TextButton add = new TextButton("Add URI");
		add.setBorders(true);
		add.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				Uri uri = new Uri();
				uri.setName("Uri Name Example");
				uri.setURI("http://example.gr");
				editing.cancelEditing();
				storeForUris.add(0, uri);
				editing.startEditing(new GridCell(0, 0));
			}
		});
		toolBar.add(add);

		VerticalLayoutContainer con = new VerticalLayoutContainer();
		con.setBorders(true);
		con.setHeight("185px");
		con.add(toolBar, new VerticalLayoutData(1, -1));
		con.add(uriGrid, new VerticalLayoutData(1, 1));

		con.setWidth("300px");

		// ************** buttons **************
		TextButton addButton = new TextButton("Add");
		addButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				onAddUris();
			}
		});
		TextButton cancelButton = new TextButton("Cancel");
		cancelButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				combo1.setValue(lastCombo1Value);
				dialogBoxGen.hide();
				functions.setDialogBoxForMessages();
				Info.display("Message",
						"You have not added any URIS for transfer");
			}
		});

		panel.setWidget(con);
		panel.setButtonAlign(BoxLayoutPack.START);
		panel.addButton(cancelButton);
		panel.addButton(addButton);

		uriGrid.setSelectionModel(new CellSelectionModel<Uri>());
		uriGrid.getColumnModel().getColumn(0).setHideable(false);

		// key handlers -------------
		foc = new FocusPanel();
		foc.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					combo1.setValue(lastCombo1Value);
					dialogBoxGen.hide();
					functions.setDialogBoxForMessages();
					Info.display("Message",
							"You have not added any URIS for transfer");
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					onAddUris();
				}
			}
		});
		foc.add(panel);
		// --------------
		return foc;
	}

	public void onAddUris(){
		dialogBoxGen.hide();
		functions.setDialogBoxForMessages();
		storeForUris.commitChanges();

		if (storeForUris.getAll().size() < 1) {
			Info.display("Message",
					"You have not added any URIS for transfer");
			combo1.setValue(lastCombo1Value);
		} else {
			lastCombo1Value = combo1.getCurrentValue();
			FolderDto tmpfolder = functions.makeFolder("URI");
			for (Uri tmp : storeForUris.getAll()) {
				FolderDto subf = functions.makeFolder(tmp.getURI());
				tmpfolder.addChild(subf);
			}
			folderSource = tmpfolder;
			
			//reset the dest panel
			folderDestination=null;
			folderResDestination=null;
			destCombo.setValue(null);
			lastDestComboValue=null;
			
			portlet.redrawEast();
		}	
	}
	/*
	 * asPopUpOperateTransfer input: Nothing -- returns: Widget Creates a pop up
	 * panel so that the user can choose a command for the specific transfer
	 * that he double clicked on.
	 */
	public Widget asPopUpOperateTransfer() {
		VerticalPanel vp = new VerticalPanel();
		FramedPanel panel = new FramedPanel();
		panel.setHeadingText("Functions for this transfer");
		panel.setWidth(300);
		panel.setBodyStyle("background: none; padding: 5px");

		VerticalLayoutContainer p = new VerticalLayoutContainer();
		panel.setWidget(p);

		transferIdInGrid = new TextField();
		transferIdInGrid.setAllowBlank(false);
		
		p.add(new FieldLabel(transferIdInGrid, "Transfer id:"),
				new VerticalLayoutData(1, -1));
		transferIdInGrid.setText(grid.getSelectionModel().getSelection().get(0)
				.getTransferId());
		transferIdInGrid.disable();
		
		commandsInGrid = new SimpleComboBox<String>(
				new LabelProvider<String>() {
					public String getLabel(String item) {
						return item.toString().substring(0, 1)
								+ item.toString().substring(1).toLowerCase();
					}
				});
		commandsInGrid.setTriggerAction(TriggerAction.ALL);
		commandsInGrid.setEditable(false);
		commandsInGrid.add("monitor");
		commandsInGrid.add("get outcomes");
		commandsInGrid.add("cancel");
		// Add a handler to change the data source
		commandsInGrid.addCollapseHandler(new CollapseHandler() {
			public void onCollapse(CollapseEvent event) {
				// set the same values in the main form
				if (commandsInGrid.getCurrentValue() == null)
					return;
				else
					commands.setText(commandsInGrid.getCurrentValue());
				ResourceName.setText(ResourceNameInGrid.getCurrentValue());
				scope.setText(scopeInGrid.getCurrentValue());
				transferId.setText(transferIdInGrid.getCurrentValue());
				if (force.isEnabled())
					force.setValue(forceInGrid.getValue());

				String v = commandsInGrid.getCurrentValue() == null ? "nothing"
						: commandsInGrid.getCurrentValue();
		//		Info.display("Selected", "You selected " + v);

				if (commandsInGrid.getCurrentValue().compareTo("monitor") == 0) {
					transferId.enable();
					force.disable();
					forceInGrid.disable();
				} else if (commands.getCurrentValue().compareTo("get outcomes") == 0) {
					transferId.enable();
					force.disable();
					forceInGrid.disable();
				} else if (commandsInGrid.getCurrentValue().compareTo("cancel") == 0) {
					transferId.enable();
					force.enable();
					forceInGrid.enable();
				}
			}
		});

		commandsInGrid.setAllowBlank(true);
		commandsInGrid.setForceSelection(true);

		p.add(new FieldLabel(commandsInGrid, "Commands"),
				new VerticalLayoutData(1, -1));

		ResourceNameInGrid = new TextField();
		ResourceNameInGrid.setAllowBlank(false);
		
		p.add(new FieldLabel(ResourceNameInGrid, "Resource Name"),
				new VerticalLayoutData(1, -1));
		ResourceNameInGrid.setText(ResourceName.getCurrentValue());
		ResourceNameInGrid.disable();
		
		scopeInGrid = new TextField();
		scopeInGrid.setAllowBlank(false);		
		p.add(new FieldLabel(scopeInGrid, "Scope"), new VerticalLayoutData(1,
				-1));
		scopeInGrid.setText(scope.getCurrentValue());
		scopeInGrid.disable();

		forceInGrid = new CheckBox();
		forceInGrid.setBoxLabel("force cancel");
		forceInGrid.disable();
		p.add(new FieldLabel(forceInGrid, "Force cancel"));
		forceInGrid.setValue(force.getValue());
		// ************** buttons **************
		TextButton submitButton = new TextButton("Submit");
		TextButton cancelButton = new TextButton("Cancel");

		cancelButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				dialogBoxGen.hide();
				functions.setDialogBoxForMessages();
			}
		});
		submitButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				dialogBoxGen.hide();
				functions.setDialogBoxForMessages();
				// check for the fields of name-scope-and schedule
				if (functions.checkFields())
					return;

				if (commandsInGrid.getCurrentValue().compareTo("monitor") == 0)
					rpCalls.monitor();
				else if (commandsInGrid.getCurrentValue().compareTo(
						"get outcomes") == 0)
					rpCalls.getOutcomes();
				else if (commandsInGrid.getCurrentValue().compareTo("cancel") == 0)
					rpCalls.cancel();
			}
		});
		p.add(cancelButton);
		p.add(submitButton);
		vp.add(panel);

		// key handlers -------------
		foc = new FocusPanel();
		foc.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					dialogBoxGen.hide();
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					dialogBoxGen.hide();
					functions.setDialogBoxForMessages();
					// check for the fields of name-scope-and schedule
					if (functions.checkFields())
						return;

					if (commandsInGrid.getCurrentValue().compareTo("monitor") == 0)
						rpCalls.monitor();
					else if (commandsInGrid.getCurrentValue().compareTo(
							"get outcomes") == 0)
						rpCalls.getOutcomes();
					else if (commandsInGrid.getCurrentValue().compareTo(
							"cancel") == 0)
						rpCalls.cancel();
				}
			}
		});
		foc.add(vp);

		// --------------
		return foc;
	}



	/*
	 * asPopUpAgentStats input: Nothing -- returns: Widget It creates a pop up
	 * panel for the agent statistics
	 */
	public Widget asPopUpAgentStats() {
		if(gridAgentStats==null){
			ColumnConfig<AgentStat, String> endpointAgentStats = new ColumnConfig<AgentStat, String>(
					agentStatProp.endpoint(), 120, "Endpoint");
			ColumnConfig<AgentStat, String> ongoing = new ColumnConfig<AgentStat, String>(
					agentStatProp.ongoing(), 60, "Ongoing");
			ColumnConfig<AgentStat, String> failed = new ColumnConfig<AgentStat, String>(
					agentStatProp.failed(), 60, "Failed");
			ColumnConfig<AgentStat, String> succesful = new ColumnConfig<AgentStat, String>(
					agentStatProp.succesful(), 60, "Succesful");
			ColumnConfig<AgentStat, String> canceled = new ColumnConfig<AgentStat, String>(
					agentStatProp.canceled(), 60, "Canceled");
			ColumnConfig<AgentStat, String> total = new ColumnConfig<AgentStat, String>(
					agentStatProp.total(), 60, "Total");

			List<ColumnConfig<AgentStat, ?>> l = new ArrayList<ColumnConfig<AgentStat, ?>>();
			l.add(endpointAgentStats);
			l.add(ongoing);
			l.add(failed);
			l.add(succesful);
			l.add(canceled);
			l.add(total);

			cmAgentStat = new ColumnModel<AgentStat>(l);

			storeAgentStats = new ListStore<AgentStat>(agentStatProp.key());

			gridAgentStats = new Grid<AgentStat>(storeAgentStats, cmAgentStat);
			gridAgentStats.getView().setAutoExpandColumn(endpointAgentStats);
			gridAgentStats.setBorders(true);
			gridAgentStats.getView().setStripeRows(true);
			gridAgentStats.getView().setColumnLines(true);

			// Add a selection model so we can select cells.
			final GridSelectionModel<AgentStat> selectionModel = new GridSelectionModel<AgentStat>();
			gridAgentStats.setSelectionModel(selectionModel);
			gridAgentStats.addRowDoubleClickHandler(new RowDoubleClickHandler() {
				public void onRowDoubleClick(RowDoubleClickEvent event) {
					String endpoint = gridAgentStats.getSelectionModel().getSelection()
							.get(0).getEndpoint();
					if (endpoint != null) {

						if (combo1==null || combo1.getCurrentValue()==null ||
								!combo1.isAgentSource()
								|| selectedAgentSource == null) {
							//if destination is not agent .. 
							if(destCombo==null || destCombo.getCurrentValue()==null ||
									!destCombo.isAgentDest()){
								agentIsSelectedFromStatsPanel=true;
								dialogBoxGen.hide();
								functions.setDialogBoxForMessages();
								Info.display("Selected", "You selected " + endpoint);
								agentHostname = endpoint;
								//setting tool tip
								if(agentStats!=null){
									if(agentStatsTooltip==null)agentStatsTooltip=functions.createAnchor("");
									agentStatsTooltip.setBodyText("Selected agent:"+agentHostname);
									agentStats.setToolTipConfig(agentStatsTooltip);
								}

								comboAgent.setValue(endpoint);
								lastDestComboValue = destCombo.getCurrentValue();
								portlet.redrawEast();
							}else{
								Info.display("Agent is already set (because selected destination is agent)", "Selected Agent " + endpoint);
							}	
						}
						else{
							Info.display("Agent is already set (because selected source is agent)", "Selected Agent " + endpoint);
						}
					}
				}
			});

		}

		if(listAgentStats==null){
			rpCalls.getAgentStatistics(null);
		}
		else if(storeAgentStats.size()<1){
			storeAgentStats.replaceAll(listAgentStats);
			gridAgentStats.reconfigure(storeAgentStats, cmAgentStat);
		}			

		FramedPanel panel = new FramedPanel();
		panel.setAnimCollapse(false);
		panel.setHeadingText("Agent Statistics - Double click to select");
		panel.setPixelSize(600, 250);
		panel.addStyleName("margin-10");

		TextButton cancelButton = new TextButton("Cancel");
		cancelButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				dialogBoxGen.hide();
				functions.setDialogBoxForMessages();
				destCombo.setValue(lastDestComboValue);
			}
		});
		TextButton refreshButton = new TextButton("Refresh");
		refreshButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				rpCalls.getAgentStatistics(null);
			}
		});

		panel.setWidget(gridAgentStats);
		panel.setButtonAlign(BoxLayoutPack.START);
		panel.addButton(cancelButton);
		panel.addButton(refreshButton);


		// key handlers -------------
		foc = new FocusPanel();
		foc.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					dialogBoxGen.hide();
					functions.setDialogBoxForMessages();
					destCombo.setValue(lastDestComboValue);
				}
			}
		});
		foc.add(panel);
		// --------------
		return foc;
	}

	/*
	 * asPopUpOutcomes input: Nothing -- returns: Widget It creates a pop up
	 * panel for showing the outcomes of a specific transfer
	 */
	public Widget asPopUpOutcomes() {
		IdentityValueProvider<Outcomes> identity = new IdentityValueProvider<Outcomes>();

		RowExpander<Outcomes> expander = new RowExpander<Outcomes>(identity,
				new AbstractCell<Outcomes>() {
			@Override
			public void render(Context context, Outcomes value,
					SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<p style='margin: 5px 5px 10px'><b>FileName:</b>"
						+ value.getFileName() + "</p>");
				sb.appendHtmlConstant("<p style='margin: 5px 5px 10px'><b>Outcomes:</b> "
						+ value.getTotalMessage() + "</p>");
			}
		});
		ColumnConfig<Outcomes, String> fileName = new ColumnConfig<Outcomes, String>(
				outcomesProp.fileName(), 70, "FileName");
		ColumnConfig<Outcomes, String> destination = new ColumnConfig<Outcomes, String>(
				outcomesProp.destination(), 90, "Destination");
		ColumnConfig<Outcomes, String> success = new ColumnConfig<Outcomes, String>(
				outcomesProp.success(), 55, "Success");
		ColumnConfig<Outcomes, String> failure = new ColumnConfig<Outcomes, String>(
				outcomesProp.failure(), 55, "Failure");
		ColumnConfig<Outcomes, String> transferTime = new ColumnConfig<Outcomes, String>(
				outcomesProp.transferTime(), 60, "Time");
		ColumnConfig<Outcomes, String> size = new ColumnConfig<Outcomes, String>(
				outcomesProp.size(), 60, "Size");
		ColumnConfig<Outcomes, String> transferredBytes = new ColumnConfig<Outcomes, String>(
				outcomesProp.transferredBytes(), 60, "TransferredBytes");
		ColumnConfig<Outcomes, String> exception = new ColumnConfig<Outcomes, String>(
				outcomesProp.exception(), 80, "Exceptions");

		List<ColumnConfig<Outcomes, ?>> l = new ArrayList<ColumnConfig<Outcomes, ?>>();
		l.add(expander);
		l.add(fileName);
		l.add(destination);
		l.add(success);
		l.add(failure);
		l.add(transferTime);
		l.add(size);
		l.add(transferredBytes);
		l.add(exception);

		ColumnModel<Outcomes> cm = new ColumnModel<Outcomes>(l);

		ListStore<Outcomes> store = new ListStore<Outcomes>(outcomesProp.key());
		if (listOutcomes != null)
			store.addAll(listOutcomes);

		final Grid<Outcomes> grid = new Grid<Outcomes>(store, cm);
		grid.getView().setAutoExpandColumn(fileName);
		grid.setBorders(true);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		expander.initPlugin(grid);

		// Add a selection model so we can select cells.
		final GridSelectionModel<Outcomes> selectionModel = new GridSelectionModel<Outcomes>();
		grid.setSelectionModel(selectionModel);
		grid.addRowDoubleClickHandler(new RowDoubleClickHandler() {
			public void onRowDoubleClick(RowDoubleClickEvent event) {
				// #
			}
		});

		FramedPanel panel = new FramedPanel();
		// panel.setCollapsible(true);
		panel.setAnimCollapse(false);
		panel.setHeadingText("Outcomes");
		panel.setPixelSize(700, 250);
		panel.addStyleName("margin-10");

		TextButton closeButton = new TextButton("Close");
		closeButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				dialogBoxGen.hide();
				functions.setDialogBoxForMessages();
			}
		});

		panel.setWidget(grid);
		panel.setButtonAlign(BoxLayoutPack.START);
		panel.addButton(closeButton);

		// key handlers -------------
		foc = new FocusPanel();
		foc.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					dialogBoxGen.hide();
					functions.setDialogBoxForMessages();
				}
			}
		});
		foc.add(panel);
		// --------------
		return foc;
	}

	public Widget asPopUpTreeOutcomes() {
		IdentityValueProvider<TreeOutcomes> identity = new IdentityValueProvider<TreeOutcomes>();

		RowExpander<TreeOutcomes> expander = new RowExpander<TreeOutcomes>(identity,
				new AbstractCell<TreeOutcomes>() {
			@Override
			public void render(Context context, TreeOutcomes value,
					SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<p style='margin: 5px 5px 10px'><b>TreeOutcomes:</b> "
						+ value.getTotalMessage() + "</p>");
			}
		});
		ColumnConfig<TreeOutcomes, String> sourceId = new ColumnConfig<TreeOutcomes, String>(
				treeOutcomesProp.sourceID(), 90, "SourceID");
		ColumnConfig<TreeOutcomes, String> destID = new ColumnConfig<TreeOutcomes, String>(
				treeOutcomesProp.destID(), 90, "DestID");
		ColumnConfig<TreeOutcomes, String> readTrees = new ColumnConfig<TreeOutcomes, String>(
				treeOutcomesProp.readTrees(), 90, "ReadTrees");
		ColumnConfig<TreeOutcomes, String> writtenTrees = new ColumnConfig<TreeOutcomes, String>(
				treeOutcomesProp.writtenTrees(), 90, "WrittenTrees");
		ColumnConfig<TreeOutcomes, String> exception = new ColumnConfig<TreeOutcomes, String>(
				treeOutcomesProp.exception(), 90, "Exceptions");
		
		ColumnConfig<TreeOutcomes, String> success = new ColumnConfig<TreeOutcomes, String>(
				treeOutcomesProp.success(), 90, "Success");
		ColumnConfig<TreeOutcomes, String> failure = new ColumnConfig<TreeOutcomes, String>(
				treeOutcomesProp.failure(), 90, "Failure");
		
		List<ColumnConfig<TreeOutcomes, ?>> l = new ArrayList<ColumnConfig<TreeOutcomes, ?>>();
		l.add(expander);
		l.add(sourceId);
		l.add(destID);
		l.add(success);
		l.add(failure);
		l.add(readTrees);
		l.add(writtenTrees);
		l.add(exception);

		ColumnModel<TreeOutcomes> cm = new ColumnModel<TreeOutcomes>(l);

		ListStore<TreeOutcomes> store = new ListStore<TreeOutcomes>(treeOutcomesProp.key());
		if (listTreeOutcomes != null){
			store.addAll(listTreeOutcomes);
		}
		
		final Grid<TreeOutcomes> grid = new Grid<TreeOutcomes>(store, cm);
		grid.getView().setAutoExpandColumn(sourceId);
		grid.setBorders(true);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		expander.initPlugin(grid);

		// Add a selection model so we can select cells.
		final GridSelectionModel<TreeOutcomes> selectionModel = new GridSelectionModel<TreeOutcomes>();
		grid.setSelectionModel(selectionModel);
		grid.addRowDoubleClickHandler(new RowDoubleClickHandler() {
			public void onRowDoubleClick(RowDoubleClickEvent event) {
				// #
			}
		});

		FramedPanel panel = new FramedPanel();
		// panel.setCollapsible(true);
		panel.setAnimCollapse(false);
		panel.setHeadingText("Tree Outcomes");
		panel.setPixelSize(700, 250);
		panel.addStyleName("margin-10");

		TextButton closeButton = new TextButton("Close");
		closeButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				dialogBoxGen.hide();
				functions.setDialogBoxForMessages();
			}
		});

		panel.setWidget(grid);
		panel.setButtonAlign(BoxLayoutPack.START);
		panel.addButton(closeButton);

		// key handlers -------------
		foc = new FocusPanel();
		foc.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					dialogBoxGen.hide();
					functions.setDialogBoxForMessages();
				}
			}
		});
		foc.add(panel);
		// --------------
		return foc;
	}
		
	public void asPopUpAskingForGoingBack(final TreeNode<BaseDto> node){
		//POP UP 
		if(toBeTransferredStore!=null)if(toBeTransferredStore.getAll().size()>0){
			MessageBox box = new MessageBox("Schedule files?", "");
			box.setPredefinedButtons(PredefinedButton.YES, PredefinedButton.NO);
			box.setIcon(MessageBox.ICONS.question());
			box.setWidth(500);
			box.setMessage("You are going back without scheduling the files you've "+
					"already dragged & dropped\n on the specific folder: "+
					folderDestination.getName()+" and they will be removed from the target.\n"+
					" Would you like to procceed without scheduling?");
			box.addHideHandler(new HideHandler() {
				@Override
				public void onHide(HideEvent event) {
					Dialog btn = (Dialog) event.getSource();
					String selectedB = btn.getHideButton().getText();
					if(selectedB.compareToIgnoreCase("YES")==0){
						functions.checkIfGoBack(node);										
					} 	
				}
			});
			box.show();
		}	
		else functions.checkIfGoBack(node);
	}
	public void showGuidedTour() {
		String title;
		List<String> images = new ArrayList<String>();
		List<String> textArrays = new ArrayList<String>();

		// Main
		title = "Main Form";
		images.add("gxt/images/tourGuide/main.png");
		textArrays
		.add("On the left side, there is the main form where the user "
				+ "can choose between several ways of scheduling such as 'direct',"
				+ "'manually' by giving the specific time instance and "
				+ "'periodically' by giving the frequency and the start time instance.");
		TourStep step1 = createTemplateStep(title, images, textArrays);

		// Toolbar
		images = new ArrayList<String>();
		textArrays = new ArrayList<String>();
		title = "Toolbar";
		images.add("gxt/images/tourGuide/toolbar.png");
		textArrays
		.add("On the right and up side, there is a toolbar where the user "
				+ "selects the source and the destination. Available source types are workspace, "
				+ "datasource and URI's. When the user chooses the source, several files from "
				+ "the source are shown in the source panel. The user can select the files he wants "
				+ "to transfer via dragging and dropping them in the target panel on the right. ");
		TourStep step2 = createTemplateStep(title, images, textArrays);

		// Source & Target Panel
		images = new ArrayList<String>();
		textArrays = new ArrayList<String>();
		title = "Source & Target Panel";
		images.add("gxt/images/tourGuide/panels.png");
		textArrays
		.add("Source panel is the panel which contains the folders and files where the "
				+ "user will select from. Targer panel is the one containing the files that user wants to "
				+ "schedule for transferring. Notice that this panel can contain only files"
				+ "and not folders."
				+ "Four buttons here: hide main in order to see the source and target panel full width"
				+ ", refresh tree so as to refresh the source tree,"
				+ "clear target to clean the target files and "
				+ "schedule for scheduling a transfer. ");
		TourStep step3 = createTemplateStep(title, images, textArrays);

		// Transfer Details
		images = new ArrayList<String>();
		textArrays = new ArrayList<String>();
		title = "Schedule Details";
		images.add("gxt/images/tourGuide/grid.png");
		textArrays
		.add("On the bottom, there is a form which shows information about the "
				+ "user's scheduled transfers. Information provided is: id, status, type and submitted date."
				+ "The user is able by double clicking on one of them to submit a command for this "
				+ "specific transfer. Available commands are: monitor, get outcomes and cancel. "
				+ "Two tool buttons here: refresh for getting again the information about the transfers"
				+ "and view agent statistics. ");
		TourStep step4 = createTemplateStep(title, images, textArrays);

		// step1.setTextVerticalAlignment(VerticalAlignment.ALIGN_MIDDLE);

		GCUBEGuidedTour gt = new GCUBEGuidedTour("gCube Scheduler Portlet",
				SchedulerPortlet.class.getName(), specificationLink, 780, 350,
				false, ThemeColor.BLUE);
		gt.addStep(step1);
		gt.addStep(step2);
		gt.addStep(step3);
		gt.addStep(step4);

		gt.openTour();
	}

	public TourStep createTemplateStep(final String header,
			final List<String> images, final List<String> textArrays) {
		if (images.size() != textArrays.size())
			return null;
		if (images == null || textArrays == null || header == null)
			return null;

		TourStep step = null;
		if (images.size() == 1) {
			step = new GCUBETemplate1Text1Image(true) {
				@Override
				public String setStepTitle() {
					return header;
				}

				@Override
				public String setStepImage() {
					return images.get(0);
				}

				@Override
				public String setStepBody() {
					return "<div style=\"line-height: 19px; padding: 10px; font-size: 14px; \">"
							+ "<div style=\"padding-bottom: 40px;\">"
							+ textArrays.get(0) + "</div>" + "</div>";
				}
			};
		} else if (images.size() == 2) {
			step = new GCUBETemplate2Text2Image(false) {
				@Override
				public String setStepTitle() {
					return header;
				}

				@Override
				public String setStepImage() {
					return images.get(0);
				}

				@Override
				public String setStepBody() {
					return "<div style=\"line-height: 19px; padding: 10px; font-size: 14px; \">"
							+ "<div style=\"padding-bottom: 40px;\">"
							+ textArrays.get(0) + "</div>" + "</div>";
				}

				@Override
				public String setStepOtherImage() {
					return images.get(1);
				}

				@Override
				public String setStepOtherBody() {
					return "<div style=\"line-height: 19px; padding: 10px; font-size: 14px; \">"
							+ "<div style=\"padding-bottom: 40px;\">"
							+ textArrays.get(1) + "</div>" + "</div>";
				}
			};
		}
		return step;
	}
	// asPopUpCreateNewFolder
	public Widget asPopUpCreateNewTreeSource(){
		FramedPanel panel = new FramedPanel();
		panel.setHeadingText("Create a New Tree Source");
		panel.setWidth(300);
		panel.setBodyStyle("background: none; padding: 5px");
		VerticalLayoutContainer p = new VerticalLayoutContainer();

		p.add(new Label(" "), new VerticalLayoutData(1, -1));

		if (newTreeSourceField == null) {
			newTreeSourceField = new TextField();
			newTreeSourceField.setAllowBlank(false);
		}
		p.add(new FieldLabel(newTreeSourceField, "New Tree Source"),
				new VerticalLayoutData(1, -1));
		newTreeSourceField.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					dialogBoxGen.hide();
					functions.setDialogBoxForMessages();
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					asPopUpCreateNewTreeSourceOnSelect();
				}
			}
		});

		// ************** buttons **************
		TextButton cancelButton = new TextButton("Cancel");
		cancelButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				dialogBoxGen.hide();
				functions.setDialogBoxForMessages();
			}
		});

		TextButton createButton = new TextButton("Create");
		createButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				asPopUpCreateNewTreeSourceOnSelect();
			}
		});

		panel.setWidget(p);
		panel.setButtonAlign(BoxLayoutPack.START);
		panel.addButton(cancelButton);
		panel.addButton(createButton);

		// key handlers -------------
		foc = new FocusPanel();
		foc.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					dialogBoxGen.hide();
					functions.setDialogBoxForMessages();
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					asPopUpCreateNewTreeSourceOnSelect();
				}
			}
		});
		foc.add(panel);
		// --------------
		return foc;
	}

	public void asPopUpCreateNewTreeSourceOnSelect(){
		if (newTreeSourceField.getCurrentValue() == null) {
			Info.display("","You must type a name! ");
			return;
		}

		dialogBoxGen.hide();
		functions.setDialogBoxForMessages();
		//add new folder in the destination tree
		rpCalls.createNewTreeSource();
	}

	// asPopUpCreateNewFolder
	public Widget asPopUpCreateNewFolder(){
		FramedPanel panel = new FramedPanel();
		panel.setHeadingText("Create a New Folder");
		panel.setWidth(300);
		panel.setBodyStyle("background: none; padding: 5px");
		VerticalLayoutContainer p = new VerticalLayoutContainer();

		p.add(new Label(" "), new VerticalLayoutData(1, -1));

		if (newFolderField == null) {
			newFolderField = new TextField();
			newFolderField.setAllowBlank(false);
		}
		p.add(new FieldLabel(newFolderField, "New Folder"),
				new VerticalLayoutData(1, -1));
		newFolderField.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					dialogBoxGen.hide();
					functions.setDialogBoxForMessages();
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					asPopUpCreateNewFolderOnSelect();
				}
			}
		});

		// ************** buttons **************
		TextButton cancelButton = new TextButton("Cancel");
		cancelButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				dialogBoxGen.hide();
				functions.setDialogBoxForMessages();
			}
		});

		TextButton createButton = new TextButton("Create");
		createButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				asPopUpCreateNewFolderOnSelect();
			}
		});

		panel.setWidget(p);
		panel.setButtonAlign(BoxLayoutPack.START);
		panel.addButton(cancelButton);
		panel.addButton(createButton);

		// key handlers -------------
		foc = new FocusPanel();
		foc.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					dialogBoxGen.hide();
					functions.setDialogBoxForMessages();
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					asPopUpCreateNewFolderOnSelect();
				}
			}
		});
		foc.add(panel);
		// --------------
		return foc;
	}

	public void asPopUpCreateNewFolderOnSelect(){
		if (newFolderField.getCurrentValue() == null) {
			Info.display("","You must type a name! ");
			return;
		}

		dialogBoxGen.hide();
		functions.setDialogBoxForMessages();
		//add new folder in the destination tree
		rpCalls.storeNewFolderInMongoDB();
	}

}
