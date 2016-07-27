/**
 * 
 */
package org.gcube.data.spd.obisplugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.data.spd.model.BasisOfRecord;
import org.gcube.data.spd.model.CommonName;
import org.gcube.data.spd.model.exceptions.IdNotValidException;
import org.gcube.data.spd.model.products.DataProvider;
import org.gcube.data.spd.model.products.DataSet;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.model.products.Product;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.model.products.Taxon;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.model.products.TaxonomyStatus;
import org.gcube.data.spd.model.products.Product.ProductType;
import org.gcube.data.spd.model.products.TaxonomyStatus.Status;
import org.gcube.data.spd.model.util.ElementProperty;
import org.gcube.data.spd.obisplugin.data.ProductKey;
import org.gcube.data.spd.obisplugin.data.SearchFilters;
import org.gcube.data.spd.obisplugin.util.Cache;
import org.gcube.data.spd.obisplugin.util.DateUtil;
import org.gcube.data.spd.obisplugin.util.Util;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class ObisClient {

	protected static GCUBELog logger = new GCUBELog(ObisClient.class);
	//"2009-12-11 11:30:00-07"
	protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("y-M-d");
	protected static final DateUtil DATE_UTIL = DateUtil.getInstance();
	protected static final SimpleDateFormat sdf = new SimpleDateFormat();

	public static final Cache<Integer, TaxonomyItem> taxonomyItemCache = new Cache<Integer, TaxonomyItem>(1000); 

	public static int getOccurrencesCount(PluginSession session, int taxonId, int datasetId, SearchFilters filters) throws SQLException
	{
		PreparedStatement statement = session.getOccurrencesCountPreparedStatement(taxonId, datasetId, filters);

		ResultSet rs = statement.executeQuery();

		if (!rs.next()) return 0;
		int occurrences = rs.getInt("occurrences");
		rs.close();

		return occurrences;
	}

	public static void getOccurrences(PluginSession session, String key, Writer<OccurrencePoint> writer) throws Exception
	{

		ProductKey productKey = ProductKey.deserialize(key);

		PreparedStatement statement = session.getOccurrencesPreparedStatement(productKey.getTaxonId(), productKey.getDataSetId(), productKey.getFilters());
		ResultSet rs = statement.executeQuery();

		String credits = generateCredits();
		//String citation = generateCitation();

		DataSet dataset =  getDataSetById(session, productKey.getDataSetId());
		
		boolean continueWrite = true;
		while(rs.next() && continueWrite) {			
			OccurrencePoint occurrence = generateOccurrencePoint(rs, credits, dataset);
			continueWrite = writer.write(occurrence);

		};

		rs.close();
	}

	private static DataSet getDataSetById(PluginSession session, int datasetId){
		logger.debug("retrieving dataset for id "+datasetId);
		DataSet dataset = null;
		ResultSet datasetRs = null;
		try{
			PreparedStatement datasetStatement = session.getDatasetPerDatasetIdPreparedStatement(datasetId);
			datasetRs = datasetStatement.executeQuery();
			if (!datasetRs.next()) return null;
			dataset = retrieveDatasetInformation(datasetRs);
		}catch(Exception e){
			logger.warn("cannot retrieve dataset for occurrence point with id "+datasetId,e);
		}finally{
			try{
				if (datasetRs!=null)
					datasetRs.close();
			}catch(Exception e) {}
		}
		logger.debug("dataset returned  "+dataset);
		return dataset;
	}
	
	protected static OccurrencePoint generateOccurrencePoint(ResultSet rs, String credits, DataSet dataSet) throws SQLException
	{
		int id = rs.getInt("id");
		OccurrencePoint occurrence = new OccurrencePoint(String.valueOf(id));

		//drs.latitude, drs.longitude, drs.datecollected, drs.basisofrecord, 
		occurrence.setDecimalLatitude(rs.getDouble("latitude"));
		occurrence.setDecimalLongitude(rs.getDouble("longitude"));

		Timestamp dateValue = rs.getTimestamp("datecollected");
		if (dateValue!=null) {
			Calendar dateCollected = Calendar.getInstance();
			dateCollected.setTimeInMillis(dateValue.getTime());
			occurrence.setEventDate(dateCollected);
		} else {
			//dxs.yearcollected, dxs.monthcollected, dxs.daycollected
			try {
				Calendar dateCollected = Calendar.getInstance();
				int year = Integer.parseInt(rs.getString("yearcollected"));
				int month = Integer.parseInt(rs.getString("monthcollected"));
				int date = Integer.parseInt(rs.getString("daycollected"));
				dateCollected.set(year, month, date);
				occurrence.setEventDate(dateCollected);
			} catch(NumberFormatException nfe){}
		}

		String basisOfRecord = rs.getString("basisofrecord");
		occurrence.setBasisOfRecord(getBasisOfRecord(basisOfRecord));

		//dxs.citation, dxs.institutioncode, dxs.collectioncode, dxs.catalognumber, dxs.collector
		occurrence.setCitation(rs.getString("citation"));
		occurrence.setCredits(credits);
		occurrence.setInstitutionCode(rs.getString("institutioncode"));
		occurrence.setCollectionCode(rs.getString("collectioncode"));
		occurrence.setCatalogueNumber(rs.getString("catalognumber"));
		occurrence.setRecordedBy(rs.getString("collector"));

		//, dxs.datelastmodified, 

		String datelastmodified = rs.getString("datelastmodified");
		if (datelastmodified!=null) {

			try {
				java.util.Date date = DATE_UTIL.parse(datelastmodified);
				Calendar lastmodified = Calendar.getInstance();
				lastmodified.setTimeInMillis(date.getTime());
				occurrence.setModified(lastmodified);
			} catch (Exception e) {
				//logger.warn("Unknow date format "+datelastmodified);
			}

		}

		//dxs.country, dxs.locality, dxs.minimumdepth, dxs.maximumdepth, dxs.coordinateprecision, dxs.concatenated
		occurrence.setCountry(rs.getString("country"));
		occurrence.setLocality(rs.getString("locality"));
		occurrence.setMinDepth(rs.getDouble("minimumdepth"));
		occurrence.setMaxDepth(rs.getDouble("maximumdepth"));
		occurrence.setCoordinateUncertaintyInMeters(rs.getString("coordinateprecision"));
		//"Animalia|Chordata|Chondrichthyes|Lamniformes|Lamnidae|Carcharodon||carcharias||Carcharodon carcharias|Linnaeus"

		/*
		 * Kingdom:	Animalia
			Phylum:	Chordata
			Class:	Chondrichthyes
			Subclass:	Elasmobranchii
			Order:	Lamniformes
			Family:	Lamnidae
			Genus:	Carcharodon
			A. Smith, 1838
			Species:	C. carcharias
		 */

		String concatenated = rs.getString("concatenated");
		if (concatenated!=null) {
			int authorStartIndex = concatenated.lastIndexOf('|');
			if (authorStartIndex>0) {
				String snPart = concatenated.substring(0, authorStartIndex);
				int scientificNameStartIndex = snPart.lastIndexOf('|');
				if (scientificNameStartIndex>0) {
					String author = (authorStartIndex+1<concatenated.length())?concatenated.substring(authorStartIndex+1):"";
					String sn = (scientificNameStartIndex+1<snPart.length())?snPart.substring(scientificNameStartIndex+1):"";
					occurrence.setScientificName(sn/*+" ("+author+")"*/);
				}
			}
			/*String[] taxon = concatenated.split("|");
			if (taxon.length>0) occurrence.setKingdom(taxon[0]);
			if (taxon.length>4) occurrence.setFamily(taxon[4]);
			if (taxon.length>11) {
				String scientific = taxon[9] +"("+taxon[10]+")";
				occurrence.setScientificName(scientific);
			}*/
		}

		occurrence.setScientificNameAuthorship(rs.getString("snAuthor"));
		
		occurrence.setIdentifiedBy(rs.getString("identifiedBy"));
		
		occurrence.setDataSet(dataSet);
		
		//retrieving dataset
		return occurrence;
	}

	public static OccurrencePoint getOccurrenceById(PluginSession session, String id) throws Exception
	{
		PreparedStatement statement = session.getOccurrenceByIdPreparedStatement(Integer.parseInt(id));
		ResultSet rs = statement.executeQuery();

		OccurrencePoint occurrence = null;
		String credits = generateCredits();
		if(rs.next()){
			int objId = rs.getInt("valid_id");
			occurrence = generateOccurrencePoint(rs, credits, getDataSetById(session, objId));
		}
		rs.close();

		return occurrence;
	}


	public static BasisOfRecord getBasisOfRecord(String basis)
	{
		if (basis==null) return BasisOfRecord.Unknown;
		if (basis.equalsIgnoreCase("HumanObservation") || basis.equalsIgnoreCase("O")) return BasisOfRecord.HumanObservation;
		if (basis.equalsIgnoreCase("L")) return BasisOfRecord.LivingSpecimen;
		if (basis.equalsIgnoreCase("P")) return BasisOfRecord.Literature;
		if (basis.equalsIgnoreCase("PreservedSpecimen") || basis.equalsIgnoreCase("S")) return BasisOfRecord.PreservedSpecimen;
		return BasisOfRecord.Unknown;

	}

	public static void searchByCommonName(PluginSession session, String searchTerm, SearchFilters filters, Writer<ResultItem> writer) throws Exception
	{

		PreparedStatement statement = session.getSearchCommonNamePreparedStatement(searchTerm);

		ResultSet rs = statement.executeQuery();

		generateResultItems(session, rs, filters, writer);
	}

	protected static void fillProducts(PluginSession session, int speciesId, int datasetId, String key, SearchFilters filters, ResultItem item) throws SQLException
	{

		List<Product> products = new LinkedList<Product>();

		//OCCURRENCES
		Product occurences = new Product(ProductType.Occurrence, key);
						
		int occurencesCount = getOccurrencesCount(session, speciesId, datasetId, filters);
		occurences.setCount(occurencesCount);
		products.add(occurences);

		logger.trace("product is "+occurences);
		
		item.setProducts(products);
	}

	public static void searchByScientificName(PluginSession session, String searchTerm, SearchFilters filters, Writer<ResultItem> writer) throws Exception
	{

		PreparedStatement statement = session.getSearchScientificNamePreparedStatement(searchTerm);

		ResultSet rs = statement.executeQuery();

		generateResultItems(session, rs, filters, writer);
	}

	protected static void generateResultItems(PluginSession session, ResultSet rs, SearchFilters filters, Writer<ResultItem> writer) throws Exception
	{
		//System.out.println("generating records");
		boolean continueWrite = true;
		String credits = generateCredits();
		String citation = generateCitation();
		while(rs.next() && continueWrite) {

			int id = rs.getInt("id");
			//System.out.println("id "+id);

			ResultItem baseItem = new ResultItem(String.valueOf(id), "");

			fillTaxon(session, id, baseItem, credits, citation);

			fillCommonNames(session, id, baseItem);
			
			
			PreparedStatement datasetStatement = session.getDatasetPreparedStatement(id);
			ResultSet dataSetrs = datasetStatement.executeQuery();

			while(dataSetrs.next()) {

				ResultItem item = Util.cloneResultItem(baseItem);

				DataSet dataset = retrieveDatasetInformation(dataSetrs);

				item.setDataSet(dataset);
				
				int dataSetId = Integer.parseInt(dataset.getId());
				
				ProductKey key = new ProductKey(id, dataSetId, filters);
				
				logger.trace("datasetid is "+dataSetId+" and product key created is "+key);
				
				fillProducts(session, id, dataSetId, key.serialize(), filters, item);

				continueWrite = writer.write(item);
			}
		}
		rs.close();
	}

	/**
	 * Fills the node with the taxon information. Also information about parent are retrieved.
	 * @param connection the db connection.
	 * @param id the taxon id.
	 * @param taxonNode the node to fill.
	 * @throws SQLException
	 */
	protected static void fillTaxon(PluginSession session, int id, Taxon taxon, String credits, String citation) throws SQLException
	{

		PreparedStatement statement = session.getTaxonPreparedStatement(id);

		ResultSet rs = statement.executeQuery();

		if (rs.next()) {
			taxon.setCitation(citation);
			taxon.setCredits(credits);

			//taxon informations
			taxon.setScientificName(rs.getString("tname"));
			taxon.setScientificNameAuthorship(rs.getString("tauthor"));

			String rank = rs.getString("rank_name");
			taxon.setRank((rank!=null)?rank:"");

			int parentId = rs.getInt("parent_id");
			rs.close();

			//check for parent
			if (parentId!=id) {

				//create and fill the parent
				Taxon parent = new Taxon(String.valueOf(parentId));
				fillTaxon(session, parentId, parent, credits, citation);
				taxon.setParent(parent);
			}
		}
	}

	protected static void fillTaxonomyItem(PluginSession session, int id, TaxonomyItem item, String credits, String citation) throws Exception
	{
		PreparedStatement statement = session.getTaxonPreparedStatement(id);

		ResultSet rs = statement.executeQuery();

		if (rs.next()) {

			//taxon informations
			item.setScientificName(rs.getString("tname"));

			String author = Util.stripNotValidXMLCharacters(rs.getString("tauthor"));
			item.setScientificNameAuthorship(author);

			//properties
			item.addProperty(new ElementProperty("worms_id", rs.getString("worms_id")));
			item.addProperty(new ElementProperty("col_id", rs.getString("col_id")));
			item.addProperty(new ElementProperty("irmng_id", rs.getString("irmng_id")));
			item.addProperty(new ElementProperty("itis_id", rs.getString("itis_id")));

			item.setCredits(credits);
			item.setCitation(citation);

			String rank = rs.getString("rank_name");
			item.setRank((rank!=null)?rank:"");

			item.setStatus(new TaxonomyStatus("", Status.ACCEPTED));

			boolean parentNull = rs.getObject("parent_id")==null;
			int parentId = rs.getInt("parent_id");
			rs.close();

			//fill common names
			fillCommonNames(session, id, item);

			//check for parent
			if (!parentNull && parentId!=id) {

				//create and fill the parent
				TaxonomyItem parent = taxonomyItemCache.get(parentId);
				if (parent == null) {
					parent = new TaxonomyItem(String.valueOf(parentId));
					fillTaxonomyItem(session, parentId, parent, credits, citation);
				}
				item.setParent(parent);
			}
		} else throw new IdNotValidException("Taxon with id "+id+" not found");
	}

	protected static void fillCommonNames(PluginSession session, int taxonNameId, TaxonomyItem item) throws SQLException
	{
		PreparedStatement statement = session.getTaxonCommonNamePreparedStatement(taxonNameId);
		ResultSet rs = statement.executeQuery();

		List<CommonName> commonNames = new ArrayList<CommonName>();
		while(rs.next()) commonNames.add(new CommonName(rs.getString("lanname"), rs.getString("cname")));
		rs.close();
		item.setCommonNames(commonNames);

	}

	protected static void fillCommonNames(PluginSession session, int taxonNameId, ResultItem item) throws SQLException
	{
		PreparedStatement statement = session.getTaxonCommonNamePreparedStatement(taxonNameId);
		ResultSet rs = statement.executeQuery();
		List<CommonName> commonNames = new ArrayList<CommonName>();
		while(rs.next()) commonNames.add(new CommonName(rs.getString("lanname"), rs.getString("cname")));
		rs.close();
		item.setCommonNames(commonNames);
	}

	private static DataSet retrieveDatasetInformation(ResultSet rs) throws SQLException{
		int dataSetId = rs.getInt("datasetId");
		DataSet dataSet = new DataSet(String.valueOf(dataSetId));
		dataSet.setCitation(rs.getString("datasetCitation"));
		dataSet.setName(rs.getString("datasetName"));

		DataProvider dataProvider = new DataProvider(String.valueOf(rs.getInt("providerId")));
		dataProvider.setName(rs.getString("providerName"));
		dataSet.setDataProvider(dataProvider);
		return dataSet;
	}

	public static Set<String> getCommonNames(PluginSession session, String scientificName) throws SQLException
	{
		PreparedStatement statement = session.getCommonNameFromScientificNamePreparedStatement(scientificName);
		ResultSet rs = statement.executeQuery();

		Set<String> commonNames = new HashSet<String>();

		while(rs.next()) commonNames.add(rs.getString("cname"));

		rs.close();

		return commonNames;
	}

	public static void getScientificNames(PluginSession session, String commonName, Writer<String> writer) throws SQLException
	{
		PreparedStatement statement = session.getScientificNameFromCommonNamePreparedStatement(commonName);
		ResultSet rs = statement.executeQuery();
		while (rs.next() && writer.write(rs.getString("tname")));
		rs.close();
	}

	public static void getTaxonByScientificNames(PluginSession session, String scientificName, Writer<TaxonomyItem> writer) throws Exception
	{
		PreparedStatement statement = session.getScientificNamePreparedStatement(scientificName);
		ResultSet rs = statement.executeQuery();
		generateTaxonomyItems(session, rs, writer);
	}

	public static void getTaxonByCommonName(PluginSession session, String commonName, Writer<TaxonomyItem> writer) throws Exception
	{
		PreparedStatement statement = session.getCommonNamePreparedStatement(commonName);
		ResultSet rs = statement.executeQuery();
		generateTaxonomyItems(session, rs, writer);
	}

	protected static void generateTaxonomyItems(PluginSession session, ResultSet rs, Writer<TaxonomyItem> writer) throws SQLException, Exception
	{
		boolean continueWrite = true;
		String credits = generateCredits();
		String citation = generateCitation();
		while(rs.next() && continueWrite) {

			Integer id = rs.getInt("id");

			TaxonomyItem taxon = taxonomyItemCache.get(id);
			if (taxon == null) {
				taxon = new TaxonomyItem(String.valueOf(id));
				fillTaxonomyItem(session, id, taxon, credits, citation);
				taxonomyItemCache.put(id, taxon);
			}

			//TaxonomyItem taxon = new TaxonomyItem(String.valueOf(id));
			//fillTaxonomyItem(session, id, taxon);
			continueWrite = writer.write(taxon);
		} 

		rs.close();
	}

	protected static List<TaxonomyItem> getChildrenTaxon(PluginSession session, int id) throws SQLException
	{
		PreparedStatement statement = session.getChildrenTaxonPreparedStatement(id);

		ResultSet rs = statement.executeQuery();

		List<TaxonomyItem> children = new ArrayList<TaxonomyItem>();
		while (rs.next()) {
			//taxon informations
			int taxonId = rs.getInt("id");

			//FIXME tmp workaround
			if (taxonId == id) continue;

			TaxonomyItem child = new TaxonomyItem(String.valueOf(taxonId));
			child.setScientificName(rs.getString("tname"));
			child.setCitation(rs.getString("tauthor"));
			String rank = rs.getString("rank_name");
			child.setRank((rank!=null)?rank:"");

			child.setStatus(new TaxonomyStatus("",Status.ACCEPTED));

			fillCommonNames(session, id, child);

			children.add(child);
		}
		rs.close();
		return children;
	}

	protected static TaxonomyItem getTaxonById(PluginSession session, int id) throws Exception
	{
		TaxonomyItem item = new TaxonomyItem(String.valueOf(id));
		String credits = generateCredits();
		String citation = generateCitation();
		fillTaxonomyItem(session, id, item, credits, citation);
		return item;
	}

	protected static String generateCitation()
	{
		StringBuilder citation = new StringBuilder("Intergovernmental Oceanographic Commission (IOC) of UNESCO. The Ocean Biogeographic Information System. Web. http://www.iobis.org. (Consulted on ");
		citation.append(sdf.format(Calendar.getInstance().getTime()));
		citation.append(")");
		return citation.toString();
	}

	protected static String generateCredits()
	{
		//credits ="This information object has been generated via the Species Product Discovery service on 2012-11-26 by interfacing with the Interim Register of Marine and Nonmarine Genera (IRMNG) (http://www.obis.org.au/irmng/)";
		StringBuilder credits = new StringBuilder("This information object has been generated via the Species Product Discovery service on ");
		credits.append(sdf.format(Calendar.getInstance().getTime()));
		credits.append(" by interfacing with the Intergovernmental Oceanographic Commission (IOC) of UNESCO. The Ocean Biogeographic Information System. Web. http://www.iobis.org.");
		return credits.toString();
	}

	static int counter = 0;
	static int sum = 0;

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String url = "jdbc:postgresql://geoserver2.i-marine.research-infrastructures.eu/obis";
		Properties props = new Properties();
		props.setProperty("user","postgres");
		props.setProperty("password","0b1s@d4sc13nc3");	


		final Connection connection = DriverManager.getConnection(url, props);

		//		System.out.println("Connected");

		final PluginSession session = new PluginSession(connection);
		session.preCacheStatements();

		searchByScientificName(session, "gadus morhua", new SearchFilters(), new Writer<ResultItem>() {

			@Override
			public boolean write(ResultItem item) {
				//				System.out.println(item.getId()+" "+item.getScientificNameAuthorship()+" "+item.getScientificName());
				return true;
			}
		});




		/*getTaxonByScientificNames(session, "sarda sarda", new Writer<TaxonomyItem>() {

			@Override
			public boolean write(TaxonomyItem item) {
				System.out.println(item.getId()+" "+item.getAuthor()+" "+item.getScientificName());
				return true;
			}
		});*/

		/*OccurrencePoint occurrencePoint = getOccurrenceById(session, "38069270");
		System.out.println(occurrencePoint);*/


		/*Taxon taxon = getTaxonByCommonName(session, "ruwe traliehoorn");//getTaxonByScientificNames(session, "Protozoa");
		System.out.println(taxon);

		List<Taxon> children = getChildrenTaxon(session, Integer.parseInt(taxon.getId()));
		for (Taxon child:children) System.out.println(child);*/

		/*searchByCommonName(session, "white shark", new SearchFilters(), new Writer<ResultItem>() {

			@Override
			public void write(ResultItem item) {
				System.out.println("Item: "+item.getDataSet().getDataProvider().getName()+" <-> "+item.getDataSet().getName());
			}
		});*/

		/*final long start = System.currentTimeMillis();
		getTaxonByScientificNames(session, "Gadus macrocephalus", new Writer<TaxonomyItem>() {
			long start = System.currentTimeMillis();

			@Override
			public void write(TaxonomyItem item) {


				System.out.println(item);

			}
		});*/

		/*SearchFilters filters = new SearchFilters();
		ObisClient.searchByScientificName(session, "sarda sarda", filters, new Writer<ResultItem>() {

			@Override
			public void write(ResultItem item) {
				System.out.println(item);
			}
		});*/

		/*List<TaxonomyItem> taxa = getChildrenTaxon(session,769809);
		for (TaxonomyItem taxon:taxa) System.out.println(taxon.getId());*/
		//navigate("", session, 741923);

		//System.out.println("result in "+(System.currentTimeMillis()-start)+" avg: "+(sum/counter)+" tot: "+counter);

		//		System.out.println("done");
		session.expire();
	}

	protected static Set<Integer> found = new HashSet<Integer>();
	protected static Map<String,TaxonomyItem> foundTaxon = new HashMap<String,TaxonomyItem>();

	protected static void navigate(String indentation, PluginSession session, int id) throws SQLException
	{
		//System.out.println("looking for children: "+id);
		if (found.contains(id)) {
			System.err.println("Already found "+id);
			System.err.println(foundTaxon.get(id));
			System.exit(-1);
		}

		List<TaxonomyItem> taxa = getChildrenTaxon(session,id);

		found.add(id);


		for (TaxonomyItem taxon:taxa) {
			System.out.println(indentation+taxon.getId()+" "+taxon.getRank());
			foundTaxon.put(taxon.getId(), taxon);
			navigate(indentation+"  ", session, Integer.valueOf(taxon.getId()));

		}
	}

}
