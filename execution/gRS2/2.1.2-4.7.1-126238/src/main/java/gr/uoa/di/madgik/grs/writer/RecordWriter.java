package gr.uoa.di.madgik.grs.writer;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import gr.uoa.di.madgik.grs.GRS2Exception;
import gr.uoa.di.madgik.grs.buffer.BufferFactory;
import gr.uoa.di.madgik.grs.buffer.GRS2BufferException;
import gr.uoa.di.madgik.grs.buffer.GRS2BufferInvalidOperationException;
import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.events.BufferEvent;
import gr.uoa.di.madgik.grs.events.BufferEvent.EventSource;
import gr.uoa.di.madgik.grs.proxy.GRS2ProxyException;
import gr.uoa.di.madgik.grs.proxy.IWriterProxy;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.record.GRS2ExceptionWrapper;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.exception.GRS2ThrowableWrapper;
import gr.uoa.di.madgik.grs.record.exception.GRS2ThrowableWrapperException;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.registry.GRSRegistry;
import gr.uoa.di.madgik.grs.store.buffer.IBufferStore;
import gr.uoa.di.madgik.grs.utils.ProgressiveTimeoutGenerator;

public class RecordWriter<T extends Record> implements IRecordWriter<T>
{
	private static Logger logger=Logger.getLogger(RecordWriter.class.getName());
	public static int DefaultBufferCapacity=50;
	public static int DefaultConcurrentPartialCapacity=1;
	public static float DefaultThreshold=0.5f;
	public static float DefaultMirrorBufferFactor=0.5f;
	public static long DefaultInactivityTimeout=300;
	public static TimeUnit DefaultInactivityTimeUnit=TimeUnit.SECONDS;
	private IBuffer buffer=null;
	private URI locator=null;
//	private Object thresholdNotificationObject=null;
	private Object immediateNotificationObject=null;

	/**
	 * Creates  a new instance
	 * 
	 * @see BufferFactory#getBuffer()
	 * @see IBuffer#setRecordDefinitions(RecordDefinition[])
	 * 
	 * @param proxy the {@link IWriterProxy} to use to publish the authored {@link IBuffer}
	 * @param definitions the {@link RecordDefinition}s of the {@link Record}s added
	 * @throws GRS2WriterException the operation could not be completed
	 */
	public RecordWriter(IWriterProxy proxy,RecordDefinition[] definitions) throws GRS2WriterException
	{
		try
		{
			logger.log(Level.FINE,"Initializing record writer");
			this.buffer=BufferFactory.getBuffer();
			this.buffer.setRecordDefinitions(definitions);
			this.buffer.setCapacity(RecordWriter.DefaultBufferCapacity);
			this.buffer.setConcurrentPartialCapacity(RecordWriter.DefaultConcurrentPartialCapacity);
//			this.buffer.setNotificationThreshold(RecordWriter.DefaultThreshold);
			this.buffer.setMirrorBuffer((int)Math.ceil(RecordWriter.DefaultBufferCapacity*RecordWriter.DefaultMirrorBufferFactor));
			this.buffer.setInactivityTimeout(RecordWriter.DefaultInactivityTimeout);
			this.buffer.setInactivityTimeUnit(RecordWriter.DefaultInactivityTimeUnit);
		//	this.thresholdNotificationObject=this.buffer.getWriterThresholdNotificationObject();
			this.immediateNotificationObject=this.buffer.getWriterImmediateNotificationObject();
			this.buffer.initialize();
			this.initLocator(proxy);
		}catch(GRS2Exception ex)
		{
			throw new GRS2WriterException("Could not initialize writer", ex);
		}
	}
	
