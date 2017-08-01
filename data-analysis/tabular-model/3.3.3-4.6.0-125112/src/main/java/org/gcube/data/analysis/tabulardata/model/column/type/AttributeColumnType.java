package org.gcube.data.analysis.tabulardata.model.column.type;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.DateType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AttributeColumnType extends ColumnType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3425897472780763254L;

	@Override
	public String getCode() {
		return "ATTRIBUTE";
	}

	@Override
	public String getName() {
		return "Attribute";
	}

	private static final List<Class<? extends DataType>> allowedDataTypes=new ArrayList<>();
	
	private static final DataType DEFAULT=new TextType();
	
	static {
		allowedDataTypes.add(BooleanType.class);
		allowedDataTypes.add(DateType.class);
		allowedDataTypes.add(GeometryType.class);
		allowedDataTypes.add(IntegerType.class);
		allowedDataTypes.add(NumericType.class);
		allowedDataTypes.add(TextType.class);		
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
