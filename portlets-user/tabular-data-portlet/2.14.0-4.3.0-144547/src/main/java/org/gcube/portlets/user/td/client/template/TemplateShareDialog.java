package org.gcube.portlets.user.td.client.template;

import org.gcube.portlets.user.td.client.resource.TabularDataResources;
import org.gcube.portlets.user.td.gwtservice.shared.template.TemplateData;
import org.gcube.portlets.user.td.gwtservice.shared.user.UserInfo;
import org.gcube.portlets.user.td.sharewidget.client.TemplateShare;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.wizardwidget.client.util.UtilsGXT3;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
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
public class TemplateShareDialog extends Window {
	private static final String WIDTH = "850px";
	private static final String HEIGHT = "530px";

	private EventBus eventBus;
	private UserInfo userInfo;
	private CommonMessages msgsCommon;

	public TemplateShareDialog(UserInfo userInfo, EventBus eventBus) {
		this.eventBus = eventBus;
		this.userInfo = userInfo;
		initMessages();
		initWindow();
		TemplateSharePanel templateDeletePanel = new TemplateSharePanel(this,
				eventBus);
		add(templateDeletePanel);
	}

	protected void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setBodyBorder(false);
		setResizable(false);
		setHeadingText("Share Template");
		setClosable(true);
		setModal(true);
		forceLayoutOnResize = true;
		getHeader().setIcon(TabularDataResources.INSTANCE.templateShare());

	}

	private void initMessages() {
		//msgs = GWT.create(TabularDataControllerMessages.class);
		msgsCommon = GWT.create(CommonMessages.class);
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

	public void templateShare(TemplateData templateData) {
		Log.debug("Share Window");
		if (userInfo.getUsername().compareTo(templateData.getOwnerLogin()) == 0) {
			@SuppressWarnings("unused")
			TemplateShare templateShare = new TemplateShare(userInfo,
					templateData, eventBus);
			close();
		} else {
			UtilsGXT3
					.info(msgsCommon.attention(),
							"In order to share a template you must be the owner of the template. "
									+ "You are not the owner of this template!");
		}

	}
}
