package org.gcube.portlets.user.td.client.logs;

import org.gcube.portal.clientcontext.client.GCubeClientContext;
import org.gcube.portlets.user.td.client.resource.TabularDataResources;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.informationwidget.client.util.UtilsGXT3;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

public class TDMLogs extends Window {
	protected static final String TDMLogsServlet = "TDLogsServlet";
	protected static final String WIDTH = "700px";
	protected static final String HEIGHT = "500px";
	protected static final String CONTENTLOGSWIDTH = "674px";
	protected static final String CONTENTLOGSHEIGHT = "410px";

	protected EventBus eventBus;
	protected VerticalLayoutContainer verticalContainer;

	public TDMLogs(EventBus eventBus) {
		super();
		this.eventBus = eventBus;
		init();
		createLogsPanel();
		callLogsServlet();
	}

	public void init() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setHeadingText("TDM Logs");
		setBodyBorder(false);
		setClosable(true);
		setResizable(false);
	}

	protected void createLogsPanel() {
		FramedPanel logsPanel = new FramedPanel();
		logsPanel.setBodyBorder(false);
		logsPanel.setHeaderVisible(false);

		ToolBar toolBar = new ToolBar();

		TextButton btnReload = new TextButton();
		// btnReload.setText("Reload");
		btnReload.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				callLogsServlet();
			}
		});

		btnReload.setIcon(TabularDataResources.INSTANCE.refresh());
		btnReload.setToolTip("Reload");
		toolBar.add(btnReload);

		SimpleContainer contentLogs = new SimpleContainer();
		contentLogs.setBorders(false);
		contentLogs.setWidth(CONTENTLOGSWIDTH);
		contentLogs.setHeight(CONTENTLOGSHEIGHT);

		verticalContainer = new VerticalLayoutContainer();
		verticalContainer.setScrollMode(ScrollMode.AUTO);
		verticalContainer.getElement().getStyle().setBackgroundColor("white");

		contentLogs.add(verticalContainer);

		VerticalLayoutContainer logsPanelLayout = new VerticalLayoutContainer();

		logsPanelLayout.add(toolBar, new VerticalLayoutData(1, 32, new Margins(
				1)));
		logsPanelLayout.add(contentLogs, new VerticalLayoutData(1, -1,
				new Margins(1)));

		logsPanel.add(logsPanelLayout);
		add(logsPanel);

	}

	public void callLogsServlet() {
		String path = GWT.getModuleBaseURL() + TDMLogsServlet + "?"
				+ Constants.CURR_GROUP_ID + "="
				+ GCubeClientContext.getCurrentContextId();
		Log.debug("TDMLogsServlet path:" + path);
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, path);
		builder.setHeader(Constants.CURR_GROUP_ID,
				GCubeClientContext.getCurrentContextId());

		try {
			builder.sendRequest(null, new RequestCallback() {

				public void onError(Request request, Throwable exception) {
					Log.error("Error retrienving logs. "
							+ exception.getLocalizedMessage());
					exception.printStackTrace();
					UtilsGXT3.alert("Logs", "Error retrienving logs");
					close();
				}

				public void onResponseReceived(Request request,
						Response response) {
					if (Response.SC_OK == response.getStatusCode()) {
						update(response.getText());
					} else {
						if (408 == response.getStatusCode()) {
							Log.error("Error retrienving logs. Session expired.");
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							Log.error("Error retrienving logs. Response StatusCode: "
									+ response.getStatusCode());
							UtilsGXT3.alert("Logs", "Error retrienving logs");
						}
						close();
					}
				}
			});
		} catch (RequestException e) {
			close();
			Log.error("Error retrienving logs. " + e.getLocalizedMessage());
			e.printStackTrace();
			UtilsGXT3.alert("Logs", "Error retrienving logs");

		}
	}

	protected void update(String text) {
		verticalContainer.clear();
		/*
		 * int body=text.indexOf("<body>"); text=text.substring(body+6);
		 */

		SafeHtmlBuilder builderLogs = new SafeHtmlBuilder();

		builderLogs.appendEscapedLines(text);
		SafeHtml safeLogs = builderLogs.toSafeHtml();
		HTML hLogs = new HTML(safeLogs);

		verticalContainer.add(hLogs, new VerticalLayoutData(1, -1, new Margins(
				1)));

		forceLayout();
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

	/**
	 * 
	 */
	protected void close() {
		hide();
	}
}
