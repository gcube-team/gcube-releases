package org.gcube.data.analysis.tabulardata.operation.parameters.leaves;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.CompositeParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.junit.Assert;
import org.junit.Test;

public class SerializationTest {

	protected void roundTripTest(Object object) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		StringWriter stringWriter = new StringWriter();
		marshaller.marshal(object, stringWriter);
		String result = stringWriter.toString();
		System.err.println(result);
	
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		Parameter unmarshalledObj = (Parameter) unmarshaller.unmarshal(new StringReader(result));
		Assert.assertEquals(object, unmarshalledObj);		
	}
	
	
	@Test
	public void testBooleanParameter() throws JAXBException{
		roundTripTest(new BooleanParameter("id", "name", "desc", Cardinality.ONE));
	}
	
	@Test
	public void testColumnMetadataParameter() throws JAXBException{
		roundTripTest(new ColumnMetadataParameter("id", "name", "desc", Cardinality.ONE));
	}
	
	@Test
	public void testColumnTypeParameter()throws JAXBException{
		roundTripTest(new ColumnTypeParameter("col", "col", "column", Cardinality.ONE));
	}
	
	@Test
	public void testDataTypeParameter() throws JAXBException{
		roundTripTest(new DataTypeParameter("id", "name", "desc", Cardinality.ONE));
	}
	
	@Test
	public void testExpressionParameter() throws JAXBException{
		roundTripTest(new ExpressionParameter("id", "name", "desc", Cardinality.ONE));
	}
	
	@Test
	public void testIntegerParameter() throws JAXBException{
		roundTripTest(new IntegerParameter("id", "name", "desc", Cardinality.ONE));
	}
	
	@Test
	public void testLocaleParameter() throws JAXBException{
		roundTripTest(new LocaleParameter("id", "name", "desc", Cardinality.ONE));
	}
	
	@Test
	public void testLocalizedTextChoiceParameter() throws JAXBException{
		roundTripTest(new LocalizedTextChoiceParameter("id", "name", "desc", Cardinality.ONE, Arrays.asList(new ImmutableLocalizedText[]{new ImmutableLocalizedText("myValue")})));
	}
	
	@Test
	public void testLocalizedTextParameter() throws JAXBException{
		roundTripTest(new LocalizedTextParameter("id", "name", "desc", Cardinality.ONE));
	}
	
	@Test
	public void testMapParameter()throws JAXBException{
		roundTripTest(new MapParameter("map", "mappp", "mappina", Cardinality.OPTIONAL, String.class, String.class));
	}
	
	@Test
	public void testMultivaluedStringParameter() throws JAXBException{
		roundTripTest(new MultivaluedStringParameter("id", "name", "desc", Cardinality.ONE,Arrays.asList(new String[]{"myString"})));
	}
	
	@Test
	public void testRegexpStringParameter() throws JAXBException{
		roundTripTest(new RegexpStringParameter("id", "name", "desc", Cardinality.ONE,"[a-b]*"));
	}
	
	@Test
	public void testTargetColumnParameter() throws JAXBException{
		roundTripTest(new TargetColumnParameter("id", "name", "desc", Cardinality.ONE));
	}
	
	@Test
	public void testTargetTableParameter() throws JAXBException{
		roundTripTest(new TargetTableParameter("id", "name", "desc", Cardinality.ONE));
	}
	
	@Test
	public void testTDTypeValueParameter() throws JAXBException{
		roundTripTest(new TDTypeValueParameter("id", "name", "desc", Cardinality.ONE));
	}
	
	
	@Test
	public void testCompositeParameter() throws JAXBException{
		Parameter tdType=new TDTypeValueParameter("id", "name", "desc", Cardinality.ONE);
		Parameter map=new MapParameter("map", "mappp", "mappina", Cardinality.OPTIONAL, String.class, String.class);
		CompositeParameter compo=new CompositeParameter("param", "compo", "the compo", Cardinality.ONE, Arrays.asList(new Parameter[]{
				tdType,map
		}));
		roundTripTest(compo);
		System.out.println(compo);
	}
	
	
	
}
