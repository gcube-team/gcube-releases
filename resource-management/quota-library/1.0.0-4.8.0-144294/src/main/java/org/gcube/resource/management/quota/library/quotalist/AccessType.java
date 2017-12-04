package org.gcube.resource.management.quota.library.quotalist;

public enum AccessType {
	  ALL(0),
	  ACCESS(1),
	  DELETE(2),
	  EXECUTE(3),
	  WRITE(4);
	  @SuppressWarnings("unused")
		private int value;
		private AccessType(int value){
			this.value=value;
		}
}
