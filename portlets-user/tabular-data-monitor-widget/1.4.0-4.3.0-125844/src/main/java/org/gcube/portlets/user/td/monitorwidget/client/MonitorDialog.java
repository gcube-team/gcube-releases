package org.gcube.portlets.user.td.monitorwidget.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.monitor.OperationMonitor;
import org.gcube.portlets.user.td.gwtservice.shared.monitor.OperationMonitorSession;
import org.gcube.portlets.user.td.monitorwidget.client.details.MonitorDetailPanel;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.BackgroundRequestEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.BackgroundRequestType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.OperationResult;

import com.allen_sauer.gwt.log.client.Log;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.ProgressBar;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * AddColumnProgressDialog is a Dialog that show progress of AddColumn
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class MonitorDialog extends Window implements MonitorUpdaterListener {
	//private DateTimeFormat dateTimeFormat= DateTimeFormat.getFormat("yyyy/MM/dd HH:mm:ss SSS");
	private static final int STATUS_POLLING_DELAY = 1000;
	private static final String WIDTH = "640px";
	private static final String HEIGHT = "378px";
	private static final String PANELWIDTH = "620px";
	private static final String PANELHEIGHT = "308px";
	private static final String PROGRESSWIDTH = "618px";

	private EventBus eventBus;
	private String taskId;
	private MonitorUpdater progressUpdater;
	private TextButton btnOk;
	private TextButton btnBackground;
	private TextButton btnAbort;
	private OperationMonitor operationMonitorResult;
	private String reason;
	private String details;

	protected ToolButton backgroundBtn;

	protected ArrayList<MonitorDialogListener> listeners = new ArrayList<MonitorDialogListener>();
	protected ArrayList<MonitorDialogEventUIListener> monitorDialogEventUIListeners = new ArrayList<MonitorDialogEventUIListener>();

	private MonitorDetailPanel monitorDetailPanel;

	public MonitorDialog(String taskId, EventBus eventBus) {
		this.eventBus = eventBus;
		this.taskId = taskId;
		operationMonitorResult = null;
		//Log.debug("Monitor Dialog Creation Start: "+dateTimeFormat.format(new Date()));
		initWindow();
		create();

	}

	protected void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setBodyBorder(false);
		setResizable(false);
		setModal(true);
		setClosable(true);
		setHeadingText("Progress");
	}

	protected void create() {
		FramedPanel panel = new FramedPanel();
		panel.setWidth(PANELWIDTH);
		panel.setHeight(PANELHEIGHT);
		panel.setHeaderVisible(false);
		panel.setBodyBorder(false);

		VerticalLayoutContainer v = new VerticalLayoutContainer();
		

		ProgressBar progressBar = new ProgressBar();
		progressBar.setWidth(PROGRESSWIDTH);

		
		monitorDetailPanel = new MonitorDetailPanel(eventBus);
		
		btnOk = new TextButton("Ok");
		btnOk.setWidth("70px");
		// btnOk.setIcon(ResourceBundle.INSTANCE.ok());
		// btnOk.setIconAlign(IconAlign.RIGHT);
		btnOk.setTitle("Ok");

		btnOk.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				updateInvocation();

			}
		});

		btnBackground = new TextButton("Background");
		btnBackground.setWidth("70px");
		// btnBackground.setIcon(ResourceBundle.INSTANCE.background());
		// btnBackground.setIconAlign(IconAlign.RIGHT);
		btnBackground.setTitle("Background");

		btnBackground.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				startBackground();

			}

		});

		btnAbort = new TextButton("Abort");
		btnAbort.setWidth("70px");
		// btnAbort.setIcon(ResourceBundle.INSTANCE.abort());
		// btnAbort.setIconAlign(IconAlign.RIGHT);
		btnAbort.setTitle("Abort");

		btnAbort.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				startAbort();

			}
		});

		HBoxLayoutContainer flowButton = new HBoxLayoutContainer();
		flowButton.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		flowButton.setPack(BoxLayoutPack.CENTER);

		flowButton.add(btnOk, new BoxLayoutData(new Margins(0, 4, 0, 4)));
		flowButton.add(btnBackground,
				new BoxLayoutData(new Margins(0, 4, 0, 4)));
		flowButton.add(btnAbort, new BoxLayoutData(new Margins(0, 4, 0, 4)));

		v.add(progressBar, new VerticalLayoutData(1, -1,
				new Margins(5, 5, 5, 5)));

		v.add(monitorDetailPanel, new VerticalLayoutData(1, -1, new Margins(5,
				5, 5, 5)));

		v.add(flowButton,
				new VerticalLayoutData(1, 36, new Margins(5, 5, 5, 5)));

	

		panel.add(v);
		add(panel);

		OperationMonitorSession operationMonitorSession = new OperationMonitorSession(
				taskId);
		progressUpdater = new MonitorUpdater(operationMonitorSession);
		progressUpdater.addListener(new MonitorBarUpdater(progressBar));

		progressUpdater.addListener(this);
		progressUpdater.scheduleRepeating(STATUS_POLLING_DELAY);
		addMonitorDialogEventUIListener(progressUpdater);
		show();
		btnOk.setVisible(false);
		//Log.debug("Monitor Dialog Creation End: "+dateTimeFormat.format(new Date()));

	}

	protected void startAbort() {
		btnOk.setEnabled(false);
		btnBackground.setEnabled(false);
		btnAbort.setEnabled(false);
		fireRequestAborted();
	}

	protected void startBackground() {
		btnOk.setEnabled(false);
		btnBackground.setEnabled(false);
		btnAbort.setEnabled(false);
		fireRequestPutInBackground();
	}

	public boolean isAbortBtnEnabled() {
		if (btnAbort != null) {
			return btnAbort.isEnabled();
		} else {
			return false;
		}
	}

	public void setAbortBtnEnabled(boolean abortBtnEnabled) {
		if (btnBackground != null) {
			btnBackground.setEnabled(abortBtnEnabled);
			btnBackground.setVisible(abortBtnEnabled);
		}
	}

	public boolean isBackgroundBtnEnabled() {
		if (btnBackground != null) {
			return btnBackground.isEnabled();
		} else {
			return false;
		}
	}

	public void setBackgroundBtnEnabled(boolean backgroundBtnEnabled) {
		if (btnBackground != null) {
			btnBackground.setEnabled(backgroundBtnEnabled);
			btnBackground.setVisible(backgroundBtnEnabled);
		}
	}

	public void addProgressDialogListener(MonitorDialogListener listener) {
		listeners.add(listener);
	}

	public void removeProgressDialogListener(MonitorDialogListener listener) {
		listeners.remove(listener);
	}

	public void addMonitorDialogEventUIListener(
			MonitorDialogEventUIListener listener) {
		monitorDialogEventUIListeners.add(listener);
	}

	public void removeMonitorDialogEventUIListener(
			MonitorDialogEventUIListener listener) {
		monitorDialogEventUIListeners.remove(listener);
	}

	@Override
	public void monitorInitializing(OperationMonitor operationMonitor) {
		try {
			if (monitorDetailPanel != null) {
				monitorDetailPanel.update(operationMonitor);
			}
		} catch (Throwable e) {
			Log.debug(e.getLocalizedMessage());
		}
		forceLayout();

	}

	@Override
	public void monitorUpdate(OperationMonitor operationMonitor) {
		try {
			if (monitorDetailPanel != null) {
				monitorDetailPanel.update(operationMonitor);
			}
		} catch (Throwable e) {
			Log.debug(e.getLocalizedMessage());
		}
		forceLayout();
	}

	@Override
	public void monitorComplete(OperationMonitor operationMonitor) {
		try {
			if (monitorDetailPanel != null) {
				monitorDetailPanel.update(operationMonitor);
			}
		} catch (Throwable e) {
			Log.debug(e.getLocalizedMessage());
		}
		Log.debug("Operation Complete return: " + operationMonitor.getTrId());
		this.operationMonitorResult = operationMonitor;
		btnBackground.setVisible(false);
		btnAbort.setVisible(false);
		forceLayout();
		fireOperationComplete(operationMonitor);
		close();
	}

	@Override
	public void monitorFailed(Throwable caught, String reason, String details,
			OperationMonitor operationMonitor) {
		try {
			if (monitorDetailPanel != null) {
				monitorDetailPanel.update(operationMonitor);

			}
		} catch (Throwable e) {
			Log.debug(e.getLocalizedMessage());
		}
		forceLayout();
		if (caught instanceof TDGWTSessionExpiredException) {
			eventBus.fireEvent(new SessionExpiredEvent(
					SessionExpiredType.EXPIREDONSERVER));
		} else {
			fireOperationFailed(caught, reason, details);
			close();
		}

	}

	public void updateInvocation() {
		if (operationMonitorResult != null) {
			fireOperationStopped(operationMonitorResult, reason, details);
		}
		close();

	}

	@Override
	public void monitorStopped(String reason, String details,
			OperationMonitor operationMonitor) {
		try {
			if (monitorDetailPanel != null) {
				monitorDetailPanel.update(operationMonitor);
			}
		} catch (Throwable e) {
			Log.debug(e.getLocalizedMessage());
		}
		Log.debug("Operation Stopped: [" + operationMonitor.getTrId() + ", " + reason
				+ ", " + details + "]");
		this.operationMonitorResult = operationMonitor;
		this.reason = reason;
		this.details = details;
		btnOk.setVisible(true);
		btnBackground.setVisible(false);
		btnAbort.setVisible(false);
		forceLayout();
	}

	@Override
	public void monitorGeneratingView(OperationMonitor operationMonitor) {
		try {
			if (monitorDetailPanel != null) {
				monitorDetailPanel.update(operationMonitor);
			}
		} catch (Throwable e) {
			Log.debug(e.getLocalizedMessage());
		}
		forceLayout();

	}

	@Override
	public void monitorValidate(OperationMonitor operationMonitor) {
		try {
			if (monitorDetailPanel != null) {
				monitorDetailPanel.update(operationMonitor);
			}
		} catch (Throwable e) {
			Log.debug(e.getLocalizedMessage());
		}
		forceLayout();
	}

	@Override
	public void monitorAborted() {
		fireOperationAborted();
		close();
	}

	@Override
	public void monitorPutInBackground() {
		fireOperationPutInBackground();
		close();
		Log.debug("Request Background");
		BackgroundRequestEvent e = new BackgroundRequestEvent(
				BackgroundRequestType.BACKGROUND);
		eventBus.fireEvent(e);

	}

	protected void fireOperationComplete(OperationMonitor operationMonitor) {
		for (MonitorDialogListener listener : listeners){
			OperationResult operationResult=new OperationResult(operationMonitor.getTrId(),
				operationMonitor.getTask().getCollateralTRIds());
			listener.operationComplete(operationResult);
		}
	}

	protected void fireOperationFailed(Throwable caught, String reason,
			String details) {
		for (MonitorDialogListener listener : listeners)
			listener.operationFailed(caught, reason, details);
	}

	protected void fireOperationStopped(OperationMonitor operationMonitor, String reason, String details) {
		for (MonitorDialogListener listener : listeners){
			OperationResult operationResult=new OperationResult(operationMonitor.getTrId(),
					operationMonitor.getTask().getCollateralTRIds());
			listener.operationStopped(operationResult, reason, details);
		}
	}

	protected void fireOperationAborted() {
		for (MonitorDialogListener listener : listeners)
			listener.operationAborted();
	}

	protected void fireOperationPutInBackground() {
		for (MonitorDialogListener listener : listeners)
			listener.operationPutInBackground();

	}

	// UI event Fire
	protected void fireRequestAborted() {
		for (MonitorDialogEventUIListener listener : monitorDialogEventUIListeners) {
			listener.requestAborted();
		}
	}

	protected void fireRequestPutInBackground() {
		for (MonitorDialogEventUIListener listener : monitorDialogEventUIListeners) {
			listener.requestPutInBackground();
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initTools() {
		/*
		 * if (backgroundBtnEnabled) { backgroundBtn = new
		 * ToolButton(ToolButton.CLOSE);
		 * backgroundBtn.setToolTip("Put in the background");
		 * backgroundBtn.addSelectHandler(new SelectHandler() {
		 * 
		 * @Override public void onSelect(SelectEvent event) { hide(); } });
		 * 
		 * header.addTool(backgroundBtn); }
		 */

		super.initTools();

		closeBtn.setVisible(false);
		/*
		 * closeBtn.addSelectHandler(new SelectHandler() {
		 * 
		 * public void onSelect(SelectEvent event) { close(); } });
		 */

	}

	protected void close() {
		if(progressUpdater!=null){
			progressUpdater.cancel();
		}
		hide();

	}

	public void updateOperationMonitor(OperationMonitor operationMonitor) {
		try {
			if (monitorDetailPanel != null) {
				monitorDetailPanel.update(operationMonitor);
			}
		} catch (Throwable e) {
			Log.debug(e.getLocalizedMessage());
		}
		forceLayout();
	}

}
