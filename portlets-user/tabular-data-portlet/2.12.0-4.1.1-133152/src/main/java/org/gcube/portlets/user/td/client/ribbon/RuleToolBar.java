/**
 * 
 */
package org.gcube.portlets.user.td.client.ribbon;

import org.gcube.portlets.user.td.client.resource.TabularDataResources;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.RibbonEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.UIStateEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.RibbonType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.UIStateType;

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
public class RuleToolBar {

	private EventBus eventBus;
	private ToolBar toolBar;

	// Rules
	private TextButton ruleOpenButton;
	private TextButton ruleDeleteButton;
	private TextButton ruleActiveButton;
	private TextButton ruleShareButton;

	// On Column
	private TextButton ruleOnColumnNewButton;
	private TextButton ruleOnColumnApplyButton;

	// On Table
	private TextButton ruleOnTableNewButton;
	private TextButton ruleOnTableApplyButton;

	public RuleToolBar(EventBus eventBus) {
		this.eventBus = eventBus;
		build();
	}

	public ToolBar getToolBar() {
		return toolBar;
	}

	protected void build() {
		RuleToolBarMessages msgs = GWT.create(RuleToolBarMessages.class);

		toolBar = new ToolBar();
		toolBar.setSpacing(1);
		toolBar.setEnableOverflow(false);

		// Rules
		ButtonGroup rulesGroup = new ButtonGroup();
		rulesGroup.setId("Manage");
		rulesGroup.setStyleName("ribbon");
		rulesGroup.setHeadingText(msgs.ruleGroupHeadingText());
		// templateGroup.disable();
		toolBar.add(rulesGroup);

		FlexTable ruleLayout = new FlexTable();
		rulesGroup.add(ruleLayout);

		ruleOpenButton = new TextButton(msgs.ruleOpenButton(),
				TabularDataResources.INSTANCE.ruleOpen32());
		ruleOpenButton.enable();
		ruleOpenButton.setToolTip(msgs.ruleOpenButtonToolTip());
		ruleOpenButton.setScale(ButtonScale.LARGE);
		ruleOpenButton.setIconAlign(IconAlign.TOP);
		ruleOpenButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		ruleOpenButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.RULE_MODIFY));
			}
		});

		ruleLayout.setWidget(0, 0, ruleOpenButton);
		ruleLayout.getFlexCellFormatter().setRowSpan(0, 0, 2);

		ruleDeleteButton = new TextButton(msgs.ruleDeleteButton(),
				TabularDataResources.INSTANCE.ruleDelete32());
		ruleDeleteButton.enable();
		ruleDeleteButton.setToolTip(msgs.ruleDeleteButtonToolTip());
		ruleDeleteButton.setScale(ButtonScale.LARGE);
		ruleDeleteButton.setIconAlign(IconAlign.TOP);
		ruleDeleteButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		ruleDeleteButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.RULE_DELETE));
			}
		});

		ruleLayout.setWidget(0, 1, ruleDeleteButton);
		ruleLayout.getFlexCellFormatter().setRowSpan(0, 1, 2);

		ruleActiveButton = new TextButton(msgs.ruleActiveButton(),
				TabularDataResources.INSTANCE.ruleActive32());
		ruleActiveButton.disable();
		ruleActiveButton.setScale(ButtonScale.LARGE);
		ruleActiveButton.setIconAlign(IconAlign.TOP);
		ruleActiveButton.setToolTip(msgs.ruleActiveButtonToolTip());
		ruleActiveButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		ruleActiveButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.RULES_ACTIVE));
			}
		});

		ruleLayout.setWidget(0, 2, ruleActiveButton);
		ruleLayout.getFlexCellFormatter().setRowSpan(0, 2, 2);

		ruleShareButton = new TextButton(msgs.ruleShareButton(),
				TabularDataResources.INSTANCE.ruleShare32());
		ruleShareButton.enable();
		ruleShareButton.setScale(ButtonScale.LARGE);
		ruleShareButton.setIconAlign(IconAlign.TOP);
		ruleShareButton.setToolTip(msgs.ruleShareButtonToolTip());
		ruleShareButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		ruleShareButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.RULE_SHARE));
			}
		});

		ruleLayout.setWidget(0, 3, ruleShareButton);
		ruleLayout.getFlexCellFormatter().setRowSpan(0, 3, 2);

		cleanCells(ruleLayout.getElement());

		// Column Rules
		ButtonGroup ruleOnColumnGroup = new ButtonGroup();
		ruleOnColumnGroup.setId("On Column");
		ruleOnColumnGroup.setStyleName("ribbon");
		ruleOnColumnGroup.setHeadingText(msgs.ruleOnColumnGroupHeadingText());

		toolBar.add(ruleOnColumnGroup);

		FlexTable ruleOnColumnLayout = new FlexTable();
		ruleOnColumnGroup.add(ruleOnColumnLayout);

		ruleOnColumnNewButton = new TextButton(msgs.ruleOnColumnNewButton(),
				TabularDataResources.INSTANCE.ruleColumnAdd32());
		ruleOnColumnNewButton.enable();
		ruleOnColumnNewButton.setToolTip(msgs.ruleOnColumnNewButtonToolTip());
		ruleOnColumnNewButton.setScale(ButtonScale.LARGE);
		ruleOnColumnNewButton.setIconAlign(IconAlign.TOP);
		ruleOnColumnNewButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		ruleOnColumnNewButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.RULE_ON_COLUMN_NEW));
			}
		});

		ruleOnColumnLayout.setWidget(0, 0, ruleOnColumnNewButton);
		ruleOnColumnLayout.getFlexCellFormatter().setRowSpan(0, 0, 2);

		ruleOnColumnApplyButton = new TextButton(msgs.ruleOnColumnApplyButton(),
				TabularDataResources.INSTANCE.ruleColumnApply32());
		ruleOnColumnApplyButton.disable();
		ruleOnColumnApplyButton.setScale(ButtonScale.LARGE);
		ruleOnColumnApplyButton.setIconAlign(IconAlign.TOP);
		ruleOnColumnApplyButton.setToolTip(msgs.ruleOnColumnApplyButtonToolTip());
		ruleOnColumnApplyButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		ruleOnColumnApplyButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.RULE_ON_COLUMN_APPLY));
			}
		});

		ruleOnColumnLayout.setWidget(0, 1, ruleOnColumnApplyButton);
		ruleOnColumnLayout.getFlexCellFormatter().setRowSpan(0, 1, 2);

		cleanCells(ruleLayout.getElement());

		// Table Rules
		ButtonGroup ruleOnTableGroup = new ButtonGroup();
		ruleOnTableGroup.setId("On Table");
		ruleOnTableGroup.setStyleName("ribbon");
		ruleOnTableGroup.setHeadingText(msgs.ruleOnTableGroupHeadingText());

		toolBar.add(ruleOnTableGroup);

		FlexTable ruleOnTableLayout = new FlexTable();
		ruleOnTableGroup.add(ruleOnTableLayout);

		ruleOnTableNewButton = new TextButton(msgs.ruleOnTableNewButton(),
				TabularDataResources.INSTANCE.ruleTableAdd32());
		ruleOnTableNewButton.enable();
		ruleOnTableNewButton.setToolTip(msgs.ruleOnTableNewButtonToolTip());
		ruleOnTableNewButton.setScale(ButtonScale.LARGE);
		ruleOnTableNewButton.setIconAlign(IconAlign.TOP);
		ruleOnTableNewButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		ruleOnTableNewButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.RULE_ON_TABLE_NEW));
			}
		});

		ruleOnTableLayout.setWidget(0, 0, ruleOnTableNewButton);
		ruleOnTableLayout.getFlexCellFormatter().setRowSpan(0, 0, 2);

		ruleOnTableApplyButton = new TextButton(msgs.ruleOnTableApplyButton(),
				TabularDataResources.INSTANCE.ruleTableApply32());
		ruleOnTableApplyButton.disable();
		ruleOnTableApplyButton.setScale(ButtonScale.LARGE);
		ruleOnTableApplyButton.setIconAlign(IconAlign.TOP);
		ruleOnTableApplyButton.setToolTip(msgs.ruleOnTableApplyButtonToolTip());
		ruleOnTableApplyButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		ruleOnTableApplyButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.RULE_ON_TABLE_APPLY));
			}
		});

		ruleOnTableLayout.setWidget(0, 1, ruleOnTableApplyButton);
		ruleOnTableLayout.getFlexCellFormatter().setRowSpan(0, 1, 2);

		cleanCells(ruleLayout.getElement());

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

	public void setUI(UIStateEvent event) {
		UIStateType uiStateType = event.getUIStateType();
		try {
			switch (uiStateType) {
			case START:
				ruleOpenButton.enable();
				ruleDeleteButton.enable();
				ruleActiveButton.disable();
				ruleShareButton.enable();
				ruleOnColumnNewButton.enable();
				ruleOnColumnApplyButton.disable();
				ruleOnTableNewButton.enable();
				ruleOnTableApplyButton.disable();
				break;
			case TR_CLOSE:
			case TR_READONLY:
				ruleOpenButton.enable();
				ruleDeleteButton.enable();
				ruleActiveButton.disable();
				ruleShareButton.enable();
				ruleOnColumnNewButton.enable();
				ruleOnColumnApplyButton.disable();
				ruleOnTableNewButton.enable();
				ruleOnTableApplyButton.disable();
				break;
			case TR_OPEN:
			case TABLEUPDATE:
			case TABLECURATION:
				ruleOpenButton.enable();
				ruleDeleteButton.enable();
				ruleActiveButton.enable();
				ruleShareButton.enable();
				ruleOnColumnNewButton.enable();
				ruleOnColumnApplyButton.enable();	
				ruleOnTableNewButton.enable();
				ruleOnTableApplyButton.enable();
				break;
			case WIZARD_OPEN:
				ruleOpenButton.disable();
				ruleDeleteButton.disable();
				ruleActiveButton.disable();
				ruleShareButton.disable();
				ruleOnColumnNewButton.disable();
				ruleOnColumnApplyButton.disable();
				ruleOnTableNewButton.disable();
				ruleOnTableApplyButton.disable();
				break;
			default:
				break;
			}
		} catch (Exception e) {
			Log.error("setUI Error : " + e.getLocalizedMessage());
		}
	}
}
