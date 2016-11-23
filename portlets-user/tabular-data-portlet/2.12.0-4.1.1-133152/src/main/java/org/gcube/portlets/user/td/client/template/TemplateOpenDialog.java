package org.gcube.portlets.user.td.client.template;

import org.gcube.portlets.user.td.client.resource.TabularDataResources;
import org.gcube.portlets.user.td.gwtservice.shared.template.TemplateData;
import org.gcube.portlets.user.tdtemplate.client.TdTemplateController;
import org.gcube.portlets.user.tdtemplate.client.TdTemplateControllerUpdater;

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
public class TemplateOpenDialog extends Window {
	private static final String WIDTH = "850px";
	private static final String HEIGHT = "530px";
	private EventBus eventBus;
 
	public TemplateOpenDialog(EventBus eventBus) {
		this.eventBus = eventBus;
		initWindow();

		TemplateOpenPanel templateOpenPanel = new TemplateOpenPanel(this,
				eventBus);
		add(templateOpenPanel);
	}

	protected void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setBodyBorder(false);
		setResizable(false);
		setHeadingText("Open Template");
		setClosable(true);
		setModal(true);
		forceLayoutOnResize = true;
		getHeader().setIcon(TabularDataResources.INSTANCE.templateEdit());

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

	protected void close() {
		hide();
	}

	public void templateOpen(TemplateData template) {
		TdTemplateControllerUpdater controller = new TdTemplateControllerUpdater(
				template.getId());
		TdTemplateController.bindCommonBus(eventBus);
		controller.getWindowTemplatePanel().show();
		close();

	}

}
