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
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class AnalyseToolBar {

	private EventBus eventBus;
	private ToolBar toolBar;

	private TextButton chartCreateButton;
	private TextButton statisticalButton;
	private TextButton rstudioButton;

	private TextButton gisButton;

	
	public AnalyseToolBar(EventBus eventBus) {
		this.eventBus = eventBus;
		build();
	}

	public ToolBar getToolBar() {
		return toolBar;
	}

	protected void build() {
		AnalyseToolBarMessages msgs = GWT.create(AnalyseToolBarMessages.class);
		
		toolBar = new ToolBar();
		toolBar.setSpacing(1);
		toolBar.setEnableOverflow(false);

		// Charts Group
		ButtonGroup chartsGroup = new ButtonGroup();
		chartsGroup.setId("Charts");
		chartsGroup.setStyleName("ribbon");
		chartsGroup.setHeadingText(msgs.chartGroupHeadingText());
		chartsGroup.enable();
		toolBar.add(chartsGroup);

		FlexTable basicLayout = new FlexTable();
		chartsGroup.add(basicLayout);

		chartCreateButton = new TextButton(msgs.chartCreateButton(),
				TabularDataResources.INSTANCE.chartBar32());
		chartCreateButton.disable();
		chartCreateButton.setScale(ButtonScale.LARGE);
		chartCreateButton.setIconAlign(IconAlign.TOP);
		chartCreateButton.setToolTip(msgs.chartCreateButtonToolTip());
		chartCreateButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		chartCreateButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.CHART_CREATE));
			}
		});

		basicLayout.setWidget(0, 0, chartCreateButton);
		basicLayout.getFlexCellFormatter().setRowSpan(0, 0, 2);

		cleanCells(basicLayout.getElement());

		// Process
		ButtonGroup processGroup = new ButtonGroup();
		processGroup.setId("Process");
		processGroup.setStyleName("ribbon");
		processGroup.setHeadingText(msgs.processGroupHeadingText());
		processGroup.enable();
		toolBar.add(processGroup);

		FlexTable processLayout = new FlexTable();
		processGroup.add(processLayout);

		rstudioButton = new TextButton(msgs.rstudioButton(),
				TabularDataResources.INSTANCE.rstudio32());
		rstudioButton.disable();
		rstudioButton.setScale(ButtonScale.LARGE);
		rstudioButton.setIconAlign(IconAlign.TOP);
		rstudioButton.setToolTip(msgs.rstudioButtonToolTip());
		rstudioButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		rstudioButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.R_STUDIO));
			}
		});

		processLayout.setWidget(0, 0, rstudioButton);
		processLayout.getFlexCellFormatter().setRowSpan(0, 0, 2);

		statisticalButton = new TextButton(msgs.statisticalButton(),
				TabularDataResources.INSTANCE.statistical32());
		statisticalButton.disable();
		statisticalButton.setScale(ButtonScale.LARGE);
		statisticalButton.setIconAlign(IconAlign.TOP);
		statisticalButton.setToolTip(msgs.statisticalButtonToolTip());
		statisticalButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		statisticalButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.STATISTICAL));
			}
		});

		processLayout.setWidget(0, 1, statisticalButton);
		processLayout.getFlexCellFormatter().setRowSpan(0, 1, 2);

		/*
		 * spreadButton = new TextButton("SPREAD",
		 * TabularDataResources.INSTANCE.cog32()); spreadButton.disable();
		 * spreadButton.setScale(ButtonScale.LARGE);
		 * spreadButton.setIconAlign(IconAlign.TOP);
		 * spreadButton.setToolTip("SPREAD");
		 * spreadButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		 * spreadButton.addSelectHandler(new SelectHandler() {
		 * 
		 * public void onSelect(SelectEvent event) { // eventBus.fireEvent(new
		 * // RibbonEvent(RibbonType.TABLEAGGREAGETE)); } });
		 * 
		 * processLayout.setWidget(0, 2, spreadButton);
		 * processLayout.getFlexCellFormatter().setRowSpan(0, 2, 2);
		 */

		cleanCells(processLayout.getElement());

		// GIS
		ButtonGroup gisGroup = new ButtonGroup();
		gisGroup.setId("GIS");
		gisGroup.setStyleName("ribbon");
		gisGroup.setHeadingText(msgs.gisGroupHeadingText());
		toolBar.add(gisGroup);

		FlexTable gisLayout = new FlexTable();
		gisGroup.add(gisLayout);

		gisButton = new TextButton(msgs.gisButton(),
				TabularDataResources.INSTANCE.gis32());
		gisButton.setScale(ButtonScale.LARGE);
		gisButton.setIconAlign(IconAlign.TOP);
		gisButton.setToolTip(msgs.gisButtonToolTip());
		gisButton.disable();
		gisButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		gisButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.CREATE_GIS_MAP));
			}
		});

		gisLayout.setWidget(0, 0, gisButton);
		gisLayout.getFlexCellFormatter().setRowSpan(0, 0, 2);
		cleanCells(gisLayout.getElement());

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
				chartCreateButton.disable();
				gisButton.disable();
				statisticalButton.disable();
				rstudioButton.disable();
				break;
			case TR_CLOSE:
			case TR_READONLY:
				chartCreateButton.disable();
				gisButton.disable();
				statisticalButton.disable();
				rstudioButton.disable();
				break;
			case TR_OPEN:
			case TABLEUPDATE:
			case TABLECURATION:
				chartCreateButton.enable();
				gisButton.enable();
				statisticalButton.enable();
				rstudioButton.enable();
				break;
			case WIZARD_OPEN:
				chartCreateButton.disable();
				gisButton.disable();
				statisticalButton.disable();
				rstudioButton.disable();
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
