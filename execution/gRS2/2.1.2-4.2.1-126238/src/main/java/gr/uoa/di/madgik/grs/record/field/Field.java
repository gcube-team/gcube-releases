package gr.uoa.di.madgik.grs.record.field;

import gr.uoa.di.madgik.grs.buffer.GRS2BufferException;
import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.buffer.IBuffer.TransportDirective;
import gr.uoa.di.madgik.grs.buffer.IBuffer.TransportOverride;
import gr.uoa.di.madgik.grs.proxy.mirror.GRS2ProxyMirrorException;
import gr.uoa.di.madgik.grs.proxy.mirror.GRS2ProxyMirrorProtocolErrorException;
import gr.uoa.di.madgik.grs.record.GRS2RecordDefinitionException;
import gr.uoa.di.madgik.grs.record.GRS2RecordMediationException;
import gr.uoa.di.madgik.grs.record.GRS2RecordSerializationException;
import gr.uoa.di.madgik.grs.record.IPumpable;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.mediation.MediatingInputStream;
import gr.uoa.di.madgik.grs.record.field.mediation.MediationFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class is the base class for all {@link Field}s that are handled by the gRS2 set of components. All implementations
 * of this abstract class must define a default constructor without arguments as instances of these classes will be instantiated
 * using reflection based on the existence of a default no arguments constructor
 * 
 * @author gpapanikos
 *
 */
public abstract class Field implements IPumpable, Serializable
{
	private static final long serialVersionUID = 1L;
	/**
	 * The {@link Record} this field is bound to
	 */
	transient protected Record record;
	private int definitionIndex=-1;
	private boolean remoteCopy=false;

	/**
	 * Sets the index of the {@link FieldDefinition} within the {@link RecordDefinition} of the bound {@link Record}
	 * 
	 * @param index the index
	 */
	public void setDefinitionIndex(int index)
	{
		this.definitionIndex=index;
	}

	/**
	 * Retrieves the index of the {@link FieldDefinition} within the {@link RecordDefinition} of the bound {@link Record}
	 * 
	 * @return the index
	 */
	public int getDefinitionIndex()
	{
		return this.definitionIndex;
	}
	
	/**
	 * Binds the {@link Field} to a specific {@link Record} that hsots it
	 * 
	 * @param record the record
	 */
	public void bind(Record record)
	{
		this.record=record;
	}
	
	/**
	 * Pre-binds the {@link Field} to the provided {@link Record} 
	 * 
	 * @param record
	 */
	public void prebind(Record record)
	{
		this.record=record;
	}
	
	/**
	 * Sets whether this instance of the {@link Field} is a remote copy of the original {@link Field}
	 * 
	 * @param remoteCopy whether or not it is a remote copy
	 */
	public void setRemoteCopy(boolean remoteCopy)
	{
		this.remoteCopy=remoteCopy;
	}
	
	/**
	 * Checks if the instance is a remote copy of the original
	 * 
	 * @return  whether or not it is a remote copy
	 */
	public boolean isRemoteCopy()
	{
		return this.remoteCopy;
	}

	/**
	 * Retrieves the {@link FieldDefinition} based on the definition index and the bound {@link Record} {@link RecordDefinition}
	 * 
	 * @return the field definition
	 * @throws GRS2RecordDefinitionException if the {@link Field} is not bound to a {@link Record} or no usable definition has been found 
	 */
	public FieldDefinition getFieldDefinition() throws GRS2RecordDefinitionException
	{
		try
		{
			if(this.record==null) throw new GRS2RecordDefinitionException("Field not properly bind to buffer");
			if(this.definitionIndex<0 || this.definitionIndex>=this.record.getDefinition().getDefinitionSize()) throw new GRS2RecordDefinitionException("No definition found for index "+this.definitionIndex);
			return this.record.getDefinition().getDefinition(definitionIndex);
		} catch (GRS2BufferException e)
		{
			throw new GRS2RecordDefinitionException("unable to retrieve field's definition",e);
		}
	}
	
