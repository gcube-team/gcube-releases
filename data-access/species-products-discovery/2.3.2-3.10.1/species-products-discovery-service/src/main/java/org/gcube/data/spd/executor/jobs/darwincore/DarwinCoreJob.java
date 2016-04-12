package org.gcube.data.spd.executor.jobs.darwincore;

import static org.gcube.data.streams.dsl.Streams.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.data.spd.Constants;
import org.gcube.data.spd.executor.jobs.JobStatus;
import org.gcube.data.spd.executor.jobs.URLJob;
import org.gcube.data.spd.executor.jobs.csv.OccurrenceReaderByKey;
import org.gcube.data.spd.model.exceptions.StreamException;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.plugin.PluginManager;
import org.gcube.data.spd.plugin.fwk.readers.LocalReader;
import org.gcube.data.spd.plugin.fwk.util.Util;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.LocalWrapper;
import org.gcube.data.spd.utils.Utils;
import org.gcube.data.streams.Stream;
import org.gcube.data.streams.exceptions.StreamSkipSignal;
import org.gcube.data.streams.exceptions.StreamStopSignal;
import org.gcube.data.streams.generators.Generator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DarwinCoreJob implements URLJob{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(DarwinCoreJob.class);
	
	private static UUIDGen uuidGen = UUIDGenFactory.getUUIDGen();
	
	private int completedEntries = 0;
	
	private Calendar endDate, startDate;
	
	private String resultURL = null;
	
	private String errorFileURL = null;
	
	private JobStatus status;
	
	private String id;
	
	private Map<TaxonomyItem, JobStatus> mapSubJobs;
	
	private String occurrenceKeys;
		
	public DarwinCoreJob(String occurrenceKeys) {
		this.id = uuidGen.nextUUID();
		this.occurrenceKeys = occurrenceKeys;
		this.status = JobStatus.PENDING;
	}
		
	
	@Override
	public void run() {
		File darwincoreFile =null;
		File errorFile = null;
		try{
			this.startDate = Calendar.getInstance();
			this.status = JobStatus.RUNNING;
			
			LocalWrapper<OccurrencePoint> localWrapper = new LocalWrapper<OccurrencePoint>(2000);
			localWrapper.forceOpen();
					
			Stream<String> ids =convert(URI.create(this.occurrenceKeys)).ofStrings().withDefaults();
			
			OccurrenceReaderByKey occurrenceReader = new OccurrenceReaderByKey(localWrapper, ids, PluginManager.get().plugins());
			
			new Thread(occurrenceReader).start();
			
			LocalReader<OccurrencePoint> ocReader= new LocalReader<OccurrencePoint>(localWrapper);
						
			IClient client = new StorageClient(Constants.SERVICE_CLASS, Constants.SERVICE_NAME, "DarwinCore", AccessType.SHARED).getClient();
			
			darwincoreFile =getDarwinCoreFile(ocReader);
			String resultPath = "/darwincore/"+this.id.replace("-", "");
			client.put(true).LFile(darwincoreFile.getAbsolutePath()).RFile(resultPath);
			this.resultURL=client.getUrl().RFile(resultPath);
			
			errorFile = Utils.createErrorFile(
					pipe(convert(localWrapper.getErrors())).through(new Generator<StreamException, String>() {

						@Override
						public String yield(StreamException element)
								throws StreamSkipSignal, StreamStopSignal {
							return element.getRepositoryName()+" "+element.getIdentifier();
						}
					}));
			
			if (errorFile!=null){
				String errorFilePath = "/darwincore/"+this.id.replace("-", "")+"-ERRORS.txt";
				client.put(true).LFile(darwincoreFile.getAbsolutePath()).RFile(errorFilePath);
				this.errorFileURL=client.getUrl().RFile(errorFilePath);
			}

			logger.trace("filePath is "+darwincoreFile.getAbsolutePath());
			this.status = JobStatus.COMPLETED;
		}catch (Exception e) {
			logger.error("error executing DWCAJob",e);
			this.status = JobStatus.FAILED;
			return;
		}finally{
			if (darwincoreFile!=null)
				darwincoreFile.delete();
			if (errorFile!=null)
				errorFile.delete();	
			this.endDate = Calendar.getInstance();
		}
	}

	public JobStatus getStatus() {
		return status;
	}

	
	
	public void setStatus(JobStatus status) {
		this.status = status;
	}

	

	public Calendar getEndDate() {
		return endDate;
	}


	public Calendar getStartDate() {
		return startDate;
	}


	public String getId() {
		return id;
	}

	public Map<TaxonomyItem, JobStatus> getMapSubJobs() {
		return mapSubJobs;
	}
		
	public String getResultURL() {
		return resultURL;
	}


	@Override
	public String getErrorURL() {
		return this.errorFileURL;
	}
	
	@Override
	public boolean validateInput(String input) {
		try{ 
			Util.getIdFromKey(input);
			Util.getProviderFromKey(input);
		}catch (Exception e) {
			return false;
		}
		return true;
	}
	
	@Override
	public int getCompletedEntries() {
		return completedEntries;
	}
	
		
	private File getDarwinCoreFile(Iterator<OccurrencePoint> reader) throws Exception{

		DateFormat df = new  SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		
		OutputStreamWriter writer = new OutputStreamWriter(new ByteArrayOutputStream());
		
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
				if (occurrence.getScientificNameAuthorship() != null)
					writer.append("<dwc:scientificNameAuthorship>" + occurrence.getScientificNameAuthorship() + "</dwc:scientificNameAuthorship>");
				if (occurrence.getInstitutionCode() != null)
					writer.append("<dwc:institutionCode>" + occurrence.getInstitutionCode() + "</dwc:institutionCode>");
				if (occurrence.getCollectionCode() != null)
					writer.append("<dwc:collectionCode>" + occurrence.getCollectionCode() + "</dwc:collectionCode>");
				if (occurrence.getCatalogueNumber() != null)
					writer.append("<dwc:catalogNumber>" + occurrence.getCatalogueNumber() + "</dwc:catalogNumber>");
				if (occurrence.getIdentifiedBy() != null)
					writer.append("<dwc:identifiedBy>" + occurrence.getIdentifiedBy() + "</dwc:identifiedBy>");
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
				completedEntries++;
			}

			writer.append("</SimpleDarwinRecordSet>");
			writer.flush();
			writer.close();
			return returnFile;
		}catch (Exception e) {
			logger.error("error writing occurrences as darwin core",e);
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
