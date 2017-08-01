package org.gcube.portlets.admin.accountingmanager.shared.data.response.task;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class SeriesTaskData implements Serializable {

	private static final long serialVersionUID = -3981690521755233678L;
	private Date date;
	private Long operationCount;

	public SeriesTaskData() {
		super();
	}

	public SeriesTaskData(Date date, Long operationCount) {
		super();
		this.date = date;
		this.operationCount = operationCount;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Long getOperationCount() {
		return operationCount;
	}

	public void setOperationCount(Long operationCount) {
		this.operationCount = operationCount;
	}

	@Override
	public String toString() {
		return "SeriesTaskData [date=" + date + ", operationCount="
				+ operationCount + "]";
	}

}
