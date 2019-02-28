package org.gcube.data.streams.publishers;

import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;

import org.gcube.data.streams.Stream;
import org.gcube.data.streams.generators.Generator;

/**
 * A {@link RecordFactory} for {@link #STRING_RECORD}s with serialisations of {@link Stream} elements. 
 * <p>
 * An untyped record is a record with a string-valued <code>payload</code> field.
 * 
 * @author Fabio Simeoni
 *
 * @param <E> the type of the serialised values
 */
public class RsStringRecordFactory<E> implements RecordFactory<E> {

	/** The type definition of a record with a string-valued <code>payload</code> field. */
	public static final RecordDefinition STRING_RECORD =  
		new GenericRecordDefinition(new FieldDefinition[]{new StringFieldDefinition("value")});
   
	private final Generator<E,String> serialiser;
	
	/**
	 * Creates an instance with a {@link Generator} that returns serialisations of {@link Stream} elements.
	 * @param serialiser the serialiser
	 */
	public RsStringRecordFactory(Generator<E,String> serialiser) {
		this.serialiser=serialiser;
	}
	
	@Override
	public GenericRecord newRecord(E element) {
		String serialisation = serialiser.yield(element);
		GenericRecord  record = new GenericRecord();
		record.setFields(new Field[]{new StringField(serialisation)});
		return record;
	};
	
	@Override
	public RecordDefinition[] definitions() {
		return new RecordDefinition[]{STRING_RECORD};
	}
}
