package org.gcube.portal.wssynclibrary.thredds;

import java.util.function.Function;

import org.gcube.portal.wssynclibrary.shared.thredds.Status;
import org.gcube.portal.wssynclibrary.shared.thredds.ThCatalogueBean;
import org.gcube.portal.wssynclibrary.shared.thredds.ThProcessDescriptor;
import org.gcube.portal.wssynclibrary.shared.thredds.ThProcessStatus;
import org.gcube.portal.wssynclibrary.shared.thredds.ThSyncFolderDescriptor;
import org.gcube.portal.wssynclibrary.shared.thredds.ThSynchFolderConfiguration;
import org.gcube.usecases.ws.thredds.engine.impl.ProcessDescriptor;
import org.gcube.usecases.ws.thredds.engine.impl.ProcessStatus;
import org.gcube.usecases.ws.thredds.model.SyncFolderDescriptor;
import org.gcube.usecases.ws.thredds.model.SynchFolderConfiguration;
import org.gcube.usecases.ws.thredds.model.gui.CatalogBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




// TODO: Auto-generated Javadoc
/**
 * The Class ThreddsConverter.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 14, 2018
 */
public class ThreddsConverter {

	public static final String THREDDS_FILTER_NC_NCML_ASC = "*.nc,*.ncml,*.asc";

	private static Logger logger = LoggerFactory.getLogger(ThreddsConverter.class);

	/** The to S sync folder descriptor. */
	public static Function<SyncFolderDescriptor, ThSyncFolderDescriptor> toThSyncFolderDescriptor = new Function<SyncFolderDescriptor, ThSyncFolderDescriptor>() {

	    public ThSyncFolderDescriptor apply(SyncFolderDescriptor t) {
	    	ThSyncFolderDescriptor mySync = new ThSyncFolderDescriptor();
	    	if(t==null) {
	    		logger.info("Input "+SyncFolderDescriptor.class.getSimpleName()+" is null, returning empty "+ThSyncFolderDescriptor.class.getSimpleName());
	    		return mySync;
	    	}

	    	mySync.setFolderId(t.getFolderId());
	    	mySync.setFolderPath(t.getFolderPath());
	    	mySync.setLocked(t.isLocked());
	    	ThProcessDescriptor localProcessDescriptor = toThProcessDescriptor.apply(t.getLocalProcessDescriptor());
			mySync.setLocalProcessDescriptor(localProcessDescriptor);
			ThSynchFolderConfiguration configuration = toThSynchFolderConfiguration.apply(t.getConfiguration());
			mySync.setConfiguration(configuration);

	        return mySync;
	    }
	};

	/** The to S sync folder descriptor. */
	public static Function<ThSyncFolderDescriptor, SyncFolderDescriptor> toSyncFolderDescriptor = new Function<ThSyncFolderDescriptor, SyncFolderDescriptor>() {

	    public SyncFolderDescriptor apply(ThSyncFolderDescriptor t) {

	    	if(t==null) {
	    		logger.warn("Input "+ThSyncFolderDescriptor.class.getSimpleName()+" is null, returning null");
	    		return null;
	    	}

	    	SynchFolderConfiguration configuration = toSynchFolderConfiguration.apply(t.getConfiguration());
			return new SyncFolderDescriptor(t.getFolderId(), t.getFolderPath(), configuration);
	    }
	};

	/** The to synch folder configuration. */
	public static Function<ThSynchFolderConfiguration, SynchFolderConfiguration> toSynchFolderConfiguration = new Function<ThSynchFolderConfiguration, SynchFolderConfiguration>() {

	    public SynchFolderConfiguration apply(ThSynchFolderConfiguration t) {

	    	if(t==null) {
	    		logger.warn("Input "+ThSynchFolderConfiguration.class.getSimpleName()+" is null, returning null");
	    		return null;
	    	}

	    	String filter = t.getFilter()!=null && !t.getFilter().isEmpty()?t.getFilter():THREDDS_FILTER_NC_NCML_ASC;
	    	return new SynchFolderConfiguration(t.getRemotePath(), filter, t.getTargetToken(), t.getToCreateCatalogName(), t.getRootFolderId());
	    }
	};


