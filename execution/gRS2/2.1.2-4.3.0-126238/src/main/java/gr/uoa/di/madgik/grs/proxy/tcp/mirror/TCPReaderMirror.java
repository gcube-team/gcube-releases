package gr.uoa.di.madgik.grs.proxy.tcp.mirror;

import gr.uoa.di.madgik.commons.server.ITCPConnectionManagerEntry.NamedEntry;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
import gr.uoa.di.madgik.compressedstream.CompressedObjectStream;
import gr.uoa.di.madgik.grs.GRS2Exception;
import gr.uoa.di.madgik.grs.buffer.GRS2BufferException;
import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.buffer.IBuffer.TransportOverride;
import gr.uoa.di.madgik.grs.events.BufferEvent;
import gr.uoa.di.madgik.grs.events.BufferEvent.EventSource;
import gr.uoa.di.madgik.grs.proxy.mirror.GRS2ProxyMirrorDisposedException;
import gr.uoa.di.madgik.grs.proxy.mirror.GRS2ProxyMirrorInvalidOperationException;
import gr.uoa.di.madgik.grs.proxy.mirror.GRS2ProxyMirrorProtocolErrorException;
import gr.uoa.di.madgik.grs.proxy.mirror.IMirror;
import gr.uoa.di.madgik.grs.proxy.mirror.PartialRequestEntry;
import gr.uoa.di.madgik.grs.proxy.mirror.PartialRequestManager;
import gr.uoa.di.madgik.grs.record.GRS2RecordSerializationException;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FileField;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This implementation of the {@link IMirror} interface specializes the mirroring procedure that needs to be performed from the
 * reader side of the mirroring procedure. The technology employed is communication over raw TCP sockets. The communication
 * protocol is coupled with the one implemented by the {@link TCPWriterMirror} which is the writer side counterpart of this class   
 * 
 * @author gpapanikos
 *
 */
public class TCPReaderMirror extends Thread implements IMirror
{
	private static Logger logger=Logger.getLogger(TCPReaderMirror.class.getName());
	
	/**
	 * The default max period of communication inactivity time span. Depending on the previous communication exchange
	 * this or the respective {@link TCPReaderMirror#ShortMirrorPeriod} is used. Currently this value is set to 100 milliseconds
	 */
	public static final long LongMirrorPeriod=100;
	/**
	 * The default min period of communication inactivity time span. Depending on the previous communication exchange
	 * this or the respective {@link TCPReaderMirror#LongMirrorPeriod} is used. Currently this value is set to 50 milliseconds
	 */
	public static final long ShortMirrorPeriod=50;
	
	private String hostname=null;
	private int port=-1;
	private String key=null;
	private boolean overrideBufferCapacity=false;
	private int bufferCapacity=-1;
	private MirroringState state=MirroringState.Open;
	
	private Socket socket=null;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private IBuffer buffer=null;
	
	private GRS2ProxyMirrorProtocolErrorException initializationException=null; 
	private final Object initializationLock=new Object();
	private boolean initializationCompleted=false;
	
	private long lastIterationRecords=0;
	private long lastIterationNeeded=0;
	private long lastPartialFields=0;
	private long lastAvailableRecords = 0;
	private boolean consequentNoNeeds = false;
	
	private OutputStream outputStream;
	private InputStream inputStream;
	
	private PartialRequestManager manager=new PartialRequestManager();
	

	public void setInputStream(ObjectInputStream ois) {
		this.in = ois;
	}
	
	
	/**
	 * Sets the name of the host where the respective {@link TCPWriterMirror} instance is running 
	 * 
	 * @param hostname the name of the host
	 */
	
	
	
	public void setHostname(String hostname)
	{
		this.hostname=hostname;
	}
	
	/**
	 * Sets the port in the remote host where the respective {@link TCPWriterMirror} is listening
	 * 
	 * @param port the port number
	 */
	public void setPort(int port)
	{
		this.port=port;
	}
	
	/**
	 * Sets the key on the remote registry that holds the {@link IBuffer} this mirror is interested in consuming
	 * 
	 * @param key the registry key of the {@link IBuffer} to consume
	 */
	public void setKey(String key)
	{
		this.key=key;
	}
	