	/**
	 * Creates  a new instance
	 * 
	 * @see BufferFactory#getBuffer()
	 * @see IBuffer#setRecordDefinitions(RecordDefinition[])
	 * @see IBuffer#setCapacity(int)
	 * @see IBuffer#setConcurrentPartialCapacity(int)
	 * @see IBuffer#setMirrorBuffer(int)
	 * 
	 * @param proxy the {@link IWriterProxy} to use to publish the authored {@link IBuffer}
	 * @param definitions the {@link RecordDefinition}s of the {@link Record}s added
	 * @param capacity the capacity of the underlying {@link IBuffer}
	 * @param concurrentPartialCapacity the concurrent partial record capacity of the underlying {@link IBuffer}
	 * @param mirrorSizeFactor the factor to calculate the mirror threshold size 
	 * @throws GRS2WriterException the operation could not be completed
	 */
	public RecordWriter(IWriterProxy proxy,RecordDefinition[] definitions, int capacity, int concurrentPartialCapacity, float mirrorSizeFactor) throws GRS2WriterException
	{
		try
		{
			logger.log(Level.FINE,"Initializing record writer");
			this.buffer=BufferFactory.getBuffer();
			this.buffer.setRecordDefinitions(definitions);
			this.buffer.setCapacity(capacity);
			this.buffer.setConcurrentPartialCapacity(concurrentPartialCapacity);
//			this.buffer.setNotificationThreshold(RecordWriter.DefaultThreshold);
			this.buffer.setMirrorBuffer((int)Math.ceil(capacity*mirrorSizeFactor));
			this.buffer.setInactivityTimeout(RecordWriter.DefaultInactivityTimeout);
			this.buffer.setInactivityTimeUnit(RecordWriter.DefaultInactivityTimeUnit);
//			this.thresholdNotificationObject=this.buffer.getWriterThresholdNotificationObject();
			this.immediateNotificationObject=this.buffer.getWriterImmediateNotificationObject();
			this.buffer.initialize();
			this.initLocator(proxy);
		}catch(GRS2Exception ex)
		{
			throw new GRS2WriterException("Could not initialize writer", ex);
		}
	}
	
	/**
	 * Creates  a new instance
	 * 
	 * @see BufferFactory#getBuffer()
	 * @see IBuffer#setRecordDefinitions(RecordDefinition[])
	 * @see IBuffer#setCapacity(int)
	 * @see IBuffer#setConcurrentPartialCapacity(int)
	 * @see IBuffer#setNotificationThreshold(float)
	 * @see IBuffer#setMirrorBuffer(int)
	 * 
	 * @param proxy the {@link IWriterProxy} to use to publish the authored {@link IBuffer}
	 * @param definitions the {@link RecordDefinition}s of the {@link Record}s added
	 * @param capacity the capacity of the underlying {@link IBuffer}
	 * @param concurrentPartialCapacity the concurrent partial record capacity of the underlying {@link IBuffer}
	 * @param mirrorSizeFactor the factor to calculate the mirror threshold size 
	 * @param threshold the notification threshold factor
	 * @throws GRS2WriterException the operation could not be completed
	 */
//	public RecordWriter(IWriterProxy proxy,RecordDefinition[] definitions, int capacity, int concurrentPartialCapacity,float mirrorSizeFactor, float threshold) throws GRS2WriterException
//	{
//		try
//		{
//			logger.log(Level.FINE,"Initializing record writer");
//			this.buffer=BufferFactory.getBuffer();
//			this.buffer.setRecordDefinitions(definitions);
//			this.buffer.setCapacity(capacity);
//			this.buffer.setConcurrentPartialCapacity(concurrentPartialCapacity);
//			this.buffer.setNotificationThreshold(threshold);
//			this.buffer.setMirrorBuffer((int)Math.floor(capacity*mirrorSizeFactor));
//			this.buffer.setInactivityTimeout(RecordWriter.DefaultInactivityTimeout);
//			this.buffer.setInactivityTimeUnit(RecordWriter.DefaultInactivityTimeUnit);
//			this.thresholdNotificationObject=this.buffer.getWriterThresholdNotificationObject();
//			this.immediateNotificationObject=this.buffer.getWriterImmediateNotificationObject();
//			this.buffer.initialize();
//			this.initLocator(proxy);
//		}catch(GRS2Exception ex)
//		{
//			throw new GRS2WriterException("Could not initialize writer", ex);
//		}
//	}
	
