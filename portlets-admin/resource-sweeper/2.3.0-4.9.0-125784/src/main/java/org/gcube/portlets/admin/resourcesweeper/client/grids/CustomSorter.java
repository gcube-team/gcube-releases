package org.gcube.portlets.admin.resourcesweeper.client.grids;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.google.gwt.core.client.GWT;

public class CustomSorter extends StoreSorter<ModelData> {
	/**
	 * need to recalculate the percentage as the returned value is not what the render returns
	 */
	@Override
	public int compare(Store<ModelData> store, ModelData m1, ModelData m2, String property) {
		if (property != null) {
			if (property.equals("VirtualAvailable")) {
				String s1 = m1.get(property);
				String s2 = m2.get(property);
				int val1 = Integer.parseInt(s1);
				int val2 = Integer.parseInt(s2);

				int tot1 = Integer.parseInt((String) m1.get("VirtualSize"));
				int tot2 = Integer.parseInt((String) m2.get("VirtualSize"));
				
				int percentage1 = (val1 * 100) / tot1; 
				int percentage2 = (val2 * 100) / tot2; 

				//GWT.log(val1 +"-"+val2);
				GWT.log(""+percentage1);
				if (percentage1 == percentage2) return 0;
				if (percentage1 > percentage2)
					return 1;
				else
					return -1;
			}
			else if (property.equals("LocalAvailableSpace")) {
				String s1 = m1.get(property);
				String s2 = m2.get(property);
				int val1 = Integer.parseInt(s1);
				int val2 = Integer.parseInt(s2);
	
				if (val1 == val2) return 0;
				if (val1 > val2)
					return 1;
				else
					return -1;
			}
			else if (property.equals("NumberOfMembers") || property.equals("Cardinality")) {
				String s1 = m1.get(property);
				String s2 = m2.get(property);
				int val1 = Integer.parseInt(s1);
				int val2 = Integer.parseInt(s2);
	
				if (val1 == val2) return 0;
				if (val1 > val2)
					return 1;
				else
					return -1;
			}
			Object v1 = m1.get(property);
			Object v2 = m2.get(property);
			return comparator.compare(v1, v2);
		}
		return comparator.compare(m1, m2);
	}
}
