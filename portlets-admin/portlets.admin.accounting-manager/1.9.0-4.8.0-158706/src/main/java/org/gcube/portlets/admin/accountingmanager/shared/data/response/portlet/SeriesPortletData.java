package org.gcube.portlets.admin.accountingmanager.shared.data.response.portlet;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class SeriesPortletData implements Serializable {
	
	private static final long serialVersionUID = -4812847957210767836L;
	private Date date;
	private Long operationCount;
	
	public SeriesPortletData(){
		super();
	}

	public SeriesPortletData(Date date, Long operationCount) {
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
		return "SeriesPortletData [date=" + date + ", operationCount="
				+ operationCount + "]";
	}
	
		
	
}
