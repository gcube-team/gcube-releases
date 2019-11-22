package org.gcube.data.analysis.tabulardata.commons.templates;


import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.gcube.data.analysis.tabulardata.commons.templates.model.Template;
import org.gcube.data.analysis.tabulardata.commons.templates.model.TemplateAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.TemplateCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.AddColumnAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.AggregationPair;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.AggregationPair.AggregationFunction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.GenerateTimeDimensionGroup;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.TimeAggregationAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.ColumnCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.commons.utils.DimensionReference;
import org.gcube.data.analysis.tabulardata.commons.utils.FormatReference;
import org.gcube.data.analysis.tabulardata.commons.utils.LocaleReference;
import org.gcube.data.analysis.tabulardata.commons.utils.TimeDimensionReference;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.OnRowErrorAction;
import org.gcube.data.analysis.tabulardata.model.DataTypeFormats;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDInteger;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.junit.Assert;
import org.junit.Test;

public class TemplateBuilderTest {

	@Test(expected=IllegalArgumentException.class)
	public void invalidColumnForTemplate(){
		Template.create(TemplateCategory.CODELIST, new TemplateColumn<IntegerType>(ColumnCategory.ATTRIBUTE, IntegerType.class), new TemplateColumn<NumericType>(ColumnCategory.ATTRIBUTE, NumericType.class));
	}

	@Test(expected=IllegalArgumentException.class)
	public void invalidTypeForColumn(){
		Template.create(TemplateCategory.DATASET, new TemplateColumn<IntegerType>(ColumnCategory.CODENAME, IntegerType.class));
	}


