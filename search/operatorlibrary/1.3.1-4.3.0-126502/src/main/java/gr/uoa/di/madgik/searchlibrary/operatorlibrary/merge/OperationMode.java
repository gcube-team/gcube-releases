package gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge;

public enum OperationMode {
	/**
	 * Forwards records in the order that they are read from each input source. 
	 * The output contains intermingled records from the input data sources
	 */
	FIFO,
	/**
	 * Reads one input source at a time, starting with the one that is first available
	 */
	FirstAvailable,
	/**
	 * Under the condition that the records arrive sorted by rank for each input source, the results will also
	 * be produced sorted by rank. Note that this implies that the results will consist of intermingled records
	 * from all input sources, in the same manner as {@link OperationMode#FIFO}
	 */
	Sort,
	
	Fusion,
	
	
	
}
