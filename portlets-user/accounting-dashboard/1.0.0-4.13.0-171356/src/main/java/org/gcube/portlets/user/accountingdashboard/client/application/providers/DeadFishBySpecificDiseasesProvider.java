package org.gcube.portlets.user.accountingdashboard.client.application.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.user.accountingdashboard.client.application.controller.Controller;
import org.gcube.portlets.user.accountingdashboard.shared.data.ScopeData;

import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.inject.Inject;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class DeadFishBySpecificDiseasesProvider extends AsyncDataProvider<ScopeData> {
	private static Logger logger = java.util.logging.Logger.getLogger("");

	@SuppressWarnings("unused")
	private Controller controller;
	private int start;
	private int length;
	private int columnSortIndex;
	private boolean ascending = false;
	private String id;
	private ArrayList<ScopeData> list;

	@Inject
	public DeadFishBySpecificDiseasesProvider(String id, Controller controller) {
		this.id = id;
		this.controller = controller;
		list = new ArrayList<>();
		bindEvents();
	}

	private void bindEvents() {
		/*
		 * EventBus eventBus = controller.getEventBus();
		 * eventBus.addHandler(DatasetsEvent.TYPE, new
		 * DatasetsEvent.DatasetsEventHandler() {
		 * 
		 * @Override public void onDatasets(DatasetsEvent event) {
		 * update(event.getList());
		 * 
		 * } });
		 */
	}

	private void update(ArrayList<ScopeData> list) {

		logger.log(Level.FINE, "Disease Data Display is empty: " + getDataDisplays().isEmpty());

		logger.log(Level.FINE, "Show=[Start=" + start + ", Length=" + length + "]");

		int limits = start + length;
		if (limits > list.size()) {
			limits = list.size();
			if (limits < start) {
				start = limits;
			}
		}

		List<ScopeData> dataInRange = new ArrayList<>();
		if (list != null && list.size() > 1) {
			if (columnSortIndex > -1) {
				logger.log(Level.FINE, "ColumnSortIndex: " + columnSortIndex);
				logger.log(Level.FINE, "Ascending: " + ascending);
				Comparator<ScopeData> comparator;
				switch (columnSortIndex) {
				case 0:
					comparator = new Comparator<ScopeData>() {
						public int compare(ScopeData d1, ScopeData d2) {
							if (d1 == d2) {
								return 0;
							}

							int diff = -1;
							if (d1 == null) {
								diff = -1;
							} else {
								if (d2 == null) {
									diff = 1;
								} else {

									if (d1 != null) {
										if (d1.getName() != null) {
											diff = ((d2 != null) && (d2.getName() != null))
													? d1.getName().compareTo(d2.getName()) : 1;
										}
									}

								}
							}
							return ascending ? -diff : diff;
						}
					};
					break;
				default:
					comparator = new Comparator<ScopeData>() {
						public int compare(ScopeData d1, ScopeData d2) {
							if (d1 == d2) {
								return 0;
							}

							int diff = -1;
							if (d1 == null) {
								diff = -1;
							} else {
								if (d2 == null) {
									diff = 1;
								} else {

									if (d1 != null) {
										if (d1.getName() != null) {
											diff = ((d2 != null) && (d2.getName() != null))
													? d1.getName().compareTo(d2.getName()) : 1;
										}
									}

								}
							}
							return ascending ? -diff : diff;
						}
					};
					break;
				}

				Collections.sort(list, comparator);

			}

			dataInRange = list.subList(start, limits);
		} else {
			dataInRange = list;
		}
		this.updateRowCount(list.size(), true);
		this.updateRowData(start, dataInRange);

	}

	@Override
	protected void onRangeChanged(HasData<ScopeData> display) {
		Range range = display.getVisibleRange();
		start = range.getStart();
		length = range.getLength();
		logger.log(Level.FINE, "Diseases Provider Range Change: [start=" + start + ", length=" + length + "]");
		retrieveData();
	}

	public void onSortChanged(int start, int length, int columnSortIndex, boolean ascending) {
		logger.log(Level.FINE, "Disease Provider Sort: [start=" + start + ", length=" + length + "]");
		this.start = start;
		this.length = length;
		this.columnSortIndex = columnSortIndex;
		this.ascending = ascending;
		retrieveData();
	}

	public void onRefreshDiseases() {
		logger.log(Level.FINE, "Disease Provider Refresh");
		retrieveData();
	}

	private void retrieveData() {
		logger.log(Level.FINE, "Disease Provider: " + id);
		update(list);
		// controller.getDatasets(productId);

	}

	public void addNewDisease() {
		// TODO Auto-generated method stub

	}

}