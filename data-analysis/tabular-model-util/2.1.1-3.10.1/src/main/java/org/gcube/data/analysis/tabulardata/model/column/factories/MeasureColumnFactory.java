package org.gcube.data.analysis.tabulardata.model.column.factories;

import java.util.ArrayList;
import java.util.Collection;

import org.gcube.data.analysis.tabulardata.model.column.type.MeasureColumnType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;

public class MeasureColumnFactory extends BaseColumnFactory<MeasureColumnType>{
	
	
	private static final ArrayList<LocalizedText> DEFAULT_LABELS=new ArrayList<LocalizedText>(); 
	
	static{
		DEFAULT_LABELS.add(new ImmutableLocalizedText("New Annotation"));
	}
	
	@Override
	protected Collection<LocalizedText> getDefaultLabels() {
		return DEFAULT_LABELS; 
	}
	
	
	@Override
	public MeasureColumnType getManagedColumnType() {
		return new MeasureColumnType();		
	}
}
