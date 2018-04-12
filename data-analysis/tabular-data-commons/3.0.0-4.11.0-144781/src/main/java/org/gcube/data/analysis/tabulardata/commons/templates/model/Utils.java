package org.gcube.data.analysis.tabulardata.commons.templates.model;

import org.gcube.data.analysis.tabulardata.commons.utils.FormatReference;
import org.gcube.data.analysis.tabulardata.model.DataTypeFormats;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;

public class Utils {
	
	public static final FormatReference TEXT_FORMAT_REFERENCE = new FormatReference(DataTypeFormats.getFormatsPerDataType(TextType.class).get(0).getId());
	
	public static FormatReference getDefaultFormatReferenceForType(Class<? extends DataType> type){
		return new FormatReference(DataTypeFormats.getFormatsPerDataType(type).get(0).getId());
	}
	
}
