package gr.uoa.di.madgik.grs.events;

import gr.uoa.di.madgik.grs.record.GRS2RecordDefinitionException;
import gr.uoa.di.madgik.grs.record.GRS2RecordSerializationException;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * {@link BufferEvent} extender that propagates a simple string based key value pair as the event payload
 * 
 * @author gpapanikos
 * 
 */
public class KeyValueEvent extends BufferEvent
{
	private String key=null;
	private String value=null;

	/**
	 * Create a new instance
	 */
	public KeyValueEvent(){}
	
	/**
	 * Create a new instance
	 * 
	 * @param key the key of the payload
	 * @param value the value of the payload
	 */
	public KeyValueEvent(String key,String value)
	{
		this.key=key;
		this.value=value;
	}
	
	/**
	 * Retrieves the payload key
	 * 
	 * @return the payload key
	 */
	public String getKey()
	{
		return this.key;
	}
	
	/**
	 * Sets the payload key
	 * 
	 * @param key the payload key
	 */
	public void setKey(String key)
	{
		this.key=key;
	}
	
	/**
	 * Retrieves the payload value
	 * 
	 * @return the payload value
	 */
	public String getValue()
	{
		return this.value;
	}
	
	/**
	 * Sets the payload value
	 * 
	 * @param value the payload value
	 */
	public void setValue(String value)
	{
		this.value=value;
	}
	
	@Override
	public void extendToXML(Document doc, Element element) throws GRS2RecordSerializationException {
		try {
		Element elm = null;

		if (this.key != null) {
			elm = doc.createElement("key");
			elm.setTextContent(String.valueOf(this.key));
			element.appendChild(elm);
		}

		if (this.value != null) {
			elm = doc.createElement("value");
			elm.setTextContent(String.valueOf(this.value));
			element.appendChild(elm);
		}
		}catch (Exception e) {
			throw new GRS2RecordSerializationException("unable to create key value event xml", e);
		}
	}

	@Override
	public void extendFromXML(Element element) throws GRS2RecordSerializationException {
		try {
			if (element.getElementsByTagName("key") != null && element.getElementsByTagName("key").getLength() >0)
				this.key = element.getElementsByTagName("key").item(0).getTextContent();
			if (element.getElementsByTagName("value") != null && element.getElementsByTagName("value").getLength() >0)
				this.value = element.getElementsByTagName("value").item(0).getTextContent();
		} catch (Exception e) {
			throw new GRS2RecordSerializationException("unable to get key value event from xml", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.events.BufferEvent#extendDeflate(java.io.DataOutput)
	 */
	@Override
	public void extendDeflate(DataOutput out) throws GRS2RecordSerializationException
	{
		try
		{
			if(this.key==null) out.writeBoolean(false);
			else
			{
				out.writeBoolean(true);
				out.writeUTF(this.key);
			}
			if(this.value==null) out.writeBoolean(false);
			else
			{
				out.writeBoolean(true);
				out.writeUTF(this.value);
			}
		} catch (IOException e)
		{
			throw new GRS2RecordSerializationException("Could not deflate event", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.events.BufferEvent#extendInflate(java.io.DataInput)
	 */
	@Override
	public void extendInflate(DataInput in) throws GRS2RecordSerializationException
	{
		try
		{
			if(in.readBoolean()) this.key=in.readUTF();
			if(in.readBoolean()) this.value=in.readUTF();
		} catch (IOException e)
		{
			throw new GRS2RecordSerializationException("Could not inflate event", e);
		}
	}

}
