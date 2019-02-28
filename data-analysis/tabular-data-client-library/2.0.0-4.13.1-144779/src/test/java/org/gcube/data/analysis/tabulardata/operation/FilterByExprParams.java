package org.gcube.data.analysis.tabulardata.operation;

import java.util.Collections;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextBeginsWith;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.table.Table;

public class FilterByExprParams extends ParameterRetriever {

	@Override
	public Map<String, Object> getParameter(Object... objs) {
		if (objs.length!=2) throw new IllegalArgumentException("filter need a table"); 
		
		System.out.println(((Table) objs[0]).getId()+" "+((Column)objs[1]).getLocalId());
		
		System.out.println(new TextBeginsWith(new ColumnReference(((Table) objs[0]).getId(), ((Column)objs[1]).getLocalId(), new TextType()), new TDText("a")));
		
		return Collections.singletonMap("expression", 
				(Object) new TextBeginsWith(new ColumnReference(((Table) objs[0]).getId(), 
						((Column)objs[1]).getLocalId()), new TDText("a")));
	}

	@Override
	protected long getOperationId() {
		return 3201;
	}

	@Override
	public boolean verifyTable(Table lastTable) {
		return lastTable!=null;
	}

	@Override
	public OperationExecution getInvocation(Map<String, Object> parameters,
			Object... objs) throws Exception {
		if (objs.length==0) throw new IllegalArgumentException("filter need a table"); 
					
		return new OperationExecution(getOperationId(), parameters);
	}

}