package org.gcube.data.analysis.tabulardata.model.column;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlSeeAlso;

import org.gcube.data.analysis.tabulardata.model.column.type.AnnotationColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeDescriptionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeNameColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.MeasureColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;

@XmlSeeAlso({
	AnnotationColumnType.class,
	AttributeColumnType.class,
	CodeColumnType.class,
	CodeDescriptionColumnType.class,
	CodeNameColumnType.class,
	DimensionColumnType.class,
	IdColumnType.class,
	MeasureColumnType.class,
	TimeDimensionColumnType.class,
	ValidationColumnType.class
})
public abstract class ColumnType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6969889657580992015L;

	public abstract String getCode();
	
	public abstract String getName();
	
	@Override
	public int hashCode(){
		return getCode().hashCode();
	}
	
	@Override
	public boolean equals(Object obj){
		if (this.getClass().equals(obj.getClass())) return true;
		return false;
	}
	
	@Override
	public String toString() {
		return getCode();
	}
	
	
	public abstract List<Class<? extends DataType>> getAllowedDataTypes();
	
	public abstract DataType getDefaultDataType();
	
	public boolean isDataTypeAllowed(DataType toCheck){
		return getAllowedDataTypes().contains(toCheck.getClass());
	}
}