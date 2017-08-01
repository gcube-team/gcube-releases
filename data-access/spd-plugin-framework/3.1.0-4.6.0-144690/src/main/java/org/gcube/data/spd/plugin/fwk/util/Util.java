package org.gcube.data.spd.plugin.fwk.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.bind.JAXBException;

import org.gcube.data.spd.model.binding.Bindings;
import org.gcube.data.spd.model.exceptions.IdNotValidException;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.model.products.ResultElement;
import org.gcube.data.spd.plugin.fwk.readers.OccurrencesReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {

	private static final Logger logger = LoggerFactory.getLogger(Util.class);
	
	public static String keyEnrichment(String provider, String key){
		return provider+":"+key;
	}
	
	public static String getProviderFromKey(String key) throws IdNotValidException{
		int index = key.indexOf(":");
		if (index==-1) throw new IdNotValidException();
		return key.substring(0, index);
	}
	
	public static String getIdFromKey(String key) throws IdNotValidException {
		int index = key.indexOf(":");
		if (index==-1) throw new IdNotValidException();
		return key.substring(index+1, key.length());
	}
	
	public static <T extends ResultElement> T copy(T obj) throws JAXBException {
		return Bindings.<T>fromXml(Bindings.<T>toXml(obj));
	}
	
	public static File getDarwinCoreFile(OccurrencesReader reader) throws Exception{

		DateFormat df = new  SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		
		FileWriter writer = null;
		
		
		try{
			File returnFile = File.createTempFile("darwinCore", "xml");
			writer = new FileWriter(returnFile);
			
			
			writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			writer.append("<SimpleDarwinRecordSet xmlns=\"http://rs.tdwg.org/dwc/xsd/simpledarwincore/\" xmlns:dc=\"http://purl.org/dc/terms/\" xmlns:dwc=\"http://rs.tdwg.org/dwc/terms/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://rs.tdwg.org/dwc/xsd/simpledarwincore/ http://rs.tdwg.org/dwc/xsd/tdwg_dwc_simple.xsd\">");

			while (reader.hasNext()){

				writer.append("<SimpleDarwinRecord>");
				writer.append("<dc:language>en</dc:language>");

				OccurrencePoint occurrence= reader.next();

				if (occurrence.getModified() != null)
					writer.append("<dc:modified>" + df.format(occurrence.getModified().getTime()) + "</dc:modified>");		
				if (occurrence.getBasisOfRecord() != null)
					writer.append("<dwc:basisOfRecord>" + occurrence.getBasisOfRecord().name() + "</dwc:basisOfRecord>");
				if (occurrence.getInstitutionCode() != null)
					writer.append("<dwc:institutionCode>" + occurrence.getInstitutionCode() + "</dwc:institutionCode>");
				if (occurrence.getCollectionCode() != null)
					writer.append("<dwc:collectionCode>" + occurrence.getCollectionCode() + "</dwc:collectionCode>");
				if (occurrence.getCatalogueNumber() != null)
					writer.append("<dwc:catalogNumber>" + occurrence.getCatalogueNumber() + "</dwc:catalogNumber>");
				if (occurrence.getRecordedBy() != null)
					writer.append("<dwc:recordedBy>" + occurrence.getRecordedBy() + "</dwc:recordedBy>");
				if (occurrence.getScientificName() != null)
					writer.append("<dwc:scientificName>" + occurrence.getScientificName() + "</dwc:scientificName>");
				if (occurrence.getKingdom() != null)
					writer.append("<dwc:kingdom>" + occurrence.getKingdom() + "</dwc:kingdom>");
				if (occurrence.getFamily() != null)
					writer.append("<dwc:family>" + occurrence.getFamily() + "</dwc:family>");
				if (occurrence.getLocality() != null)
					writer.append("<dwc:locality>" + occurrence.getLocality() + "</dwc:locality>");
				if (occurrence.getEventDate() != null)
				{
					writer.append("<dwc:eventDate>" + df.format(occurrence.getEventDate().getTime()) + "</dwc:eventDate>");	
					writer.append("<dwc:year>" + occurrence.getEventDate().get(Calendar.YEAR) + "</dwc:year>");
				}
				if (occurrence.getDecimalLatitude() != 0.0)
					writer.append("<dwc:decimalLatitude>" + occurrence.getDecimalLatitude() + "</dwc:decimalLatitude>");
				if (occurrence.getDecimalLongitude() != 0.0)
					writer.append("<dwc:decimalLongitude>" + occurrence.getDecimalLongitude() + "</dwc:decimalLongitude>");
				if (occurrence.getCoordinateUncertaintyInMeters() != null)
					writer.append("<dwc:coordinateUncertaintyInMeters>" + occurrence.getCoordinateUncertaintyInMeters() + "</dwc:coordinateUncertaintyInMeters>");
				if (occurrence.getMaxDepth() != 0.0)
					writer.append("<dwc:maximumDepthInMeters>" + occurrence.getMaxDepth() + "</dwc:maximumDepthInMeters>");
				if (occurrence.getMinDepth() != 0.0)
					writer.append("<dwc:minimumDepthInMeters>" + occurrence.getMinDepth() + "</dwc:minimumDepthInMeters>");

				writer.append("</SimpleDarwinRecord>");
			}

			writer.append("</SimpleDarwinRecordSet>");
			writer.flush();
			writer.close();
			return returnFile;
		}catch (Exception e) {
			logger.error("error writeing occurrences as darwin core",e);
			throw e;
		}finally{
			try {
				writer.close();
			} catch (IOException e) {
				logger.warn("error closing the output stream",e);
			}
		}
	}
	
	
}
