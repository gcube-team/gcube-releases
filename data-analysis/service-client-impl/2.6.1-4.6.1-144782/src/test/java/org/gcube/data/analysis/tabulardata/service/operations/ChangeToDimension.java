package org.gcube.data.analysis.tabulardata.service.operations;

import java.util.HashMap;
import java.util.Map;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TaskStatus;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.service.impl.TabularDataServiceFactory;
import org.gcube.data.analysis.tabulardata.service.operation.Task;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId;
import org.junit.Test;

public class ChangeToDimension {

	@Test
	public void execute() throws Exception{
		ScopeProvider.instance.set("/gcube/devsec");
						
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("refColumn", new ColumnReference(new TableId(76),new ColumnLocalId("1476fafe-1e63-444b-8110-e390a5960bce")));
		
		Map<Long, Long> idsMapping = new HashMap<>();
		idsMapping.put(1l, 2l);
		idsMapping.put(2l, 1l);
		parameters.put("mapping", idsMapping);
		
		OperationExecution opExec = new OperationExecution(2006, parameters);
		opExec.setColumnId("1e61340a-41bf-4255-b50d-505e30a107db");
		
		Task task = TabularDataServiceFactory.getService().execute(opExec, new TabularResourceId(31));
		
		while(!task.getStatus().isFinal()){
			System.out.println(task.getStatus());
			Thread.sleep(4000);
		}
		
		System.out.println(task.getStatus());
		
		if (task.getStatus() == TaskStatus.FAILED)
			task.getErrorCause().printStackTrace();
		
		
	}
	
}
