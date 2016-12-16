package org.gcube.portlets.admin.accountingmanager.client.maindata.charts.utils;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ChartDateTimeData implements Serializable {

	private static final long serialVersionUID = -2346476874965664631L;
	private Date date;
	private Long value;

	public ChartDateTimeData() {
		super();
	}

	/**
	 * 
	 * @param date
	 * @param value
	 */
	public ChartDateTimeData(Date date, Long value) {
		super();
		this.date = date;
		this.value = value;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "ChartDateTimeData [date=" + date + ", value=" + value + "]";
	}

}