	/**
	 * Creates  a new instance
	 * 
	 * @see BufferFactory#getBuffer()
	 * @see IBuffer#setRecordDefinitions(RecordDefinition[])
	 * @see IBuffer#setCapacity(int)
	 * @see IBuffer#setConcurrentPartialCapacity(int)
	 * @see IBuffer#setNotificationThreshold(float)
	 * @see IBuffer#setMirrorBuffer(int)
	 * @see IBuffer#setInactivityTimeout(long)
	 * @see IBuffer#setInactivityTimeUnit(TimeUnit)
	 * @see IBuffer#getWriterThresholdNotificationObject()
	 * @see IBuffer#initialize()
	 * 
	 * @param proxy the {@link IWriterProxy} to use to publish the authored {@link IBuffer}
	 * @param definitions the {@link RecordDefinition}s of the {@link Record}s added
	 * @param capacity the capacity of the underlying {@link IBuffer}
	 * @param concurrentPartialCapacity the concurrent partial record capacity of the underlying {@link IBuffer}
	 * @param mirrorSizeFactor the factor to calculate the mirror threshold size 
	 * @param inactivityTimeout the inactivity timeout after which the {@link IBuffer} is considered eligible for 
	 * disposal. This value is interpreted in conjunction with the inactivityTimeUnit value
	 * @param inactivityTimeUnit the inactivity timeout unit after which the {@link IBuffer} is considered eligible for 
	 * disposal. This value is interpreted in conjunction with the inactivityTimeout value
	 * @throws GRS2WriterException the operation could not be completed
	 */
	public RecordWriter(IWriterProxy proxy,RecordDefinition[] definitions, int capacity, int concurrentPartialCapacity,float mirrorSizeFactor, long inactivityTimeout, TimeUnit inactivityTimeUnit) throws GRS2WriterException
	{
		try
		{
			logger.log(Level.FINE,"Initializing record writer");
			this.buffer=BufferFactory.getBuffer();
			this.buffer.setRecordDefinitions(definitions);
			this.buffer.setCapacity(capacity);
			this.buffer.setConcurrentPartialCapacity(concurrentPartialCapacity);
		//	this.buffer.setNotificationThreshold(threshold); TODO add threshold parameter (second to last)  @param threshold the notification threshold factor
			this.buffer.setMirrorBuffer((int)Math.ceil(capacity*mirrorSizeFactor));
			this.buffer.setInactivityTimeout(inactivityTimeout);
			this.buffer.setInactivityTimeUnit(inactivityTimeUnit);
		//	this.thresholdNotificationObject=this.buffer.getWriterThresholdNotificationObject();
			this.immediateNotificationObject=this.buffer.getWriterImmediateNotificationObject();
			this.buffer.initialize();
			this.initLocator(proxy);
		}catch(GRS2Exception ex)
		{
			throw new GRS2WriterException("Could not initialize writer", ex);
		}
	}
	
	/**
	 * Creates a new instance using the configuration retrieved from an {@link IRecordReader}. The record definitions, capacity and
	 * concurrent partial capacity configuration parameters are duplicated from the reader and all other configuration parameters
	 * are set to their default values.
	 * 
	 * @see BufferFactory#getBuffer()
	 * @see IBuffer#setRecordDefinitions(RecordDefinition[])
	 * 
	 * @param proxy the {@link IWriterProxy} to use to publish the authored {@link IBuffer}
	 * @param reader the {@link IRecordReader} to retrieve configuration from
	 * @throws GRS2WriterException the operation could not be completed
	 */
	@SuppressWarnings("unchecked")
	public RecordWriter(IWriterProxy proxy, IRecordReader reader) throws GRS2WriterException {
		try
		{
			logger.log(Level.FINE, "Initializing record writer");
			this.buffer=BufferFactory.getBuffer();
			RecordDefinition defs[] = new RecordDefinition[reader.getRecordDefinitions().length];
			int i = 0;
			for(RecordDefinition def : reader.getRecordDefinitions()) {
				defs[i] = def.getClass().newInstance();
				defs[i].copyFrom(def);
				i++;
			}
			this.buffer.setRecordDefinitions(defs);
			this.buffer.setCapacity(reader.getCapacity());
			this.buffer.setConcurrentPartialCapacity(reader.getConcurrentPartialCapacity());
			this.buffer.setMirrorBuffer((int)Math.ceil(RecordWriter.DefaultBufferCapacity*RecordWriter.DefaultMirrorBufferFactor));
			this.buffer.setInactivityTimeout(RecordWriter.DefaultInactivityTimeout);
			this.buffer.setInactivityTimeUnit(RecordWriter.DefaultInactivityTimeUnit);
		//	this.thresholdNotificationObject=this.buffer.getWriterThresholdNotificationObject();
			this.immediateNotificationObject=this.buffer.getWriterImmediateNotificationObject();
			this.buffer.initialize();
			this.initLocator(proxy);
		}catch(Exception ex)
		{
			throw new GRS2WriterException("Could not initialize writer", ex);
		}
	}
	
