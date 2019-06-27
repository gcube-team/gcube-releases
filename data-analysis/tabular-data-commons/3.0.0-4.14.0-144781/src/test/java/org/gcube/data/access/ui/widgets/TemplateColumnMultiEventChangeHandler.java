package org.gcube.data.access.ui.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.extern.slf4j.Slf4j;

import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments.ChangeHandler;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;

@Slf4j
public class TemplateColumnMultiEventChangeHandler implements ChangeHandler<TemplateColumn<?>>{

	Map<String, List<Object>> valuesMap = new HashMap<String, List<Object>>();

			
	public TemplateColumnMultiEventChangeHandler() {
		super();
	}



	@Override
	public synchronized List<TemplateColumn<?>> change(List<Object> values, List<TemplateColumn<?>> baseSelector, String argumentSenderId) {
		List<TemplateColumn<?>> columnsToReturn = new ArrayList<>(baseSelector);
		valuesMap.put(argumentSenderId, values);
		for (Entry<String, List<Object>> entry : valuesMap.entrySet()){
			if (entry.getValue().size()>0)
				for(Object obj: entry.getValue()){
					TemplateColumn<?> selectedColumn = (TemplateColumn<?>) obj;
					if(columnsToReturn.contains(selectedColumn))
						columnsToReturn.remove(selectedColumn);
				}
			 
		}
		return columnsToReturn;
	}


}
