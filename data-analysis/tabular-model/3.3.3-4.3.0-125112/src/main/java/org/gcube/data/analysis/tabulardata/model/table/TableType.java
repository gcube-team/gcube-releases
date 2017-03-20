package org.gcube.data.analysis.tabulardata.model.table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlSeeAlso;

import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
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
import org.gcube.data.analysis.tabulardata.model.table.type.CodelistTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.DatasetTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.DatasetViewTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.GenericTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.HierarchicalCodelistTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.TimeCodelistTableType;
@XmlSeeAlso({
	CodelistTableType.class,
	DatasetTableType.class,
	DatasetViewTableType.class,
	GenericTableType.class,
	HierarchicalCodelistTableType.class,
	TimeCodelistTableType.class
})
		
public abstract class TableType implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6841440717260930667L;

	private static final List<ColumnType> DEFAULT_ALLOWED_COLUMN_TYPES=new ArrayList<>();
	
	static{
		DEFAULT_ALLOWED_COLUMN_TYPES.add(new AnnotationColumnType());
		DEFAULT_ALLOWED_COLUMN_TYPES.add(new AttributeColumnType());
		DEFAULT_ALLOWED_COLUMN_TYPES.add(new CodeColumnType());
		DEFAULT_ALLOWED_COLUMN_TYPES.add(new CodeDescriptionColumnType());
		DEFAULT_ALLOWED_COLUMN_TYPES.add(new CodeNameColumnType());
		DEFAULT_ALLOWED_COLUMN_TYPES.add(new DimensionColumnType());
		DEFAULT_ALLOWED_COLUMN_TYPES.add(new MeasureColumnType());
		DEFAULT_ALLOWED_COLUMN_TYPES.add(new TimeDimensionColumnType());
		DEFAULT_ALLOWED_COLUMN_TYPES.add(new ValidationColumnType());
		DEFAULT_ALLOWED_COLUMN_TYPES.add(new IdColumnType());
	}
	
	public abstract String getCode();
	
	public abstract String getName();
	
	@Override
	public int hashCode(){
		return getCode().hashCode();
	}
	
	@Override
	public boolean equals(Object obj){
		return this.getClass().equals(obj.getClass());
	}
	
	@Override
	public String toString(){
		return getCode();
	}
	
	public List<ColumnType> getAllowedColumnTypes(){
		return DEFAULT_ALLOWED_COLUMN_TYPES;
	}
}
