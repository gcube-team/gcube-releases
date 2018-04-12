package org.gcube.data.spd.parser;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DarwinSimpleRecord {

	private static final String GUID 				= "GlobalUniqueIdentifier";
	private static final String COLLECTION_CODE 	= "CollectionCode";
	private static final String AUTHOR 				= "AuthorYearOfScientificName";
	private static final String SCIENTIFICNAME 		= "ScientificName";
	private static final String BASIS_OF_RECORD 	= "BasisOfRecord";
	private static final String CATALOG_NUM 		= "CatalogNumber";
	private static final String CLAZZ 				= "Class";
	private static final String COLLECTOR 			= "collector";
	private static final String COLLECTOR_NUM 		= "CollectorNumber";
	private static final String COOR_UNCER_METERS 	= "CoordinateUncertaintyInMeters";
	private static final String CONTINENT 			= "Continent";
	private static final String COUNTRY 			= "Country";
	private static final String MODIFIED 			= "DateLastModified";
	private static final String FAMILY 				= "Family";
	private static final String FIELD_NUM 			= "FieldNumber";
	private static final String GENUS 				= "Genus";
	private static final String IDENTIFIED_BY 		= "IdentifiedBy";
	private static final String INDIVIDUAL_COUNT 	= "IndividualCount";
	private static final String INFRA_EPITHET 		= "InfraspecificEpithet";
	private static final String INSTITUTION_CODE 	= "InstitutionCode";
	private static final String KINGDOM 			= "Kingdom";
	private static final String LOCALITY 			= "Locality";
	private static final String ORDER 				= "Order";
	private static final String PHYLUM 				= "Phylum";
	private static final String REMARKS 			= "Remarks";
	private static final String SEX 				= "Sex";
	private static final String SPEC_EPITHET 		= "SpecificEpithet";
	private static final String STATE_PROV 			= "StateProvince";
	private static final String TYPE_STATUS 		= "TypeStatus";
	private static final String DECIMAL_LAT 		= "DecimalLatitude";
	private static final String DECIMAL_LONG 		= "DecimalLongitude";
	private static final String MAX_DEPTH 			= "MaximumDepthInMeters";
	private static final String MIN_DEPTH 			= "MinimumDepthInMeters";
	private static final String MAX_ELEVATION 		= "MaximumElevationInMeters";
	private static final String MIN_ELEVATION 		= "MinimumElevationInMeters";
	private static final String VERB_LAT 			= "VerbatimLatitude";
	private static final String VERB_LON 			= "VerbatimLongitude";

	private static final String RECORD 				= "DarwinRecord";


	static Logger logger = LoggerFactory.getLogger(DarwinSimpleRecord.class);

	public String authorYearOfScientificName;
	public String basisOfRecord;
	public String catalogNumber;
	public String clazz;
	public String collectionCode;
	//	public String collectionID;
	public String collector;
	public String collectorNumber;
	public String coordinateUncertaintyInMeters;
	public String continent;
	public String country;
	//	public String dayOfYear;
	public Calendar dateLastModified;
	public String decimalLatitude;
	public String decimalLongitude;	
	//	public Calendar eventTime;
	public String family;
	public String fieldNumber;
	public String globalUniqueIdentifier;
	public String genus;
	public String identifiedBy;
	public String individualCount;
	public String infraspecificEpithet;
	public String institutionCode;
	public String kingdom;
	public String locality;
	public String maximumDepthInMeters;
	public String maximumElevationInMeters;
	public String minimumDepthInMeters;
	public String minimumElevationInMeters;
	public String order;
	public String phylum;
	//	public String recordedBy;
	public String remarks;
	public String scientificName;
	//	public String scientificNameAuthorship;
	public String sex;
	public String specificEpithet;
	public String stateProvince;
	public String typeStatus;
	public String verbatimLatitude;
	public String verbatimLongitude;

	public static SimpleDateFormat df=new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");


	public DarwinSimpleRecord(XMLEventReader eventReader, boolean complete){
		//		System.out.println("DarwinSimpleRecord  " + eventReader);	

		while (eventReader.hasNext()){
			XMLEvent event = null;
			try {
				event = eventReader.nextEvent();
			} catch (XMLStreamException e) {
				logger.error("XMLStreamException", e);
			}
			//			   StartElement startElement = event.asStartElement();
			//		        System.out.println(startElement.getName().getLocalPart());

			if (Utils.checkStartElement(event, GUID)){			
				try {
					this.globalUniqueIdentifier = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;

			}else if (Utils.checkStartElement(event, AUTHOR)){	
				try {
					this.authorYearOfScientificName = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;

			}else if (Utils.checkStartElement(event, SCIENTIFICNAME)){	
				try {
					this.scientificName = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;

			}else if (Utils.checkStartElement(event, BASIS_OF_RECORD)){		
				try {

					this.basisOfRecord = Utils.readCharacters(eventReader);
					//					System.out.println(this.basisOfRecord);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;


			}else if (Utils.checkStartElement(event, CATALOG_NUM)){	
				try {
					this.catalogNumber = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;


			}else if ((complete) && (Utils.checkStartElement(event, CLAZZ))){	
				try {
					this.clazz = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;

			}else if (Utils.checkStartElement(event, COOR_UNCER_METERS)){	
				try {
					this.coordinateUncertaintyInMeters = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;

			}else if (Utils.checkStartElement(event, COLLECTION_CODE)){	
				try {
					this.collectionCode = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;

			}else if ((complete) && (Utils.checkStartElement(event, COLLECTOR))){		
				try {
					this.collector = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;


			}else if ((complete) && (Utils.checkStartElement(event, COLLECTOR_NUM))){		
				try {
					this.collectorNumber = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;

			}else if ((complete) && (Utils.checkStartElement(event, CONTINENT))){			
				try {
					this.continent = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;

			}else if (Utils.checkStartElement(event, COUNTRY)){	
				try {
					this.country = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;

			}else if (Utils.checkStartElement(event, DECIMAL_LAT)){	
				try {
					this.decimalLatitude = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;

			}else if (Utils.checkStartElement(event, DECIMAL_LONG)){	
				try {
					this.decimalLongitude = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;

			}else if (Utils.checkStartElement(event, FAMILY)){	
				try {
					this.family = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;

			}else if ((complete) && (Utils.checkStartElement(event, FIELD_NUM)) ){			
				try {
					this.fieldNumber = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;

			}else if ((complete) && (Utils.checkStartElement(event, GENUS)) ){		
				try {
					this.genus = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;		

			}else if (Utils.checkStartElement(event, IDENTIFIED_BY)){	
				try {
					this.identifiedBy = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;	


			}else if ((complete) && (Utils.checkStartElement(event, INDIVIDUAL_COUNT))  ){		
				try {
					this.individualCount = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;	

			}else if ((complete) && (Utils.checkStartElement(event, INFRA_EPITHET))  ){		
				try {
					this.infraspecificEpithet = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;

			}else if (Utils.checkStartElement(event, INSTITUTION_CODE)){	
				try {
					this.institutionCode = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;


			}else if (Utils.checkStartElement(event, KINGDOM)){	
				try {
					this.kingdom = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;


			}else if (Utils.checkStartElement(event, LOCALITY)){	
				try {
					this.locality = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;


			}else if (Utils.checkStartElement(event, MAX_DEPTH)){	
				try {
					this.maximumDepthInMeters = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;


			}else if (Utils.checkStartElement(event, MIN_DEPTH)){	
				try {
					this.minimumDepthInMeters = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;

			}else if ((complete) && (Utils.checkStartElement(event, MAX_ELEVATION))){
				try {
					this.maximumElevationInMeters = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;

			}else if ((complete) && (Utils.checkStartElement(event, MIN_ELEVATION))){
				try {
					this.minimumElevationInMeters = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;
			}else if (Utils.checkStartElement(event, MODIFIED)){	
				String modifiedDate = null;
				try {
					modifiedDate = Utils.readCharacters(eventReader);
					if (modifiedDate!=null)
						this.dateLastModified = getCalendar(modifiedDate);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;


			}else if ((complete) && (Utils.checkStartElement(event, ORDER))){	
				try {
					this.order = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;


			}else if ((complete) && (Utils.checkStartElement(event, PHYLUM))){		
				try {
					this.phylum = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;


			}else if ((complete) && (Utils.checkStartElement(event, REMARKS))){		
				try {
					this.remarks = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;


			}else if ((complete) && (Utils.checkStartElement(event, SEX))){
				try {
					this.sex = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;


			}else if ((complete) && (Utils.checkStartElement(event, SPEC_EPITHET))){
				try {
					this.specificEpithet = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;

			}else if ((complete) && (Utils.checkStartElement(event, STATE_PROV))){
				try {
					this.stateProvince = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;


			}else if ((complete) && (Utils.checkStartElement(event, TYPE_STATUS))){
				try {
					this.typeStatus = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;



			}else if ((complete) && (Utils.checkStartElement(event, VERB_LAT))){	
				try {
					this.verbatimLatitude = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;


			}else if ((complete) && (Utils.checkStartElement(event, VERB_LON))){	
				try {
					this.verbatimLongitude = Utils.readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
				continue;


			} else if (Utils.checkEndElement(event, RECORD)){	
				//				System.out.println("</DarwinRecord>");	
				break;
			}
			}


			}

			private Calendar getCalendar(String myDate) {
				Calendar dateC = null;
				try {

					myDate = myDate.replace("T", " ");
					dateC = dateString2Calendar(myDate);

				} catch (ParseException e) {
					logger.error("ParseException" , e);
				}
				return dateC;
			}


			private Calendar dateString2Calendar(String s) throws ParseException {
				Calendar cal=Calendar.getInstance();
				try{
					Date d1=df.parse(s);
					cal.setTime(d1);
				}
				catch (Exception e) {
					return null;
				}
				return cal;
			}

			}
