package gr.uoa.di.madgik.grs.proxy.tcp.mirror;

import gr.uoa.di.madgik.compressedstream.CompressedObjectStream;
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
import gr.uoa.di.madgik.grs.record.GRS2RecordSerializationException;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FileField;
import gr.uoa.di.madgik.grs.registry.GRSRegistry;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This implementation of the {@link IMirror} interface specializes the mirroring procedure that needs to be performed from the
 * writer side of the mirroring procedure. The technology employed is communication over raw TCP sockets. The communication
 * protocol is coupled with the one implemented by the {@link TCPReaderMirror} which is the writer side counterpart of this class   
 * 
 * @author gpapanikos
 *
 */
public class TCPWriterMirror extends Thread implements IMirror
{
	private static Logger logger=Logger.getLogger(TCPWriterMirror.class.getName());
	
	private Socket socket=null;
	private String key=null;
	private MirroringState state=MirroringState.Open;
	
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private IBuffer buffer=null;
	
	private int readerNeeded=0;
	private boolean doDispose=false;
	private PartialRequestEntry[] partials=null;
	
	
	public void setInputStream(ObjectInputStream ois) {
		this.in = ois;
	}
	
	
	/**
	 * Sets the socket used by the {@link TCPReaderMirror}
	 * 
	 * @param socket the socket to use for the communication
	 */
	public void setSocket(Socket socket)
	{
		this.socket=socket;
	}
	
