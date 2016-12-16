package org.gcube.data.spd.gbifplugin.search.query;

import java.util.Iterator;
import java.util.Map;

import lombok.NonNull;

import org.gcube.common.core.utils.logging.GCUBELog;

public abstract class PagedQueryIterator<T> implements Iterator<T>{

	protected GCUBELog log = new GCUBELog(PagedQueryIterator.class);
	
	private @NonNull PagedQueryObject pagedQuery;

	public PagedQueryIterator(@NonNull PagedQueryObject pagedQuery) {
		this.pagedQuery = pagedQuery;
	}

	protected abstract T getObject(Map<String,Object> mappedObject) throws Exception;

	Map<String, Object> mapping;
	
	Iterator<Map<String,Object>> resultIterator;

	Map<String,Object> actualObject= null;
	
	Long start = null; 
	Long parsingStart = null; 
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean hasNext() {
		try{
			if (resultIterator==null){
				String query = pagedQuery.buildNext();
				start = System.currentTimeMillis();
				mapping = MappingUtils.getObjectMapping(query);
				parsingStart = System.currentTimeMillis();
				log.trace("[Benchmark] got Elements with query "+query+" and took "+(parsingStart-start));
				resultIterator = ((Iterable<Map<String,Object>>) mapping.get("results")).iterator();
			}
						
			if (!resultIterator.hasNext()){
				log.trace("[Benchmark] page retrieved and parsed in "+(System.currentTimeMillis()-start));
				if ((Boolean)mapping.get("endOfRecords")){
					log.trace("is end of records, no next element");			
					return false;
				}
				resultIterator = null;
				
			} else{
				actualObject = resultIterator.next();
				if (useIt(actualObject))
					return true;
			}
			return this.hasNext();
			
		}catch(Exception e){
			log.error("error computing hasNext",e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public T next() {
		try{
			return getObject(actualObject);
		}catch(Exception e){
			log.error("error computing next",e);
			throw new RuntimeException(e);
		}
	}

	protected boolean useIt(Map<String,Object> mappedObject){
		return true;
	}
	
	@Override
	public void remove() {
		resultIterator = null;
	}

}
