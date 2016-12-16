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

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
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
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class CurationToolBar {

	private EventBus eventBus;
	private ToolBar toolBar;
	
	//Validation
	private TextButton validationsShowButton;
	private TextButton validationsDeleteButton;
	private TextButton duplicateDetectionButton;
	
	
	//Structure
	private TextButton tableTypeButton;
	private TextButton changePositionColumnButton;
	private TextButton changeColumnLabelButton;
	private TextButton columnTypeButton;
	private TextButton addColumnButton;
	private TextButton deleteColumnButton;
	private TextButton splitColumnButton;
	private TextButton mergeColumnButton;
	private TextButton denormalizeButton;
	private TextButton normalizeButton;
	
	
	//Helper
	private TextButton extractCodelistButton;
	private TextButton codelistMappingButton;	
	//private TextButton generateSummaryButton;
	

	public CurationToolBar(EventBus eventBus) {
		this.eventBus = eventBus;
		build();
	}

	public ToolBar getToolBar() {
		return toolBar;
	}

	protected void build() {
		CurationToolBarMessages msgs = GWT.create(CurationToolBarMessages.class);
	
		toolBar = new ToolBar();
		toolBar.setSpacing(1);
		toolBar.setEnableOverflow(false);

		// Validation
		ButtonGroup validationGroup = new ButtonGroup();
		validationGroup.setId("Validation");
		validationGroup.setStyleName("ribbon");
		validationGroup.setHeadingText(msgs.validationGroupHeadingText());
		validationGroup.enable();
		toolBar.add(validationGroup);

		FlexTable validationLayout = new FlexTable();
		validationGroup.add(validationLayout);
		
		validationsShowButton = new TextButton(msgs.validationsShowButton(),
				TabularDataResources.INSTANCE.validation32());
		validationsShowButton.disable();
		validationsShowButton.setScale(ButtonScale.LARGE);
		validationsShowButton.setIconAlign(IconAlign.TOP);
		validationsShowButton
				.setToolTip(msgs.validationsShowButtonToolTip());
		validationsShowButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		validationsShowButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(
						RibbonType.VALIDATIONS_SHOW));
			}
		});

		validationLayout.setWidget(0, 0, validationsShowButton);
		validationLayout.getFlexCellFormatter().setRowSpan(0, 0, 2);

		
		
	
		validationsDeleteButton = new TextButton(msgs.validationsDeleteButton(),
				TabularDataResources.INSTANCE.validationDelete32());
		validationsDeleteButton.disable();
		validationsDeleteButton.setScale(ButtonScale.LARGE);
		validationsDeleteButton.setIconAlign(IconAlign.TOP);
		validationsDeleteButton
				.setToolTip(msgs.validationsDeleteButtonToolTip());
		validationsDeleteButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		validationsDeleteButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(
						RibbonType.VALIDATIONS_DELETE));
			}
		});

		validationLayout.setWidget(0, 1, validationsDeleteButton);
		validationLayout.getFlexCellFormatter().setRowSpan(0, 1, 2);
		
		duplicateDetectionButton = new TextButton(msgs.duplicateDetectionButton(),
				TabularDataResources.INSTANCE.tableDuplicateRows32());
		duplicateDetectionButton.disable();
		duplicateDetectionButton.setScale(ButtonScale.LARGE);
		duplicateDetectionButton.setIconAlign(IconAlign.TOP);
		duplicateDetectionButton
				.setToolTip(msgs.duplicateDetectionButtonToolTip());
		duplicateDetectionButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		duplicateDetectionButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(
						RibbonType.DUPLICATE_DETECTION));
			}
		});

		validationLayout.setWidget(0, 2, duplicateDetectionButton);
		validationLayout.getFlexCellFormatter().setRowSpan(0, 2, 2);
		
		cleanCells(validationLayout.getElement());

		// Structure
		ButtonGroup structureGroup = new ButtonGroup();
		structureGroup.setId("Structure");
		structureGroup.setStyleName("ribbon");
		structureGroup.setHeadingText(msgs.structureGroupHeadingText());
		structureGroup.enable();
		toolBar.add(structureGroup);

		FlexTable structureLayout = new FlexTable();
		structureGroup.add(structureLayout);

		tableTypeButton = new TextButton(msgs.tableTypeButton(),
				TabularDataResources.INSTANCE.tableType32());
		tableTypeButton.setScale(ButtonScale.LARGE);
		tableTypeButton.disable();
		tableTypeButton.setIconAlign(IconAlign.TOP);
		tableTypeButton.setToolTip(msgs.tableTypeButtonToolTip());
		tableTypeButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		tableTypeButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.TABLE_TYPE));
			}
		});

		structureLayout.setWidget(0, 0, tableTypeButton);
		structureLayout.getFlexCellFormatter().setRowSpan(0, 0, 2);

		columnTypeButton = new TextButton(msgs.columnTypeButton(),
				TabularDataResources.INSTANCE.columnType32());
		columnTypeButton.disable();
		columnTypeButton.setScale(ButtonScale.LARGE);
		columnTypeButton.setIconAlign(IconAlign.TOP);
		columnTypeButton.setToolTip(msgs.columnTypeButtonToolTip());
		columnTypeButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		columnTypeButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.COLUMN_TYPE));
			}
		});

		structureLayout.setWidget(0, 1, columnTypeButton);
		structureLayout.getFlexCellFormatter().setRowSpan(0, 1, 2);

		
		
		changePositionColumnButton = new TextButton(msgs.changePositionColumnButton(),
				TabularDataResources.INSTANCE.columnReorder32());
		changePositionColumnButton.disable();
		changePositionColumnButton.setScale(ButtonScale.LARGE);
		changePositionColumnButton.setIconAlign(IconAlign.TOP);
		changePositionColumnButton.setToolTip(msgs.changePositionColumnButtonToolTip());
		changePositionColumnButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		changePositionColumnButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.COLUMN_POSITION));
			}
		});

		structureLayout.setWidget(0, 2, changePositionColumnButton);
		structureLayout.getFlexCellFormatter().setRowSpan(0, 2, 2);

		
		changeColumnLabelButton = new TextButton(msgs.changeColumnLabelButton(),
				TabularDataResources.INSTANCE.columnLabel32());
		changeColumnLabelButton.disable();
		changeColumnLabelButton.setScale(ButtonScale.LARGE);
		changeColumnLabelButton.setIconAlign(IconAlign.TOP);
		changeColumnLabelButton.setToolTip(msgs.changeColumnLabelButtonToolTip());
		changeColumnLabelButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		changeColumnLabelButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.COLUMN_LABEL));
			}
		});

		structureLayout.setWidget(0, 3, changeColumnLabelButton);
		structureLayout.getFlexCellFormatter().setRowSpan(0, 3, 2);

		
		addColumnButton = new TextButton(msgs.addColumnButton(),
				TabularDataResources.INSTANCE.columnAdd32());
		addColumnButton.disable();
		addColumnButton.setScale(ButtonScale.LARGE);
		addColumnButton.setIconAlign(IconAlign.TOP);
		addColumnButton.setToolTip(msgs.addColumnButtonToolTip());
		addColumnButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		addColumnButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.COLUMN_ADD));
			}
		});

		structureLayout.setWidget(0, 4, addColumnButton);
		structureLayout.getFlexCellFormatter().setRowSpan(0, 4, 2);

		deleteColumnButton = new TextButton(msgs.deleteColumnButton(),
				TabularDataResources.INSTANCE.columnDelete32());
		deleteColumnButton.disable();
		deleteColumnButton.setScale(ButtonScale.LARGE);
		deleteColumnButton.setIconAlign(IconAlign.TOP);
		deleteColumnButton.setToolTip(msgs.deleteColumnButtonToolTip());
		deleteColumnButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		deleteColumnButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.COLUMN_DELETE));
			}
		});

		structureLayout.setWidget(0, 5, deleteColumnButton);
		structureLayout.getFlexCellFormatter().setRowSpan(0, 5, 2);
		
		//
		splitColumnButton = new TextButton(msgs.splitColumnButton(),
				TabularDataResources.INSTANCE.columnSplit32());
		splitColumnButton.disable();
		splitColumnButton.setScale(ButtonScale.LARGE);
		splitColumnButton.setIconAlign(IconAlign.TOP);
		splitColumnButton.setToolTip(msgs.splitColumnButtonToolTip());
		splitColumnButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		splitColumnButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.COLUMN_SPLIT));
			}
		});

		structureLayout.setWidget(0, 6, splitColumnButton);
		structureLayout.getFlexCellFormatter().setRowSpan(0, 6, 2);

		mergeColumnButton = new TextButton(msgs.mergeColumnButton(),
				TabularDataResources.INSTANCE.columnMerge32());
		mergeColumnButton.disable();
		mergeColumnButton.setScale(ButtonScale.LARGE);
		mergeColumnButton.setIconAlign(IconAlign.TOP);
		mergeColumnButton.setToolTip(msgs.mergeColumnButtonToolTip());
		mergeColumnButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		mergeColumnButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.COLUMN_MERGE));
			}
		});

		structureLayout.setWidget(0, 7, mergeColumnButton);
		structureLayout.getFlexCellFormatter().setRowSpan(0, 7, 2);
		//
		
		denormalizeButton = new TextButton(msgs.denormalizeButton(),
				TabularDataResources.INSTANCE.tableDenormalize32());
		denormalizeButton.disable();
		denormalizeButton.setScale(ButtonScale.LARGE);
		denormalizeButton.setIconAlign(IconAlign.TOP);
		denormalizeButton.setToolTip(msgs.denormalizeButtonToolTip());
		denormalizeButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		denormalizeButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.DENORMALIZE));
			}
		});

		structureLayout.setWidget(0, 8, denormalizeButton);
		structureLayout.getFlexCellFormatter().setRowSpan(0, 8, 2);

		normalizeButton = new TextButton(msgs.normalizeButton(),
				TabularDataResources.INSTANCE.tableNormalize32());
		normalizeButton.disable();
		normalizeButton.setScale(ButtonScale.LARGE);
		normalizeButton.setIconAlign(IconAlign.TOP);
		normalizeButton.setToolTip(msgs.normalizeButtonToolTip());
		normalizeButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		normalizeButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.NORMALIZE));
			}
		});

		structureLayout.setWidget(0, 9, normalizeButton);
		structureLayout.getFlexCellFormatter().setRowSpan(0, 9, 2);
		
		cleanCells(structureLayout.getElement());

		// Helper
		ButtonGroup helperGroup = new ButtonGroup();
		helperGroup.setId("Helper");
		helperGroup.setStyleName("ribbon");
		helperGroup.setHeadingText(msgs.helperGroupHeadingText());
		helperGroup.enable();
		toolBar.add(helperGroup);

		FlexTable helperLayout = new FlexTable();
		helperGroup.add(helperLayout);
		
		
		
		extractCodelistButton = new TextButton(msgs.extractCodelistButton(),
				TabularDataResources.INSTANCE.codelist32());
		extractCodelistButton.disable();
		extractCodelistButton.setScale(ButtonScale.LARGE);
		extractCodelistButton.setIconAlign(IconAlign.TOP);
		extractCodelistButton.setToolTip(msgs.extractCodelistButtonToolTip());
		extractCodelistButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		extractCodelistButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.EXTRACT_CODELIST));
			}
		});

		helperLayout.setWidget(0, 0, extractCodelistButton);
		helperLayout.getFlexCellFormatter().setRowSpan(0, 0, 2);

		
		codelistMappingButton = new TextButton(msgs.codelistMappingButton(),
				TabularDataResources.INSTANCE.codelistMapping32());
		codelistMappingButton.disable();
		codelistMappingButton.setScale(ButtonScale.LARGE);
		codelistMappingButton.setIconAlign(IconAlign.TOP);
		codelistMappingButton.setToolTip(msgs.codelistMappingButtonToolTip());
		codelistMappingButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		codelistMappingButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.CODELIST_MAPPING));
			}
		});

		helperLayout.setWidget(0, 1, codelistMappingButton);
		helperLayout.getFlexCellFormatter().setRowSpan(0, 1, 2);
		
		/*
		generateSummaryButton = new TextButton("Generate Summary",
				TabularDataResources.INSTANCE.summary32());
		generateSummaryButton.disable();
		generateSummaryButton.setScale(ButtonScale.LARGE);
		generateSummaryButton.setIconAlign(IconAlign.TOP);
		generateSummaryButton.setToolTip("Generate Summary");
		generateSummaryButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		generateSummaryButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.GENERATESUMMARY));
			}
		});

		helperLayout.setWidget(0, 2, generateSummaryButton);
		helperLayout.getFlexCellFormatter().setRowSpan(0, 2, 2);
		*/
		cleanCells(helperLayout.getElement());

		eventBus.addHandler(UIStateEvent.TYPE,
				new UIStateEvent.UIStateHandler() {

					public void onUIState(UIStateEvent event) {
						setUI(event);

					}
				});

	}

	protected void cleanCells(Element elem) {
		NodeList<Element> tds = elem.<XElement> cast().select("td");
		for (int i = 0; i < tds.getLength(); i++) {
			Element td = tds.getItem(i);

			if (!td.hasChildNodes() && td.getClassName().equals("")) {
				td.removeFromParent();
			}
		}
	}

	public void setUI(UIStateEvent event) {
		UIStateType uiStateType = event.getUIStateType();
		try {
			switch (uiStateType) {
			case START:
				validationsShowButton.disable();
				validationsDeleteButton.disable();
				duplicateDetectionButton.disable();
				
				tableTypeButton.disable();
				changePositionColumnButton.disable();
				changeColumnLabelButton.disable();
				columnTypeButton.disable();
				addColumnButton.disable();
				deleteColumnButton.disable();
				splitColumnButton.disable();
				mergeColumnButton.disable();
				denormalizeButton.disable();
				normalizeButton.disable();
				
				extractCodelistButton.disable();
				codelistMappingButton.disable();
				//generateSummaryButton.disable();
				
				break;
			case TR_CLOSE:
			case TR_READONLY:
				validationsShowButton.disable();
				validationsDeleteButton.disable();
				duplicateDetectionButton.disable();
				
				tableTypeButton.disable();
				changePositionColumnButton.disable();
				changeColumnLabelButton.disable();
				columnTypeButton.disable();
				addColumnButton.disable();
				deleteColumnButton.disable();
				splitColumnButton.disable();
				mergeColumnButton.disable();
				denormalizeButton.disable();
				normalizeButton.disable();
				
				extractCodelistButton.disable();
				codelistMappingButton.disable();
				//generateSummaryButton.disable();
				break;
			case TR_OPEN:
			case TABLEUPDATE:
			case TABLECURATION:
				validationsShowButton.enable();
				validationsDeleteButton.enable();
				duplicateDetectionButton.enable();
				
				tableTypeButton.enable();
				changePositionColumnButton.enable();
				changeColumnLabelButton.enable();
				columnTypeButton.enable();
				addColumnButton.enable();
				deleteColumnButton.enable();
				splitColumnButton.enable();
				mergeColumnButton.enable();
				denormalizeButton.enable();
				normalizeButton.enable();
				
				
				TRId trId = event.getTrId();
				if (trId != null && trId.getTableType() != null
						&& trId.getTableType().compareTo("Codelist") == 0) {
					extractCodelistButton.disable();
					codelistMappingButton.enable();
				} else {
					extractCodelistButton.enable();
					codelistMappingButton.disable();
				}
				//generateSummaryButton.disable();
				break;
			case WIZARD_OPEN:
				validationsShowButton.disable();
				validationsDeleteButton.disable();
				duplicateDetectionButton.disable();
				
				tableTypeButton.disable();
				changePositionColumnButton.disable();
				changeColumnLabelButton.disable();
				columnTypeButton.disable();
				addColumnButton.disable();
				deleteColumnButton.disable();
				splitColumnButton.disable();
				mergeColumnButton.disable();
				denormalizeButton.disable();
				normalizeButton.disable();
				
				extractCodelistButton.disable();
				codelistMappingButton.disable();
				//generateSummaryButton.disable();
				break;
			default:
				break;
			}
		} catch (Exception e) {
			Log.error("setUI Error on Ribbon Curation: "
					+ e.getLocalizedMessage());
		}
	}

}
