package org.gcube.portlets.user.td.gwtservice.server.trservice;

import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AnnotationColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeDescriptionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeNameColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.MeasureColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;


/**
 * 
 * @author "Giancarlo Panichi"
 *
 */
public class ColumnTypeCodeMap {
	
	public static ColumnType getColumnType(ColumnTypeCode columnTypeCode){
		
		switch(columnTypeCode){
		case ANNOTATION:
			return new  AnnotationColumnType();
		case ATTRIBUTE:
			return new AttributeColumnType();
		case CODE:
			return new CodeColumnType();
		case CODEDESCRIPTION:
			return new CodeDescriptionColumnType();
		case CODENAME:
			return new CodeNameColumnType();
		case DIMENSION:
			return new DimensionColumnType();
		case MEASURE:
			return new MeasureColumnType();
		case TIMEDIMENSION:
			return new TimeDimensionColumnType();
		default:
			return null;
		
		}
	}
}
