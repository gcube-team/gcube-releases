//package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.util.ArrayList;
//import java.util.UUID;
//
//import org.gcube.contentmanagement.contentmanager.stubs.calls.iterators.RemoteIterator;
//import org.gcube.contentmanagement.gcubedocumentlibrary.io.DocumentReader;
//import org.gcube.contentmanagement.gcubedocumentlibrary.projections.DocumentProjection;
//import org.gcube.contentmanagement.gcubedocumentlibrary.projections.Projections;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.GCubeDocument;
//import org.gcube.datatransformation.datatransformationlibrary.DTSCore;
//import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
//import org.gcube.datatransformation.datatransformationlibrary.dataelements.impl.DataElementImpl;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.ContentTypeDataSource;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataBridge;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandlerDefinitions;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource;
//import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
//import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
//import org.gcube.datatransformation.datatransformationlibrary.reports.Record.Status;
//import org.gcube.datatransformation.datatransformationlibrary.reports.Record.Type;
//import org.gcube.datatransformation.datatransformationlibrary.reports.ReportManager;
//import org.gcube.datatransformation.datatransformationlibrary.security.DTSSManager;
//import org.gcube.datatransformation.datatransformationlibrary.statistics.Metric;
//import org.gcube.datatransformation.datatransformationlibrary.statistics.StatisticsManager;
//import org.gcube.datatransformation.datatransformationlibrary.statistics.StatisticsManager.MetricType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * @author Dimitris Katris, NKUA
// *
// * <p>
// * This <tt>DataSource</tt> fetches <tt>DataElements</tt> from a content collection.
// * </p>
// */
//public class CMAggregateDataSource extends Thread implements DataSource, ContentTypeDataSource {
//
//	private String contentCollectionID;
//	private DocumentReader cmReader;
//	
//	private DataBridge bridge = DTSCore.getDataBridge();
//	private static Logger log = LoggerFactory.getLogger(CMAggregateDataSource.class);
//	private static Metric cmsDataSourceMetric = StatisticsManager.createMetric("CMSDataSourceMetric", "Time to retrieve object from CMS", MetricType.SOURCE);
//
//	/**
//	 * @param input The input value of the <tt>DataSource</tt>.
//	 * @param inputParameters The input parameters of the <tt>DataSource</tt>.
//	 * @throws Exception If data source could not be initialized
//	 */
//	public CMAggregateDataSource(String input, Parameter[] inputParameters) throws Exception {
//		log.debug("Going to fetch objects from collection with id: "+input + " in scope " + DTSSManager.getScope());
//		this.contentCollectionID=input;
//		
//		cmReader = new DocumentReader(contentCollectionID, DTSSManager.getScope(), DTSSManager.getSecurityManager());
//		
//		this.start();
//	}
//	
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource#hasNext()
//	 * @return true if the <tt>DataSource</tt> has more elements.
//	 */
//	public boolean hasNext() {
//		return bridge.hasNext();
//	}
//
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource#next()
//	 * @return the next element of the <tt>DataSource</tt>.
//	 */
//	public DataElement next() {
//		return bridge.next();
//	}
//	
//	/**
//	 * @see java.lang.Thread#run()
//	 */
//	public void run(){
//		File tempIDsStorage = null;
//		try {
//			String seperator = UUID.randomUUID().toString();
//			tempIDsStorage = File.createTempFile("DTS", ".tmp");
//			log.info("File storing gDoc IDs: " + tempIDsStorage.getName());
//		    BufferedWriter out = new BufferedWriter(new FileWriter(tempIDsStorage));
//			DocumentProjection dp = Projections.document().with(Projections.NAME);
//			RemoteIterator<GCubeDocument> documentIterator = cmReader.get(dp);
//			int i = 0;
//			while(documentIterator.hasNext()){
//				i++;
//				if ((i % 100) == 0)
//					log.info("Pre-fetched IDs for " + i +" docs.");
//				GCubeDocument document = documentIterator.next();
//			    out.write(document.id()+"\n");
//			    out.write(document.uri().toString()+"\n");
//			    out.write(document.name()+"\n");
//			    out.write(seperator+"\n");
//			    
//			}
//		    out.close();
//			log.info("Done prefetching IDs and staff...");
//		    
//		    String id, uri, name, str;
//		    boolean firstline =  true;
//		    BufferedReader in = new BufferedReader(new FileReader(tempIDsStorage));
//		    while (( id = in.readLine()) != null){
//		    	if (id.isEmpty())
//		    		break;
//		    	uri = in.readLine();
//		    	name = "";
//
//		    	str = in.readLine();
//		    	firstline = true;
//		    	while(!str.equals(seperator)){
//		    		if (!firstline)
//		    			name += "\n";
//		    		name += str;
//		    		firstline = false;
//		    		str = in.readLine();
//		    	}
//
//				manageObject(id, uri, name);
//	        }
//		    in.close();
//		    
//			log.info("Removing temp file.");
//		    tempIDsStorage.delete();
//		} catch (Exception e) {
//			log.error("Did not manage to fetch content from cms", e);
//		} finally {
//			bridge.close();
//		}
//	}
//	
//	private void manageObject(String id, String uri, String name){
//		try {
//			long startTime = System.currentTimeMillis();
//			DataElement object = DataElementImpl.getSourceDataElement();
//			object.setId(uri);
//			log.debug("Reading from CM gdoc with URL: "+uri);
//			object.setAttribute(DataHandlerDefinitions.ATTR_CONTENT_OID, id);
//			object.setAttribute(DataHandlerDefinitions.ATTR_METADATA_OID, id);
//			object.setAttribute(DataHandlerDefinitions.ATTR_DOCUMENT_NAME, name);
//			object.setAttribute(DataHandlerDefinitions.ATTR_COLLECTION_ID, contentCollectionID);
//
//			object.setContentType(new ContentType("application/xml", new ArrayList<Parameter>()));
//			
//			cmsDataSourceMetric.addMeasure(System.currentTimeMillis()-startTime);
//			if(object==null){throw new Exception();}
//			bridge.append(object);
//			ReportManager.manageRecord(id, "Object with id "+id+" was added for processing CMS", Status.SUCCESSFUL, Type.SOURCE);
//		} catch (Exception e) {
//			log.error("Could not manage to fetch the object "+uri,e);
//			ReportManager.manageRecord(id, "Object with id "+id+" could not be fetched by CMS", Status.FAILED, Type.SOURCE);
//		}
//	}
//	
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#close()
//	 */
//	public void close() {
//		bridge.close();
//	}
//
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#isClosed()
//	 * @return true if the <tt>DataHandler</tt> has been closed.
//	 */
//	public boolean isClosed() {
//		return bridge.isClosed();
//	}
//	
//	public ContentType nextContentType() {
//		DataElement de = bridge.next();
//		
//		return de == null? null : de.getContentType();
//	}
//	
////	public static void main(String[] args) throws Exception {
////		TCPConnectionManager.Init(new TCPConnectionManagerConfig("meteora.di.uoa.gr", new ArrayList<PortRange>(), true));
////
////		TCPConnectionManager.RegisterEntry(new ChannelTCPConnManagerEntry());
////		TCPConnectionManager.RegisterEntry(new TCPStoreConnectionHandler());
////		TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());
////
////		String input = "5b268db0-9d63-11de-8d8f-a04a2d1ca936";
////		DTSSManager.setScope("/gcube/devNext");
//////		ScopeProvider.instance.set("/gcube/devNext");
////
////		CMAggregateDataSource ds = new CMAggregateDataSource(input, null);
////		
////		while(ds.hasNext()) {
////			DataElement de = ds.next();
////			System.out.println("ID: " + de.getId());
////			System.out.println("Content: " + CMCLI.convertStreamToString(de.getContent()));
////		}
////		
////		Thread.sleep(60*60*1000);
////		DataSink sink = new GRS2DataSink(null, null);
////		final DataSource dest = new GRS2DataSource(sink.getOutput(), null);
////		new Thread() {
////			public void run() {
////				while(dest.hasNext()){
////					DataElement de = dest.next();
////					if (de == null)
////						System.out.println(de);
////					else
////						System.out.println(de.getId());
////				}
////			}
////		}.start();
////
////		
////		while(ds.hasNext()) {
////			DataElement de = ds.next();
////			if (de == null)
////				System.out.println("got null");
////			else
////				sink.append(de);
////		}
////	}
//}
