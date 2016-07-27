package org.gcube.portlets.user.tdw.datasource.td.map;

import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;


public class ColumnTypeMap {
		
	public static boolean isIdColumnType(ColumnType columnType){
		if(columnType instanceof  IdColumnType){
			return true;
		} else {
			return false;
		}
		
	}
}
