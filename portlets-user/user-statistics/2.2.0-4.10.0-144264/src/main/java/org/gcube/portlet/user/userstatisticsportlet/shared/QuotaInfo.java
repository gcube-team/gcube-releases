package org.gcube.portlet.user.userstatisticsportlet.shared;

import java.io.Serializable;

/**
 * A quota info bean: maximum and current value are contained in it.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class QuotaInfo implements Serializable{

	private static final long serialVersionUID = -7823313890536756579L;
	private Float max;
	private Float current;
	public QuotaInfo() {
		super();
	}

	public QuotaInfo(Float max, Float current) {
		super();
		this.max = max;
		this.current = current;
	}
	public Float getMax() {
		return max;
	}
	public void setMax(Float max) {
		this.max = max;
	}
	public Float getCurrent() {
		return current;
	}
	public void setCurrent(Float current) {
		this.current = current;
	}
	@Override
	public String toString() {
		return "QuotaInfo [max=" + max + ", current=" + current + "]";
	}
}
