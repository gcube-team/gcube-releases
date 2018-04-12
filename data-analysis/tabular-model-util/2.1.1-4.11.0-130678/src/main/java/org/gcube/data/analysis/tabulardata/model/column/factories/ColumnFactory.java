package org.gcube.data.analysis.tabulardata.model.column.factories;

import java.util.Collection;

import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.metadata.column.DataLocaleMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;

public interface ColumnFactory<T extends ColumnType> {

	public Column createDefault();
	public Column create(DataType dataType);
	public Column create(DataType dataType,Collection<LocalizedText> labels);
	public Column create(DataType dataType,Collection<LocalizedText> labels,String dataLocale);
	public Column create(DataType dataType,Collection<LocalizedText> labels,DataLocaleMetadata dataLocale);
	public Column create(LocalizedText label);
	public Column create(LocalizedText label, DataLocaleMetadata dataLocale);
	public Column create(LocalizedText label,DataType dataType);
	
	
}
