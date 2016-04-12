package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl;

import java.util.ArrayList;

import org.gcube.common.clients.fw.queries.StatefulQuery;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.streams.Stream;
import org.gcube.data.tml.proxies.TReader;
import org.gcube.data.tml.proxies.TServiceFactory;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.io.XMLBindings;
import org.gcube.data.trees.patterns.Patterns;
import org.gcube.datatransformation.datatransformationlibrary.DTSCore;
import org.gcube.datatransformation.datatransformationlibrary.DTSScope;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This {@link DataSource} fetches {@link Tree}s from Tree Manager.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class TMDataSource implements DataSource, ContentTypeDataSource {

	private String treeCollectionID;

	private DataBridge bridge = DTSCore.getDataBridge();

	private static Logger log = LoggerFactory.getLogger(TMDataSource.class);

	private Stream<Tree> treesReader;

	private volatile boolean finished = false;
	private Object sync = new Object();

	private MemoryFileBackedQueue<Record> queue;

	/**
	 * This constructor for {@link TMDataSource}
	 * 
	 * @param input
	 *            The input value of the {@link DataSource}.
	 * @param inputParameters
	 *            The output parameters of the <tt>DataSource</tt>.
	 */
	public TMDataSource(String input, Parameter[] inputParameters) {
		treeCollectionID = input;

		String scope = null;
		if ((scope = DTSScope.getScope()) != null) {
			ScopeProvider.instance.set(scope);
		}

		log.debug("Going to fetch objects from tree manager with id: " + treeCollectionID + " under scope: " + scope);

		StatefulQuery query = TServiceFactory.readSource().withId(treeCollectionID).build();
		TReader treader = TServiceFactory.reader().matching(query).build();

		treesReader = treader.get(Patterns.tree());

		new Thread() {
			public void run() {
				this.setName("TMDataSource");

				try {
					queue = new MemoryFileBackedQueue<Record>(100);
					
					new Thread() {
						public void run() {
							this.setName("TMDataSource Retriever");
							log.info("File backed queue created for storing trees");
							int i = 0;
							Record rec;
							try {								
								while (treesReader.hasNext()) {
									log.trace("going to fetch tree #" + (i+1));
									Tree t = treesReader.next();
									i++;
									if ((i % 100) == 0)
										log.debug("fetched " + i + " trees.");
									log.trace("tree #" + i + " retrived");
									
									rec = new Record(t.uri().toASCIIString(), XMLBindings.toString(t), null);
									if (!queue.offer(rec)) {
										log.error("Could not store retrieved tree");
										break;
									}
									
									synchronized (sync) {
										sync.notify();
									}
								}
								treesReader.close();
							} catch (Exception e) {
								log.info("An unexpected error occured while reading the stream from tm client", e);
								synchronized (sync) {
									bridge.append(new DTSExceptionWrapper(e));
									finished = true;
									sync.notify();
								}
								return;
							}
							
							log.info("Finished retrieving objects from initial source");
							synchronized (sync) {
								finished = true;
								sync.notify();
							}
						}
					}.start();

					Record rec;
					outer: while ((!finished || queue.size() > 0)  && !bridge.isClosed()) {
						synchronized (sync) {
							while(queue.size() == 0 && !finished) {
								sync.wait();
								if (finished)
									continue outer;
							}
						}
						rec = queue.poll();
						if (rec == null) {
							log.warn("Queue returned null");
							continue;
						}
						
						manageObject(rec.getId(), rec.getPayload());
					}


				} catch (Exception e) {
					log.error("Did not manage to fetch content from tree manager", e);
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
			log.error("Could not manage to fetch next object's content type", e);
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

	private void manageObject(String id, String payload) {
		try {
			StrDataElement object = StrDataElement.getSourceDataElement();
			object.setId(id);
			object.setContent(payload);

			object.setAttribute(DataHandlerDefinitions.ATTR_COLLECTION_ID, treeCollectionID);
			object.setAttribute(DataHandlerDefinitions.ATTR_CONTENT_OID, id);

			object.setContentType(new ContentType("application/xml", new ArrayList<Parameter>()));

			bridge.append(object);
			log.trace("Object with id " + id + " was added for processing by TM");
			ReportManager.manageRecord(id, "Object with id " + id + " was added for processing by TM", Status.SUCCESSFUL, Type.SOURCE);
		} catch (Exception e) {
			log.error("Could not manage to fetch the object " + id, e);
			ReportManager.manageRecord(id, "Object with id " + id + " could not be fetched TM", Status.FAILED, Type.SOURCE);
		}
	}

//	public static void main(String[] args) throws Exception {
////		Thread.sleep(5000);
//		String scope;
////		scope = "/gcube/devNext";
//		scope = "/d4science.research-infrastructures.eu/FARM";
////		scope = "/d4science.research-infrastructures.eu/gCubeApps";
//
//		ScopeProvider.instance.set(scope);
//		DTSScope.setScope(scope);
//		String input;
//		// input= "ijoat:OTHR";
////		input = "553e9014-fd4f-45fd-868e-07834c55b83b";
////		input = "7374617475733D756E707562";
////		input = "OpenAIRE";
//		input= "ZooKeys";
//		DataSource ds = new TMDataSource(input, null);
//		ArrayList<Integer> list = new ArrayList<Integer>();
//		int i = 0;
//		while (ds.hasNext()) {
//			StringWriter writer = new StringWriter();
//			DataElement de = ds.next();
//			IOUtils.copy(de.getContent(), writer, "UTF-8");
//			String theString = writer.toString();
//
//			System.out.println(++i + ": " + de.getId());
//			list.add(new Integer(theString.length()));
////			System.out.println(theString);
//		}
//		System.out.println("Total: " + i);
//		Collections.sort(list);
//		Collections.reverse(list);
//		int topNum = 10;
//		for (int j = 0; j < (list.size() < topNum? list.size() : topNum) ; j++)
//			System.out.println(list.get(j));
//		// FtsRowset_Transformer transformer = new FtsRowset_Transformer();
//
//		 Thread.sleep(3600 * 1000);
//		List<DataSource> sources = new ArrayList<DataSource>();
//		sources.add(ds);
//
//		while (ds.hasNext())
//			System.out.println(RSDataElementUtil.stringFromInputStream(ds.next().getContent()));
//
//		Thread.sleep(3600 * 1000);
//		List<Parameter> programParameters = new ArrayList<Parameter>();
//		// programParameters.add(new Parameter("xslt",
//		// "bb099010-f2c8-11dd-99ef-cbe8b682b1c1"));
//		// programParameters.add(new Parameter("finalftsxslt",
//		// "821167b0-8b78-11e0-a9c6-9c00829f1447"));
//		programParameters.add(new Parameter("xslt:1", "$BrokerXSLT_DwC-A_anylanguage_to_ftRowset_anylanguage"));
//		programParameters.add(new Parameter("xslt:2", "$BrokerXSLT_TAXONOMY_anylanguage_to_ftRowset_anylanguage"));
//		programParameters.add(new Parameter("xslt:3", "$BrokerXSLT_PROVENANCE_anylanguage_to_ftRowset_anylanguage"));
//		programParameters.add(new Parameter("finalftsxslt", "$BrokerXSLT_wrapperFT"));
//		programParameters.add(new Parameter("indexType", "haha_2.0"));
//
//		ContentType targetContentType = new ContentType();
//		targetContentType.setMimeType("text/xml");
//		targetContentType.setContentTypeParameters(Arrays.asList(new Parameter[] { new Parameter("schemaURI", "http://ftrowset.xsd") }));
//
//		PathDataSink sink = new PathDataSink("/home/jgerbe/testArea/sink", null);
//		// transformer.transform(sources, programParameters, targetContentType,
//		// sink);
//		sink.getOutput();
//		Thread.sleep(60 * 60 * 1000);
//	}
}
