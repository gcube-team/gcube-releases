package org.gcube.data.spd.flora;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.data.spd.flora.capabilities.ClassificationCapabilityImpl;
import org.gcube.data.spd.flora.capabilities.ExpansionCapabilityImpl;
import org.gcube.data.spd.flora.dbconnection.ConnectionPool;
import org.gcube.data.spd.flora.dbconnection.ConnectionPoolException;
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


public class FloraPlugin extends AbstractPlugin{


	public static String jdbc;
	public static String username ;
	public static String password;
	public static String baseurl;


	public static final String credits = "This information object has been generated via the Species Product Discovery service on XDATEX by interfacing with Lista de Espécies da Flora do Brasil (http://floradobrasil.jbrj.gov.br/)";
	public static final String citation = "Accessed through: Lista de Espécies da Flora do Brasil - Jardim Botânico do Rio de Janeiro at http://floradobrasil.jbrj.gov.br/2012/ on ";
	public static final String dumpDb = "/createFloraDb.sql";
	public static final String tableName = "flora";
	public static final String driver = "org.postgresql.Driver";

	static Logger logger = LoggerFactory.getLogger(FloraPlugin.class);

	@Override
	public void initialize(ServiceEndpoint res) throws Exception {
		for (AccessPoint ap:res.profile().accessPoints()) {
			if (ap.name().equals("rest")) {
				baseurl = ap.address();
			}
			else if (ap.name().equals("jdbc")) {
				jdbc = ap.address();	
				username = ap.username();	
				password = StringEncrypter.getEncrypter().decrypt(ap.password());
			}
		}
		//		if ((!Utils.SQLTableExists(FloraPlugin.tableName) || (!Utils.SQLTableExists("updates")))){
		//			System.out.println("Create tables");
		//			if (Utils.createDB())
		//				logger.trace("Completed.");			
		//		}
		//
		//		long update = Utils.lastupdate();
		//		new UpdateThread(update);
	}


	@Override
	public void update(ServiceEndpoint res) throws Exception {
		for (AccessPoint ap:res.profile().accessPoints()) {
			if (ap.name().equals("rest")) {
				baseurl = ap.address();
			}
			else if (ap.name().equals("jdbc")) {
				jdbc = ap.address();	
				username = ap.username();
				password = ap.password();			
			}
		}
		super.update(res);
	}

	@Override
	public String getDescription() {
		return ("Brazilian Flora Plugin");
	}

	@Override
	public String getRepositoryName() {
		return ("BrazilianFlora");
	}

	@Override
	public void shutdown() throws Exception {
		//		Discover.shutdown();
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

		ResultSet results = null;
		try{
			results = createRSNames(word);
			if (results!=null){

				while(results.next()) {	

					String id = results.getString(1);
					String scientific_name = results.getString(2);
					String rank = results.getString(3);
					String idParent = results.getString(4);
					String author = results.getString(5);

					ResultItem rs = null;
					try{

						rs = createResultItem(id, scientific_name, rank, idParent, author);
					}catch (Exception e) {
						logger.error("Error retrieving information ", e);
					}

					if (writer.isAlive()){
						if (rs!=null)
							writer.write(rs);
					}
					else
						break;
				}
			}
		} catch (SQLException e) {
			logger.error("sql Error", e);
		}finally
		{
			try {
				if (results != null)
					results.close();
			} catch (SQLException ex) {
				logger.error("sql Error", ex);
			}
		}
	}



	/**
	 * get ResultSet by scientificName
	 */
	private ResultSet createRSNames(String scientificName) {
		ResultSet results = null;	

		ConnectionPool pool = null;

		try {
			pool = ConnectionPool.getConnectionPool();
			String term = "%" + scientificName + "%";

			String query = "select id, scientific_name, rank, id_parent, citation from "+ tableName + " where UPPER(scientific_name) like UPPER(?)";
			results =  pool.selectPrestatement(query, term);
		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException",e);
		}

		return results;

	}


	private ResultItem createResultItem(String id, String ScName, String rank, String idParent, String author) {

		ResultItem item = new ResultItem(id, ScName);

		item.setCitation(Utils.createCitation());
		item.setCredits(Utils.createCredits());

		item.setRank(rank);
		item.setScientificNameAuthorship(author);
		//	    		rs.setProvider("Brazilian Flora");

		DataSet dataSet = new DataSet("floraid");
		dataSet.setName("Brazilian Flora");
		DataProvider dp = new DataProvider("floraid");
		dp.setName("Brazilian Flora");
		dataSet.setDataProvider(dp);
		item.setDataSet(dataSet);

		if ((rank != null) && (idParent!= null)) {
			item.setParent(findTaxonById(idParent));
			//			if (parentRS!=null){
			//				Taxon parent = createTaxonByRSItem(parentRS);
			//				item.setParent(parent);
			//			}
		}

		return item;	  
	}


	//	public Taxon createTaxonByRSItem(RSItem rs){
	//		Taxon parent = null;
	//		RSItem parentRs = null;
	//		Taxon t = new Taxon(rs.getId());
	//		t.setRank(rs.getRank());
	//		t.setScientificName(rs.getScientific_name());
	//		t.setAuthor(rs.getAuthor());     
	//		if  (rs.getParentId() != null)    {    	
	//			parentRs = findTaxonById(rs.getParentId());
	//			parent = createTaxonByRSItem(parentRs);
	//		}
	//
	//		t.setParent(parent);
	//		return t;
	//	}


	/**
	 * Find taxonomy 
	 */
	public Taxon findTaxonById(String id) {		

		ResultSet results = null;
		Taxon item = null;
		ConnectionPool pool = null;
		try {
			pool = ConnectionPool.getConnectionPool();	

			String query = "select id_parent, rank, scientific_name, citation from "+ FloraPlugin.tableName + " where id = ?";
			results =  pool.selectPrestatement(query, id);

			if(results.next()) {	  
				item = new Taxon(id);
				if (results.getString(1) != null)
					item.setParent(findTaxonById(results.getString(1)));
				item.setRank(results.getString(2));
				item.setScientificName(results.getString(3));
				item.setScientificNameAuthorship(results.getString(4));
			}
		}
		catch (SQLException sqlExcept) {
			logger.error("sql Error",sqlExcept);
		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException",e);
		} finally{
			try {
				results.close();
			} catch (SQLException e) {
				logger.error("sql Error",e);
			}
		}
		//        logger.trace("returning taxon");
		return item;		
	}

	@Override
	public RepositoryInfo getRepositoryInfo() {
		RepositoryInfo info = new RepositoryInfo(
				"http://floradobrasil.jbrj.gov.br/2012/imgs/logo_fdb_2012.gif", 
				"http://floradobrasil.jbrj.gov.br/",
				"The List of Species of Brazilian Flora is a project coordinated by the Botanical Garden of Rio de Janeiro in partnership with the CRIA. CRIA is responsible for the development and maintenance of the information system. The whole process of validation and inclusion of new data is done online by a network of over 400 taxonomists from Brazil and abroad. The online public version was completed in May 2010."				);
		return info;
	}


}



