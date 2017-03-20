package org.gcube.portlets.user.td.gwtservice.shared.history;

import java.io.Serializable;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class RollBackSession implements Serializable {

	private static final long serialVersionUID = -1896235499708614266L;

	protected TRId trId;
	protected Long historyId;

	public RollBackSession() {
	}

	public RollBackSession(TRId trId, Long historyId) {
		this.trId = trId;
		this.historyId = historyId;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public Long getHistoryId() {
		return historyId;
	}

	public void setHistoryId(Long historyId) {
		this.historyId = historyId;
	}

	@Override
	public String toString() {
		return "RollBackSession [trId=" + trId + ", historyId=" + historyId
				+ "]";
	}

}
