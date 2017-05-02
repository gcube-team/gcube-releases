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
public class TemplateToolBar {

	private EventBus eventBus;
	private ToolBar toolBar;

	// Template
	private TextButton templateNewButton;
	private TextButton templateOpenButton;
	private TextButton templateDeleteButton;
	private TextButton templateApplyButton;
	private TextButton templateShareButton;
	
	// Export
	private TextButton templateExportSDMXButton;
	

	public TemplateToolBar(EventBus eventBus) {
		this.eventBus = eventBus;
		build();
	}

	public ToolBar getToolBar() {
		return toolBar;
	}

	protected void build() {
		TemplateToolBarMessages msgs = GWT
				.create(TemplateToolBarMessages.class);

		toolBar = new ToolBar();
		toolBar.setSpacing(1);
		toolBar.setEnableOverflow(false);

		// Template
		ButtonGroup templateGroup = new ButtonGroup();
		templateGroup.setId("Manage");
		templateGroup.setStyleName("ribbon");
		templateGroup.setHeadingText(msgs.templateGroupHeadingText());
		// templateGroup.disable();
		toolBar.add(templateGroup);

		FlexTable templateLayout = new FlexTable();
		templateGroup.add(templateLayout);

		templateNewButton = new TextButton(msgs.templateNewButton(),
				TabularDataResources.INSTANCE.templateAdd32());
		templateNewButton.enable();
		templateNewButton.setToolTip(msgs.templateNewButtonToolTip());
		templateNewButton.setScale(ButtonScale.LARGE);
		templateNewButton.setIconAlign(IconAlign.TOP);
		templateNewButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		templateNewButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.TEMPLATE_NEW));
			}
		});

		templateLayout.setWidget(0, 0, templateNewButton);
		templateLayout.getFlexCellFormatter().setRowSpan(0, 0, 2);

		templateOpenButton = new TextButton(msgs.templateOpenButton(),
				TabularDataResources.INSTANCE.templateEdit32());
		templateOpenButton.enable();
		templateOpenButton.setToolTip(msgs.templateOpenButtonToolTip());
		templateOpenButton.setScale(ButtonScale.LARGE);
		templateOpenButton.setIconAlign(IconAlign.TOP);
		templateOpenButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		templateOpenButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.TEMPLATE_OPEN));
			}
		});

		templateLayout.setWidget(0, 1, templateOpenButton);
		templateLayout.getFlexCellFormatter().setRowSpan(0, 1, 2);

		templateDeleteButton = new TextButton(msgs.templateDeleteButton(),
				TabularDataResources.INSTANCE.templateDelete32());
		templateDeleteButton.enable();
		templateDeleteButton.setToolTip(msgs.templateDeleteButtonToolTip());
		templateDeleteButton.setScale(ButtonScale.LARGE);
		templateDeleteButton.setIconAlign(IconAlign.TOP);
		templateDeleteButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		templateDeleteButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.TEMPLATE_DELETE));
			}
		});

		templateLayout.setWidget(0, 2, templateDeleteButton);
		templateLayout.getFlexCellFormatter().setRowSpan(0, 2, 2);

		templateApplyButton = new TextButton(msgs.templateApplyButton(),
				TabularDataResources.INSTANCE.templateApply32());
		templateApplyButton.disable();
		templateApplyButton.setScale(ButtonScale.LARGE);
		templateApplyButton.setIconAlign(IconAlign.TOP);
		templateApplyButton.setToolTip(msgs.templateApplyButtonToolTip());
		templateApplyButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		templateApplyButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.TEMPLATE_APPLY));
			}
		});

		templateLayout.setWidget(0, 3, templateApplyButton);
		templateLayout.getFlexCellFormatter().setRowSpan(0, 3, 2);

		templateShareButton = new TextButton(msgs.templateShareButton(),
				TabularDataResources.INSTANCE.templateShare32());
		templateShareButton.enable();
		templateShareButton.setScale(ButtonScale.LARGE);
		templateShareButton.setIconAlign(IconAlign.TOP);
		templateShareButton.setToolTip(msgs.templateShareButton());
		templateShareButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		templateShareButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.TEMPLATE_SHARE));
			}
		});

		templateLayout.setWidget(0, 4, templateShareButton);
		templateLayout.getFlexCellFormatter().setRowSpan(0, 4, 2);

		cleanCells(templateLayout.getElement());

		// Export
		ButtonGroup exportGroup = new ButtonGroup();
		exportGroup.setStyleName("ribbon");
		exportGroup.setHeadingText(msgs.templateExportGroupHeadingText());
		toolBar.add(exportGroup);

		FlexTable exportLayout = new FlexTable();
		exportGroup.add(exportLayout);

		templateExportSDMXButton = new TextButton(msgs.templateExportSDMXButton(),
				TabularDataResources.INSTANCE.sdmx32());
		templateExportSDMXButton.disable();
		templateExportSDMXButton.setToolTip(msgs.templateExportSDMXButtonToolTip());
		templateExportSDMXButton.setScale(ButtonScale.LARGE);
		templateExportSDMXButton.setIconAlign(IconAlign.TOP);
		templateExportSDMXButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		templateExportSDMXButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.TEMPLATE_EXPORT_SDMX));
			}
		});

		exportLayout.setWidget(0, 0, templateExportSDMXButton);
		exportLayout.getFlexCellFormatter().setRowSpan(0, 0, 2);

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
				templateOpenButton.enable();
				templateNewButton.enable();
				templateDeleteButton.enable();
				templateApplyButton.disable();
				templateShareButton.enable();
				templateExportSDMXButton.disable();
				break;
			case TR_CLOSE:
			case TR_READONLY:
				templateOpenButton.enable();
				templateNewButton.enable();
				templateDeleteButton.enable();
				templateApplyButton.disable();
				templateShareButton.enable();
				templateExportSDMXButton.disable();
				break;
			case TR_OPEN:
			case TABLEUPDATE:
			case TABLECURATION:
				templateOpenButton.enable();
				templateNewButton.enable();
				templateDeleteButton.enable();
				templateApplyButton.enable();
				templateShareButton.enable();
				templateExportSDMXButton.enable();
				break;
			case WIZARD_OPEN:
				templateOpenButton.disable();
				templateNewButton.disable();
				templateDeleteButton.disable();
				templateApplyButton.disable();
				templateShareButton.disable();
				templateExportSDMXButton.disable();
				break;
			default:
				break;
			}
		} catch (Exception e) {
			Log.error("setUI Error : " + e.getLocalizedMessage());
		}
	}
}
