/**
 * 
 */
package org.gcube.informationsystem.cache;

import java.util.LinkedList;
import java.util.List;

/**
 * @author paul
 * 
 */
public class ScopesUtil {

	/**
	 * Retrieves all parent scopes without the original scope. Scopes are paths
	 * within tree-like constructs, e.g. /A/B/C.
	 * 
	 * @param scope
	 *            gcube scope
	 * @return all parent scopes without the original scope
	 */
	public static String[] enumerateParentScopes(String scope) {
		List<String> all = new LinkedList<String>();
		String[] toks = scope.substring(1).split("/");

		for(int i=toks.length;i>0;i--) {
			String base = "/" + toks[0];
			for(int j=1;j<i;j++)
				base = base + "/" + toks[j];
			all.add(base);
		}
		return all.toArray(new String[0]);
	}

	/**
	 * Main
	 * @param args main arguments
	 */
	public static void main(String[] args) {
		enumerateParentScopes("/gcube/devsec/sss/ddd");
	}

}