	/**
	 * Creates a new instance using the same record definitions as the ones of the supplied {@link IRecordReader}.
	 * Capacity, concurrent partial capacity and mirror buffer factor configuration parameters can be configured to a non-default value.
	 * All other configuration parameters are set to their default values.
	 * 
	 * @see BufferFactory#getBuffer()
	 * @see IBuffer#setRecordDefinitions(RecordDefinition[])
	 * @see IBuffer#setCapacity(int)
	 * @see IBuffer#setConcurrentPartialCapacity(int)
	 * @see IBuffer#setMirrorBuffer(int)
	 * 
	 * @param proxy the {@link IWriterProxy} to use to publish the authored {@link IBuffer}
	 * @param reader the {@link IRecordReader} to retrieve record definitions from
	 * @param capacity the capacity of the underlying {@link IBuffer}
	 * @param concurrentPartialCapacity the concurrent partial record capacity of the underlying {@link IBuffer}
	 * @param mirrorSizeFactor the factor to calculate the mirror threshold size 
	 * @throws GRS2WriterException the operation could not be completed
	 */
	@SuppressWarnings("unchecked")
	public RecordWriter(IWriterProxy proxy, IRecordReader reader, int capacity, int concurrentPartialCapacity, float mirrorSizeFactor) throws GRS2WriterException {
		try
		{
			logger.log(Level.FINE, "Initializing record writer");
			this.buffer=BufferFactory.getBuffer();
			RecordDefinition defs[] = new RecordDefinition[reader.getRecordDefinitions().length];
			int i = 0;
			for(RecordDefinition def : reader.getRecordDefinitions()) {
				defs[i] = def.getClass().newInstance();
				defs[i].copyFrom(def);
				i++;
			}
			this.buffer.setRecordDefinitions(defs);
			this.buffer.setCapacity(capacity);
			this.buffer.setConcurrentPartialCapacity(concurrentPartialCapacity);
			this.buffer.setMirrorBuffer((int)Math.ceil(capacity*mirrorSizeFactor));
			this.buffer.setInactivityTimeout(RecordWriter.DefaultInactivityTimeout);
			this.buffer.setInactivityTimeUnit(RecordWriter.DefaultInactivityTimeUnit);
		//	this.thresholdNotificationObject=this.buffer.getWriterThresholdNotificationObject();
			this.immediateNotificationObject=this.buffer.getWriterImmediateNotificationObject();
			this.buffer.initialize();
			this.initLocator(proxy);
		}catch(Exception ex)
		{
			throw new GRS2WriterException("Could not initialize writer", ex);
		}
	}
	
	
	/**
	 * Creates a new instance using the same record definitions as the ones of the supplied {@link IRecordReader}.
	 * All configuration parameters except for inactivity timeout can be configured to a non-default value.
	 * Inactivity timeout and unit are set to their default values.
	 * 
	 * @see BufferFactory#getBuffer()
	 * @see IBuffer#setRecordDefinitions(RecordDefinition[])
	 * @see IBuffer#setCapacity(int)
	 * @see IBuffer#setConcurrentPartialCapacity(int)
	 * @see IBuffer#setNotificationThreshold(float)
	 * @see IBuffer#setMirrorBuffer(int)
	 * @see IBuffer#setNotificationThreshold(float)
	 * @see IBuffer#getWriterThresholdNotificationObject()
	 * @see IBuffer#initialize()
	 * 
	 * @param proxy the {@link IWriterProxy} to use to publish the authored {@link IBuffer}
	 * @param reader the {@link IRecordReader} to retrieve record definitions from
	 * @param capacity the capacity of the underlying {@link IBuffer}
	 * @param concurrentPartialCapacity the concurrent partial record capacity of the underlying {@link IBuffer}
	 * @param mirrorSizeFactor the factor to calculate the mirror threshold size 
	 * @param threshold the notification threshold factor
	 * @throws GRS2WriterException the operation could not be completed
	 */
//	public RecordWriter(IWriterProxy proxy, IRecordReader reader, int capacity, int concurrentPartialCapacity, float mirrorSizeFactor, float threshold) throws GRS2WriterException {
//		try
//		{
//			logger.log(Level.FINE, "Initializing record writer");
//			this.buffer=BufferFactory.getBuffer();
//			RecordDefinition defs[] = new RecordDefinition[reader.getRecordDefinitions().length];
//			int i = 0;
//			for(RecordDefinition def : reader.getRecordDefinitions()) {
//				defs[i] = def.getClass().newInstance();
//				defs[i].copyFrom(def);
//				i++;
//			}
//			this.buffer.setRecordDefinitions(defs);
//			this.buffer.setCapacity(capacity);
//			this.buffer.setConcurrentPartialCapacity(concurrentPartialCapacity);
//			this.buffer.setMirrorBuffer((int)Math.floor(capacity*mirrorSizeFactor));
//		//	this.buffer.setNotificationThreshold(threshold);
//			this.buffer.setInactivityTimeout(RecordWriter.DefaultInactivityTimeout);
//			this.buffer.setInactivityTimeUnit(RecordWriter.DefaultInactivityTimeUnit);
//		//	this.thresholdNotificationObject=this.buffer.getWriterThresholdNotificationObject();
//			this.immediateNotificationObject=this.buffer.getWriterImmediateNotificationObject();
//			this.buffer.initialize();
//			this.initLocator(proxy);
//		}catch(Exception ex)
//		{
//			throw new GRS2WriterException("Could not initialize writer", ex);
//		}
//	}
	
