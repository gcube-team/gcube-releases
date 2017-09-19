package org.gcube.data.analysis.tabulardata.expression.dsl;

import org.gcube.data.analysis.tabulardata.expression.leaf.ColumnReferencePlaceholder;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDInteger;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDNumeric;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;

public class Types {

	public static TDText text(String value){
		return new TDText(value);
	}
	
	public static TDInteger integer(int value){
		return new TDInteger(value);
	}
	
	public static TDNumeric numeric(double value){
		return new TDNumeric(value);
	}
	
	public static ColumnReferencePlaceholder textCustomPlaceholder(){
		return new ColumnReferencePlaceholder(new TextType(), "ph","Text Column PH");
	}
	
	public static ColumnReferencePlaceholder textPlaceholder(String id){
		return new ColumnReferencePlaceholder(new TextType(), id, "Text Column PH ("+id+")");
	}
	
	public static ColumnReferencePlaceholder typedPlaceholder(String id, Class<? extends DataType> dataTypeClass){
		DataType dataType;
		try{
			dataType = dataTypeClass.newInstance();
		}catch(Exception e){
			dataType = new TextType();
		}
				
		return new ColumnReferencePlaceholder(dataType, id, "Column PH ("+id+")");
	}
}
