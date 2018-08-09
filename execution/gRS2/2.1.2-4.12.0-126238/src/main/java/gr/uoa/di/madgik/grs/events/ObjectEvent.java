package gr.uoa.di.madgik.grs.events;

import gr.uoa.di.madgik.grs.events.BufferEvent.EventSource;
import gr.uoa.di.madgik.grs.record.GRS2RecordDefinitionException;
import gr.uoa.di.madgik.grs.record.GRS2RecordSerializationException;
import gr.uoa.di.madgik.grs.record.IPumpable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * {@link BufferEvent} extender that propagates an object that implements {@link IPumpable}
 * 
 * @author gpapanikos
 *
 */
public class ObjectEvent extends BufferEvent
{
	private IPumpable item=null;
	
	/**
	 * Create a new instance
	 */
	public ObjectEvent(){}
	
	/**
	 * Create a new instance
	 * 
	 * @param item the payload object
	 */
	public ObjectEvent(IPumpable item)
	{
		this.item=item;
	}

	/**
	 * Sets the payload object
	 * 
	 * @param item the payload object
	 */
	public void setItem(IPumpable item)
	{
		this.item = item;
	}

	/**
	 * Retrieves the payload object 
	 * 
	 * @return the payload object
	 */
	public IPumpable getItem()
	{
		return item;
	}

	@Override
	public void extendToXML(Document doc, Element element) throws GRS2RecordSerializationException {
		try {
			Element elm = null;
			element.appendChild(elm);

			if (this.item != null) {
				elm = doc.createElement("item");
				elm.setTextContent(String.valueOf(this.item.getClass()));
				element.appendChild(elm);
				element.appendChild(this.item.toXML(doc));
			}

		} catch (Exception e) {
			throw new GRS2RecordSerializationException("unable to create key value event xml", e);
		}
	}

	@Override
	public void extendFromXML(Element element) throws GRS2RecordSerializationException {
		try {
			if (element.getElementsByTagName("item") != null && element.getElementsByTagName("item").getLength() > 0) {
				String itemString = element.getElementsByTagName("item").item(0).getTextContent();
				this.item = (IPumpable) Class.forName(itemString).newInstance();
				this.item.fromXML(element);
			}

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
			if(this.item==null) out.writeBoolean(false);
			else
			{
				out.writeBoolean(true);
				out.writeUTF(this.item.getClass().getName());
				this.item.deflate(out);
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
			if(in.readBoolean())
			{
				String objType=in.readUTF();
				this.item=(IPumpable)Class.forName(objType).newInstance();
				this.item.inflate(in);
			}
		} catch (Exception e)
		{
			throw new GRS2RecordSerializationException("Could not inflate event", e);
		}
	}
}