	/**
	 * Instructs the mirror to use a different buffer capacity than that transmitted by the respective {@link TCPWriterMirror}.
	 * Capacity overriding only takes place if the requested capacity is smaller than the one specified by the respective {@link TCPWriterMirror},
	 * otherwise the latter is used
	 * 
	 * @param capacity The buffer capacity
	 */
	public void overrideBufferCapacity(int capacity)
	{
		this.overrideBufferCapacity=true;
		this.bufferCapacity=capacity;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.proxy.mirror.IMirror#getBuffer()
	 */
	public IBuffer getBuffer()
	{
		return this.buffer;
	}
	
	/**
	 * This invocation of this method starts a new execution thread where the mirroring procedure is executed. The created
	 * thread of execution is executed in the background and as a daemon service. 
	 * 
	 * @throws GRS2ProxyMirrorInvalidOperationException if the mirroring state does not allow the operation to be executed
	 */
	public void handle() throws GRS2ProxyMirrorInvalidOperationException
	{
		if(this.state!=MirroringState.Open) throw new GRS2ProxyMirrorInvalidOperationException("Invalid mirroring state");
		if(this.key==null) throw new GRS2ProxyMirrorInvalidOperationException("No key defined");
		if(this.hostname==null) throw new GRS2ProxyMirrorInvalidOperationException("No hostname defined");
		if(this.port<=0) throw new GRS2ProxyMirrorInvalidOperationException("No port defined");
		this.setDaemon(true);
		this.setName("reader mirror ("+this.key+")");
		this.start();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This method actually invokes the respective {@link TCPReaderMirror#dispose(boolean)} with an argument of false. This is
	 * because the actual disposing needs to take place when the mirroring thread is prepared to gracefully exit its execution 
	 * </p>
	 * 
	 * @see gr.uoa.di.madgik.grs.proxy.mirror.IMirror#dispose()
	 */
	public void dispose()
	{
		this.dispose(false);
	}
	
	/**
	 * This method disposes the resources that are used by the instance but does so in two phases. One can request the disposal
	 * passing an argument of <code>false</code> which puts the mirroring thread in a state where the needed actions are taken to 
	 * gracefully exit this part of the protocol but also notify the respective {@link TCPWriterMirror} to stop the mirroring
	 * procedure gracefully. When the method in again invoked with a <code>true</code> argument from the protocol implementing 
	 * thread, all resources are disposed. 
	 * 
	 * @param purge whether the resources need to be immediately disposed, or this need to wait until the protocol thread
	 * is ready to purge the involved resources
	 */
	public void dispose(boolean purge)
	{
		if(this.state==MirroringState.Purged) return;
		if(purge) this.state=MirroringState.Purged;
		else this.state=MirroringState.Close;
		if(purge)
		{
			try{ if(this.out!=null) this.out.flush(); } catch (IOException e) { }
			try{ if(this.out!=null) this.out.close(); } catch (IOException e) { }
			try{ if(this.in!=null) this.in.close(); } catch (IOException e) { }
			try{ if(this.socket!=null) this.socket.close(); } catch (IOException e) { }
			try{ if(this.buffer!=null) {logger.log(Level.FINER, "Disposing buffer"); this.buffer.dispose();} } catch (Exception e) { }
			try{ if(this.manager!=null) this.manager.dispose(); } catch (Exception e) { }
			this.manager=null;
		}
	}
	
	/**
	 * This method can be used by {@link TCPReaderMirror} clients, that want to be sure that the initialization procedure of the 
	 * protocol is completed before they continue their operation. If someone invokes this method before communication has
	 * been successfully been established with the remote {@link TCPWriterMirror}, and the involved {@link IBuffer} information
	 * including the {@link RecordDefinition}s are exchanged and the {@link IBuffer} is initialized, the method will block him.
	 * After this operation is completed, and for any later invocation, the method will return control immediately
	 * 
	 * @return true if the initialization was successful, false if there was an error during initialization
	 */
	public boolean waitInitialization()
	{
		synchronized(this.initializationLock)
		{
			while(!this.initializationCompleted) try{this.initializationLock.wait();}catch(Exception ex){}
			if(this.initializationException!=null) return false;
			return true;
		}
	}
	
	/**
	 * In case the initialization was not successful, and the call to {@link TCPReaderMirror#waitInitialization()} returned
	 * false, this method will return the error that was recorded during the initialization. If no error was recorded,
	 * null is returned 
	 * 
	 * @return the initialization error or null if no error was recorded
	 */
	public GRS2ProxyMirrorProtocolErrorException getInitializationError()
	{
		return this.initializationException;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * the management of the partial requests is performed using {@link PartialRequestManager}
	 * </p>
	 * 
	 * @throws GRS2ProxyMirrorDisposedException the mirror is already disposed
	 * 
	 * @see gr.uoa.di.madgik.grs.proxy.mirror.IMirror#pollPartial(long, int)
	 */
	public boolean pollPartial(long recordIndex, int fieldIndex) throws GRS2ProxyMirrorDisposedException
	{
		if(this.state!=MirroringState.Open) return true;
		if(this.manager==null) return true;
		if(!this.manager.requestExists(recordIndex, fieldIndex)) return true; 
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The indicative time returned for the next poll operation is the amount defined as {@link TCPReaderMirror#ShortMirrorPeriod}
	 * </p>
	 * 
	 * @throws GRS2ProxyMirrorDisposedException the mirror is already disposed
	 * @throws GRS2ProxyMirrorInvalidOperationException if the {@link PartialRequestManager} has not been initialized
	 * 
	 * @see gr.uoa.di.madgik.grs.proxy.mirror.IMirror#requestPartial(long, int, gr.uoa.di.madgik.grs.buffer.IBuffer.TransportOverride, java.lang.Object)
	 */
	public long requestPartial(long recordIndex, int fieldIndex, TransportOverride override, Object notify) throws GRS2ProxyMirrorInvalidOperationException, GRS2ProxyMirrorDisposedException
	{
		if(this.state!=MirroringState.Open) throw new GRS2ProxyMirrorInvalidOperationException("Mirroring is closing. No additional request accepted");
		if(this.manager==null) throw new GRS2ProxyMirrorInvalidOperationException("Mirroring is closing. No additional request accepted");
		this.manager.block(recordIndex, fieldIndex, override, notify);
		return TCPReaderMirror.ShortMirrorPeriod;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * In the context of the mirroring procedure, the execution of this thread includes the following actions.<br/>
	 * Initially, a connection to the remote {@link TCPWriterMirror} is established, the details of the employed
	 * {@link IBuffer} are retrieved, as well as the {@link RecordDefinition}s that describe the {@link Record}s that
	 * will be retrieved. The appropriate {@link IBuffer} implementation is instantiated, configured and set with all 
	 * the retrieved information.<br/>
	 * After the initialization is completed, any blocked clients waiting to be notified on initialization completion 
	 * are notified.<br/>
	 * The main protocol loop is then initialized. On every iteration, a request is send to the remote {@link TCPWriterMirror}
	 * with the number of records that could be added to the local {@link IBuffer}, any partial data requests, as well as
	 * the status of the consumption procedure. <br/>
	 * In the next step, any event emitted from the reader is send over to the writer <br/>
	 * The next step is to retrieve from the {@link TCPWriterMirror} any {@link Record}s send as well as partial data
	 * from the requested ones. The respective information is used to either populate new {@link Record}s and add them to
	 * the local {@link IBuffer}, or to locate and enhance the {@link Field}s for which additional data have been received.<br/>
	 * Any events that are send from the writer are also received <br/>
	 * The remote status is also retrieved and the protocol loop continues unless it should be teardown based on the remote 
	 * status.<br/>
	 * If the remote mirror has responded with the information requested, then the period of the next protocol loop is defined
	 * as the minimum defined in {@link TCPReaderMirror#ShortMirrorPeriod} while in case no needed information was received
	 * the period is set to the maximum defined in {@link TCPReaderMirror#LongMirrorPeriod}
	 * </p>
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run()
	{
		int cnt = 0;
		try
		{
			synchronized(this.initializationLock)
			{
				try
				{
					this.connectToWriter();
					this.initializeBuffer();
				}catch(Exception ex)
				{
					this.initializationException=new GRS2ProxyMirrorProtocolErrorException("Could not initialize connection to writer buffer", ex);
				}
				finally
				{
					this.initializationCompleted=true;
					this.initializationLock.notifyAll();
					if(this.initializationException!=null) return;
				}
			}
			while(true)
			{
				if(this.state==MirroringState.Purged) break; //this shouldn't happen
				
				boolean teardown=this.readerRequests();
				if(teardown)break;
				
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
						Thread.sleep(TCPReaderMirror.LongMirrorPeriod);
					if(this.lastIterationRecords==0 && this.lastPartialFields==0) Thread.sleep(TCPReaderMirror.LongMirrorPeriod);
					else if(this.lastIterationNeeded==0 && this.lastPartialFields==0)
					{
						if(this.buffer.availableRecords() != 0) Thread.sleep(TCPReaderMirror.LongMirrorPeriod);
						else Thread.sleep(TCPReaderMirror.ShortMirrorPeriod);
					}
				}catch(Exception ex){}
				
				
				if (++cnt % 1000 == 0) {
//					this.out.reset();
					if (in.markSupported()) {
						this.in.reset();
					}
					cnt = 0;
				}
			}
		}catch(Exception ex)
		{
			if(this.state==MirroringState.Open && logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING,"Unrecoverable error during mirroring process",ex);
			else if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE,"Unrecoverable error during mirroring process",ex);
		}
		finally
		{
			this.dispose(true);
		}
	}
	
	private boolean readerRequests() throws Exception {
		boolean teardown = true;
		
		teardown = this.flushRequest();
		this.flushEvents();
		this.out.flush();
		
		return teardown;
	}
	
	private Integer retrieve() throws Exception {
		logger.log(Level.FINEST, "HTTPReaderMirror : retrievePartialRequests....");
		this.retrievePartialRequests(); 
		logger.log(Level.FINEST, "HTTPReaderMirror : retrievePartialRequests....OK");
		
		logger.log(Level.FINEST, "HTTPReaderMirror : retrieveRecords....");
		this.retrieveRecords();
		logger.log(Level.FINEST, "HTTPReaderMirror : retrieveRecords....OK");
		
		logger.log(Level.FINEST, "HTTPReaderMirror : retrieveEvents....");
		this.retrieveEvents();
		logger.log(Level.FINEST, "HTTPReaderMirror : retrieveEvents....OK");
		
		logger.log(Level.FINEST, "HTTPReaderMirror : retrieveStatus....");
		Integer isClosed = this.retrieveStatus();
		logger.log(Level.FINEST, "HTTPReaderMirror : retrieveStatus....OK");
		
		return isClosed;
	}
	
	private Integer retrieveStatus() throws Exception {
		Integer isClosed = this.in.readInt();
		return isClosed;
	}
	
	private void retrievePartialRequests() throws IOException, GRS2Exception, ClassNotFoundException 
	{
		ArrayList<PartialRequest> partialRequests = (ArrayList<PartialRequest>) CompressedObjectStream.readObject(in);
//		ArrayList<PartialRequest> partialRequests = (ArrayList<PartialRequest>) in.readObject();
		
		for (PartialRequest pr : partialRequests)
		{
			long recordIndex=pr.partRecordIndex;
			int fieldIndex=pr.fieldIndex;
			TransportOverride override=pr.override;
			Record rec=this.buffer.locate(recordIndex);
			if(rec==null) throw new GRS2ProxyMirrorInvalidOperationException("Invalid record index provided");
//			this.manager.unblock(recordIndex, fieldIndex);
			
			Field[] fields = rec.getFields();
			if (fields == null)
				throw new GRS2ProxyMirrorInvalidOperationException("No fields to update");
			if (fieldIndex < 0 || fieldIndex >= fields.length)
				throw new GRS2ProxyMirrorInvalidOperationException("Invalid field index provided");
			Field f = fields[fieldIndex];
			
			FileField ff = (FileField) f;
			ff.extendReadObject(in, override);
			
			this.manager.unblock(recordIndex, fieldIndex);
		}
		
	}

	private void retrieveRecords() throws IOException, GRS2Exception, InstantiationException, IllegalAccessException, ClassNotFoundException 
	{
//		ArrayList<Record> records = (ArrayList<Record>) CompressedObjectStream.readObject(in);
		ArrayList<Record> records = (ArrayList<Record>) in.readObject();
		
		this.lastIterationRecords=0;
		for (Record rec : records) {
			rec.prebind(buffer);	
			rec.setRemoteCopy(true);
			this.buffer.put(rec);
			this.lastIterationRecords+=1;
		}		
	}
	
	private void retrieveEvents() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, GRS2RecordSerializationException, GRS2BufferException
	{
		ArrayList<BufferEvent> events = (ArrayList<BufferEvent>) CompressedObjectStream.readObject(in);
		
		for (BufferEvent event : events) {
			this.buffer.emit(event);
		}
	}
	
	private void flushEvents() throws IOException, GRS2BufferException, GRS2RecordSerializationException
	{
		ArrayList<BufferEvent> events = new ArrayList<BufferEvent>();

		while(true)
		{
			BufferEvent event=this.buffer.receive(EventSource.Reader);
			if(event==null) break;
			
			events.add(event);
		}
//		out.writeObject(events);
		CompressedObjectStream.writeObject(events, this.out);
	}
	
	private boolean flushRequest() throws IOException
	{
		boolean doDispose=false;
		int needed=0;
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
		boolean simulateActivity = this.buffer.getSimulateActivity();
		
		Request request = new Request();
		request.doDispose = doDispose;
		request.needed = needed;
		request.simulateActivity = simulateActivity;
		
		
		if(this.manager!=null && !doDispose)
		{
			request.entries = new ArrayList<PartialRequestEntry>(Arrays.asList(this.manager.getEntries()));
		}
		
		
//		this.out.writeObject(request);
		CompressedObjectStream.writeObject(request, this.out);
		
		logger.log(Level.FINEST, "Flush request doDispose=" + doDispose + " mirroring state=" + this.state);
		return doDispose;
	}
	
	
	
	private void connectToWriter() throws UnknownHostException, IOException
	{
		this.socket=new Socket(this.hostname, this.port);
		
//		socket.setSendBufferSize(TCPConnectionManager.BUFFERSIZE);
//		socket.setReceiveBufferSize(TCPConnectionManager.BUFFERSIZE);
		
		
		this.outputStream = this.socket.getOutputStream();
		
		DataOutputStream dos = new DataOutputStream(outputStream);
		dos.writeUTF(NamedEntry.gRS2.toString());
		
		this.out=new ObjectOutputStream(new BufferedOutputStream(this.outputStream));
		
		//this.out.writeUTF(NamedEntry.gRS2.toString());
		dos.flush();
		this.out.writeUTF(this.key);
		this.out.flush();
		
		this.inputStream = this.socket.getInputStream();
		this.in = new ObjectInputStream(new BufferedInputStream(this.inputStream));
	}
	
	private void initializeBuffer() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, GRS2Exception
	{
		//BufferConfig bufferConfig = (BufferConfig) this.in.readObject();
		BufferConfig bufferConfig = (BufferConfig) CompressedObjectStream.readObject(in);
		
		this.buffer=(IBuffer) Class.forName(bufferConfig.bufferClassName).newInstance();
		if(this.overrideBufferCapacity && this.bufferCapacity<bufferConfig.capacity) this.buffer.setCapacity(this.bufferCapacity);
		else this.buffer.setCapacity(bufferConfig.capacity);
		this.buffer.setConcurrentPartialCapacity(bufferConfig.capacity);
		this.buffer.setInactivityTimeout(bufferConfig.inactivityTimeout);
		this.buffer.setInactivityTimeUnit(bufferConfig.inactivityTimeoutUnit);
	//	this.buffer.setNotificationThreshold(threshold);
		this.buffer.setTransportDirective(bufferConfig.transportDirective);
		RecordDefinition []definitions=new RecordDefinition[bufferConfig.recordDefinitions.size()];
		int i = 0;
		for (RecordDefinition rd : bufferConfig.recordDefinitions) {
			definitions[i] = rd;
			i++;
		}
		this.buffer.setRecordDefinitions(definitions);
		
		this.buffer.initialize();
	}
}
