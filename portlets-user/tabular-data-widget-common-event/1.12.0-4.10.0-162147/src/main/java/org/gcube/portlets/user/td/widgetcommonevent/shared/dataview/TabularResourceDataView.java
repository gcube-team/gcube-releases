package org.gcube.portlets.user.td.widgetcommonevent.shared.dataview;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class TabularResourceDataView extends DataView {

	private static final long serialVersionUID = -8615122839978608904L;

	private TRId trId;
	private String tabName;
	

	public TabularResourceDataView() {
		super();
		dataViewType = DataViewType.GRID;

	}

	public TabularResourceDataView(TRId trId) {
		super();
		dataViewType = DataViewType.GRID;
		this.trId = trId;
		this.tabName = null;
	}
	
	public TabularResourceDataView(TRId trId, String tabName) {
		super();
		dataViewType = DataViewType.GRID;
		this.trId = trId;
		this.tabName = tabName;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	@Override
	public String toString() {
		return "TabularResourceDataView [trId=" + trId + ", tabName=" + tabName
				+ "]";
	}

	
	

}
