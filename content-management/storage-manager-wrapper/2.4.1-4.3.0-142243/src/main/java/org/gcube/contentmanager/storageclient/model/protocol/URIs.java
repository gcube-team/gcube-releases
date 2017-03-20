package org.gcube.contentmanager.storageclient.model.protocol;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.common.core.scope.GCUBEScopeManager;
import org.gcube.contentmanager.storageclient.model.protocol.smp.Handler;

/**
 * Utility methods for content URI creation and manipulation.
 * @author Fabio Simeoni (University of Strathclyde)
 * @author Roberto Cirillo (ISTI-CNR)
 *
 */
public class URIs {

	static {
		Handler.activateProtocol();
	}
	
	/**Scheme of cms URIs.*/
	public static final String PROTOCOL="smp";
	
	/**
	 * Indicates whether a URI is a valid content URI.
	 * @param uri the URI.
	 * @throws URISyntaxException if the URI fails validation.
	 */
	public static void validate(URI uri) throws URISyntaxException {
		if (!PROTOCOL.equals(uri.getScheme()) || 
				uri.getAuthority()==null || 
					uri.getPath()==null ||
						uri.getPath().length()<2)
			throw new IllegalArgumentException(new URISyntaxException(uri.toString(),"uri is not a well-formed content URI"));
	}

	/**
	 * Constructs a content URI from a collection identifiers and one or more node identifiers.
	 * @param collectionID the collection identifier.
	 * @param identifiers the node identifiers.
	 * @return the URI.
	 * @throws IllegalArgumentException if the input is <code>null</code> or empty.
	 */
	public static URI make(String collectionID, String ... identifiers) throws IllegalArgumentException {
		
		if (collectionID==null || identifiers==null || identifiers.length==0)
			throw new IllegalArgumentException("null or empty input");
		
		StringBuilder path = new StringBuilder();
		for (Object id : identifiers) 
			path.append("/"+id.toString());

		URI uri = null;
		try {
			uri = new URI(PROTOCOL,collectionID,path.toString(),null);
		}
		catch(URISyntaxException e) {
			throw new RuntimeException("error in generation uri with "+PROTOCOL+","+collectionID+","+path,e);
		}
		return uri;
	}
	
	/**
	 * Returns the collection identifier in a <code>sm</code> URI.
	 * @param uri the URI.
	 * @return the identifier.
	 * @throws URISyntaxException if the URI is not a content URI.
	 */
	public static String collectionID(URI uri) throws URISyntaxException {
		validate(uri);
		return uri.getAuthority();
	}
	
	/**
	 * Returns the document identifier in a content URI.
	 * @param uri the URI.
	 * @return the identifier.
	 * @throws URISyntaxException if the URI is not a content URI.
	 */
	public static String documentID(URI uri) throws URISyntaxException {
		validate(uri);
		String p = uri.getPath().substring(1);
		if (p.endsWith("/")) 
			p = p.substring(0,p.length()-1);
		int index = p.indexOf("/");
		return p.substring(0,index>0?index:p.length());
	}
	
	/**
	 * Returns the identifier of the node identified by a content URI.
	 * @param uri the URI.
	 * @return the identifier.
	 * @throws URISyntaxException if the URI is not a content URI.
	 */
	public static String nodeID(URI uri) throws URISyntaxException {
		validate(uri);
		return uri.getPath().substring(uri.getPath().lastIndexOf("/")+1);
	}
	
	/**
	 * Returns the identifiers in a content URI.
	 * @param uri the URI.
	 * @return the identifiers.
	 * @throws URISyntaxException if the URI is not a content URI.
	 */
	public static String[] nodeIDs(URI uri) throws URISyntaxException {
		validate(uri);
		List<String> ids = new ArrayList<String>();
		for (String s : uri.getPath().substring(1).split("/")) //will be validated here
			ids.add(s);
		return ids.toArray(new String[0]);
	}
	
	/**
	 * Returns a content URI for the parent of the node identified by another content URI.
	 * @param uri the input URI.
	 * @return the parent URI.
	 * @throws URISyntaxException if the input URI is not a content URL.
	 */
	public static URI parentURI(URI uri) throws URISyntaxException {
		validate(uri);
		String u = uri.getPath();
		return	make(uri.getAuthority(),u.substring(1,u.lastIndexOf("/")).split("/"));
	}
	
	/**
	 * Returns a content URI for the document of the node identified by another content URI.
	 * @param uri the input URI.
	 * @return the document URI.
	 * @throws URISyntaxException if the input URI is not a content URI.
	 */
	public static URI documentURI(URI uri) throws URISyntaxException {
		validate(uri);
		return make(uri.getAuthority(),documentID(uri));
	}
	
	/**
	 * Returns a URL connection in a given scope.
	 * @param uri a content URI.
	 * @param scope the scope.
	 * @return the connection.
	 * @throws IOException if the connections could not be established.
	 * @throws URISyntaxException if the URI is not a content URI or if the protocol handler for the <code>smp</code> scheme is not active.
	 * @deprecated since 2.3.1. Use {@link URLConnection} normally in current scope.
	 */
	public static URLConnection connection(URI uri, String scope) throws IOException, URISyntaxException {
		validate(uri);
		URLConnection connection = uri.toURL().openConnection();
		return connection;
	}
}