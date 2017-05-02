package org.gcube.portlets.user.td.gwtservice.shared.tr.groupby;

import java.io.Serializable;
import java.util.HashMap;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class TimeAggregationSession implements Serializable {

	private static final long serialVersionUID = 7297177399486247575L;

	protected TRId trId;
	protected ColumnData column;
	protected HashMap<String, Object> map;

	public TimeAggregationSession() {

	}

	/**
	 * 
	 * @param trId
	 * @param column
	 * @param map
	 */
	public TimeAggregationSession(TRId trId, ColumnData column,
			HashMap<String, Object> map) {
		super();
		this.trId = trId;
		this.column = column;
		this.map = map;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public ColumnData getColumn() {
		return column;
	}

	public void setColumn(ColumnData column) {
		this.column = column;
	}

	public HashMap<String, Object> getMap() {
		return map;
	}

	public void setMap(HashMap<String, Object> map) {
		this.map = map;
	}

	@Override
	public String toString() {
		return "TimeAggregationSession [trId=" + trId + ", column=" + column
				+ ", map=" + map + "]";
	}

}