	@Test
	public void serializeTemplateTest() throws JAXBException{
		try{
			JAXBContext jaxbContext = JAXBContext.newInstance(Template.class);
			TemplateColumn<NumericType> column = new TemplateColumn<NumericType>(ColumnCategory.ATTRIBUTE, NumericType.class , new FormatReference(DataTypeFormats.getFormatsPerDataType(NumericType.class).get(0).getId()));
			column.addRule(2l);

			TemplateColumn<IntegerType> dimColumn = new TemplateColumn<IntegerType>(ColumnCategory.DIMENSION, IntegerType.class, new DimensionReference(new TableId(32), new ColumnLocalId("columnId")));

			TemplateColumn<IntegerType> timeColumn = new TemplateColumn<IntegerType>(ColumnCategory.TIMEDIMENSION, IntegerType.class, new TimeDimensionReference(PeriodType.MONTH, PeriodType.MONTH.getAcceptedFormats().get(0).getId()));

			Template template = Template.create(TemplateCategory.DATASET, column, timeColumn, dimColumn);

			TemplateColumn<IntegerType> addedColumn = new TemplateColumn<IntegerType>(ColumnCategory.MEASURE, IntegerType.class, new FormatReference(DataTypeFormats.getFormatsPerDataType(IntegerType.class).get(0).getId()));

			Assert.assertEquals(template.getColumns(), template.getActualStructure());

			template.addAction(new AddColumnAction(addedColumn, new TDInteger(0)));

			Assert.assertTrue(template.getActualStructure().size() > template.getColumns().size());
			
			List<TemplateColumn<?>> groupcolumn = new ArrayList<>(1);
			groupcolumn.add(column);

			TimeAggregationAction taa = new TimeAggregationAction(timeColumn, PeriodType.YEAR, groupcolumn);

			template.addAction(taa);

			//Assert.assertTrue(template.getActualStructure().size() < template.getColumns().size());

			//Assert.assertNotEquals(template.getColumns(), template.getActualStructure());

			template.setOnErrorAction(OnRowErrorAction.DISCARD);

			StringWriter sw = new StringWriter();
			jaxbContext.createMarshaller().marshal(template, sw);

			System.out.println(sw.toString());

			Template template1 = (Template)jaxbContext.createUnmarshaller().unmarshal(new StringReader(sw.toString()));
			System.out.println(template1);
		}catch (Exception e){
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	public void createValidTemplate(){

		TemplateColumn<NumericType> column = new TemplateColumn<NumericType>(ColumnCategory.ATTRIBUTE, NumericType.class , new FormatReference(DataTypeFormats.getFormatsPerDataType(NumericType.class).get(0).getId()));
		column.addRule(2l);

		TemplateColumn<IntegerType> dimColumn = new TemplateColumn<IntegerType>(ColumnCategory.DIMENSION, IntegerType.class, new DimensionReference(new TableId(32), new ColumnLocalId("columnId")));

		TemplateColumn<IntegerType> timeColumn = new TemplateColumn<IntegerType>(ColumnCategory.TIMEDIMENSION, IntegerType.class, new TimeDimensionReference(PeriodType.MONTH, PeriodType.MONTH.getAcceptedFormats().get(0).getId()));

		Template template = Template.create(TemplateCategory.DATASET, column, timeColumn, dimColumn);

		TemplateColumn<IntegerType> addedColumn = new TemplateColumn<IntegerType>(ColumnCategory.MEASURE, IntegerType.class, new FormatReference(DataTypeFormats.getFormatsPerDataType(IntegerType.class).get(0).getId()));

		Assert.assertEquals(template.getColumns(), template.getActualStructure());

		template.addAction(new AddColumnAction(addedColumn, new TDInteger(0)));

		Assert.assertTrue(template.getActualStructure().size() > template.getColumns().size());

		List<TemplateColumn<?>> groupcolumn = new ArrayList<>(1);
		groupcolumn.add(column);

		TimeAggregationAction taa = new TimeAggregationAction(timeColumn, PeriodType.YEAR, groupcolumn, new AggregationPair(addedColumn, AggregationFunction.MAX));

		template.addAction(taa);
		
		Assert.assertNotEquals(template.getColumns(), template.getActualStructure());

		template.setOnErrorAction(OnRowErrorAction.DISCARD);

		for (TemplateColumn<?> actualColumn: template.getActualStructure())
			System.out.println(actualColumn.getRepresentation());
	
	}

	@Test
	public void createValidTemplateForCodelist(){

		TemplateColumn<TextType> column = new TemplateColumn<TextType>(ColumnCategory.CODE, TextType.class);
		column.addRule(2l);

		TemplateColumn<TextType> dimColumn = new TemplateColumn<TextType>(ColumnCategory.CODENAME, TextType.class, new LocaleReference("en"));

		TemplateColumn<TextType> timeColumn = new TemplateColumn<TextType>(ColumnCategory.CODEDESCRIPTION, TextType.class);

		Template template = Template.create(TemplateCategory.CODELIST, column, timeColumn, dimColumn);
/*
		TemplateColumn<IntegerType> addedColumn = new TemplateColumn<IntegerType>(ColumnCategory.MEASURE, IntegerType.class, new FormatReference(DataTypeFormats.getFormatsPerDataType(IntegerType.class).get(0).getId()));

		Assert.assertEquals(template.getColumns(), template.getActualStructure());

		template.addAction(new AddColumnAction(addedColumn, new TDInteger(0)));

		Assert.assertTrue(template.getActualStructure().size() > template.getColumns().size());

		List<TemplateColumn<?>> groupcolumn = new ArrayList<>(1);
		groupcolumn.add(column);

		TimeAggregationAction taa = new TimeAggregationAction(timeColumn, PeriodType.YEAR, groupcolumn, new AggregationPair(addedColumn, AggregationFunction.MAX));

		template.addAction(taa);
		
		Assert.assertNotEquals(template.getColumns(), template.getActualStructure());

		template.setOnErrorAction(OnRowErrorAction.DISCARD);

		for (TemplateColumn<?> actualColumn: template.getActualStructure())
			System.out.println(actualColumn.getRepresentation());*/
	
	}
	
}
