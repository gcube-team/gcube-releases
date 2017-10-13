package org.gcube.portlets.user.td.gwtservice.shared.tr.groupby;

import java.io.Serializable;
import java.util.HashMap;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class GroupBySession implements Serializable {

	private static final long serialVersionUID = -1896235499708614266L;

	protected TRId trId;
	protected HashMap<String, Object> map;

	public GroupBySession() {

	}

	public GroupBySession(TRId trId, HashMap<String, Object> map) {
		this.trId = trId;
		this.map = map;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public HashMap<String, Object> getMap() {
		return map;
	}

	public void setMap(HashMap<String, Object> map) {
		this.map = map;
	}

	@Override
	public String toString() {
		return "GroupBySession [trId=" + trId + ", map=" + map + "]";
	}

}
