package gr.uoa.di.madgik.grs.proxy.http.mirror;

import gr.uoa.di.madgik.grs.GRS2Exception;
import gr.uoa.di.madgik.grs.buffer.GRS2BufferException;
import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.buffer.IBuffer.TransportDirective;
import gr.uoa.di.madgik.grs.buffer.IBuffer.TransportOverride;
import gr.uoa.di.madgik.grs.events.BufferEvent;
import gr.uoa.di.madgik.grs.events.BufferEvent.EventSource;
import gr.uoa.di.madgik.grs.proxy.mirror.GRS2ProxyMirrorInvalidOperationException;
import gr.uoa.di.madgik.grs.proxy.mirror.GRS2ProxyMirrorProtocolErrorException;
import gr.uoa.di.madgik.grs.proxy.mirror.IMirror;
import gr.uoa.di.madgik.grs.proxy.mirror.PartialRequestEntry;
import gr.uoa.di.madgik.grs.record.GRS2RecordDefinitionException;
import gr.uoa.di.madgik.grs.record.GRS2RecordSerializationException;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.registry.GRSRegistry;
import gr.uoa.di.madgik.grs.xml.XMLHelper;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This implementation of the {@link IMirror} interface specializes the
 * mirroring procedure that needs to be performed from the writer side of the
 * mirroring procedure. The technology employed is communication over HTTP connections.
 * The communication protocol is coupled with the one implemented by
 * the {@link HTTPReaderMirror} which is the writer side counterpart of this
 * class
 * 
 * @author Alex Antoniadis
 * 
 */
public class HTTPWriterMirror extends Thread implements IMirror {
	private static Logger logger = Logger.getLogger(HTTPWriterMirror.class.getName());
	private static final int TIMEOUT = 100000; //100 secs 

	private String key = null;
	private MirroringState state = MirroringState.Open;

	private Socket socket;
	private OutputStream out;
	private IBuffer buffer = null;
	private String request = null;

	private int readerNeeded = 0;
	private boolean doDispose = false;
	private PartialRequestEntry[] partials = null;
	
	public static final int dataformat = 0; //0 xml, 1 json
	
	
	private BlockingQueue<Request> requests =
		    new LinkedBlockingQueue<Request>(2);
	
	
	private class Request{
		protected String request;
		protected Socket socket;
		protected BufferedOutputStream out;
	};

	private Request req = null;
	
	/**
	 * Sets the local {@link GRSRegistry} key associated with the
	 * {@link IBuffer} that needs to be mirrored
	 * 
	 * @param key
	 *            the key associated with the served {@link IBuffer}
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.proxy.mirror.IMirror#getBuffer()
	 */
	public IBuffer getBuffer() {
		return this.buffer;
	}

