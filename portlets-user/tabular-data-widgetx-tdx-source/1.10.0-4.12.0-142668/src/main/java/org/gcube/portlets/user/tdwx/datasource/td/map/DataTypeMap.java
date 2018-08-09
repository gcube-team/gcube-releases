package org.gcube.portlets.user.tdwx.datasource.td.map;

import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.DateType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
//import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.portlets.user.tdwx.shared.model.ValueType;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class DataTypeMap {
	
	public static ValueType getValueType(DataType dataType){
		if( dataType instanceof BooleanType){
			return ValueType.BOOLEAN;
		}
		if( dataType instanceof DateType){
			return ValueType.DATE;
		} 
		if( dataType instanceof IntegerType){
			return ValueType.INTEGER;
		} 
		if( dataType instanceof GeometryType){
			return ValueType.GEOMETRY;
		} 
		if( dataType instanceof NumericType){
			return ValueType.DOUBLE;
		} 
		
		return ValueType.STRING;
	};
	
}
