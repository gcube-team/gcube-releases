package org.gcube.data.transfer.library.model;

import org.gcube.data.transfer.library.faults.InvalidSourceException;

import lombok.Data;

@Data 
public abstract class Source<T>{
	
	public abstract boolean validate() throws InvalidSourceException;
	
	public abstract void prepare();
	
	public abstract void clean();
	
	public abstract T getTheSource();
}