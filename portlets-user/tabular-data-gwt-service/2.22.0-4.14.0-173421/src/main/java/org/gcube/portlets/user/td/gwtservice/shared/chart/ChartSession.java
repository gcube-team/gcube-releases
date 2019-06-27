package org.gcube.portlets.user.td.gwtservice.shared.chart;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.user.UserInfo;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

import com.google.web.bindery.event.shared.EventBus;

/**
 * Chart Session
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class ChartSession implements Serializable {

	private static final long serialVersionUID = -51554111438593905L;
	private TRId trId;
	private UserInfo userInfo;
	private EventBus eventBus;
	private ArrayList<ColumnData> columns;
	
	public ChartSession() {
		super();

	}

	public ChartSession(TRId trId, UserInfo userInfo, EventBus eventBus,
			ArrayList<ColumnData> columns) {
		super();
		this.trId = trId;
		this.userInfo = userInfo;
		this.eventBus = eventBus;
		this.columns = columns;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public EventBus getEventBus() {
		return eventBus;
	}

	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	public ArrayList<ColumnData> getColumns() {
		return columns;
	}

	public void setColumns(ArrayList<ColumnData> columns) {
		this.columns = columns;
	}

	@Override
	public String toString() {
		return "ChartSession [trId=" + trId + ", userInfo=" + userInfo
				+ ", eventBus=" + eventBus + ", columns=" + columns + "]";
	}

	

}
