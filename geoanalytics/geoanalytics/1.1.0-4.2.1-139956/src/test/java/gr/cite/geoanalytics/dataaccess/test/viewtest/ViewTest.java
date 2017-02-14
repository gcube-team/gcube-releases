package gr.cite.geoanalytics.dataaccess.test.viewtest;

import gr.cite.gaap.viewbuilders.PostGISMaterializedViewBuilder;
import gr.cite.geoanalytics.common.ViewBuilder;
import gr.cite.geoanalytics.context.Configuration;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.principal.PrincipalData;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.shape.ShapeImport;
import gr.cite.geoanalytics.manager.UserManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.swing.text.View;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

public class ViewTest
{
	
	private EntityManager entityManager;
	private PostGISMaterializedViewBuilder vb;
	
	private static Configuration context;
	private static int dbSize = 100;
	private static int columnCount = 10;
	private static int queryColumnCount = 1;
	private static double hitRatio = 0.8f;
	private static int runs = 100;
	private static List<Integer> cols = new ArrayList<Integer>();
	
	@Inject
	public void setContext(Configuration context) {
		this.context = context;
	}
	
	@Inject
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	@Inject
	public void setVb(PostGISMaterializedViewBuilder vb) {
		this.vb = vb;
	}
	
	
	public static void resetTestCase() {
		resetTestCase(dbSize, columnCount, queryColumnCount, hitRatio, runs);
	}
	
	public static void resetTestCase(int dbSize, int columnCount, int queryColumnCount, 
			double hitRatio, int runs)
	{
		cols = new ArrayList<Integer>();
		for(int i=2; i <= columnCount; i++)
			cols.add(i);
		Collections.shuffle(cols);
		ViewTest.dbSize = dbSize;
		ViewTest.columnCount = columnCount;
		ViewTest.queryColumnCount = queryColumnCount;
		ViewTest.hitRatio = hitRatio;
		
	}
	
	private void resetColumns() {
		Collections.shuffle(cols);
	}
	
	public void createShapeEntities() throws Exception {
		EntityManager em = entityManager;
		
		System.out.print("Persisting entities...");
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		Principal sys = new Principal();
		PrincipalData sysData = new PrincipalData();
		sys.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
		sysData.setFullName("__System_User__");
		sysData.setInitials("__SU__");
		sysData.setEmail("sys@example.com");
		sys.setCreationDate(Calendar.getInstance().getTime());
		sys.setLastUpdate(Calendar.getInstance().getTime());
		sysData.setExpirationDate(new Date(3000 - 1900, 12, 31));
		sys.setName("___System_Usr___");
		sys.setCreator(sys);
		sys.setPrincipalData(sysData);
		
		em.persist(sysData);
		em.persist(sys);
		
		double lon = -105.0;
		double lat = 40.0;
		GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 8307);
		Geometry point = geometryFactory.createPoint(new Coordinate(lon, lat));
		
		ShapeImport airStripOne = new ShapeImport();
		UUID airStripOneId = UUID.randomUUID();
		airStripOne.setCreationDate(Calendar.getInstance().getTime());
		airStripOne.setCreator(sys);
		airStripOne.setData("data");
		airStripOne.setGeography(point);
		
		airStripOne.setLastUpdate(Calendar.getInstance().getTime());
		airStripOne.setShapeIdentity("id");
		airStripOne.setShapeImport(airStripOneId);
		em.persist(airStripOne);

