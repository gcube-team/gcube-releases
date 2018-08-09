package org.gcube.data.spd.obisplugin.search;

import static org.gcube.data.spd.obisplugin.search.query.MappingUtils.getAsCalendar;
import static org.gcube.data.spd.obisplugin.search.query.MappingUtils.getAsDouble;
import static org.gcube.data.spd.obisplugin.search.query.MappingUtils.getAsInteger;
import static org.gcube.data.spd.obisplugin.search.query.MappingUtils.getAsString;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.gcube.data.spd.model.BasisOfRecord;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.exceptions.StreamBlockingException;
import org.gcube.data.spd.model.products.DataProvider;
import org.gcube.data.spd.model.products.DataSet;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.obisplugin.Constants;
import org.gcube.data.spd.obisplugin.search.query.MappingUtils;
import org.gcube.data.spd.obisplugin.search.query.PagedQueryIterator;
import org.gcube.data.spd.obisplugin.search.query.PagedQueryObject;
import org.gcube.data.spd.obisplugin.search.query.QueryByIdentifier;
import org.gcube.data.spd.obisplugin.search.query.QueryCondition;
import org.gcube.data.spd.obisplugin.search.query.QueryType;
import org.gcube.data.spd.obisplugin.search.query.ResultType;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OccurrenceSearch {

	private static Logger log = LoggerFactory.getLogger(OccurrenceSearch.class);

	private String baseURL;

	private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	public OccurrenceSearch(String baseURL) {
		this.baseURL = baseURL;
	}

	public void search(ObjectWriter<OccurrencePoint> writer, String scientificName, int limit, Condition ...conditions) throws Exception{
		PagedQueryObject occurrencesQuery = new PagedQueryObject(baseURL, ResultType.Occurrence, limit);
		List<QueryCondition> queryConditions = Utils.elaborateConditions(conditions);
		occurrencesQuery.setConditions(QueryCondition.cond("scientificname",scientificName.replaceAll(" ", "%20")));
		occurrencesQuery.getConditions().addAll(queryConditions);
		writeElements(writer, occurrencesQuery);
	}

	public void searchByKey(ObjectWriter<OccurrencePoint> writer, String key, int limit) throws Exception{
		PagedQueryObject occurrencesQuery = new PagedQueryObject(baseURL, ResultType.Occurrence, limit);
		ProductKey productKey = Utils.elaborateProductsKey(key);
		occurrencesQuery.getConditions().addAll(productKey.getQueryCondition());
		writeElements(writer, occurrencesQuery);
	}

	private void writeElements(ObjectWriter<OccurrencePoint> writer, PagedQueryObject occurrencesQuery){
		PagedQueryIterator<OccurrencePoint> pagedIterator = new PagedQueryIterator<OccurrencePoint>(occurrencesQuery) {

			@Override
			protected OccurrencePoint getObject(Map<String, Object> mappedObject)
					throws Exception {
				OccurrencePoint op = retrieveElement(mappedObject);
				Calendar now = Calendar.getInstance();
				String credits = "Biodiversity occurrence data accessed through OBIS WebService, http://api.iobis.org/, "+format.format(now.getTime())+")";
				op.setCredits(credits);
				return op;
			}

		};

		try{
			while (pagedIterator.hasNext() && writer.isAlive())
				writer.write(pagedIterator.next());
		}catch(Exception e){
			log.error("error writing occurrences",e);
			writer.write(new StreamBlockingException(Constants.REPOSITORY_NAME));
		}
	}

	public OccurrencePoint searchById(String id) throws Exception{
		QueryByIdentifier queryByIdentifier = new QueryByIdentifier(baseURL, id, QueryType.Occurrence);
		return retrieveElement(MappingUtils.getObjectMapping(queryByIdentifier.build()));
	}


	/*
	  	FOSSIL_SPECIMEN
			An occurrence record describing a fossilized specimen.
		HUMAN_OBSERVATION
			An occurrence record describing an observation made by one or more people.
		LITERATURE
			An occurrence record based on literature alone.
		LIVING_SPECIMEN
			An occurrence record describing a living specimen, e.g.
		MACHINE_OBSERVATION
			An occurrence record describing an observation made by a machine.
		MATERIAL_SAMPLE
			An occurrence record based on samples taken from other specimens or the environment.
		OBSERVATION
			An occurrence record describing an observation.
		PRESERVED_SPECIMEN
	 		An occurrence record describing a preserved specimen.
		UNKNOWN
	 */

	



	private OccurrencePoint retrieveElement(Map<String, Object> mappedObj) throws Exception{
		/*
		 
		 {"id":772,"decimalLongitude":79.5,"decimalLatitude":-62.5,"eventDate":"1974-01-01 11:00:00",
		 "institutionCode":"AADC","collectionCode":"WC","catalogNumber":"1672","individualCount":193.0,
		 "datasetName":"Whale catches in the Southern Ocean","phylum":"Chordata","order":"Cetartiodactyla",
		 "family":"Balaenopteridae","genus":"Balaenoptera","scientificName":"Balaenoptera bonaerensis",
		 "originalScientificName":"Balaenoptera bonaerensis","scientificNameAuthorship":"Burmeister, 1867",
		 "obisID":409234,"resourceID":22,"yearcollected":1974,"species":"Balaenoptera bonaerensis","qc":1073217151,"aphiaID":231405
		 ,"speciesID":409234,"scientificNameID":"urn:lsid:marinespecies.org:taxname:231405","class":"Mammalia"}
		 
		 
		 */
		
		long start = System.currentTimeMillis();
		String occurrenceId = getAsInteger(mappedObj, "id").toString();
		OccurrencePoint occurrence = new OccurrencePoint(occurrenceId);
		
		occurrence.setDecimalLatitude(getAsDouble(mappedObj, "decimalLatitude"));
		occurrence.setDecimalLongitude(getAsDouble(mappedObj, "decimalLongitude"));
		
		Calendar eventDate = getAsCalendar(mappedObj, "eventDate");
		occurrence.setEventDate(eventDate);
		occurrence.setCollectionCode(getAsString(mappedObj, "collectionCode"));
		occurrence.setInstitutionCode(getAsString(mappedObj, "institutionCode"));
		occurrence.setCatalogueNumber(getAsString(mappedObj, "catalogNumber"));
		
		//occurrence.setRecordedBy(getAsString(mappedObj, "recordedBy"));
		//occurrence.setIdentifiedBy(getAsString(mappedObj, "identifiedBy"));
		//occurrence.setCountry(getAsString(mappedObj, "country"));
		//occurrence.setLocality(getAsString(mappedObj, "locality"));

		occurrence.setBasisOfRecord(BasisOfRecord.Unknown);

		/*occurrence.setMinDepth(getAsDouble(mappedObj, "elevation"));
		occurrence.setMaxDepth(getAsDouble(mappedObj, "depth"));
		*/
		
		occurrence.setKingdom("Animalia");
		occurrence.setFamily(getAsString(mappedObj, "family"));

		occurrence.setScientificNameAuthorship(getAsString(mappedObj, "scientificNameAuthorship")); 
		occurrence.setScientificName(getAsString(mappedObj, "scientificName")); 
		
		String datasetName = getAsString(mappedObj, "datasetName");
		DataSet dataset = new DataSet(datasetName);
		dataset.setCitation(datasetName);
		dataset.setName(datasetName);
		DataProvider dataProvider = new DataProvider("OBIS");
		dataProvider.setName("OBIS");
		dataset.setDataProvider(dataProvider);
		
		occurrence.setDataSet(dataset);
		
		//occurrence.setCitation(getAsString(mappedObj, "accordingTo"));
		log.trace("[Benchmark] time to retrieve occurrence is "+(System.currentTimeMillis()-start));
		return occurrence;
	}


}
