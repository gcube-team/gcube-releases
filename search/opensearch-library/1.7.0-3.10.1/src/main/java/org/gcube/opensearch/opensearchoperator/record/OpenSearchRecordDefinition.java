package org.gcube.opensearch.opensearchoperator.record;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import gr.uoa.di.madgik.grs.buffer.IBuffer.TransportDirective;
import gr.uoa.di.madgik.grs.record.GRS2RecordSerializationException;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;

public class OpenSearchRecordDefinition extends RecordDefinition {
	public static final String PayloadFieldName = "payload";
	public static final boolean defaultCompress = false;

	public OpenSearchRecordDefinition() {
		this(OpenSearchRecordDefinition.defaultCompress);
	}
	
	public OpenSearchRecordDefinition(boolean compress) {
		StringFieldDefinition sDef = new StringFieldDefinition(OpenSearchRecordDefinition.PayloadFieldName);
		sDef.setTransportDirective(TransportDirective.Full);
		sDef.setCompress(OpenSearchRecordDefinition.defaultCompress);
		this.Fields = new ArrayList<FieldDefinition>();
		this.Fields.add(sDef);
	}
	
	public void copyFrom(OpenSearchRecordDefinition other) throws Exception
	{
		super.copyFrom(other);
	}
	
	@Override
	public boolean extendEquals(Object obj) {
		if(!(obj instanceof OpenSearchRecordDefinition))
			return false;
		return true;
	}
	
	@Override
	public void extendDeflate(DataOutput out) throws GRS2RecordSerializationException { }
	
	@Override
	public void extendInflate(DataInput in) throws GRS2RecordSerializationException { }

	@Override
	public void extendToXML(Element element) throws GRS2RecordSerializationException { }
	
	@Override
	public void extendFromXML(Element element) throws GRS2RecordSerializationException { }

}
