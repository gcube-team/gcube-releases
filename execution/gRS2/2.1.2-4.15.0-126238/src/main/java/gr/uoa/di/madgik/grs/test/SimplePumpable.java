package gr.uoa.di.madgik.grs.test;

import gr.uoa.di.madgik.grs.record.GRS2RecordDefinitionException;
import gr.uoa.di.madgik.grs.record.GRS2RecordSerializationException;
import gr.uoa.di.madgik.grs.record.IPumpable;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.UUID;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SimplePumpable implements IPumpable {
	private static int counterAll = 0;

	public String payload = null;
	public int counter = 0;

	public SimplePumpable() {
	}

	public void populate() {
		payload = UUID.randomUUID().toString();
		counter = counterAll;
		counterAll += 1;
	}

	@Override
	public String toString() {
		return this.payload + " (" + this.counter + ")";
	}

	public void deflate(DataOutput out) throws GRS2RecordSerializationException {
		try {
			out.writeUTF(payload);
			out.writeInt(counter);
		} catch (Exception ex) {
			throw new GRS2RecordSerializationException("oups", ex);
		}
	}

	public void inflate(DataInput in) throws GRS2RecordSerializationException {
		try {
			this.payload = in.readUTF();
			this.counter = in.readInt();
		} catch (Exception ex) {
			throw new GRS2RecordSerializationException("oups", ex);
		}
	}

	public void inflate(DataInput in, boolean reset) throws GRS2RecordSerializationException {
		this.inflate(in);
	}

	public Element toXML(Document doc) throws GRS2RecordSerializationException, GRS2RecordDefinitionException,
	DOMException {
		Element elm = null;

		if (this.payload != null) {
			elm = doc.createElement("payload");
			elm.setTextContent(this.payload.toString());
		}
		
		return elm;

	}

	@Override
	public void fromXML(Element element) throws GRS2RecordSerializationException, GRS2RecordDefinitionException,
			DOMException {
		String payload = element.getElementsByTagName("payload").item(0).getTextContent();
		this.payload = payload;
		
	}
	
	


}