	/**
	 * Sets the local {@link GRSRegistry} key associated with the {@link IBuffer} that needs to be mirrored
	 * 
	 * @param key the key associated with the served {@link IBuffer}
	 */
	public void setKey(String key)
	{
		this.key=key;
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
		if(this.socket==null) throw new GRS2ProxyMirrorInvalidOperationException("No socket defined");
		if(this.key==null) throw new GRS2ProxyMirrorInvalidOperationException("No key defined");
		//If this is handled here instead of the caller the behavior of the remaining readers and existing writer will be compromised
		if(this.getState()!=State.NEW) throw new GRS2ProxyMirrorInvalidOperationException("Mirroring already in progress");
		this.setDaemon(true);
		this.setName("writer mirror ("+this.key+")");
		this.start();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This method actually invokes the respective {@link TCPWriterMirror#dispose(boolean)} with an argument of false. This is
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
	 * gracefully exit this part of the protocol but also notify the respective {@link TCPReaderMirror} to stop the mirroring
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
			try{ if(this.buffer!=null) this.buffer.dispose(); } catch (Exception e) { }
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This method is not supported in the {@link TCPWriterMirror}
	 * </p>
	 * 
	 * @throws GRS2ProxyMirrorInvalidOperationException if the method is invoked
	 * 
	 * @see gr.uoa.di.madgik.grs.proxy.mirror.IMirror#pollPartial(long, int)
	 */
	public boolean pollPartial(long recordIndex, int fieldIndex) throws GRS2ProxyMirrorInvalidOperationException
	{
		throw new GRS2ProxyMirrorInvalidOperationException("Operation not supported in writer mirror");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This method is not supported in the {@link TCPWriterMirror}
	 * </p>
	 * 
	 * @throws GRS2ProxyMirrorInvalidOperationException if the method is invoked
	 * 
	 * @see gr.uoa.di.madgik.grs.proxy.mirror.IMirror#requestPartial(long, int, gr.uoa.di.madgik.grs.buffer.IBuffer.TransportOverride, java.lang.Object)
	 */
	public long requestPartial(long recordIndex, int fieldIndex, TransportOverride override, Object notify) throws GRS2ProxyMirrorInvalidOperationException
	{
		throw new GRS2ProxyMirrorInvalidOperationException("Operation not supported in writer mirror");
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * In the context of the mirroring procedure, the execution of this thread includes the following actions.<br/>
	 * Initially, the local {@link GRSRegistry} is queried for the {@link IBuffer} to serve and its state is checked
	 * to make sure that it is available for mirroring.<br/> 
	 * The main protocol loop is then initialized. On every iteration, the reader request is read and the needed {@link Record}s
	 * are returned limited based on the mirroring factor defined by the {@link IBuffer} and the available records. Additionally,
	 * if there are requests for partial data to be transfered, they are included in the response.<br/>
	 * Additionally, after the request is read, the incoming events are read and emitted in the local {@link IBuffer} and
	 * after all record data is send, the available events are send.<bt/>
	 * The status of the local mirroring procedure is also transmitted, while the remote state received is checked to see
	 * if the connection should be teardown.
	 * </p>
	 * 
	 * @see java.lang.Thread#run()
	 */
	
	
	public void writerAcceptRequests() throws Exception {
		logger.log(Level.FINEST, "HTTPWriterMirror : parseRequest....");
		this.parseRequest();
		logger.log(Level.FINEST, "HTTPWriterMirror : parseRequest....OK");

		logger.log(Level.FINEST, "HTTPWriterMirror : retrieveEvents....");
		this.retrieveEvents();
		logger.log(Level.FINEST, "HTTPWriterMirror : retrieveEvents....OK");
	}
	
	private boolean writerResponse() throws Exception {
		logger.log(Level.FINEST, "HTTPWriterMirror : flushPartialRequests....");
		this.flushPartialRequests();
		logger.log(Level.FINEST, "HTTPWriterMirror : flushPartialRequests....OK");

		logger.log(Level.FINEST, "HTTPWriterMirror : flushForwardBuffer....");
		this.flushForwardBuffer();
		logger.log(Level.FINEST, "HTTPWriterMirror : flushForwardBuffer....OK");

		logger.log(Level.FINEST, "HTTPWriterMirror : flushEvents....");
		this.flushEvents();
		logger.log(Level.FINEST, "HTTPWriterMirror : flushEvents....OK");

		logger.log(Level.FINEST, "HTTPWriterMirror : flushStatus....");
		boolean breakLoop = this.flushStatus();
		logger.log(Level.FINEST, "HTTPWriterMirror : flushStatus....OK");
		
		
		this.out.flush();
		
		return breakLoop;
	}
	
	private boolean flushStatus() throws Exception {
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
		
		this.out.writeInt(value);
		
		return (value == 2);
	}
	
	public void run()
	{
		int cnt = 0;
		try
		{
			this.buffer=GRSRegistry.Registry.getBuffer(this.key);
			if(this.buffer==null && (this.state==MirroringState.Close || this.state==MirroringState.Purged)) throw new GRS2ProxyMirrorInvalidOperationException("Mirroring is already closed. Cannot initialize the protocol");
			else if (this.buffer==null && this.state==MirroringState.Open) throw new GRS2ProxyMirrorInvalidOperationException("No registry entry found for key "+this.key);

			this.out= new ObjectOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));
			//this.out= new ObjectOutputStream(this.socket.getOutputStream());
			
			try{this.flushBufferConfig();}catch(Exception ex){throw new GRS2ProxyMirrorProtocolErrorException("Could not complete buffer configuration mirroring", ex);}
			while(true)
			{
				
				
				if (this.state == MirroringState.Purged || this.state == MirroringState.Close)
					break;
				try {
					this.writerAcceptRequests();
				} catch (Exception ex) {
					throw new GRS2ProxyMirrorProtocolErrorException("Could not parse input request", ex);
				}
				
				if (doDispose) {
					logger.log(Level.FINEST, "Writer mirror received dispose request");
					break;
				}
				
				if (this.state == MirroringState.Purged)
					break;

				if (writerResponse())
					break;
				
				
//				if(this.state==MirroringState.Purged) break; //this shouldn't happen
//				try{this.parseRequest();}catch(Exception ex){throw new GRS2ProxyMirrorProtocolErrorException("Could not parse input request", ex);}
//				if(doDispose) logger.log(Level.FINEST, "Writer mirror received dispose request");
//				try{this.retrieveEvents();}catch(Exception ex){throw new GRS2ProxyMirrorProtocolErrorException("Could not parse input request", ex);}
//				if(this.state==MirroringState.Purged || this.doDispose) break;  //the purge part shouldn't happen
//				if(this.state==MirroringState.Close)
//				{
//					logger.log(Level.FINEST, "Disposing writer mirror");
//					//no partial results will be delivered
//					this.out.writeBoolean(false);
//					//no records will be delivered
//					this.out.writeBoolean(false);
//					//no events will be delivered
//					this.out.writeBoolean(false);
//					//teardown
//					this.out.writeShort(2); //dispose
//					this.out.flush();
//					break;
//				}
////				this.flushPartialRequests();
//				this.flushForwardBuffer();
//				this.flushEvents();
//				//teardown
//				boolean isClosed=this.buffer.getStatus()==Status.Close && this.buffer.availableRecords()==0;
//				if(isClosed) this.out.writeShort(1); //close
//				else this.out.writeShort(0); //open
//				this.out.flush();
				
				if (++cnt % 1000 == 0) {
					this.out.reset();
//					if (in.markSupported()) {
//						this.in.reset();
//					}
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
	
	
	
	
	
	private void flushBufferConfig() throws IOException, GRS2Exception
	{
		BufferConfig bufferConfig = new BufferConfig();
		bufferConfig.bufferClassName = this.buffer.getClass().getName();
		bufferConfig.capacity = this.buffer.getCapacity();
		bufferConfig.concurrentPartialTimeout = this.buffer.getConcurrentPartialCapacity();
		bufferConfig.inactivityTimeout = this.buffer.getInactivityTimeout();
		bufferConfig.inactivityTimeoutUnit = this.buffer.getInactivityTimeUnit();
		bufferConfig.transportDirective = this.buffer.getTransportDirective();
		bufferConfig.recordDefinitions = new ArrayList<RecordDefinition>(Arrays.asList(this.buffer.getRecordDefinitions()));
		
		CompressedObjectStream.writeObject(bufferConfig, this.out);
		//this.out.writeObject(bufferConfig);

		this.out.flush();
	}
	
	
	private void parseRequest() throws IOException, ClassNotFoundException
	{	
		Request request = (Request) CompressedObjectStream.readObject(in);
		
		this.doDispose=request.doDispose;
		this.readerNeeded=request.needed;
		boolean simulateActivity = request.simulateActivity;
		if(simulateActivity == true)
			this.buffer.markSimulateActivity();
		
		if (request.entries != null) {
			int len = request.entries.size();
			
			this.partials=new PartialRequestEntry[len];
			int i = 0;
			for (PartialRequestEntry pre : request.entries) {
				this.partials[i] = pre;
				i++;
			}
		}
	}
	
	private void retrieveEvents() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, GRS2RecordSerializationException, GRS2BufferException
	{
		ArrayList<BufferEvent> events = (ArrayList<BufferEvent>) CompressedObjectStream.readObject(in);
//		ArrayList<BufferEvent> events = (ArrayList<BufferEvent>) in.readObject();
		
		for (BufferEvent event : events) {
			this.buffer.emit(event);
		}
		
	}
	
	private void flushEvents() throws IOException, GRS2BufferException, GRS2RecordSerializationException
	{
		ArrayList<BufferEvent> events = new ArrayList<BufferEvent>();
		
		while(true)
		{
			BufferEvent event=this.buffer.receive(EventSource.Writer);
			if(event==null) break;
			
			events.add(event);
		}
//		out.writeObject(events);
		CompressedObjectStream.writeObject(events, this.out);
	}
	
	
	
	
	
	private void flushPartialRequests() throws GRS2Exception, IOException
	{
		int num=0;
		ArrayList<PartialRequest> partialRequests = new ArrayList<PartialRequest>(this.partials.length);
		
		for(PartialRequestEntry entry : this.partials)
		{
			Record rec=this.buffer.locate(entry.getRecordIndex());
			if(rec==null) throw new GRS2ProxyMirrorInvalidOperationException("Invalid record index provided");
			Field[] fields=rec.getFields();
			if(fields==null) throw new GRS2ProxyMirrorInvalidOperationException("No fields to marshal");
			if(entry.getFieldIndex()<0 || entry.getFieldIndex()>=fields.length) throw new GRS2ProxyMirrorInvalidOperationException("Invalid field index provided");
			Field f=fields[entry.getFieldIndex()];
			
			PartialRequest pr = new PartialRequest();
			pr.partRecordIndex = entry.getRecordIndex();
			pr.fieldIndex = entry.getFieldIndex();
			pr.field = f;
			pr.override = entry.getOverride();
			
			num+=1;
			
			partialRequests.add(pr);
		}
//		out.writeObject(partialRequests);
		CompressedObjectStream.writeObject(partialRequests, this.out);
		
		for(PartialRequest pr : partialRequests) {
			FileField ff = (FileField) pr.field;
			ff.extendWriteObject(out, pr.override);
//			ff.extendReceive(in, pr.override);
		}
		
	}
	
	
	
	private void flushForwardBuffer() throws GRS2Exception, IOException
	{
		long available=this.buffer.availableRecords();
		long mirrorBuffer=this.buffer.getMirrorBuffer();
		long toMirror=this.readerNeeded;
		if(toMirror>available) toMirror=available;
		if(toMirror>mirrorBuffer) toMirror=mirrorBuffer;
		
		
		ArrayList<Record> records = new ArrayList<Record>((int)toMirror);
		
		for(int i=0;i<toMirror;i+=1)
		{
			Record rec=this.buffer.get();
			if(rec==null)
			{
				if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING,"Record not available although declared as available");
				break;
			}
			
			records.add(rec);
		}
		this.out.writeObject(records);
//		CompressedObjectStream.writeObject(records, this.out);
	}
}


class PartialRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	
	Long partRecordIndex;
	Integer fieldIndex;
	TransportOverride override;
	Field field;
	
}

class BufferConfig implements Serializable {
	private static final long serialVersionUID = 1L;
	
	String bufferClassName;
	Integer capacity;
	Integer concurrentPartialTimeout;
	Long inactivityTimeout;
	TimeUnit inactivityTimeoutUnit;
	TransportDirective transportDirective;
	ArrayList<RecordDefinition> recordDefinitions;
}

class Request implements Serializable {
	private static final long serialVersionUID = 1L;
	
	Boolean doDispose;
	Integer needed;
	Boolean simulateActivity;
	ArrayList<PartialRequestEntry> entries;
	
}