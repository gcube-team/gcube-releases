package org.gcube.data.analysis.tabulardata.model.column.factories;

import java.util.ArrayList;
import java.util.Collection;

import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.relationship.ColumnRelationship;

public class DimensionColumnFactory extends BaseColumnFactory<DimensionColumnType>{
	
	private static final ArrayList<LocalizedText> DEFAULT_LABELS=new ArrayList<LocalizedText>(); 
	
	static{
		DEFAULT_LABELS.add(new ImmutableLocalizedText("New Dimension"));
	}
	
	@Override
	protected Collection<LocalizedText> getDefaultLabels() {
		return DEFAULT_LABELS; 
	}
	
	public Column create(LocalizedText label, ColumnRelationship columnRelationship){
		Column result = create(label);
		result.setRelationship(columnRelationship);
		return result;
	}
	
	public Column create(ColumnRelationship columnRelationship){
		Column result = createDefault();
		result.setRelationship(columnRelationship);
		return result;
	}

	@Override
	public DimensionColumnType getManagedColumnType() {
		return new DimensionColumnType();
	}
	
}