	/**
	 * Creates a new instance using the same record definitions as the ones of the supplied {@link IRecordReader}.
	 * All configuration parameters can be configured to a non-default value.
	 * 
	 * @see BufferFactory#getBuffer()
	 * @see IBuffer#setRecordDefinitions(RecordDefinition[])
	 * @see IBuffer#setCapacity(int)
	 * @see IBuffer#setConcurrentPartialCapacity(int)
	 * @see IBuffer#setNotificationThreshold(float)
	 * @see IBuffer#setMirrorBuffer(int)
	 * @see IBuffer#setInactivityTimeout(long)
	 * @see IBuffer#setInactivityTimeUnit(TimeUnit)
	 * @see IBuffer#getWriterThresholdNotificationObject()
	 * @see IBuffer#initialize()
	 * 
	 * @param proxy the {@link IWriterProxy} to use to publish the authored {@link IBuffer}
	 * @param reader the {@link IRecordReader} to retrieve record definitions from
	 * @param capacity the capacity of the underlying {@link IBuffer}
	 * @param concurrentPartialCapacity the concurrent partial record capacity of the underlying {@link IBuffer}
	 * @param mirrorSizeFactor the factor to calculate the mirror threshold size 
	 * @param inactivityTimeout the inactivity timeout after which the {@link IBuffer} is considered eligible for 
	 * disposal. This value is interpreted in conjunction with the inactivityTimeUnit value
	 * @param inactivityTimeUnit the inactivity timeout unit after which the {@link IBuffer} is considered eligible for 
	 * disposal. This value is interpreted in conjunction with the inactivityTimeout value
	 * @throws GRS2WriterException the operation could not be completed
	 */
	@SuppressWarnings("unchecked")
	public RecordWriter(IWriterProxy proxy, IRecordReader reader, int capacity, int concurrentPartialCapacity, float mirrorSizeFactor, long inactivityTimeout, TimeUnit inactivityTimeUnit) throws GRS2WriterException {
		try
		{
			logger.log(Level.FINE, "Initializing record writer");
			this.buffer=BufferFactory.getBuffer();
			RecordDefinition defs[] = new RecordDefinition[reader.getRecordDefinitions().length];
			int i = 0;
			for(RecordDefinition def : reader.getRecordDefinitions()) {
				defs[i] = def.getClass().newInstance();
				defs[i].copyFrom(def);
				i++;
			}
			this.buffer.setRecordDefinitions(defs);
			this.buffer.setCapacity(capacity);
			this.buffer.setConcurrentPartialCapacity(concurrentPartialCapacity);
			this.buffer.setMirrorBuffer((int)Math.ceil(capacity*mirrorSizeFactor));
			this.buffer.setInactivityTimeout(inactivityTimeout);
			this.buffer.setInactivityTimeUnit(inactivityTimeUnit);
		//	this.thresholdNotificationObject=this.buffer.getWriterThresholdNotificationObject();
			this.immediateNotificationObject=this.buffer.getWriterImmediateNotificationObject();
			this.buffer.initialize();
			this.initLocator(proxy);
		}catch(Exception ex)
		{
			throw new GRS2WriterException("Could not initialize writer", ex);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordWriter#setBufferStore(IBufferStore)
	 */
	public void setBufferStore(IBufferStore store) throws GRS2WriterException
	{
		try
		{
			this.buffer.setBufferStore(store);
		}catch(GRS2Exception ex)
		{
			throw new GRS2WriterException("Could not set buffer store", ex);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordWriter#getCapacity()
	 */
	public int getCapacity() throws GRS2WriterException
	{
		try
		{
			return this.buffer.getCapacity();
		}catch(GRS2Exception ex)
		{
			throw new GRS2WriterException("unable to retrieve capacity", ex);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordWriter#getThreshold()
	 */
//	public float getThreshold() throws GRS2WriterException
//	{
//		try
//		{
//			return this.buffer.getNotificationThreshold();
//		}catch(GRS2Exception ex)
//		{
//			throw new GRS2WriterException("unable to retrieve notification threshold", ex);
//		}
//	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordWriter#getThresholdNotificationObject()
	 */
//	public Object getThresholdNotificationObject()
//	{
//		return this.thresholdNotificationObject;
//	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordWriter#setThreshold(float)
	 */
//	public void setThreshold(float threshold) throws GRS2WriterException
//	{
//		try
//		{
//			this.buffer.setNotificationThreshold(threshold);
//		}catch(GRS2Exception ex)
//		{
//			throw new GRS2WriterException("Could not set threshold factor", ex);
//		}
//	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordWriter#getLocator()
	 */
	public URI getLocator() throws GRS2WriterException
	{
		if(this.locator==null) throw new GRS2WriterInvalidOperationException("Writer not properly initialized. No locator available");
		return this.locator;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordWriter#getStatus()
	 */
	public synchronized IBuffer.Status getStatus()
	{
		return this.buffer.getStatus();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordWriter#availableRecords()
	 */
	public synchronized int availableRecords() throws GRS2WriterException
	{
		try
		{
			return this.buffer.availableRecords();
		}catch(GRS2Exception ex)
		{
			throw new GRS2WriterException("Could not retrieve available number of records", ex);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordWriter#totalRecords()
	 */
	public synchronized long totalRecords() throws GRS2WriterException
	{
		try
		{
			return this.buffer.totalRecords();
		}catch(GRS2Exception ex)
		{
			throw new GRS2WriterException("Could not retrieve total number of records", ex);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordWriter#put(Record)
	 */
	public synchronized boolean put(T record) throws GRS2WriterException
	{
		try
		{
			return this.buffer.put(record);
		}catch(GRS2Exception ex)
		{
			throw new GRS2WriterException("Could not put record", ex);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordWriter#put(Record, long, TimeUnit)
	 */
	public synchronized boolean put(T record,long timeout, TimeUnit unit) throws GRS2WriterException
	{
		try
		{
			boolean success=false;
			ProgressiveTimeoutGenerator ptf = new ProgressiveTimeoutGenerator(unit.toMillis(timeout));
			while (true)
			{
				if(this.buffer.getStatus()!=Status.Open) break;
				success=this.buffer.put(record);
				if(success || !ptf.hasNext()) break;
				synchronized (this.immediateNotificationObject)
				{
					try { this.immediateNotificationObject.wait(ptf.next()); } catch (InterruptedException e){ break; }
				}
			}
			return success;
		}catch(GRS2Exception ex)
		{
			throw new GRS2WriterException("Could not put record", ex);
		}
	}
	
	public synchronized boolean put(Throwable th,long timeout, TimeUnit unit) throws GRS2WriterException
	{
		try
		{
			boolean success=false;
			ProgressiveTimeoutGenerator ptf = new ProgressiveTimeoutGenerator(unit.toMillis(timeout));
			while (true)
			{
				if(this.buffer.getStatus()!=Status.Open) break;
				
				GenericRecord rec = new GRS2ExceptionWrapper(th);
				rec.setFields(new Field[]{});
				success=this.buffer.put(rec);
				if(success || !ptf.hasNext()) break;
				synchronized (this.immediateNotificationObject)
				{
					try { this.immediateNotificationObject.wait(ptf.next()); } catch (InterruptedException e){ break; }
				}
			}
			return success;
		}catch(GRS2Exception ex)
		{
			throw new GRS2WriterException("Could not put record", ex);
		} 
//		catch (GRS2ThrowableWrapperException e) {
//			e.printStackTrace();
//			throw new GRS2WriterException("Could not put exception", e);
//		}
	}

	private T doImport(T record, int newDefinitionIndex) throws GRS2Exception {
		record.show();
		record.makeAvailable();
		record.unbind();
		record.setDefinitionIndex(newDefinitionIndex);
		record.makeLocal();
		return record;
	}
	
	/**	
	 * {@inheritDoc}
	 * 
	 * @see IRecordWriter#importRecord(Record)
	 */
	public boolean importRecord(T record) throws GRS2Exception {
		this.doImport(record, record.getDefinitionIndex());
		return this.put(record);
	}
	
	/**	
	 * {@inheritDoc}
	 * 
	 * @see IRecordWriter#importRecord(Record, long, TimeUnit)
	 */
	public boolean importRecord(T record, long timeout, TimeUnit unit) throws GRS2Exception {
		this.doImport(record, record.getDefinitionIndex());
		return this.put(record, timeout, unit);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordWriter#importRecord(Record, int)
	 */
	public boolean importRecord(T record, int newDefinitionIndex) throws GRS2Exception {
		this.doImport(record, newDefinitionIndex);
		return this.put(record);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordWriter#importRecord(Record, int, long, TimeUnit)
	 */
	public boolean importRecord(T record, int newDefinitionIndex, long timeout, TimeUnit unit) throws GRS2Exception {
		this.doImport(record, newDefinitionIndex);
		return this.put(record, timeout, unit);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordWriter#close()
	 */
	public synchronized void close() throws GRS2WriterException
	{
		try
		{
			this.buffer.close();
		}catch(GRS2Exception ex)
		{
			throw new GRS2WriterException("Could not close writer", ex);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordWriter#dispose()
	 */
	public synchronized void dispose()
	{
		this.buffer.dispose();
		this.buffer=null;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordWriter#emit(BufferEvent)
	 */
	public synchronized void emit(BufferEvent event) throws GRS2WriterException, GRS2WriterInvalidArgumentException
	{
		if(event==null) throw new GRS2WriterInvalidArgumentException("event cannot be null");
		try
		{
			event.setSource(EventSource.Writer);
			this.buffer.emit(event);
		} catch (GRS2BufferException e)
		{
			throw new GRS2WriterException("unable to emit event", e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordWriter#receive()
	 */
	public synchronized BufferEvent receive() throws GRS2WriterException
	{
		try
		{
			return this.buffer.receive(EventSource.Reader);
		} catch (GRS2BufferException e)
		{
			throw new GRS2WriterException("unable to receive event", e);
		}
	}
	
	private void initLocator(IWriterProxy proxy) throws GRS2BufferException, GRS2ProxyException
	{
		if(this.locator!=null) return;
		if(buffer.getStatus()!=Status.Open) throw new GRS2BufferInvalidOperationException("Buffer is not open");
		String key=GRSRegistry.Registry.add(this.buffer);
		buffer.setKey(key);
		proxy.setKey(key);
		this.buffer.setMirror(proxy.bind());
		this.locator=proxy.getLocator();
	}

}
