/**
 * 
 */
package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.server;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerDataSpace;
import org.gcube.portlets.user.tdw.datasource.jdbc.JDBCDataSource;
import org.gcube.portlets.user.tdw.datasource.jdbc.SimpleJDBCDataSourceFactory;
import org.gcube.portlets.user.tdw.server.datasource.DataSource;
import org.gcube.portlets.user.tdw.server.datasource.DataSourceException;
import org.gcube.portlets.user.tdw.server.datasource.DataSourceFactory;
import org.gcube.portlets.user.tdw.shared.model.TableId;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.Constants;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.server.util.SessionUtil;

/**
 * @author ceras
 *
 */
public class StatisticaManagerDataSourceFactory implements DataSourceFactory {
	
	protected Logger logger = Logger.getLogger(SimpleJDBCDataSourceFactory.class);
//	private BufferedHashMap<String, JDBCDataSource> dataSourcesBuffer = new BufferedHashMap<String, JDBCDataSource>(10); // TODO change to 100
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdw.server.datasource.DataSourceFactory#getId()
	 */
	@Override
	public String getId() {
		return Constants.TD_DATASOURCE_FACTORY_ID;
	}
	

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdw.server.datasource.DataSourceFactory#openDataSource(org.gcube.application.framework.core.session.ASLSession, org.gcube.portlets.user.tdw.shared.model.TableId)
	 */
	@Override
	public DataSource openDataSource(ASLSession session, TableId tt) throws DataSourceException {

		String id = tt.getTableKey();
		String tableId = id;
		
		// the table id
		tableId = tableId.toLowerCase();
		
		// get url from service
		String url = getUrlFromTableId(session, tableId);
		
		// create the datasource, with jdbc url and table name (id)
		JDBCDataSource dataSource = new JDBCDataSource(Constants.TD_DATASOURCE_FACTORY_ID, url, tableId);

		
//		JDBCDataSource dataSource = dataSourcesBuffer.get(tableId);
//		
//		if (dataSource==null) {
//			
//			// get url from service
//			String url = getUrlFromTableId(session, tableId);
//			
//			// create the datasource, with jdbc url and table name (id)
//			dataSource = new JDBCDataSource(Constants.TD_DATASOURCE_FACTORY_ID, url, tableId);
//			
//			dataSourcesBuffer.put(tableId, dataSource);
//		}
		
		return dataSource;
		
	}

	/**
	 * @param tableId
	 * @return
	 */
	protected String getUrlFromTableId(ASLSession session, String tableId) {

		StatisticalManagerDataSpace dataSpace = SessionUtil.getDataSpaceService(session);
		String url = dataSpace.getDBParameters(tableId);

//		System.out.println("NAME: "+param.getName());
//		System.out.println("VALUE: "+param.getValue().getValues()[0]);
//		System.out.println("TABLE NAME:" +tableName);
		System.out.println("TABLE URL:" +url);		
		
		return url;
	}


	@Override
	public void closeDataSource(ASLSession session, DataSource dataSource) throws DataSourceException {
		JDBCDataSource jdatasource = (JDBCDataSource) dataSource;
//		Collection<JDBCDataSource> values = dataSourcesBuffer.values();
//		if (!values.contains(jdatasource));
			jdatasource.close();
	}

//	public abstract String[] getTableInfos(String tableId);

}
