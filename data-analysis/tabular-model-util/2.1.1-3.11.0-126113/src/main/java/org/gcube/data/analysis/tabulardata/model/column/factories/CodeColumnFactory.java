package org.gcube.data.analysis.tabulardata.model.column.factories;

import java.util.ArrayList;
import java.util.Collection;

import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;

public class CodeColumnFactory extends BaseColumnFactory<CodeColumnType>{
	
	private static final ArrayList<LocalizedText> DEFAULT_LABELS=new ArrayList<LocalizedText>(); 
	
	static{
		DEFAULT_LABELS.add(new ImmutableLocalizedText("New Code"));
	}
	
	@Override
	protected Collection<LocalizedText> getDefaultLabels() {
		return DEFAULT_LABELS; 
	}
	
	@Override
	public CodeColumnType getManagedColumnType() {
		return new CodeColumnType();
	}
}
