package gr.uoa.di.madgik.grs.events;

import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.record.GRS2RecordDefinitionException;
import gr.uoa.di.madgik.grs.record.GRS2RecordSerializationException;
import gr.uoa.di.madgik.grs.record.IPumpable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This is the base class that all events that can be emitted and received through the {@link IBuffer}
 * must extend. All extenders of it must declare a default no arguments constructor
 * 
 * @author gpapanikos
 *
 */
public abstract class BufferEvent implements IPumpable, Serializable
{
	/**
	 * The source of the event
	 * 
	 * @author gpapanikos
	 *
	 */
	public enum EventSource
	{
		/**
		 * The event is emitted from the writer targeting the reader
		 */
		Writer,
		/**
		 * The event is emitted from the reader targeting the writer
		 */
		Reader
	}
	
	private EventSource source;
	
	/**
	 * Retrieves the source of the event
	 * 
	 * @return the event source
	 */
	public EventSource getSource()
	{
		return this.source;
	}
	
	/**
	 * Sets the event source
	 * 
	 * @param source the event source
	 */
	public void setSource(EventSource source)
	{
		this.source=source;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.record.IPumpable#deflate(java.io.DataOutput)
	 */
	public void deflate(DataOutput out) throws GRS2RecordSerializationException
	{
		try
		{
			out.writeUTF(this.source.toString());
			this.extendDeflate(out);
		} catch (IOException e)
		{
			throw new GRS2RecordSerializationException("Could not deflate event", e);
		}
	}
	
	/**
	 * This method is implemented by {@link BufferEvent} extenders to deflate the additional information
	 * they handle
	 * 
	 * @param out the stream to deflate to
	 * @throws GRS2RecordSerializationException there was a problem deflating the event
	 */
	public abstract void extendDeflate(DataOutput out) throws GRS2RecordSerializationException;

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.record.IPumpable#inflate(java.io.DataInput)
	 */
	public void inflate(DataInput in) throws GRS2RecordSerializationException
	{
		try
		{
			this.source=EventSource.valueOf(in.readUTF());
			this.extendInflate(in);
		} catch (IOException e)
		{
			throw new GRS2RecordSerializationException("Could not inflate event", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.record.IPumpable#inflate(java.io.DataInput, boolean)
	 */
	public void inflate(DataInput in, boolean reset) throws GRS2RecordSerializationException
	{
		this.inflate(in);
	}
	
	/**
	 * This method is implemented by {@link BufferEvent} extenders to inflate the additional information
	 * they handle
	 * 
	 * @param in the stream to inflate from
	 * @throws GRS2RecordSerializationException there was a problem inflating the event
	 */
	public abstract void extendInflate(DataInput in) throws GRS2RecordSerializationException;
	
	@Override
	public Element toXML(Document doc) throws GRS2RecordSerializationException, GRS2RecordDefinitionException,
			DOMException {
		
		Element element= doc.createElement("keyValueEvent");
		Element elm = null;
		
		elm = doc.createElement("source");
		elm.setTextContent(this.getSource().toString());
		element.appendChild(elm);

		this.extendToXML(doc, element);
		
		return element;
	}
	
	@Override
	public void fromXML(Element element) throws GRS2RecordSerializationException, GRS2RecordDefinitionException,
			DOMException {
		
		String source = element.getElementsByTagName("source").item(0).getTextContent();
		this.source=EventSource.valueOf(source);
		this.extendFromXML(element);
	}
	
	
	
	
	public abstract void extendToXML(Document doc, Element element) throws GRS2RecordSerializationException;
	public abstract void extendFromXML(Element element) throws GRS2RecordSerializationException;
}
