/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.autoComplete;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class JobAutoCompleteData implements IsSerializable {
	private List<JobAutoCompleteEntryDesc> autoCompleteEntries;
	private Map<String, ObjectDesc> autoCompleteObjectStore;
	
	public JobAutoCompleteData() { }
	
	public JobAutoCompleteData(List<JobAutoCompleteEntryDesc> autoCompleteEntries, Map<String, ObjectDesc> autoCompleteObjectStore) {
		this.autoCompleteEntries = autoCompleteEntries;
		this.autoCompleteObjectStore = autoCompleteObjectStore;
	}
	
	public List<JobAutoCompleteEntryDesc> getAutoCompleteEntries() {
		return this.autoCompleteEntries;
	}
	
	public Map<String, ObjectDesc> getAutoCompleteObjectStore() {
		return this.autoCompleteObjectStore;
	}
}
