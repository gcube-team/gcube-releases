/**
 * 
 */
package org.gcube.data.oai.tmplugin.repository.iterators;

import java.net.URI;
import java.util.NoSuchElementException;


import org.gcube.common.data.Record;
import org.gcube.common.data.RecordIterator;
import org.gcube.data.streams.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fabio Simeoni
 *
 */
public abstract class RecordIter implements Stream<Record> {

	RecordIterator records = null;
	private final static Logger log = LoggerFactory.getLogger(RecordIter.class);
 
	public Record next()  {
		
		//		if (!hasNext())
		//			throw new NoSuchElementException();

		Record record = null;
		
		try {
			do{
				record = records.next();

			}while(notValid(record));

		} catch (Exception e) {
			log.warn("an error occurred trying to retrieve a record (moving to the next element)",e);	
		}

		return record;

	};

	//check if the record is valid
	private boolean notValid(Record record) throws Exception {

		//		System.out.println(record.getHeader().getIdentifier() + " valid?");
		if (record==null || record.getMetadata()==null || record.IsDeleted()){	
//			try{
//				log.debug("record not valid " + record.getHeader().getIdentifier());
//			}catch (Exception e) {}
			hasNext();
			return true;
		}
		return false;
	}

	public URI locator() {return null;} //unused

	/**{@inheritDoc}*/
	@Override
	public void close() {}

	@Override
	public void remove() {
	}

}
