package org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition;

import java.util.LinkedList;
import java.util.List;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.resource.SDMXDataStructureDefinitionResourceBuilder;

public class SDMXDataOperationManagerBuilder 
{
	
	private Table table;
	private DatabaseConnectionProvider connectionProvider;
	private  OperationInvocation invocation;
	private CubeManager cubeManager;
	private List<SDMXDataOperationExecutor> executors;
	private SDMXDataStructureDefinitionResourceBuilder resourceBuilder;
	
	public SDMXDataOperationManagerBuilder ()
	{
		this.executors = new LinkedList<>();
	}
	
	
	public void setTable(Table table) {
		this.table = table;
	}
	public void setConnectionProvider(DatabaseConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}
	public void setInvocation(OperationInvocation invocation) {
		this.invocation = invocation;
	}
	public void setCubeManager(CubeManager cubeManager) {
		this.cubeManager = cubeManager;
	}

	public void setExecutors(List<SDMXDataOperationExecutor> executors) {
		this.executors = executors;
	}
	
	public void addExecutor (SDMXDataOperationExecutor executor)
	{
		this.executors.add(executor);
	}
	
	
	
	public void setResourceBuilder(SDMXDataStructureDefinitionResourceBuilder resourceBuilder) {
		this.resourceBuilder = resourceBuilder;
	}


	public SDMXDataOperationManager build ()
	{
		return new SDMXDataOperationManager(this.table, this.connectionProvider, this.invocation, this.cubeManager, this.executors,this.resourceBuilder);
	}

}
