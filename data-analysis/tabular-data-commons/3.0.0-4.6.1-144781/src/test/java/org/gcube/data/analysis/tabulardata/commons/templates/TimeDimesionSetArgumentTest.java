package org.gcube.data.analysis.tabulardata.commons.templates;

import java.io.Console;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments.ArgumentContainer;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments.ArgumentDescriptor;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments.ArgumentInstance;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments.ArgumentType;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments.ContainerInstance;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments.InstanceInterface;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.AggregationPair.AggregationFunction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.junit.Assert;
import org.junit.Test;
import org.gcube.data.access.ui.Util;

public class TimeDimesionSetArgumentTest {

	
	//TemplateColumn<?> column, PeriodType periodType, List<TemplateColumn<?>> groupColumns, AggregationPair ... aggregationPairs
	
	@Test
	public void createInstanceContainer(){
		
		ArgumentContainer totalArgument = getArgumentContainer();
		
		System.out.println(Util.printArgumentContainer(totalArgument, 0));
		
		System.out.println("----------------------------");
		
		ContainerInstance firstInstance = ContainerInstance.createFirstInstance(totalArgument);
				
		//firstInstance.createInstanceOf(totalArgument.getContainers().get(0));
		
		System.out.println(Util.printContainerInstance(firstInstance,0));
		
		String argumentContainerId = totalArgument.getContainers().get(0).getIdentifier();
		
		System.out.println("agument container id is "+argumentContainerId);
		
		ArgumentInstance<?> inst = firstInstance.getContainerInstances().get(argumentContainerId).get(0).getAllArgumentInstances().iterator().next();
				
		Assert.assertNotNull(inst);		
			
		InstanceInterface foundInst = ContainerInstance.getChildInstanceById(firstInstance, inst.getIdentifier());
		
		System.out.println(foundInst);
		
	}
	
	
		
	private ArgumentContainer getArgumentContainer(){
		
		ArgumentDescriptor<TemplateColumn<?>> selectTimeDimension = 
				new ArgumentDescriptor<>("Time Dimension Column", "Time dimension to aggregate", false, ArgumentType.ExistingTemplateColumn);
		List<TemplateColumn<?>> timeColumns = new ArrayList<>();
		/*for (TemplateColumn<?> column: columns)
			if (column.getColumnType()==ColumnCategory.TIMEDIMENSION)
				timeColumns.add(column);
		selectTimeDimension.setSelection(timeColumns);*/
				
		ArgumentDescriptor<TemplateColumn<?>> selectGroup = 
				new ArgumentDescriptor<>("Group Columns", "Columns to group", true, ArgumentType.ExistingTemplateColumn);
		
		ArgumentDescriptor<TemplateColumn<?>> selectAggrCol = 
						new ArgumentDescriptor<>("Aggregate Column", "Column to aggregate", true, ArgumentType.ExistingTemplateColumn);
				
		ArgumentDescriptor<AggregationFunction> selectAggrFunction = 
							new ArgumentDescriptor<>("Aggregation function", "Funtion to aggregate for", true, ArgumentType.Integer);
	
		List<ArgumentContainer> aggrcontainers = Collections.emptyList();
		List<ArgumentDescriptor<?>> aggrArgumentDescriptors = new ArrayList<>();
		aggrArgumentDescriptors.add(selectAggrCol);
		aggrArgumentDescriptors.add(selectAggrFunction);
		ArgumentContainer aggrContainer = new ArgumentContainer(aggrArgumentDescriptors, aggrcontainers, 0, Integer.MAX_VALUE);
		aggrContainer.setName("aggregation group");
		
		List<ArgumentDescriptor<?>> totalArguments = new ArrayList<>();
		totalArguments.add(selectTimeDimension);
		totalArguments.add(selectGroup);
		
		ArgumentContainer totalContainer = new ArgumentContainer(totalArguments, Collections.singletonList(aggrContainer), 1,1);
		totalContainer.setName("totalContainer");
		return totalContainer;
	}
	
}
