package org.gcube.data.streams.publishers;

import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;

import org.gcube.data.streams.Iteration;
import org.gcube.data.streams.Stream;

/**
 * Generates {@link Record}s from the elements of a {@link Stream}.
 * 
 * @author Fabio Simeoni
 *
 * @param <E> the type of the elements
 */
public interface RecordFactory<E> {

	/** The ongoing iteration. */
	static final Iteration iteration = new Iteration();
	
	/**
	 * Returns the definitions of the records.
	 * @return the definitions
	 */
	RecordDefinition[] definitions();
	
	/**
	 * Generates a {@link Record} from a {@link Stream} element.
	 * @param element the element
	 * @return the record
	 * @throws RuntimeException if no record can be generated from the input element
	 */
	Record newRecord(E element);
}
