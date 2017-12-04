/**
 * 
 */
package org.gcube.portlets.user.tdw.client.util;

import org.gcube.portlets.user.tdw.shared.ServletParameters;

import com.sencha.gxt.data.shared.SortInfo;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.writer.DataWriter;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class PagingLoadUrlEncoder implements DataWriter<PagingLoadConfig, String> {
	
	protected UrlBuilder urlBuilder = new UrlBuilder();

	
	public String write(PagingLoadConfig config) {
		
		System.out.println("Offset: "+config.getOffset());
		System.out.println("Limit: "+config.getLimit());
		
		urlBuilder.clear();
		
		urlBuilder.addParameter(ServletParameters.OFFSET, String.valueOf(config.getOffset()));
		urlBuilder.addParameter(ServletParameters.LIMIT, String.valueOf(config.getLimit()));
				
		for (SortInfo info:config.getSortInfo()) {
			
			System.out.println("SORTFIELD: "+info.getSortField());
			System.out.println("SORTDIR: "+info.getSortDir());
			urlBuilder.addParameter(ServletParameters.SORTFIELD, info.getSortField());
			urlBuilder.addParameter(ServletParameters.SORTDIR, (info.getSortDir()==null)?null:info.getSortDir().toString());
		}

		return urlBuilder.toString();
	}

}
