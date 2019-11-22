package org.gcube.data.analysis.tabulardata.cube.time;

import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;

public interface TimeCodelistCreator {
			
	public Table getTable(PeriodType periodType); 

}
