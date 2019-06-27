package org.gcube.data.analysis.tabulardata.model.column.factories;

import java.util.ArrayList;
import java.util.Collection;

import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;

public class AttributeColumnFactory extends BaseColumnFactory<AttributeColumnType>{
	
	private static final ArrayList<LocalizedText> DEFAULT_LABELS=new ArrayList<LocalizedText>(); 
	
	static{
		DEFAULT_LABELS.add(new ImmutableLocalizedText("New Attribute"));
	}
	
	@Override
	protected Collection<LocalizedText> getDefaultLabels() {
		return DEFAULT_LABELS; 
	}
	
	@Override
	public AttributeColumnType getManagedColumnType() {
		return new AttributeColumnType();
	}
}
