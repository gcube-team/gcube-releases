package org.gcube.portlets.user.accountingdashboard.client.application.mainarea.filter;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.gcube.portlets.user.accountingdashboard.client.application.event.RequestReportEvent;
import org.gcube.portlets.user.accountingdashboard.client.application.event.RequestReportEvent.RequestReportEventHandler;
import org.gcube.portlets.user.accountingdashboard.client.application.mainarea.filter.scopetree.ScopeTreeModel;
import org.gcube.portlets.user.accountingdashboard.client.resources.AppResources;
import org.gcube.portlets.user.accountingdashboard.client.resources.ScopeTreeResources;
import org.gcube.portlets.user.accountingdashboard.shared.data.RequestReportData;
import org.gcube.portlets.user.accountingdashboard.shared.data.ScopeData;

import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.TreeNode;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class FilterAreaView extends ViewWithUiHandlers<FilterAreaPresenter>
		implements FilterAreaPresenter.FilterAreaView {

	private static Logger logger = java.util.logging.Logger.getLogger("");

	interface Binder extends UiBinder<Widget, FilterAreaView> {
	}

	@UiField
	HTMLPanel periodPanel;

	@UiField
	HTMLPanel explorePanel;

	@UiField(provided = true)
	ListBox yearStart;

	@UiField(provided = true)
	ListBox monthStart;

	@UiField(provided = true)
	ListBox yearEnd;

	@UiField(provided = true)
	ListBox monthEnd;

	@UiField(provided = true)
	CellTree scopeTree;

	private ListDataProvider<ScopeData> dataProvider;
	private ScopeTreeModel scopeTreeModel;
	
	@SuppressWarnings("unused")
	private AppResources resources;

	private ScopeData scopeData;

	@Inject
	FilterAreaView(Binder uiBinder, AppResources resources) {
		this.resources = resources;
		init();
		initWidget(uiBinder.createAndBindUi(this));

	}

	private void init() {
		yearStart = new ListBox();
		yearStart.setMultipleSelect(false);

		yearEnd = new ListBox();
		yearEnd.setMultipleSelect(false);

		Date now = new Date();
		String currentYear = DateTimeFormat.getFormat(PredefinedFormat.YEAR).format(now);
		logger.log(Level.FINE, "Current year: " + currentYear);
		int year = Integer.parseInt(currentYear);
		for (int i = 2015; i <= year; i++) {
			yearStart.addItem(String.valueOf(i));
			yearEnd.addItem(String.valueOf(i));
		}
		
		String yearStartDefaultValue;
		if(year==2015){
			yearStartDefaultValue="2015";
		} else {
			yearStartDefaultValue=String.valueOf(year-1);
		}
		yearStart.setSelectedValue(yearStartDefaultValue);
		yearEnd.setSelectedValue(String.valueOf(year));

		yearStart.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				requestReport();

			}
		});

		yearEnd.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				requestReport();

			}
		});

		// returns a String array with localized names of the months
		String[] months = LocaleInfo.getCurrentLocale().getDateTimeFormatInfo().monthsFull();
		logger.log(Level.FINE, "Months: " + months.length);
		monthStart = new ListBox();
		monthStart.setMultipleSelect(false);

		monthEnd = new ListBox();
		monthEnd.setMultipleSelect(false);
		
		for (int i = 0; i < months.length; i++) {
			monthStart.addItem(months[i]);
			monthEnd.addItem(months[i]);
		}

		String currentMonth = DateTimeFormat.getFormat(PredefinedFormat.MONTH).format(now);

		monthStart.setSelectedValue(currentMonth);
		monthEnd.setSelectedValue(currentMonth);

		monthStart.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				requestReport();

			}
		});

		monthEnd.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				requestReport();

			}
		});

		dataProvider = new ListDataProvider<ScopeData>();

		RequestReportEventHandler handler = new RequestReportEventHandler() {

			@Override
			public void onData(RequestReportEvent event) {
				scopeData = event.getScopeData();
				requestReport();

			}
		};

		scopeTreeModel = new ScopeTreeModel(dataProvider, handler);
		ScopeTreeResources scopeTreeResources = GWT.create(ScopeTreeResources.class);
		scopeTree = new CellTree(scopeTreeModel, null, scopeTreeResources);
		scopeTree.setDefaultNodeSize(500);
	}

	@Override
	public void displayScopeData(ScopeData scopeData) {
		ArrayList<ScopeData> scopeDataList = new ArrayList<>();
		scopeDataList.add(scopeData);
		this.scopeData = scopeData;
		dataProvider.setList(scopeDataList);
		dataProvider.refresh();
		dataProvider.flush();
		TreeNode root = scopeTree.getRootTreeNode();
		root.setChildOpen(root.getIndex(), true);
		scopeTreeModel.setSelected(scopeData, true);
		requestReport();
	}

	private void requestReport() {
		String[] months = LocaleInfo.getCurrentLocale().getDateTimeFormatInfo().monthsFull();
		logger.log(Level.FINE, "Months: " + months.length);
		
		String yearS = yearStart.getValue();
		String monthS = monthStart.getValue();
		
		String monthSN=null;
		for(int i=0; i<months.length; i++){
			if(months[i].compareTo(monthS)==0){
				int v=i+1;
				if(v>9){
					monthSN=""+v;
				} else {
					monthSN="0"+v;
				}
			}
		}
		
		String dateStart = yearS + "-" + monthSN + "-01";
		logger.fine("DateStart: " + dateStart);
		
		String yearE = yearEnd.getValue();
		String monthE = monthEnd.getValue();

		String monthEN=null;
		for(int i=0; i<months.length; i++){
			if(months[i].compareTo(monthE)==0){
				int v=i+1;
				if(v>9){
					monthEN=""+v;
				} else {
					monthEN="0"+v;
				}
			}
		}
		
		String dateEnd = yearE + "-" + monthEN + "-01";
		logger.fine("DateEnd: " + dateEnd);

		RequestReportData requestReportData = new RequestReportData(scopeData, dateStart, dateEnd);
		getUiHandlers().getReport(requestReportData);

	}

}
