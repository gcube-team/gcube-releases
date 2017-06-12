package org.gcube.data.spd.gbifplugin.search;

import static org.gcube.data.spd.gbifplugin.search.query.MappingUtils.getAsCalendar;
import static org.gcube.data.spd.gbifplugin.search.query.MappingUtils.getAsDouble;
import static org.gcube.data.spd.gbifplugin.search.query.MappingUtils.getAsInteger;
import static org.gcube.data.spd.gbifplugin.search.query.MappingUtils.getAsString;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.gcube.data.spd.gbifplugin.Constants;
import org.gcube.data.spd.gbifplugin.search.query.MappingUtils;
import org.gcube.data.spd.gbifplugin.search.query.PagedQueryIterator;
import org.gcube.data.spd.gbifplugin.search.query.PagedQueryObject;
import org.gcube.data.spd.gbifplugin.search.query.QueryByIdentifier;
import org.gcube.data.spd.gbifplugin.search.query.QueryCondition;
import org.gcube.data.spd.gbifplugin.search.query.QueryType;
import org.gcube.data.spd.gbifplugin.search.query.ResultType;
import org.gcube.data.spd.model.BasisOfRecord;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.exceptions.StreamBlockingException;
import org.gcube.data.spd.model.products.DataSet;
import org.gcube.data.spd.model.products.OccurrencePoint;
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
		occurrencesQuery.setConditions(QueryCondition.cond("scientificName",scientificName.replaceAll(" ", "%20")), QueryCondition.cond("hasCoordinate","true"));
		occurrencesQuery.getConditions().addAll(queryConditions);

		writeElements(writer, occurrencesQuery, null);
	}

	public void searchByKey(ObjectWriter<OccurrencePoint> writer, String key, int limit) throws Exception{
		PagedQueryObject occurrencesQuery = new PagedQueryObject(baseURL, ResultType.Occurrence, limit);
		ProductKey productKey = Utils.elaborateProductsKey(key);
		occurrencesQuery.getConditions().addAll(productKey.getQueryCondition());
		occurrencesQuery.getConditions().add( QueryCondition.cond("hasCoordinate","true"));
		writeElements(writer, occurrencesQuery, productKey.getDataset());
	}

	private void writeElements(ObjectWriter<OccurrencePoint> writer, PagedQueryObject occurrencesQuery, final DataSet dataset){
		PagedQueryIterator<OccurrencePoint> pagedIterator = new PagedQueryIterator<OccurrencePoint>(occurrencesQuery) {

			@Override
			protected OccurrencePoint getObject(Map<String, Object> mappedObject)
					throws Exception {
				OccurrencePoint op = retrieveElement(mappedObject);
				if (dataset!=null){
					Calendar now = Calendar.getInstance();
					String credits = "Biodiversity occurrence data published by: "+dataset.getDataProvider().getName()+" (Accessed through GBIF Data Portal, data.gbif.org, "+format.format(now.getTime())+")";
					op.setCredits(credits);
					op.setDataSet(dataset);
				}
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

	private static BasisOfRecord matchBasisOfRecord(String value){
		if (value.equals("PRESERVED_SPECIMEN")) return BasisOfRecord.PreservedSpecimen;
		else if (value.equals("HUMAN_OBSERVATION")) return BasisOfRecord.HumanObservation;
		else if (value.equals("FOSSIL_SPECIMEN")) return BasisOfRecord.FossilSpecimen;
		else if (value.equals("MACHINE_OBSERVATION")) return BasisOfRecord.MachineObservation;
		else if (value.equals("LIVING_SPECIMEN")) return BasisOfRecord.LivingSpecimen;
		else if (value.equals("OBSERVATION")) return BasisOfRecord.Observation;
		else if (value.equals("MATERIAL_SAMPLE")) return BasisOfRecord.MaterialSample;
		else if (value.equals("LITERATURE")) return BasisOfRecord.Literature;
		return BasisOfRecord.Unknown;
	}



	private OccurrencePoint retrieveElement(Map<String, Object> mappedObj) throws Exception{
		long start = System.currentTimeMillis();
		String occurrenceId = getAsInteger(mappedObj, "key").toString();
		OccurrencePoint occurrence = new OccurrencePoint(occurrenceId);

		occurrence.setCollectionCode(getAsString(mappedObj, "collectionCode"));
		occurrence.setInstitutionCode(getAsString(mappedObj, "institutionCode"));
		occurrence.setCatalogueNumber(getAsString(mappedObj, "catalogNumber"));
		occurrence.setRecordedBy(getAsString(mappedObj, "recordedBy"));
		occurrence.setIdentifiedBy(getAsString(mappedObj, "identifiedBy"));
		occurrence.setCountry(getAsString(mappedObj, "country"));
		occurrence.setLocality(getAsString(mappedObj, "locality"));

		Calendar eventDate = getAsCalendar(mappedObj, "eventDate");
		if (eventDate==null)		
			eventDate =getAsCalendar(mappedObj,"dateIdentified");

		occurrence.setEventDate(eventDate);

		occurrence.setDecimalLatitude(getAsDouble(mappedObj, "decimalLatitude"));
		occurrence.setDecimalLongitude(getAsDouble(mappedObj, "decimalLongitude"));

		occurrence.setBasisOfRecord(matchBasisOfRecord(getAsString(mappedObj, "basisOfRecord")));

		occurrence.setMinDepth(getAsDouble(mappedObj, "elevation"));
		occurrence.setMaxDepth(getAsDouble(mappedObj, "depth"));

		String taxonKey = getAsInteger(mappedObj, "taxonKey").toString();
		ReducedTaxon rt = retrieveParentTaxon(taxonKey);
		occurrence.setKingdom(rt.getKingdom());
		occurrence.setFamily(rt.getFamily());

		QueryByIdentifier taxonQuery = new QueryByIdentifier(baseURL, taxonKey , QueryType.Taxon);
		Map<String, Object> taxon = MappingUtils.getObjectMapping(taxonQuery.build());
		occurrence.setScientificNameAuthorship(getAsString(taxon, "authorship")); 
		occurrence.setScientificName(getAsString(taxon, "scientificName")); 
		occurrence.setCitation(getAsString(taxon, "accordingTo"));
		log.trace("[Benchmark] time to retrieve occurrence is "+(System.currentTimeMillis()-start));
		return occurrence;
	}


	private ReducedTaxon retrieveParentTaxon(String taxonId) throws Exception {
		long start = System.currentTimeMillis();
		QueryByIdentifier query = new QueryByIdentifier(baseURL, taxonId, QueryType.Taxon);
		query.addPath("parents");
		LinkedList<HashMap<String, Object>> parentsList = MappingUtils.getObjectList(query.build());
		ReducedTaxon taxon = new ReducedTaxon();
		for(HashMap<String, Object> mappedObject : parentsList){
			String rank = getAsString(mappedObject, "rank");
			String value = getAsString(mappedObject, "scientificName");
			if (rank.equalsIgnoreCase("family"))
				taxon.setFamily(value);
			else if (rank.equalsIgnoreCase("kingdom"))
				taxon.setKingdom(value);
			if (taxon.isValid())
				return taxon;
		}
		log.trace("[Benchmark] time to retrieve taxon is "+(System.currentTimeMillis()-start));
		return taxon;
	}


	protected static class ReducedTaxon{

		private String family = null;
		private String kingdom = null;
		public String getFamily() {
			return family;
		}
		public void setFamily(String family) {
			this.family = family;
		}
		public String getKingdom() {
			return kingdom;
		}
		public void setKingdom(String kingdom) {
			this.kingdom = kingdom;
		}

		public boolean isValid(){
			return family!=null && kingdom!=null;
		}
	}

}
