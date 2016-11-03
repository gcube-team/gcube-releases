/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
import org.h2.jdbcx.JdbcDataSource;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 11, 2013
 * 
 */
public class DBTester {

	public static final String JDBCDRIVER = "jdbc:h2:";

	public static Logger logger = Logger.getLogger(DBTester.class);


	
	public static void main(String[] a) throws Exception {
		/*
		Class.forName("org.h2.Driver");
		Connection conn = DriverManager.getConnection("jdbc:h2:/home/francesco-mangiacrapa/Portal-Bundle2.2/tomcat-6.0.29/persistence/h2dbspecies/h2testusergcubedevsec;create=true", "","");
		// add application code here
		
		 Statement stat = conn.createStatement();
		 ResultSet rs = stat.executeQuery("select * from TaxonomyRow");
		 
		 ResultSetMetaData meta = rs.getMetaData();
		 int columnCount = meta.getColumnCount();


		 
		 while (rs.next())
		 {
			 System.out.println("New row");
			 for (int i = 1; i <= columnCount; i++) {
					
//				 System.out.println("ColumName: "+ meta.getColumnName(i));
				 System.out.println("ColumLabel: "+meta.getColumnLabel(i));
				 System.out.println(rs.getString(meta.getColumnLabel(i)));
				 
			 }
			 
			 System.out.println("\n\n");
		 }
		conn.close();
		*/
	
		testJdbcDataSource();
	}

	
	
	public static void testJdbcDataSource() throws NamingException {

		JdbcDataSource ds = new JdbcDataSource();
		ds.setURL("jdbc:h2:/home/francesco-mangiacrapa/Portal-Bundle2.2/tomcat-6.0.29/persistence/h2dbspecies/h2testusergcubedevsec;create=true");
		Context ctx = new InitialContext();
		ctx.bind("java:global/jpa-eclipselink/TaxonomyRow", ds);
		
//		final Context context = EJBContainer.createEJBContainer(p).getContext();
//		
//        Movies movies = (Movies) context.lookup("java:global/jpa-eclipselink/Movies");
		try {
			Connection conn = ds.getConnection();
			 ResultSet rs = conn.createStatement().executeQuery("select * from TaxonomyRow");
			 ResultSetMetaData meta = rs.getMetaData();
			 int columnCount = meta.getColumnCount();
	
	
			 while (rs.next())
			 {
				 System.out.println("New row");
				 for (int i = 1; i <= columnCount; i++) {
						
	//				 System.out.println("ColumName: "+ meta.getColumnName(i));
					 System.out.println("ColumLabel: "+meta.getColumnLabel(i));
					 System.out.println(rs.getString(meta.getColumnLabel(i)));
					 
				 }
				 
				 System.out.println("\n\n");
			 }
			conn.close();
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	public static EntityManagerFactory createEntityManagerFactory(String connectionUrl) {

		Map<String, String> properties = new HashMap<String, String>();
		// properties.put("javax.persistence.jdbc.driver", jdbcDriverH2);
		String jdbcUrl = JDBCDRIVER + connectionUrl + ";create=true";
		logger.trace("jdbc url " + jdbcUrl);
		try {
			properties.put("javax.persistence.jdbc.url", jdbcUrl);
		} catch (Exception e) {
			logger.error("error on javax.persistence.jdbc.url " + e, e);
		}

		// emf = Persistence.createEntityManagerFactory("jpablogPUnit");
		return Persistence.createEntityManagerFactory("SPD_PERSISTENCE_FACTORY", properties);

	}

}
