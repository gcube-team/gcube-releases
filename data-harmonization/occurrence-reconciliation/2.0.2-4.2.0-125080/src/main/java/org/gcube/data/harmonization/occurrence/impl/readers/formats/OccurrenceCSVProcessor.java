package org.gcube.data.harmonization.occurrence.impl.readers.formats;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.csv4j.CSVLineProcessor;

import org.apache.commons.io.input.CountingInputStream;
import org.gcube.data.harmonization.occurrence.impl.readers.CSVParserConfiguration;
import org.gcube.data.harmonization.occurrence.impl.readers.StreamProgress;
import org.gcube.data.harmonization.occurrence.impl.readers.StreamProgress.OperationState;
import org.gcube.data.spd.model.BasisOfRecord;
import org.gcube.data.spd.model.products.DataProvider;
import org.gcube.data.spd.model.products.DataSet;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.ResultWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OccurrenceCSVProcessor implements CSVLineProcessor {


	protected static final SimpleDateFormat FORMAT = new SimpleDateFormat("MM/dd/yy KK:mm:ss a"); 

	private static final Logger logger = LoggerFactory.getLogger(OccurrenceCSVProcessor.class);	

	private Map<OccurrencePointFields,Integer> mapping=new HashMap<OccurrencePointFields, Integer>();

	private ResultWrapper<OccurrencePoint> wrapper;
	private StreamProgress progress;
	private CSVParserConfiguration config;
	private CountingInputStream cis;
	private long pointCount=0;
	
	public OccurrenceCSVProcessor(ResultWrapper<OccurrencePoint> wrapper,StreamProgress progress,CSVParserConfiguration config,CountingInputStream cis) {
		this.wrapper=wrapper;
		this.progress=progress;
		this.config=config;
		this.cis=cis;
	}

	@Override
	public boolean continueProcessing() {
		return !progress.getState().equals(StreamProgress.OperationState.FAILED);
	}

	@Override
	public void processDataLine(int arg0, List<String> arg1) {
		OccurrencePoint point=new OccurrencePoint("");
		point.setDataSet(new DataSet(""));
		point.getDataSet().setDataProvider(new DataProvider(""));

		for(Entry<OccurrencePointFields,Integer> entry:mapping.entrySet()){
			String columnValue=arg1.get(entry.getValue());
			switch(entry.getKey()){

			case BASIS_OF_RECORD : 	try{
				point.setBasisOfRecord(BasisOfRecord.valueOf(columnValue));
			}catch(Exception e){
				logger.warn("Unable to evaluate basis of record : "+columnValue);			
			} break;

			case CATALOGUE_NUMBER : point.setCatalogueNumber(columnValue);
			break;

			case CITATION : 		point.setCitation(columnValue);
			break;

			case COLLECTION_CODE : 	point.setCollectionCode(columnValue);
			break;

			case COORDINATE_UNCERTAINTY_IN_METERS : point.setCoordinateUncertaintyInMeters(columnValue);
			break;

			case COUNTRY : 			point.setCountry(columnValue);
			break;

			case DATA_PROVIDER_ID : break;

			case DATA_PROVIDER_NAME : 	point.getDataSet().getDataProvider().setName(columnValue);
			break;

			case DATA_SET_CITATION : 	break;

			case DATA_SET_ID : 		break;

			case DATA_SET_NAME : 	point.getDataSet().setName(columnValue);
			break;

			case DECIMAL_LATITUDE : try{
				point.setDecimalLatitude(Double.parseDouble(columnValue));
			}catch(Exception e){
				logger.debug("Unable to parse decimal latitude "+columnValue);
			}break;

			case DECIMAL_LONGITUDE : 	try{
				point.setDecimalLongitude(Double.parseDouble(columnValue));
			}catch(Exception e){
				logger.debug("Unable to parse decimal longitude "+columnValue);
			}break;
			case EVENT_DATE : 	try{
				Date date=FORMAT.parse(columnValue);
				Calendar calendar=Calendar.getInstance();
				calendar.setTime(date);
				point.setEventDate(calendar);
			}catch(Exception e){
				logger.warn("Unable to parse event date : "+columnValue);
			}break;

			case FAMILY : 	point.setFamily(columnValue);
			break;

			case ID : 		point.setId(columnValue);
			break;

			case INSTITUTION_CODE :	point.setInstitutionCode(columnValue);
			break;

			case KINGDOM : 	point.setKingdom(columnValue);
			break;

			case LOCALITY : point.setLocality(columnValue);
			break;

			case MAX_DEPTH : 	try{
				point.setMaxDepth(Double.parseDouble(columnValue));
			}catch(Exception e){
				logger.debug("Unable to parse max depth "+columnValue);
			}break;

			case MIN_DEPTH :  	try{
				point.setMinDepth(Double.parseDouble(columnValue));
			}catch(Exception e){
				logger.debug("Unable to parse min depth "+columnValue);
			}break;

			case MODIFIED :		try{
				Date date=FORMAT.parse(columnValue);
				Calendar calendar=Calendar.getInstance();
				calendar.setTime(date);
				point.setModified(calendar);
			}catch(Exception e){
				logger.warn("Unable to parse modified: "+columnValue);
			}break;

			case PROVIDER : 	point.setProvider(columnValue);
			break;

			case RECORDED_BY : 	point.setRecordedBy(columnValue);
			break;

			case SCIENTIFIC_NAME : 	point.setScientificName(columnValue);
			break;
			}
		}


		try {
			wrapper.add(point);
			progress.setElaboratedLenght(cis.getCount());
			System.out.println(pointCount++);
		} catch (Exception e) {
			progress.setFailureReason("Unable to stream data");
			progress.setFailureDetails(e.getMessage());
			progress.setState(OperationState.FAILED);
		}


	}

	@Override
	public void processHeaderLine(int arg0, List<String> arg1) {
		//Check for open modeller mapping
		for(int i=0;i<arg1.size();i++)
			if(config.getFieldMap()[i]){
				String s=arg1.get(i);
				if(s.equals("#id"))mapping.put(OccurrencePointFields.ID, i);
				else if(s.equals("label"))mapping.put(OccurrencePointFields.SCIENTIFIC_NAME, i);
				else if(s.equals("long"))mapping.put(OccurrencePointFields.DECIMAL_LONGITUDE, i);
				else if(s.equals("lat"))mapping.put(OccurrencePointFields.DECIMAL_LATITUDE, i);
			}
		if(mapping.size()!=4){						
			//Check for standard mapping
			for(int i=0;i<arg1.size();i++)
				if(config.getFieldMap()[i]){
					String s=arg1.get(i);
					if(s.equals("institutionCode"))mapping.put(OccurrencePointFields.INSTITUTION_CODE, i);
					else if(s.equals("collectionCode"))mapping.put(OccurrencePointFields.COLLECTION_CODE, i);
					else if(s.equals("catalogueNumber"))mapping.put(OccurrencePointFields.CATALOGUE_NUMBER, i);
					else if(s.equals("dataSet"))mapping.put(OccurrencePointFields.DATA_SET_NAME, i);
					else if(s.equals("dataProvider"))mapping.put(OccurrencePointFields.DATA_PROVIDER_NAME, i);
					else if(s.equals("dataSource"))mapping.put(OccurrencePointFields.PROVIDER, i);
					else if(s.equals("recordedBy"))mapping.put(OccurrencePointFields.RECORDED_BY, i);
					else if(s.equals("eventDate"))mapping.put(OccurrencePointFields.EVENT_DATE, i);
					else if(s.equals("modified"))mapping.put(OccurrencePointFields.MODIFIED, i);
					else if(s.equals("scientificName"))mapping.put(OccurrencePointFields.SCIENTIFIC_NAME, i);
					else if(s.equals("kingdom"))mapping.put(OccurrencePointFields.KINGDOM, i);
					else if(s.equals("family"))mapping.put(OccurrencePointFields.FAMILY, i);
					else if(s.equals("locality"))mapping.put(OccurrencePointFields.LOCALITY, i);
					else if(s.equals("country"))mapping.put(OccurrencePointFields.COUNTRY, i);
					else if(s.equals("citation"))mapping.put(OccurrencePointFields.CITATION, i);
					else if(s.equals("decimalLatitude"))mapping.put(OccurrencePointFields.DECIMAL_LATITUDE, i);
					else if(s.equals("decimalLongitude"))mapping.put(OccurrencePointFields.DECIMAL_LONGITUDE, i);		
					else if(s.equals("coordinateUncertaintyInMeters"))mapping.put(OccurrencePointFields.COORDINATE_UNCERTAINTY_IN_METERS, i);
					else if(s.equals("maxDepth"))mapping.put(OccurrencePointFields.MAX_DEPTH, i);
					else if(s.equals("minDepth"))mapping.put(OccurrencePointFields.MIN_DEPTH, i);
					else if(s.equals("basisOfRecord"))mapping.put(OccurrencePointFields.BASIS_OF_RECORD, i);
				}	
			if(mapping.size()!=21){
				progress.setFailureReason("Unable to understand model");
				progress.setFailureDetails("");
				progress.setState(OperationState.FAILED);	
			}			
		}
	}
}
