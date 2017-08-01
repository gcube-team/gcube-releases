package org.gcube.portlets.admin.accountingmanager.client.custom;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.sencha.gxt.chart.client.chart.axis.CategoryAxis;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.Store.StoreFilter;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;

/**
 * 
 * @author Giancarlo Panichi
 *
 * @param <M> type of axis
 */
public class FormatedTimeAxis<M> extends CategoryAxis<M, String> {
	private Date startDate;
	private Date endDate;
	private ListStore<M> substore;
	private StoreSortInfo<M> sort;
	private StoreFilter<M> filter;
	private DateTimeFormat dateTimeFormat;

	/**
	 * Creates a time axis.
	 * 
	 * @param dateTimeFormat date time format
	 */
	public FormatedTimeAxis(DateTimeFormat dateTimeFormat) {
		super();
		this.dateTimeFormat = dateTimeFormat;
	}

	/**
	 * Returns the ending date of the axis.
	 * 
	 * @return the ending date of the axis
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Returns the starting date of the axis.
	 * 
	 * @return the starting date of the axis
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Sets the ending date of the axis.
	 * 
	 * @param endDate
	 *            the ending date of the axis
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Sets the starting date of the axis.
	 * 
	 * @param startDate
	 *            the starting date of the axis
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Override
	protected void applyData() {
		if (sort == null) {
			sort = new StoreSortInfo<M>(field, SortDir.ASC);
			filter = new StoreFilter<M>() {
				@Override
				public boolean select(Store<M> store, M parent, M item) {
					String stringValue = field.getValue(item);
					Date value = dateTimeFormat.parse(stringValue);
					boolean result = value.after(startDate)
							&& value.before(endDate) || value.equals(startDate)
							|| value.equals(endDate);
					return result;
				}
			};
		}
		ListStore<M> store = chart.getStore();
		substore = new ListStore<M>(store.getKeyProvider());
		substore.addSortInfo(sort);
		substore.addFilter(filter);
		substore.setEnableFilters(true);
		substore.addAll(store.getAll());
		chart.setSubstore(substore);
		super.applyData();
	}

	@Override
	protected void createLabels() {
		labelNames.clear();
		for (int i = 0; i < substore.size(); i++) {
			labelNames.add(field.getValue(substore.get(i)));
		}
	}

}
