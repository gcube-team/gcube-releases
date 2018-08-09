package org.gcube.data.analysis.tabulardata.model.table.type;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.MeasureColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;
import org.gcube.data.analysis.tabulardata.model.table.TableType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DatasetTableType extends TableType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8337209632642614267L;

	private static final List<ColumnType> ALLOWED_COLUMN_TYPES=new ArrayList<>();
	
	static{		
		ALLOWED_COLUMN_TYPES.add(new AttributeColumnType());		
		ALLOWED_COLUMN_TYPES.add(new DimensionColumnType());
		ALLOWED_COLUMN_TYPES.add(new MeasureColumnType());
		ALLOWED_COLUMN_TYPES.add(new TimeDimensionColumnType());
		ALLOWED_COLUMN_TYPES.add(new ValidationColumnType());
		ALLOWED_COLUMN_TYPES.add(new IdColumnType());
	}
	
	
	@Override
	public String getCode() {
		return "DATASET";
	}

	@Override
	public String getName() {
		return "Dataset";
	}
	
	public List<ColumnType> getAllowedColumnTypes(){
		return ALLOWED_COLUMN_TYPES;
	}
}
