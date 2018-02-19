package gr.uoa.di.madgik.grs.proxy.http.mirror;

import gr.uoa.di.madgik.commons.server.http.IHTTPConnectionManagerEntry;
import gr.uoa.di.madgik.grs.GRS2Exception;
import gr.uoa.di.madgik.grs.buffer.GRS2BufferException;
import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.buffer.IBuffer.TransportDirective;
import gr.uoa.di.madgik.grs.buffer.IBuffer.TransportOverride;
import gr.uoa.di.madgik.grs.events.BufferEvent;
import gr.uoa.di.madgik.grs.events.BufferEvent.EventSource;
import gr.uoa.di.madgik.grs.proxy.mirror.GRS2ProxyMirrorDisposedException;
import gr.uoa.di.madgik.grs.proxy.mirror.GRS2ProxyMirrorInvalidOperationException;
import gr.uoa.di.madgik.grs.proxy.mirror.GRS2ProxyMirrorProtocolErrorException;
import gr.uoa.di.madgik.grs.proxy.mirror.IMirror;
import gr.uoa.di.madgik.grs.proxy.mirror.PartialRequestEntry;
import gr.uoa.di.madgik.grs.proxy.mirror.PartialRequestManager;
import gr.uoa.di.madgik.grs.proxy.tcp.mirror.TCPReaderMirror;
import gr.uoa.di.madgik.grs.record.GRS2RecordDefinitionException;
import gr.uoa.di.madgik.grs.record.GRS2RecordSerializationException;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.xml.XMLHelper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
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

/**
 * This implementation of the {@link IMirror} interface specializes the
 * mirroring procedure that needs to be performed from the reader side of the
 * mirroring procedure. The technology employed is communication over HTTP 
 * connections. The communication protocol is coupled with the one implemented by
 * the {@link HTTPWriterMirror} which is the writer side counterpart of this
 * class.
 * 
 * @author Alex Antoniadis
 * 
 */
public class HTTPReaderMirror extends Thread implements IMirror {
	private static Logger logger = Logger.getLogger(HTTPReaderMirror.class.getName());

	/**
	 * The default max period of communication inactivity time span. Depending
	 * on the previous communication exchange this or the respective
	 * {@link HTTPReaderMirror#ShortMirrorPeriod} is used. Currently this value
	 * is set to 100 milliseconds
	 */
	public static final long LongMirrorPeriod = 100;
	/**
	 * The default min period of communication inactivity time span. Depending
	 * on the previous communication exchange this or the respective
	 * {@link HTTPReaderMirror#LongMirrorPeriod} is used. Currently this value
	 * is set to 50 milliseconds
	 */
	public static final long ShortMirrorPeriod = 50;
	
	public static final int ReaderTimout = 10000; // reader will fail after 10 secs 

	private String hostname = null;
	private int port = -1;
	private String key = null;
	private boolean overrideBufferCapacity = false;
	private int bufferCapacity = -1;
	private MirroringState state = MirroringState.Open;

	private InputStream in;
	private OutputStream out;
	private IBuffer buffer = null;

	private GRS2ProxyMirrorProtocolErrorException initializationException = null;
	private final Object initializationLock = new Object();
	private boolean initializationCompleted = false;

	private long lastIterationRecords = 0;
	private long lastPartialFields = 0;
	private long lastIterationNeeded=0;
	private long lastAvailableRecords = 0;
	private boolean consequentNoNeeds = false;

	private PartialRequestManager manager = new PartialRequestManager();
	private HttpURLConnection conn;

