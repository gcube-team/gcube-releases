/**
 * 
 */
package org.gcube.portlets.user.td.taskswidget.shared.job;

import java.util.ArrayList;
import java.util.List;

/**
 * The Enum TdJobClassifierType.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 29, 2015
 */
public enum TdJobClassifierType {

		CLASSIFIER_UNKNOWN("Classifier_Unknown"), 
		PROCESSING("Processing"),
		DATAVALIDATION("DataValidation"), //UPDATED FROM VALIDATIOno
		PREPROCESSING("Pre-Processing"), 
		POSTPROCESSING("Post-Processing"), 
		FALLBACK("Fallback");
		
		protected String label;
		
		/**
		 * Instantiates a new td job classifier type.
		 *
		 * @param label the label
		 */
		TdJobClassifierType(String label)
		{
			this.label = label;
		}

		/**
		 * Gets the label.
		 *
		 * @return the label
		 */
		public String getLabel() {
			return label;
		}

		/**
		 * Gets the list labels.
		 *
		 * @return the list labels
		 */
		public static List<String> getListLabels(){
			
			List<String> listLabels = new ArrayList<String>();
			
			for (TdJobClassifierType item : TdJobClassifierType.values()) 
				listLabels.add(item.getLabel());
			
			return listLabels;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return this.getLabel();
		}
}
