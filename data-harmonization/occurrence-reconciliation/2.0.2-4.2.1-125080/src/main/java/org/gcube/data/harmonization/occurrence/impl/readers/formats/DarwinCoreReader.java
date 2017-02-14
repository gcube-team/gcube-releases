package org.gcube.data.harmonization.occurrence.impl.readers.formats;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.lang.StringEscapeUtils;
import org.gcube.data.harmonization.occurrence.impl.readers.StreamProgress;
import org.gcube.data.harmonization.occurrence.impl.readers.StreamProgress.OperationState;
import org.gcube.data.harmonization.occurrence.impl.readers.XMLParserConfiguration;
import org.gcube.data.spd.model.BasisOfRecord;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.ResultWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DarwinCoreReader extends DefaultHandler{

	private static final Logger logger = LoggerFactory.getLogger(DarwinCoreReader.class);

	private DateFormat df = new  SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	private ResultWrapper<OccurrencePoint> wrapper=null;

	private OccurrencePoint currentPoint=null;
	private StringBuilder stringBuilder;
	private StreamProgress progress;
	private XMLParserConfiguration config;
	private CountingInputStream cis;

	public DarwinCoreReader(ResultWrapper<OccurrencePoint> wrapper,StreamProgress progress,XMLParserConfiguration config,CountingInputStream cis) {
		this.progress=progress;
		this.wrapper=wrapper;
		this.config=config;
		this.cis=cis;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException{
		super.startElement(uri, localName, qName, attributes);
		if(qName.equals("SimpleDarwinRecord")) currentPoint=new OccurrencePoint("");
	}

	@Override
	public void characters(char[] ch, int start, int length)
	throws SAXException {
		if(currentPoint!=null){
			StringBuilder local=new StringBuilder();
			for(int i=start;i<start+length;i++)
				local.append(ch[i]);
			stringBuilder=new StringBuilder(StringEscapeUtils.escapeXml(local.toString()));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
	throws SAXException {
		if(currentPoint!=null){	
			if(qName.equals("SimpleDarwinRecord")){
				try {
					wrapper.add(currentPoint);
					progress.setElaboratedLenght(cis.getCount());
				} catch (Exception e) {
					progress.setFailureReason("Unable to stream data");
					progress.setFailureDetails(e.getMessage());
					progress.setState(OperationState.FAILED);
				}
			}else if(qName.equals("dc:modified")){ 
				try{
					Calendar cal=Calendar.getInstance();
					cal.setTime(df.parse(stringBuilder.toString()));
					currentPoint.setModified(cal);
				}catch(ParseException e){
					logger.debug("Unable to parse modified "+stringBuilder.toString());
				}
			}else if(qName.equals("dwc:basisOfRecord")){
				try{
					currentPoint.setBasisOfRecord(BasisOfRecord.valueOf(stringBuilder.toString()));
				}catch(Exception e){
					logger.debug("Unable to parse basis of record "+stringBuilder.toString());
				}
			}else if(qName.equals("dwc:institutionCode")){
				currentPoint.setInstitutionCode(stringBuilder.toString());
			}else if(qName.equals("dwc:collectionCode")){
				currentPoint.setCollectionCode(stringBuilder.toString());
			}else if(qName.equals("dwc:catalogNumber")){
				currentPoint.setCatalogueNumber(stringBuilder.toString());
			}else if(qName.equals("dwc:recordedBy")){
				currentPoint.setRecordedBy(stringBuilder.toString());
			}else if(qName.equals("dwc:scientificName")){
				currentPoint.setScientificName(stringBuilder.toString());
			}else if(qName.equals("dwc:kingdom")){
				currentPoint.setKingdom(stringBuilder.toString());
			}else if(qName.equals("dwc:family")){
				currentPoint.setFamily(stringBuilder.toString());
			}else if(qName.equals("dwc:locality")){
				currentPoint.setLocality(stringBuilder.toString());
			}else if(qName.equals("dwc:eventDate")){ 
				try{
					Calendar cal=Calendar.getInstance();
					cal.setTime(df.parse(stringBuilder.toString()));
					currentPoint.setEventDate(cal);
				}catch(ParseException e){
					logger.debug("Unable to parse eventDate "+stringBuilder.toString());
				}
			}else if(qName.equals("dwc:decimalLatitude")){
				try{
					currentPoint.setDecimalLatitude(Double.parseDouble((stringBuilder.toString())));
				}catch(Exception e){
					logger.debug("Unable to parse decimalLatitude "+stringBuilder.toString());
				}
			}else if(qName.equals("dwc:decimalLongitude")){
				try{
					currentPoint.setDecimalLongitude(Double.parseDouble((stringBuilder.toString())));
				}catch(Exception e){
					logger.debug("Unable to parse decimalLongitude "+stringBuilder.toString());
				}
			}else if(qName.equals("dwc:coordinateUncertaintyInMeters")){
					currentPoint.setCoordinateUncertaintyInMeters(stringBuilder.toString());
			}else if(qName.equals("dwc:maximumDepthInMeters")){
				try{
					currentPoint.setMaxDepth(Double.parseDouble((stringBuilder.toString())));
				}catch(Exception e){
					logger.debug("Unable to parse max depth "+stringBuilder.toString());
				}
			}else if(qName.equals("dwc:minimumDepthInMeters")){
				try{
					currentPoint.setMinDepth(Double.parseDouble((stringBuilder.toString())));
				}catch(Exception e){
					logger.debug("Unable to parse min depth "+stringBuilder.toString());
				}
			}
		}
	}

	@Override
	public void endDocument() throws SAXException {
		try {
			wrapper.close();
		} catch (Exception e) {
			logger.error("Unable to close Stream", e);
		}
	}

}
