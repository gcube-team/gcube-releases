package org.gcube.data.spd.executor.jobs.dwca;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.commons.io.FileUtils;
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
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.model.util.Capabilities;
import org.gcube.data.spd.plugin.PluginManager;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.plugin.fwk.readers.LocalReader;
import org.gcube.data.spd.plugin.fwk.util.Util;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.data.spd.plugin.fwk.writers.Writer;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.LocalWrapper;
import org.gcube.data.spd.stubs.UnsupportedCapabilityFault;
import org.gcube.data.spd.stubs.UnsupportedPluginFault;
import org.gcube.data.spd.utils.JobRetryCall;
import org.gcube.data.spd.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DWCAJobByChildren implements URLJob{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(DWCAJobByChildren.class);
	
	private static UUIDGen uuidGen = UUIDGenFactory.getUUIDGen();
	
	private int completedEntries = 0;
	
	private String resultURL = null;
	
	private String errorFileURL = null;
		
	private JobStatus status;
	
	private Calendar endDate, startDate;
	
	private String id;
	
	private Map<TaxonomyItem, JobStatus> mapSubJobs;
	
	private String taxonKey;
	
	
	public DWCAJobByChildren(String taxonKey) {
		logger.trace("the Taxon Key is "+taxonKey);
		this.mapSubJobs = new HashMap<TaxonomyItem, JobStatus>();
		this.id = uuidGen.nextUUID();
		this.taxonKey = taxonKey;
		this.status = JobStatus.PENDING;
	}
		
	private AbstractPlugin pluginToUse = null;
	
	private AbstractPlugin getPlugin(String key) throws Exception{
		if (pluginToUse==null){
			PluginManager manager = PluginManager.get();
			String pluginName = Util.getProviderFromKey(key);
			if (!manager.plugins().containsKey(pluginName))
				throw new UnsupportedPluginFault();
			return manager.plugins().get(pluginName);
		} else return pluginToUse;
	}
	
	//ONLY FOR TEST PURPOSE
	public void setPluginToUse(AbstractPlugin plugin){
		this.pluginToUse = plugin;
	}
	
	@Override
	public void run() {
		File errorFile = null;
		File dwcaFile = null;
		try{
			this.startDate = Calendar.getInstance();
			this.status = JobStatus.RUNNING;
			
			AbstractPlugin plugin = getPlugin(this.taxonKey);
			
			logger.trace("plugin for this job is"+ plugin.getRepositoryName());
			String id = Util.getIdFromKey(this.taxonKey);
							
			if (!plugin.getSupportedCapabilities().contains(Capabilities.Classification)) throw new UnsupportedCapabilityFault();
			
			List<TaxonomyItem> taxa = getChildrenWithRetry(id, plugin);
			if (taxa==null) throw new Exception("failed contacting external repository");
			if (taxa.size()==0) throw new Exception("the taxon with key "+this.taxonKey+" has no children" );
						
			TaxonomyItem rootItem = plugin.getClassificationInterface().retrieveTaxonById(id);
			
			for (TaxonomyItem taxon : taxa){
				taxon.setParent(rootItem);	
				mapSubJobs.put(taxon, JobStatus.PENDING);
			}
			
			final LocalWrapper<TaxonomyItem> localWrapper = new LocalWrapper<TaxonomyItem>(2000);
			Writer<TaxonomyItem> writer = new Writer<TaxonomyItem>(localWrapper, new TaxonomyItemWriterManager(plugin.getRepositoryName()));
			writer.register();						
			
			final LocalWrapper<String> errorWrapper = new LocalWrapper<String>(2000);
			Writer<String> errorWriter = new Writer<String>(errorWrapper);
			errorWriter.register();	
			
			do{
				writer.write(rootItem);
				rootItem = rootItem.getParent();
			} while (rootItem !=null);
			
			new TaxonReader(writer, errorWriter, plugin).start();
				
			LocalReader<TaxonomyItem> reader = new LocalReader<TaxonomyItem>(localWrapper);
						
			MapDwCA dwca = new MapDwCA();
			dwcaFile =dwca.createDwCA(reader);
			
			logger.trace("the file is null ?"+(dwcaFile==null));
			
			logger.trace("filePath is "+dwcaFile.getAbsolutePath());
			
			IClient client = new StorageClient(Constants.SERVICE_CLASS, Constants.SERVICE_NAME, "DWCA", AccessType.SHARED).getClient();
			
			String resultPath = "/dwca/"+this.id.replace("-", "")+".zip";
						
			client.put(true).LFile(dwcaFile.getAbsolutePath()).RFile(resultPath);
			
			this.resultURL=client.getUrl().RFile(resultPath);
									
			LocalReader<String> errorReader = new LocalReader<String>(errorWrapper);
			errorFile = Utils.createErrorFile(errorReader);
			errorReader.close();
			
			if (errorFile!=null){
				String errorFilePath = "/dwca/"+this.id.replace("-", "")+"-ERRORS.txt";
				client.put(true).LFile(errorFile.getAbsolutePath()).RFile(errorFilePath);
				this.errorFileURL= client.getUrl().RFile(errorFilePath);
			}  
				
			logger.trace("files stored");
						
			this.status = JobStatus.COMPLETED;
		}catch (Exception e) {
			logger.error("error executing DWCAJob",e);
			this.status = JobStatus.FAILED;
			return;
		} finally{
			if (dwcaFile!=null && dwcaFile.exists())
				try {
					FileUtils.forceDelete(dwcaFile.getParentFile());
				} catch (IOException e) {
					logger.error("error deleting file",e);
				}
			if (errorFile!=null && errorFile.exists())
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


	public String getId() {
		return id;
	}

	public Map<TaxonomyItem, JobStatus> getMapSubJobs() {
		return mapSubJobs;
	}
		
	public Calendar getEndDate() {
		return endDate;
	}

	public Calendar getStartDate() {
		return startDate;
	}

	public String getResultURL() {
		return resultURL;
	}

	@Override
	public String getErrorURL() {
		return errorFileURL;
	}

	public class TaxonReader extends Thread{
		
		private Writer<TaxonomyItem> writer;
		private Writer<String> errorWriter;
		private AbstractPlugin plugin;
		
		public TaxonReader(Writer<TaxonomyItem> writer, Writer<String> errorWriter, AbstractPlugin plugin) {
			this.writer = writer;
			this.plugin = plugin;
			this.errorWriter = errorWriter;
		}

		@Override
		public void run() {
			for (Entry<TaxonomyItem, JobStatus> entry: mapSubJobs.entrySet()){
				entry.setValue(JobStatus.RUNNING);
				try{
					retrieveTaxaTree(writer, errorWriter, entry.getKey(),  plugin);
					entry.setValue(JobStatus.COMPLETED);
					completedEntries++;
				}catch (Exception e) {
					errorWriter.write(entry.getKey().getScientificName());
					entry.setValue(JobStatus.FAILED);
					logger.warn("failed computing job for taxon "+entry.getKey());
				}
			}
			this.writer.close();
			this.errorWriter.close();
		}

		
		private void retrieveTaxaTree(ObjectWriter<TaxonomyItem> writer, ObjectWriter<String> errorWriter, TaxonomyItem taxon, AbstractPlugin plugin) throws IdNotValidException, Exception{
			writer.write(taxon);

			//retrieving references
			if (taxon.getStatus()!=null && taxon.getStatus().getRefId() != null){
				String id = taxon.getStatus().getRefId();
				try {
					TaxonomyItem tempTaxon = retrieveTaxonIdWithRetry(id, plugin);
					do{
						writer.write(tempTaxon);
						tempTaxon = tempTaxon.getParent();
					}while(tempTaxon!=null);
				} catch (Exception e) {
					logger.warn("refId "+id+" not retrieved for plugin "+plugin.getRepositoryName(),e);
					errorWriter.write(plugin.getRepositoryName()+" - "+taxon.getId()+" - "+taxon.getScientificName());
				}
			}
			
			
			List<TaxonomyItem> items = null;
			
			try{
				items = getChildrenWithRetry(taxon.getId(), plugin);
			}catch (MaxRetriesReachedException e) {
				logger.trace("error retrieving element with id {} and scientific name {} ",taxon.getId(),taxon.getScientificName());
				errorWriter.write(plugin.getRepositoryName()+" - "+taxon.getId()+" - "+taxon.getScientificName());
			}
			
			if (items!=null) 
				for(TaxonomyItem item : items){
					item.setParent(taxon);
					logger.trace("sending request for item with id "+item.getId());
					retrieveTaxaTree(writer, errorWriter, item, plugin);
				}
			

		}		
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
	
	private List<TaxonomyItem> getChildrenWithRetry(final String id, final AbstractPlugin plugin) throws IdNotValidException, MaxRetriesReachedException{
		return new JobRetryCall<List<TaxonomyItem>, IdNotValidException>() {

			@Override
			protected List<TaxonomyItem> execute() throws ExternalRepositoryException, IdNotValidException {
				return plugin.getClassificationInterface().retrieveTaxonChildrenByTaxonId(id);
			}
		}.call();
	}

	private TaxonomyItem retrieveTaxonIdWithRetry(final String id, final AbstractPlugin plugin) throws IdNotValidException, MaxRetriesReachedException{
		return new JobRetryCall<TaxonomyItem, IdNotValidException>() {

			@Override
			protected TaxonomyItem execute() throws ExternalRepositoryException, IdNotValidException {
				return plugin.getClassificationInterface().retrieveTaxonById(id);
			}
		}.call();
	}
	

	@Override
	public int getCompletedEntries() {
		return completedEntries;
	}

	
}