	/**
	 * Resolves the transport directive that needs to be used by this {@link Field}. If the defined {@link TransportDirective} is set
	 * to {@link TransportDirective#Inherit}, the bound record's {@link Record#resolveTransportDirective()} is used
	 * 
	 * @return the resolved {@link TransportDirective}
	 * @throws GRS2RecordDefinitionException The bound {@link Record}'s definition could not be retrieved
	 * @throws GRS2BufferException the {@link IBuffer} state does not allow for this operation to be completed
	 */
	public TransportDirective resolveTransportDirective() throws GRS2RecordDefinitionException, GRS2BufferException
	{
		FieldDefinition def=this.getFieldDefinition();
		switch(def.getTransportDirective())
		{
			case Full:
			case Partial:
			{
				return def.getTransportDirective();
			}
			case Inherit:
			{
				return this.record.resolveTransportDirective();
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
	 * Deflates the state and information of this {@link Field} in the provided stream. After deflating the internally kept information,
	 * {@link Field#extendDeflate(DataOutput)} is invoked for class extenders to persist their information
	 * 
	 * @param out the stream to writer information to
	 * @throws GRS2RecordSerializationException There was a problem in the serialization
	 */
	public final void deflate(DataOutput out) throws GRS2RecordSerializationException
	{
		try
		{
			out.writeInt(this.definitionIndex);
			out.writeBoolean(this.remoteCopy);
			this.extendDeflate(out);
		} catch (IOException e)
		{
			throw new GRS2RecordSerializationException("unable to marshal record", e);
		}
	}
	
	public final Element toXML(Document doc) throws GRS2RecordSerializationException
	{
		Element element = doc.createElement("field");
		
		Element elm = null;

		elm = doc.createElement("definitionIndex");
		elm.setTextContent(String.valueOf(this.getDefinitionIndex()));
		element.appendChild(elm);
		
		elm = doc.createElement("remoteCopy");
		elm.setTextContent(String.valueOf(this.remoteCopy));
		element.appendChild(elm);
		
		this.extendToXML(doc, element);
		
		return element;
	}
	
	public final void fromXML(Element element) throws GRS2RecordSerializationException
	{
		this.fromXML(element, false);
	}
	
	public final void fromXML(Element element, boolean reset) throws GRS2RecordSerializationException
	{
		Integer definitionIndex = Integer.valueOf(element.getElementsByTagName("definitionIndex").item(0).getTextContent());
		this.definitionIndex = definitionIndex;
		Boolean remoteCopy = Boolean.valueOf(element.getElementsByTagName("remoteCopy").item(0).getTextContent());
		this.remoteCopy = remoteCopy;
		if(reset) this.remoteCopy=false;
		
		this.extendFromXML(element, reset);
	}
	
	/**
	 * Sets the state and information of this {@link Field} in the provided stream. After sending the internally kept information,
	 * {@link Field#extendSend(DataOutput, TransportOverride)} is invoked for class extenders to persist their information
	 * 
	 * @param out the stream to send information to
	 * @throws GRS2RecordSerializationException There was a problem in the serialization
	 */
	public final void send(DataOutput out) throws GRS2RecordSerializationException
	{
		try
		{
			out.writeInt(this.definitionIndex);
			out.writeBoolean(this.remoteCopy);
			this.extendSend(out,TransportOverride.Defined);
		} catch (IOException e)
		{
			throw new GRS2RecordSerializationException("unable to marshal record", e);
		}
	}
	
//	private void writeObject(java.io.ObjectOutputStream out) throws IOException
//	{
//		System.out.println("Field::write object");
//		try
//		{
//			out.writeInt(this.definitionIndex);
//			out.writeBoolean(this.remoteCopy);
//			//this.extendSend(out,TransportOverride.Defined);
//		} catch (IOException e)
//		{
//			throw new IOException("unable to marshal record", e);
//		} /*catch (GRS2RecordSerializationException e) {
//			throw new IOException("unable to marshal record", e);
//		}*/
//	}
	
	public final Element sendToXML(Document doc) throws GRS2RecordSerializationException
	{
		try
		{
			Element element = doc.createElement("field");
			Element elm = null;

			elm = doc.createElement("definitionIndex");
			elm.setTextContent(String.valueOf(this.getDefinitionIndex()));
			element.appendChild(elm);
			
			elm = doc.createElement("remoteCopy");
			elm.setTextContent(String.valueOf(this.remoteCopy));
			element.appendChild(elm);
			
			this.extendSendToXML(doc, element,TransportOverride.Defined);
			
			return element;
		} catch (Exception e)
		{
			throw new GRS2RecordSerializationException("unable to marshal record", e);
		}
	}
	
	public final void receiveFromXML(Element element) throws GRS2RecordSerializationException
	{
		try
		{
			Integer definitionIndex = Integer.parseInt(element.getElementsByTagName("definitionIndex").item(0).getTextContent());
			this.definitionIndex = definitionIndex;
			Boolean remoteCopy = Boolean.parseBoolean(element.getElementsByTagName("remoteCopy").item(0).getTextContent());
			this.remoteCopy = remoteCopy;
			this.extendReceiveFromXML(element,TransportOverride.Defined);
		} 
		catch (Exception e)
		{
			throw new GRS2RecordSerializationException("unable to unmarshal record", e);
		}
	}

	/**
	 * Called by {@link Record#makeLocal()}. External use is unnecessary and discouraged.
	 * <p>
	 * Resets any information which indicate previous executed transfers and makes the {@link Field} behave as if it were local.
	 * If partial transfers are involved {@link Field#makeAvailable()} should be invoked beforehand in case the full payload is needed.
	 * This method has no effect if the {@link Field} is created locally or retrieved by the same {@link IBuffer} instance the producer used
	 */
	public final void makeLocal()
	{
		this.extendMakeLocal();
		this.remoteCopy = false;
	}
	
	/**
	 * Method that needs to be implemented by the class extenders to deflate the information they keep
	 * 
	 * @param out the stream to deflate to
	 * @throws GRS2RecordSerializationException There was a problem in the serialization
	 */
	public abstract void extendDeflate(DataOutput out) throws GRS2RecordSerializationException;
	
	public abstract void extendToXML(Document doc, Element element) throws GRS2RecordSerializationException;
	
	public abstract void extendFromXML(Element element, boolean reset) throws GRS2RecordSerializationException;

	/**
	 * Method that needs to be implemented by the class extenders to send the information they keep
	 * 
	 * @param out the stream to send information to
	 * @param override the {@link TransportOverride} directive 
	 * @throws GRS2RecordSerializationException There was a problem in the serialization
	 */
	public abstract void extendSend(DataOutput out, TransportOverride override) throws GRS2RecordSerializationException;

	/**
	 * Method that needs to be implemented by the class extenders to reset any additional information concerning previous executed
	 * transfers they keep and make the field behave as if it were local
	 */
	protected abstract void extendMakeLocal();
	
	/**
	 * {@inheritDoc}
	 * 
	 * Similar as calling {@link Field#inflate(DataInput, boolean)} with a reset parameter of false
	 * 
	 * @see Field#inflate(DataInput)
	 * @see gr.uoa.di.madgik.grs.record.IPumpable#inflate(DataInput)
	 */
	public final void inflate(DataInput in) throws GRS2RecordSerializationException
	{
		this.inflate(in, false);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Inflates a {@link Field} that was previously deflated using {@link Field#deflate(DataOutput)}. After the {@link Field}
	 * deflate's its internal information, the {@link Field#extendInflate(DataInput, boolean)} is invoked
	 * 
	 * @param in the stream to inflate from
	 * @param reset whether or not the inflated information needs to be reset to clear any indication of previous executed transfers
	 * @throws GRS2RecordSerializationException There was a problem in the deserialization
	 */
	public final void inflate(DataInput in, boolean reset) throws GRS2RecordSerializationException
	{
		try
		{
			this.definitionIndex=in.readInt();
			this.remoteCopy=in.readBoolean();
			if(reset) this.remoteCopy=false;
			this.extendInflate(in,reset);
		} catch (IOException e)
		{
			throw new GRS2RecordSerializationException("unable to unmarshal record", e);
		}
	}
	
	/**
	 * Receives the information send by a respective call to {@link Field#send(DataOutput)}. After the internally kept information are read, 
	 * the stream is passed to {@link Field#extendReceive(DataInput, TransportOverride)}
	 * 
	 * @param in the stream to read from
	 * @throws GRS2RecordSerializationException There was a problem in the deserialization
	 */
	public final void receive(DataInput in) throws GRS2RecordSerializationException
	{
		try
		{
			this.definitionIndex=in.readInt();
			this.remoteCopy=in.readBoolean();
			this.extendReceive(in,TransportOverride.Defined);
		} catch (IOException e)
		{
			throw new GRS2RecordSerializationException("unable to unmarshal record", e);
		}
	}
	
//	private void readObject(java.io.ObjectInputStream in)
//            throws java.io.IOException, java.lang.ClassNotFoundException{
//		System.out.println("Field::readObject");
//		
//		try
//		{
//			this.definitionIndex=in.readInt();
//			this.remoteCopy=in.readBoolean();
//			//this.extendReceive(in,TransportOverride.Defined);
//		} catch (IOException e)
//		{
//			throw new IOException("unable to unmarshal record", e);
//		} /*catch (GRS2RecordSerializationException e) {
//			throw new IOException("unable to unmarshal record", e);
//		}*/
//	}

	/**
	 * Method that needs to be implemented by the {@link Field} extenders to inflate their internal information
	 * 
	 * @param in the stream to inflate from
	 * @param reset whether or not the inflated information needs to be reset to clear any indication of previous executed transfers
	 * @throws GRS2RecordSerializationException There was a problem in the deserialization
	 */
	public abstract void extendInflate(DataInput in, boolean reset) throws GRS2RecordSerializationException;

	/**Method that needs to be implemented by the {@link Field} extenders to receive their priveously send information
	 * 
	 * @param in the stream to receive from
	 * @param override the {@link TransportOverride} to use
	 * @throws GRS2RecordSerializationException There was a problem in the deserialization
	 */
	public abstract void extendReceive(DataInput in, TransportOverride override) throws GRS2RecordSerializationException;
	
	/**
	 * Disposes all internal state and invokes {@link Field#extendDispose()}
	 */
	public void dispose()
	{
		this.extendDispose();
		this.record=null;
		this.definitionIndex=-1;
	}
	
	/**
	 * Method to be implemented by class extenders to dispose their internal state
	 */
	public abstract void extendDispose();
	
	/**
	 * Method that needs to be implemented by class extenders to indicate in case of a remote field copy when the field is not
	 * expecting any more data from its original instance. For an instance that is not remote, the field is always available
	 * 
	 * @return whether or not the remote copy is available
	 */
	public abstract boolean isAvailable();
	
	/**
	 * Makes fully available the content of the {@link Field} by invoking {@link Field#makeAvailable(TransportOverride)} with a
	 * {@link gr.uoa.di.madgik.grs.buffer.IBuffer.TransportOverride#Override} value
	 * 
	 * @throws GRS2RecordDefinitionException the definition of the bound record could not be utilized
	 * @throws GRS2BufferException the state of the {@link IBuffer} does not permit this operation to be completed
	 * @throws GRS2ProxyMirrorException The state of the mirroring protocol does not permit for this operation to be completed
	 */
	public void makeAvailable() throws GRS2RecordDefinitionException, GRS2BufferException, GRS2ProxyMirrorException
	{
		this.makeAvailable(TransportOverride.Override);
	}
	
	/**
	 * Invokes the bound {@link Record}'s {@link Record#requestPartial(TransportOverride, int)} method with the provided 
	 * {@link gr.uoa.di.madgik.grs.buffer.IBuffer.TransportOverride} value
	 * 
	 * @param override the {@link TransportOverride} to use
	 * @throws GRS2RecordDefinitionException the definition of the bound record could not be utilized
	 * @throws GRS2BufferException the state of the {@link IBuffer} does not permit this operation to be completed
	 * @throws GRS2ProxyMirrorException The state of the mirroring protocol does not permit for this operation to be completed
	 */
	public void makeAvailable(TransportOverride override) throws GRS2RecordDefinitionException, GRS2BufferException, GRS2ProxyMirrorException
	{
		if(this.isAvailable()) return;
		TransportDirective dir=this.resolveTransportDirective();
		if(dir==TransportDirective.Full) return;
		else if (dir==TransportDirective.Inherit) throw new GRS2ProxyMirrorProtocolErrorException("Unsupported transport directive after resolution");
		else this.record.requestPartial(override, this.getDefinitionIndex());
	}
	
	/**
	 * Provides an {@link InputStream} over the locally available payload of the {@link Field}
	 * 
	 * @return the input stream
	 * @throws IOException The input stream could not be created
	 */
	public abstract InputStream getInputStream() throws IOException;
	
	/**
	 * Retrieves an {@link MediatingInputStream} over this {@link Field}
	 * 
	 * @return the stream to be used
	 * @throws IOException the stream could not be created
	 * @throws GRS2RecordMediationException the mediated stream could not be created
	 */
	public MediatingInputStream getMediatingInputStream() throws IOException, GRS2RecordMediationException
	{
		return MediationFactory.getStream(this);
	}

	public abstract void extendSendToXML(Document doc, Element element, TransportOverride override)
			throws GRS2RecordSerializationException;

	public abstract void extendReceiveFromXML(Element element, TransportOverride override)
			throws GRS2RecordSerializationException;

}
