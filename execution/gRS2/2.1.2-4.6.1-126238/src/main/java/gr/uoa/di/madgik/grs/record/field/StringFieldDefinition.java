package gr.uoa.di.madgik.grs.record.field;

import gr.uoa.di.madgik.grs.buffer.IBuffer.TransportDirective;
import gr.uoa.di.madgik.grs.record.GRS2RecordSerializationException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The {@link FieldDefinition} implementation for {@link StringField}
 * 
 * @author gpapanikos
 *
 */
public class StringFieldDefinition extends FieldDefinition implements Serializable
{
	/**
	 * The default MIME type currently set to text/plain
	 */
	public static final String DefaultMimeType="text/plain";
	/**
	 * The default transport directive currently set to {@link TransportDirective#Full}
	 */
	public static final TransportDirective DefaultDirective=TransportDirective.Full;
	/**
	 * The default charset used to encode the payload for transport. Currently set to UTF-8
	 */
	public static final String DefaultCharset = "UTF-8";
	
	private String charset=StringFieldDefinition.DefaultCharset;
	
	/**
	 * Creates a new instance
	 */
	public StringFieldDefinition()
	{
		this.setMimeType(StringFieldDefinition.DefaultMimeType);
		this.setTransportDirective(StringFieldDefinition.DefaultDirective);
	}
	
	/**
	 * Creates a new instance
	 * 
	 * @param name the field definition name
	 */
	public StringFieldDefinition(String name)
	{
		this.setMimeType(StringFieldDefinition.DefaultMimeType);
		this.setTransportDirective(StringFieldDefinition.DefaultDirective);
		this.setName(name);
	}
	
	/**
	 * The charset name to use to encode the payload
	 * 
	 * @param charset the charset name
	 */
	public void setCharset(String charset)
	{
		this.charset=charset;
	}
	
	/**
	 * Retrieves the charset name used to encode the payload
	 * 
	 * @return the charset name
	 */
	public String getCharset()
	{
		return this.charset;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The set {@link TransportDirective} is not affected. Only the default {@link TransportDirective#Full} is supported 
	 * </p>
	 * 
	 * @see gr.uoa.di.madgik.grs.record.field.FieldDefinition#setTransportDirective(gr.uoa.di.madgik.grs.buffer.IBuffer.TransportDirective)
	 */
	public void setTransportDirective(TransportDirective directive)
	{
		//nothing changes
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The default {@link TransportDirective#Full} is returned
	 * </p>
	 * 
	 * @see gr.uoa.di.madgik.grs.record.field.FieldDefinition#getTransportDirective()
	 */
	public TransportDirective getTransportDirective()
	{
		return StringFieldDefinition.DefaultDirective;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.record.field.FieldDefinition#extendEquals(java.lang.Object)
	 */
	@Override
	public boolean extendEquals(Object obj)
	{
		if(!(obj instanceof StringFieldDefinition)) return false;
		if(!this.charset.equals(((StringFieldDefinition)obj).charset)) return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.record.field.FieldDefinition#extendDeflate(java.io.DataOutput)
	 */
	@Override
	public void extendDeflate(DataOutput out) throws GRS2RecordSerializationException
	{
		try
		{
			out.writeUTF(this.charset);
		}
		catch(Exception ex)
		{
			throw new GRS2RecordSerializationException("Could not complete marshalling of definition",ex);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.record.field.FieldDefinition#extendInflate(java.io.DataInput)
	 */
	@Override
	public void extendInflate(DataInput in) throws GRS2RecordSerializationException
	{
		try
		{
			this.charset=in.readUTF();
		}
		catch(Exception ex)
		{
			throw new GRS2RecordSerializationException("Could not complete unmarshalling of definition",ex);
		}
	}

	@Override
	public void extendToXML(Document doc, Element element) throws GRS2RecordSerializationException {
		Element el = doc.createElement("charset");
		el.setTextContent(this.charset);
		element.appendChild(el);
	}

	@Override
	public void extendFromXML(Element element) throws GRS2RecordSerializationException {
		this.charset = element.getElementsByTagName("charset").item(0).getTextContent();
		
	}

}
