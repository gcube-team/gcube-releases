package org.gcube.data.analysis.tabulardata.query.parameters;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.Equals;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.LessThan;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.NotEquals;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextContains;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextMatchSQLRegexp;
import org.gcube.data.analysis.tabulardata.expression.logical.And;
import org.gcube.data.analysis.tabulardata.expression.logical.IsNull;
import org.gcube.data.analysis.tabulardata.expression.logical.Or;
import org.gcube.data.analysis.tabulardata.expression.logical.ValueIsIn;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDBoolean;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDDate;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDInteger;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDNumeric;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SerializationTests {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public final void testOrder() throws JAXBException {
		QueryOrder order = new QueryOrder(new ColumnLocalId(UUID.randomUUID().toString()),QueryOrderDirection.DESCENDING);
		roundTripTest(order);
	}
	
	@Test
	public final void testPage() throws JAXBException{
		QueryPage page = new QueryPage(20, 200);
		roundTripTest(page);
	}
	
	@Test
	public final void testFilter() throws JAXBException{
		QueryFilter filter = new QueryFilter(createExpression());
		roundTripTest(filter);
	}
	
	
	
	private Expression createExpression() {
		ColumnReference targetColumnReference =  new ColumnReference(new TableId(10), new ColumnLocalId("test"));
		ColumnReference anotherTargetColumnReference =  new ColumnReference(new TableId(10), new ColumnLocalId("test"));
		
		//Other expression
		Expression isNull = new IsNull(new ColumnReference(new TableId(10), new ColumnLocalId("test")));
		//Expression columnIsIn = new ValueIsIn(targetColumnReference, anotherTargetColumnReference);
		
		//Text expression
		Expression textContains = new TextContains(new ColumnReference(new TableId(10), new ColumnLocalId("test")),new TDText("test"));
		Expression textMatchRegexp = new TextMatchSQLRegexp(targetColumnReference, new TDText("[a-b]*"));
		
		//Comparable
		Expression equals = new Equals(targetColumnReference, new TDDate(new Date()));
		Expression greaterThan = new Equals(targetColumnReference, new TDInteger(5));
		Expression lessThan = new LessThan(targetColumnReference , new TDNumeric(5.1f));
		Expression notEquals = new NotEquals(targetColumnReference, new TDBoolean(false));
		
		//Composite
		Expression and = new And(textContains, isNull, textContains );
		Expression or = new Or(and, textMatchRegexp, equals, greaterThan, lessThan, notEquals);
		return or;
	}

	private void roundTripTest(Object object) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		StringWriter stringWriter = new StringWriter();
		marshaller.marshal(object, stringWriter);
		String result = stringWriter.toString();
		System.err.println(result);

		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		Object unmarshalledObj = (Object) unmarshaller.unmarshal(new StringReader(result));
		Assert.assertEquals(object, unmarshalledObj);
	}

}
