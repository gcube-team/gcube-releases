package org.gcube.portlets.admin.tokengeneratoradmin.shared;

import java.io.Serializable;

/**
 * Valid port range bean
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
public class PortRange implements Serializable {

	private static final long serialVersionUID = -5494327497265764907L;
	private int start;
	private int end;
	/**
	 * 
	 */
	public PortRange() {
		super();
	}
	/**
	 * @param start
	 * @param end
	 */
	public PortRange(int start, int end) {
		super();
		this.start = start;
		this.end = end;
	}
	
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	@Override
	public String toString() {
		return "PortRange [start=" + start + ", end=" + end + "]";
	}	
}
