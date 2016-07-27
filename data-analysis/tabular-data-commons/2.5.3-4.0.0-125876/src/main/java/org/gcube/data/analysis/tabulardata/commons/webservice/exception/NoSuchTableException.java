package org.gcube.data.analysis.tabulardata.commons.webservice.exception;

import javax.xml.ws.WebFault;

import org.gcube.data.analysis.tabulardata.commons.webservice.exception.beans.TabularDataExceptionBean;
import org.gcube.data.analysis.tabulardata.model.table.TableId;

@WebFault
public class NoSuchTableException extends Exception {

	
/**
	 * 
	 */
	private static final long serialVersionUID = 872265953943392965L;
	
	
	private TabularDataExceptionBean faultInfo ;
	
	public NoSuchTableException(TableId id) {
		this.faultInfo = new TabularDataExceptionBean("A table with id "+id.getValue()+" does not exists");
	}
	
	public NoSuchTableException(String message, TabularDataExceptionBean faultInfo, Throwable cause){
		this.faultInfo = faultInfo;
	}
		
	public NoSuchTableException(String message, TabularDataExceptionBean faultInfo){
		this.faultInfo = faultInfo;
	}
	
	public TabularDataExceptionBean getFaultInfo(){
		return faultInfo;
	}

}
