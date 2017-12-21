package org.gcube.portlets.user.td.gwtservice.shared.tr.column;

import java.io.Serializable;
import java.util.HashMap;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class LabelColumnSession implements Serializable {

	private static final long serialVersionUID = -1896235499708614266L;

	private TRId trId;
	private HashMap<ColumnData, String> maps;

	public LabelColumnSession() {

	}

	public LabelColumnSession(TRId trId, HashMap<ColumnData, String> maps) {
		super();
		this.trId = trId;
		this.maps = maps;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public HashMap<ColumnData, String> getMaps() {
		return maps;
	}

	public void setMaps(HashMap<ColumnData, String> maps) {
		this.maps = maps;
	}

	@Override
	public String toString() {
		return "LabelColumnSession [trId=" + trId + ", maps=" + maps + "]";
	}

}
