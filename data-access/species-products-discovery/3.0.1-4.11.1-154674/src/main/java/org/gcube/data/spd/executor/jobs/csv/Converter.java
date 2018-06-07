package org.gcube.data.spd.executor.jobs.csv;


public interface Converter<T, D> {
	
	public D convert(T input) throws Exception;
}
