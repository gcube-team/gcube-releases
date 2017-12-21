package org.gcube.data.spd.itis;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.data.spd.itis.capabilities.ClassificationCapabilityImpl;
import org.gcube.data.spd.itis.capabilities.ExpansionCapabilityImpl;
import org.gcube.data.spd.itis.capabilities.NamesMappingImpl;
import org.gcube.data.spd.itis.dbconnection.ConnectionPool;
import org.gcube.data.spd.itis.dbconnection.ConnectionPoolException;
import org.gcube.data.spd.model.CommonName;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.RepositoryInfo;
import org.gcube.data.spd.model.products.DataProvider;
import org.gcube.data.spd.model.products.DataSet;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.model.products.Taxon;
import org.gcube.data.spd.model.util.Capabilities;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.plugin.fwk.capabilities.ClassificationCapability;
import org.gcube.data.spd.plugin.fwk.capabilities.ExpansionCapability;
import org.gcube.data.spd.plugin.fwk.capabilities.MappingCapability;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ItisPlugin extends  AbstractPlugin{

	static Logger logger = LoggerFactory.getLogger(ItisPlugin.class);

	public static String hostName;
	public static String jdbc;
	public static String user;
	public static String password;	


	final static String urlDump = "http://www.itis.gov/downloads/itisMySQLTables.tar.gz";
	public static String baseurl = "http://www.itis.gov/ITISWebService/services/ITISService";
	public final static String driver = "com.mysql.jdbc.Driver";
	final static String zipFileName = "itisMySQLTables.tar.gz";
	final static String fileDump = "dropcreateloaditis.sql";


	@Override
	public void initialize(ServiceEndpoint res) throws Exception {
		try{
			for (AccessPoint ap:res.profile().accessPoints()) {
				if (ap.name().equals("jdbc")) {
//					jdbc = "jdbc:mysql://node24.d.d4science.research-infrastructures.eu/ITIS?autoReconnect=true";
					jdbc = ap.address();	
					user = ap.username();	
					password = StringEncrypter.getEncrypter().decrypt(ap.password());
								System.out.println("jdbc "+ jdbc + " user "+  user + " password "+ password);

					Iterator<Property> properties = ap.properties().iterator();
					while(properties.hasNext()) {
						Property p = properties.next();			    
						if (p.name().equals("hostName")){							
							hostName = p.toString();
							break;
						}
					}			    				
				}
			}
		}catch (Exception e) {
			logger.error("Error in inizialize", e);
		}
		try{
			if (!Utils.SQLTableExists("updates")){
				new UpdateThread(0);			
			}
			else{
				long update = Utils.lastupdate();
				new UpdateThread(update);
			}
		}catch (Exception e) {
			logger.error("Error during update", e);
		}

		super.initialize(res);
	}


	@Override
	public void update(ServiceEndpoint res) throws Exception {

		for (AccessPoint ap:res.profile().accessPoints()) {	
			if (ap.name().equals("jdbc")) {
				jdbc = ap.address();	
				user = ap.username();
				password = ap.password();

				Iterator<Property> properties = ap.properties().iterator();
				while(properties.hasNext()) {
					Property p = properties.next();			    
					if (p.equals("hostName")){
						hostName = p.toString();
						break;
					}			    			
				}	
			}	
		}
		super.update(res);
	}



	@Override
	public String getDescription() {
		return ("Interagency Taxonomic Information System (ITIS) Plugin");
	}

	@Override
	public String getRepositoryName() {
		return ("ITIS");
	}

	@SuppressWarnings("serial")
	@Override
	public Set<Capabilities> getSupportedCapabilities() {
		return new HashSet<Capabilities>(){{add(Capabilities.NamesMapping);add(Capabilities.Classification);add(Capabilities.Expansion);}};
	}


	@Override
	public ClassificationCapability getClassificationInterface() {
		return new ClassificationCapabilityImpl();
	}


	@Override
	public ExpansionCapability getExpansionInterface() {
		return new ExpansionCapabilityImpl();
	}


	@Override
	public MappingCapability getMappingInterface() {
		return new NamesMappingImpl();
	}


	@Override
	public void searchByScientificName(String scientificName,
			ObjectWriter<ResultItem> writer, Condition... properties) {
		ResultSet rs = null;
		try{
			ConnectionPool pool = null;
			Connection con = null;
			try {
				pool = ConnectionPool.getConnectionPool();
				con = pool.getConnection();

				String query = "select a.tsn, a.completename from longnames as a join taxonomic_units as b on a.tsn = b.tsn where UPPER(a.completename) like UPPER(?)";		
				rs =  pool.selectPrestatement(query, "%" + scientificName + "%");	

			} catch (ConnectionPoolException e) {
				logger.error("ConnectionPoolException", e);			
			} catch (SQLException e) {
				logger.error("SQL Error", e);
			} finally {				
				if ((pool!=null) && (con!=null)){
					pool.releaseConnection(con);
				}
			}

			if (rs!=null){	
				while(rs.next())
				{	
					String tsn = rs.getString(1);   				
					String completeName = rs.getString(2);                	
					searchByTsn(tsn, writer, completeName,"scientific name", null);
				}
			}
		} catch (SQLException e) {
			logger.error("SQL Error", e);
		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException Error", e);
		}finally{
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException ex) {
				logger.error("SQL Error", ex);
			}
		}
	}



	//	private ResultSet createRSByCommonName(String commonName) {
	//		ConnectionPool pool = null;
	//		Connection con = null;
	//		ResultSet rs = null;
	//		try {
	//			pool = ConnectionPool.getConnectionPool();
	//			con = pool.getConnection();
	//
	//			String query = "select tsn, vernacular_name from vernaculars where UPPER(vernacular_name) like UPPER(?)";		
	//			rs =  pool.selectPrestatement(query, "%" + commonName + "%");	
	//
	//		} catch (ConnectionPoolException e) {
	//			logger.error("ConnectionPoolException", e);			
	//		} catch (SQLException e) {
	//			logger.error("SQL Error", e);
	//
	//		} finally {		
	//			if ((pool!=null) && (con!=null)){
	//				pool.releaseConnection(con);
	//			}
	//		}
	//		return rs;
	//	}
	//
	//
	//	/**
	//	 * Search by id (if you are looking for a common  name) or search by scientific name
	//	 */
	//	private void searchCommon(String tsn, ObjectWriter<ResultItem> writer, String type) {
	//
	//		//		public void search(String id, ObjectWriter<ResultItem> writer, String type) {
	//		//		logger.trace("search by: " + type + "; id: " + id);
	//
	//		ConnectionPool pool = null;
	//		Connection con = null;
	//		ResultSet rs = null;
	//		try {
	//			pool = ConnectionPool.getConnectionPool();
	//			con = pool.getConnection();
	//
	//			String query = "select completename from longnames where tsn = ?";		
	//			rs =  pool.selectPrestatement(query, tsn);	 
	//
	//			if(rs.next()){	
	//
	//				String completeName = rs.getString(1);                 	
	//				searchByTsn(tsn, writer, completeName,type, null);
	//			}
	//
	//		} catch (SQLException e) {
	//			logger.error("SQL Error", e);
	//		} catch (ConnectionPoolException e) {
	//			logger.error("ConnectionPoolException", e);
	//
	//		} finally {
	//			try {
	//				if (rs != null) {
	//					rs.close();
	//				}
	//			} catch (SQLException ex) {
	//				logger.error("SQL Error", ex);
	//			}
	//			if ((pool!=null) && (con!=null)){
	//				pool.releaseConnection(con);
	//			}
	//		}
	//	}


	/**
	 * Search by id (if you are looking for a common  name) or search by scientific name
	 * @throws ConnectionPoolException 
	 */
	public void searchByTsn(String tsn, ObjectWriter<ResultItem> writer, String completeName, String type, String id) throws ConnectionPoolException {

		ResultSet rs2 = null;
		ConnectionPool pool = null;
		Connection con = null;
		try{
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();

			String query = "select a.source, a.version, a.source_id from other_sources as a join reference_links as b on b.doc_id_prefix = a.source_id_prefix and b.documentation_id = a.source_id and b.tsn = ?";		
			rs2 =  pool.selectPrestatement(query, tsn);


			if (rs2!=null){	
				while(rs2.next()) {		
					//get version
					String  source = rs2.getString(1);	
					String  version = rs2.getString(2);
					String  id_source = rs2.getString(3);

					ResultItem ri = new ResultItem(tsn, completeName);

					DataSet dataSet = new DataSet(id_source);			
					dataSet.setName(source + " " + version );

					DataProvider dp = new DataProvider("ITIS");	
					dp.setName("The Integrated Taxonomic Information System (ITIS)");

					dataSet.setDataProvider(dp);

					ri.setDataSet(dataSet);

					//set common names
					List<CommonName> commonNames = Utils.getCommonNameFromId(tsn);
					ri.setCommonNames(commonNames);
					commonNames.clear();

					ri.setLsid("urn:lsid:itis.gov:itis_tsn:"+ tsn);				
					ri.setCitation(Utils.getCitationItis());
					ri.setCredits(Utils.getCitationItis());

					// logger.trace("select taxon_author_id, rank_id from taxonomic_units where tsn = " + tsn );

					ResultSet rs = null;

					try {
						pool = ConnectionPool.getConnectionPool();
						con = pool.getConnection();
						Taxon hierarchy = null;
						if (type.equals("synonym")) {
							hierarchy = getHierarchyFromId(id, pool, con);
							ri.setParent(hierarchy);

							String query1 = "select taxon_author_id, rank_id from taxonomic_units where tsn = ?";		
							rs =  pool.selectPrestatement(query1, id);

						}
						else {
							//							logger.trace("select taxon_author_id, rank_id from taxonomic_units where tsn = " + tsn );
							hierarchy = getHierarchyFromId(tsn, pool, con);
							ri.setParent(hierarchy);

							String query1 = "select taxon_author_id, rank_id from taxonomic_units where tsn = ?";		
							rs =  pool.selectPrestatement(query1, tsn);

						}
						if (rs!=null) {
							if (rs.next()) {
								// logger.trace(" taxon_author_id " + results.getString(1) + " rank_id " + results.getString(2));
								ri.setScientificNameAuthorship(Utils.getInfoFromId(rs.getString(1), "author"));												
								ri.setRank(Utils.getInfoFromId(rs.getString(2), "rank"));    
							}
						}
					}catch (Exception e) {
						logger.error("Error retrieving author and rank", e);
					}finally {
						try {
							if (rs != null)
								rs.close();
						} catch (SQLException ex) {
							logger.error("SQL Error", ex);
						}
					}
					writer.write(ri);
				}
			}
		} catch (SQLException e) {
			logger.error("SQL Error", e);
		}finally{
			try {
				if (rs2 != null)
					rs2.close();
				if ((pool!=null) && (con!=null)){
					pool.releaseConnection(con);
				}
			} catch (SQLException ex) {
				logger.error("SQL Error", ex);
			}
		}


	}


	/**
	 * Get Hierarchy by ID (return Taxon)
	 */
	public Taxon getHierarchyFromId(String id, ConnectionPool pool, Connection con) {
		//		logger.trace("getHierarchyFromId " + id);

		Taxon b = null;
		ResultSet rs = null;
		try {
			if (pool == null) pool = ConnectionPool.getConnectionPool();
			if (con==null) con = pool.getConnection();

			String query = "select a.parent_tsn, b.unit_name1, b.unit_name2, b.unit_name3, b.unit_name4, b.taxon_author_id, b.rank_id from taxonomic_units as a join taxonomic_units as b on a.parent_tsn = b.tsn and a.tsn = ?";		
			rs =  pool.selectPrestatement(query, id);

			if(rs.next()) {		
				String parent_id = rs.getString(1);	
				String rank_id = rs.getString(7);
				String citation_id = rs.getString(6);

				if (!rs.getString(2).equals("")) {
					String sName = rs.getString(2);
					if (!rs.getString(3).equals("")) {
						sName = sName.concat(" " + rs.getString(3));
						if (!rs.getString(4).equals("")) {
							sName = sName.concat(" " + rs.getString(4));
							if (!rs.getString(5).equals("")) {
								sName = sName.concat(" " + rs.getString(5));
							}
						}
					}
					//					logger.trace("parent_tsn: " + parent_id + " Scientific name " + sName + " rank_id " + rank_id);	
					b = new Taxon(parent_id);
					b.setLsid("urn:lsid:itis.gov:itis_tsn:"+ parent_id);

					if (!rank_id.equals("0"))
						b.setRank(Utils.getInfoFromId(rank_id, "rank"));     

					if (!citation_id.equals("0"))
						b.setCitation(Utils.getInfoFromId(citation_id, "author"));

					b.setScientificName(sName);  

					if (!parent_id.equals("0"))
						b.setParent(getHierarchyFromId(parent_id, pool, con));
				}
			}
		} catch (SQLException e) {
			logger.error("SQL Error", e);
		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException", e);
		} finally {
			try {
				if (rs  != null) {
					rs.close();
				}
			} catch (SQLException ex) {
				logger.error("SQL Error", ex);
			}
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}
		}
		return b;
	}


	@Override
	public RepositoryInfo getRepositoryInfo() {
		RepositoryInfo info = new RepositoryInfo(
				"http://www.catalogueoflife.org/prototype/images/logos/itis.jpg", 
				"http://www.itis.gov/", 
				"ITIS provides access to a database with reliable information on species names and their hierarchical classification. The database is reviewed periodically to ensure high quality with valid classifications, revisions, and additions of newly described species. The ITIS database includes documented taxonomic information of flora and fauna from both aquatic and terrestrial habitats.  ITIS is a partnership of U.S., Canadian, and Mexican agencies (ITIS-North America); other organizations; and taxonomic specialists. ITIS is also a partner of Species 2000 and the Global Biodiversity Information Facility (GBIF). The ITIS and Species 2000 Catalogue of Life (CoL) partnership is proud to provide the taxonomic backbone to the Encyclopedia of Life (EOL).");
		return info;
	}









}



