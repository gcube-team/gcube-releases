package org.gcube.data.analysis.tabulardata.model.column.factories;

import java.util.ArrayList;
import java.util.Collection;

import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeDescriptionColumnType;
import org.gcube.data.analysis.tabulardata.model.metadata.column.DataLocaleMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;

public class CodeDescriptionColumnFactory extends BaseColumnFactory<CodeDescriptionColumnType>{

	private static final ArrayList<LocalizedText> DEFAULT_LABELS=new ArrayList<LocalizedText>(); 
	
	static{
		DEFAULT_LABELS.add(new ImmutableLocalizedText("New Code Description"));
	}
	
	@Override
	protected Collection<LocalizedText> getDefaultLabels() {
		return DEFAULT_LABELS; 
	}
	
	@Override
	public CodeDescriptionColumnType getManagedColumnType() {		
		return new CodeDescriptionColumnType();
	}
	
	public Column create(String dataLocale){
		Column column = create(new ImmutableLocalizedText("name_"+dataLocale,"en"));
		column.setMetadata(new DataLocaleMetadata(dataLocale));		
		return column;
	}
}
