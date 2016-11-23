package gr.uoa.di.madgik.grs.record.field;

import gr.uoa.di.madgik.grs.buffer.IBuffer.TransportDirective;
import gr.uoa.di.madgik.grs.record.GRS2RecordSerializationException;

import java.io.DataInput;
import java.io.DataOutput;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The {@link FieldDefinition} implementation for {@link ObjectField}. The compression instruction is not used
 * for the respective {@link ObjectField} as the actual payload serialization is performed outside the {@link Field}
 * methods
 * 
 * @author gpapanikos
 *
 */
public class ObjectFieldDefinition extends FieldDefinition
{
	/**
	 * The default MIME type currently set to text/plain
	 */
	public static final String DefaultMimeType="application/octet-stream";
	/**
	 * The default transport directive currently set to {@link TransportDirective#Full}
	 */
	public static final TransportDirective DefaultDirective=TransportDirective.Full;
	
	/**
	 * Creates a new instance
	 */
	public ObjectFieldDefinition()
	{
		this.setMimeType(ObjectFieldDefinition.DefaultMimeType);
		this.setTransportDirective(ObjectFieldDefinition.DefaultDirective);
	}
	
	/**
	 * Creates a new instance
	 * 
	 * @param name the field definition name
	 */
	public ObjectFieldDefinition(String name)
	{
		this.setMimeType(ObjectFieldDefinition.DefaultMimeType);
		this.setTransportDirective(ObjectFieldDefinition.DefaultDirective);
		this.setName(name);
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
		return ObjectFieldDefinition.DefaultDirective;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.record.field.FieldDefinition#extendEquals(java.lang.Object)
	 */
	@Override
	public boolean extendEquals(Object obj)
	{
		if(!(obj instanceof ObjectFieldDefinition)) return false;
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
		//nothing to deflate
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.record.field.FieldDefinition#extendInflate(java.io.DataInput)
	 */
	@Override
	public void extendInflate(DataInput in) throws GRS2RecordSerializationException
	{
		//nothing to inflate
	}

	@Override
	public void extendToXML(Document doc, Element element) throws GRS2RecordSerializationException {
		//nothing to deflate
	}

	@Override
	public void extendFromXML(Element element) throws GRS2RecordSerializationException {
		//nothing to inflate
	}

}
