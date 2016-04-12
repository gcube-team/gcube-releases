package org.gcube.datatransfer.portlets.user.client;

import java.util.Date;
import java.util.List;

import org.gcube.datatransfer.portlets.user.client.obj.ComboForDestination;
import org.gcube.datatransfer.portlets.user.client.obj.ComboForSource;
import org.gcube.datatransfer.portlets.user.client.utils.Utils;
import org.gcube.datatransfer.portlets.user.shared.obj.BaseDto;
import org.gcube.datatransfer.portlets.user.shared.obj.FolderDto;
import org.gcube.datatransfer.portlets.user.shared.prop.BaseDtoProperties;
import org.hsqldb.rights.Right;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.util.DateWrapper;
import com.sencha.gxt.data.shared.IconProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.event.StoreAddEvent;
import com.sencha.gxt.data.shared.event.StoreAddEvent.StoreAddHandler;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent.DndDropHandler;
import com.sencha.gxt.dnd.core.client.TreeDragSource;
import com.sencha.gxt.dnd.core.client.TreeDropTarget;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.BeforeExpandItemEvent;
import com.sencha.gxt.widget.core.client.event.BeforeExpandItemEvent.BeforeExpandItemHandler;
import com.sencha.gxt.widget.core.client.event.CollapseEvent;
import com.sencha.gxt.widget.core.client.event.CollapseEvent.CollapseHandler;
import com.sencha.gxt.widget.core.client.event.DisableEvent;
import com.sencha.gxt.widget.core.client.event.DisableEvent.DisableHandler;
import com.sencha.gxt.widget.core.client.event.ExpandItemEvent;
import com.sencha.gxt.widget.core.client.event.ExpandItemEvent.ExpandItemHandler;
import com.sencha.gxt.widget.core.client.event.FocusEvent;
import com.sencha.gxt.widget.core.client.event.FocusEvent.FocusHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.ParseErrorEvent;
import com.sencha.gxt.widget.core.client.event.ParseErrorEvent.ParseErrorHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.DateField;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.TimeField;
import com.sencha.gxt.widget.core.client.form.validator.MinDateValidator;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeNode;
import com.sencha.gxt.examples.resources.client.images.ExampleImages;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class Panels extends Common{

	/*
	 * asWidgetLayout input: Nothing -- returns: Widget Sets the general Layout
	 * of the port-let
	 */
	public Widget asWidgetLayout() {
		// mainContainer takes the real width after resizing
		// because the width of it is in percents (100%)
		panelGeneral = new FramedPanel();
		panelGeneral.setHeadingText("Scheduler Service");
		panelGeneral.setBorders(true);
		// panelGeneral.setHeight("100%");

		if ((RootPanel.get().getOffsetHeight() - 150) < minGenHeight)
			panelGeneral.setHeight(minGenHeight);
		else {
			panelGeneral.setHeight((RootPanel.get().getOffsetHeight() - 150));
			// panelGeneral.setHeight("99%");
		}

		if (RootPanel.get("mainContainer").getOffsetWidth() < minGenWidth)
			panelGeneral.setWidth(minGenWidth);
		else
			panelGeneral.setWidth("99%");

		panelGeneral.setBodyStyle("background: none; padding: 0px");

		west = new VerticalPanel();
		east = new VerticalPanel();
		VerticalPanel southPanel = new VerticalPanel();
		south = new ContentPanel();
		southPanel.add(south);

		// Create a Dock Panel
		DockPanel dock = new DockPanel();
		// dock.setSpacing(4);
		dock.setHorizontalAlignment(DockPanel.ALIGN_CENTER);

		dock.add(southPanel, DockPanel.SOUTH);
		dock.add(west, DockPanel.WEST);
		dock.add(east, DockPanel.EAST);

		// Return the content
		dock.ensureDebugId("cwDockPanel");
		panelGeneral.add(dock);

		return panelGeneral;
	}

	/*
	 * asWidgetToolbar input: Nothing -- returns: Widget Creates the panel which
	 * provides the options for selecting files The user can choose between
	 * localFile-DataSource-URI
	 */
	public Widget asWidgetToolbar() {
		int width;
		if (west.getWidgetCount() > 0)
			width = totalWidth - 325;
		else
			width = totalWidth - 25;

		FramedPanel panel = new FramedPanel();
		panel.setHeadingHtml("Schedule Source & Destination");
		panel.setPixelSize(width, 100);
		// panel.addStyleName("margin-25");

		HorizontalLayoutContainer outer = new HorizontalLayoutContainer();
		VerticalLayoutContainer outer1 = new VerticalLayoutContainer();
		VerticalLayoutContainer outer2 = new VerticalLayoutContainer();

		// item 1
		if (combo1 == null) {
			combo1Label = new LabelToolItem("Select: ");
			combo1 = new ComboForSource(new LabelProvider<String>() {
				public String getLabel(String item) {
					return item.toString().substring(0, 1)
							+ item.toString().substring(1).toLowerCase();
				}
			});

			combo1.setEmptyText("Select Source ...");
			combo1.setTriggerAction(TriggerAction.ALL);
			// combo1.setToolTipConfig(functions.createAnchor("Select the Source Type"));

			combo1.setEditable(false);
			combo1.add(SOURCETYPE.TreeBased.toString());
			combo1.add(SOURCETYPE.Workspace.toString());
			combo1.add(SOURCETYPE.DataSource.toString());
			combo1.add(SOURCETYPE.URI.toString());
			combo1.add(SOURCETYPE.MongoDB.toString());
			combo1.add(SOURCETYPE.AgentSource.toString());
			// Add a handler to change the data source
			combo1.addFocusHandler(new FocusHandler() {
				public void onFocus(FocusEvent event) {
					dialogBoxGen = functions.createDialogBox(popups.asPopUpSourceType());
					dialogBoxGen.center();
					focusTimer.schedule(200);
					combo1.collapse();
				}
			});

			combo1.hide();
			showSourcesButton=new TextButton("Source");
			showSourcesButton.setBorders(true);
			//	showSourcesButton.setToolTipConfig(functions.createAnchor("Select source"));
			showSourcesButton.addSelectHandler(new SelectHandler() {
				public void onSelect(SelectEvent event) {
					dialogBoxGen = functions.createDialogBox(popups.asPopUpSourceType());
					dialogBoxGen.center();
					focusTimer.schedule(200);
				}
			});
		}

		toolbarSource = new ToolBar();
		toolbarSource.setBorders(true);
		toolbarSource.add(combo1Label);
		toolbarSource.add(showSourcesButton);
		outer1.add(toolbarSource);

		if(sourceAnchor==null)sourceAnchor=functions.createAnchor("");
		toolbarSource.setToolTipConfig(sourceAnchor);

		if (destCombo == null) {
			destLabel = new LabelToolItem("Select: ");
			destCombo = new ComboForDestination(new LabelProvider<String>() {
				public String getLabel(String item) {
					return item.toString().substring(0, 1)
							+ item.toString().substring(1).toLowerCase();
				}
			});

			destCombo.setTriggerAction(TriggerAction.ALL);
			// destCombo.setToolTipConfig(functions.createAnchor("Select the Destination Type"));
			destCombo.setEmptyText("Select Destination ...");
			destCombo.setEditable(false);
			destCombo.add(DESTTYPE.TreeBased.toString());
			destCombo.add(DESTTYPE.MongoDBStorage.toString());
			destCombo.add(DESTTYPE.DataStorage.toString());
			destCombo.add(DESTTYPE.AgentDest.toString());
			// Add a handler to change the data source
			destCombo.addFocusHandler(new FocusHandler() {
				public void onFocus(FocusEvent event) {
					dialogBoxGen = functions.createDialogBox(popups.asPopUpDestinationType());
					dialogBoxGen.center();
					focusTimer.schedule(200);
					destCombo.collapse();
				}
			});

			destCombo.hide();
			showDestinationsButton=new TextButton("Destinantion");
			showDestinationsButton.setBorders(true);
			//	showDestinationsButton.setToolTipConfig(functions.createAnchor("Select destination"));
			showDestinationsButton.addSelectHandler(new SelectHandler() {
				public void onSelect(SelectEvent event) {
					if(combo1.isTreeBased()){
						dialogBoxGen = functions.createDialogBox(popups.asPopUpTreeWriteSources());
					}
					else dialogBoxGen = functions.createDialogBox(popups.asPopUpDestinationType());
					dialogBoxGen.center();
					focusTimer.schedule(200);
				}
			});
		}
		ToolBar toolbarDest = new ToolBar();
		toolbarDest.setBorders(true);
		toolbarDest.add(destLabel);
		toolbarDest.add(showDestinationsButton);
		outer2.add(toolbarDest);

		if (destinationF == null) {
			destLabelF = new LabelToolItem("Dest Folder: ");
			destinationF = new TextField();
			destinationF.setValue(destinationFolder);
			// destinationF.setToolTipConfig(functions.createAnchor(""));
			destinationF.setAllowBlank(false);
			
			destinationF.disable();
		}

		toolbarDestF = new ToolBar();
		toolbarDestF.setBorders(true);
		toolbarDestF.add(destLabelF);
		toolbarDestF.add(destinationF);		

		if(destinationAnchor==null)	destinationAnchor=functions.createAnchor(destinationF.getCurrentValue());
		else destinationAnchor.setBodyText(destinationF.getCurrentValue());
		toolbarDestF.setToolTipConfig(destinationAnchor);
		outer2.add(toolbarDestF);

		outer.add(outer1, new HorizontalLayoutData(.5, 1));
		outer.add(outer2, new HorizontalLayoutData(.5, 1));
		panel.setWidget(outer);

		return panel;
	}

	/*
	 * asWidgetScheduler input: Nothing -- returns: Widget Creates the widget
	 * with the main form - the scheduler
	 */
	public Widget asWidgetScheduler() {
		FramedPanel panel = new FramedPanel();
		panel.setHeadingText("Main");
		panel.setPixelSize(300, 295);
		panel.setBodyStyle("background: none; padding: 5px");

		VerticalLayoutContainer p = new VerticalLayoutContainer();
		panel.add(p);

		// comboAgent
		if (comboAgent == null) {
			comboAgent = new SimpleComboBox<String>(
					new LabelProvider<String>() {
						public String getLabel(String item) {
							return item.toString().substring(0, 1)
									+ item.toString().substring(1)
									.toLowerCase();
						}
					});
			comboAgent.setTriggerAction(TriggerAction.ALL);
			comboAgent.setEditable(false);
			comboAgent.setEmptyText("Select Agent ...");
			// Add a handler to change the data source

			comboAgent.addFocusHandler(new FocusHandler() {
				public void onFocus(FocusEvent event) {
					dialogBoxGen = functions.createDialogBox(popups.asPopUpAgents(FolderToRetrieve.NONE));
					dialogBoxGen.center();
					focusTimer.schedule(200);
					comboAgent.collapse();
				}
			});
		}

		// CHANGED - decided to make it hidden
		// the agent will be selected from destination type
		// p.add(new FieldLabel(comboAgent, "Agent"), new VerticalLayoutData(1,
		// -1));

		// ResourceName
		if (ResourceName == null) {
			ResourceName = new TextField();
			ResourceName.setAllowBlank(false);
			ResourceName.setEmptyText("Enter username...");
			ResourceName.setValue(defaultResourceName);
			
			ResourceName.disable();
		}
		p.add(new FieldLabel(ResourceName, "Username"), new VerticalLayoutData(
				1, -1));
		// Scope
		if (scope == null) {
			scope = new TextField();
			scope.setAllowBlank(false);
			scope.setValue(defaultScope);
			
			scope.disable();
		}
		p.add(new FieldLabel(scope, "Scope"), new VerticalLayoutData(1, -1));
		// transferId
		transferId = new TextField();
		transferId.setAllowBlank(false);
		
		// CHANGED - decided to make it hidden
		// p.add(new FieldLabel(transferId, "Transfer id"), new
		// VerticalLayoutData(1, -1));
		transferId.disable();

		// force
		force = new CheckBox();
		force.setBoxLabel("force cancel");
		force.setValue(true);
		force.disable();
		// hp = new HorizontalPanel();
		// hp.add(force);
		// CHANGED - decided to make it hidden
		// p.add(new FieldLabel(force, "Force cancel"));

		// overwrite
		overwrite = new CheckBox();
		overwrite.setBoxLabel("Overwrite");
		overwrite.setValue(true);
		p.add(new FieldLabel(overwrite, "Overwrite"));
		// unzip
		unzip = new CheckBox();
		unzip.setBoxLabel("Unzip");
		unzip.setValue(false);
		// HorizontalPanel hp = new HorizontalPanel();
		p.add(new FieldLabel(unzip, "Unzip"));

		// date
		date = new DateField();
		date.addParseErrorHandler(new ParseErrorHandler() {
			public void onParseError(ParseErrorEvent event) {
				Info.display("Parse Error", event.getErrorValue()
						+ " could not be parsed as a date");
			}
		});

		

		final long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
		Date minDay = new Date();
		minDay.setTime(new Date().getTime() - MILLIS_IN_A_DAY);
		date.addValidator(new MinDateValidator(minDay));
		dateLabel = new FieldLabel(date, "Schedule Date");
		p.add(dateLabel, new VerticalLayoutData(1, -1));
		date.disable();
		// time
		time = new TimeField();
		time.addParseErrorHandler(new ParseErrorHandler() {
			public void onParseError(ParseErrorEvent event) {
				Info.display("Parse Error", event.getErrorValue()
						+ " could not be parsed as a valid time");
			}
		});
		time.setMinValue(new DateWrapper().clearTime().addHours(0).asDate());
		time.setMaxValue(new DateWrapper().clearTime().addHours(23)
				.addSeconds(1).asDate());
		timeLabel = new FieldLabel(time, "Schedule Time");
		p.add(timeLabel, new VerticalLayoutData(1, -1));
		// frequency
		frequency = new SimpleComboBox<String>(new LabelProvider<String>() {
			public String getLabel(String item) {
				return item.toString().substring(0, 1)
						+ item.toString().substring(1).toLowerCase();
			}
		});
		time.disable();

		frequency.setTriggerAction(TriggerAction.ALL);
		frequency.setEditable(false);
		frequency.add("perMinute");
		frequency.add("perHour");
		frequency.add("perDay");
		frequency.add("perWeek");
		frequency.add("perMonth");
		frequency.add("perYear");
		frequency.setAllowBlank(true);
		frequency.setForceSelection(true);
		
		frequency.disable();
		p.add(new FieldLabel(frequency, "Frequency"), new VerticalLayoutData(1,
				-1));

		// typeofSchedule
		typeOfSchedule = new SimpleComboBox<String>(
				new LabelProvider<String>() {
					public String getLabel(String item) {
						return item.toString().substring(0, 1)
								+ item.toString().substring(1).toLowerCase();
					}
				});
		typeOfSchedule.setTriggerAction(TriggerAction.ALL);
		typeOfSchedule.setEditable(false);
		typeOfSchedule.add("direct");
		typeOfSchedule.add("manually scheduled");
		typeOfSchedule.add("periodically scheduled");
		typeOfSchedule.addCollapseHandler(new CollapseHandler() {
			public void onCollapse(CollapseEvent event) {
				if (typeOfSchedule == null)
					return;

				String v = typeOfSchedule.getCurrentValue() == null ? "nothing"
						: typeOfSchedule.getCurrentValue();
		//		Info.display("Selected", "You selected " + v);

				if (typeOfSchedule.getCurrentValue() == null)
					return;

				if (typeOfSchedule.getCurrentValue().compareTo("direct") == 0) {
					date.disable();
					time.disable();
					frequency.disable();
				} else if (typeOfSchedule.getCurrentValue().compareTo(
						"manually scheduled") == 0) {
					date.enable();
					time.enable();
					dateLabel.setText("Schedule Date");
					timeLabel.setText("Schedule Time");
					frequency.disable();
				} else if (typeOfSchedule.getCurrentValue().compareTo(
						"periodically scheduled") == 0) {
					date.enable();
					time.enable();
					dateLabel.setText("Schedule Start Date");
					timeLabel.setText("Schedule Start Time");
					frequency.enable();
				}
			}
		});
		typeOfSchedule.addDisableHandler(new DisableHandler() {
			public void onDisable(DisableEvent event) {
				date.disable();
				time.disable();
				frequency.disable();
			}
		});
		typeOfSchedule.setAllowBlank(true);
		typeOfSchedule.setForceSelection(true);
		typeOfSchedule.enable();

		p.add(new FieldLabel(typeOfSchedule, "Type of schedule"),
				new VerticalLayoutData(1, -1));

		// commands
		commands = new SimpleComboBox<String>(new LabelProvider<String>() {
			public String getLabel(String item) {
				return item.toString().substring(0, 1)
						+ item.toString().substring(1).toLowerCase();
			}
		});
		commands.setTriggerAction(TriggerAction.ALL);

		commands.setEditable(false);
		commands.add("schedule");
		commands.add("monitor");
		commands.add("get transfers");
		commands.add("get outcomes");
		commands.add("cancel");

		commands.setAllowBlank(true);
		commands.setForceSelection(true);

		// CHANGED - decided to make it hidden
		// p.add(new FieldLabel(commands, "Commands"), new VerticalLayoutData(1,
		// -1));

		// ************** buttons **************
		TextButton submitButton = new TextButton("Submit");
		submitButton
		.setToolTipConfig(functions.createAnchor("Submit the above selected command"));

		TextButton cancelButton = new TextButton("Cancel");

		cancelButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				RootPanel.get("scheduler").clear();
			}
		});
		submitButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				// check for the fields of name-scope-and schedule
				if (functions.checkFields())
					return;

				if (commands.getCurrentValue().compareTo("schedule") == 0) {
					portlet.redrawEast();
					combo1.setValue(SOURCETYPE.DataSource.toString());
				} else if (commands.getCurrentValue().compareTo("monitor") == 0) {
					rpCalls.monitor();
				} else if (commands.getCurrentValue()
						.compareTo("get transfers") == 0) {
					rpCalls.getTransfers();
				} else if (commands.getCurrentValue().compareTo("get outcomes") == 0) {
					rpCalls.getOutcomes();
				} else if (commands.getCurrentValue().compareTo("cancel") == 0) {
					rpCalls.cancel();
				}
			}
		});
		panel.setWidget(p);
		panel.setButtonAlign(BoxLayoutPack.START);
		// CHANGED - decided to make it hidden
		// panel.addButton(submitButton);
		return panel;
	}




	/*
	 * asWidgetListFiles input: Nothing -- returns: Widget Creates a panel where
	 * there is a source tree and a destination target. The client just drops
	 * and drags the files he wants to transfer
	 */
	public Widget asWidgetListFiles() {
		int width;
		if (west.getWidgetCount() > 0)
			width = totalWidth - 325;
		else
			width = totalWidth - 15;

		final ContentPanel cp1 = new ContentPanel();
		cp1.setPixelSize(width - 15, 140);
		cp1.addStyleName("margin-8");

		VerticalLayoutContainer outer = new VerticalLayoutContainer();
		vpListFiles = new HorizontalLayoutContainer();

		//********* getting SOURCE folder ************//
		if (folderSource == null) {
			if (folderResSource == null) {
				functions.printMsgInDialogBox("Error : retuned folderResSource=null");
				folderSource = functions.makeFolder("Empty source");
			} else {
				// deSeriarilize folderSource
				folderSource = (FolderDto) FolderDto
						.createSerializer()
						.deSerialize(folderResSource,
								"org.gcube.datatransfer.portlets.user.shared.obj.FolderDto");

				// setting some values used for the addTheGoBackOption function
				if (combo1.isDatasource()
						&& currentDataSourcePath != null) {
					if (currentDataSourcePath.compareTo("") == 0 ||
							currentDataSourcePath.compareTo("./") == 0){
						selectedDatasourcePath = folderSource.getName().replaceFirst(
								".//", "./");
						lastSelectedDataSourceFolderName=selectedDatasourcePath;
					}
				} else if (combo1.isWorkspace()) {
					if (idWorkspaceRoot == null && callingWorkspaceRoot){
						idWorkspaceRoot = folderSource.getIdInWorkspace();
					}
				} else if (combo1.isMongoDB()) {
					if (currentMongoDBSourcePath.compareTo("/") == 0){
						selectedMongoDBSourcePath = folderSource.getName()
								.replaceFirst(".//", "./");
						lastSelectedMongoDBFolderName=selectedMongoDBSourcePath;
					}
				} else if (combo1.isAgentSource()) {
					if (currentAgentSourcePath.compareTo("") == 0){
						selectedAgentSourcePath = folderSource.getName()
								.replaceFirst(".//", "./");
						lastSelectedAgentFolderName=selectedAgentSourcePath;
					}
				}
				folderSource = functions.addTheGoBackOption(true);
			}
		}
		// ******* source *******
		sourceStore = new TreeStore<BaseDto>(BaseDtoProperties.key);
		sourceStore.addSubTree(0, folderSource.getChildren());

		sourceTree = new Tree<BaseDto, String>(sourceStore,
				baseDtoProp.shortname()) {
			protected boolean hasChildren(BaseDto model) {
				return super.hasChildren(model);
			}

			@Override
			protected void onDoubleClick(Event event) {
				TreeNode<BaseDto> node = findNode(event.getEventTarget()
						.<Element> cast());
				// we override double click only in case of back
				if (node.getModel().getShortname().compareTo("<< Back") == 0) {
					if (combo1.isDatasource()) {
						lastSelectedDataSourceFolderName = node.getModel().getName();
						rpCalls.getDatasourceFolder(selectedDataSourceId, node
								.getModel().getName());
					} else if (combo1.isWorkspace()) {
						lastSelectedFolderId = node.getModel()
								.getIdInWorkspace();
						neededParent = true;
						rpCalls.getWorkspaceFolder(node.getModel().getIdInWorkspace(),
								true);
					} else if (combo1.isMongoDB()) {
						lastSelectedMongoDBFolderName = node.getModel()
								.getName();
						rpCalls.getMongoDBFolder(node.getModel().getName());
					} else if (combo1.isAgentSource()) {
						lastSelectedAgentFolderName = node.getModel().getName();
						rpCalls.getAgentFolder(node.getModel().getName());
					}
				} else if(combo1.isTreeBased()){
					if(forTreeElementDescription==null){
						forTreeElementDescription = new Dialog();
						forTreeElementDescription.setPredefinedButtons(PredefinedButton.OK);
						forTreeElementDescription.setBodyStyleName("pad-text");
						forTreeElementDescription.getBody().addClassName("pad-text");
						forTreeElementDescription.setHideOnButtonClick(true);
						forTreeElementDescription.setWidth(300);
					}	
					forTreeElementDescription.clear();
					forTreeElementDescription.setHeadingText(node.getModel().getName());
					String[] parts=node.getModel().getLink().split("--");
					if(parts==null || parts.length<1)Info.display("TreeSource","no extra info");//nothing
					else if(parts.length==1){
						forTreeElementDescription.add(new Label("id="+parts[0]));
						forTreeElementDescription.show();
					}
					else if(parts.length>1){
						forTreeElementDescription.add(new Label("id="+parts[0]+" - cardinality="+parts[1]));
						forTreeElementDescription.show();
					}
				}
				else{
					super.onDoubleClick(event);					
				}
			}
		};
	//	sourceTree.getStyle().setLeafIcon(ExampleImages.INSTANCE.text());
		sourceTree.setIconProvider(Utils.iconProviderForSource);
		sourceTree.getElement().getStyle().setBackgroundColor("white");
		sourceTree.addBeforeExpandHandler(new BeforeExpandItemHandler<BaseDto>() {
			public void onBeforeExpand(
					BeforeExpandItemEvent<BaseDto> event) {
				TreeNode<BaseDto> node = sourceTree.findNode(event
						.getItem());
				sourceTree.refresh(node.getModel());

				if (event.getItem().getChildren() == null)
					return;
				// if the folderSource is empty ..
				if (sourceTree.getStore()
						.getChildCount(event.getItem()) == 0
						|| (sourceTree.getStore().getChildCount(
								event.getItem()) == 1 && sourceTree
								.getStore()
								.getFirstChild(event.getItem())
								.getName().compareTo("") == 0)) {

					if (combo1.isDatasource()) {
						lastSelectedDataSourceFolderName = event.getItem()
								.getName();
						rpCalls.getDatasourceFolder(selectedDataSourceId, event
								.getItem().getName());
					} else if (combo1.isWorkspace()) {
						lastSelectedFolderId = event.getItem()
								.getIdInWorkspace();
						if (event.getItem().getShortname()
								.compareTo("<< Back") == 0) {
							neededParent = true;
							rpCalls.getWorkspaceFolder(event.getItem()
									.getIdInWorkspace(), true);
						} else {
							// functions.printMsgInDialogBox("id="+folderSource.getIdInWorkspace()+"name="+folderSource.getName());
							neededParent = false;
							rpCalls.getWorkspaceFolder(event.getItem()
									.getIdInWorkspace(), false);
						}
					} else if (combo1.isMongoDB()) {
						lastSelectedMongoDBFolderName = node.getModel()
								.getName();
						rpCalls.getMongoDBFolder(node.getModel().getName());
					} else if (combo1.isAgentSource()) {
						lastSelectedAgentFolderName = node.getModel()
								.getName();
						rpCalls.getAgentFolder(node.getModel().getName());
					}
				}
			}
		});
		sourceTree.addExpandHandler(new ExpandItemHandler<BaseDto>() {
			public void onExpand(ExpandItemEvent<BaseDto> event) {
				// Nothing
			}
		});
		new TreeDragSource<BaseDto>(sourceTree);

		//********* getting DESTINATION folder ************//
		if (folderDestination == null) {
			targetStore=null;
			toBeTransferredStore.clear();
			toBeTransferredStore.commitChanges();
			if (folderResDestination == null) {
				//	functions.printMsgInDialogBox("Error : retuned folderResDestination=null");
				folderDestination = functions.makeFolder("Empty destination");
			} else {
				// deSeriarilize folderSource
				folderDestination = (FolderDto) FolderDto
						.createSerializer()
						.deSerialize(folderResDestination,
								"org.gcube.datatransfer.portlets.user.shared.obj.FolderDto");

				// setting some values used for the addTheGoBackOption function
				if (destCombo.isDataStorage()
						&& currentDataStoragePath != null) {
					if (currentDataStoragePath.compareTo("") == 0 || 
							currentDataStoragePath.compareTo("./") == 0){
						destinationF.setValue("./");
						destinationAnchor.setBodyText("./");
						selectedDatastoragePath = folderDestination.getName().replaceFirst(
								".//", "./");
						lastSelectedDatastorageFolderName=selectedDatastoragePath;
					}
				}
				//else if (destCombo.getCurrentValue().compareTo(SOURCETYPE.Workspace.toString()) == 0) {
				//	if (idWorkspaceRootDest == null && callingWorkspaceRootDest)
				//		idWorkspaceRootDest = folderDestination.getIdInWorkspaceDest();
				//}
				else if (destCombo.isMongoDBStorage()) {
					if (currentMongoDBDestinationPath.compareTo("/") == 0){
						destinationF.setValue("/");
						destinationAnchor.setBodyText("/");
						selectedMongoDBDestinationPath = folderDestination.getName()
								.replaceFirst(".//", "./");
						lastSelectedMongoDBFolderDestName=selectedMongoDBDestinationPath;
					}
				} else if (destCombo.isAgentDest()) {
					if (currentAgentDestinationPath.compareTo("") == 0){
						destinationF.setValue("./");
						destinationAnchor.setBodyText("./");
						selectedAgentDestinationPath = folderDestination.getName()
								.replaceFirst(".//", "./");
						lastSelectedAgentFolderDestName=selectedAgentDestinationPath;
					}
				}
				folderDestination = functions.addTheGoBackOption(false);
			}
		}
		// ******* target *******
		if (targetStore == null) {
			toBeTransferredStore=new TreeStore<BaseDto>(BaseDtoProperties.key);

			targetStore = new TreeStore<BaseDto>(BaseDtoProperties.key);
			targetStore.addSubTree(0, folderDestination.getChildren());
			targetStore.addStoreAddHandler(new StoreAddHandler<BaseDto>() {
				@Override
				public void onAdd(StoreAddEvent<BaseDto> event) {
					//Info.display("dest path",folderDestination.getName());
					List<BaseDto> list = event.getItems();
					toBeTransferredStore.add(event.getItems());

					int num = 0;
					String links = ""; // only for optional print
					String names = ""; // only for optional print
					for(BaseDto tmp : list){
						if (tmp.getName().compareTo("") == 0)
							continue;
						if (tmp.getChildren() != null) {
							if (tmp.getChildren().size() == 1
									&& tmp.getChildren().get(0).getData()
									.getName().compareTo("") == 0) {
								toBeTransferredStore.remove(tmp);
								toBeTransferredStore.commitChanges();
								targetStore.remove(tmp);
								targetStore.commitChanges();
								Info.display("",
										"You can drag and drop files but not whole folders!!");
								sourceStore = null;
								continue;
							}
						}
						links = tmp.getLink() + "\n"; // only for optional
						// print
						names = tmp.getName() + "\n"; // only for optional
						// print
						num++;
					}
					if(list.size()>1 && combo1.isTreeBased()){
						toBeTransferredStore.clear();
						toBeTransferredStore.commitChanges();
						targetStore.clear();
						targetStore.commitChanges();
						Info.display("",
								"You can transfer one tree collection at a time!!");
						sourceStore = null;
					}

					if (sourceStore == null) {
						portlet.redrawEast();
					} 
				}
			});

			targetTree = new Tree<BaseDto, String>(targetStore,	baseDtoProp.shortname()) {
				protected boolean hasChildren(BaseDto model) {
					return super.hasChildren(model);
				}
				@Override
				protected void onDoubleClick(Event event) {
					final TreeNode<BaseDto> node = findNode(event.getEventTarget()
							.<Element> cast());
					// we override double click only in case of back
					if (node.getModel().getShortname().compareTo("<< Back") == 0) {
						popups.asPopUpAskingForGoingBack(node);
					}
					else if(combo1.isTreeBased()){
						if(forTreeElementDescription==null){
							forTreeElementDescription = new Dialog();
							forTreeElementDescription.setPredefinedButtons(PredefinedButton.OK);
							forTreeElementDescription.setBodyStyleName("pad-text");
							forTreeElementDescription.getBody().addClassName("pad-text");
							forTreeElementDescription.setHideOnButtonClick(true);
							forTreeElementDescription.setWidth(300);
						}	
						forTreeElementDescription.clear();
						forTreeElementDescription.setHeadingText(node.getModel().getName());
						String[] parts=node.getModel().getLink().split("--");
						if(parts==null || parts.length<1)Info.display("TreeSource","no extra info");//nothing
						else if(parts.length==1){
							forTreeElementDescription.add(new Label("id="+parts[0]));
							forTreeElementDescription.show();
						}
						else if(parts.length>1){
							forTreeElementDescription.add(new Label("id="+parts[0]+" - cardinality="+parts[1]));
							forTreeElementDescription.show();
						}
					}
					else super.onDoubleClick(event);
				}
			};
			
			targetTree.setIconProvider(Utils.iconProviderForTarget);
			targetTree.getElement().getStyle().setBackgroundColor("white");
			targetTree.addBeforeExpandHandler(new BeforeExpandItemHandler<BaseDto>() {
				public void onBeforeExpand(
						BeforeExpandItemEvent<BaseDto> event) {
					TreeNode<BaseDto> node = targetTree.findNode(event
							.getItem());
					targetTree.refresh(node.getModel());

					if (node.getModel().getShortname().compareTo("<< Back") == 0) {
						popups.asPopUpAskingForGoingBack(node);
						return;
					}

					if (event.getItem().getChildren() == null)
						return;
					// if the folderSource is empty ..
					if (targetTree.getStore()
							.getChildCount(event.getItem()) == 0
							|| (targetTree.getStore().getChildCount(
									event.getItem()) == 1 && targetTree
									.getStore()
									.getFirstChild(event.getItem())
									.getName().compareTo("") == 0)) {

						String value = node.getModel().getName();
						destinationF.setValue(value);
						destinationAnchor.setBodyText(value);
						if (destCombo.isDataStorage()) {
							lastSelectedDatastorageFolderName = event.getItem().getName();
							rpCalls.getDatastorageFolder(selectedDataStorageId, event.getItem().getName());
						} 
						//	else if (destCombo.getCurrentValue().compareTo(SOURCETYPE.Workspace.toString()) == 0) {
						//		lastSelectedFolderDestId = event.getItem().getIdInWorkspace();
						//		if (event.getItem().getShortname().compareTo("<< Back") == 0) {
						//			neededParentDest = true;
						//			rpCalls.getWorkspaceFolder(event.getItem().getIdInWorkspace(), true);
						//		} else {
						//			// functions.printMsgInDialogBox("id="+folderSource.getIdInWorkspace()+"name="+folderSource.getName());
						//			neededParentDest = false;
						//			rpCalls.getWorkspaceFolder(event.getItem().getIdInWorkspace(), false);
						//		}
						//	} 
						else if (destCombo.isMongoDBStorage()) {
							lastSelectedMongoDBFolderDestName = node.getModel().getName();
							rpCalls.getMongoDBFolderDest(node.getModel().getName());
						} else if (destCombo.isAgentDest()) {
							destinationF.setValue("."+destinationF.getCurrentValue());
							destinationAnchor.setBodyText("."+destinationF.getCurrentValue());
							lastSelectedAgentFolderDestName = node.getModel().getName();
							rpCalls.getAgentFolderDest(node.getModel().getName());
						}
					}
				}
			});

			new TreeDragSource<BaseDto>(targetTree);
			targetTreeDropTarget = new TreeDropTarget<BaseDto>(targetTree);		
			targetTreeDropTarget.setAllowSelfAsSource(true);
			targetTreeDropTarget.addDropHandler(new DndDropHandler() {
				public void onDrop(DndDropEvent event) {
				}
			});
		}

		vpListFiles.add(sourceTree, new HorizontalLayoutData(.5, 1));
		vpListFiles.add(targetTree, new HorizontalLayoutData(.5, 1));
		vpListFiles.setBorders(true);
		sourceTree.setBorders(true);
		targetTree.setBorders(true);

		// *********** buttons ************
		showSchedulerButton = new TextButton("Show Main");
		showSchedulerButton
		.setToolTipConfig(functions.createAnchor("Showing the Main Panel"));
		hideSchedulerButton = new TextButton("Hide Main");
		hideSchedulerButton
		.setToolTipConfig(functions.createAnchor("Hiding the Main Panel"));

		sendButton = new TextButton("Schedule");
		// sendButton.setToolTipConfig(functions.createAnchor("Schedule and Transfer the files you have selected"));

		TextButton clearTargetButton = new TextButton("Clear Target");
		clearTargetButton
		.setToolTipConfig(functions.createAnchor("Clear the files you have already selected"));
		
		
		TextButton refreshSourceButton = new TextButton("Refresh source");
		refreshSourceButton.setToolTipConfig(functions.createAnchor("Refresh the source tree"));
		
		TextButton resetTargetButton = new TextButton("Reset target");
		resetTargetButton.setToolTipConfig(functions.createAnchor("Reset and refresh the target tree"));
		
		showSchedulerButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				west.clear();
				west.add(panels.asWidgetScheduler());
				// default values
				ResourceName.setText(defaultResourceName);
				scope.setText(defaultScope);
				portlet.redrawEast();
				showSchedulerButton.hide();
				hideSchedulerButton.show();
			}
		});
		hideSchedulerButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				west.clear();
				portlet.redrawEast();
				hideSchedulerButton.hide();
				showSchedulerButton.show();
			}
		});
		if (west.getWidgetCount() < 1) {
			showSchedulerButton.show();
			hideSchedulerButton.hide();
		} else {
			showSchedulerButton.hide();
			hideSchedulerButton.show();
		}

		sendButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				if (combo1.isWorkspace()) {
					dialogBoxGen = functions.createDialogBox(popups.asPopUpAuthenticationWorkspace());
					dialogBoxGen.center();
					focusTimer.schedule(200);
				} else {
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
					onResetTarget();
				}
			}
		});

		//not used anymore ... (reset target instead because now there is browsing in the target panel)
		clearTargetButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				targetStore.clear();
				targetStore.commitChanges();
				toBeTransferredStore.clear();
				toBeTransferredStore.commitChanges();

				sourceStore = null;
				portlet.redrawEast();
				sourceTree.collapseAll();
			}
		});
		refreshSourceButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				onRefreshSource();
			}
		});

		resetTargetButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				onResetTarget();
			}
		});
		if(makeNewFolder==null){
			makeNewFolder = new TextButton("New target folder");
			makeNewFolder.setToolTipConfig(functions.createAnchor("Create a new folder in the destination"));
			makeNewFolder.addSelectHandler(new SelectHandler() {
				public void onSelect(SelectEvent event) {
					dialogBoxGen = functions.createDialogBox(popups.asPopUpCreateNewFolder());
					dialogBoxGen.center();
					focusTimer.schedule(200);
				}
			});
			makeNewFolder.hide();
		}
		if(deleteCurrentFolder==null){
			deleteCurrentFolder = new TextButton("Delete target folder");
			deleteCurrentFolder.setToolTipConfig(functions.createAnchor("Delete the current folder in the destination"));
			deleteCurrentFolder.addSelectHandler(new SelectHandler() {
				public void onSelect(SelectEvent event) {
					MessageBox box = new MessageBox("Delete Current Folder?", "");
					box.setPredefinedButtons(PredefinedButton.YES, PredefinedButton.NO);
					box.setIcon(MessageBox.ICONS.question());
					box.setWidth(500);
					box.setMessage("You are about to delete the current folder: "+
							folderDestination.getName()+"\n"+
							"(including all the subfiles and subfolders)\n"+
							" Would you like to procceed?");
					box.addHideHandler(new HideHandler() {
						@Override
						public void onHide(HideEvent event) {
							Dialog btn = (Dialog) event.getSource();
							String selectedB = btn.getHideButton().getText();
							if(selectedB.compareToIgnoreCase("YES")==0){
								rpCalls.deleteFolderInMongoDB();										
							} 	
						}
					});
					box.show();
				}
			});
			deleteCurrentFolder.hide();
		}
		if(makeNewTreeSource==null){
			makeNewTreeSource = new TextButton("New tree source");
			makeNewTreeSource.setToolTipConfig(functions.createAnchor("Create a new tree source"));
			makeNewTreeSource.addSelectHandler(new SelectHandler() {
				public void onSelect(SelectEvent event) {
					dialogBoxGen = functions.createDialogBox(popups.asPopUpCreateNewTreeSource());
					dialogBoxGen.center();
					focusTimer.schedule(200);
				}
			});
			makeNewTreeSource.hide();
		}
		if(deleteCurrentTreeSource==null){
			deleteCurrentTreeSource = new TextButton("Delete target source");
			deleteCurrentTreeSource.setToolTipConfig(functions.createAnchor("Delete the current source in the destination"));
			deleteCurrentTreeSource.addSelectHandler(new SelectHandler() {
				public void onSelect(SelectEvent event) {
					MessageBox box = new MessageBox("Delete Current Tree Source?", "");
					box.setPredefinedButtons(PredefinedButton.YES, PredefinedButton.NO);
					box.setIcon(MessageBox.ICONS.question());
					box.setWidth(500);
					box.setMessage("You are about to delete the current tree source");//+
					//							": "+
					//							folderDestination.getName()+"\n"+
					//							"(including all the subfiles and subfolders)\n"+
					//							" Would you like to procceed?");
					box.addHideHandler(new HideHandler() {
						@Override
						public void onHide(HideEvent event) {
							Dialog btn = (Dialog) event.getSource();
							String selectedB = btn.getHideButton().getText();
							if(selectedB.compareToIgnoreCase("YES")==0){
								rpCalls.deleteTreeSource();										
							} 	
						}
					});
					box.show();
				}
			});
			deleteCurrentTreeSource.hide();
		}
		outer.add(vpListFiles, new VerticalLayoutData(1, 1));
		cp1.setWidget(outer);

		FramedPanel form = new FramedPanel();
		form.setHeaderVisible(false);
		// form.setWidth(width);
		form.setPixelSize(width, 197);

		VerticalLayoutContainer p = new VerticalLayoutContainer();
		p.add(cp1);

		form.add(p);
		form.setButtonAlign(BoxLayoutPack.START);
		form.addButton(hideSchedulerButton);
		form.addButton(showSchedulerButton);
		form.addButton(refreshSourceButton);
		//form.addButton(clearTargetButton);
		form.addButton(resetTargetButton);
		if(makeNewFolder==null || deleteCurrentFolder==null);
		else{
			form.addButton(makeNewFolder);       //new one (from toolbar widget)
			form.addButton(deleteCurrentFolder);  //new one (from toolbar widget)
		}
		if(makeNewTreeSource==null || deleteCurrentTreeSource==null);
		else{
			form.addButton(makeNewTreeSource);       //new one (from toolbar widget)
			form.addButton(deleteCurrentTreeSource);  //new one (from toolbar widget)
		}
		form.addButton(sendButton);


		// cp1.setHeadingText(folderSource.getShortname());
		cp1.setHeadingHtml(functions.createHeader(width));
		return form;
	}
	
	public void onRefreshSource(){
		if (combo1 == null)
			return;
		else if (combo1.getCurrentValue() == null)
			return;

		if (combo1.isTreeBased()) {
			rpCalls.getTreeReadSources(false);
		}
		if (combo1.isDatasource()
				&& lastSelectedDataSourceFolderName != null) {
			rpCalls.getDatasourceFolder(selectedDataSourceId,
					lastSelectedDataSourceFolderName);
		} 
		else if (combo1.isWorkspace()
				&& lastSelectedFolderId != null) {
			rpCalls.getWorkspaceFolder(lastSelectedFolderId, neededParent);
		}
		else if (combo1.isMongoDB()
				&& lastSelectedMongoDBFolderName != null) {
			rpCalls.getMongoDBFolder(lastSelectedMongoDBFolderName);
		}
		else if (combo1.isAgentSource()
				&& lastSelectedAgentFolderName != null) {
			rpCalls.getAgentFolder(lastSelectedAgentFolderName);
		}
		else if (combo1.isURI()) {
			if(loadingIconForSource==null)loadingIconForSource=functions.createLoadingIcon();
			Timer stopLoadingIconTimer = new Timer() {
				@Override
				public void run() {
					functions.stopLoadingIcon(loadingIconForSource);
					portlet.redrawEast();
				}
			};					
			functions.startLoadingIcon(sourceTree,loadingIconForSource);
			stopLoadingIconTimer.schedule(1000);					
		}
	}
	public void onResetTarget(){
		if (destCombo == null)
			return;
		else if (destCombo.getCurrentValue() == null)
			return;

		if (destCombo.isTreeBased()) {
			//rpCalls.getTreeWriteSources(true);
			targetStore.clear();
			targetStore.commitChanges();
			onRefreshSource();
		}
		if (destCombo.isDataStorage()
				&& lastSelectedDatastorageFolderName != null) {
			rpCalls.getDatastorageFolder(selectedDataStorageId,
					lastSelectedDatastorageFolderName);
		}
		//	else if (destCombo.getCurrentValue().compareTo(SOURCETYPE.Workspace.toString()) == 0
		//		&& lastSelectedFolderIdDest != null) {
		//		rpCalls.getWorkspaceFolder(lastSelectedFolderIdDest, neededParentDest);
		//	}
		else if (destCombo.isMongoDBStorage()
				&& lastSelectedMongoDBFolderDestName != null) {
			rpCalls.getMongoDBFolderDest(lastSelectedMongoDBFolderDestName);
		}
		else if (destCombo.isAgentDest()
				&& lastSelectedAgentFolderDestName != null) {
			rpCalls.getAgentFolderDest(lastSelectedAgentFolderDestName);
		}
	}
}
