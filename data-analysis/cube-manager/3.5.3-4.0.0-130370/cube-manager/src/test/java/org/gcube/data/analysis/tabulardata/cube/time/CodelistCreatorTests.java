package org.gcube.data.analysis.tabulardata.cube.time;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.model.metadata.table.TimePeriodTypeMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class CodelistCreatorTests {

	@Inject
	CubeManager cubeManager;

	@Test
	public final void testDay()  {
		Table table = createTableForPeriodType(PeriodType.DAY);
		System.out.println(table.getName());
		Assert.assertEquals(PeriodType.DAY, table.getMetadata(TimePeriodTypeMetadata.class).getPeriodType());
	}

	@Test
	public final void testMonth() {
		Table table = createTableForPeriodType(PeriodType.MONTH);
		Assert.assertEquals(PeriodType.MONTH, table.getMetadata(TimePeriodTypeMetadata.class).getPeriodType());
	}

	@Test
	public final void testYear()  {
		Table table = createTableForPeriodType(PeriodType.YEAR);
		Assert.assertEquals(PeriodType.YEAR, table.getMetadata(TimePeriodTypeMetadata.class).getPeriodType());
	}

	public Table createTableForPeriodType(PeriodType periodType) {
		Table table = cubeManager.getTimeTable(periodType);
		Assert.assertNotNull(table);
		return table;
	}

}