	/** The to S synch folder configuration. */
	public static Function<SynchFolderConfiguration, ThSynchFolderConfiguration> toThSynchFolderConfiguration = new Function<SynchFolderConfiguration, ThSynchFolderConfiguration>() {

	    public ThSynchFolderConfiguration apply(SynchFolderConfiguration t) {
	    	ThSynchFolderConfiguration mySync = new ThSynchFolderConfiguration();
	    	if(t==null) {
	    		logger.info("Input "+SynchFolderConfiguration.class.getSimpleName()+" is null, returning empty "+ThSynchFolderConfiguration.class.getSimpleName());
	    		return mySync;
	    	}
	    	mySync.setFilter(t.getFilter());
	    	mySync.setRemotePath(t.getRemotePath());
	    	mySync.setRemotePersistence(t.getRemotePersistence());
	    	mySync.setTargetToken(t.getTargetToken());
	    	mySync.setToCreateCatalogName(t.getToCreateCatalogName());
	    	mySync.setRootFolderId(t.getRootFolderId());
	        return mySync;
	    }
	};


	/** The to S sync folder descriptor. */
	public static Function<ProcessDescriptor, ThProcessDescriptor> toThProcessDescriptor = new Function<ProcessDescriptor, ThProcessDescriptor>() {

	    public ThProcessDescriptor apply(ProcessDescriptor t) {
	    	ThProcessDescriptor mySync = new ThProcessDescriptor();
	    	if(t==null) {
	    		logger.info("Input "+ProcessDescriptor.class.getSimpleName()+" is null, returning empty "+ThProcessDescriptor.class.getSimpleName());
	    		return mySync;
	    	}

	    	mySync.setFolderId(t.getFolderId());
	       	mySync.setFolderPath(t.getFolderPath());
	       	mySync.setProcessId(t.getProcessId());
	       	ThSynchFolderConfiguration fc = toThSynchFolderConfiguration.apply(t.getSynchConfiguration());
	       	mySync.setSynchConfiguration(fc);
	        return mySync;
	    }
	};


	/** The to th catalogue bean. */
	public static Function<CatalogBean, ThCatalogueBean> toThCatalogueBean = new Function<CatalogBean, ThCatalogueBean>() {

		@Override
		public ThCatalogueBean apply(CatalogBean t) {

			if(t==null)
				return null;

			return new ThCatalogueBean(t.getName(), t.getPath(), t.getIsDefault());
		}
	};


	/** The to th process status. */
	public static Function<ProcessStatus, ThProcessStatus> toThProcessStatus = new Function<ProcessStatus, ThProcessStatus>() {

	    public ThProcessStatus apply(ProcessStatus t) {
	    	ThProcessStatus mySync = new ThProcessStatus();
	    	if(t==null) {
	    		logger.info("Input "+ProcessStatus.class.getSimpleName()+" is null, returning empty "+ThProcessStatus.class.getSimpleName());
	    		return mySync;
	    	}

	    	mySync.setCurrentMessage(t.getCurrentMessage());
	    	mySync.setPercentCompleted(t.getPercent());

	    	if(t.getErrorCount()!=null)
	    		mySync.setErrorCount(t.getErrorCount().get());
	    	if(t.getLogBuilder()!=null)
	    		mySync.setLogBuilder(t.getLogBuilder().toString());
	    	if(t.getQueuedTransfers()!=null)
	    		mySync.setQueuedTransfers(t.getQueuedTransfers().get());
	    	if(t.getServedTransfers()!=null)
	    		mySync.setServedTransfers(t.getServedTransfers().get());

	    	if(t.getStatus()!=null) {

		    	switch (t.getStatus()) {
				case COMPLETED:
					mySync.setStatus(Status.COMPLETED);
					break;
				case INITIALIZING:
					mySync.setStatus(Status.INITIALIZING);
					break;
				case ONGOING:
					mySync.setStatus(Status.ONGOING);
					break;
				case WARNINGS:
					mySync.setStatus(Status.WARNINGS);
					break;
				case STOPPED:
					mySync.setStatus(Status.STOPPED);
					break;

				default:
					break;
				}
	    	}
	    	//mySync.setStatus(t.getStatus());
	        return mySync;
	    }
	};



}
