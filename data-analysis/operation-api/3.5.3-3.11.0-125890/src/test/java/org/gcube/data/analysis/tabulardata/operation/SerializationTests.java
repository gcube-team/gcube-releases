package org.gcube.data.analysis.tabulardata.operation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.invocation.ImmutableOperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.BooleanParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.DataTypeParameter;
import org.junit.Test;

public class SerializationTests {

	@Test
	public void testOperationInvocation() throws JAXBException {
		OperationInvocation invocation = createOperationInvocation();
		SerializationUtil.roundTripTest(invocation);
	}

	private OperationInvocation createOperationInvocation() {
		return new ImmutableOperationInvocation(new TableId(20), new ColumnLocalId("tst"), createOperationDescriptor(),
				createTestParameters());
	}

	private Map<String, Object> createTestParameters() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		return parameters;
	}

	private OperationDescriptor createOperationDescriptor() {
		OperationDescriptor descriptor = new ImmutableOperationDescriptor(new OperationId(412), "test", "test",
				OperationScope.TABLE, OperationType.EXPORT, createParameters());
		return descriptor;
	}

	private List<Parameter> createParameters() {
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.add(new BooleanParameter("test", "test", "test", Cardinality.ONE));
		parameters.add(new DataTypeParameter("test", "test", "test", Cardinality.ONE));
		// TODO test other parameters and composite
		return parameters;
	}

	@Test
	public void testOperationDescriptor() throws JAXBException {
		SerializationUtil.roundTripTest(createOperationDescriptor());
	}

}
