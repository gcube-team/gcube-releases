package org.gcube.portlets.admin.accountingmanager.shared.data.response.storage;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SeriesStorageData implements Serializable {

	private static final long serialVersionUID = -192149405907077352L;

	private Date date;
	private Long dataVolume;
	private Long operationCount;

	public SeriesStorageData() {
		super();
	}

	/**
	 * 
	 * @param date
	 * @param dataVolume
	 * @param operationCount
	 */
	public SeriesStorageData(Date date, Long dataVolume, Long operationCount) {
		super();
		this.date = date;
		this.dataVolume = dataVolume;
		this.operationCount = operationCount;
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

	public Long getOperationCount() {
		return operationCount;
	}

	public void setOperationCount(Long operationCount) {
		this.operationCount = operationCount;
	}

	@Override
	public String toString() {
		return "SeriesStorageData [date=" + date + ", dataVolume=" + dataVolume
				+ ", operationCount=" + operationCount + "]";
	}

}
