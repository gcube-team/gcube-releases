package gr.uoa.di.madgik.grs.record.field;

import gr.uoa.di.madgik.grs.buffer.IBuffer.TransportDirective;
import gr.uoa.di.madgik.grs.record.GRS2RecordSerializationException;

import java.io.DataInput;
import java.io.DataOutput;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The {@link FieldDefinition} implementation for {@link FileField}
 * 
 * @author gpapanikos
 *
 */
public class FileFieldDefinition extends FieldDefinition
{
	/**
	 * The default MIME type currently set to application/octet-stream
	 */
	public static final String DefaultMimeType="application/octet-stream";
	/**
	 * The default transport directive currently set to {@link TransportDirective#Inherit}
	 */
	public static final TransportDirective DefaultDirective=TransportDirective.Inherit;
	/**
	 * The default charset used to encode the original payload path name for transport. Currently set to UTF-8
	 */
	public static final String DefaultCharset = "UTF-8";
	/**
	 * The default size of the local buffer used to read the local file and send over the stream
	 */
	public static final int DefaultLocalBuffer = 100*1024;
	/**
	 * The default producer side cleanup behavior
	 */
	public static final boolean DefaultDeleteOnDispose = false;
	
	private String charset=FileFieldDefinition.DefaultCharset;
	private int localBuffer=FileFieldDefinition.DefaultLocalBuffer;
	private boolean deleteOnDispose=FileFieldDefinition.DefaultDeleteOnDispose;
	
	/**
	 * Creates a new instance
	 */
	public FileFieldDefinition()
	{
		this.setMimeType(FileFieldDefinition.DefaultMimeType);
		this.setTransportDirective(FileFieldDefinition.DefaultDirective);
	}
	
	/**
	 * Creates a new instance
	 * 
	 * @param name the field definition name
	 */
	public FileFieldDefinition(String name)
	{
		this.setMimeType(FileFieldDefinition.DefaultMimeType);
		this.setTransportDirective(FileFieldDefinition.DefaultDirective);
		this.setName(name);
	}
	
	/**
	 * The charset name to use to encode the payload path
	 * 
	 * @param charset the charset name
	 */
	public void setCharset(String charset)
	{
		this.charset=charset;
	}
	
	/**
	 * Retrieves the charset name used to encode the payload path
	 * 
	 * @return the charset name
	 */
	public String getCharset()
	{
		return this.charset;
	}
	
	/**
	 * Sets the local buffer size used when accessing the local file
	 * 
	 * @param localBuffer the local buffer size
	 */
	public void setLocalBuffer(int localBuffer)
	{
		this.localBuffer=localBuffer;
	}
	
	/**
	 * Retrieves the local buffer size used when accessing the local file
	 * 
	 * @return the local buffer size
	 */
	public int getLocalBuffer()
	{
		return this.localBuffer;
	}

	/**
	 * Sets the local file cleanup behavior
	 * 
	 * @param deleteOnDispose true if the local file should be deleted when this {@link FileField} is disposed, false otherwise
	 */
	public void setDeleteOnDispose(boolean deleteOnDispose)
	{
		this.deleteOnDispose=deleteOnDispose;
	}
	
	/**
	 * Retrieves the local file cleanup behavior
	 * 
	 * @return true if the local file will be deleted when this {@link FileField} is disposed, false otherwise
	 */
	public boolean getDeleteOnDispose()
	{
		return this.deleteOnDispose;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.record.field.FieldDefinition#extendEquals(java.lang.Object)
	 */
	@Override
	public boolean extendEquals(Object obj)
	{
		if(!(obj instanceof FileFieldDefinition)) return false;
		if(!this.charset.equals(((FileFieldDefinition)obj).charset)) return false;
		if(this.localBuffer!=((FileFieldDefinition)obj).localBuffer) return false;
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
			out.writeInt(this.localBuffer);
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
			this.localBuffer=in.readInt();
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
		
		el = doc.createElement("localBufferSize");
		el.setTextContent(String.valueOf(this.localBuffer));
		element.appendChild(el);
		
	}

	@Override
	public void extendFromXML(Element element) throws GRS2RecordSerializationException {
		this.charset = element.getElementsByTagName("charset").item(0).getTextContent();
		this.localBuffer = Integer.valueOf(element.getElementsByTagName("localBufferSize").item(0).getTextContent()); 
	}

}
