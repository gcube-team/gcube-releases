package org.gcube.data.analysis.tabulardata.model.column.type;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DimensionColumnType extends ColumnType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6077284106250703341L;

	@Override
	public String getCode() {
		return "DIMENSION";
	}

	@Override
	public String getName() {
		return "Dimension";
	}

	private static final List<Class<? extends DataType>> allowedDataTypes=new ArrayList<>();
	
	private static final DataType DEFAULT=new IntegerType();
	
	static {
		allowedDataTypes.add(IntegerType.class);		
	}
	
	@Override
	public List<Class<? extends DataType>> getAllowedDataTypes() {
		return allowedDataTypes;
	}
	
	@Override
	public DataType getDefaultDataType() {		
		return DEFAULT;
	}
}
