package gr.uoa.di.madgik.grs.record.field;

import gr.uoa.di.madgik.grs.buffer.IBuffer.TransportDirective;
import gr.uoa.di.madgik.grs.buffer.IBuffer.TransportOverride;
import gr.uoa.di.madgik.grs.proxy.mirror.GRS2ProxyMirrorProtocolErrorException;
import gr.uoa.di.madgik.grs.record.GRS2RecordDefinitionException;
import gr.uoa.di.madgik.grs.record.GRS2RecordException;
import gr.uoa.di.madgik.grs.record.GRS2RecordSerializationException;
import gr.uoa.di.madgik.grs.record.IPumpable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Field holding the payload of an Object that extends {@link IPumpable}
 * 
 * @author gpapanikos
 *
 */
public class ObjectField extends Field
{
	private IPumpable payload=null;
	
	private boolean marshaledCompleted=false;
	
	/**
	 * Creates a new instance
	 */
	public ObjectField(){}
	
	/**
	 * Creates a new instance
	 * 
	 * @param payload the payload of the field
	 */
	public ObjectField(IPumpable payload)
	{
		this.payload=payload;
	}
	
	/**
	 * Sets the payload of the field
	 * 
	 * @param payload
	 */
	public void setPayload(IPumpable payload)
	{
		this.payload=payload;
	}
	
