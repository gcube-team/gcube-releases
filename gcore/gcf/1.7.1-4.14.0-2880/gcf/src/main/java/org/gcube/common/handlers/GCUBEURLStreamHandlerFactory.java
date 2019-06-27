/**
 * 
 */
package org.gcube.common.handlers;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.StringTokenizer;

/**
 * @author Fabio Simeoni (University of Strathclyde)
 *
 */
public class GCUBEURLStreamHandlerFactory implements URLStreamHandlerFactory {

	
	//private static GCUBELog logger  = new GCUBELog(GCUBEURLStreamHandlerFactory.class);
    /**
     * The property which specifies the package prefix list to be scanned
     * for protocol handlers.  The value of this property (if any) should
     * be a vertical bar delimited list of package names to search through
     * for a protocol handler to load.  The policy of this class is that
     * all protocol handlers will be in a class called <protocolname>.Handler,
     * and each package in the list is examined in turn for a matching
     * handler.  If none are found (or the property is not specified), the
     * default package prefix, sun.net.www.protocol, is used.  The search
     * proceeds from the first package in the list to the last and stops
     * when a match is found.
     */
	 private static final String protocolPathProp = "java.protocol.handler.pkgs";
	 
	 
	/**{@inheritDoc}*/
	public URLStreamHandler createURLStreamHandler(String protocol) {
		
		String packagePrefixList = System.getProperty(protocolPathProp);
	
		URLStreamHandler handler =null;

		if (packagePrefixList!=null) {
			StringTokenizer packagePrefixIter = new StringTokenizer(packagePrefixList, "|");
			while (handler == null && packagePrefixIter.hasMoreTokens()) {
			
				String packagePrefix = packagePrefixIter.nextToken().trim();
				try {
					String clsName = packagePrefix + "." + protocol +".Handler";
					Class<?> cls = null;
					try {
						cls = Class.forName(clsName);
		             } catch (ClassNotFoundException e) {
					    ClassLoader cl = ClassLoader.getSystemClassLoader();
					    if (cl != null) {
					        cls = cl.loadClass(clsName);
					    }
					}
					if (cls != null) {
					    handler  =  (URLStreamHandler)cls.newInstance();
					}
				    } catch (Exception e) {
				    	//too verbose to expose
				    	//logger.warn("could not resolve "+protocol+" ("+e.getClass().getSimpleName()+"), hopefully is a standard one");
				    }
			}
		}
		
		return handler;

	}

}
