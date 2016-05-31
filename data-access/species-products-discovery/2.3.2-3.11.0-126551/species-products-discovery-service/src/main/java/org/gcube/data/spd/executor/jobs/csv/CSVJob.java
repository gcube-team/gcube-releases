package org.gcube.data.spd.executor.jobs.csv;

import static org.gcube.data.streams.dsl.Streams.convert;

import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.csv4j.CSVWriter;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.data.spd.Constants;
import org.gcube.data.spd.executor.jobs.JobStatus;
import org.gcube.data.spd.executor.jobs.URLJob;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.plugin.PluginManager;
import org.gcube.data.spd.plugin.fwk.readers.LocalReader;
import org.gcube.data.spd.plugin.fwk.writers.Writer;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.LocalWrapper;
import org.gcube.data.streams.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class CSVJob implements URLJob{

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(CSVJob.class);
	
	private static UUIDGen uuidGen = UUIDGenFactory.getUUIDGen();
	
	private int completedEntries = 0;
	
	private String resultURL = null;
		
	private Calendar endDate, startDate;
	
	private JobStatus status;
	
	private String id;
	
	private Map<TaxonomyItem, JobStatus> mapSubJobs;
	
	private String referenceIds;
		
	
	public CSVJob(String referenceIds) {
		this.mapSubJobs = new HashMap<TaxonomyItem, JobStatus>();
		this.id = uuidGen.nextUUID();
		this.referenceIds = referenceIds;
		this.status = JobStatus.PENDING;
	}
		
	
	@Override
	public void run() {
		File csvFile = null;
		try{
			this.startDate = Calendar.getInstance();
			this.status = JobStatus.RUNNING;
			
			csvFile = File.createTempFile(this.id.replace("-", ""), ".csv");
			logger.trace("outputfile "+csvFile.getAbsolutePath());

			LocalWrapper<OccurrencePoint> localWrapper = new LocalWrapper<OccurrencePoint>(1000);
			localWrapper.forceOpen();
			
			final LocalWrapper<String> errorWrapper = new LocalWrapper<String>(2000);
			errorWrapper.forceOpen();
			Writer<String> errorWriter = new Writer<String>(errorWrapper);
			errorWriter.register();	
			
			Stream<String> ids =convert(URI.create(this.referenceIds)).ofStrings().withDefaults();
			
			OccurrenceReaderByKey occurrenceReader =new OccurrenceReaderByKey(localWrapper, ids, PluginManager.get().plugins());
			
			new Thread(occurrenceReader).start();
						
			FileWriter fileWriter = new FileWriter(csvFile);
			CSVWriter csvWriter = new CSVWriter(fileWriter);
				
			
			csvWriter.writeLine(getHeader());
			
			LocalReader<OccurrencePoint> ocReader= new LocalReader<OccurrencePoint>(localWrapper);
			
			Converter<OccurrencePoint, List<String>> csvConverter = getConverter();
			
			while (ocReader.hasNext()){
				 OccurrencePoint op = ocReader.next();
				 csvWriter.writeLine(csvConverter.convert(op));
				 completedEntries++;
			}
			
			fileWriter.close();
			
			logger.trace("closing file, witing it to the storage");
			
			IClient client = new StorageClient(Constants.SERVICE_CLASS, Constants.SERVICE_NAME, "CSV", AccessType.SHARED).getClient();
			
			String filePath = "/csv/"+this.id.replace("-", "")+".csv";
									
			client.put(true).LFile(csvFile.getAbsolutePath()).RFile(filePath);
			
			this.resultURL=client.getUrl().RFile(filePath);
			
			this.status = JobStatus.COMPLETED;
		}catch (Exception e) {
			logger.error("error executing CSVJob",e);
			this.status = JobStatus.FAILED;
			return;
		}finally{
			if (csvFile!=null)
				csvFile.delete();
			this.endDate = Calendar.getInstance();
		}
	}

	public JobStatus getStatus() {
		return status;
	}

	
	
	public void setStatus(JobStatus status) {
		this.status = status;
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
		// TODO Auto-generated method stub
		return null;
	}


	public abstract Converter<OccurrencePoint, List<String>> getConverter();

	public abstract List<String> getHeader();
	
	@Override
	public boolean validateInput(String input) {
		try{
			URI.create(input);
		}catch (Exception e) {
			return false;
		}
		return true;
	}

	
	@Override
	public int getCompletedEntries() {
		return completedEntries;
	}


	public Calendar getEndDate() {
		return endDate;
	}


	public Calendar getStartDate() {
		return startDate;
	}
	
}
