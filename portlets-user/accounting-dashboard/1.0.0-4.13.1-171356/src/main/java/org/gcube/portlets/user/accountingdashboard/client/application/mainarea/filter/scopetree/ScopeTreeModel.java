package org.gcube.portlets.user.accountingdashboard.client.application.mainarea.filter.scopetree;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.gcube.portlets.user.accountingdashboard.client.application.event.RequestReportEvent;
import org.gcube.portlets.user.accountingdashboard.client.application.event.RequestReportEvent.RequestReportEventHandler;
import org.gcube.portlets.user.accountingdashboard.shared.data.ScopeData;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class ScopeTreeModel implements TreeViewModel {

	private static Logger logger = Logger.getLogger("");

	private ListDataProvider<ScopeData> dataProvider;

	private final SingleSelectionModel<ScopeData> selectionModel = new SingleSelectionModel<>();

	public ScopeTreeModel(ListDataProvider<ScopeData> dataProvider, final RequestReportEventHandler handler) {
		this.dataProvider = dataProvider;

		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {

			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				@SuppressWarnings("rawtypes")
				SingleSelectionModel noSelectionModel = (SingleSelectionModel) event.getSource();
				ScopeData scopeDataSelected = (ScopeData) noSelectionModel.getSelectedObject();
				logger.fine("Selected: " + scopeDataSelected);
				RequestReportEvent requestEvent = new RequestReportEvent(scopeDataSelected);
				handler.onData(requestEvent);

			}
		});
	}
	
	
	public void setSelected(ScopeData item, boolean selected){
		selectionModel.setSelected(item, selected);
	}
	
	public boolean isSelected(ScopeData item){
		return selectionModel.isSelected(item);
	}
	
	

	@Override
	public <T> NodeInfo<?> getNodeInfo(T value) {
		if (value == null) {
			// LEVEL 0.
			// We passed null as the root value. Return the composers.
			// Create a data provider that contains the list of composers.

			// Create a cell to display a composer.
			Cell<ScopeData> cell = new AbstractCell<ScopeData>() {
				@Override
				public void render(Context context, ScopeData value, SafeHtmlBuilder sb) {
					if (value != null) {
						sb.appendEscaped(value.getName());
					}
				}
			};

			// Return a node info that pairs the data provider and the cell.
			return new DefaultNodeInfo<ScopeData>(dataProvider, cell, selectionModel, null);
		} else if (value instanceof ScopeData) {
			// LEVEL 1.
			// We want the children of the composer. Return the playlists.
			ListDataProvider<ScopeData> dataProvider = new ListDataProvider<ScopeData>(
					((ScopeData) value).getChildren());
			Cell<ScopeData> cell = new AbstractCell<ScopeData>() {

				@Override
				public void render(Context context, ScopeData value, SafeHtmlBuilder sb) {
					if (value != null) {
						sb.appendEscaped(value.getName());
					}
				}
			};
			
			return new DefaultNodeInfo<ScopeData>(dataProvider, cell, selectionModel, null);
		} 

		/*
		 * else if (value instanceof ScopeData) {
		 * 
		 * // LEVEL 2 - LEAF. // We want the children of the playlist. Return
		 * the songs. ListDataProvider<String> dataProvider = new
		 * ListDataProvider<String>(((ScopeData) value).geSongs()); // Use the
		 * shared selection model. return new
		 * DefaultNodeInfo<String>(dataProvider, new TextCell(), selectionModel,
		 * null); }
		 */
		return null;
	}

	@Override
	public boolean isLeaf(Object value) {
		// The leaf nodes are the songs, which are Strings.
		if (value != null && value instanceof ScopeData) {
			ArrayList<ScopeData> childrens = ((ScopeData) value).getChildren();
			if (childrens == null || childrens.isEmpty()) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
}