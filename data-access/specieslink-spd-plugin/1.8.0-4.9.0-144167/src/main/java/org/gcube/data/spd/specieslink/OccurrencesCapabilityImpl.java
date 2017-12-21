package org.gcube.data.spd.specieslink;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.gcube.data.spd.model.BasisOfRecord;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.model.exceptions.StreamBlockingException;
import org.gcube.data.spd.parser.DarwinSimpleRecord;
import org.gcube.data.spd.parser.RecordsIterator;
import org.gcube.data.spd.plugin.fwk.capabilities.OccurrencesCapability;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class OccurrencesCapabilityImpl extends OccurrencesCapability {

	static Logger logger = LoggerFactory.getLogger(OccurrencesCapabilityImpl.class);

	@SuppressWarnings("serial")
	public Set<Conditions> getSupportedProperties() {
		return new HashSet<Conditions>() {{
			add(Conditions.DATE);
			add(Conditions.COORDINATE);
		}};
		
	}

	private static BasisOfRecord matchBasisOfRecord(String value){
		if(value!=null){
			for (BasisOfRecord record: BasisOfRecord.values())
				if (record.name().toLowerCase().equals(value.toLowerCase())) return record;
			if (value.toLowerCase().equals("S".toLowerCase())) return BasisOfRecord.PreservedSpecimen;
			else if (value.toLowerCase().equals("Fotot".toLowerCase())) return BasisOfRecord.MachineObservation;
		}
		return BasisOfRecord.HumanObservation;
	}

	@Override
	public void searchByScientificName(String scientificName,
			ObjectWriter<OccurrencePoint> writer, Condition... properties) {
		logger.trace("searchByScientificName " + scientificName + " in SpeciesLink");
		String f = "";
		try {
			f = Utils.elaborateProps(properties);
		} catch (Exception e) {
			logger.error("error elaborating properties",e);
			return;
		}

		try{
			String filter = "http://rs.tdwg.org/dwc/dwcore/ScientificName%20like%20%22" + scientificName.replace(" ", "%20") + "%22" + f + "&orderBy=http://rs.tdwg.org/dwc/dwcore/ScientificName&orderBy=http://rs.tdwg.org/dwc/dwcore/InstitutionCode";
//System.out.println("SpeciesLinkPlugin.baseurl " + SpeciesLinkPlugin.baseurl);
			createElement(SpeciesLinkPlugin.baseurl, filter, SpeciesLinkPlugin.model, SpeciesLinkPlugin.limit, writer);
		}catch (Exception e) {
			logger.error("General Error", e);
			writer.write(new StreamBlockingException("SpeciesLink", ""));

		}

	}


	private void createElement(String url, String filter, String model,
			int limit, ObjectWriter<OccurrencePoint> writer) {

		RecordsIterator set = new RecordsIterator(url, filter, model, limit, false);
		Iterator<DarwinSimpleRecord> it = set.iterator();

		DarwinSimpleRecord element = null;		

		while (it.hasNext()) 
		{
			element = it.next();
			
			OccurrencePoint a = new OccurrencePoint(element.globalUniqueIdentifier);
			a.setScientificNameAuthorship(element.authorYearOfScientificName);
			
			if (element.basisOfRecord!=null)
				a.setBasisOfRecord(matchBasisOfRecord(element.basisOfRecord));
			a.setCatalogueNumber(element.catalogNumber);
			a.setCoordinateUncertaintyInMeters(element.coordinateUncertaintyInMeters);
//			a.setRecordedBy(element.recordedBy);
			a.setIdentifiedBy(element.identifiedBy);
			a.setCollectionCode(element.collectionCode);
			a.setCountry(element.country);
			if (element.decimalLatitude != null)
				a.setDecimalLatitude(Double.parseDouble(element.decimalLatitude));
			if (element.decimalLongitude != null)
				a.setDecimalLongitude(Double.parseDouble(element.decimalLongitude));
//			a.setEventDate(element.eventTime);
			a.setFamily(element.family);
			a.setInstitutionCode(element.institutionCode);
			a.setKingdom(element.kingdom);
			a.setLocality(element.locality);
			
			if (element.maximumDepthInMeters != "")
				a.setMaxDepth(Double.parseDouble(element.maximumDepthInMeters));
			if (element.minimumDepthInMeters != "")
				a.setMinDepth(Double.parseDouble(element.minimumDepthInMeters));

			a.setModified(element.dateLastModified);
			a.setScientificName(element.scientificName);	
			
			
			a.setCredits(Utils.credits());
			a.setCitation(Utils.citation());

			if ((a!=null) && (writer.isAlive()))
				writer.write(a);
			else
				break;

		}


	}

	@Override
	public void getOccurrencesByProductKeys(
			ClosableWriter<OccurrencePoint> writer, Iterator<String> keys) {
		try{
			while(keys.hasNext()) {

				String key = keys.next(); 
				logger.trace("getOccurrencesByProductKeys " + key + " in SpeciesLink");

				createElement(SpeciesLinkPlugin.baseurl, key, SpeciesLinkPlugin.model, SpeciesLinkPlugin.limit, writer);


			}
		}catch (Exception e) {
			writer.write(new StreamBlockingException("SpeciesLink",""));
		}

	}

	@Override
	public void getOccurrencesByIds(ClosableWriter<OccurrencePoint> writer,
			Iterator<String> ids) {
		try{
			while(ids.hasNext()) {

				String id = ids.next(); 
				logger.trace("getOccurrencesByIds " + id + " in SpeciesLink");

				String filter = "http://rs.tdwg.org/dwc/terms/occurrenceID%20equals%20%22" + id + "%22";

				createElement(SpeciesLinkPlugin.baseurl, filter, SpeciesLinkPlugin.model, SpeciesLinkPlugin.limit, writer);
			}
		}catch (Exception e) {
			writer.write(new StreamBlockingException("SpeciesLink",""));
		}

	}


}