		for(int i = 0; i < dbSize; i++) {
			Shape s = new Shape();
			s.setCode("testCode" + i);
			s.setCreationDate(new Date());
			s.setCreator(sys);
			s.setLastUpdate(new Date());
			s.setName("airStrip"+i);
			StringBuilder data = new StringBuilder();
			data.append("<shapeInfo><attr"+i+"_1 type=\"int\">"+i+"</attr"+i+"_1>");
			for(int c = 2; c <= columnCount; c++)
					data.append("<attr"+i+"_"+c+">val"+i+"_"+c+"</attr"+i+"_"+c+">");
			data.append("</shapeInfo>");
			s.setExtraData(data.toString());
			geometryFactory = new GeometryFactory(new PrecisionModel(), 8307);
			point = geometryFactory.createPoint(new Coordinate(lon, lat));
			s.setGeography(point);
			s.setShapeClass(3);
			s.setShapeImport(airStripOne);
			em.persist(s);
		}
		tx.commit();
		em.close();
		System.out.println("done");
	}
	
	public void createEntityViews() throws Exception {
		EntityManager em = entityManager;
		
		System.out.print("Creating views...");
		List<Shape> result = em.createQuery("from Shape", Shape.class).getResultList();
		//System.out.println(result.size() + " results");
		for (Shape sh : (List<Shape>) result) {
			UserManager um = new UserManager();
		//	System.out.println(vb.createViewStatementForShape(sh));
			vb.forShape(sh).createViewStatement().execute();
		}
		em.close();
		System.out.println("done");
		
	}
	
	public void createRawColumnObjects() throws SQLException {
		Connection con = null;
        Statement st = null;

        try {
        	EntityManager em = entityManager;
    	
        	System.out.print("Creating raw tables...");
    		List<Shape> result = em.createQuery("from Shape", Shape.class).getResultList();
    		//System.out.println(result.size() + " results");
    		int i = 0;
    		for (Shape sh : (List<Shape>) result) {
    			con = DriverManager.getConnection(context.getDataLayerConfig().getDbUrl(), context.getDataLayerConfig().getDbUser(), context.getDataLayerConfig().getDbPass());
                
    			st = con.createStatement();
    			st.executeUpdate("DROP TABLE IF EXISTS \"" + sh.getName() + "_raw\";");
    			st = con.createStatement();
                StringBuilder q = new StringBuilder();
                q.append("CREATE TABLE \"" + sh.getName() + "_raw\" (attr"+i+"_1 integer, ");
                for(int c=2; c<=columnCount; c++) {
                	q.append("attr"+i+"_"+c+" character varying(250)");
                	if(c < columnCount) q.append(", ");
                }
                q.append(");");
               // System.out.println(q);
                st.executeUpdate(q.toString());
                st = con.createStatement();
                q = new StringBuilder();
                q.append("INSERT INTO \"" + sh.getName() + "_raw\" VALUES("+i+", ");
                for(int c=2; c<=columnCount; c++) {
                	q.append("'val"+i+"_"+c+"'");
                	if(c < columnCount) q.append(", ");
                }
                q.append(");");
                //System.out.println(q);
                st.executeUpdate(q.toString());
                st.close();
                con.close();
                i++;
            }
    		em.close();
    		System.out.println("done");
            
        }catch (SQLException ex) {
            throw ex;

        }finally {
        	try 
            {
                if (st != null)  st.close();
                if (con != null) con.close();
            } catch (SQLException ex) 
            {
                throw ex;
            }
        }
	}
	
	public void dropEntityViews() throws SQLException
	{
		Connection con = null;
        Statement st = null;

        try 
        {
        	EntityManager em = entityManager;
    	
        	System.out.print("Dropping views...");
    		List<Shape> result = em.createQuery("from Shape", Shape.class).getResultList();
    		//System.out.println(result.size() + " results");
    		int i = 0;
    		for (Shape sh : (List<Shape>) result) {
    			con = DriverManager.getConnection(context.getDataLayerConfig().getDbUrl(), context.getDataLayerConfig().getDbUser(), context.getDataLayerConfig().getDbPass());
                
    			st = con.createStatement();
    			st.executeUpdate("DROP VIEW IF EXISTS \"" + sh.getName() + "\";");
                st.close();
                con.close();
                i++;
            }
    		em.close();
    		System.out.println("done");
            
        }catch (SQLException ex) 
        {
            throw ex;

        }finally 
        {
        	try 
            {
                if (st != null)  st.close();
                if (con != null) con.close();
            } catch (SQLException ex) 
            {
                throw ex;
            }
        }
	}
	
	public void dropRawTables() throws SQLException
	{
		Connection con = null;
        Statement st = null;

        try 
        {
        	EntityManager em = entityManager;
    	
        	System.out.print("Dropping raw tables...");
    		List<Shape> result = em.createQuery("from Shape", Shape.class).getResultList();
    		//System.out.println(result.size() + " results");
    		int i = 0;
    		for (Shape sh : (List<Shape>) result) {
    			con = DriverManager.getConnection(context.getDataLayerConfig().getDbUrl(), context.getDataLayerConfig().getDbUser(), context.getDataLayerConfig().getDbPass());
                
    			st = con.createStatement();
    			st.executeUpdate("DROP TABLE IF EXISTS \"" + sh.getName() + "_raw\";");
                st.close();
                con.close();
                i++;
            }
    		em.close();
    		System.out.println("done");
            
        }catch (SQLException ex) 
        {
            throw ex;

        }finally 
        {
        	try 
            {
                if (st != null)  st.close();
                if (con != null) con.close();
            } catch (SQLException ex) 
            {
                throw ex;
            }
        }
	}
	
	public long testShapeObjectQueries() throws SQLException
	{

		ResultSet rs = null;
		Statement st = null;
		Connection con = null;
		
		try 
        {
    		con = DriverManager.getConnection(context.getDataLayerConfig().getDbUrl(), context.getDataLayerConfig().getDbUser(), context.getDataLayerConfig().getDbPass());

    		double adjustedHitRatio = Math.pow(hitRatio, (1/(double)columnCount));
    		//double adjustedHitRatio = hitRatio;
    		long start = System.nanoTime();
    		
    		Random rnd = new Random();
    		int hits = 0;
    		for (int i=0; i<dbSize; i++) {
    			resetColumns();
                st = con.createStatement();
                StringBuilder q = new StringBuilder();
                q.append("SELECT *  from \"airStrip" + i + "\" where attr"+i+"_1=" + (rnd.nextDouble() < adjustedHitRatio ? i : (i+1)));
                Iterator<Integer> it = cols.iterator();
                for(int c=2; c<=queryColumnCount; c++)
                {
                	Integer colIndex = it.next();
                	q.append(" and attr"+i+"_"+colIndex+"='val"+i+"_"+(rnd.nextDouble() < adjustedHitRatio ? colIndex : (colIndex+1)) + "'");
                }
                q.append(";");
                //System.out.println(q.toString());
                rs = st.executeQuery(q.toString()); 
                while(rs.next())
                {
                	hits++;
//                	System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getString(3) + " " + rs.getString(4) + " " + rs.getString(5));
                }
            }
    		long end = System.nanoTime();
    		System.out.println("View queries completed in " + ((end-start)/(double)1000000) + " milliseconds, hit ratio=" + (hits/(double)dbSize));
    		return end-start;
	            
        }finally 
        {
        	try 
            {
        		if (rs != null)  rs.close();
                if (st != null)  st.close();
                if (con != null) con.close();
            } catch (SQLException ex) 
            {
                throw ex;
            }
        }
	}
	
	public long testRawColumnObjectQueries() throws SQLException
	{
		ResultSet rs = null;
		Statement st = null;
		Connection con = null;
		
		try 
        {
    		con = DriverManager.getConnection(context.getDataLayerConfig().getDbUrl(), context.getDataLayerConfig().getDbUser(), context.getDataLayerConfig().getDbPass());
    		
    		double adjustedHitRatio = Math.pow(hitRatio, (1/(double)columnCount));
    		//double adjustedHitRatio = hitRatio;
    		long start = System.nanoTime();
    		
    		int hits = 0;
    		Random rnd = new Random();
    		for (int i=0; i<dbSize; i++) {
    			resetColumns();
                st = con.createStatement();
                StringBuilder q = new StringBuilder();
                q.append("SELECT *  from \"airStrip" + i + "_raw\" where attr"+i+"_1=" + (rnd.nextDouble() < adjustedHitRatio ? i : (i+1)));
                Iterator<Integer> it = cols.iterator();
                for(int c=2; c<=queryColumnCount; c++)
                {
                	Integer colIndex = it.next();
                	q.append(" and attr"+i+"_"+colIndex+"='val"+i+"_"+(rnd.nextDouble() < adjustedHitRatio ? colIndex : (colIndex+1)) + "' ");
                }
                q.append(";");
               // System.out.println(q.toString());
                rs = st.executeQuery(q.toString()); 
                while(rs.next())
                {
                	hits++;
//                	System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getString(3) + " " + rs.getString(4) + " " + rs.getString(5));
                }
            }
    		long end = System.nanoTime();
    		System.out.println("Raw dataqueries completed in " + ((end-start)/(double)1000000) + " milliseconds, hit ratio=" + (hits/(double)dbSize));
    		return end-start;
    	
	            
        }finally 
        {
        	try 
            {
        		if (rs != null)  rs.close();
                if (st != null)  st.close();
                if (con != null) con.close();
            } catch (SQLException ex) 
            {
                throw ex;
            }
        }
	}
	
	public void test() throws Exception
	{
		createShapeEntities();
		createEntityViews();
		createRawColumnObjects();
		
		Map<Integer, Double> rs = new LinkedHashMap<Integer, Double>();
		for(int c = 0; c < columnCount; c++)
		{
			double ratioSum = 0.0f;
			resetTestCase(dbSize, columnCount, c, hitRatio, runs);
			for(int i = 0; i < runs; i++)
			{
				long raw = testRawColumnObjectQueries();
				long view = testShapeObjectQueries();
				ratioSum +=  (raw/(double)view);
			}
			double r = (ratioSum/(double)runs);
			System.out.println("Ratio: " + r );
			rs.put(c, r);
		}
		
		for(Map.Entry<Integer, Double> r : rs.entrySet())
			System.out.println(r.getKey() + ":" + r.getValue());
		
		dropEntityViews();
		dropRawTables();
	}
}
