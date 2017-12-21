package org.gcube.data.analysis.tabulardata.mobile.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EntityList {

	private static List<String> get(List<String> listoToIterate, String patternToRemove) {
		if (listoToIterate.isEmpty()) return Collections.emptyList();
		List<String> returnList = new ArrayList<String>();
		Pattern pattern= Pattern.compile(patternToRemove);
		for (String val : listoToIterate ){
			Matcher m = pattern.matcher(val);
			if (m.matches())
				returnList.add(m.group(1));
		}
		return returnList;
	}

	public static List<String> getUserList(List<String> entities){
		return get(entities, "u\\(([^\\)]+)\\)");
	}
	
	public static List<String> getGroupList(List<String> entities){
		return get(entities, "g\\(([^\\)]+)\\)");
	}
}
