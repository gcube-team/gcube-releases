package org.gcube.portlets.user.tdw.datasource.jdbc;

import junit.framework.Assert;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portlets.user.tdw.server.datasource.DataSource;
import org.gcube.portlets.user.tdw.server.datasource.DataSourceException;
import org.gcube.portlets.user.tdw.server.datasource.Direction;
import org.gcube.portlets.user.tdw.shared.model.TableId;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDBDatasourceTest {
	
	private static final String TEST_DATABASE_TABLE = "adespekjvbhzoptamcvewivtorlicofm";
	private static final String TEST_DATABASE_URL = "jdbc:postgresql://pc-fortunati.isti.cnr.it:5432/tabulardata";
	
	private static final Logger log = LoggerFactory.getLogger(JDBDatasourceTest.class);

	@Test
	public void testGetDataAsJson() {
		SimpleJDBCDataSourceFactory dataSourceFactory = new SimpleJDBCDataSourceFactory();

		String tableKey = dataSourceFactory.registerTable(
				TEST_DATABASE_URL, TEST_DATABASE_TABLE);
		TableId tableId = new TableId(dataSourceFactory.getId(), tableKey);

		ASLSession session = SessionManager.getInstance().getASLSession("test", "luigi.fortunati");
		try {
			DataSource dataSource = dataSourceFactory.openDataSource(session, tableId);
			String jsonDocument = dataSource.getDataAsJSon(1, 500, null, Direction.ASC);
			Assert.assertNotNull(jsonDocument);
			Assert.assertFalse(jsonDocument.isEmpty());
			log.info(jsonDocument);
		} catch (DataSourceException e) {
			e.printStackTrace();
		}
	}

}
