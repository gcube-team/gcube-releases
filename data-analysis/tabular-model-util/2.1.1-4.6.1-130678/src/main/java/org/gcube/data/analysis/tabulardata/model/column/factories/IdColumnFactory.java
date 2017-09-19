package org.gcube.data.analysis.tabulardata.model.column.factories;

import java.util.ArrayList;
import java.util.Collection;

import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.metadata.column.DataLocaleMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;

public class IdColumnFactory extends BaseColumnFactory<IdColumnType> {

	private static final ArrayList<LocalizedText> DEFAULT_LABELS=new ArrayList<LocalizedText>(); 

	static{
		DEFAULT_LABELS.add(new ImmutableLocalizedText("ID"));
	}

	@Override
	protected Collection<LocalizedText> getDefaultLabels() {
		return DEFAULT_LABELS; 
	}

	public static Column create(){
		Column idColumn = create(new IdColumnType(), new IntegerType());
		idColumn.setName("id");
		return idColumn;
	}

	@Override
	public IdColumnType getManagedColumnType() {		
		return new IdColumnType();
	}
	
	@Override
	protected Column create(ColumnType columnType, DataType dataType,
			DataLocaleMetadata dataLocale, Collection<LocalizedText> labels) {		
		Column col=super.create(columnType, dataType, dataLocale, labels);
		col.setName("id");
		return col;
	}
	
}
