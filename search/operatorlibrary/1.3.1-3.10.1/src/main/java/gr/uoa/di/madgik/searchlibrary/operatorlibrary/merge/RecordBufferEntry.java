package gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge;

import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;

/**
 * A utility class used by readers to pass records along with their id (actually, their index in a vector of {@link ReaderHolder}
 * The inclusion of the reader id is necessary in order for the {@link MergeWorker} involved in the authoring of the final result set to infer
 * the exact {@link RecordDefinition} offset for each record encountered via the intermediate buffer
 * 
 * @author gerasimos.farantatos
 *
 */
public class RecordBufferEntry {
	public final Record record;
	public final int id;
	
	/**
	 * Creates a new instance
	 * 
	 * @param record The record to be passed to a writer via a buffer
	 * @param id The id of the reader from which the record was read
	 */
	public RecordBufferEntry(Record record, int id) {
		this.record = record;
		this.id = id;
	}
}
