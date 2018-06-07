package org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl.data.impl.converters;

import java.util.Date;

import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl.data.DateConverter;

public class DateDateConverter implements DateConverter{

	@Override
	public Object convertDate(Date date, Column referredColumn) {

		return date;
	}

}
