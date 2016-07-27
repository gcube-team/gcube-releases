package org.gcube.opensearch.opensearchoperator.record;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import gr.uoa.di.madgik.grs.buffer.GRS2BufferException;
import gr.uoa.di.madgik.grs.record.GRS2RecordDefinitionException;
import gr.uoa.di.madgik.grs.record.GRS2RecordSerializationException;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.StringField;

public class OpenSearchRecord extends GenericRecord {
	
	private String uniqueIdentifier = "";
	
	public OpenSearchRecord(String payload) {
		this.setFields(new Field[]{new StringField(payload)});
	}
	
	public OpenSearchRecord(String payload, String uniqueIdentifier) {
		this.uniqueIdentifier = uniqueIdentifier;
		this.setFields(new Field[]{new StringField(payload)});
	}
	
	
	public void setUniqueIdentifier(String uniqueIdentifier) {
		this.uniqueIdentifier = uniqueIdentifier;
	}
	
	public String getUniqueIdentifier() {
		return this.uniqueIdentifier;
	}
	
	public void setPayload(String payload) throws GRS2RecordDefinitionException, GRS2BufferException {
		this.getField(OpenSearchRecordDefinition.PayloadFieldName).setPayload(payload);
	}
	
	public String getPayload() throws GRS2RecordDefinitionException, GRS2BufferException {
		return this.getField(OpenSearchRecordDefinition.PayloadFieldName).getPayload();
	}
	
	@Override
	public StringField getField(String name) throws GRS2RecordDefinitionException, GRS2BufferException {
		return (StringField)super.getField(name);
	}
	
	@Override
	public void extendSend(DataOutput out) throws GRS2RecordSerializationException {
		this.extendDeflate(out);
	}
	
	@Override
	public void extendReceive(DataInput in) throws GRS2RecordSerializationException {
		this.extendInflate(in, false);
	}
	
	@Override
	public void extendDeflate(DataOutput out) throws GRS2RecordSerializationException {
		try {
			out.writeUTF(this.uniqueIdentifier);
		}catch(IOException e) {
			throw new GRS2RecordSerializationException("Could not deflate record", e);
		}
	}
	
	@Override
	public void extendInflate(DataInput in, boolean reset) throws GRS2RecordSerializationException {
		try {
			this.uniqueIdentifier = in.readUTF();
		}catch(IOException e) {
			throw new GRS2RecordSerializationException("Could not inflate record", e);
		}
	}
	
	@Override
	public void extendDispose() {
		this.uniqueIdentifier = null;
	}
	
	@Override
	protected void extendMakeLocal() {
		//nothing to reset
	}
	
	public void extendSendToXML(Document doc, Element element) throws GRS2RecordSerializationException
	{
		Element elm = doc.createElement("uniqueIdentifier");
		elm.setTextContent(this.uniqueIdentifier);
		element.appendChild(elm);
	}
	
	public void extendReceiveFromXML(Element element) throws GRS2RecordSerializationException
	{
		this.uniqueIdentifier = element.getElementsByTagName("uniqueIdentifier").item(0).getTextContent();
	}
}
