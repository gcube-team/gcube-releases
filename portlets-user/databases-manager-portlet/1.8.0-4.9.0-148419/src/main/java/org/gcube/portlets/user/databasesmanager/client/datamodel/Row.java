package org.gcube.portlets.user.databasesmanager.client.datamodel;

import java.io.Serializable;
import java.util.List;
//import java.util.logging.Logger;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class Row extends BaseModelData implements Serializable {

	private static final long serialVersionUID = 1L;

	// private static int ID = 0;
	// private static Logger rootLogger = Logger.getLogger("Row");

	public Row() {
	}

	public Row(List<String> attributes, List<String> values, int index)
			throws Exception {
		set("index", index);
		if (attributes != null && !attributes.isEmpty() && values != null
				&& !values.isEmpty()){
			int limit;
			if(attributes.size()>=values.size()){
				limit=values.size();
			} else {
				limit=attributes.size();
			}
			for (int i = 0; i < limit; i++) {
				set(attributes.get(i), values.get(i));
			}
		}
	}

	public String getValue(String attribute) {
		return get(attribute);
	}

	// private static List<String> parseCVSString(String row) throws Exception {
	//
	// String delimiter = ",";
	//
	// List<String> elements = new ArrayList<String>();
	// String phrase = row;
	// int idxdelim = -1;
	// boolean quot = false;
	// phrase = phrase.trim();
	// while ((idxdelim = phrase.indexOf(delimiter)) >= 0) {
	// quot = phrase.startsWith("\"");
	// if (quot) {
	// phrase = phrase.substring(1);
	// String quoted = "";
	// if (phrase.startsWith("\""))
	// phrase = phrase.substring(1);
	// else{
	// RE regexp = new RE("[^\\\\]\"");
	// boolean matching = regexp.match(phrase);
	//
	// if (matching) {
	// int i0 = regexp.getParenStart(0);
	// quoted = phrase.substring(0, i0 + 1).trim();
	// phrase = phrase.substring(i0 + 2).trim();
	// }
	// }
	//
	// if (phrase.startsWith(delimiter))
	// phrase = phrase.substring(1);
	//
	// elements.add(quoted);
	//
	// } else {
	// elements.add(phrase.substring(0, idxdelim));
	// phrase = phrase.substring(idxdelim + 1).trim();
	// }
	// }
	// if (phrase.startsWith("\""))
	// phrase = phrase.substring(1);
	//
	// if (phrase.endsWith("\""))
	// phrase = phrase.substring(0, phrase.length() - 1);
	//
	// elements.add(phrase);
	//
	// return elements;
	// }
}