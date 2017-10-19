package gr.uoa.di.madgik.grs.record.field;

import gr.uoa.di.madgik.grs.buffer.IBuffer.TransportDirective;
import gr.uoa.di.madgik.grs.record.GRS2RecordDefinitionException;
import gr.uoa.di.madgik.grs.record.GRS2RecordSerializationException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.Serializable;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The field definition represents the scheme of a single {@link Field} and holds all the metadata available for the
 * {@link Field}. All extending classes of this class must define a default no arguments constructor
 * 
 * @author gpapanikos
 *
 */
public abstract class FieldDefinition implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The default {@link TransportDirective} to be used by the {@link FieldDefinition}. 
	 * Currently set to {@link TransportDirective#Inherit} 
	 */
	public static final TransportDirective DefaultDirective=TransportDirective.Inherit;
	/**
	 * The default MIME type to be used by the {@link FieldDefinition}. Currently set to text/plain 
	 */
	public static final String DefaultMimeType="text/plain";
	/**
	 * The default value for the compression option. Currently set to false
	 */
	public static final boolean DefaultDoCompress=false;
	/**
	 * The default chunk size to be used during partial transfer. Currently set to 500K
	 */
	public static final int DefaultChunkSize=512*1024;
	
	private TransportDirective directive=FieldDefinition.DefaultDirective;
	private String mimeType=FieldDefinition.DefaultMimeType;
	private boolean compress=FieldDefinition.DefaultDoCompress;
	private int chunkSize=FieldDefinition.DefaultChunkSize;
	private String name="";
	
	/**
	 * Sets the name of the field
	 * 
	 * @param name the field name
	 */
	public void setName(String name)
	{
		if(name==null) this.name="";
		this.name=name;
	}
	
	/**
	 * Gets the field name
	 * 
	 * @return the field name
	 */
	public String getName()
	{
		return this.name;
	}
	
	/**
	 * Sets the MIME type
	 * 
	 * @param mimeType the MIME type
	 */
	public void setMimeType(String mimeType)
	{
		if(mimeType==null) this.mimeType="";
		else this.mimeType=mimeType;
	}
	
	/**
	 * Gets the MIME type
	 * 
	 * @return the MIME type
	 */
	public String getMimeType()
	{
		return this.mimeType;
	}

	/**
	 * Sets whether compression should be used during transfer
	 * 
	 * @param compress whether or not to use compression during transfer
	 */
	public void setCompress(boolean compress)
	{
		this.compress = compress;
	}

	/**
	 * Whether or not compression will be used during transfer
	 * 
	 * @return whether or not compression will be used during transfer
	 */
	public boolean isCompress()
	{
		return compress;
	}

	/**
	 * Sets the chunk size in bytes that will be used during partial transfer
	 * 
	 * @param chunkSize the chunk size in bytes that will be used during partial transfer
	 */
	public void setChunkSize(int chunkSize)
	{
		if(this.chunkSize>0) this.chunkSize = chunkSize;
	}

	/**
	 * Retrieves the chunk size in bytes that will be used during partial transfer
	 * 
	 * @return the chunk size in bytes that will be used during partial transfer
	 */
	public int getChunkSize()
	{
		if(this.chunkSize<=0) this.chunkSize=DefaultChunkSize;
		return this.chunkSize;
	}

	/**
	 * Sets the transport directive to be used during transfer
	 * 
	 * @param directive the directive to be used
	 */
	public void setTransportDirective(TransportDirective directive)
	{
		this.directive = directive;
	}

	/**
	 * Retrieves the transport directive to be used during transfer
	 * 
	 * @return the transport directive to be used during transfer
	 */
	public TransportDirective getTransportDirective()
	{
		return this.directive;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The properties checked for equality are the configuration values that can be set for the {@link FieldDefinition}
	 * and additionally, the {@link FieldDefinition#extendEquals(Object)} is invoked to check the extender equality logic
	 * </p>
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		if(!(obj instanceof FieldDefinition)) return false;
		if(!this.directive.equals(((FieldDefinition)obj).directive)) return false;
		if(!this.mimeType.equalsIgnoreCase(((FieldDefinition)obj).mimeType)) return false;
		if(this.compress^((FieldDefinition)obj).compress) return false;
		if(this.chunkSize!=((FieldDefinition)obj).chunkSize) return false;
		if(!this.name.equalsIgnoreCase(((FieldDefinition)obj).name)) return false;
		return this.extendEquals(obj);
	}
	
	/**
	 * Method to be implemented by extenders to check if two instances are equal
	 * 
	 * @param obj the instance to check for equality
	 * @return true if equal, false otherwise
	 */
	public abstract boolean extendEquals(Object obj);
	
	/**
	 * Deflates the field definition in the provided stream and calls {@link FieldDefinition#extendDeflate(DataOutput)}
	 * 
	 * @param out the stream to deflate to
	 * @throws GRS2RecordSerializationException A serialization error occurred
	 */
	public void deflate(DataOutput out) throws GRS2RecordSerializationException
	{
		try
		{
			out.writeUTF(this.directive.toString());
			out.writeUTF(this.mimeType);
			out.writeBoolean(this.compress);
			out.writeInt(this.chunkSize);
			out.writeUTF(this.name);
			this.extendDeflate(out);
		}
		catch(Exception ex)
		{
			throw new GRS2RecordSerializationException("Could not complete marshalling of definition",ex);
		}
	}
	
	public final Element toXML(Document doc) throws GRS2RecordSerializationException, GRS2RecordDefinitionException, DOMException {
		Element element = doc.createElement("fieldDefinition");

		Element elm = null;
		
		elm = doc.createElement("directive");
		elm.setTextContent(String.valueOf(this.directive.toString()));
		element.appendChild(elm);
		
		elm = doc.createElement("mimeType");
		elm.setTextContent(String.valueOf(this.mimeType));
		element.appendChild(elm);
		
		elm = doc.createElement("compress");
		elm.setTextContent(String.valueOf(this.compress));
		element.appendChild(elm);
		
		elm = doc.createElement("chunkSize");
		elm.setTextContent(String.valueOf(this.chunkSize));
		element.appendChild(elm);
		
		elm = doc.createElement("name");
		elm.setTextContent(String.valueOf(this.name));
		element.appendChild(elm);
		
		this.extendToXML(doc, element);
		
		return element;
	}
	
	public abstract void extendToXML(Document doc, Element element) throws GRS2RecordSerializationException;
	public abstract void extendFromXML(Element element) throws GRS2RecordSerializationException;
	
	
	/**
	 * Method to be implemented by class extenders to deflate additional information
	 * 
	 * @param out the stream to deflate to
	 * @throws GRS2RecordSerializationException A serialization error occurred
	 */
	public abstract void extendDeflate(DataOutput out) throws GRS2RecordSerializationException;
	
	/**
	 * Inflates the field definition from the previously deflated stream. After the internal information is retrieved,
	 * the {@link FieldDefinition#extendInflate(DataInput)} is invoked
	 * 
	 * @param in the stream to inflate from
	 * @throws GRS2RecordSerializationException A deserialization error occurred
	 */
	public void inflate(DataInput in) throws GRS2RecordSerializationException
	{
		try
		{
			this.directive=TransportDirective.valueOf(in.readUTF());
			this.mimeType=in.readUTF();
			this.compress=in.readBoolean();
			this.chunkSize=in.readInt();
			this.name=in.readUTF();
			this.extendInflate(in);
		}
		catch(Exception ex)
		{
			throw new GRS2RecordSerializationException("Could not complete unmarshalling of definition",ex);
		}
	}
	
	public final void fromXML(Element element) throws GRS2RecordSerializationException, GRS2RecordDefinitionException, DOMException {
		this.directive=TransportDirective.valueOf(element.getElementsByTagName("directive").item(0).getTextContent());
		this.mimeType=element.getElementsByTagName("mimeType").item(0).getTextContent();
		this.compress=Boolean.valueOf(element.getElementsByTagName("mimeType").item(0).getTextContent());
		this.chunkSize=Integer.valueOf(element.getElementsByTagName("chunkSize").item(0).getTextContent());
		this.name=element.getElementsByTagName("name").item(0).getTextContent();
		this.extendFromXML(element);
	}
	
	/**
	 * Method to be implemented by the class extenders to inflate additional information previously deflated
	 * 
	 * @param in the stream to inflate from
	 * @throws GRS2RecordSerializationException A deserialization error occurred
	 */
	public abstract void extendInflate(DataInput in) throws GRS2RecordSerializationException;
}
