package org.gcube.data.analysis.tabulardata.model.mapping;


import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDBoolean;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDDate;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDGeometry;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDInteger;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDNumeric;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;

public interface SQLModelMapper {

	public abstract String translateModelValueToSQL(TDBoolean value);

	public abstract String translateModelValueToSQL(TDText value);
	
	public abstract String translateModelValueToSQL(TDGeometry value);

	public abstract String translateModelValueToSQL(TDInteger value);

	public abstract String translateModelValueToSQL(TDNumeric value);

	public abstract String translateModelValueToSQL(TDDate value);

	public abstract String translateModelValueToSQL(TDTypeValue value);

	public abstract String translateDataTypeToSQL(DataType type);

}