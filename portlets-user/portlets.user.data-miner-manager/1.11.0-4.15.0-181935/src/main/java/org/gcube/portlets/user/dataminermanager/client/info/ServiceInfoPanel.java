package org.gcube.portlets.user.dataminermanager.client.info;

import java.util.ArrayList;
import java.util.List;

import org.gcube.data.analysis.dataminermanagercl.shared.service.ServiceInfo;
import org.gcube.data.analysis.dataminermanagercl.shared.service.ServiceInfoData;
import org.gcube.portlets.user.dataminermanager.client.common.EventBusProvider;
import org.gcube.portlets.user.dataminermanager.client.events.ServiceInfoEvent;
import org.gcube.portlets.user.dataminermanager.client.events.ServiceInfoRequestEvent;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GroupingView;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ServiceInfoPanel extends FramedPanel {

	private static final String RUNTIME_FEATURE = "Runtime features";
	private static final String SERVICE_INFO_TITLE = "Service Profile";
	private static final ServiceInfoDataProperties props = GWT.create(ServiceInfoDataProperties.class);
	private VerticalLayoutContainer v;
	private VerticalLayoutContainer environmentVBox;

	public ServiceInfoPanel() {
		super();
		Log.debug("ServiceInfoPanel");
		init();
		create();
		bind();
		EventBusProvider.INSTANCE.fireEvent(new ServiceInfoRequestEvent());

	}

	private void init() {
		setItemId("ServiceInfoPanel");
		forceLayoutOnResize = true;
		setBodyBorder(false);
		setBorders(false);
		setBodyStyle("backgroundColor:white;");
		setHeaderVisible(false);
		setResize(true);
		setHeadingText(SERVICE_INFO_TITLE);
		setBodyStyle("backgroundColor:white;");

	}

	private void create() {
		try {
			v = new VerticalLayoutContainer();
			v.setScrollMode(ScrollMode.AUTO);
			add(v);
			createView();
			forceLayout();
		} catch (Throwable e) {
			Log.error("Error creating ServiceInfoPanel: " + e.getLocalizedMessage(), e);
		}
	}

	private void bind() {

		EventBusProvider.INSTANCE.addHandler(ServiceInfoEvent.TYPE, new ServiceInfoEvent.ServiceInfoEventHandler() {

			@Override
			public void onRequest(ServiceInfoEvent event) {
				Log.debug("Catch ServiceInfoEvent");
				showServiceInfo(event.getServiceInfo());

			}

		});

	}

	private void createView() {

		SimpleContainer sectionTitle = new SimpleContainer();
		SimpleContainer sectionSubTitle = new SimpleContainer();

		// title
		HtmlLayoutContainer title = new HtmlLayoutContainer(
				"<center style='font-size:16px;font-weight:bold;'>" + SERVICE_INFO_TITLE + "</center>");
		sectionTitle.add(title, new MarginData());
		sectionTitle.getElement().getStyle().setMarginRight(20, Unit.PX);
		v.add(sectionTitle, new VerticalLayoutData(-1, -1, new Margins(10)));

		// subtitle
		HtmlLayoutContainer subtitle = new HtmlLayoutContainer(
				"<p style='font-size:12px;'>This page reports information on the DataMiner "
						+ "service instance serving this working environment giving an up to date "
						+ "picture of its capacities and capabilities.</p>");
		sectionSubTitle.add(subtitle, new MarginData());
		sectionSubTitle.getElement().getStyle().setMarginRight(20, Unit.PX);
		v.add(sectionSubTitle, new VerticalLayoutData(-1, -1, new Margins(10)));

	}

	private FieldSet environmentView() {
		try {
			environmentVBox = new VerticalLayoutContainer();

			FieldSet configurationFieldSet = new FieldSet();
			configurationFieldSet.setHeadingText(RUNTIME_FEATURE);
			configurationFieldSet.setCollapsible(true);
			configurationFieldSet.add(environmentVBox);
			configurationFieldSet.getElement().getStyle().setMarginRight(20, Unit.PX);
			return configurationFieldSet;
		} catch (Throwable e) {
			Log.error("Error in ServiceInfoPanel in environment: " + e.getLocalizedMessage(), e);
			throw e;
		}
	}

	private void showAddress(String address) {
		// Service address
		SimpleContainer sectionServerAddress = new SimpleContainer();

		HtmlLayoutContainer addressHtml = new HtmlLayoutContainer(
				"<p style='font-size:12px;'>The base url of the service instance is: " + "<a href='" + address + "'>"
						+ address + "</a></p>");
		sectionServerAddress.add(addressHtml, new MarginData());
		sectionServerAddress.getElement().getStyle().setMarginRight(20, Unit.PX);
		v.add(sectionServerAddress, new VerticalLayoutData(-1, -1, new Margins(10)));

	}

	private void showServiceInfo(ServiceInfo serviceInfo) {

		if (serviceInfo != null) {
			
			String address=serviceInfo.getServiceAddress();
			if(address!=null&&!address.isEmpty()){
				showAddress(address);
			}
			
			FieldSet environmentFieldSet = environmentView();
			v.add(environmentFieldSet, new VerticalLayoutData(-1, -1, new Margins(10)));

			ArrayList<ServiceInfoData> properties=serviceInfo.getServiceProperties();
			if (properties!=null&&!properties.isEmpty()) {
				Grid<ServiceInfoData> grid = createInfoGrid(properties);
				environmentVBox.add(grid, new VerticalLayoutData(1, -1, new Margins(0, 4, 0, 4)));
			} else {
				HtmlLayoutContainer emptyInfoContainer = new HtmlLayoutContainer(
						"<div class='service-property'><p>No Info Available.</p></div>");

				environmentVBox.add(emptyInfoContainer, new VerticalLayoutData(1, -1, new Margins(0, 4, 0, 4)));
			}
		} else {
			FieldSet environmentFieldSet = environmentView();
			v.add(environmentFieldSet, new VerticalLayoutData(-1, -1, new Margins(10)));

			HtmlLayoutContainer emptyInfoContainer = new HtmlLayoutContainer(
					"<div class='service-property'><p>No Info Available.</p></div>");

			environmentVBox.add(emptyInfoContainer, new VerticalLayoutData(1, -1, new Margins(0, 4, 0, 4)));
		}

		forceLayout();

	}

	private Grid<ServiceInfoData> createInfoGrid(ArrayList<ServiceInfoData> properties) {

		ColumnConfig<ServiceInfoData, String> keyCol = new ColumnConfig<ServiceInfoData, String>(props.key(), 100,
				"Key");
		ColumnConfig<ServiceInfoData, String> valueCol = new ColumnConfig<ServiceInfoData, String>(props.value(), 100,
				"Value");
		ColumnConfig<ServiceInfoData, String> categoryCol = new ColumnConfig<ServiceInfoData, String>(props.category(),
				100,"Category");

		List<ColumnConfig<ServiceInfoData, ?>> columns = new ArrayList<ColumnConfig<ServiceInfoData, ?>>();
		columns.add(keyCol);
		columns.add(valueCol);
		columns.add(categoryCol);

		ColumnModel<ServiceInfoData> cm = new ColumnModel<ServiceInfoData>(columns);

		ListStore<ServiceInfoData> store = new ListStore<ServiceInfoData>(props.id());
		store.addAll(properties);

		final GroupingView<ServiceInfoData> groupingView = new GroupingView<ServiceInfoData>();
		groupingView.setShowGroupedColumn(false);
		groupingView.groupBy(categoryCol);
		groupingView.setForceFit(true);
		groupingView.setAutoExpandColumn(valueCol);
		groupingView.setEmptyText("No info retrieved");

		Grid<ServiceInfoData> grid = new Grid<ServiceInfoData>(store, cm, groupingView);

		grid.setAllowTextSelection(true);
		grid.setBorders(false);
		grid.setColumnReordering(false);

		// Stage manager, turn on state management
		grid.setStateful(true);
		grid.setStateId("gridServiceInfoData");

		return grid;

	}

}
