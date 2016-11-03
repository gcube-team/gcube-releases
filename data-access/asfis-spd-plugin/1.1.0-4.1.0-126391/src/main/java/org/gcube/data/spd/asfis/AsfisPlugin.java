package org.gcube.data.spd.asfis;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.data.spd.asfis.capabilities.ClassificationCapabilityImpl;
import org.gcube.data.spd.asfis.capabilities.NamesMappingImpl;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.RepositoryInfo;
import org.gcube.data.spd.model.products.DataProvider;
import org.gcube.data.spd.model.products.DataSet;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.model.products.Taxon;
import org.gcube.data.spd.model.util.Capabilities;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.plugin.fwk.capabilities.ClassificationCapability;
import org.gcube.data.spd.plugin.fwk.capabilities.MappingCapability;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;


public class AsfisPlugin extends AbstractPlugin{

	//	public static String jdbc;
	//	public static String username ;
	//	public static String password;

	//		public static String jdbc = "jdbc:postgresql://127.0.0.1:5432/asfis";
	public static String jdbc = "jdbc:postgresql://dl26.di.uoa.gr/";
	public static String username = "gcube";
	public static String password = "d4science";

	public static final String credits = "This information object has been generated via the Species Product Discovery service on XDATEX by interfacing with ASFIS List of Species for Fishery Statistics Purposes (http://www.fao.org/fishery/collection/asfis/en)";
	public static final String citation = "Accessed through: ASFIS List of Species for Fishery Statistics Purposes at http://www.fao.org/fishery/collection/asfis/en on ";
	public static final String xslFile = "/ASFIS.xls";
	public static final String table = "asfis";
	public static final String driver = "org.postgresql.Driver";

	public static final String SPA = "Spanish";
	public static final String FRA = "French";
	public static final String ENG = "English";
	public static final String ARA = "Arabic";
	public static final String CHI = "Chinese";
	public static final String RUS = "Russian";

	public static final String STATUS = "Validated";


	GCUBELog logger = new GCUBELog(AsfisPlugin.class);

	@Override
	public void initialize(ServiceEndpoint res) throws Exception {
		for (AccessPoint ap:res.profile().accessPoints()) {

			if (ap.name().equals("jdbc")) {
				jdbc = ap.address();	
				username = ap.username();	
				password = StringEncrypter.getEncrypter().decrypt(ap.password());
			}
		}
	}


	@Override
	public void update(ServiceEndpoint res) throws Exception {
		for (AccessPoint ap:res.profile().accessPoints()) {

			if (ap.name().equals("jdbc")) {
				jdbc = ap.address();	
				username = ap.username();
				password = ap.password();			
			}
		}
		super.update(res);
	}

	@Override
	public String getDescription() {
		return ("ASFIS Plugin");
	}

	@Override
	public String getRepositoryName() {
		return ("ASFIS");
	}

	@Override
	public void shutdown() throws Exception {
		//		Discover.shutdown();
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

	@Override
	public void searchByScientificName(String word,
			ObjectWriter<ResultItem> writer, Condition... properties) {
		logger.info("searching by ScientificName " + word);
		ResultSet results = null;
		try{
			results = Utils.createResultSetByName(word);
			if (results!=null){

				while(results.next()) {	

					//id, Scientific_name, Author, Family, Order_rank 
					String id = results.getString(1);
					String scientific_name = results.getString(2);
					String author = results.getString(3);
					//common names
					String englishName = results.getString(4);
					String frenchName = results.getString(5);
					String spanishName = results.getString(6);

					String arabic_name = results.getString(12);
					String chinese_name = results.getString(13);
					String russian_name = results.getString(14);

					//parent_id, TAXOCODE, ISSCAAP, threeA_CODE, rank
					String parent_id = results.getString(7);
					String taxocode = results.getString(8);
					String isscaap = results.getString(9);
					String threeA_CODE = results.getString(10);
					String rank = results.getString(11);

					logger.info("found ResultSet for " + scientific_name);

					ResultItem rs = null;
					try{
						rs = createResultItem(id, scientific_name, author, englishName, frenchName, spanishName, parent_id, taxocode, isscaap, threeA_CODE, rank, arabic_name, chinese_name, russian_name);
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





	//	TAXOCODE, Scientific_name, Author, Family, Order_rank 
	private ResultItem createResultItem(String id, String ScName, String author, String englishName, String frenchName, String spanishName, String parent_id, String taxocode, String isscaap, String threeA_CODE, String rank, String arabic_name, String chinese_name, String russian_name) {

		//		System.out.println("rank " + rank + " : " + ScName + " parent: " + parent_id );
		ResultItem item = new ResultItem(id, ScName);
		item.setRank(rank);
		item.setScientificNameAuthorship(author);

		//set common names
		if ((englishName!=null) | (frenchName!=null) | (spanishName!=null) | (arabic_name!=null) |(chinese_name!=null) | (russian_name!=null))
			item.setCommonNames(Utils.setCommonNames(englishName, frenchName, spanishName, arabic_name, chinese_name, russian_name));

		//set credit and citation
		item.setCitation(Utils.createCitation());
		item.setCredits(Utils.createCredits());

		DataSet dataSet = new DataSet("ASFISid");
		dataSet.setName("ASFIS");
		DataProvider dp = new DataProvider("ASFISid");
		dp.setName("ASFIS");
		dataSet.setDataProvider(dp);
		item.setDataSet(dataSet);

		item.setParent(createTaxonByID(parent_id));	

		return item;	  
	}



	private Taxon createTaxonByID(String id) {	

		logger.info("createTaxonByID " + id);
		Taxon taxon = null;
		ResultSet results = null;
		try{
			results = Utils.createResultSetByID(id);

			if (results!=null){
				if (results.next()){

					String parent_id = results.getString(2);

					if (!id.equals(parent_id)){						
						try{
							String scientific_name = results.getString(1);
							logger.info("found ResultSet for " + scientific_name);
							String rank = results.getString(3);

							taxon = new Taxon(id, scientific_name);							
							taxon.setRank(rank);										
							taxon.setParent(createTaxonByID(parent_id));
							taxon.setCitation(Utils.createCitation());
							taxon.setCredits(Utils.createCredits());

							return taxon;
						}catch (Exception e) {
							logger.error("Error retrieving information ", e);
						}
					}
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
		return taxon;

	}


	@Override
	public RepositoryInfo getRepositoryInfo() {
		RepositoryInfo info = new RepositoryInfo(
				"http://termportal.fao.org/faoas/as/pages/img/ftTermTitle_en.gif", 
				"http://www.fao.org/fishery/collection/asfis/en/",
				"The FAO Fisheries and Aquaculture Statistics and Information Service (FIPS) collates world capture and aquaculture production statistics at either the species, genus, family or higher taxonomic levels in 2 119 statistical categories (2011 data) referred to as species items.");
		return info;
	}


}



