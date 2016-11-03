package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataBridge;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.reports.ReportManager;
import org.gcube.datatransformation.datatransformationlibrary.reports.Record.Status;
import org.gcube.datatransformation.datatransformationlibrary.reports.Record.Type;

/**
 * @author Dimitris Katris, NKUA
 *
 * {@link DataBridge} which filters the {@link DataElement}s that are appended to it depending their {@link ContentType}.
 */
public class FilterDataBridge extends REFDataBridge {
	
	/**
	 * The source from which the <tt>FilterDataBridge</tt> retrieves {@link DataElement}s.
	 */
	protected DataSource source=null;
	
	/**
	 * The <tt>ContentType</tt> with which the <tt>FilterDataBridge</tt> checks the elements of the source {@link DataSource}. 
	 */
	protected ContentType filterContentType;
	
	/**
	 * Logs operations performed by <tt>FilterDataBridge</tt> class.
	 */
	private static Logger log = LoggerFactory.getLogger(FilterDataBridge.class);
	
	/**
	 * Creates a new <tt>FilterDataBridge</tt>.
	 * 
	 * @param source The source from which the <tt>FilterDataBridge</tt> retrieves {@link DataElement}s.
	 * @param filterContentType The <tt>ContentType</tt> with which the <tt>FilterDataBridge</tt> checks the elements of the source {@link DataSource}.
	 * @throws Exception If source or filterContentType are not properly set.
	 */
	public FilterDataBridge(DataSource source, ContentType filterContentType) throws Exception {
		super();
		this.source = source;
		this.filterContentType = filterContentType;
		log.debug("Filtering will be performed by content type "+filterContentType.toString());
		startFiltering();
	}

	/**
	 * Starts running the filtering thread.
	 * 
	 * @throws Exception If source or filterContentType are not properly set.
	 */
	private void startFiltering() throws Exception {
		if(this.source==null || this.filterContentType==null){
			throw new Exception("Data Source or FilterContentFormat is null");
		}
		FilterThread fthread = new FilterThread();
		fthread.filterbridge=this;
		fthread.start();
	}
}

/**
 * @author Dimitris Katris, NKUA
 * 
 * Thread class which performs the filtering.
 */
class FilterThread extends Thread{
	
	/**
	 * The <tt>FilterDataBridge</tt> on which the filtering is performed.
	 */
	protected FilterDataBridge filterbridge;
	
	/**
	 * Logs operations performed by <tt>FilterThread</tt> class.
	 */
	private static Logger log = LoggerFactory.getLogger(FilterThread.class);
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run(){
		try{
			while(filterbridge.source.hasNext()){
				try {
					DataElement object = filterbridge.source.next();
					if(object!=null){
						log.debug("Filtering object with id "+object.getId()+" and content type "+object.getContentType().toString());
						if(object.getContentType().equals(filterbridge.filterContentType)
								|| ContentType.support(filterbridge.filterContentType, object.getContentType())
								|| ContentType.gensupport(filterbridge.filterContentType, object.getContentType())){
							log.debug("Object with ID "+object.getId()+" NOT filtered");
							ReportManager.manageRecord(object.getId(), "Object with id "+object.getId()+" and content type "+object.getContentType().toString()+" passed the filter", Status.SUCCESSFUL, Type.FILTER);
							filterbridge.append(object);
						}else{
							log.debug("Object with ID "+object.getId()+" FILTERED");
							ReportManager.manageRecord(object.getId(), "Object with id "+object.getId()+" and content type "+object.getContentType().toString()+" was filtered", Status.FAILED, Type.FILTER);
						}
					}
				} catch (Exception e) {
					log.debug("Error in filtering an object, continuing...",e);
				}
			}
			log.debug("Filtering finished successfully");
		} catch (Exception e){
			log.error("Undefined exception in filtering the data source",e);
		} finally {
			filterbridge.close();
		}
	}
}