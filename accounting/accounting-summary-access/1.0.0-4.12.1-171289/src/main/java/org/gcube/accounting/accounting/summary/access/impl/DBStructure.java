package org.gcube.accounting.accounting.summary.access.impl;

public class DBStructure {

	public static class Measure{
		public static final String TABLENAME="monthly";
		public static final String TIME="time";
		public static final String DIMENSION="dimension";
		public static final String MEASURE="measure";
		public static final String CONTEXT="context";
	}
	
	public static class DIMENSIONS{
		public static final String TABLENAME="dimensions";
		public static final String ID="id";
		public static final String LABEL="LABEL";
		public static final String GROUP="dimension_group";
		public static final String AGGREGATED_MEASURE="aggregated_measure";
	}
	
	public static class CONTEXTS{
		public static final String TABLENAME="contexts";
		public static final String ID="id";
		public static final String LABEL="LABEL";
	}
}