	/**
	 * Sets the name of the host where the respective {@link HTTPWriterMirror}
	 * instance is running
	 * 
	 * @param hostname
	 *            the name of the host
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	/**
	 * Sets the port in the remote host where the respective
	 * {@link HTTPWriterMirror} is listening
	 * 
	 * @param port
	 *            the port number
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Sets the key on the remote registry that holds the {@link IBuffer} this
	 * mirror is interested in consuming
	 * 
	 * @param key
	 *            the registry key of the {@link IBuffer} to consume
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Instructs the mirror to use a different buffer capacity than that
	 * transmitted by the respective {@link HTTPWriterMirror}. Capacity
	 * overriding only takes place if the requested capacity is smaller than the
	 * one specified by the respective {@link HTTPWriterMirror}, otherwise the
	 * latter is used
	 * 
	 * @param capacity
	 *            The buffer capacity
	 */
	public void overrideBufferCapacity(int capacity) {
		this.overrideBufferCapacity = true;
		this.bufferCapacity = capacity;
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
	 * executed in the background and as a daemon service.
	 * 
	 * @throws GRS2ProxyMirrorInvalidOperationException
	 *             if the mirroring state does not allow the operation to be
	 *             executed
	 */
	public void handle() throws GRS2ProxyMirrorInvalidOperationException {
		if (this.state != MirroringState.Open)
			throw new GRS2ProxyMirrorInvalidOperationException("Invalid mirroring state");
		if (this.key == null)
			throw new GRS2ProxyMirrorInvalidOperationException("No key defined");
		if (this.hostname == null)
			throw new GRS2ProxyMirrorInvalidOperationException("No hostname defined");
		if (this.port <= 0)
			throw new GRS2ProxyMirrorInvalidOperationException("No port defined");
		this.setDaemon(true);
		this.setName("reader mirror (" + this.key + ")");
		this.start();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This method actually invokes the respective
	 * {@link HTTPReaderMirror#dispose(boolean)} with an argument of false. This
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
	 * also notify the respective {@link HTTPWriterMirror} to stop the mirroring
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
			close();
			try {
				if (this.buffer != null) {
					logger.log(Level.FINER, "Disposing buffer");
					this.buffer.dispose();
				}
			} catch (Exception e) {
			}
			try {
				if (this.manager != null)
					this.manager.dispose();
			} catch (Exception e) {
			}
			this.manager = null;
		}
	}

	/**
	 * This method can be used by {@link HTTPReaderMirror} clients, that want to
	 * be sure that the initialization procedure of the protocol is completed
	 * before they continue their operation. If someone invokes this method
	 * before communication has been successfully been established with the
	 * remote {@link HTTPWriterMirror}, and the involved {@link IBuffer}
	 * information including the {@link RecordDefinition}s are exchanged and the
	 * {@link IBuffer} is initialized, the method will block him. After this
	 * operation is completed, and for any later invocation, the method will
	 * return control immediately
	 * 
	 * @return true if the initialization was successful, false if there was an
	 *         error during initialization
	 */
	public boolean waitInitialization() {
		synchronized (this.initializationLock) {
			while (!this.initializationCompleted)
				try {
					this.initializationLock.wait();
				} catch (Exception ex) {
				}
			if (this.initializationException != null)
				return false;
			return true;
		}
	}

	/**
	 * In case the initialization was not successful, and the call to
	 * {@link HTTPReaderMirror#waitInitialization()} returned false, this method
	 * will return the error that was recorded during the initialization. If no
	 * error was recorded, null is returned
	 * 
	 * @return the initialization error or null if no error was recorded
	 */
	public GRS2ProxyMirrorProtocolErrorException getInitializationError() {
		return this.initializationException;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * the management of the partial requests is performed using
	 * {@link PartialRequestManager}
	 * </p>
	 * 
	 * @throws GRS2ProxyMirrorDisposedException
	 *             the mirror is already disposed
	 * 
	 * @see gr.uoa.di.madgik.grs.proxy.mirror.IMirror#pollPartial(long, int)
	 */
	public boolean pollPartial(long recordIndex, int fieldIndex) throws GRS2ProxyMirrorDisposedException {
		if (this.state != MirroringState.Open)
			return true;
		if (this.manager == null)
			return true;
		if (!this.manager.requestExists(recordIndex, fieldIndex))
			return true;
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The indicative time returned for the next poll operation is the amount
	 * defined as {@link HTTPReaderMirror#ShortMirrorPeriod}
	 * </p>
	 * 
	 * @throws GRS2ProxyMirrorDisposedException
	 *             the mirror is already disposed
	 * @throws GRS2ProxyMirrorInvalidOperationException
	 *             if the {@link PartialRequestManager} has not been initialized
	 * 
	 * @see gr.uoa.di.madgik.grs.proxy.mirror.IMirror#requestPartial(long, int,
	 *      gr.uoa.di.madgik.grs.buffer.IBuffer.TransportOverride,
	 *      java.lang.Object)
	 */
	public long requestPartial(long recordIndex, int fieldIndex, TransportOverride override, Object notify)
			throws GRS2ProxyMirrorInvalidOperationException, GRS2ProxyMirrorDisposedException {
		if (this.state != MirroringState.Open)
			throw new GRS2ProxyMirrorInvalidOperationException("Mirroring is closing. No additional request accepted");
		if (this.manager == null)
			throw new GRS2ProxyMirrorInvalidOperationException("Mirroring is closing. No additional request accepted");
		this.manager.block(recordIndex, fieldIndex, override, notify);
		return HTTPReaderMirror.ShortMirrorPeriod;
	}

	private void bufferInit() throws Exception {
		conn = this.connectToWriter(false);
		this.in = conn.getInputStream();
		this.initializeBuffer();

	}

	/**
	 * Sends the reader requests and events to the other side.
	 * 
	 * @return true if dispose, false if else.
	 * @throws Exception if some XML transformation or the HTTP transfer goes wrong.
	 */
	private boolean readerRequests() throws Exception {
		boolean teardown = true;
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new GRS2Exception("Couldn't create an XML document", e);
		}

		Document doc = docBuilder.newDocument();

		Element element = doc.createElement("totalRequest");

		logger.log(Level.FINEST, "HTTPReaderMirror : flushRequest....");
		teardown = this.flushRequest(doc, element);
		logger.log(Level.FINEST, "HTTPReaderMirror : flushRequest....OK");

		logger.log(Level.FINEST, "HTTPReaderMirror : flushEvents....");
		this.flushEvents(doc, element);
		logger.log(Level.FINEST, "HTTPReaderMirror : flushEvents....OK");

		doc.appendChild(element);

//		System.out.println("*** reader sends");
//		XMLHelper.printXML(doc);
//		System.out.println("*************************************");
		
		sendHTTP(doc);
		return teardown;
	}

	/**
	 * Retrieves the partial requests, records, events and status from the other side by parsing the received XML.
	 * @return the status number
	 * @throws Exception
	 */
	private Integer retrieve() throws Exception {
		this.in = conn.getInputStream();

		//XXX: xml or json
		
		Document doc = null;
		
		/*if (HTTPWriterMirror.dataformat == 1)
			doc = XMLHelper.getJSONDocument(this.in);
		else */
			doc = XMLHelper.getXMLDocument(this.in);
		
		
//		System.out.println("*** reader " + Thread.currentThread().getName() +  " received");
//		XMLHelper.printXML(doc);
//		System.out.println("*************************************");
		
		
		logger.log(Level.FINEST, "HTTPReaderMirror : retrievePartialRequests....");
		this.retrievePartialRequests(doc); 
		logger.log(Level.FINEST, "HTTPReaderMirror : retrievePartialRequests....OK");
		
		logger.log(Level.FINEST, "HTTPReaderMirror : retrieveRecords....");
		this.retrieveRecords(doc);
		logger.log(Level.FINEST, "HTTPReaderMirror : retrieveRecords....OK");
		
		logger.log(Level.FINEST, "HTTPReaderMirror : retrieveEvents....");
		this.retrieveEvents(doc);
		logger.log(Level.FINEST, "HTTPReaderMirror : retrieveEvents....OK");
		
		logger.log(Level.FINEST, "HTTPReaderMirror : retrieveStatus....");
		Integer isClosed = this.retrieveStatus(doc);
		logger.log(Level.FINEST, "HTTPReaderMirror : retrieveStatus....OK");
		
		return isClosed;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * In the context of the mirroring procedure, the execution of this thread
	 * includes the following actions.<br/>
	 * Initially, a connection to the remote {@link HTTPWriterMirror} is
	 * established, the details of the employed {@link IBuffer} are retrieved,
	 * as well as the {@link RecordDefinition}s that describe the {@link Record}
	 * s that will be retrieved. The appropriate {@link IBuffer} implementation
	 * is instantiated, configured and set with all the retrieved information.<br/>
	 * After the initialization is completed, any blocked clients waiting to be
	 * notified on initialization completion are notified.<br/>
	 * The main protocol loop is then initialized. On every iteration, a request
	 * is send to the remote {@link HTTPWriterMirror} with the number of records
	 * that could be added to the local {@link IBuffer}, any partial data
	 * requests, as well as the status of the consumption procedure. <br/>
	 * In the next step, any event emitted from the reader is send over to the
	 * writer <br/>
	 * The next step is to retrieve from the {@link HTTPWriterMirror} any
	 * {@link Record}s send as well as partial data from the requested ones. The
	 * respective information is used to either populate new {@link Record}s and
	 * add them to the local {@link IBuffer}, or to locate and enhance the
	 * {@link Field}s for which additional data have been received.<br/>
	 * Any events that are send from the writer are also received <br/>
	 * The remote status is also retrieved and the protocol loop continues
	 * unless it should be teardown based on the remote status.<br/>
	 * If the remote mirror has responded with the information requested, then
	 * the period of the next protocol loop is defined as the minimum defined in
	 * {@link HTTPReaderMirror#ShortMirrorPeriod} while in case no needed
	 * information was received the period is set to the maximum defined in
	 * {@link HTTPReaderMirror#LongMirrorPeriod}
	 * </p>
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		try {
			//if (this.buffer == null || this.buffer.isInitializedFromWriter() == true) {
			synchronized (this.initializationLock) {
				try {
					bufferInit();
				} catch (Exception ex) {
					this.initializationException = new GRS2ProxyMirrorProtocolErrorException(
							"Could not initialize connection to writer buffer", ex);
				} finally {
					this.initializationCompleted = true;
					this.initializationLock.notifyAll();
					if (this.initializationException != null)
						return;
				}
			}

			close();

			while (true) {
				if (this.state == MirroringState.Purged)
					break; // this shouldn't happen

				boolean teardown = this.readerRequests();
				if (teardown){
					//it is required in http communications
					this.in = conn.getInputStream();
					break;
				}
				//try {
				Integer isClosed = retrieve();
				boolean breakLoop = false;
				switch (isClosed) {
				case 0: {
					break;
				}
				case 1: {
					logger.log(Level.FINEST, "Received \"close\" from writer mirror. Closing buffer");
					//breakLoop = true;
					try {
						this.buffer.close();
					} catch (Exception ex) {
					}
					break;
				}
				case 2: {
					logger.log(Level.FINEST, "Received \"dispose\" from writer mirror. Disposing buffer");
					try {
						this.buffer.dispose();
					} catch (Exception ex) {
					}
					breakLoop = true;
					
					break;
				}
				default: {
					throw new GRS2ProxyMirrorProtocolErrorException("Unrecognized status token");
				}
				}

				if (breakLoop)
					break;
				try{
					
					if (consequentNoNeeds)
						Thread.sleep(HTTPReaderMirror.LongMirrorPeriod);
					else if(this.lastIterationRecords==0 && this.lastPartialFields==0) Thread.sleep(TCPReaderMirror.LongMirrorPeriod);
					else if(this.lastIterationNeeded==0 && this.lastPartialFields==0)
					{
						if(this.buffer.availableRecords() != 0) Thread.sleep(HTTPReaderMirror.LongMirrorPeriod);
						else Thread.sleep(HTTPReaderMirror.ShortMirrorPeriod);
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				/*} catch (Exception ex) {
					if (this.state != MirroringState.Open || this.buffer.getStatus() == Status.Dispose) {
						logger.log(Level.FINE, "Skipping producer data");
						while (true) {
							while (this.in.available() > 0)
								this.in.skip(this.in.available());
							if (this.in.available() == 0)
								try {
									Thread.sleep(5000);
								} catch (Exception exx) {
								}
							if (this.in.available() == 0)
								break;
						}
						close();
						continue;
					} else {
						throw ex;
					}
				}*/
				close();
			}
		} catch (Exception ex) {
			// if buffer is closed or disposed ignore exceptions. This is done because the reader may close but the XML document records have not been consumed (put in buffer) yet 
			if (this.getBuffer().getStatus() == IBuffer.Status.Open && this.state == MirroringState.Open)
				ex.printStackTrace();
			if (this.state == MirroringState.Open && logger.isLoggable(Level.WARNING))
				logger.log(Level.WARNING, "Unrecoverable error during mirroring process", ex);
			else if (logger.isLoggable(Level.FINE))
				logger.log(Level.FINE, "Unrecoverable error during mirroring process", ex);
		} finally {
			this.dispose(true);
		}
	}

	
	/**
	 * Retrieves the partial records from a DOM document.
	 * @param doc the DOM document that includes a partial records element
	 * @throws GRS2Exception if any parsing stage goes wrong
	 */
	private void retrievePartialRequests(Document doc) throws GRS2Exception {
		Element element = (Element) doc.getDocumentElement();

		NodeList partialRequests = element.getElementsByTagName("partialRecords").item(0).getChildNodes();

		for (int i = 0; i < partialRequests.getLength(); ++i) {
			Element partialRequest = (Element) partialRequests.item(i);

			long recordIndex = Long.valueOf(partialRequest.getElementsByTagName("partRecordIndex").item(0)
					.getTextContent());
			int fieldIndex = Integer.valueOf(partialRequest.getElementsByTagName("fieldIndex").item(0)
					.getTextContent());
			TransportOverride override = TransportOverride.valueOf(partialRequest.getElementsByTagName("override")
					.item(0).getTextContent());
			Record rec = this.buffer.locate(recordIndex);
			if (rec == null)
				throw new GRS2ProxyMirrorInvalidOperationException("Invalid record index provided");
			Field[] fields = rec.getFields();
			if (fields == null)
				throw new GRS2ProxyMirrorInvalidOperationException("No fields to update");
			if (fieldIndex < 0 || fieldIndex >= fields.length)
				throw new GRS2ProxyMirrorInvalidOperationException("Invalid field index provided");
			Field f = fields[fieldIndex];

//			f.fromXML(doc.getDocumentElement());
			f.extendReceiveFromXML(doc.getDocumentElement(), override);
			
			this.manager.unblock(recordIndex, fieldIndex);
		}
	}

	/**
	 * Retrieves the records from a DOM document.
	 * @param doc the DOM document that includes a records element
	 * @throws GRS2Exception if any parsing stage goes wrong
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	private void retrieveRecords(Document doc) throws GRS2Exception, InstantiationException, IllegalAccessException, ClassNotFoundException {
		Element element = (Element) doc.getDocumentElement();
		element = (Element) element.getElementsByTagName("records").item(0);

		NodeList records = element.getChildNodes();
		if (records != null) {
			for (int i = 0; i < records.getLength(); ++i) {
				Element record = (Element) records.item(i);
				String recordClass = record.getElementsByTagName("recordClass").item(0).getTextContent();
				
				Record rec = (Record)Class.forName(recordClass).newInstance();
				rec.prebind(buffer);
//				 XXX: 
//				rec.fromXML(record);
				rec.receiveFromXML(record);
				rec.setRemoteCopy(true);
				this.buffer.put(rec);
				this.lastIterationRecords += 1;
			}
		}
	}

	/**
	 * Retrieves the events from a DOM document.
	 * @param doc the DOM document that includes an events element
	 * @throws GRS2RecordSerializationException if any parsing stage goes wrong
	 * @throws DOMException 
	 * @throws GRS2RecordDefinitionException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws GRS2BufferException 
	 */
	private void retrieveEvents(Document doc) throws GRS2RecordSerializationException, GRS2RecordDefinitionException, DOMException, InstantiationException, IllegalAccessException, ClassNotFoundException, GRS2BufferException {
		Element element = (Element) doc.getDocumentElement();
		NodeList events = element.getElementsByTagName("events").item(0).getChildNodes();

		if (events != null) {
			int len = events.getLength();

			for (int i = 0; i < len; i++) {
				Element event = (Element) events.item(i);

				String eventType = event.getElementsByTagName("eventType").item(0).getTextContent();
				BufferEvent ev = (BufferEvent) Class.forName(eventType).newInstance();

				ev.fromXML(event);
				this.buffer.emit(ev);
			}
		}
	}

	/**
	 * Retrieves the status from a DOM document
	 * @param doc the DOM document that includes an events element
	 * @return an Integer referring to the status code.
	 * @throws GRS2RecordSerializationException if any parsing stage goes wrong
	 */
	private Integer retrieveStatus(Document doc) throws GRS2RecordSerializationException {
		Element element = (Element) doc.getDocumentElement();
		Integer isClosed = Integer.valueOf(element.getElementsByTagName("status").item(0).getTextContent());

		return isClosed;
	}

	
	private void flushEvents(Document doc, Element element) throws Exception {
		Element events = doc.createElement("events");

		while (true) {
			BufferEvent event = this.buffer.receive(EventSource.Reader);
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

	private boolean flushRequest(Document doc, Element element) throws Exception {
		boolean doDispose = false;
		int needed = 0;
		int availableRecords = -1;
		
		try {
			if (this.state != MirroringState.Open || this.buffer.getStatus() == Status.Dispose)
				doDispose = true;
			else{
				availableRecords = this.buffer.availableRecords();
				needed = this.buffer.getCapacity() - availableRecords;
			}
		} catch (Exception ex) {
			if (logger.isLoggable(Level.FINE))
				logger.log(Level.FINE, "Could not check for needed state flushing info. Setting to dispose", ex);
			doDispose = true;
		}
		long consumed = this.lastAvailableRecords - availableRecords;
		if (consumed < 0) consumed*=-1;
		
		this.lastAvailableRecords = availableRecords;
		if (this.lastAvailableRecords > 0 && needed > 0)
			if ((int)consumed < needed)
				needed = (int) consumed;

		
		this.consequentNoNeeds = (this.lastIterationNeeded == 0 && needed == 0);
		
		this.lastIterationNeeded = needed;
		
		Element requestEl = doc.createElement("request");

		Element el = doc.createElement("doDispose");
		el.setTextContent(String.valueOf(doDispose));
		requestEl.appendChild(el);

//		if (needed == 0)
//			System.out.println("needed equals 0");
		
		el = doc.createElement("needed");
		el.setTextContent(String.valueOf(needed));
		requestEl.appendChild(el);

		boolean simulateActivity = this.buffer.getSimulateActivity();

		el = doc.createElement("simulateActivity");
		el.setTextContent(String.valueOf(simulateActivity));
		requestEl.appendChild(el);
		if (this.manager != null && doDispose == false) {
			PartialRequestEntry[] entries = this.manager.getEntries();

			//PartialRequestEntry[] entries = new PartialRequestEntry[]{new PartialRequestEntry(0, 0, TransportOverride.Override, new Object())};
			Element partialRequestEntries = doc.createElement("partialRequestEntries");
			this.lastPartialFields = entries.length;
			for (PartialRequestEntry entry : entries) {
				Element partialRequestEntry = doc.createElement("partialRequestEntry");

				Element elm = doc.createElement("recordIndex");
				elm.setTextContent(String.valueOf(entry.getRecordIndex()));
				partialRequestEntry.appendChild(elm);

				elm = doc.createElement("fieldIndex");
				elm.setTextContent(String.valueOf(entry.getFieldIndex()));
				partialRequestEntry.appendChild(elm);

				elm = doc.createElement("override");
				elm.setTextContent(String.valueOf(entry.getOverride().toString()));
				partialRequestEntry.appendChild(elm);

				partialRequestEntries.appendChild(partialRequestEntry);
			}
			requestEl.appendChild(partialRequestEntries);
		}
		element.appendChild(requestEl);

		logger.log(Level.FINEST, "Flush request doDispose=" + doDispose + " mirroring state=" + this.state);

		return doDispose;
	}

	/**
	 * Opens an HTTP connection to the writers.
	 * @param output
	 * @return the HttpURLConnection referring to the successful connection
	 * @throws Exception
	 */
	private HttpURLConnection connectToWriter(boolean output) throws Exception {
		init();
		String url = "http://" + this.hostname + ":" + this.port;
		logger.log(Level.FINEST, "HTTPReaderMirror : Reader created connection....");
		logger.log(Level.FINEST, "HTTPReaderMirror : url : " + url);
		
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
//		connection.setReadTimeout(ReaderTimout); // needs to avoid blocking forever
		output = true;
		connection.setDoOutput(output);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("key", this.key);
		connection.setRequestProperty("EntryName", IHTTPConnectionManagerEntry.NamedEntry.gRS2.name());

		if (output) {
			this.out = connection.getOutputStream();
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(this.out));
			out.flush();
		}
		return connection;
	}

	
	private void initializeBuffer() throws Exception {
		//XXX: xml or json
		
		Document doc = null;
		
		/*if (HTTPWriterMirror.dataformat == 1)
			doc = XMLHelper.getJSONDocument(this.in);
		else */
			doc = XMLHelper.getXMLDocument(this.in);
		
		
		logger.log(Level.FINEST, "In initializeBuffer XML document parsed");
		Element element = doc.getDocumentElement();

		String bufferType = element.getElementsByTagName("bufferClass").item(0).getTextContent();
		int capacity = Integer.parseInt(element.getElementsByTagName("capacity").item(0).getTextContent());
		int concurrentPartial = Integer.parseInt(element.getElementsByTagName("concurrentPartialCapacity").item(0)
				.getTextContent());
		long inactivityTimeout = Long.parseLong(element.getElementsByTagName("inactivityTimeout").item(0)
				.getTextContent());
		TimeUnit InactivityTimeUnit = TimeUnit.valueOf(element.getElementsByTagName("inactivityTimeUnit").item(0)
				.getTextContent());
		TransportDirective transportDirective = TransportDirective.valueOf(element
				.getElementsByTagName("transportDirective").item(0).getTextContent());

		NodeList recordDefinitions = element.getElementsByTagName("recordDefinitions").item(0).getChildNodes();

		RecordDefinition[] definitions = null;
		if (recordDefinitions != null && recordDefinitions.getLength() > 0) {
			int len = recordDefinitions.getLength();

			definitions = new RecordDefinition[len];
			for (int i = 0; i < len; i++) {
				Element recordDefinition = (Element) recordDefinitions.item(i);
				String recordDefType = recordDefinition.getElementsByTagName("recordDefinitionClass").item(0)
						.getTextContent();
				RecordDefinition def = (RecordDefinition) Class.forName(recordDefType).newInstance();
				def.fromXML(recordDefinition);
				definitions[i] = def;
			}

		}
		this.buffer = (IBuffer) Class.forName(bufferType).newInstance();
		if (this.overrideBufferCapacity && this.bufferCapacity < capacity)
			this.buffer.setCapacity(this.bufferCapacity);
		else
			this.buffer.setCapacity(capacity);
		this.buffer.setConcurrentPartialCapacity(concurrentPartial);
		this.buffer.setInactivityTimeout(inactivityTimeout);
		this.buffer.setInactivityTimeUnit(InactivityTimeUnit);
		this.buffer.setTransportDirective(transportDirective);
		this.buffer.setRecordDefinitions(definitions);
		this.buffer.initialize();
	}

	private void sendHTTP(Document doc) throws Exception {
		conn = connectToWriter(true);
		//XXX: json or xml
		/*if (HTTPWriterMirror.dataformat == 1)
			XMLHelper.sendJSON(doc, out);
		else*/
			XMLHelper.sendXML(doc, out);
	}

	private void init() {
		this.out = null;
		this.in = null;
		this.conn = null;
	}

	private void close() {
		try {
			if (out != null) {
				this.out.flush();
				this.out.close();
			}
			if (in != null)
				this.in.close();
			if (conn != null) {
				this.conn.disconnect();
			}
			logger.log(Level.FINEST, "HTTPReaderMirror : Reader closed connection....");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			init();
		}

	}

}
