package org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl.data;

import java.util.Date;

import org.gcube.data.analysis.tabulardata.model.column.Column;

public interface DateConverter {

	public Object convertDate (Date date, Column referredColumn);
	
}
