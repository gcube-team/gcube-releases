package org.gcube.dataaccess.test;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;



public class TestPostgres {

	/**
	 * @param args
	 */
	
	@SuppressWarnings({"unchecked"})
	public static List<Object> executeHQLQuery(String query, SessionFactory DBSessionFactory, boolean useSQL) throws Exception{
		
		List<Object> obj = null;
		Session ss = null;
		try {
			ss = DBSessionFactory.getCurrentSession();

			ss.beginTransaction();

			Query qr = null;

			if (useSQL)
				qr = ss.createSQLQuery(query);
			else
				qr = ss.createQuery(query);

			List<Object> result = qr.list();

			ss.getTransaction().commit();

			/*
			if (result == null)
				System.out.println("Hibernate doesn't return a valid object when org.gcube.contentmanagement.lexicalmatcher retrieve UserState Object");

			if (result != null && result.size() == 0)
				System.out.println(String.format("found nothing in database"));
*/
			if (result != null && result.size() != 0) {
				obj = result;
			}

		} catch (Exception e) {

//			System.out.println(String.format("Error while executing query: %1$s %2$s", query, e.getMessage()));
//			e.printStackTrace();
			System.out.println(String.format("Error while executing query: %1$s %2$s", query, e.getMessage()));
			throw e;
		}

		return obj;

	}
	
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub

		/*AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath("./cfg/");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		//config.setParam("DatabaseDialect", "org.hibernate.dialect.PostgresPlusDialect");
		config.setParam(
				"DatabaseURL",
				"jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");

		SessionFactory dbconnection = DatabaseUtils.initDBSession(config);
		List<Object> rawnames = DatabaseFactory
				.executeSQLQuery(
						"select a.source_data as sourceA, b.source_data as sourceB, a.target_data_scientific_name as targetA, b.target_data_scientific_name as tagertB " +
						"from bionymoutsimplefaked1csvpreprcsv as a join bionymoutfaked1csvpreprcsv as b on a.source_data=b.source_data limit 10",
						dbconnection);
		
		
		List<Object> rawnames = DatabaseFactory
		.executeSQLQuery(
				"select a.source_data as sourceA, a.target_data_scientific_name as targetA, b.source_data sourceB " +
				"from bion_id_a1f27126_df23_4980_8e2b_4afc8aaa404f as a " +
				"left join bion_id_ab251ee0_7cc6_49b2_8956_330f4716650f as b " +
				"on a.source_data=b.source_data",
				dbconnection);
		
		
		//List<Object> rawnames = DatabaseFactory.executeHQLQuery(query, DBSessionFactory, useSQL);
		
		
		
		
		
		
		System.out.println("***************************************************************");
		System.out.println();

		for (int i = 0; i < rawnames.size(); i++) {

			Object[] row = (ObEntityManagerject[]) rawnames.get(i);

			for (int j = 0; j < row.length; j++) {

				System.out.print("\"" + row[j] + "\"; ");

			}
			System.out.println();
			//System.out.println("Fine ");

		}*/
		
		
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath("./cfg/");
		config.setParam("DatabaseUserName", "root");
		config.setParam("DatabasePassword", "test");
		config.setParam("DatabaseDriver","com.mysql.jdbc.Driver");
		config.setParam("DatabaseDialect", "org.hibernate.dialect.MySQLDialect");
		config.setParam(
				"DatabaseURL",
				"jdbc:mysql://146.48.87.169/timeseries");

		SessionFactory dbconnection = DatabaseUtils.initDBSession(config);
		
		
		/*List<Object> rawnames = DatabaseFactory
				.executeSQLQuery(
						"select a.source_data as sourceA, b.source_data as sourceB, a.target_data_scientific_name as targetA, b.target_data_scientific_name as tagertB " +
						"from bionymoutsimplefaked1csvpreprcsv as a join bionymoutfaked1csvpreprcsv as b on a.source_data=b.source_data limit 10",
						dbconnection);*/
		
		//try{
			
				
			
			
//			dbconnection.getCurrentSession().;
//					
//
//			ss.getCause()
			
