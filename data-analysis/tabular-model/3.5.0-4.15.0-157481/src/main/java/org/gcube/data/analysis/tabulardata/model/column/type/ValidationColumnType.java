package org.gcube.data.analysis.tabulardata.model.column.type;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ValidationColumnType extends ColumnType {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5391784848497556743L;

	@Override
	public String getCode() {
		return "VALIDATION";
	}

	@Override
	public String getName() {
		return "Validation";
	}

	private static final List<Class<? extends DataType>> allowedDataTypes=new ArrayList<>();
	
	private static final DataType DEFAULT=new BooleanType();
	
	static {
		allowedDataTypes.add(BooleanType.class);
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
