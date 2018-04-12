package org.gcube.data.analysis.tabulardata.model.column.factories;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AnnotationColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeDescriptionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeNameColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.MeasureColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.metadata.column.DataLocaleMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.junit.Assert;
import org.junit.Test;

public class ColumnFactoriesTest {

	private static ColumnType[] types=new ColumnType[]{
		new AnnotationColumnType(),
		new AttributeColumnType(),
		new CodeColumnType(),
		new CodeDescriptionColumnType(),
		new CodeNameColumnType(),
		new DimensionColumnType(),
		new IdColumnType(),
		new MeasureColumnType(),
		new TimeDimensionColumnType(),
		new ValidationColumnType()
	};
	
	
	@Test
	public void test() throws InstantiationException, IllegalAccessException{
		for(ColumnType type:types){
			ColumnFactory<?> factory=BaseColumnFactory.getFactory(type);
			LocalizedText label=(LocalizedText)new ImmutableLocalizedText(type.getName()+" column");
			Collection<LocalizedText> labels=Collections.singletonList(label);
			String dataLocale=Locale.ENGLISH.toString();
			DataLocaleMetadata localeMetadata=new DataLocaleMetadata(dataLocale);			
			DataType dataType=type.getAllowedDataTypes().get(0).newInstance();
			
			Assert.assertEquals(type, factory.createDefault().getColumnType());
			Assert.assertEquals(type, factory.create(dataType).getColumnType());
			Assert.assertEquals(type, factory.create(dataType,labels).getColumnType());
			Assert.assertEquals(type, factory.create(dataType,labels,dataLocale).getColumnType());
			Assert.assertEquals(type, factory.create(dataType,labels,localeMetadata).getColumnType());
			Assert.assertEquals(type, factory.create(label).getColumnType());
			Assert.assertEquals(type, factory.create(label,localeMetadata).getColumnType());
			Assert.assertEquals(type, factory.create(label,dataType).getColumnType());
		}
	}
	
	@Test
	public void testDataType(){
		Column col =new AttributeColumnFactory().create(new ImmutableLocalizedText("catcher", "en"), new TextType(30));
		System.out.println("type is "+col.getDataType());
	}
}
