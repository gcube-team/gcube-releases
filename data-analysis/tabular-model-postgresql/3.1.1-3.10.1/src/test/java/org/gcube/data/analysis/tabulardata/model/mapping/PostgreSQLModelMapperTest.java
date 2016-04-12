package org.gcube.data.analysis.tabulardata.model.mapping;

import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.DateType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.datatype.geometry.GeometryShape;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDGeometry;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class PostgreSQLModelMapperTest {

	private static SQLModelMapper mapper;
	
	@BeforeClass
	public static void setUp() throws Exception {
		mapper = new PostgreSQLModelMapper();
	}

	
	public void testDataTypeTranslation(DataType dataType, String expectedString) {
		Assert.assertEquals(expectedString, mapper.translateDataTypeToSQL(dataType));
	}
	
	@Test
	public void testBoolean(){
		testDataTypeTranslation(new BooleanType(), "boolean");
	}
	
	@Test
	public void testDate(){
		testDataTypeTranslation(new DateType(), "date");
	}
	
	@Test
	public void testText(){
		testDataTypeTranslation(new TextType(), "varchar(256)");
		testDataTypeTranslation(new TextType(20), "varchar(20)");
	}
	
	@Test
	public void testGeometry(){
		testDataTypeTranslation(new GeometryType(2), "geometry(GEOMETRY,0)");
		testDataTypeTranslation(new GeometryType(GeometryShape.POINT,2), "geometry(POINT,0)");
		testDataTypeTranslation(new GeometryType(0,3), "geometry(GEOMETRY,0)");
		testDataTypeTranslation(new GeometryType(3, GeometryShape.LINESTRING, 4), "geometry(LINESTRING,3)");
	}
	
	@Test
	public void testInteger(){
		testDataTypeTranslation(new IntegerType(), "integer");
	}
	
	@Test
	public void testNumeric(){
		testDataTypeTranslation(new NumericType(), "numeric");
		testDataTypeTranslation(new NumericType(20), "numeric(20)");
		testDataTypeTranslation(new NumericType(20,5), "numeric(20,5)");
	}
	
	@Test
	public void testGeometryValues(){
		TDGeometry tdGeo = new TDGeometry("LINESTRING(45.4 56,45 67,56 67)");
		System.out.println(mapper.translateModelValueToSQL(tdGeo));
	}
	
	
	
	

}