	/**
	 * This invocation of this method starts a new execution thread where the
	 * mirroring procedure is executed. The created thread of execution is
	 * executed in the background and as a daemon service. Every new invocation
	 * pushes the request into a blocking queue in order to avoid contentions. 
	 * See {@link HTTPWriterMirror.run}
	 * 
	 * @throws GRS2ProxyMirrorInvalidOperationException
	 *             if the mirroring state does not allow the operation to be
	 *             executed
	 */
	public synchronized void handle(String request, Socket socket, BufferedOutputStream out) throws GRS2ProxyMirrorInvalidOperationException {
		if (this.key == null)
			throw new GRS2ProxyMirrorInvalidOperationException("No key defined");

		
		Request r = new Request();
		r.request = request;
		r.socket = socket;
		r.out = out;
		
		
		if (this.buffer != null){
			this.requests.add(r); // add it in the queue
		} else {
			req = r; // just pass it to run function (no queue required)
			this.setDaemon(true);
			this.setName("writer mirror (" + this.key + ")");
			this.start();
		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This method actually invokes the respective
	 * {@link HTTPWriterMirror#dispose(boolean)} with an argument of false. This
	 * is because the actual disposing needs to take place when the mirroring
	 * thread is prepared to gracefully exit its execution
	 * </p>
	 * 
	 * @see gr.uoa.di.madgik.grs.proxy.mirror.IMirror#dispose()
	 */
	public void dispose() {
		this.dispose(false);
	}

	/**
	 * This method disposes the resources that are used by the instance but does
	 * so in two phases. One can request the disposal passing an argument of
	 * <code>false</code> which puts the mirroring thread in a state where the
	 * needed actions are taken to gracefully exit this part of the protocol but
	 * also notify the respective {@link HTTPReaderMirror} to stop the mirroring
	 * procedure gracefully. When the method in again invoked with a
	 * <code>true</code> argument from the protocol implementing thread, all
	 * resources are disposed.
	 * 
	 * @param purge
	 *            whether the resources need to be immediately disposed, or this
	 *            need to wait until the protocol thread is ready to purge the
	 *            involved resources
	 */
	public void dispose(boolean purge) {
		if (this.state == MirroringState.Purged)
			return;
		if (purge)
			this.state = MirroringState.Purged;
		else
			this.state = MirroringState.Close;
		if (purge) {
			try {
				closeSocket();
			} catch (Exception e) {
			}
			try {
				if (this.buffer != null)
					this.buffer.dispose();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This method is not supported in the {@link HTTPWriterMirror}
	 * </p>
	 * 
	 * @throws GRS2ProxyMirrorInvalidOperationException
	 *             if the method is invoked
	 * 
	 * @see gr.uoa.di.madgik.grs.proxy.mirror.IMirror#pollPartial(long, int)
	 */
	public boolean pollPartial(long recordIndex, int fieldIndex) throws GRS2ProxyMirrorInvalidOperationException {
		throw new GRS2ProxyMirrorInvalidOperationException("Operation not supported in writer mirror");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This method is not supported in the {@link HTTPWriterMirror}
	 * </p>
	 * 
	 * @throws GRS2ProxyMirrorInvalidOperationException
	 *             if the method is invoked
	 * 
	 * @see gr.uoa.di.madgik.grs.proxy.mirror.IMirror#requestPartial(long, int,
	 *      gr.uoa.di.madgik.grs.buffer.IBuffer.TransportOverride,
	 *      java.lang.Object)
	 */
	public long requestPartial(long recordIndex, int fieldIndex, TransportOverride override, Object notify)
			throws GRS2ProxyMirrorInvalidOperationException {
		throw new GRS2ProxyMirrorInvalidOperationException("Operation not supported in writer mirror");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * In the context of the mirroring procedure, the execution of this thread
	 * includes the following actions.<br/>
	 * Initially, the local {@link GRSRegistry} is queried for the
	 * {@link IBuffer} to serve and its state is checked to make sure that it is
	 * available for mirroring.<br/>
	 * The main protocol loop is then initialized. On every iteration, the
	 * reader request is read and the needed {@link Record}s are returned
	 * limited based on the mirroring factor defined by the {@link IBuffer} and
	 * the available records. Additionally, if there are requests for partial
	 * data to be transfered, they are included in the response.<br/>
	 * Additionally, after the request is read, the incoming events are read and
	 * emitted in the local {@link IBuffer} and after all record data is send,
	 * the available events are send.<bt/> The status of the local mirroring
	 * procedure is also transmitted, while the remote state received is checked
	 * to see if the connection should be teardown.
	 * </p>
	 * 
	 * @see java.lang.Thread#run()
	 */

	private void writerAcceptRequests() throws Exception {
		//XXX: xml or json
		
		Document doc = null;
				
		/*if (dataformat == 1)
			doc = XMLHelper.getJSONDocument(this.request);
		else*/ 
			doc = XMLHelper.getXMLDocument(this.request);
		
//		System.out.println("*** writer received");
//		XMLHelper.printXML(doc);
//		System.out.println("*************************************");

		logger.log(Level.FINEST, "HTTPWriterMirror : parseRequest....");
		parseRequest(doc);
		logger.log(Level.FINEST, "HTTPWriterMirror : parseRequest....OK");

		logger.log(Level.FINEST, "HTTPWriterMirror : retrieveEvents....");
		retrieveEvents(doc);
		logger.log(Level.FINEST, "HTTPWriterMirror : retrieveEvents....OK");
	}

	private void writerEmptyResponse() throws Exception {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new GRS2Exception("Couldn't create an XML document", e);
		}

		Document doc = docBuilder.newDocument();
		Element element = doc.createElement("response");
		doc.appendChild(element);
		XMLHelper.sendXML(doc, out);
	}
	
	private boolean writerResponse() throws Exception {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new GRS2Exception("Couldn't create an XML document", e);
		}

		Document doc = docBuilder.newDocument();
		Element element = doc.createElement("response");

		logger.log(Level.FINEST, "HTTPWriterMirror : flushPartialRequests....");
		this.flushPartialRequests(doc, element);
		logger.log(Level.FINEST, "HTTPWriterMirror : flushPartialRequests....OK");

		logger.log(Level.FINEST, "HTTPWriterMirror : flushForwardBuffer....");
		this.flushForwardBuffer(doc, element);
		logger.log(Level.FINEST, "HTTPWriterMirror : flushForwardBuffer....OK");

		logger.log(Level.FINEST, "HTTPWriterMirror : flushEvents....");
		this.flushEvents(doc, element);
		logger.log(Level.FINEST, "HTTPWriterMirror : flushEvents....OK");

		logger.log(Level.FINEST, "HTTPWriterMirror : flushStatus....");
		boolean breakLoop = this.flushStatus(doc, element);
		logger.log(Level.FINEST, "HTTPWriterMirror : flushStatus....OK");

		doc.appendChild(element);
		
//		System.out.println("*** writer " + Thread.currentThread().getName() + " sends ");
//		XMLHelper.printXML(doc);
//		System.out.println("*************************************");
		
		//XXX: json or xml
		
		/*if (dataformat == 1)
			XMLHelper.sendJSON(doc, out);
		else*/
			XMLHelper.sendXML(doc, out);
	 
		return breakLoop;
	}

	public void run() {
		try {

			this.buffer = GRSRegistry.Registry.getBuffer(this.key);
			if (this.buffer == null && (this.state == MirroringState.Close || this.state == MirroringState.Purged))
				throw new GRS2ProxyMirrorInvalidOperationException(
						"Mirroring is already closed. Cannot initialize the protocol");
			else if (this.buffer == null && this.state == MirroringState.Open)
				throw new GRS2ProxyMirrorInvalidOperationException("No registry entry found for key " + this.key);

			
			if (req  == null || req.request == null){
				throw new GRS2ProxyMirrorProtocolErrorException("Writer timed out!");
			}
			this.socket = req.socket;
			this.request = req.request;
			this.out = req.out;
			
			try {
				this.out.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
				this.flushBufferConfig();
				closeSocket();
			} catch (Exception ex) {
				throw new GRS2ProxyMirrorProtocolErrorException(
						"Could not complete buffer configuration mirroring", ex);
			}
			req = null;
			
			
			while (true) {
				req = requests.poll(TIMEOUT, TimeUnit.MILLISECONDS); //This queue is filling with elements from the handle method
				
				if (req  == null || req.request == null){
					//break;//timeout
					throw new GRS2ProxyMirrorProtocolErrorException("Writer timed out!");
				}
				this.socket = req.socket;
				this.request = req.request;
				this.out = req.out;
				
				this.out.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
				
				if (this.state == MirroringState.Purged || this.state == MirroringState.Close)
					break;
				try {
					this.writerAcceptRequests();
				} catch (Exception ex) {
					throw new GRS2ProxyMirrorProtocolErrorException("Could not parse input request", ex);
				}
				if (doDispose) {
					logger.log(Level.FINEST, "Writer mirror received dispose request");
					writerEmptyResponse(); // because HTTP connection always waits for response
					break;
				}
				
				if (this.state == MirroringState.Purged)
					break;

				if (writerResponse())
					break;
				
				req = null;
				closeSocket();
					
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			if (this.state == MirroringState.Open && logger.isLoggable(Level.WARNING))
				logger.log(Level.WARNING, "Unrecoverable error during mirroring process", ex);
			else if (logger.isLoggable(Level.FINE))
				logger.log(Level.FINE, "Unrecoverable error during mirroring process", ex);
		} finally {
			logger.log(Level.FINEST, "Writer finished work");
			this.dispose(true);
		}

		closeSocket();
	}

	private boolean flushStatus(Document doc, Element element) throws Exception {
		Element el = doc.createElement("status");
		Integer value = null;

		if (this.state == MirroringState.Close) {
			value = 2;
		} else {
			if (this.buffer.getStatus() == Status.Close && this.buffer.availableRecords() == 0) {
				value = 1;
			} else {
				value = 0;
			}
		}
		el.setTextContent(String.valueOf(value));
		element.appendChild(el);

		return (value == 2);
	}

	private void flushBufferConfig() throws Exception {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new GRS2Exception("Couldn't create an XML document", e);
		}

		Document doc = docBuilder.newDocument();

		Element bufferConfig = doc.createElement("bufferConfig");

		Element el = doc.createElement("bufferClass");
		el.setTextContent(this.buffer.getClass().getName());
		bufferConfig.appendChild(el);

		el = doc.createElement("capacity");
		el.setTextContent(String.valueOf(this.buffer.getCapacity()));
		bufferConfig.appendChild(el);

		el = doc.createElement("concurrentPartialCapacity");
		el.setTextContent(String.valueOf(this.buffer.getConcurrentPartialCapacity()));
		bufferConfig.appendChild(el);

		el = doc.createElement("inactivityTimeout");
		el.setTextContent(String.valueOf(this.buffer.getInactivityTimeout()));
		bufferConfig.appendChild(el);

		el = doc.createElement("inactivityTimeUnit");
		el.setTextContent(String.valueOf(this.buffer.getInactivityTimeUnit().toString()));
		bufferConfig.appendChild(el);

		el = doc.createElement("transportDirective");
		el.setTextContent(String.valueOf(this.buffer.getTransportDirective().toString()));
		bufferConfig.appendChild(el);

		el = doc.createElement("recordDefinitions");

		if (this.buffer.getRecordDefinitions() != null && this.buffer.getRecordDefinitions().length > 0) {
			for (RecordDefinition def : this.buffer.getRecordDefinitions()) {
				Element rd = doc.createElement("recordDefinition");

				Element rdClass = doc.createElement("recordDefinitionClass");
				rdClass.setTextContent(def.getClass().getName());
				rd.appendChild(def.toXML(doc));

				rd.appendChild(rdClass);
				el.appendChild(rd);
			}
		}

		bufferConfig.appendChild(el);
		doc.appendChild(bufferConfig);

		//XXX: json or xml
		
		/*if (dataformat == 1)
			XMLHelper.sendJSON(doc, out);
		else*/
			XMLHelper.sendXML(doc, out);
	}

	private void parseRequest(Document doc) throws IOException, ClassNotFoundException, ParserConfigurationException,
			SAXException {
		Element element = (Element) doc.getDocumentElement();
		element = (Element) element.getElementsByTagName("request").item(0);

		this.doDispose = Boolean.valueOf(element.getElementsByTagName("doDispose").item(0).getTextContent());
		this.readerNeeded = Integer.valueOf(element.getElementsByTagName("needed").item(0).getTextContent());
		boolean simulateActivity = Boolean.valueOf(element.getElementsByTagName("simulateActivity").item(0)
				.getTextContent());

		if (simulateActivity == true)
			this.buffer.markSimulateActivity();

		if (element.getElementsByTagName("partialRequestEntries").item(0) != null) {
			NodeList partialRequestEntries = element.getElementsByTagName("partialRequestEntries").item(0)
					.getChildNodes();

			int len = partialRequestEntries.getLength();
			this.partials = new PartialRequestEntry[len];
			for (int i = 0; i < len; i++) {
				Element partialRequestEntry = (Element) partialRequestEntries.item(i);

				long recordIndex = Long.valueOf(partialRequestEntry.getElementsByTagName("recordIndex").item(0)
						.getTextContent());
				int fieldIndex = Integer.valueOf(partialRequestEntry.getElementsByTagName("fieldIndex").item(0)
						.getTextContent());
				String traspOverride = partialRequestEntry.getElementsByTagName("override").item(0).getTextContent();

				TransportOverride override = TransportOverride.valueOf(traspOverride);
				this.partials[i] = new PartialRequestEntry(recordIndex, fieldIndex, override, null);
			}
		}
	}

	private void retrieveEvents(Document doc) throws IOException, InstantiationException, IllegalAccessException,
			ClassNotFoundException, GRS2RecordSerializationException, GRS2BufferException,
			GRS2RecordDefinitionException, DOMException {
		Element element = (Element) doc.getDocumentElement();
		
		if (element.getElementsByTagName("events") == null || element.getElementsByTagName("events").getLength() == 0 )
			return;
		
		NodeList events = element.getElementsByTagName("events").item(0).getChildNodes();

		int len = events.getLength();
		for (int i = 0; i < len; i++) {
			Element event = (Element) events.item(i);

			String eventType = event.getElementsByTagName("eventType").item(0).getTextContent();
			BufferEvent ev = (BufferEvent) Class.forName(eventType).newInstance();

			ev.fromXML(event);
			this.buffer.emit(ev);
		}
	}

	private void flushEvents(Document doc, Element element) throws Exception {
		Element events = doc.createElement("events");

		while (true) {
			BufferEvent event = this.buffer.receive(EventSource.Writer);
			if (event == null)
				break;
			Element eventEl = doc.createElement("event");
			Element eventType = doc.createElement("eventType");
			eventType.setTextContent(event.getClass().getName());
			eventEl.appendChild(eventType);

			Element eventExt = event.toXML(doc);
			eventEl.appendChild(eventExt);

			events.appendChild(eventEl);
		}
		element.appendChild(events);
	}

	private void flushPartialRequests(Document doc, Element element) throws Exception {
		Element partRecords = doc.createElement("partialRecords");

		int i = 1;
		if (partRecords != null && this.partials != null) {
			for (PartialRequestEntry entry : this.partials) {
//				System.out.println("entry : " + i);
				Record rec = this.buffer.locate(entry.getRecordIndex());
				if (rec == null){
					
					throw new GRS2ProxyMirrorInvalidOperationException("Invalid record index provided");
				}
				Field[] fields = rec.getFields();
				if (fields == null)
					throw new GRS2ProxyMirrorInvalidOperationException("No fields to marshal");
				if (entry.getFieldIndex() < 0 || entry.getFieldIndex() >= fields.length)
					throw new GRS2ProxyMirrorInvalidOperationException("Invalid field index provided");
				Field f = fields[entry.getFieldIndex()];

				Element partRecord = doc.createElement("partialRecord");

				Element el = doc.createElement("partRecordIndex");
				el.setTextContent(String.valueOf(entry.getRecordIndex()));
				partRecord.appendChild(el);

				el = doc.createElement("fieldIndex");
				el.setTextContent(String.valueOf(entry.getFieldIndex()));
				partRecord.appendChild(el);

				el = doc.createElement("override");
				el.setTextContent(String.valueOf(entry.getOverride()));
				partRecord.appendChild(el);

//				 f.extendToXML(doc, partRecord);
				f.extendSendToXML(doc, partRecord, entry.getOverride());

				partRecords.appendChild(partRecord);
			}
		}
		element.appendChild(partRecords);
	}

	private void flushForwardBuffer(Document doc, Element element) throws Exception {
		Element records = doc.createElement("records");

		long available = this.buffer.availableRecords();
		long mirrorBuffer = this.buffer.getMirrorBuffer();
		long toMirror = this.readerNeeded;
		if (toMirror > available)
			toMirror = available;
		if (toMirror > mirrorBuffer)
			toMirror = mirrorBuffer;
		for (int i = 0; i < toMirror; i += 1) {
			Record rec = this.buffer.get();
			
			if (rec == null) {
				if (logger.isLoggable(Level.WARNING))
					logger.log(Level.WARNING, "Record not available although declared as available");
				break;
			}
			Element el = doc.createElement("record");

			Element recClassEl = doc.createElement("recordClass");
			recClassEl.setTextContent(rec.getClass().getName());
			el.appendChild(recClassEl);

			// root elements\
			//XXX:
			Element recEl = rec.sendToXML(doc);
//			Element recEl = rec.toXML(doc);
			el.appendChild(recEl);

			records.appendChild(el);
		}
		element.appendChild(records);
	}

	private void closeSocket() {
		logger.log(Level.FINEST, "Writer closed socket");
		this.request = null;
		try {
			if (this.out != null) {
				this.out.flush();
				this.out.close();
				this.out = null;
			}
		} catch (Exception e) {}
		try {
			if (this.socket != null) {
				this.socket.close();
				this.socket = null;
			}
		} catch (Exception e) {}
	}
}
