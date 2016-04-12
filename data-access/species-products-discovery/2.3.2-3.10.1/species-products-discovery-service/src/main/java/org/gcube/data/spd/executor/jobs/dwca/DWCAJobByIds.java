package org.gcube.data.spd.executor.jobs.dwca;

import static org.gcube.data.streams.dsl.Streams.convert;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.commons.io.FileUtils;
import org.gcube.common.scope.impl.ScopedTasks;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.data.spd.Constants;
import org.gcube.data.spd.exception.MaxRetriesReachedException;
import org.gcube.data.spd.executor.jobs.JobStatus;
import org.gcube.data.spd.executor.jobs.URLJob;
import org.gcube.data.spd.manager.TaxonomyItemWriterManager;
import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;
import org.gcube.data.spd.model.exceptions.IdNotValidException;
import org.gcube.data.spd.model.exceptions.StreamNonBlockingException;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.model.util.Capabilities;
import org.gcube.data.spd.plugin.PluginManager;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.plugin.fwk.readers.LocalReader;
import org.gcube.data.spd.plugin.fwk.util.Util;
import org.gcube.data.spd.plugin.fwk.writers.Writer;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.AbstractWrapper;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.LocalWrapper;
import org.gcube.data.spd.utils.JobRetryCall;
import org.gcube.data.spd.utils.Utils;
import org.gcube.data.streams.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DWCAJobByIds implements URLJob{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(DWCAJobByChildren.class);
	
	private static UUIDGen uuidGen = UUIDGenFactory.getUUIDGen();
	
	private String resultURL = null;
	
	private String errorURL = null;
	
	private Calendar endDate, startDate;
	
	private int completedEntries = 0;
		
	private JobStatus status;
	
	private String id;
		
	private String idsReference;
		
	public DWCAJobByIds(String idsReference) {
		//this.mapSubJobs = new HashMap<TaxonomyItem, JobStatus>();
		this.id = uuidGen.nextUUID();
		this.idsReference = idsReference;
		this.status = JobStatus.PENDING;
	}
		
	
	@Override
	public void run() {
		File errorsFile= null;
		File dwcaFile = null;
		try{
			this.startDate = Calendar.getInstance();
			this.status = JobStatus.RUNNING;
			
			final LocalWrapper<TaxonomyItem> localWrapper = new LocalWrapper<TaxonomyItem>(2000);
			
			final LocalWrapper<String> errorWrapper = new LocalWrapper<String>(2000);
			Writer<String> errorWriter = new Writer<String>(errorWrapper);
			errorWriter.register();	
						
			TaxonReader taxonReader = new TaxonReader(localWrapper, errorWriter, idsReference);
			new Thread(ScopedTasks.bind(taxonReader)).start();
			
			LocalReader<TaxonomyItem> reader = new LocalReader<TaxonomyItem>(localWrapper);
			
			MapDwCA dwca = new MapDwCA();
			dwcaFile =dwca.createDwCA(reader);
			
			logger.trace("the file is null ?"+(dwcaFile==null));
			
			IClient client = new StorageClient(Constants.SERVICE_CLASS, Constants.SERVICE_NAME, "DWCA", AccessType.SHARED).getClient();
			
			String resultPath = "/dwca/"+this.id.replace("-", "")+".zip";
			client.put(true).LFile(dwcaFile.getAbsolutePath()).RFile(resultPath);
			this.resultURL=client.getUrl().RFile(resultPath);
			
			LocalReader<String> errorReader = new LocalReader<String>(errorWrapper);
			errorsFile = Utils.createErrorFile(errorReader);
			errorReader.close();
			
			if (errorsFile!=null){
				String errorFilePath = "/dwca/"+this.id.replace("-", "")+"-ERRORS.txt";
				client.put(true).LFile(errorsFile.getAbsolutePath()).RFile(errorFilePath);
				this.errorURL=client.getUrl().RFile(errorFilePath);
			}
			
			logger.trace("filePath is "+dwcaFile.getAbsolutePath());
			this.status = JobStatus.COMPLETED;
		}catch (Exception e) {
			logger.error("error executing DWCAJob",e);
			this.status = JobStatus.FAILED;
			return;
		}finally{
			if (dwcaFile!=null)
				try {
					FileUtils.forceDelete(dwcaFile.getParentFile());
				} catch (IOException e) {
					logger.warn("error deleting file",e);
				}
			if (errorsFile!=null)
				errorsFile.delete();
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

	
	public String getResultURL() {
		return resultURL;
	}
	
	@Override
	public String getErrorURL() {
		return this.errorURL;
	}
	
	public Calendar getEndDate() {
		return endDate;
	}


	public Calendar getStartDate() {
		return startDate;
	}



	public class TaxonReader  implements Runnable{
		
		
		private AbstractWrapper<TaxonomyItem> wrapper;
		private String idsReference;
		private Writer<String> errorWriter;
		
		public TaxonReader(AbstractWrapper<TaxonomyItem> wrapper, Writer<String> errorWriter,  String idsReference) {
			this.wrapper = wrapper;
			this.idsReference = idsReference;
			this.errorWriter= errorWriter;
		}

		@Override
		public void run() {
			Stream<String> ids =convert(URI.create(this.idsReference)).ofStrings().withDefaults();
			PluginManager manager = PluginManager.get();
			
			Map<String, Writer<TaxonomyItem>> pluginMap = new HashMap<String, Writer<TaxonomyItem>>();
			
			while(ids.hasNext()){
				String key = ids.next();
				String id = null;
				String provider = null;
				try{
					id = Util.getIdFromKey(key);
					provider = Util.getProviderFromKey(key);
					Writer<TaxonomyItem> writer;
					AbstractPlugin plugin = manager.plugins().get(provider);
					if (plugin!=null && plugin.getSupportedCapabilities().contains(Capabilities.Classification)){
						if (!pluginMap.containsKey(provider)){
							writer = new Writer<TaxonomyItem>(wrapper, new TaxonomyItemWriterManager(plugin.getRepositoryName()));
							writer.register();
							pluginMap.put(plugin.getRepositoryName(), writer);
						}else
							writer = pluginMap.get(plugin.getRepositoryName());
						TaxonomyItem item = retrieveByIdWithRetry(plugin, id);
						writer.write(item);
						//retrieving references
						if (item.getStatus()!=null && item.getStatus().getRefId() != null){
							String refId = item.getStatus().getRefId();
							try {
								TaxonomyItem tempTaxon = retrieveByIdWithRetry(plugin, refId);
								do{
									writer.write(tempTaxon);
									tempTaxon = tempTaxon.getParent();
								}while(tempTaxon!=null);
							} catch (IdNotValidException e) {
								logger.warn("refId "+id+" not retrieved for plugin "+plugin.getRepositoryName(),e);
								writer.write(new StreamNonBlockingException(plugin.getRepositoryName(), refId));
							}
						}
						while (item.getParent()!=null)
							writer.write(item = item.getParent());
					}
					else logger.warn("taxon capability or plugin not found for key " +key);
				
					completedEntries++;
				}catch (IdNotValidException e) {
					logger.error("error retrieving key "+key,e);
					errorWriter.write(provider+" - "+id);
				}catch (MaxRetriesReachedException e) {
					logger.error("max retry reached for "+provider,e);
					errorWriter.write(provider+" - "+id);
				}
				
			}
			for(Writer<TaxonomyItem> writer: pluginMap.values())
				writer.close();
			this.errorWriter.close();
		}
		
	}

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
		
	private TaxonomyItem retrieveByIdWithRetry(final AbstractPlugin plugin, final String id) throws MaxRetriesReachedException, IdNotValidException{
		return new JobRetryCall<TaxonomyItem, IdNotValidException>() {

			@Override
			protected TaxonomyItem execute()
					throws ExternalRepositoryException, IdNotValidException {
				return plugin.getClassificationInterface().retrieveTaxonById(id);
			}
		}.call();
	}
	
}
