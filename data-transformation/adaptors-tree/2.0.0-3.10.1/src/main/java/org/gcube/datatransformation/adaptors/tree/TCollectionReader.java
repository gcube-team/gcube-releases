package org.gcube.datatransformation.adaptors.tree;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.UUID;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.gcube.common.clients.fw.queries.StatefulQuery;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.streams.Stream;
import org.gcube.data.tml.proxies.TReader;
import org.gcube.data.tml.proxies.TServiceFactory;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.io.XMLBindings;
import org.gcube.data.trees.patterns.Patterns;
import org.gcube.datatransformation.adaptors.tree.tools.TimeTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TCollectionReader {

	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(TCollectionReader.class);
	
	private String collectionID;
	private String collectionName = null;
	private String scope;
	private Object sync = new Object();
//	MemoryFileBackedQueue<Record> queue;
	boolean finished; //flag to determine if reading is finished
	String seperator = UUID.randomUUID().toString();
	StatefulQuery query;
	Stream <Tree> treesReader;
	TReader treader;
	private XMLOutputFactory factory;
	private XMLStreamWriter writer;

//	treeCollectionID -> "78e01c59-35b9-48ae-b20d-ebf6d403670e";  //Nature tree collection 
	
	
	public TCollectionReader(String collectionID, String scope) throws IOException{
		this.collectionID = collectionID;
		this.scope = scope;
		ScopeProvider.instance.set(scope);
		finished = false;
//		queue = new MemoryFileBackedQueue<Record>(100000);
		query = TServiceFactory.readSource().withId(collectionID).build();
		treader = TServiceFactory.reader().matching(query).build();
		treesReader = treader.get(Patterns.tree());
		seperator = UUID.randomUUID().toString();
		factory = XMLOutputFactory.newInstance();
	}
	
	public TCollectionReader(String collectionID, String scope, String collectionName) throws IOException{
		this.collectionID = collectionID;
		this.collectionName = collectionName;
		this.scope = scope;
		ScopeProvider.instance.set(scope);
		finished = false;
//		queue = new MemoryFileBackedQueue<Record>(100000);
		query = TServiceFactory.readSource().withId(collectionID).build();
		treader = TServiceFactory.reader().matching(query).build();
		treesReader = treader.get(Patterns.tree());
		seperator = UUID.randomUUID().toString();
		factory = XMLOutputFactory.newInstance();
	}
	

	public void readPrintCollections(Writer out) throws XMLStreamException, IOException {

		try {
			
			writer = factory.createXMLStreamWriter(out);

			writer.writeStartDocument();
			writer.writeStartElement("collection");

			writer.writeStartElement("name");
			if ((collectionName==null)||(collectionName.isEmpty())||(collectionName.equals("")))
				writer.writeCharacters(collectionID);
			else
				writer.writeCharacters(collectionName);
			writer.writeEndElement();

			writer.writeStartElement("provenance");
			writer.writeCharacters("Tree Collection");
			writer.writeEndElement();

			writer.writeStartElement("timestamp");
			writer.writeCharacters(TimeTools.getCurrentTimestamp());
			writer.writeEndElement();

			writer.writeStartElement("records");
			
			int i=0;
			while (treesReader.hasNext()) {
				Tree t = treesReader.next();
				writer.writeStartElement("record");
				writer.writeStartElement("id");
				writer.writeCharacters(t.uri().toASCIIString());
				writer.writeEndElement();
				writer.writeStartElement("fields");
				writer.writeStartElement("field");
				writer.writeStartElement("name");
				writer.writeCharacters("payload");
				writer.writeEndElement();
				writer.writeStartElement("mimetype");
				writer.writeCharacters("text/xml");
				writer.writeEndElement();
				writer.writeStartElement("payload");
				writer.writeCData(XMLBindings.toString(t));
				writer.writeEndElement();
				writer.writeEndElement();//field element
				writer.writeEndElement();//fields element
				writer.writeEndElement();//record element
				out.flush();
				logger.debug("Wrote tree #"+(++i)+" on the output");
			}
			writer.writeEndElement();//records element
			writer.writeEndElement();//collection element
			out.flush();
		} catch (XMLStreamException e) {
			logger.error("Did not manage to write content to output" + e);
		} catch (IOException e){
			System.out.println("IOException occurred during writing to output, closing output stream and returning!");
			out.close();
			return;
		}
		
		out.flush();
		out.close();

	}
	
	public void readPrintCollectionsProfiling(final PrintWriter out) {
		long start = System.currentTimeMillis();
		try {		
			int i=0;
			while (treesReader.hasNext()) {
				treesReader.next();
				logger.debug("Read tree #"+(++i)+"\tTime Elapsed: "+(long)(System.currentTimeMillis()-start)+"ms");
//				System.out.println("Read tree #"+(++i)+"\tTime Elapsed: "+(long)(System.currentTimeMillis()-start)+"ms");
			}
		} catch (Exception e) {
			System.out.println("Did not manage to fetch content from IS" + e);
		}

	}
	
	
	
	/**
	 * Adds all tree objects from the Tree Collection into the FileMemoryQueue and then writes them on the PrintReader. 
	 */
	/*
	public void readPrintCollectionsQueued2(final PrintWriter out) throws IOException {
		
//		ScopeProvider.instance.set(scope);

		finished = false;
		
		new Thread() {
			public void run() {
				this.setName("QueuedOutput");
				try {
//					final String seperator = UUID.randomUUID().toString();
					new Thread() {
						public void run() {
							Record rec;
							while (treesReader.hasNext()) {
								Tree t = treesReader.next();
								rec = new Record(t.uri().toASCIIString(), XMLBindings.toString(t));
								queue.offer(rec);
								synchronized (sync) {
									sync.notify();
								}
								System.out.println("Read a tree from the collection to queue");
							}
							
							synchronized (sync) {
								finished = true;
								sync.notify();
							}
						}
					}.start();
					
					writer = factory.createXMLStreamWriter(out);
					
					writer.writeStartDocument();
					writer.writeStartElement("collection");
					
					writer.writeStartElement("name");
					if ((collectionName==null)||(collectionName.isEmpty())||(collectionName.equals("")))
						writer.writeCharacters(collectionID);
					else
						writer.writeCharacters(collectionName);
					writer.writeEndElement();
					
					writer.writeStartElement("provenance");
					writer.writeCharacters("Tree Collection");
					writer.writeEndElement();
					
					writer.writeStartElement("timestamp");
					writer.writeCharacters(TimeTools.getCurrentTimestamp());
					writer.writeEndElement();
					
					writer.writeStartElement("records");
					
					Record rec;
					outer: while ((!finished || queue.size() >= 0)) {
						System.out.println("while");
						synchronized (sync) {
							while(queue.size() == 0) {
								sync.wait();
								if (finished)
									continue outer;
							}
						}
						rec = queue.poll();
						if (rec == null) {
							continue;
						}
						System.out.println("Retrieved a tree from the queue");
						
						writer.writeStartElement("record");
						writer.writeStartElement("id");
						writer.writeCharacters(rec.getId());
						writer.writeEndElement();
						writer.writeStartElement("fields");
						writer.writeStartElement("field");
						writer.writeStartElement("name");
						writer.writeCharacters("payload");
						writer.writeEndElement();
						writer.writeStartElement("mimetype");
						writer.writeCharacters("text/xml");
						writer.writeEndElement();
						writer.writeStartElement("payload");
						writer.writeCharacters(rec.getPayload());
						writer.writeEndElement();
						writer.writeEndElement();//field element
						writer.writeEndElement();//fields element
						writer.writeEndElement();//record element
						out.flush();
					}
					writer.writeEndElement();//records element
					writer.writeEndElement();//collection element
					out.flush();		
				} catch (Exception e) {
				} finally {
					queue.destroy();

				}
			}
		}.start();
	
	}
	 */

	
}






class Record implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String payload;
	
	public String getId() {
		return id;
	}
	
	public String getPayload() {
		return payload;
	}
	
	public Record(String id, String payload) {
		this.id = id;
		this.payload = payload;
	}
}