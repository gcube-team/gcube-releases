package org.gcube.data.access.storagehub;

public class Range {

	private int start;
	private int limit;
	
	public Range(int start, int limit) {
		super();
		this.start = start;
		this.limit = limit;
	}
	
	public int getStart() {
		return start;
	}
	
	public int getLimit() {
		return limit;
	}

	@Override
	public String toString() {
		return "Range [start=" + start + ", limit=" + limit + "]";
	}
	
	
	
}
