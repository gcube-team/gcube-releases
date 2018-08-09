package org.gcube.data.access.bean;

public enum PositionType {
	FIXED_PRIORITY {
		@Override
		public String toString() {
			return "fixedPriority";
		}
	},
	OFFSET_FROM_TOP {
		@Override
		public String toString() {
			return "offsetFromTop";
		}
	},
	OFFSET_FROM_BOTTOM {
		@Override
		public String toString() {
			return "offsetFromBottom";
		}
	}

}
