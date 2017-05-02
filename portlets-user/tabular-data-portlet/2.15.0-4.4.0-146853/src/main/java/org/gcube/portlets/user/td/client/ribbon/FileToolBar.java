/**
 * 
 */
package org.gcube.portlets.user.td.client.ribbon;

import org.gcube.portlets.user.td.client.resource.TabularDataResources;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.RibbonEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.UIStateEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.RibbonType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.UIStateType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.TableType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.ButtonArrowAlign;
import com.sencha.gxt.cell.core.client.ButtonCell.ButtonScale;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.button.ButtonGroup;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class FileToolBar {
	private FileToolBarMessages msgs;
	private EventBus eventBus;
	private ToolBar toolBar;

	private TextButton openButton;
	private TextButton cloneButton;
	private TextButton closeButton;
	private TextButton shareButton;

	private TextButton deleteButton;
	private TextButton propertiesButton;

	private TextButton importCSVButton;
	// private TextButton importJSONButton;
	private TextButton importSDMXButton;
	private TextButton exportSDMXButton;
	private TextButton exportCSVButton;
	private TextButton exportJSONButton;

	private TextButton timelineButton;
	private TextButton backgroundButton;

	// History
	private TextButton historyButton;
	private TextButton undoButton;

	// Help
	private TextButton helpButton;
	private TextButton languageButton;
	// private TextButton logsButton;
	// private TextButton testButton;

	// Language Menu
	private MenuItem enItem;
	private MenuItem itItem;
	private MenuItem esItem;

	public FileToolBar(EventBus eventBus) {
		this.eventBus = eventBus;
		msgs = GWT.create(FileToolBarMessages.class);
		build();
	}

	public ToolBar getToolBar() {
		return toolBar;
	}

	protected void build() {

		toolBar = new ToolBar();
		toolBar.setSpacing(1);
		toolBar.setEnableOverflow(false);

		// File
		ButtonGroup fileGroup = new ButtonGroup();
		//fileGroup.setId("Tabular Resource");
		fileGroup.setStyleName("ribbon");
		fileGroup.setHeadingText(msgs.fileGroupHeadingText());
		toolBar.add(fileGroup);

		FlexTable fileLayout = new FlexTable();
		fileLayout.setStyleName("ribbon");
		fileGroup.add(fileLayout);

		openButton = new TextButton(msgs.openButton(),
				TabularDataResources.INSTANCE.trOpen32());
		//openButton.setId("openButton");
		openButton.setScale(ButtonScale.LARGE);
		openButton.setIconAlign(IconAlign.TOP);
		openButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		openButton.setToolTip(msgs.openButtonToolTip());

		openButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.OPEN));
			}
		});

		fileLayout.setWidget(0, 0, openButton);
		fileLayout.getFlexCellFormatter().setRowSpan(0, 0, 2);

		closeButton = new TextButton(msgs.closeButton(),
				TabularDataResources.INSTANCE.trClose32());
		closeButton.disable();
		closeButton.setScale(ButtonScale.LARGE);
		closeButton.setIconAlign(IconAlign.TOP);
		closeButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		closeButton.setToolTip(msgs.closeButtonToolTip());

		closeButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.CLOSE));
			}
		});

		fileLayout.setWidget(0, 1, closeButton);
		fileLayout.getFlexCellFormatter().setRowSpan(0, 1, 2);

		cloneButton = new TextButton(msgs.cloneButton(),
				TabularDataResources.INSTANCE.clone32());
		cloneButton.disable();
		cloneButton.setScale(ButtonScale.LARGE);
		cloneButton.setIconAlign(IconAlign.TOP);
		cloneButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		cloneButton.setToolTip(msgs.cloneButtonToolTip());

		cloneButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.CLONE));
			}
		});

		fileLayout.setWidget(0, 2, cloneButton);
		fileLayout.getFlexCellFormatter().setRowSpan(0, 2, 2);

		shareButton = new TextButton(msgs.shareButton(),
				TabularDataResources.INSTANCE.trShare32());
		shareButton.disable();
		//shareButton.setId("shareButton");
		shareButton.setScale(ButtonScale.LARGE);
		shareButton.setIconAlign(IconAlign.TOP);
		shareButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		shareButton.setToolTip(msgs.shareButtonToolTip());

		shareButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.SHARE));
			}
		});

		fileLayout.setWidget(0, 3, shareButton);
		fileLayout.getFlexCellFormatter().setRowSpan(0, 3, 2);

		deleteButton = new TextButton(msgs.deleteButton(),
				TabularDataResources.INSTANCE.delete());
		deleteButton.disable();
		//deleteButton.setId("closeButton");
		deleteButton.setToolTip(msgs.deleteButtonToolTip());

		fileLayout.setWidget(0, 4, deleteButton);
		deleteButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.DELETE));
			}
		});

		propertiesButton = new TextButton(msgs.propertiesButton(),
				TabularDataResources.INSTANCE.properties());
		propertiesButton.disable();
		//propertiesButton.setId("propertiesButton");
		propertiesButton.setToolTip(msgs.propertiesButtonToolTip());

		fileLayout.setWidget(1, 4, propertiesButton);
		propertiesButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.PROPERTIES));
			}
		});

		cleanCells(fileLayout.getElement());

		// Import
		ButtonGroup importGroup = new ButtonGroup();
		//importGroup.setId("Import");
		importGroup.setStyleName("ribbon");
		importGroup.setHeadingText(msgs.importGroupHeadingText());
		toolBar.add(importGroup);

		FlexTable importLayout = new FlexTable();
		importGroup.add(importLayout);

		importSDMXButton = new TextButton(msgs.importSDMXButton(),
				TabularDataResources.INSTANCE.sdmx32());
		importSDMXButton.setScale(ButtonScale.LARGE);
		importSDMXButton.setIconAlign(IconAlign.TOP);
		importSDMXButton.setToolTip(msgs.importSDMXButtonToolTip());
		importSDMXButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		importSDMXButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.IMPORT_SDMX));
			}
		});

		importLayout.setWidget(0, 0, importSDMXButton);
		importLayout.getFlexCellFormatter().setRowSpan(0, 0, 2);

		importCSVButton = new TextButton(msgs.importCSVButton(),
				TabularDataResources.INSTANCE.csv32());
		importCSVButton.setScale(ButtonScale.LARGE);
		importCSVButton.setIconAlign(IconAlign.TOP);
		importCSVButton.setToolTip(msgs.importCSVButtonToolTip());
		importCSVButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		importCSVButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.IMPORT_CSV));
			}
		});
		importLayout.setWidget(0, 1, importCSVButton);
		importLayout.getFlexCellFormatter().setRowSpan(0, 1, 2);

		/*
		 * importJSONButton = new TextButton("JSON",
		 * TabularDataResources.INSTANCE.json()); importJSONButton.disable();
		 * importJSONButton.setToolTip("Import table from JSON source");
		 * importJSONButton.addSelectHandler(new SelectHandler() {
		 * 
		 * public void onSelect(SelectEvent event) { eventBus.fireEvent(new
		 * RibbonEvent(RibbonType.IMPORT_JSON)); } }); importLayout.setWidget(1,
		 * 1, importJSONButton);
		 */

		cleanCells(importLayout.getElement());

		// Export
		ButtonGroup exportGroup = new ButtonGroup();
		//exportGroup.setId("Export");
		exportGroup.setStyleName("ribbon");
		exportGroup.setHeadingText(msgs.exportGroupHeadingText());
		// exportGroup.disable();
		toolBar.add(exportGroup);

		FlexTable exportLayout = new FlexTable();
		exportGroup.add(exportLayout);

		exportSDMXButton = new TextButton(msgs.exportSDMXButton(),
				TabularDataResources.INSTANCE.sdmx32());
		exportSDMXButton.disable();
		exportSDMXButton.setToolTip(msgs.exportSDMXButtonToolTip());
		exportSDMXButton.setScale(ButtonScale.LARGE);
		exportSDMXButton.setIconAlign(IconAlign.TOP);
		exportSDMXButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		exportSDMXButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.EXPORT_SDMX));
			}
		});

		exportLayout.setWidget(0, 0, exportSDMXButton);
		exportLayout.getFlexCellFormatter().setRowSpan(0, 0, 2);

		exportCSVButton = new TextButton(msgs.exportCSVButton(),
				TabularDataResources.INSTANCE.csv());
		exportCSVButton.disable();
		exportCSVButton.setToolTip(msgs.exportCSVButtonToolTip());
		exportCSVButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.EXPORT_CSV));
			}
		});
		exportLayout.setWidget(0, 2, exportCSVButton);

		exportJSONButton = new TextButton(msgs.exportJSONButton(),
				TabularDataResources.INSTANCE.json());
		exportJSONButton.disable();
		exportJSONButton.setToolTip(msgs.exportJSONButtonToolTip());
		exportJSONButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.EXPORT_JSON));
			}
		});
		exportLayout.setWidget(1, 2, exportJSONButton);
		cleanCells(exportLayout.getElement());

		// TimeLine (Tasks)
		ButtonGroup taskGroup = new ButtonGroup();
		//taskGroup.setId("TasksStatus");
		taskGroup.setStyleName("ribbon");
		taskGroup.setHeadingText(msgs.taskGroupHeadingText());
		toolBar.add(taskGroup);

		FlexTable taskLayout = new FlexTable();
		taskGroup.add(taskLayout);

		timelineButton = new TextButton(msgs.timelineButton(),
				TabularDataResources.INSTANCE.timeline32());
		timelineButton.disable();
		timelineButton.setToolTip(msgs.timelineButtonToolTip());
		timelineButton.setScale(ButtonScale.LARGE);
		timelineButton.setIconAlign(IconAlign.TOP);
		timelineButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		timelineButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.TIMELINE));
			}
		});

		taskLayout.setWidget(0, 0, timelineButton);
		taskLayout.getFlexCellFormatter().setRowSpan(0, 0, 2);

		backgroundButton = new TextButton(msgs.backgroundButton(),
				TabularDataResources.INSTANCE.basketBackground32());
		backgroundButton.enable();
		backgroundButton.setToolTip(msgs.backgroundButtonToolTip());
		backgroundButton.setScale(ButtonScale.LARGE);
		backgroundButton.setIconAlign(IconAlign.TOP);
		backgroundButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		backgroundButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.BACKGROUND_TASKS));
			}
		});

		taskLayout.setWidget(0, 1, backgroundButton);
		taskLayout.getFlexCellFormatter().setRowSpan(0, 1, 2);

		cleanCells(taskLayout.getElement());

		// History Group
		ButtonGroup historyGroup = new ButtonGroup();
		//historyGroup.setId("History");
		historyGroup.setStyleName("ribbon");
		historyGroup.setHeadingText(msgs.historyGroupHeadingText());
		historyGroup.enable();
		toolBar.add(historyGroup);

		FlexTable historyLayout = new FlexTable();
		historyGroup.add(historyLayout);

		historyButton = new TextButton(msgs.historyButton(),
				TabularDataResources.INSTANCE.history32());
		historyButton.disable();
		historyButton.setScale(ButtonScale.LARGE);
		historyButton.setIconAlign(IconAlign.TOP);
		historyButton.setToolTip(msgs.historyButtonToolTip());
		historyButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		historyButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.HISTORY));
			}
		});

		historyLayout.setWidget(0, 0, historyButton);
		historyLayout.getFlexCellFormatter().setRowSpan(0, 0, 2);

		undoButton = new TextButton(msgs.undoButton(),
				TabularDataResources.INSTANCE.discard32());
		undoButton.disable();
		undoButton.setScale(ButtonScale.LARGE);
		undoButton.setIconAlign(IconAlign.TOP);
		undoButton.setToolTip(msgs.undoButtonToolTip());
		undoButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		undoButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.UNDO));
			}
		});
		historyLayout.setWidget(0, 1, undoButton);
		historyLayout.getFlexCellFormatter().setRowSpan(0, 1, 2);

		cleanCells(historyLayout.getElement());

		// Help
		ButtonGroup helpGroup = new ButtonGroup();
		//helpGroup.setId("Help");
		helpGroup.setStyleName("ribbon");
		helpGroup.setHeadingText(msgs.helpGroupHeadingText());
		toolBar.add(helpGroup);

		FlexTable helpLayout = new FlexTable();
		helpGroup.add(helpLayout);

		languageButton = new TextButton(msgs.languageButton(),
				TabularDataResources.INSTANCE.language32());
		languageButton.enable();
		languageButton.setScale(ButtonScale.LARGE);
		languageButton.setIconAlign(IconAlign.TOP);
		languageButton.setToolTip(msgs.languageButtonToolTip());
		languageButton.setArrowAlign(ButtonArrowAlign.RIGHT);
		languageButton.setMenu(createLanguageMenu());

		helpLayout.setWidget(0, 0, languageButton);
		helpLayout.getFlexCellFormatter().setRowSpan(0, 0, 2);

		helpButton = new TextButton(msgs.helpButton(),
				TabularDataResources.INSTANCE.help32());
		helpButton.enable();
		helpButton.setToolTip(msgs.helpButtonToolTip());
		helpButton.setScale(ButtonScale.LARGE);
		helpButton.setIconAlign(IconAlign.TOP);
		helpButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		helpButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.HELP));
			}
		});

		helpLayout.setWidget(0, 1, helpButton);
		helpLayout.getFlexCellFormatter().setRowSpan(0, 1, 2);

		/*
		 * logsButton = new TextButton("Logs",
		 * TabularDataResources.INSTANCE.logs32()); logsButton.enable();
		 * logsButton.setToolTip("Show Logs");
		 * logsButton.setScale(ButtonScale.LARGE);
		 * logsButton.setIconAlign(IconAlign.TOP);
		 * logsButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		 * logsButton.addSelectHandler(new SelectHandler() {
		 * 
		 * public void onSelect(SelectEvent event) { eventBus.fireEvent(new
		 * RibbonEvent(RibbonType.LOGS)); } });
		 * 
		 * helpLayout.setWidget(0, 1, logsButton);
		 * helpLayout.getFlexCellFormatter().setRowSpan(0, 1, 2);
		 */

		/*
		 * testButton = new TextButton("Test",
		 * TabularDataResources.INSTANCE.test32()); testButton.disable();
		 * testButton.setToolTip("Test");
		 * testButton.setScale(ButtonScale.LARGE);
		 * testButton.setIconAlign(IconAlign.TOP);
		 * testButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		 * testButton.addSelectHandler(new SelectHandler() {
		 * 
		 * public void onSelect(SelectEvent event) { eventBus.fireEvent(new
		 * RibbonEvent(RibbonType.TEST)); } });
		 * 
		 * helpLayout.setWidget(0, 2, testButton);
		 * helpLayout.getFlexCellFormatter().setRowSpan(0, 2, 2);
		 */

		cleanCells(helpLayout.getElement());

		eventBus.addHandler(UIStateEvent.TYPE,
				new UIStateEvent.UIStateHandler() {

					public void onUIState(UIStateEvent event) {
						setUI(event);

					}
				});

	}

	private void cleanCells(Element elem) {
		NodeList<Element> tds = elem.<XElement> cast().select("td");
		for (int i = 0; i < tds.getLength(); i++) {
			Element td = tds.getItem(i);

			if (!td.hasChildNodes() && td.getClassName().equals("")) {
				td.removeFromParent();
			}
		}
	}

	private Menu createLanguageMenu() {
		Menu menuReplace = new Menu();
		enItem = new MenuItem(msgs.english());
		// TabularDataResources.INSTANCE.sflagEN());
		itItem = new MenuItem(msgs.italian());
		// TabularDataResources.INSTANCE.sflagIT());
		esItem = new MenuItem(msgs.spanish());
		// TabularDataResources.INSTANCE.sflagES());

		enItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.LANGUAGE_EN));

			}
		});

		itItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.LANGUAGE_IT));

			}
		});

		esItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.LANGUAGE_ES));

			}
		});

		menuReplace.add(enItem);
		menuReplace.add(esItem);
		menuReplace.add(itItem);
		return menuReplace;
	}

	public void setUI(UIStateEvent event) {
		UIStateType uiStateType = event.getUIStateType();
		try {
			switch (uiStateType) {
			case START:
				openButton.enable();
				cloneButton.disable();
				closeButton.disable();
				shareButton.disable();
				deleteButton.disable();
				propertiesButton.disable();
				importCSVButton.enable();
				// importJSONButton.disable();
				importSDMXButton.enable();
				exportCSVButton.disable();
				exportJSONButton.disable();
				exportSDMXButton.disable();
				timelineButton.disable();
				backgroundButton.enable();
				historyButton.disable();
				undoButton.disable();
				// testButton.disable();
				break;
			case TR_CLOSE:
			case TR_READONLY:
				openButton.enable();
				cloneButton.disable();
				closeButton.disable();
				shareButton.disable();
				deleteButton.disable();
				propertiesButton.disable();
				importCSVButton.enable();
				// importJSONButton.disable();
				importSDMXButton.enable();
				exportCSVButton.disable();
				exportJSONButton.disable();
				exportSDMXButton.disable();
				timelineButton.disable();
				backgroundButton.enable();
				historyButton.disable();
				undoButton.disable();
				// testButton.disable();
				break;
			case TR_OPEN:
			case TABLEUPDATE:
			case TABLECURATION:
				openButton.enable();
				cloneButton.enable();
				closeButton.enable();
				shareButton.enable();
				deleteButton.enable();
				propertiesButton.enable();
				importCSVButton.enable();
				// importJSONButton.disable();
				importSDMXButton.enable();
				TRId trId = event.getTrId();

				if (trId != null
						&& trId.getTableTypeName() != null
						&& (trId.getTableTypeName().compareTo(
								TableType.CODELIST.getTableTypeLabel()) == 0 || trId
								.getTableTypeName().compareTo(
										TableType.DATASET.getTableTypeLabel()) == 0)) {
					exportSDMXButton.enable();
				} else {
					exportSDMXButton.disable();
				}
				exportCSVButton.enable();
				exportJSONButton.enable();
				timelineButton.enable();
				backgroundButton.enable();
				historyButton.enable();
				undoButton.enable();
				// testButton.enable();
				break;
			case WIZARD_OPEN:
				openButton.disable();
				cloneButton.disable();
				closeButton.disable();
				shareButton.disable();
				deleteButton.disable();
				propertiesButton.disable();
				importCSVButton.disable();
				// importJSONButton.disable();
				importSDMXButton.disable();
				exportCSVButton.disable();
				exportJSONButton.disable();
				exportSDMXButton.disable();
				timelineButton.disable();
				backgroundButton.disable();
				historyButton.disable();
				undoButton.disable();
				// testButton.disable();
				break;
			default:
				break;
			}
		} catch (Exception e) {
			Log.error("setUI Error : " + e.getLocalizedMessage());
		}
	}
}
