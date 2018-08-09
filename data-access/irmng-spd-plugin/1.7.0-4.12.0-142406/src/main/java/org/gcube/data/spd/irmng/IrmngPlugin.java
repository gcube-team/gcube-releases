package org.gcube.data.spd.irmng;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.data.spd.irmng.capabilities.ClassificationCapabilityImpl;
import org.gcube.data.spd.irmng.capabilities.ExpansionCapabilityImpl;
import org.gcube.data.spd.irmng.dbconnection.ConnectionPool;
import org.gcube.data.spd.irmng.dbconnection.ConnectionPoolException;
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
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IrmngPlugin extends AbstractPlugin {

	static Logger logger = LoggerFactory.getLogger(IrmngPlugin.class);

	//Connection
	
	public static String jdbc;
	public static String user;
	public static String password;
	public static String dumpUrl;
	
	
//	public static String jdbc = "jdbc:postgresql://127.0.0.1:5432/irmng_db";
//	public static String user = "postgres";
//	public static String password = "";
//  public static String dumpUrl = "http://www.cmar.csiro.au/datacentre/downloads/IRMNG_DWC.zip";
	
	final static String citation = "Accessed through: The Interim Register of Marine and Nonmarine Genera at http://www.obis.org.au/irmng/ on ";
	final static String credits = "This information object has been generated via the Species Product Discovery service on XDATEX by interfacing with the Interim Register of Marine and Nonmarine Genera (IRMNG) (http://www.obis.org.au/irmng/)";

	public final static String driver = "org.postgresql.Driver";
	public static final String dumpDb = "/createIrmngDb.sql";

	@Override
	public void initialize(ServiceEndpoint res) throws Exception {
		for (AccessPoint ap:res.profile().accessPoints()) {
			if (ap.name().equals("dump")) {
				dumpUrl = ap.address();
			}
			else if (ap.name().equals("jdbc")) {
				jdbc = ap.address();
				user = ap.username();
				password = StringEncrypter.getEncrypter().decrypt(ap.password());	
			}
		}
//		if (!(Utils.SQLTableExists("taxon")) || !(Utils.SQLTableExists("speciesprofile")) || !(Utils.SQLTableExists("updates")))
//			try {
//				logger.trace("create db");
//				Utils.createDB();
//			} catch (SQLException e) {
//				logger.error("sql Error", e);
//			} catch (IOException e) {
//				logger.error("IOException", e);
//			}
//
//		long update = UpdateThread.lastupdate();
//		new UpdateThread(update);	

		super.initialize(res);
	}


	@Override
	public void update(ServiceEndpoint res) throws Exception {
		for (AccessPoint ap:res.profile().accessPoints()) {
			if (ap.name().equals("dump")) {
				dumpUrl = ap.address();
			}
			else if (ap.name().equals("jdbc")) {
				jdbc = ap.address();
				user = ap.username();
				password = ap.password();			
			}
		}
		super.update(res);
	}


	@Override
	public String getRepositoryName() {		
		return "IRMNG";
	}

	@Override
	public String getDescription() {
		return "Plugin for IRMNG";
	}

	@SuppressWarnings("serial")
	@Override
	public Set<Capabilities> getSupportedCapabilities() {
		return new HashSet<Capabilities>(){{add(Capabilities.Classification);add(Capabilities.Expansion);}};
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
	public void searchByScientificName(String word,
			ObjectWriter<ResultItem> writer, Condition... properties) {

		logger.trace("searchByScientificName " + word);
		ResultSet rs = null;
		try{

			rs = getRSByScName(word);
			if (rs!=null){
				while(rs.next()) {	

					String id = rs.getString(1);
					String rank = rs.getString(2);
					String idParent = rs.getString(3);
					String author = rs.getString(4);
					String citation = rs.getString(5);
					String name = rs.getString(6);
//					logger.trace(id + " " + rank + " "+ idParent + " "+ author + " "+citation);					
					ResultItem item = createResultItem(id, rank, author, citation, idParent, name);
					if ((item!=null) && (writer.isAlive()))
						writer.write(item);
				}
			}
		}catch (SQLException sqlExcept) {        	
			logger.error("sql Error", sqlExcept);
		}finally{	
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException ex) {
				logger.error("sql Error", ex);
			}
		}
	}


	private ResultSet getRSByScName(String scientificName) {

		ConnectionPool pool = null;
		Connection con = null;
		ResultSet results = null;

		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();

			String name = "%" + scientificName + "%";
			String query = "select taxonid, taxonrank, parentnameusageid, scientificnameauthorship, nameaccordingto, name from taxon where UPPER(name) like UPPER(?)";	
			results =  pool.selectPrestatement(query, name);
		}
		catch (Throwable e) {
			logger.error("general Error", e);
		}finally{
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}			
		}
		return results;
	}


	private ResultItem createResultItem(String id, String rank, String author, String citation, String idParent, String name) {
		ResultItem item = null;
		try{

			item = new ResultItem(id, name);

			item.setCredits(Utils.createCredits());

			StringBuilder cit = new StringBuilder();
			if (citation != null){
				cit.append(citation);
				cit.append(". ");
			}
			cit.append(Utils.createCitation());
			item.setCitation(cit.toString());

			item.setScientificNameAuthorship(author);
			item.setRank(rank);

			DataSet dataSet = new DataSet(citation);
			dataSet.setName(citation);
			dataSet.setCitation(citation);

			DataProvider dp = new DataProvider("irmngid");
			dp.setName("IRMNG");
			dataSet.setDataProvider(dp);
			item.setDataSet(dataSet);
			//				logger.trace("idParent " + idParent);

			if ((rank != null) && (idParent!= null)) {
				
				ResultSet parentRS = findTaxonById(idParent);

				if (parentRS.next()) {
					String idP = parentRS.getString(1);
					String rankP = parentRS.getString(2);
					String authorP = parentRS.getString(3);
					String citationP = parentRS.getString(4);
					String nameP = parentRS.getString(5);
					
					Taxon parent = createResultItem(idParent, rankP, authorP, citationP, idP, nameP);
					if (parent!=null)
						item.setParent(parent);
				}
			}
		} catch (SQLException e) {
			logger.error("ResultSet empty" , e);
		}finally{
		
		}
		return item;
	}


	private ResultSet findTaxonById(String idParent) {
	
//		logger.trace("idParent " + idParent);
		ConnectionPool pool = null;
		Connection con = null;
		ResultSet results = null;

		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();

			String query = "select parentnameusageid, scientificname, taxonrank, scientificnameauthorship, name from taxon where taxonid = ?";	
			results =  pool.selectPrestatement(query, idParent);
		}
		catch (SQLException sqlExcept) {
			logger.error("sql Error",sqlExcept);
		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException Error",e);
		} finally{			
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}	
		}

		return results;
	}

	@Override
	public RepositoryInfo getRepositoryInfo() {
		RepositoryInfo info = new RepositoryInfo(
				"http://www.cmar.csiro.au/datacentre/irmng/irmng-small.gif", 
				"http://www.cmar.csiro.au/datacentre/irmng/",
				"The Interim Register of Marine and Nonmarine Genera (IRMNG) project, sponsored initially by OBIS Australia as a contribution to the international OBIS system, attempts to bring the content of extensive published genus-level compilations for botanical and zoological genus names, as well as smaller ones for prokaryotes and viruses, at least to certain cut-off points in time, into a common framework and fill residual gaps in genus-level coverage as apparent. Since its inception in 2006, the IRMNG data compilation has grown to include over 465,000 genus names as at mid 2012, covering all types of biota, both extant and fossil, out of an estimated 480,000-500,000 (+?) genus names ever published, the exact number being unclear because such a compilation has not been attempted previously. In addition, some 1.6 million species names have been included in the system as readily available, linked to their correct parent genus name instance so far as possible, to further assist the discrimination of marine from nonmarine taxa and to supply confirmation of correct spellings for species names as available.");
		return info;
	}



}
