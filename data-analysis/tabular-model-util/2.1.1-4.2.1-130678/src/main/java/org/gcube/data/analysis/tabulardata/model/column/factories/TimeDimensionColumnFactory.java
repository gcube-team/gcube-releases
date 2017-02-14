package org.gcube.data.analysis.tabulardata.model.column.factories;

import java.util.ArrayList;
import java.util.Collection;

import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.metadata.column.PeriodTypeMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;

public class TimeDimensionColumnFactory extends BaseColumnFactory<TimeDimensionColumnType> {
	
	private static final ArrayList<LocalizedText> DEFAULT_LABELS=new ArrayList<LocalizedText>(); 
	
	static{
		DEFAULT_LABELS.add(new ImmutableLocalizedText("Time Dimension"));
	}
	
	@Override
	protected Collection<LocalizedText> getDefaultLabels() {
		return DEFAULT_LABELS; 
	}
	
	public Column create(PeriodType periodType){
		Column toReturn=create(new ImmutableLocalizedText(periodType.getName()));
		toReturn.setMetadata(new PeriodTypeMetadata(periodType));
		return toReturn;
	}

	@Override
	public TimeDimensionColumnType getManagedColumnType() {		
		return new TimeDimensionColumnType();
	}
}