	//	}		
		 /*catch(JDBCExceptionReporter e)
		    {
		            Throwable t = e.getCause();               
		            SQLException ex = (SQLException) t.getCause();
		            while(ex != null){
		                 while(t != null) {
		                     t = t.getCause();
		                 }
		                // Logger logger=new Logger();
		                // logger.warn("SQLException="+ex.getLocalizedMessage());
		                 
		                 System.out.println("sono qui");

		                 ex = ex.getNextException();
		            }
		    } 
		*/
		System.out.println("***************************************************************");
		System.out.println();
		
		//List<Object> rawnames =((javax.persistence.Query) query).getResultList(); 
		
		
		
		try{
			
			/*List<Object> rawnames = executeHQLQuery(
							"select * from (select a.field1, b.field1_id as bb, b.field1 from a7f768710_c7b0_11df_b2bc_e0f36cf4c8cd as a "+ 
									"left join cur_00d4e2d0_ecbd_11df_87fa_de008e0850ff as b on a.field3_id=b.field1_id) as cd",*/
//							"select * "+
//							"from a7f768710_c7b0_11df_b2bc_e0f36cf4c8cd as a,  cur_00d4e2d0_ecbd_11df_87fa_de008e0850ff as b limit 10",
//							dbconnection, true);
			
			
			List<Object> rawnames = executeHQLQuery(
					"select a.field1, b.field1_id from a7f768710_c7b0_11df_b2bc_e0f36cf4c8cd as a "+ 
							"left join cur_00d4e2d0_ecbd_11df_87fa_de008e0850ff as b on a.field3_id=b.field1_id",dbconnection, true);
//					"select * "+
//					"from a7f768710_c7b0_11df_b2bc_e0f36cf4c8cd as a,  cur_00d4e2d0_ecbd_11df_87fa_de008e0850ff as b limit 10",
//					dbconnection, true);	
			
			
			
		System.out.println("Size: "+rawnames.size());

		for (int i = 0; i < rawnames.size(); i++) {

			Object[] row = (Object[]) rawnames.get(i);

			for (int j = 0; j < row.length; j++) {

				System.out.print("\"" + row[j] + "\"; ");

			}
			System.out.println();
			//System.out.println("Fine ");

		}
		
	}catch(Exception e){
		e.printStackTrace();
		System.out.println("message: "+e.getMessage());  
		//System.out.println(e.getLocalizedMessage());
		
		StackTraceElement [] elem=e.getStackTrace();
		
		
		System.out.println("localized: "+e.getCause().toString());
		String error=e.getCause().toString();
		if (error.contains("MySQLSyntaxErrorException"))
		{
			
			System.out.println("ERROR "+e.getMessage()+" "+"because an error is present: "+e.getCause().getMessage());
			
			
		}
		
//	     System.out.println("cause: "+e.getCause().getMessage());	
		
//		for (int i=0;i<elem.length;i++){
//			
//			
//			
//		System.out.println("elem: "+ elem[i]);	
//			
//			
//			
//		}
		//System.out.println("JDBC Error: "+JDBCExceptionReporter.DEFAULT_EXCEPTION_MSG);
		//System.out.println("JDBC Error: "+JDBCExceptionReporter.DEFAULT_WARNING_MSG);
		//System.out.println("JDBC Error: "+JDBCExceptionReporter.log.getName());
		//System.out.println("JDBC Error: "+JDBCExceptionReporter.log.ROOT_LOGGER_NAME);
		//org.slf4j.Logger logger = LoggerFactory.getLogger("log4j.logger.org.hibernate");
		
		
//		Session s=dbconnection.openSession();
//		s.close()
		
		
		
		
	//	System.out.println(dbconnection.getCache().getClass());
		
		//SQLException ex= new SQLException(JDBCExceptionReporter.log.getName());
		
		//SQLException ex= new SQLException
		
		//System.out.println("message: "+ex.getLocalizedMessage());
		
	//	System.out.println(ex.getCause();
		
	//	JDBCExceptionReporter.;
		
		//System.out.println("Eccezione:" +e.getMessage());
		//e.printStackTrace();
		//e.getLocalizedMessage();
		
		
		/*Connection a=dbconnection.getCurrentSession().disconnect();
		
		
		try {
			System.out.println(a.getWarnings().getSQLState());
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			
			System.out.println("message: "+e1.getCause().getStackTrace().length);
			//e1.printStackTrace();
		}
		*/
		
		
	}

	}
}