	/**
	 * Retrieves the payload of the field
	 * 
	 * @return the field payload
	 * @throws GRS2RecordException 
	 */
	public IPumpable getPayload() 
	{
		return this.payload;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.record.field.Field#getFieldDefinition()
	 */
	public ObjectFieldDefinition getFieldDefinition()  throws GRS2RecordDefinitionException
	{
		if(!(super.getFieldDefinition() instanceof ObjectFieldDefinition)) throw new GRS2RecordDefinitionException("Provided field definition is not of required type");
		return (ObjectFieldDefinition)super.getFieldDefinition();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.record.field.Field#isAvailable()
	 */
	@Override
	public boolean isAvailable()
	{
		return (this.marshaledCompleted || !this.isRemoteCopy());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The full payload serialization is transformed into a byte array. This is not a good method to use for 
	 * large objects serialization. In general this method is left for homogeneity but should be avoided
	 * </p>
	 * 
	 * @see gr.uoa.di.madgik.grs.record.field.Field#getInputStream()
	 */
	@Override
	public InputStream getInputStream() throws IOException
	{
		if(this.payload==null) return null;
		byte[] br;
		try
		{
			ByteArrayOutputStream bout=new ByteArrayOutputStream();
			DataOutputStream dout=new DataOutputStream(bout);
			this.payload.deflate(dout);
			try{dout.flush();}catch(Exception ex){}
			try{bout.flush();}catch(Exception ex){}
			try{dout.close();}catch(Exception ex){}
			try{bout.close();}catch(Exception ex){}
			br=bout.toByteArray();
		} catch (GRS2RecordSerializationException e)
		{
			throw new IOException("Unable to deflate object : "+e.getMessage());
		}
		return new ByteArrayInputStream(br);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.record.field.Field#extendSend(java.io.DataOutput, gr.uoa.di.madgik.grs.buffer.IBuffer.TransportOverride)
	 */
	@Override
	public void extendSend(DataOutput out, TransportOverride override) throws GRS2RecordSerializationException
	{
		try
		{
			TransportDirective dir=this.resolveTransportDirective();
			if(override==TransportOverride.Override) dir=TransportDirective.Full;
			
			if(this.marshaledCompleted) throw new GRS2ProxyMirrorProtocolErrorException("More to marshal requested but full payload is already provided");
			this.record.markActivity();
			if(this.payload==null)
			{
				this.marshaledCompleted=true;
				out.writeBoolean(false);
			}
			else
			{
				if(dir==TransportDirective.Inherit) throw new GRS2ProxyMirrorProtocolErrorException("Unsupported transport directive after resolution");
				if(dir==TransportDirective.Partial) throw new GRS2ProxyMirrorProtocolErrorException("Unsupported transport directive after resolution");
				out.writeBoolean(true);
				out.writeUTF(this.payload.getClass().getName());
				this.payload.deflate(out);
				this.marshaledCompleted=true;
				this.record.markActivity();
			}
		} catch (Exception e)
		{
			throw new GRS2RecordSerializationException("unable to marshal field", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.record.field.Field#extendReceive(java.io.DataInput, gr.uoa.di.madgik.grs.buffer.IBuffer.TransportOverride)
	 */
	@Override
	public void extendReceive(DataInput in, TransportOverride override) throws GRS2RecordSerializationException
	{
		try
		{
			if(this.marshaledCompleted) throw new GRS2ProxyMirrorProtocolErrorException("Marshaling of field is already completed");
			TransportDirective dir=this.resolveTransportDirective();
			if(override==TransportOverride.Override) dir=TransportDirective.Full;
			
			if(dir==TransportDirective.Inherit) throw new GRS2ProxyMirrorProtocolErrorException("Unsupported transport directive after resolution");
			this.record.markActivity();
			boolean payloadNotNull=in.readBoolean();
			if(!payloadNotNull)
			{
				this.payload=null;
				this.marshaledCompleted=true;
			}
			else
			{
				String pumpableType=in.readUTF();
				this.payload=(IPumpable)Class.forName(pumpableType).newInstance();
				this.payload.inflate(in);
				this.marshaledCompleted=true;
				this.record.markActivity();
			}
		} catch (Exception e)
		{
			throw new GRS2RecordSerializationException("unable to unmarshal field", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.record.field.Field#extendDispose()
	 */
	@Override
	public void extendDispose()
	{
		this.payload=null;
		this.marshaledCompleted=false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.record.field.Field#extendDeflate(java.io.DataOutput)
	 */
	@Override
	public void extendDeflate(DataOutput out) throws GRS2RecordSerializationException
	{
		try
		{
			if(this.payload==null) out.writeBoolean(false);
			else
			{
				out.writeBoolean(true);
				out.writeUTF(this.payload.getClass().getName());
				this.payload.deflate(out);
			}
			out.writeBoolean(this.marshaledCompleted);
		}catch(Exception ex)
		{
			throw new GRS2RecordSerializationException("unable to deflate field", ex);
		}
	}

	@Override
	public void extendToXML(Document doc, Element element) throws GRS2RecordSerializationException {
		try {
			Element el = null;
			Element elm = null;

			if (this.payload != null) {
				el = doc.createElement("payloadElement");

				elm = doc.createElement("payloadClassName");
				elm.setTextContent(String.valueOf(this.payload.getClass().getName()));
				el.appendChild(elm);

				Element payload = this.payload.toXML(doc);

				el.appendChild(payload);

				element.appendChild(el);
			}

			elm = doc.createElement("marshaledCompleted");
			elm.setTextContent(String.valueOf(this.marshaledCompleted));
			element.appendChild(elm);
		} catch (Exception e) {
			throw new GRS2RecordSerializationException("unable to deflate field", e);
		}

	}

	@Override
	public void extendFromXML(Element element, boolean reset) throws GRS2RecordSerializationException {
		Element payloadElement = (Element) element.getElementsByTagName("payloadElement").item(0);
		String payloadClassName = payloadElement.getElementsByTagName("payloadClassName").item(0).getTextContent();

		try {
			this.payload = (IPumpable) Class.forName(payloadClassName).newInstance();

			this.marshaledCompleted = Boolean.valueOf(element.getElementsByTagName("marshaledCompleted").item(0)
					.getTextContent());
			if (reset) {
				this.marshaledCompleted = false;
			}

			this.payload.fromXML(payloadElement);
		} catch (Exception e) {
			throw new GRS2RecordSerializationException("unable to get record from xml", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.record.field.Field#extendInflate(java.io.DataInput, boolean)
	 */
	@Override
	public void extendInflate(DataInput in, boolean reset) throws GRS2RecordSerializationException
	{
		try
		{
			boolean payloadNotNull=in.readBoolean();
			if(!payloadNotNull)this.payload=null;
			else
			{
				String pumpableType=in.readUTF();
				this.payload=(IPumpable)Class.forName(pumpableType).newInstance();
				this.payload.inflate(in);
			}
			this.marshaledCompleted=in.readBoolean();
			if(reset)
			{
				this.marshaledCompleted=false;
			}
		}catch(Exception ex)
		{
			throw new GRS2RecordSerializationException("unable to inflate field", ex);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.record.field.Field#extendMakeLocal()
	 */
	@Override
	protected void extendMakeLocal()
	{
		this.marshaledCompleted=false;
	}

	@Override
	public void extendSendToXML(Document doc, Element element, TransportOverride override)
			throws GRS2RecordSerializationException {
		try {
			Element el = null;
			Element elm = null;

			TransportDirective dir = this.resolveTransportDirective();
			if (override == TransportOverride.Override)
				dir = TransportDirective.Full;

			if (this.marshaledCompleted)
				throw new GRS2ProxyMirrorProtocolErrorException(
						"More to marshal requested but full payload is already provided");
			this.record.markActivity();
			if (this.payload == null) {
				this.marshaledCompleted = true;
			} else {

				if (dir == TransportDirective.Inherit)
					throw new GRS2ProxyMirrorProtocolErrorException("Unsupported transport directive after resolution");
				if (dir == TransportDirective.Partial)
					throw new GRS2ProxyMirrorProtocolErrorException("Unsupported transport directive after resolution");

				el = doc.createElement("payloadElement");

				elm = doc.createElement("payloadClassName");
				elm.setTextContent(String.valueOf(this.payload.getClass().getName()));
				el.appendChild(elm);

				Element payload = this.payload.toXML(doc);

				el.appendChild(payload);

				element.appendChild(el);

				this.marshaledCompleted = true;
				this.record.markActivity();
			}
		} catch (Exception e) {
			throw new GRS2RecordSerializationException("unable to marshal field", e);
		}

	}

	@Override
	public void extendReceiveFromXML(Element element, TransportOverride override)
			throws GRS2RecordSerializationException {
		try {
			if (this.marshaledCompleted)
				throw new GRS2ProxyMirrorProtocolErrorException("Marshaling of field is already completed");
			TransportDirective dir = this.resolveTransportDirective();
			if (override == TransportOverride.Override)
				dir = TransportDirective.Full;

			if (dir == TransportDirective.Inherit)
				throw new GRS2ProxyMirrorProtocolErrorException("Unsupported transport directive after resolution");
			this.record.markActivity();

			Element payloadElement = (Element) element.getElementsByTagName("payloadElement").item(0);
			if (payloadElement != null) {

				String payloadClassName = payloadElement.getElementsByTagName("payloadClassName").item(0)
						.getTextContent();
				this.payload = (IPumpable) Class.forName(payloadClassName).newInstance();
				this.payload.fromXML(payloadElement);
				this.marshaledCompleted = true;
				this.record.markActivity();

			}

		} catch (Exception e) {
			throw new GRS2RecordSerializationException("unable to unmarshal field", e);
		}

	}

}
