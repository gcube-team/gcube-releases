package org.gcube.portlets.user.td.expressionwidget.client.help;

import org.gcube.portlets.user.td.expressionwidget.client.resources.ExpressionResources;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class HelpReplaceColumnByExpressionPanel extends FramedPanel {

	protected static final String WIDTH = "700px";
	protected static final String HEIGHT = "404px";
	protected static final String HELP_CONTENT_WIDTH = "700px";
	protected static final String HELP_CONTENT_HEIGHT = "404px";
	protected EventBus eventBus;

	protected HelpReplaceColumnByExpressionDialog parent;

	private TextButton btnClose;

	public HelpReplaceColumnByExpressionPanel(
			HelpReplaceColumnByExpressionDialog parent, EventBus eventBus) {
		super();
		this.parent = parent;
		this.eventBus = eventBus;
		init();
		create();
	}

	protected void init() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setBodyBorder(false);
		setHeaderVisible(false);
		// Important: fixed rendering of widgets
		forceLayoutOnResize = true;

	}

	protected void create() {
		VerticalLayoutContainer basicLayout = new VerticalLayoutContainer();
		basicLayout.setAdjustForScroll(true);
		basicLayout.setScrollMode(ScrollMode.AUTO);

		//
		SimpleContainer helpContent = new SimpleContainer();
		helpContent.setWidth(HELP_CONTENT_WIDTH);
		helpContent.setHeight(HELP_CONTENT_HEIGHT);

		VerticalLayoutContainer helpContentLayout = new VerticalLayoutContainer();
		helpContent.add(helpContentLayout);
		SafeHtmlBuilder safeHelp = new SafeHtmlBuilder();
		HelpReplaceColumnByExpression helpR;
		try {

		    helpR = new HelpReplaceColumnByExpression();
			helpR.render(safeHelp);
		} catch (Throwable e) {
			Log.error("Error in Help:" + e.getLocalizedMessage());
			e.printStackTrace();
			return;
		}

		HTML htmlHelp = new HTML();
		htmlHelp.setHTML(safeHelp.toSafeHtml());
		helpContentLayout.add(htmlHelp);

		//
		HBoxLayoutContainer flowButton = new HBoxLayoutContainer();
		flowButton.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		flowButton.setPack(BoxLayoutPack.CENTER);

		btnClose = new TextButton("Close");
		btnClose.setIcon(ExpressionResources.INSTANCE.close());
		btnClose.setIconAlign(IconAlign.RIGHT);
		btnClose.setTitle("Close");
		btnClose.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Close");
				close();
			}
		});
		flowButton.add(btnClose, new BoxLayoutData(new Margins(2, 4, 2, 4)));

		// Add to basic layout
		basicLayout.add(helpContent, new VerticalLayoutData(-1, -1,
				new Margins(1)));

		basicLayout.add(flowButton, new VerticalLayoutData(-1, 36, new Margins(
				5, 2, 5, 2)));
		add(basicLayout);

	}

	protected void close() {
		parent.close();
	}

}
