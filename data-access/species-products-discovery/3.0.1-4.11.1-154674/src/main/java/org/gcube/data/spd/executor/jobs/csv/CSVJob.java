package org.gcube.data.spd.executor.jobs.csv;

import static org.gcube.data.streams.dsl.Streams.convert;

import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.sf.csv4j.CSVWriter;

import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.data.spd.Constants;
import org.gcube.data.spd.executor.jobs.URLJob;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.model.service.types.CompleteJobStatus;
import org.gcube.data.spd.model.service.types.JobStatus;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.plugin.fwk.readers.LocalReader;
import org.gcube.data.spd.plugin.fwk.writers.Writer;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.LocalWrapper;
import org.gcube.data.spd.utils.DynamicMap;
import org.gcube.data.streams.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class CSVJob extends URLJob{

	private static Logger logger = LoggerFactory.getLogger(CSVJob.class);
	
	private int completedEntries = 0;

	private String resultURL = null;

	private Calendar endDate, startDate;

	private JobStatus status;

	private String id;

	private Map<TaxonomyItem, CompleteJobStatus> mapSubJobs;

	private Map<String, AbstractPlugin> plugins;

	public CSVJob(Map<String, AbstractPlugin> plugins) {
		this.mapSubJobs = new HashMap<TaxonomyItem, CompleteJobStatus>();
		this.id = UUID.randomUUID().toString();
		this.status = JobStatus.PENDING;
		this.plugins = plugins;
	}

	@Override
	public void execute() {
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

			Stream<String> ids =convert(DynamicMap.get(this.id));

			OccurrenceReaderByKey occurrenceReader =new OccurrenceReaderByKey(localWrapper, ids, plugins);

			new Thread(occurrenceReader).start();

			FileWriter fileWriter = new FileWriter(csvFile);
			CSVWriter csvWriter = new CSVWriter(fileWriter);

			csvWriter.writeLine(getHeader());

			LocalReader<OccurrencePoint> ocReader= new LocalReader<OccurrencePoint>(localWrapper);

			Converter<OccurrencePoint, List<String>> csvConverter = getConverter();

			logger.debug("starting to read from localReader");
												
			while (ocReader.hasNext()){
				OccurrencePoint op = ocReader.next();
				csvWriter.writeLine(csvConverter.convert(op));
				completedEntries++;
			}
			
			if (completedEntries==0)
				throw new Exception("no record waswritten");
			
			logger.debug("closing file, writing it to the storage");
						
			fileWriter.close();
			csvWriter.close();
			
			IClient client = new StorageClient(Constants.SERVICE_CLASS, Constants.SERVICE_NAME, "CSV", AccessType.SHARED).getClient();

			String filePath = "/csv/"+this.id.replace("-", "")+".csv";

			client.put(true).LFile(csvFile.getAbsolutePath()).RFile(filePath);

			this.resultURL=client.getUrl().RFile(filePath);
			
			logger.debug("job completed");
			
			this.status = JobStatus.COMPLETED;
		}catch (Exception e) {
			logger.error("error executing CSVJob",e);
			this.status = JobStatus.FAILED;
			return;
		}finally{
			if (csvFile!=null)
				csvFile.delete();
			this.endDate = Calendar.getInstance();
			DynamicMap.remove(this.id);
			
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

	public Map<TaxonomyItem, CompleteJobStatus> getMapSubJobs() {
		return mapSubJobs;
	}

	public String getResultURL() {
		return resultURL;
	}

	@Override
	public String getErrorURL() {
		return null;
	}


	public abstract Converter<OccurrencePoint, List<String>> getConverter();

	public abstract List<String> getHeader();

	@Override
	public boolean validateInput(String input) {
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

	@Override
	public boolean isResubmitPermitted() {
		return false;
	}

	
}
