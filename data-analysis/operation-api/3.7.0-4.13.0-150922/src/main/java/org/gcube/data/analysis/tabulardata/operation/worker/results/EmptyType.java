package org.gcube.data.analysis.tabulardata.operation.worker.results;


public final class EmptyType implements Result{
	
	private static final EmptyType instance = new EmptyType();
	
	private EmptyType(){}
	
	public static EmptyType instance(){
		return instance;
	}

}
