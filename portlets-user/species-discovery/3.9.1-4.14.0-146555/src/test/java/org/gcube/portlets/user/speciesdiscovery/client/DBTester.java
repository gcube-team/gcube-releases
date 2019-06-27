/**
 *
 */
package org.gcube.portlets.user.speciesdiscovery.client;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.metamodel.EntityType;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.speciesdiscovery.shared.ResultRow;
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

		//Class.forName("org.h2.Driver");
		//Connection conn = DriverManager.getConnection("jdbc:h2:/home/francesco-mangiacrapa/Portal-Bundle-3.0.0-3.2.0/tomcat-6.0.29/persistence/h2dbspecies/h2testusergcubedevsec;create=true", "","");
		// add application code here

		/* Statement stat = conn.createStatement();
		 ResultSet rs = stat.executeQuery("select * from ResultRow");

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
		 }*/

		//testJdbcDataSource();

		//String queryString = "SELECT MIN(tax.id) from Taxon tax";


//
//		testTypedQuery(queryString, Taxon.class);

		getAllEntities();


		 String queryString = "SELECT *" +
		 	" FROM "+ResultRow.class.getSimpleName()+" r" +
		 	" LEFT OUTER JOIN RESULTROW_TAXON rt";
//						" INNER JOIN "+Taxon.class.getSimpleName()+" t";

		 queryString = "select *" +
		 	" from RESULTROW r JOIN RESULTROW_TAXON rt on r.ID=rt.RESULTROW_ID JOIN TAXON t on t.INTERNALID=rt.MATCHINGTAXON_INTERNALID" +
		 	" where t.RANK = 'Genus' and t.ID IN" +
		 	" (select MIN(tax.ID) from TAXON tax)";
//////
//
//		 testTypedQuery(queryString, ResultRow.class);

		//testQuery(queryString);


		testNativeQuery(queryString, ResultRow.class);

	}

	/**
	 * @param queryString
	 * @param class1
	 */
	private static void testNativeQuery(String queryString, Class<?> className) {

		EntityManagerFactory emF = createEntityManagerFactory("/home/francesco-mangiacrapa/Portal-Bundle-3.0.0-3.2.0/tomcat-6.0.29/persistence/h2dbspecies/h2testusergcubedevsec;");
		EntityManager em = emF.createEntityManager();
		Query query = em.createNativeQuery(queryString, className);

		List<Object> listResult = new ArrayList<Object>();
		try {
			listResult = query.getResultList();
			for (Object object : listResult) {
				System.out.println(object.toString());
			}
		} catch (Exception e) {
			logger.error("Error in TypedQuery: " + e.getMessage(), e);
		}  finally {
			em.close();
		}


	}

	public static void getAllEntities(){
		EntityManagerFactory emF = createEntityManagerFactory("/home/francesco-mangiacrapa/Portal-Bundle-3.0.0-3.2.0/tomcat-6.0.29/persistence/h2dbspecies/h2testusergcubedevsec;");
		EntityManager em = emF.createEntityManager();
		for (EntityType<?> entity : em.getMetamodel().getEntities()) {
		    final String className = entity.getName();
		    System.out.println("Trying select * from: " + className);
		    Query q = em.createQuery("SELECT c from " + className + " c");
		    q.getResultList().iterator();
		    System.out.println("ok: " + className);
		}


	}

	public static void testTypedQuery(String queryString, Class classToReturn){
		EntityManagerFactory emF = createEntityManagerFactory("/home/francesco-mangiacrapa/Portal-Bundle-3.0.0-3.2.0/tomcat-6.0.29/persistence/h2dbspecies/h2testusergcubedevsec;");
		EntityManager em = emF.createEntityManager();
		TypedQuery<Class> tQuery = em.createQuery(queryString, classToReturn);

		List<Class> listResult = new ArrayList<Class>();
		try {
			listResult = tQuery.getResultList();
			System.out.println(listResult.toString());
		} catch (Exception e) {
			logger.error("Error in TypedQuery: " + e.getMessage(), e);
		}  finally {
			em.close();
		}

	}

	public static void testQuery(String queryString){
		EntityManagerFactory emF = createEntityManagerFactory("/home/francesco-mangiacrapa/Portal-Bundle-3.0.0-3.2.0/tomcat-6.0.29/persistence/h2dbspecies/h2testusergcubedevsec;");
		EntityManager em = emF.createEntityManager();
		Query query = em.createQuery(queryString);

		List<Object> listResult = new ArrayList<Object>();
		try {
			listResult = query.getResultList();
			System.out.println(listResult.toString());
		} catch (Exception e) {
			logger.error("Error in TypedQuery: " + e.getMessage(), e);
		}  finally {
			em.close();
		}

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
