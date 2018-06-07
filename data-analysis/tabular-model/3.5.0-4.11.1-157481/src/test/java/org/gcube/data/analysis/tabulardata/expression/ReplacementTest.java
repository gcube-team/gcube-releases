package org.gcube.data.analysis.tabulardata.expression;

import org.gcube.data.analysis.tabulardata.expression.composite.text.Concat;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.junit.Test;

public class ReplacementTest {

	@Test
	public void replace() throws MalformedExpressionException{
		ColumnReference ref=new ColumnReference(new TableId(10), new ColumnLocalId("io"));
		Concat concat=new Concat(ref, ref);
		TableReferenceReplacer replacer=new TableReferenceReplacer(concat);
		ColumnReference newRef=new ColumnReference(new TableId(14), new ColumnLocalId("io"));
		replacer.replaceColumnReference(ref, newRef);
		System.out.println(replacer.getExpression());
		new GeometryType().getDefaultValue().validate();
	}
	
}
