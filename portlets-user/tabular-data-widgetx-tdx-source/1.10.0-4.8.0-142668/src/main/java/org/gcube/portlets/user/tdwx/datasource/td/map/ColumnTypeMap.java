package org.gcube.portlets.user.tdwx.datasource.td.map;

import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.MeasureColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ColumnTypeMap {

	/*
	 * public static org.gcube.portlets.user.tdwx.shared.model.ColumnType
	 * map(ColumnType columnType){ if(columnType==null){ return null; }
	 * 
	 * if(columnType instanceof AnnotationColumnType){
	 * 
	 * }
	 * 
	 * if(columnType instanceof AttributeColumnType){
	 * 
	 * } }
	 */

	public static boolean isIdColumnType(ColumnType columnType) {
		if (columnType instanceof IdColumnType) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isMeasureColumnType(ColumnType columnType) {
		if (columnType instanceof MeasureColumnType) {
			return true;
		} else {
			return false;
		}

	}

	public static boolean isCodeColumnType(ColumnType columnType) {
		if (columnType instanceof CodeColumnType) {
			return true;
		} else {
			return false;
		}

	}

	public static boolean isValidationColumnType(ColumnType columnType) {
		if (columnType instanceof ValidationColumnType) {
			return true;
		} else {
			return false;
		}

	}

	public static boolean isDimensionColumnType(ColumnType columnType) {
		if (columnType instanceof DimensionColumnType) {
			return true;
		} else {
			return false;
		}

	}

	public static boolean isTimeDimensionColumnType(ColumnType columnType) {
		if (columnType instanceof TimeDimensionColumnType) {
			return true;
		} else {
			return false;
		}

	}

}
