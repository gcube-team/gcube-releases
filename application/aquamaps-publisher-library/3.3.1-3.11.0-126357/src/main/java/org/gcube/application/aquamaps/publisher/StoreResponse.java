package org.gcube.application.aquamaps.publisher;


public class StoreResponse<T> {

	public static enum PerformedOperation{
		NEWLY_INSERTED, USED_EXISTING, UPDATED_EXISTING
	}
	
	private T storedObj;
	private PerformedOperation operation;
	public StoreResponse(T storedObj, PerformedOperation operation) {
		super();
		this.storedObj = storedObj;
		this.operation = operation;
	}
	public PerformedOperation getOperation() {
		return operation;
	}
	public T getStoredId() {
		return storedObj;
	}
}
