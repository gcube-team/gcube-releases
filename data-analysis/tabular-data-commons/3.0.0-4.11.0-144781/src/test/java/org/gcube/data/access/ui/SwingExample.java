package org.gcube.data.access.ui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.gcube.data.access.ui.widgets.TemplateColumnMultiEventChangeHandler;
import org.gcube.data.analysis.tabulardata.commons.templates.model.Template;
import org.gcube.data.analysis.tabulardata.commons.templates.model.TemplateCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments.ArgumentContainer;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments.ArgumentDescriptor;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments.ArgumentType;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments.ChangeHandler;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments.ContainerInstance;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments.DependencyDescriptor.DependencyType;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.AddColumnAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.AggregationPair;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.AggregationPair.AggregationFunction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.ColumnCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.commons.utils.DimensionReference;
import org.gcube.data.analysis.tabulardata.commons.utils.FormatReference;
import org.gcube.data.analysis.tabulardata.commons.utils.TimeDimensionReference;
import org.gcube.data.analysis.tabulardata.model.DataTypeFormats;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDInteger;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.junit.Assert;

public class SwingExample implements Runnable {

	public void run() {
		// Create the window
		JFrame f = new JFrame("Dynamic Widget creator test, !");
		// Sets the behavior for when the window is closed
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



		f.setLayout(new FlowLayout());

		ContainerInstance contInstance = ContainerInstance.createFirstInstance(getArgumentContainer());

		System.out.println(Util.printContainerInstance(contInstance, 0));

		for (Component comp: Util.createContainer(Arrays.asList(contInstance), f))
			f.add(comp);


		f.setSize(700, 400);

		// Arrange the components inside the window
		f.pack();
		// By default, the window is not visible. Make it visible.
		f.setVisible(true);
	}

	public static void main(String[] args) {
		SwingExample se = new SwingExample();
		// Schedules the application to be run at the correct time in the event queue.
		SwingUtilities.invokeLater(se);
	}




	private ArgumentContainer getArgumentContainer(){

		final Template template = getTemplate();

		ArgumentDescriptor<TemplateColumn<?>> selectTimeDimension = new ArgumentDescriptor<>("Time Dimension Column", "Time dimension to aggregate", false, ArgumentType.ExistingTemplateColumn);
		List<TemplateColumn<?>> timeColumns = new ArrayList<>();
		for (TemplateColumn<?> column: template.getActualStructure())
			if (column.getColumnType()==ColumnCategory.TIMEDIMENSION)
				timeColumns.add(column);
		selectTimeDimension.setSelection(timeColumns);


		ArgumentDescriptor<TemplateColumn<?>> selectGroup = new ArgumentDescriptor<>("Group Columns", "Columns to group", true, ArgumentType.ExistingTemplateColumn);

		selectGroup.setDependsOn(selectTimeDimension, new TemplateColumnMultiEventChangeHandler(), DependencyType.GLOBAL);		
		selectGroup.setSelection(template.getActualStructure());		


		ArgumentDescriptor<TemplateColumn<?>> selectAggrCol = new ArgumentDescriptor<>("Aggregate Column", "Column to aggregate", false, ArgumentType.ExistingTemplateColumn);
		selectAggrCol.setSelection(template.getActualStructure());
		TemplateColumnMultiEventChangeHandler tcmultiEventChanger =  new TemplateColumnMultiEventChangeHandler();
		selectAggrCol.setDependsOn(selectGroup,tcmultiEventChanger, DependencyType.GLOBAL);
		selectAggrCol.setDependsOn(selectTimeDimension, tcmultiEventChanger, DependencyType.GLOBAL);
		selectAggrCol.setDependsOn(selectAggrCol,tcmultiEventChanger, DependencyType.GLOBAL);
		
		ArgumentDescriptor<AggregationFunction> selectAggrFunction = new ArgumentDescriptor<>("Aggregation function", "Funtion to aggregate for", false, ArgumentType.String);
		selectAggrFunction.setSelection(Arrays.asList(AggregationFunction.values()));		
		selectAggrFunction.setDependsOn(selectAggrCol, new ChangeHandler<AggregationPair.AggregationFunction>() {

			@Override
			public List<AggregationFunction> change(List<Object> values,
					List<AggregationFunction> baseSelector,
					String argumentSenderId) {
				System.out.println("calling change on aggregate function");
				List<AggregationFunction> columnsToReturn = new ArrayList<>();
				if (values.size()>0){
					TemplateColumn<?> columnselected = (TemplateColumn<?>)values.get(0); 
					for (AggregationFunction funct : AggregationFunction.values())
						if (funct.getAllowedTypes().contains(columnselected.getValueType()) || funct.getAllowedTypes().isEmpty())
							columnsToReturn.add(funct);
				} else return baseSelector;
				System.out.println("returning new columns "+columnsToReturn);
				return columnsToReturn;
			}},  DependencyType.LOCAL);
		

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
		totalContainer.setName("TimeDimension aggregation");

		System.out.println(Util.printArgumentContainer(totalContainer, 0));


		return totalContainer;
	}

	Template getTemplate(){
		TemplateColumn<GeometryType> column = new TemplateColumn<GeometryType>(ColumnCategory.ATTRIBUTE, GeometryType.class , new FormatReference(DataTypeFormats.getFormatsPerDataType(GeometryType.class).get(0).getId()));
		column.setLabel("att");	
		
		TemplateColumn<IntegerType> dimColumn = new TemplateColumn<IntegerType>(ColumnCategory.DIMENSION, IntegerType.class, new DimensionReference(new TableId(32), new ColumnLocalId("columnId")));
		dimColumn.setLabel("species");
		
		TemplateColumn<IntegerType> timeColumn = new TemplateColumn<IntegerType>(ColumnCategory.TIMEDIMENSION, IntegerType.class, new TimeDimensionReference(PeriodType.MONTH, PeriodType.MONTH.getAcceptedFormats().get(0).getId()));
		timeColumn.setLabel("date 1");
		TemplateColumn<IntegerType> timeColumn2 = new TemplateColumn<IntegerType>(ColumnCategory.TIMEDIMENSION, IntegerType.class, new TimeDimensionReference(PeriodType.MONTH, PeriodType.MONTH.getAcceptedFormats().get(0).getId()));
		timeColumn2.setLabel("date 2");
		Template template = Template.create(TemplateCategory.DATASET, column, timeColumn, timeColumn2, dimColumn);

		TemplateColumn<IntegerType> addedColumn = new TemplateColumn<IntegerType>(ColumnCategory.MEASURE, IntegerType.class, new FormatReference(DataTypeFormats.getFormatsPerDataType(IntegerType.class).get(0).getId()));
		addedColumn.setLabel("added column");
		
		Assert.assertEquals(template.getColumns(), template.getActualStructure());

		template.addAction(new AddColumnAction(addedColumn, new TDInteger(0)));
		return template;

	}

}