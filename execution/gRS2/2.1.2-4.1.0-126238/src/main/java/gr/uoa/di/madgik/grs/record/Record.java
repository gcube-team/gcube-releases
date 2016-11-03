package gr.uoa.di.madgik.grs.record;

import gr.uoa.di.madgik.compressedstream.CompressedObjectStream;
import gr.uoa.di.madgik.grs.buffer.GRS2BufferException;
import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.buffer.IBuffer.TransportDirective;
import gr.uoa.di.madgik.grs.buffer.IBuffer.TransportOverride;
import gr.uoa.di.madgik.grs.proxy.mirror.GRS2ProxyMirrorException;
import gr.uoa.di.madgik.grs.proxy.mirror.GRS2ProxyMirrorProtocolErrorException;
import gr.uoa.di.madgik.grs.proxy.mirror.IMirror;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.FileField;
import gr.uoa.di.madgik.grs.writer.RecordWriter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class is the base class for all {@link Record}s that are handled by the gRS2 set of components. All implementations
 * of this abstract class must define a default constructor without arguments as instances of these classes will be instantiated
 * using reflection based on the existence of a default no arguments constructor. One thing that distinguishes the records 
 * belonging to one {@link IBuffer}, is the id. This id is set from within the framework once the record is bound to an 
 * {@link IBuffer} as described in {@link Record#bind(IBuffer)}. This means that the id of each {@link Record} represents the
 * index of the {@link Record} within the virtually infinite array of {@link Record}s managed by the {@link  IBuffer}
 * 
 * @author gpapanikos
 *
 */
public abstract class Record implements IPumpable, Serializable
{
	private static final long serialVersionUID = -1338682225030852571L;
	private int definitionIndex=-1;
	transient private IBuffer buffer=null;
	private long id=-1;
	transient private Field[] fields=null;
	private boolean defaultField=false;
	private boolean remoteCopy=false;
	
	transient private IBuffer hiddenBuffer = null;
	private long hiddenId = -1;
	private int hiddenDefinitionIndex = -1;
	private boolean hasBeenHidden = false;

	/**
	 * Retrieves the id of the {@link Record}
	 * 
	 * @return the id
	 */
	public long getID()
	{
		return this.id;
	}
	
	public void setID(long id)
	{
		this.id = id;
	}
	
	/**
	 * Retrieves the {@link RecordDefinition} associated with this {@link Record}. This operation is only available
	 * after the {@link Record#bind(IBuffer)} method is invoked. This method is typically invoked when a {@link Record}
	 * is added to an {@link IBuffer}
	 * 
	 * @return the record definition
	 * @throws GRS2RecordDefinitionException if the {@link Record} was not properly bind to a {@link IBuffer}
	 * @throws GRS2BufferException the available definitions could not be retrieved from the {@link IBuffer}  
	 */
	public RecordDefinition getDefinition() throws GRS2RecordDefinitionException, GRS2BufferException
	{
		if(this.buffer==null) throw new GRS2RecordDefinitionException("Record not properly bind to buffer");
		if(this.definitionIndex<0 || this.definitionIndex>=this.buffer.getRecordDefinitions().length) throw new GRS2RecordDefinitionException("Definition index not properly initialized");
		return this.buffer.getRecordDefinitions()[this.definitionIndex];
	}
	
	/**
	 * Gets the index of the definition of this {@link Record} in the record definitions array provided to the {@link IBuffer}
	 * 
	 * @return the index
	 */
	public int getDefinitionIndex()
	{
		return this.definitionIndex;
	}
	
	/**
	 * Sets the index of the definition of this {@link Record} in the record definitions array provided to the {@link IBuffer}
	 * 
	 * @param index the index
	 */
	public void setDefinitionIndex(int index)
	{
		this.definitionIndex=index;
	}

	/**
	 * Sets the {@link Field}s this {@link Record} hosts
	 * 
	 * @param fields the fields
	 */
	public void setFields(Field[] fields)
	{
		this.fields = fields;
		this.defaultField = false;
	}

	/**
	 * Retrieves the {@link Field}s this {@link Record} hosts
	 * 
	 * @return the fields
	 */
	public Field[] getFields()
	{
		return this.fields;
	}
	
	/**
	 * Retrieve the {@link Field} with the specified name
	 * 
	 * @param name the name of the field as is defined in the {@link Field}'s {@link FieldDefinition}
	 * @return the field or null if the field was not found
	 * @throws GRS2RecordDefinitionException If there was a problem retrieving the available fields definitions
	 * @throws GRS2BufferException If there was a problem retrieving the definitions from the {@link IBuffer}
	 */
	public Field getField(String name) throws GRS2RecordDefinitionException, GRS2BufferException
	{
		int index=this.getDefinition().getDefinition(name);
		if(index<0 || index>=this.fields.length) return null;
		return this.fields[index];
	}
	
	/**
	 * Retrieve the {@link Field} with the specified index in the fields array
	 * 
	 * @param index the field index
	 * @return the Field
	 */
	public Field getField(int index)
	{
		if(index<0 || index>=this.fields.length) return null;
		return this.fields[index];
	}
	
	/**
	 * Binds the {@link Record} to the provided {@link IBuffer}. If the record has no definition index defined and the buffer's
	 * record definitions only contain a single definition, the only available definition is used by default. The id of the record
	 * is set as {@link IBuffer#totalRecords()}-1. Therefore the id always coincides with the virtual index of the {@link Record}
	 * if one considers the {@link Record} as being available in an unbounded array of {@link Record}s. For every {@link Field}
	 * available in the {@link Record}, the {@link Field#setDefinitionIndex(int)} as well as {@link Field#bind(Record)} method 
	 * is invoked.
	 * 
	 * @param buffer the {@link IBuffer} to bind this record to
	 * @throws GRS2BufferException the {@link IBuffer} state does not permit this operation to be completed
	 * @throws GRS2RecordDefinitionException no usable definition could be found and associated with the {@link Record}
	 */
	public void bind(IBuffer buffer) throws GRS2BufferException,GRS2RecordDefinitionException
	{
		this.buffer=buffer;
		if(this.definitionIndex<0 && this.buffer.getRecordDefinitions().length==1) this.definitionIndex=0;
		else if (this.definitionIndex<0 && this.buffer.getRecordDefinitions().length>1) throw new GRS2RecordDefinitionException("No definition index defined and default value cannot be infered");
		if(this.definitionIndex<0 || this.definitionIndex>=this.buffer.getRecordDefinitions().length) 
				throw new GRS2RecordDefinitionException("No definition found for index "+this.definitionIndex);
		this.id=this.buffer.totalRecords()-1; //to start from 0
		if(this.fields==null)
		{
			this.fields=new Field[0];
			this.defaultField=true;
		}
		if (!(this instanceof GRS2ExceptionWrapper)){
			if(this.getDefinition().getDefinitionSize()!=this.fields.length) throw new GRS2RecordDefinitionException("Definition does not match record");
			for(int i=0;i<this.fields.length;i+=1)
			{
				this.fields[i].setDefinitionIndex(i);
				this.fields[i].bind(this);
			}
		}
		this.hiddenBuffer = this.buffer;
		this.hiddenId = this.id;
		this.hiddenDefinitionIndex = this.definitionIndex;
	}
	
	/**
	 * Unbinds the {@link Record} from the {@link IBuffer}. The id of the record, its definition index are reset and any information pertaining to
	 * partial transfers are reset. The {@link Field}s are not unbound from the {@link Record}
	 */
	public void unbind()
	{
		this.buffer = null;
		this.id = -1;
		this.definitionIndex = -1;
		if(this.defaultField == true)
		{
			this.fields = null;
			this.defaultField = false;
		}
	}
	
	/**
	 * Hides the {@link Record} from the {@link IBuffer} it is currectly bound to so that it won't be disposed by the former. All buffer related functionality will
	 * not be available until the record is revealed again to the {@link IBuffer}
	 */
	public void hide() 
	{
		this.hasBeenHidden = true;
		this.hiddenBuffer = this.buffer;
		this.buffer = null;
		this.hiddenDefinitionIndex = this.definitionIndex;
		this.definitionIndex = -1;
		this.hiddenId = this.id;
		this.id = -1;
		
	}
	
	/**
	 * Reveals a hidden {@link Record} to the {@link IBuffer}, making available all functionality related to the former.
	 * The record still won't be explicitly disposed by the {@link IBuffer}
	 */
	public void show()
	{
		this.buffer = this.hiddenBuffer;
		this.definitionIndex = this.hiddenDefinitionIndex;
		this.id = this.hiddenId;
	}
	
	/**
	 * Determines whether this {@link Record} is bound to a specific instance of {@link IBuffer}
	 * 
	 * @param buffer the {@link IBuffer} which this record will be tested against
	 * @return true if this {@link Record} is bound to the {@link IBuffer}, false otherwise regardless if the record is bound to another
	 * buffer or not bound to any buffer
	 */
	public boolean isBoundTo(IBuffer buffer) 
	{
		if(this.buffer == buffer)
			return true;
		return false;
	}
	
	/**
	 * This method performs a pre-bind of the {@link Record} to the provided {@link IBuffer} to enable the invocation of some
	 * other method that needs to have the {@link IBuffer} of the record available but before adding the record to the {@link IBuffer}
	 * properly
	 * 
	 * @param buffer the {@link IBuffer} to pre-bind to
	 */
	public void prebind(IBuffer buffer)
	{
		this.buffer=buffer;
	}
	
	/**
	 * Notifies the {@link IBuffer} associated with this {@link Record} that some activity has taken place
	 */
	public void markActivity()
	{
		this.buffer.markSimulateActivity();
	}
	
	/**
	 * Set if the current instance of the {@link Record} is a remote copy of the original instance of the {@link Record}
	 * 
	 * @param remoteCopy whether or not this is the remote copy
	 */
	public void setRemoteCopy(boolean remoteCopy)
	{
		this.remoteCopy=remoteCopy;
		if(this.fields!=null) for(Field f : this.fields) f.setRemoteCopy(remoteCopy);
	}
	
	/**
	 * Retrieves if this is the remote copy of the record
	 * 
	 * @return true if it is the remote copy
	 */
	public boolean isRemoteCopy()
	{
		return this.remoteCopy;
	}

	/**
	 * Resolves the {@link TransportDirective} that should be used fir this record. In case the defined {@link TransportDirective}
	 * is set to {@link TransportDirective#Inherit}, a call to the bound {@link IBuffer}'s {@link IBuffer#resolveTransportDirective()}
	 * 
	 * @return the resolved {@link TransportDirective}
	 * @throws GRS2RecordDefinitionException the record definition is not available
	 * @throws GRS2BufferException the buffer is unavailable
	 */
	public TransportDirective resolveTransportDirective() throws GRS2RecordDefinitionException, GRS2BufferException
	{
		RecordDefinition def=this.getDefinition();
		switch(def.getTransportDirective())
		{
			case Full:
			case Partial:
			{
				return def.getTransportDirective();
			}
			case Inherit:
			{
				return this.buffer.resolveTransportDirective();
			}
			default:
			{
				return TransportDirective.Full;
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * Deflates the state and payload of the {@link Record} to the provided stream. After deflating the internally kept
	 * information, a call to {@link Record#extendDeflate(DataOutput)} is made and then for each field hosted, a call to
	 * {@link Field#deflate(DataOutput)} is made
	 * 
	 * @param out the stream to deflate to
	 * @throws GRS2RecordSerializationException there was a problem serializing the information
	 */
	public final void deflate(DataOutput out) throws GRS2RecordSerializationException
	{
		try
		{
			out.writeInt(this.definitionIndex);
			out.writeLong(this.id);
			out.writeBoolean(this.remoteCopy);
			this.extendDeflate(out);
			for(Field f : this.fields)
			{
				out.writeBoolean(true);
				out.writeUTF(f.getClass().getName());
				f.deflate(out);
			}
			out.writeBoolean(false);
		} catch (IOException e)
		{
			throw new GRS2RecordSerializationException("unable to deflate record", e);
		}
	}
	
	/**
	 * Sends the state and payload of the {@link Record} to a remote requester. After writing the internally managed information,
	 * a call to {@link Record#extendSend(DataOutput)} is made, and for every {@link Field} hosted, a call to {@link Field#send(DataOutput)}
	 * is made
	 * 
	 * @param out the stream to send to
	 * @throws GRS2RecordSerializationException there was a problem serializing the information
	 */
	public final void send(DataOutput out) throws GRS2RecordSerializationException
	{
		try
		{
			out.writeInt(this.definitionIndex);
			out.writeLong(this.id);
			out.writeBoolean(this.remoteCopy);
			this.extendSend(out);
			for(Field f : this.fields)
			{
				out.writeBoolean(true);
				out.writeUTF(f.getClass().getName());
				f.send(out);
			}
			out.writeBoolean(false);
		} catch (IOException e)
		{
			throw new GRS2RecordSerializationException("unable to marshal record", e);
		}
	}

	public Element sendToXML(Document doc) throws GRS2RecordSerializationException {
		try {
			Element element = doc.createElement("record");
			Element elm = null;

			elm = doc.createElement("definitionIndex");
			elm.setTextContent(String.valueOf(this.getDefinitionIndex()));
			element.appendChild(elm);
			
			elm = doc.createElement("id");
			elm.setTextContent(String.valueOf(this.getID()));
			element.appendChild(elm);

			elm = doc.createElement("remoteCopy");
			elm.setTextContent(String.valueOf(this.remoteCopy));
			element.appendChild(elm);

			Element fields = doc.createElement("fields");
			for (Field f : this.getFields()) {
				Element fieldEl = f.sendToXML(doc);

				elm = doc.createElement("fieldClassName");
				elm.setTextContent(f.getClass().getName());
				fieldEl.appendChild(elm);

				fields.appendChild(fieldEl);
			}
			element.appendChild(fields);
			return element;
		} catch (Exception e) {
			throw new GRS2RecordSerializationException("unable to marshal record", e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * Similar as calling {@link Record#inflate(DataInput, boolean)} with a reset parameter of false
	 * 
	 * @see Record#inflate(DataInput, boolean)
	 * @see gr.uoa.di.madgik.grs.record.IPumpable#inflate(java.io.DataInput)
	 */
	public final void inflate(DataInput in) throws GRS2RecordSerializationException
	{
		this.inflate(in, false);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * Inflates the {@link Record} from the provided stream as it was previously deflated using the method {@link Record#deflate(DataOutput)}.
	 * After deflating the internally kept information, the stream is passed to {@link Record#extendDeflate(DataOutput)} and then the 
	 * serializations of the hosted {@link Field}s are read by the respective {@link Field#inflate(DataInput, boolean)}
	 * 
	 * @param in the stream to inflate from
	 * @param reset whether or not the inflated information should be reset to drop any information previously stored concerning  transfers 
	 * performed by the {@link Record}
	 * @throws GRS2RecordSerializationException there was a problem deserializing the information
	 */
	public final void inflate(DataInput in, boolean reset) throws GRS2RecordSerializationException
	{
		try
		{
			this.definitionIndex= in.readInt();
			this.id=in.readLong();
			this.remoteCopy=in.readBoolean();
			if(reset) this.remoteCopy=false;
			this.extendInflate(in,reset);
			ArrayList<Field> fs=new ArrayList<Field>();
			while(in.readBoolean())
			{
				String fieldType=in.readUTF();
				Field f = (Field)Class.forName(fieldType).newInstance();
				f.prebind(this);
				f.inflate(in,reset);
				fs.add(f);
			}
			this.fields=fs.toArray(new Field[0]);
		} catch (Exception e)
		{
			throw new GRS2RecordSerializationException("unable to marshal record", e);
		}
	}
	
	/**
	 * Receives the state and payload of the {@link Record} from a remote sender. After reading the internally managed information,
	 * a call to {@link Record#extendReceive(DataInput)} is made, and for every {@link Field} hosted, a call to {@link Field#receive(DataInput)}
	 * is made
	 * 
	 * @param in the stream to receive from
	 * @throws GRS2RecordSerializationException there was a problem deserializing the information
	 */
	public final void receive(DataInput in) throws GRS2RecordSerializationException
	{
		try
		{
			this.definitionIndex= in.readInt();
			this.id=in.readLong();
			this.remoteCopy=in.readBoolean();
			this.extendReceive(in);
			ArrayList<Field> fs=new ArrayList<Field>();
			while(in.readBoolean())
			{
				String fieldType=in.readUTF();
				Field f = (Field)Class.forName(fieldType).newInstance();
				f.prebind(this);
				f.receive(in);
				fs.add(f);
			}
			this.fields=fs.toArray(new Field[0]);
		} catch (Exception e)
		{
			throw new GRS2RecordSerializationException("unable to marshal record", e);
		}
	}
	
	/**
	 * Called by {@link RecordWriter#importRecord}. External usage is unnecessary and discouraged.
	 * <p>
	 * Resets all internally kept information concerning transfers performed by the {@link Record} and makes it behave as it were local.
	 * For all {@link Field}s, {@link Field#makeLocal()} is invoked, therefore if partial transfers are involved {@link Field#makeAvailable()}
	 * should be invoked beforehand in case the full payload is needed.
	 * 
	 * @see gr.uoa.di.madgik.grs.record.field.Field#makeLocal()
	 */
	public final void makeLocal() 
	{
		this.extendMakeLocal();
		for(Field f : this.fields)
			f.makeLocal();
		this.remoteCopy = false;
	}
	
	/**
	 * The method all {@link Record} implementations should implement to add any information they keep during transfer
	 * 
	 * @param out the stream to transfer through
	 * @throws GRS2RecordSerializationException Information serialization problem
	 */
	public abstract void extendSend(DataOutput out) throws GRS2RecordSerializationException;
	
	/**
	 * The method all {@link Record} implementations should implement to read any information they send during transfer
	 * 
	 * @param in the stream to read from
	 * @throws GRS2RecordSerializationException Information deserialization error
	 */
	public abstract void extendReceive(DataInput in) throws GRS2RecordSerializationException;
	
	/**
	 * The method all {@link Record} implementations should implement to add any information they keep during deflate
	 * 
	 * @param out the stream to deflate to
	 * @throws GRS2RecordSerializationException Information serialization problem
	 */
	public abstract void extendDeflate(DataOutput out) throws GRS2RecordSerializationException;
	
	/**
	 * The method all {@link Record} implementations should implement to read any information they deflated
	 * 
	 * @param in the stream to inflate from 
	 * @param reset whether or not the inflated information should be reset to drop any information previously stored concerning transfers 
	 * performed by the {@link Field}
	 * @throws GRS2RecordSerializationException Information deserialization error
	 */
	public abstract void extendInflate(DataInput in, boolean reset) throws GRS2RecordSerializationException;
	
	/**
	 * The method all {@link Record} implementations should implement to reset additional information they keep concerning transfers performed
	 */
	protected abstract void extendMakeLocal();
	
	/**
	 * Disposes all internally kept state, calls {@link Record#extendDispose()} and then for every hosted {@link Field}, calls
	 * {@link Field#dispose()}
	 */
	public void dispose()
	{
		if(this.hasBeenHidden)
			return;
		this.extendDispose();
		if(this.fields!=null) for(Field f : this.fields) f.dispose();
		this.fields=null;
		this.definitionIndex=-1;
		this.buffer=null;
	}
	
	/**
	 * Method to be implemented by {@link Record} extenders to dispose any internally kept state
	 */
	public abstract void extendDispose();
	
	/**
	 * if the associated {@link IBuffer} has a {@link IMirror} set, then the {@link IMirror#requestPartial(long, int, TransportOverride, Object)}
	 * method is used to request additional payload for the specific {@link Field} of the {@link Record}. Requests for more of partial
	 * content can only be issued against {@link Field}s of a {@link Record} and not for {@link Record}s themselves
	 * 
	 * @param override the {@link TransportOverride} directive
	 * @param fieldIndex the field index to request data for
	 * @throws GRS2ProxyMirrorException The state of the {@link IMirror} does not allow for this operation to be completed
	 */
	public void requestPartial(TransportOverride override, int fieldIndex) throws GRS2ProxyMirrorException
	{
		IMirror mirror=this.buffer.getMirror();
		if(mirror==null) return;
		Object notify=new Object();
		synchronized(notify)
		{
			long period=mirror.requestPartial(this.id, fieldIndex, override, notify);
			while(true)
			{
				try{notify.wait(period);}catch(Exception ex){}
				if(mirror.pollPartial(this.id, fieldIndex)) break;
			}
		}
	}
	
	/**
	 * For all hosted {@link Field}s, the method {@link Record#makeAvailable(int)}
	 * 
	 * @throws GRS2RecordDefinitionException the record definition could not be retrieved
	 * @throws GRS2ProxyMirrorException There was an error during the mirroring process
	 * @throws GRS2BufferException the {@link IBuffer} state does not allow for this operation to be completed
	 */
	public void makeAvailable() throws GRS2RecordDefinitionException, GRS2ProxyMirrorException, GRS2BufferException
	{
		for (int i = 0; i < this.fields.length; i += 1)
			this.makeAvailable(i);
	}

	/**
	 * For all hosted {@link Field}s, the method {@link Record#makeAvailable(TransportOverride, int)}
	 * 
	 * @param override the {@link TransportOverride} value to use
	 * @throws GRS2RecordDefinitionException the record definition could not be retrieved
	 * @throws GRS2ProxyMirrorException There was an error during the mirroring process
	 * @throws GRS2BufferException the {@link IBuffer} state does not allow for this operation to be completed
	 */
//	public void makeAvailable(TransportOverride override) throws GRS2RecordDefinitionException, GRS2ProxyMirrorException, GRS2BufferException
//	{
//		for(int i=0;i<this.fields.length;i+=1) this.makeAvailable(override,i);
//	}

	/**
	 * For the specified {@link Field} with the set name, the method {@link Record#makeAvailable(TransportOverride, int)} with a 
	 * value of {@link TransportOverride#Override}
	 * 
	 * @param fieldName the name of the field to make available
	 * @throws GRS2RecordDefinitionException the record definition could not be retrieved
	 * @throws GRS2ProxyMirrorException There was an error during the mirroring process
	 * @throws GRS2BufferException the {@link IBuffer} state does not allow for this operation to be completed
	 */
	public void makeAvailable(String fieldName) throws GRS2RecordDefinitionException, GRS2ProxyMirrorException, GRS2BufferException
	{
		this.makeAvailable(TransportOverride.Override,this.getDefinition().getDefinition(fieldName));
	}
	
	/**
	 * For the specified {@link Field} with the set name, the method {@link Record#makeAvailable(TransportOverride, int)}
	 * 
	 * @param override the value of the {@link TransportOverride} parameter
	 * @param fieldName the name of the field to make available
	 * @throws GRS2RecordDefinitionException the record definition could not be retrieved
	 * @throws GRS2ProxyMirrorException There was an error during the mirroring process
	 * @throws GRS2BufferException the {@link IBuffer} state does not allow for this operation to be completed
	 */
//	public void makeAvailable(TransportOverride override,String fieldName) throws GRS2RecordDefinitionException, GRS2ProxyMirrorException, GRS2BufferException
//	{
//		this.makeAvailable(override,this.getDefinition().getDefinition(fieldName));
//	}
//	
	/**
	 * For the specified {@link Field} with the set index, the method {@link Record#makeAvailable(TransportOverride, int)} with a 
	 * value of {@link TransportOverride#Override}
	 * 
	 * @param fieldIndex the index of the field
	 * @throws GRS2RecordDefinitionException the record definition could not be retrieved
	 * @throws GRS2ProxyMirrorException There was an error during the mirroring process
	 * @throws GRS2BufferException the {@link IBuffer} state does not allow for this operation to be completed
	 */
	public void makeAvailable(int fieldIndex) throws GRS2RecordDefinitionException, GRS2ProxyMirrorException, GRS2BufferException
	{
		this.makeAvailable(TransportOverride.Override,fieldIndex);
	}
	
	/**
	 * For the specified field index and with the provided {@link TransportOverride} value, a request is made using the
	 * {@link Record#requestPartial(TransportOverride, int)} method
	 * 
	 * @param override The transport override value to use for the request
	 * @param fieldIndex the field index to request more payload for
	 * @throws GRS2RecordDefinitionException the record definition could not be retrieved
	 * @throws GRS2ProxyMirrorException There was an error during the mirroring process
	 * @throws GRS2BufferException the {@link IBuffer} state does not allow for this operation to be completed
	 */
	private void makeAvailable(TransportOverride override,int fieldIndex) throws GRS2ProxyMirrorException, GRS2RecordDefinitionException, GRS2BufferException
	{
		if(fieldIndex<0 || fieldIndex>=this.fields.length) throw new GRS2RecordDefinitionException("Provided field index is not available");
		Field f=this.fields[fieldIndex];
		if(f.isAvailable()) return;
		TransportDirective dir=f.resolveTransportDirective();
		if(dir==TransportDirective.Full) return;
		else if (dir==TransportDirective.Inherit) throw new GRS2ProxyMirrorProtocolErrorException("Unsupported transport directive after resolution");
		else this.requestPartial(override, fieldIndex);
	}

	public final Element toXML(Document doc) throws GRS2RecordSerializationException {
		Element element = doc.createElement("record");

		Element elm = null;

		elm = doc.createElement("definitionIndex");
		elm.setTextContent(String.valueOf(this.getDefinitionIndex()));
		element.appendChild(elm);

		elm = doc.createElement("id");
		elm.setTextContent(String.valueOf(this.getID()));
		element.appendChild(elm);

		elm = doc.createElement("remoteCopy");
		elm.setTextContent(String.valueOf(this.isRemoteCopy()));
		element.appendChild(elm);

		Element fields = doc.createElement("fields");

		for (Field f : this.getFields()) {
			Element fieldEl = f.toXML(doc);

			elm = doc.createElement("fieldClassName");
			elm.setTextContent(f.getClass().getName());
			fieldEl.appendChild(elm);

			fields.appendChild(fieldEl);
		}

		element.appendChild(fields);

		return element;
		// doc.appendChild(element);

	}

	public void receiveFromXML(Element element) throws GRS2RecordSerializationException {
		try {
			Integer definitionIndex = Integer.parseInt(element.getElementsByTagName("definitionIndex").item(0)
					.getTextContent());
			this.definitionIndex = definitionIndex;
			Long id = Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent());
			this.id = id;
			Boolean remoteCopy = Boolean.parseBoolean(element.getElementsByTagName("remoteCopy").item(0)
					.getTextContent());
			this.remoteCopy = remoteCopy;

			Element fields = (Element) element.getElementsByTagName("fields").item(0);
			ArrayList<Field> fs = new ArrayList<Field>();

			NodeList fieldList = fields.getChildNodes();
			for (int i = 0; i < fieldList.getLength(); ++i) {
				Element fieldEl = (Element) fieldList.item(i);

				String fieldType = fieldEl.getElementsByTagName("fieldClassName").item(0).getTextContent();

				Field f;
				try {
					f = (Field) Class.forName(fieldType).newInstance();
				} catch (Exception e) {
					throw new GRS2RecordSerializationException("unable to get record from xml", e);
				}
				f.prebind(this);
				f.receiveFromXML(fieldEl);
				fs.add(f);

			}

			this.fields = fs.toArray(new Field[0]);
		} catch (Exception e) {
			throw new GRS2RecordSerializationException("unable to marshal record", e);
		}
	}

	public final void fromXML(Element element) throws GRS2RecordSerializationException {
		Integer definitionIndex = Integer.parseInt(element.getElementsByTagName("definitionIndex").item(0)
				.getTextContent());
		this.definitionIndex = definitionIndex;
		Long id = Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent());
		this.id = id;
		Boolean remoteCopy = Boolean.parseBoolean(element.getElementsByTagName("remoteCopy").item(0).getTextContent());
		this.remoteCopy = remoteCopy;

		Element fields = (Element) element.getElementsByTagName("fields").item(0);
		ArrayList<Field> fs = new ArrayList<Field>();

		NodeList fieldList = fields.getChildNodes();
		for (int i = 0; i < fieldList.getLength(); ++i) {
			Element fieldEl = (Element) fieldList.item(i);

			String fieldType = fieldEl.getElementsByTagName("fieldClassName").item(0).getTextContent();

			Field f;
			try {
				f = (Field) Class.forName(fieldType).newInstance();
			} catch (Exception e) {
				throw new GRS2RecordSerializationException("unable to get record from xml", e);
			}
			f.prebind(this);
			f.fromXML(fieldEl);
			fs.add(f);
		}

		this.fields = fs.toArray(new Field[0]);
	}
	
	
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException, GRS2RecordDefinitionException, GRS2BufferException{
		this.writeFields(out);
		
		ArrayList<Field> compressedFields = new ArrayList<Field>();
		ArrayList<Field> uncompressedFields = new ArrayList<Field>();
		
		for (Field f : this.fields) {
			if (f instanceof FileField)
				uncompressedFields.add(f);
			else
				compressedFields.add(f);
		}
		
		CompressedObjectStream.writeObject(compressedFields, out);
		out.writeInt(uncompressedFields.size());
		for (Field f : uncompressedFields) {
			TransportOverride over = TransportOverride.Defined;
			((FileField) f).extendWriteObject(out, over);
		}
		
		//out.writeObject(uncompressedFields);
	}
	
	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream in)  throws java.io.IOException, java.lang.ClassNotFoundException{
		this.readFields(in);
		
		ArrayList<Field> compressedFields = (ArrayList<Field>) CompressedObjectStream.readObject(in);
		Integer numOfUncompressed = in.readInt();
		
		this.fields = new Field[compressedFields.size() +  numOfUncompressed];
		
		for (Field f : compressedFields) {
			int idx = f.getDefinitionIndex();
			if (idx<0 || idx > this.fields.length) {
				throw new java.io.IOException("Definition index not in range 0, " + this.fields.length);
			}
			if (this.fields[idx] != null) {
				throw new java.io.IOException("Definition index has already been used by another field");
			}
			this.fields[idx] = f;
		}
		
		for (int k = 0 ; k < numOfUncompressed ; k++) {
			FileField ff = new FileField();
			ff.prebind(this);
			ff.extendReadObject(in, TransportOverride.Defined);
			int idx = ff.getDefinitionIndex();
			if (idx<0 || idx > this.fields.length) {
				throw new java.io.IOException("Definition index not in range 0, " + this.fields.length);
			}
			if (this.fields[idx] != null) {
				throw new java.io.IOException("Definition index has already been used by another field");
			}
			
			this.fields[idx] = ff;
		}
	}
	
	private void writeFields(java.io.ObjectOutputStream out) throws IOException{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		BufferedOutputStream bfos = new BufferedOutputStream(bos);

		GZIPOutputStream gz = new GZIPOutputStream(bfos);
		ObjectOutputStream oos = new ObjectOutputStream(gz);

		oos.writeObject(this.definitionIndex);
		oos.writeObject(this.id);
		oos.writeObject(this.defaultField);
		oos.writeObject(this.remoteCopy);
		oos.writeObject(this.hiddenId);
		oos.writeObject(this.hiddenDefinitionIndex);
		oos.writeObject(this.hasBeenHidden);
		
		gz.finish();
		oos.flush();

		byte[] compressed = bos.toByteArray();
		out.writeObject(compressed);

		bos.close();
		bfos.close();
		gz.close();
		oos.close();
	}
	
	private void readFields(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException{
		byte[] compressed = (byte[]) in.readObject();

		ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
		BufferedInputStream bfis = new BufferedInputStream(bis);
		
		GZIPInputStream gz = new GZIPInputStream(bfis);
		ObjectInputStream ois = new ObjectInputStream(gz);
		
		this.definitionIndex = (Integer) ois.readObject();
		this.id = (Long) ois.readObject();
		this.defaultField = (Boolean) ois.readObject();
		this.remoteCopy = (Boolean) ois.readObject();
		this.hiddenId = (Long) ois.readObject();
		this.hiddenDefinitionIndex = (Integer) ois.readObject();
		this.hasBeenHidden = (Boolean) ois.readObject();
		
		bis.close();
		bfis.close();
		gz.close();
		ois.close();
	}
}
