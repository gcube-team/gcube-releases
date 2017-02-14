package org.gcube.portlets.user.td.expressionwidget.client.help;

import org.gcube.portlets.user.td.expressionwidget.client.resources.ExpressionResources;

import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class HelpReplaceColumnByExpressionDialog extends Window {
	protected static final String WIDTH = "742px";
	protected static final String HEIGHT = "520px";

	protected EventBus eventBus;
	protected HelpReplaceColumnByExpressionPanel helpReplaceColumnByExpressionPanel;

	public HelpReplaceColumnByExpressionDialog(EventBus eventBus) {
		this.eventBus = eventBus;
		initWindow();
		create();

	}

	protected void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setBodyBorder(false);
		setResizable(false);
		setHeadingText("Help Replace Column By Expression");
		setClosable(true);
		getHeader().setIcon(
				ExpressionResources.INSTANCE.columnReplaceByExpression());

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initTools() {
		super.initTools();

		closeBtn.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				close();
			}
		});

	}

	protected void create() {
			helpReplaceColumnByExpressionPanel = new HelpReplaceColumnByExpressionPanel(
					this, eventBus);
			add(helpReplaceColumnByExpressionPanel);

		
	}


	protected void close() {
		hide();
	}

	
}
