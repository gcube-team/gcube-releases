package gr.uoa.di.madgik.hive.representation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TableFieldsAssociation {
	private Map<String, Set<String>> fieldsToTableMap = new HashMap<String, Set<String>>();

	public void asscociate(String field, String table) {
		if (fieldsToTableMap.containsKey(field)) {
			fieldsToTableMap.get(field).add(table);
		} else {
			Set<String> set = new HashSet<String>();
			set.add(table);
			fieldsToTableMap.put(field, set);
		}
	}
	
	public boolean isUnique(String field, Set<String> tables) {
		int cnt = 0;
		for (String table : tables) {
			if(fieldsToTableMap.get(field).contains(table))
				cnt++;
		}
		return cnt == 1;
	}
	
	public void removeTableAssociations(String table) {
		for (Entry<String, Set<String>> e :fieldsToTableMap.entrySet()) {
			if (e.getValue().remove(table));
		}
	}
}
