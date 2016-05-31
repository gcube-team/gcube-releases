package org.gcube.spatial.data.gis.symbology;

/**
	 * Create a rendering style to display features from the given feature source by matching unique values of the specified feature attribute to colours
	 * 
	 * @param reader
	 *            the feature source
	 * @return a new Style instance
	 * @throws Exception
	 */

	public class ClassStyleDef {
		
		public static enum ClassType{
			RANGE,
			SINGLE_VALUE;
		}
		
		public ClassStyleDef() {
			super();
			// TODO Auto-generated constructor stub
		}

		/**
		 * @uml.property name="from"
		 */
		private Object from = 0;
		/**
		 * @uml.property name="to"
		 */
		private Object to = 0;

		
		private ClassType type=ClassType.RANGE;
		
		public ClassStyleDef(Object from, Object to) {
			super();
			this.from = from;
			this.to = to;
			this.type=ClassType.RANGE;
		}

		public ClassStyleDef(Object value){
			this.from=value;
			this.type=ClassType.SINGLE_VALUE;
		}
		
		public ClassType getType() {
			return type;
		}
		
		/**
		 * @return
		 * @uml.property name="from"
		 */
		public Object getFrom() {
			return from;
		}

		/**
		 * @return
		 * @uml.property name="to"
		 */
		public Object getTo() {
			return to;
		}

		/**
		 * @param from
		 * @uml.property name="from"
		 */
		public void setFrom(Object from) {
			this.from = from;
		}

		/**
		 * @param to
		 * @uml.property name="to"
		 */
		public void setTo(Object to) {
			this.to = to;
		}
	}