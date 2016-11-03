/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.shared;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public enum OccurrencesSaveEnum {

		STANDARD("standard"), 
		OPENMODELLER("openModeller");
		
		protected String label;
		
		OccurrencesSaveEnum(String label)
		{
			this.label = label;
		}

		/**
		 * @return the label
		 */
		public String getLabel() {
			return label;
		}

		public static List<String> getListLabels(){
			
			List<String> listLabels = new ArrayList<String>();
			
			for (OccurrencesSaveEnum item : OccurrencesSaveEnum.values()) 
				listLabels.add(item.getLabel());
			
			return listLabels;
		}
}
