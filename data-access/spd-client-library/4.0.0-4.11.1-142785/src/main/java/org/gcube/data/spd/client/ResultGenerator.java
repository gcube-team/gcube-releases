package org.gcube.data.spd.client;

import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.field.StringField;

import org.gcube.data.spd.model.binding.Bindings;
import org.gcube.data.streams.exceptions.StreamSkipSignal;
import org.gcube.data.streams.generators.Generator;

public class ResultGenerator<T> implements Generator<GenericRecord, T> {

	
	public ResultGenerator(){}
	
	@Override
	public T yield(GenericRecord record) {
		try {
			return Bindings.<T>fromXml(((StringField)record.getField("result")).getPayload());
		} catch (Exception e) {
			throw new StreamSkipSignal();
		}
	}

}