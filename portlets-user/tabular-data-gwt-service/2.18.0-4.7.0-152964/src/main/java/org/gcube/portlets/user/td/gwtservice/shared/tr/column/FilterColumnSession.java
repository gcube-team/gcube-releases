package org.gcube.portlets.user.td.gwtservice.shared.tr.column;

import java.io.Serializable;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class FilterColumnSession implements Serializable {

	private static final long serialVersionUID = -5362632291599472352L;
	private TRId trId;
	private C_Expression cexpression;

	public FilterColumnSession() {

	}

	public FilterColumnSession(TRId trId, C_Expression cexpression) {
		super();
		this.trId = trId;
		this.cexpression = cexpression;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public C_Expression getCexpression() {
		return cexpression;
	}

	public void setCexpression(C_Expression cexpression) {
		this.cexpression = cexpression;
	}

	@Override
	public String toString() {
		return "FilterColumnSession [trId=" + trId + ", cexpression="
				+ cexpression + "]";
	}

}
