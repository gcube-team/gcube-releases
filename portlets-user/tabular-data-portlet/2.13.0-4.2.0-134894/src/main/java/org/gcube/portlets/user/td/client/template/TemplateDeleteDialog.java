package org.gcube.portlets.user.td.client.template;

import org.gcube.portlets.user.td.client.resource.TabularDataResources;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.template.TemplateDeleteSession;
import org.gcube.portlets.user.td.tablewidget.client.util.UtilsGXT3;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
public class TemplateDeleteDialog extends Window {
	private static final String WIDTH = "850px";
	private static final String HEIGHT = "530px";
	private EventBus eventBus;

	public TemplateDeleteDialog(EventBus eventBus) {
		this.eventBus = eventBus;
		initWindow();

		TemplateDeletePanel templateDeletePanel = new TemplateDeletePanel(this,
				eventBus);
		add(templateDeletePanel);
	}

	protected void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setBodyBorder(false);
		setResizable(false);
		setHeadingText("Delete Template");
		setClosable(true);
		setModal(true);
		forceLayoutOnResize = true;
		getHeader().setIcon(TabularDataResources.INSTANCE.templateDelete());

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

	protected void templatesDelete(
			final TemplateDeleteSession templateDeleteSession) {
		

		TDGWTServiceAsync.INSTANCE.templateDelete(templateDeleteSession,
				new AsyncCallback<Void>() {
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert("Error Locked",
										caught.getLocalizedMessage());
							} else {
								Log.debug("Delete Template Error: "
										+ caught.getLocalizedMessage());
								UtilsGXT3.alert("Apply Template Error ",
										"Error in invocation of delete template operation! "
												+ caught.getLocalizedMessage());
								
							}
							close();
						}
					}

					public void onSuccess(Void result) {
						UtilsGXT3.info("Delete Template", "Template deleted!");
						close();
					}

				});

	}

}
