package org.gcube.application.framework.core.security;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import org.gcube.application.framework.core.cache.CachesManager;
import org.gcube.application.framework.core.genericresources.model.ISGenericResource;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.util.CacheEntryConstants;
import org.gcube.application.framework.core.util.QueryString;
//import org.gcube.soa3.connector.common.security.CredentialManager;
import org.kxml2.io.KXmlParser;
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.common.core.security.GCUBESecurityManagerImpl;
//import org.gcube.common.core.utils.logging.GCUBELog;
import org.kxml2.io.KXmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Valia Tsagkalidou (KNUA)
 *
 */

public class PortalSecurityManager {//extends GCUBESecurityManagerImpl {

	/** Object logger. */
//	protected final GCUBELog logger = new GCUBELog(this);
	private static Logger logger = LoggerFactory.getLogger(PortalSecurityManager.class); 
	
	public PortalSecurityManager(String scope) {
		super();
		this.scope = scope;
	}

	public PortalSecurityManager(ASLSession session) {
		super();
		this.scope = session.getScope();
	}

//	GCUBEScope scope;
	String scope;
	
	public boolean isSecurityEnabled() {
		QueryString query = new QueryString();
		query.put(CacheEntryConstants.vreResource, "true");
		query.put(CacheEntryConstants.vre, scope.toString());
		List<ISGenericResource> res = (List<ISGenericResource>)CachesManager.getInstance().getGenericResourceCache().get(query).getValue();
		if(res == null || res.size() == 0)
			return false;
		else
		{
			try {
				return parseBody(res.get(0).getBody());
			} catch (Exception e) {
				logger.error("",e);
				return false;
			}
		}
	}

	/**
	     * Loads from the <em>Body</em> element the resource information
	     * @param body the <em>Body</em> of the generic resource
	     * @throws Exception if the element is not valid or well formed
	     */
	    private boolean parseBody(String body) throws Exception {
	        KXmlParser parser = new KXmlParser();
	        parser.setInput(new BufferedReader(new StringReader(body)));        
	        loop: while (true) {
	            try {
	                switch (parser.next()) {
	                case KXmlParser.START_TAG:
	                	if (parser.getName().equals("SecurityEnabled"))
	                	{
	                		boolean res = Boolean.valueOf(parser.nextText()).booleanValue();
	        				logger.debug("Found value:" + res);
	                		return res;
	                	}
	                    else parser.nextText();//just skip the text
	                    break;
	                case KXmlParser.END_DOCUMENT: break loop;
	                }                
	            } catch (Exception e) {
					logger.error("",e);
	                throw new Exception ("Unable to parse the ScopeResource body");
	            }
	        }
	        return false;
	    }

}
