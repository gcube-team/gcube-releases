/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.server;

import org.gcube.portlets.user.tdtemplate.server.session.CacheServerExpressions;
import org.gcube.portlets.user.tdtemplate.shared.TdTemplateUpdater;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 28, 2014
 *
 */
public class TemplateUpdaterForDescription {
	
	private TdTemplateUpdater tdUpdater;
	private CacheServerExpressions cache;

	
	/**
	 * @param tdUpdater
	 * @param cache
	 */
	public TemplateUpdaterForDescription(TdTemplateUpdater tdUpdater, CacheServerExpressions cache) {
		this.tdUpdater = tdUpdater;
		this.cache = cache;
	}
	
	public TdTemplateUpdater getTdUpdater() {
		return tdUpdater;
	}

	public CacheServerExpressions getCache() {
		return cache;
	}


	public void setTdUpdater(TdTemplateUpdater tdUpdater) {
		this.tdUpdater = tdUpdater;
	}


	public void setCache(CacheServerExpressions cache) {
		this.cache = cache;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TemplateUpdaterForDescription [tdUpdater=");
		builder.append(tdUpdater);
		builder.append(", cache=");
		builder.append(cache);
		builder.append("]");
		return builder.toString();
	}
}
