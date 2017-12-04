package org.gcube.data.analysis.tabulardata.statistical;

import java.io.IOException;

import net.sf.csv4j.ParseException;
import net.sf.csv4j.ProcessingException;

import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.factories.AnnotationColumnFactory;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.junit.Assert;
import org.junit.Test;

public class LabelFixingTest {

	@Test
	public void testCases() throws ParseException, IOException, ProcessingException{
		AnnotationColumnFactory factory=new AnnotationColumnFactory();
		Column col=factory.create(new ImmutableLocalizedText("CL +è27785£$%&/(%/())-:;.:,|!\"\\"));
		String fixed=Common.fixColumnName(col);
		System.out.println("FROM "+OperationHelper.retrieveColumnLabel(col)+" TO "+fixed);
		Assert.assertTrue(Common.isValidString(fixed));
		String exportCase="789e_5875";
		Assert.assertFalse(Common.isValidString(exportCase));
		Assert.assertTrue(Common.isValidString(Common.fixColumnName(exportCase)));
	}
	
}
