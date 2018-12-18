package org.gcube.portlets.user.td.monitorwidget.client.background;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.monitor.BackgroundOperationMonitor;
import org.gcube.portlets.user.td.gwtservice.shared.task.State;
import org.gcube.portlets.user.td.monitorwidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.monitorwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ProgressBarCell;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class MonitorBackgroundPanel extends FramedPanel implements
		MonitorBackgroundUpdaterListener {
	private static final int STATUS_POLLING_DELAY = 5000;
	private static final String WIDTH = "640px";
	private static final String HEIGHT = "520px";
	private static final int CC1WIDTH = 90;
	private static final int CC2WIDTH = 66;
	private static final int CC3WIDTH = 104;
	private static final int PROGRESS_WIDTH = 100;
	private MonitorBackgroundDialog parent;

	private ArrayList<MonitorBackgroundEventUIListener> monitorBackgroundEventUIListeners = new ArrayList<MonitorBackgroundEventUIListener>();

	private Menu contextMenu;
	private MenuItem infoItem;
	private MenuItem abortItem;
	private MenuItem hideItem;

	// private TRId trId;
	private VerticalLayoutContainer con;
	// private ValidationsTasksMetadata validationsTasksMetadata;

	private ListStore<BackgroundOperationMonitor> store;
	private Grid<BackgroundOperationMonitor> grid;
	private ArrayList<BackgroundOperationMonitor> backgroundOperationMonitorList;
	private ArrayList<BackgroundOperationMonitor> backgroundOperationMonitorListCache;

	private EventBus eventBus;
	private MonitorBackgroundUpdater monitorBackgroundUpdater;

	public interface BackgroundOperationMonitorProperties extends
			PropertyAccess<BackgroundOperationMonitor> {

		@Path("taskId")
		ModelKeyProvider<BackgroundOperationMonitor> taskId();

		ValueProvider<BackgroundOperationMonitor, String> tabularResourceName();

		ValueProvider<BackgroundOperationMonitor, State> state();

		ValueProvider<BackgroundOperationMonitor, Float> progress();
	}

	public MonitorBackgroundPanel(EventBus eventBus) {
		super();
		backgroundOperationMonitorListCache = null;
		this.eventBus = eventBus;
		forceLayoutOnResize = true;
		init();
		create();
	}

	public MonitorBackgroundPanel(MonitorBackgroundDialog parent,
			EventBus eventBus) {
		super();
		backgroundOperationMonitorListCache = null;
		this.eventBus = eventBus;
		this.parent = parent;
		forceLayoutOnResize = true;
		init();
		create();
	}

	protected void init() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);
		setResize(true);

	}

	protected void create() {
		con = new VerticalLayoutContainer();
		con.setScrollMode(ScrollMode.AUTO);
		BackgroundOperationMonitorProperties props = GWT
				.create(BackgroundOperationMonitorProperties.class);
		store = new ListStore<BackgroundOperationMonitor>(props.taskId());

		addChildrensToStore();

		ColumnConfig<BackgroundOperationMonitor, String> cc1 = new ColumnConfig<BackgroundOperationMonitor, String>(
				props.tabularResourceName());
		cc1.setHeader("Tabular Resource");
		cc1.setWidth(CC1WIDTH);
		cc1.setCell(new AbstractCell<String>() {

			@Override
			public void render(Context context, String value, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<span title='"
						+ SafeHtmlUtils.htmlEscape(value) + "'>"
						+ SafeHtmlUtils.htmlEscape(value) + "</span>");

			}
		});

		ColumnConfig<BackgroundOperationMonitor, State> cc2 = new ColumnConfig<BackgroundOperationMonitor, State>(
				props.state());
		cc2.setHeader("Status");
		cc2.setWidth(CC2WIDTH);

		ColumnConfig<BackgroundOperationMonitor, Double> cc3 = new ColumnConfig<BackgroundOperationMonitor, Double>(
				new ValueProvider<BackgroundOperationMonitor, Double>() {

					@Override
					public Double getValue(BackgroundOperationMonitor object) {
						Double d = null;
						Float f = object.getProgress();
						d = new Double(f);
						return d;
					}

					@Override
					public void setValue(BackgroundOperationMonitor object,
							Double value) {
						object.setProgress(value.floatValue());
					}

					@Override
					public String getPath() {
						return "progress";
					}
				}, CC3WIDTH, "Progress");
		cc3.setHeader("Progress");

		ProgressBarCell progress = new ProgressBarCell() {
			@Override
			public boolean handlesSelection() {
				return false;
			}
		};
		progress.setProgressText("{0}% Complete");
		progress.setWidth(PROGRESS_WIDTH);

		cc3.setCell(progress);

		List<ColumnConfig<BackgroundOperationMonitor, ?>> l = new ArrayList<ColumnConfig<BackgroundOperationMonitor, ?>>();
		l.add(cc1);
		l.add(cc2);
		l.add(cc3);
		ColumnModel<BackgroundOperationMonitor> cm = new ColumnModel<BackgroundOperationMonitor>(
				l);

		grid = new Grid<BackgroundOperationMonitor>(store, cm);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.getView().setAutoFill(true);
		grid.setBorders(false);
		grid.setLoadMask(true);
		grid.setColumnReordering(false);
		grid.setColumnResize(true);
		grid.getView().setAutoExpandColumn(cc1);

		createContextMenu();

		con.add(grid, new VerticalLayoutData(1, -1, new Margins(0)));

		add(con, new MarginData(0));

		addMonitor();

		forceLayout();

	}

	protected void addMonitor() {
		monitorBackgroundUpdater = new MonitorBackgroundUpdater();
		monitorBackgroundUpdater.addListener(this);

		addMonitorBackgroundEventUIListener(monitorBackgroundUpdater);
		monitorBackgroundUpdater.scheduleRepeating(STATUS_POLLING_DELAY);
		monitorBackgroundUpdater.run();
	}

	public void addMonitorBackgroundEventUIListener(
			MonitorBackgroundEventUIListener listener) {
		monitorBackgroundEventUIListeners.add(listener);
	}

	public void removeMonitorBackgroundEventUIListener(
			MonitorBackgroundEventUIListener listener) {
		monitorBackgroundEventUIListeners.remove(listener);
	}

	public void update() {
		forceLayout();
	}

	public void close() {
		if (parent != null) {
			parent.close();
		}
		

	}
	
	public void cancelMonitorBackgroundUpdater(){
		if(monitorBackgroundUpdater!=null){
			monitorBackgroundUpdater.cancel();
		}
	}
	

	private void addChildrensToStore() {
		if (backgroundOperationMonitorList != null) {
			store.addAll(backgroundOperationMonitorList);
		}
	}

	protected void createContextMenu() {
		contextMenu = new Menu();

		infoItem = new MenuItem();
		infoItem.setText("Info");
		infoItem.setIcon(ResourceBundle.INSTANCE.information());
		infoItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				BackgroundOperationMonitor selected = grid.getSelectionModel()
						.getSelectedItem();
				Log.debug(selected.toString());
				requestMoreInfo(selected);
			}
		});

		abortItem = new MenuItem();
		abortItem.setText("Abort");
		abortItem.setIcon(ResourceBundle.INSTANCE.basketDelete());
		abortItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				BackgroundOperationMonitor selected = grid.getSelectionModel()
						.getSelectedItem();
				Log.debug(selected.toString());
				requestBacgroundAbort(selected);

			}

		});

		hideItem = new MenuItem();
		hideItem.setText("Hide");
		hideItem.setIcon(ResourceBundle.INSTANCE.basketRemove());
		hideItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				BackgroundOperationMonitor selected = grid.getSelectionModel()
						.getSelectedItem();
				Log.debug(selected.toString());
				requestBacgroundHidden(selected);

			}

		});

		contextMenu.add(infoItem);
		contextMenu.add(abortItem);
		contextMenu.add(hideItem);

		grid.setContextMenu(contextMenu);

	}

	protected void requestMoreInfo(BackgroundOperationMonitor selected) {
		Log.debug("Found Task Id: " + selected.getTaskId());
		MonitorBackgroundInfoDialog monitorBackgroundInfoDialog = new MonitorBackgroundInfoDialog(
				selected, eventBus);
		monitorBackgroundInfoDialog.show();
		return;

	}

	protected void requestBacgroundAbort(BackgroundOperationMonitor selected) {
		Log.debug("Found Task Id: " + selected.getTaskId());
		if (selected.getState() == State.IN_PROGRESS
				|| selected.getState() == State.INITIALIZING
				|| selected.getState() == State.VALIDATING_RULES) {
			fireRequestAborted(selected.getTaskId());
		}

	}

	protected void requestBacgroundHidden(BackgroundOperationMonitor selected) {
		Log.debug("Found Task Id: " + selected.getTaskId());
		fireRequestHidden(selected.getTaskId());
		return;
	}

	@Override
	public void operationMonitorListUpdated(
			ArrayList<BackgroundOperationMonitor> backgroundOperationMonitorList) {
		this.backgroundOperationMonitorList = backgroundOperationMonitorList;

		if (!isCacheValid(backgroundOperationMonitorList)) {
			store.clear();
			store.commitChanges();
			addChildrensToStore();
			store.commitChanges();
			forceLayout();
		}

	}

	protected boolean isCacheValid(
			ArrayList<BackgroundOperationMonitor> backgroundOperationMonitorList) {
		if (backgroundOperationMonitorList == null
				|| backgroundOperationMonitorList.size() <= 0) {
			backgroundOperationMonitorListCache = null;
			return false;
		}

		if (backgroundOperationMonitorListCache == null) {
			backgroundOperationMonitorListCache = backgroundOperationMonitorList;
			return false;
		}

		if (backgroundOperationMonitorListCache.size() == backgroundOperationMonitorList
				.size()) {
			for (int i = 0; i < backgroundOperationMonitorList.size(); i++) {
				BackgroundOperationMonitor backgroundOperationMonitor = backgroundOperationMonitorList
						.get(i);
				BackgroundOperationMonitor backgroundOperationMonitorCache = backgroundOperationMonitorListCache
						.get(i);
				if (backgroundOperationMonitor.getTaskId().compareTo(
						backgroundOperationMonitorCache.getTaskId()) == 0
						&& backgroundOperationMonitor.getState().compareTo(
								backgroundOperationMonitorCache.getState()) == 0
						&& backgroundOperationMonitor.getProgress() == backgroundOperationMonitorCache
								.getProgress()) {

				} else {
					backgroundOperationMonitorListCache = backgroundOperationMonitorList;
					return false;
				}

			}
		} else {
			backgroundOperationMonitorListCache = backgroundOperationMonitorList;
			return false;
		}

		return true;
	}

	@Override
	public void retrieveOperationMonitorListFailed(Throwable caught) {
		if (caught instanceof TDGWTSessionExpiredException) {
			eventBus.fireEvent(new SessionExpiredEvent(
					SessionExpiredType.EXPIREDONSERVER));
		} else {
			UtilsGXT3.alert("Error", "Error retrieving tasks in bacground: "
					+ caught.getLocalizedMessage());

		}

	}

	// UI event Fire
	protected void fireRequestAborted(String taskId) {
		for (MonitorBackgroundEventUIListener listener : monitorBackgroundEventUIListeners) {
			listener.requestAborted(taskId);

		}
	}

	protected void fireRequestHidden(String taskId) {
		for (MonitorBackgroundEventUIListener listener : monitorBackgroundEventUIListeners) {
			listener.requestHidden(taskId);

		}
	}

	protected void fireRequestResume(String taskId) {
		for (MonitorBackgroundEventUIListener listener : monitorBackgroundEventUIListeners) {
			listener.requestResume(taskId);

		}
	}

}