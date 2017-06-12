package org.gcube.data.analysis.tabulardata.model.column.type;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CodeDescriptionColumnType extends ColumnType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3681472510668760130L;

	@Override
	public String getCode() {
		return "CODEDESCRIPTION";
	}

	@Override
	public String getName() {
		return "Code Description";
	}

	private static final List<Class<? extends DataType>> allowedDataTypes=new ArrayList<>();
	
	private static final DataType DEFAULT=new TextType();
	
	static {
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
