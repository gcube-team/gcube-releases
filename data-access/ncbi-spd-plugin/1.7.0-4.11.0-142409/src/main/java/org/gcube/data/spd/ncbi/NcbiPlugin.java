package org.gcube.data.spd.ncbi;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.RepositoryInfo;
import org.gcube.data.spd.model.products.DataProvider;
import org.gcube.data.spd.model.products.DataSet;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.model.products.Taxon;
import org.gcube.data.spd.model.util.Capabilities;
import org.gcube.data.spd.model.util.ElementProperty;
import org.gcube.data.spd.ncbi.capabilities.ClassificationCapabilityImpl;
import org.gcube.data.spd.ncbi.capabilities.ExpansionCapabilityImpl;
import org.gcube.data.spd.ncbi.capabilities.NamesMappingImpl;
import org.gcube.data.spd.ncbi.connection.ConnectionPool;
import org.gcube.data.spd.ncbi.connection.ConnectionPoolException;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.plugin.fwk.capabilities.ClassificationCapability;
import org.gcube.data.spd.plugin.fwk.capabilities.ExpansionCapability;
import org.gcube.data.spd.plugin.fwk.capabilities.MappingCapability;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NcbiPlugin extends AbstractPlugin{

	static Logger logger = LoggerFactory.getLogger(NcbiPlugin.class);

	//POSTGRESQL CONNECTION

	public static String dbDriver = "org.postgresql.Driver";   

	public static String sqlfile = "/test.sql";

	public static String jdbc;
	public static String username;
	public static String password;
	public static String urlDump;


	//	public static Hashtable<String, String> tables = new Hashtable<String, String>();

	public final static String credits = "This information object has been generated via the Species Product Discovery service on XDATEX by interfacing with The National Center for Biotechnology Information (http://www.ncbi.nlm.nih.gov/)";
	public final static String citation = "Accessed through: The National Center for Biotechnology Information at http://www.ncbi.nlm.nih.gov/ on ";


	final static String zipFileName = "taxdump.tar.gz";
	final static String fileDump = "/ncbi_db.sql";
	final static String names[] = {"nodes", "delnodes", "merged", "citations", "names", "division", "gencode"};
	final static String tables[] = {"nodes", "delnodes", "merged", "citations", "names", "division", "gencode", "citation", "updates"};
	@Override
	public void initialize(ServiceEndpoint res) throws Exception {
		setUseCache(true);
		for (AccessPoint ap:res.profile().accessPoints()) {				
			if (ap.name().equals("ftp")) {
				urlDump = ap.address();
			}
			else if (ap.name().equals("jdbc")) {
				jdbc = ap.address();	
				username = ap.username();
				password = StringEncrypter.getEncrypter().decrypt(ap.password());	
			}
		}

		//		Boolean flag = false;
		//		for(String s : names){
		//			if (!(Utils.SQLTableExists(s)))
		//				flag = true;
		//			break;
		//		}
		//
		//		if (flag)
		//			Utils.createDB();
		//
		//		long update = Utils.lastupdate();
		//		new UpdateThread(update);

	}

	@Override
	public void update(ServiceEndpoint res) throws Exception {
		//		for (AccessPoint ap:res.profile().accessPoints()) {	
		//				if (ap.name().equals("ftp")) {
		//					urlDump = ap.address();
		//				}
		//				else if (ap.name().equals("jdbc")) {
		//					jdbc = ap.address();	
		//					username = ap.username();
		//					password = ap.password();			
		//				}
		//			}
		//			super.update(res);
	}

	@Override
	public String getDescription() {
		return ("NCBI Plugin");
	}

	@Override
	public String getRepositoryName() {
		return ("NCBI");
	}

	@SuppressWarnings("serial")
	@Override
	public Set<Capabilities> getSupportedCapabilities() {
		return new HashSet<Capabilities>(){{add(Capabilities.Classification);add(Capabilities.NamesMapping);}};
	}

	@Override
	public ClassificationCapability getClassificationInterface() {
		return new ClassificationCapabilityImpl();
	}

	@Override
	public MappingCapability getMappingInterface() {
		return new NamesMappingImpl();
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.spd.plugin.fwk.AbstractPlugin#getExpansionInterface()
	 */
	@Override
	public ExpansionCapability getExpansionInterface() {
		return new ExpansionCapabilityImpl();
	}

	@Override
	public void searchByScientificName(String word,
			ObjectWriter<ResultItem> writer, Condition... properties) {
		logger.trace("searchByScientificName for "+word+" in NCBI...");

		ResultSet rs1 = null;
		ResultSet rs2 = null;

		ConnectionPool pool = null;
		Connection con = null;

		try{
			rs1 = Utils.getListScientificNameID(word);
			if (rs1!=null){

				while(rs1.next()) {	
					String  id = rs1.getString(1);

					pool = ConnectionPool.getConnectionPool();
					con = pool.getConnection();
					Statement statement = con.createStatement();
					try {

						//				logger.trace("select b.name_txt, a.rank, a.parent_tax_id from nodes as a NATURAL JOIN names as b where b.name_class = 'scientific name' and a.tax_id =" + id );
						rs2 = statement.executeQuery("select b.name_txt, a.rank, a.parent_tax_id from nodes as a NATURAL JOIN names as b where b.name_class = 'scientific name' and a.tax_id =" + id );

						if(rs2.next()) {	

							String ScName = rs2.getString(1);
							String rank = rs2.getString(2);
							String idParent = rs2.getString(3);

							ResultItem rs = new ResultItem(id, ScName);

							rs.setCommonNames(Utils.getCommonNames(id));

							Calendar now = Calendar.getInstance();
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
							rs.setCredits(credits.replace("XDATEX", format.format(now.getTime())));
							rs.setCitation(citation.replace("XDATEX", format.format(now.getTime())));

							rs.setScientificNameAuthorship(Utils.setAuthorship(id, ScName));

							if (rank.equals("no rank"))
								rs.setRank(null);
							else					
								rs.setRank(rank);

							List<String> citation = Utils.getCitation(idParent);
							for (String c: citation){
								ElementProperty property = new ElementProperty("Comments and References", c);
								rs.addProperty(property);
							}

							DataSet dataSet = new DataSet("GenBank");
							dataSet.setName("GenBank taxonomy database");
							DataProvider dp = new DataProvider("GenBank");
							dp.setName("GenBank taxonomy database");
							dataSet.setDataProvider(dp);
							rs.setDataSet(dataSet);

							if (!(idParent.equals("1")))	
								rs.setParent(findTax(idParent));	            	      	

							if (writer.isAlive())
								writer.write(rs);
							else
								break;
						}

						logger.trace("searchByCommonName finished for "+word+" in NCBI");

					}
					catch (SQLException sqlExcept){
						logger.error("sql Error", sqlExcept);
					} finally {
						if ((pool!=null) && (con!=null)){
							pool.releaseConnection(con);
						}
						try {				
							if (rs2 != null) 
								rs2.close();
						} catch (SQLException ex) {
							logger.error("sql Error", ex);
						} catch (Exception e) {
							logger.error("General Error", e);
						}
					}
				}
			}
		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException", e);
		} catch (SQLException e) {
			logger.error("SQLException", e);
		}finally{
			try {
				if (rs1 != null)
					rs1.close();
			} catch (SQLException e) {
				logger.error("sql Error", e);
			}
		}

	}



	@Override
	public RepositoryInfo getRepositoryInfo() {
		RepositoryInfo info = new RepositoryInfo(
				"http://static.pubmed.gov/portal/portal3rc.fcgi/3417851/img/3242381", 
				"http://www.ncbi.nlm.nih.gov/", 
				"The National Center for Biotechnology Information advances science and health by providing access to biomedical and genomic information.The NCBI Taxonomy contains the names and phylogenetic lineages of more than 160,000 organisms that have molecular data in the NCBI databases. New taxa are added to the Taxonomy database as data are deposited for them.");
		return info;
	}


	//find taxonomy 
	public static Taxon findTax(String id_record) {		
		String id = null;
		try{
			id = Utils.getOriginalId(id_record);
		}
		catch (ArrayIndexOutOfBoundsException e){
			id = id_record;
			//			logger.error("id is already a original one", e);
		}
		Taxon t = null;

		ConnectionPool pool = null;
		Connection con = null;
		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();
			Statement statement = con.createStatement();
			ResultSet rs = statement.executeQuery("select a.parent_tax_id, b.name_txt, a.rank from nodes as a NATURAL JOIN names as b where a.tax_id = " + id + " and b.name_class = 'scientific name'");
			if (rs!=null){
				if(rs.next())
				{	        	
					String id_parent = rs.getString(1);
					String rank = rs.getString(3);
					t = new Taxon(id+"");
					t.setRank(rank);
					t.setScientificName(rs.getString(2));
					if  ((id_parent != "1") && !(rank.equals("kingdom")))		
						t.setParent(findTax(id_parent));	
				}
				rs.close();
			}
		}
		catch (SQLException sqlExcept) {
			logger.error("sql Error",sqlExcept);
		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException",e);
		}finally {
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}
		}   
		return t;		
	}

}
