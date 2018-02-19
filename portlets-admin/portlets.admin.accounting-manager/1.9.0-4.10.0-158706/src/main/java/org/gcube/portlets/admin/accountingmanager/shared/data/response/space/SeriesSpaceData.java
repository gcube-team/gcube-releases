package org.gcube.portlets.admin.accountingmanager.shared.data.response.space;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class SeriesSpaceData implements Serializable {

	private static final long serialVersionUID = -3946372711632911008L;
	private Date date;
	private Long dataVolume;

	public SeriesSpaceData() {
		super();
	}

	public SeriesSpaceData(Date date, Long dataVolume) {
		super();
		this.date = date;
		this.dataVolume = dataVolume;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Long getDataVolume() {
		return dataVolume;
	}

	public void setDataVolume(Long dataVolume) {
		this.dataVolume = dataVolume;
	}

	@Override
	public String toString() {
		return "SeriesSpaceData [date=" + date + ", dataVolume=" + dataVolume
				+ "]";
	}

}
