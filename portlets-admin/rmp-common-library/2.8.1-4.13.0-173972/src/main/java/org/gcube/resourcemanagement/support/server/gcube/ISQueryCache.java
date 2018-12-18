/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: ISQueryCache.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.server.gcube;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ISQueryCacheKeyT {
	String keyValue = null;
	String queryExpression = null;

	public ISQueryCacheKeyT(String scope, String queryExpression, String... params) {
		if (scope != null && queryExpression != null && params != null && params.length > 0) {
			this.queryExpression = queryExpression.trim();
			this.keyValue = scope + "*" + queryExpression + "*" + "[";
			for (String entry : params) {
				keyValue += "(" + entry + ")";
			}
			this.keyValue += "]";
		}
	}
	
	public String getQueryExpression() {
		return this.queryExpression;
	}

	@Override
	public String toString() {
		return this.keyValue;
	}

	@Override
	public boolean equals(Object obj) {
		if (this.keyValue == null) {
			return false;
		}
		if (obj instanceof ISQueryCacheKeyT) {
			return this.keyValue.equals(((ISQueryCacheKeyT) obj).keyValue);
		}
		return super.equals(obj);
	}

	public int hashCode() {
		return this.keyValue.hashCode();
	}
}

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class ISQueryCache {
	Map<ISQueryCacheKeyT, List<String>> results = new HashMap<ISQueryCacheKeyT, List<String>>();

	public void insert(ISQueryCacheKeyT key, List<String> elem) {
		this.results.put(key, elem);
	}

	public boolean contains(ISQueryCacheKeyT key) {
		if (key == null) {
			return false;
		}
		return this.results.containsKey(key);
	}
	
	public List<String> get(ISQueryCacheKeyT key) {
		if (key != null && this.results.containsKey(key)) {
			return this.results.get(key);
		}
		return null;
	}
	
	public void empty() {
		this.results.clear();
	}
}
