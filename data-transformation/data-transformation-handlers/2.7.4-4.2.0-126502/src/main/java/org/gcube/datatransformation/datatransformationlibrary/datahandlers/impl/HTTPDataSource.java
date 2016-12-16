package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.gcube.datatransformation.datatransformationlibrary.DTSCore;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DTSExceptionWrapper;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.impl.StrDataElement;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.ContentTypeDataSource;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataBridge;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandlerDefinitions;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.gcube.datatransformation.datatransformationlibrary.reports.Record.Status;
import org.gcube.datatransformation.datatransformationlibrary.reports.Record.Type;
import org.gcube.datatransformation.datatransformationlibrary.reports.ReportManager;
import org.gcube.datatransformation.datatransformationlibrary.utils.queue.MemoryFileBackedQueue;
import org.gcube.datatransformation.datatransformationlibrary.utils.stax.StaxReader;
import org.gcube.datatransformation.datatransformationlibrary.utils.stax.StaxResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This {@link DataSource} fetches records through HTTP protocol with XML
 * structure.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class HTTPDataSource implements DataSource, ContentTypeDataSource {
	private static Logger log = LoggerFactory.getLogger(HTTPDataSource.class);

	private static final String COLLECTION = "collection";
	private static final String NAME = "name";
	private static final String PROVENANCE = "provenance";
	private static final String TIMESTAMP = "timestamp";
	private static final String RECORDS = "records";
	private static final String RECORD = "record";
	private static final String ID = "id";
	private static final String FIELDS = "fields";
	private static final String FIELD = "field";
	private static final String MIMETYPE = "mimetype";
	private static final String PAYLOAD = "payload";

	private static final String COLLNAMEPATH = "/" + COLLECTION + "/" + NAME;
	private static final String COLLPROVENANCEPATH = "/" + COLLECTION + "/" + PROVENANCE;
	private static final String COLLTIMESTAMPPATH = "/" + COLLECTION + "/" + TIMESTAMP;
	private static final String RECORDIDPATH = "/" + COLLECTION + "/" + RECORDS + "/" + RECORD + "/" + ID;
	private static final String FIELDNAMEPATH = "/" + COLLECTION + "/" + RECORDS + "/" + RECORD + "/" + FIELDS + "/" + FIELD + "/" + NAME;
	private static final String FIELDMIMETYPEPATH = "/" + COLLECTION + "/" + RECORDS + "/" + RECORD + "/" + FIELDS + "/" + FIELD + "/" + MIMETYPE;
	private static final String FIELDPAYLOADPATH = "/" + COLLECTION + "/" + RECORDS + "/" + RECORD + "/" + FIELDS + "/" + FIELD + "/" + PAYLOAD;

	/**
	 * 
	 */
	public enum CollPaths {
		COLLNAMEPATH, COLLPROVENANCEPATH, COLLTIMESTAMPPATH, RECORDIDPATH, FIELDNAMEPATH, FIELDMIMETYPEPATH, FIELDPAYLOADPATH
	}

	private Map<String, String> pathsMap;

	private DataBridge bridge = DTSCore.getDataBridge();

	private URL url;
	private String collectionID;
	private String provenance;
	private String timestamp;

	private volatile boolean finished = false;
	private Object sync = new Object();
	private Object waitInit = new Object();

	private MemoryFileBackedQueue<Serializable> queue;

	private void init() {
		pathsMap = new HashMap<String, String>();

		pathsMap.put(CollPaths.COLLNAMEPATH.toString(), COLLNAMEPATH);
		pathsMap.put(CollPaths.COLLPROVENANCEPATH.toString(), COLLPROVENANCEPATH);
		pathsMap.put(CollPaths.COLLTIMESTAMPPATH.toString(), COLLTIMESTAMPPATH);
		pathsMap.put(CollPaths.RECORDIDPATH.toString(), RECORDIDPATH);
		pathsMap.put(CollPaths.FIELDNAMEPATH.toString(), FIELDNAMEPATH);
		pathsMap.put(CollPaths.FIELDMIMETYPEPATH.toString(), FIELDMIMETYPEPATH);
		pathsMap.put(CollPaths.FIELDPAYLOADPATH.toString(), FIELDPAYLOADPATH);
	}

	private StaxReader staxReader;

	/**
	 * This constructor for {@link HTTPDataSource}
	 * 
	 * @param input
	 *            The input value of the {@link DataSource}.
	 * @param inputParameters
	 *            The output parameters of the <tt>DataSource</tt>.
	 * @throws IOException
	 *             if source is not available
	 * @throws XMLStreamException
	 *             if content is corrupted
	 */
	public HTTPDataSource(String input, Parameter[] inputParameters) throws XMLStreamException, IOException {
		url = new URL(input);
		log.debug("Going to fetch objects from url location: " + url);

		init();

		if (inputParameters != null)
			for (Parameter par : inputParameters) {
				if (pathsMap.containsKey(par.getName())) {
					if (par.getValue().startsWith("/"))
						pathsMap.put(par.getName(), par.getValue());
					else {
						if (par.getName().equals(CollPaths.COLLNAMEPATH.toString()))
							collectionID = par.getValue();
						else if (par.getName().equals(CollPaths.COLLPROVENANCEPATH.toString()))
							provenance = par.getValue();
						else if (par.getName().equals(CollPaths.COLLTIMESTAMPPATH.toString()))
							timestamp = par.getValue();
					}
				}

			}

		staxReader = new StaxReader(new HashSet<String>(pathsMap.values()), url);

		new Thread() {
			public void run() {
				this.setName("HTTPDataSource");

				try {
					queue = new MemoryFileBackedQueue<Serializable>(100);

					new Thread() {
						public void run() {
							this.setName("HTTPDataSourceRetriever");
							log.info("File backed queue created for storing input");
							int i = 0;
							Record rec = new Record();
							while (staxReader.hasNext()) {
								StaxResponse resp = staxReader.next();

								if (resp == null) {
									if (staxReader.hasNext())
										log.warn("received null... continue");
									continue;
								}

								// Determine how to manage returned data

								// Collection related
								if (collectionID == null && resp.getPath().equals(pathsMap.get(CollPaths.COLLNAMEPATH.toString()))) {
									synchronized (waitInit) {
										collectionID = resp.getResult();
										waitInit.notify();
										synchronized (sync) {
											sync.notify();
										}
									}
								}
								if (provenance == null && resp.getPath().equals(pathsMap.get(CollPaths.COLLPROVENANCEPATH.toString()))) {
									synchronized (waitInit) {
										provenance = resp.getResult();
										waitInit.notify();
										synchronized (sync) {
											sync.notify();
										}
									}
								}
								if (timestamp == null && resp.getPath().equals(pathsMap.get(CollPaths.COLLTIMESTAMPPATH.toString()))) {
									synchronized (waitInit) {
										timestamp = resp.getResult();
										waitInit.notify();
										synchronized (sync) {
											sync.notify();
										}
									}
								}

								// Records related
								if (resp.getPath().equals(pathsMap.get(CollPaths.RECORDIDPATH.toString()))) {
									rec.setId(resp.getResult());
								}
								if (resp.getPath().equals(pathsMap.get(CollPaths.FIELDMIMETYPEPATH.toString()))) {
									rec.setMimetype(resp.getResult());
								}
								if (resp.getPath().equals(pathsMap.get(CollPaths.FIELDPAYLOADPATH.toString()))) {
									rec.setPayload(resp.getResult());
								}

								if (!rec.isInitialised())
									continue;

								i++;
								if ((i % 100) == 0)
									log.debug("fetched " + i + " records.");
								log.trace("got record #" + i);

								if (!queue.offer(rec)) {
									log.error("Could not store retrieved record");
									break;
								}
								rec = new Record();

								synchronized (sync) {
									sync.notify();
								}
							}

							log.info("Finished retrieving objects from initial source");
							synchronized (sync) {
								finished = true;
								sync.notify();
							}
							staxReader.close();
						}
					}.start();

					// Wait until collections basic info is parsed
					synchronized (waitInit) {
						// XXX make prov and timestamp optional ?
						while (collectionID == null || provenance == null || timestamp == null)
							waitInit.wait();
					}

					Serializable ser;
					int i = 0;
					outer: while ((!finished || queue.size() > 0) && !bridge.isClosed()) {
						synchronized (sync) {
							while (!finished && queue.size() == 0) {
								sync.wait();
								if (finished)
									continue outer;
							}
						}
						Record rec = null;
						ser = queue.poll();
						if (ser instanceof Record)
							rec = (Record) ser;
						else if (ser instanceof DTSExceptionWrapper) {
							bridge.append(((DTSExceptionWrapper)ser));
							continue;
						}
						
						if (rec == null) {
							log.warn("Queue returned null");
							continue;
						} else
							log.debug("retrieved record #" + ++i);

						manageObject(rec.getId(), rec.getMimetype(), rec.getPayload());
					}

				} catch (Exception e) {
					log.error("Did not manage to fetch content from cms", e);
				} finally {
					log.info("Removing queue.");
					queue.destroy();
					bridge.close();
				}
			}
		}.start();
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#close()
	 */
	@Override
	public void close() {
		bridge.close();
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#isClosed()
	 * @return true if the {@link TReader} has been closed. Otherwise false.
	 */
	@Override
	public boolean isClosed() {
		return bridge.isClosed();
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.ContentTypeDataSource#nextContentType()
	 * @return next DataElement content type. application.xml in any case.
	 */
	@Override
	public ContentType nextContentType() {
		DataElement de = null;
		try {
			de = bridge.next();
		} catch (Exception e) {
			log.error("error while retrieving content type", e);
		}

		return de == null ? null : de.getContentType();
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource#hasNext()
	 * @return true only if TReader has more trees to read. Otherwise false.
	 */
	@Override
	public boolean hasNext() {
		return bridge.hasNext();
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource#next()
	 * @return next DataElement
	 * @throws Exception 
	 */
	@Override
	public DataElement next() throws Exception {
		return bridge.next();
	}

	private void manageObject(String id, String mimetype, String payload) {
		try {
			StrDataElement object = StrDataElement.getSourceDataElement();
			object.setId(id);
			object.setContent(payload);

			object.setAttribute(DataHandlerDefinitions.ATTR_COLLECTION_ID, collectionID);
			object.setAttribute(DataHandlerDefinitions.ATTR_CONTENT_OID, id);

			object.setContentType(new ContentType(mimetype, new ArrayList<Parameter>()));

			log.trace("Object with id " + id + " will be added for processing by HTTP source");
			bridge.append(object);
			ReportManager.manageRecord(id, "Object with id " + id + " was added for processing", Status.SUCCESSFUL, Type.SOURCE);
		} catch (Exception e) {
			log.error("Could not manage to fetch the object " + id, e);
			ReportManager.manageRecord(id, "Object with id " + id + " could not be fetched", Status.FAILED, Type.SOURCE);
		}
	}

//	public static void main(String[] args) throws Exception {
//		DTSScope.setScope("/gcube/devNext");
//		int i = 0;
//		HTTPDataSource ds = new HTTPDataSource("http://dionysus.di.uoa.gr:8080/aslHarvestersHttpDB/HarvestDatabase?sourcename=DionysusDB&propsname=CountriesTree", new Parameter[] {});
//		while (!ds.isClosed() || ds.hasNext()) {
//			StrDataElement de = (StrDataElement) ds.next();
//			if (de == null) {
//				System.out.println("\nnull");
//				continue;
//			}
//
////			System.out.print(".");
////			if ((++i) % 100 == 0) {
//				System.out.println(i);
//				System.out.println("id: " + de.getId());
//				System.out.println("mimetype: " + de.getContentType().getMimeType());
//				System.out.println("content: " + de.getStringContent());
//				System.out.println("Attibutes: " + de.getAllAttributes());
//				System.out.println("-------------------------------------------");
////			}
//		}
//		System.out.println("Total records: " + i);
//	}
}
