package org.gcube.data.analysis.tabulardata.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.DateType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;

import static org.gcube.data.analysis.tabulardata.model.ValueFormat.format;
import static org.gcube.data.analysis.tabulardata.model.time.TimeConstants.*;
import static org.gcube.data.analysis.tabulardata.model.NumberConstant.*;

public class DataTypeFormats {

	private static Map<Class<? extends DataType>, List<ValueFormat>> dataTypeFormats= new HashMap<Class<? extends DataType>, List<ValueFormat>>();
	
	static {
		dataTypeFormats.put(BooleanType.class, Arrays.asList(format("BOOL TEXT","^([Ff][Aa][Ll][Ss][Ee]|[Tt][Rr][Uu][Ee])$","false|true"),format("BOOL NUMBER","^(0|1)$","0|1")));
		dataTypeFormats.put(DateType.class, Arrays.asList(ISO_DATE_ANY_SEP, EUROPEAN_DATE, US_DATE));
		dataTypeFormats.put(GeometryType.class, Arrays.asList(format("POINT","^POINT\\(\\s*[\\+-]?\\d+(\\.\\d+)?\\s+[\\+-]?\\d+(\\.\\d+)?\\s*\\)$"," (eg POINT(13.2 24) )")));
		dataTypeFormats.put(IntegerType.class, Arrays.asList(STORAGE_FORMAT_INT));
		dataTypeFormats.put(NumericType.class, Arrays.asList(STORAGE_FORMAT_DEC, FRENCH_FORMAT, GERMAN_FORMAT, ITALIAN_FORMAT, US_FORMAT));
		dataTypeFormats.put(TextType.class, Arrays.asList(format("TEXT","^.*$","lorem ipsum")));
	}
		
	
	public static List<ValueFormat> getFormatsPerDataType(Class<? extends DataType> dataType){
		return dataTypeFormats.get(dataType);
	}
	
	public static ValueFormat getFormatPerId(Class<? extends DataType> dataType, String id){
		for (ValueFormat tf : dataTypeFormats.get(dataType))
			if (tf.getId().equals(id)) 
				return tf;
		return null;
	}
}
