package org.gcube.contentmanager.storageclient.model.protocol.smp;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLConnection;
import org.gcube.contentmanager.storageclient.model.protocol.smp.SMPURLConnectionFactory;

/**
 * Allow to the end user to manage a smp url as an url with a configured stream handler
 * @author Roberto Cirillo (ISTI-CNR)
 *
 */
@Deprecated
public class SMPUrl extends java.net.URLStreamHandler{
	
	private java.net.URL url;

	/**
	 * map a url string as a normal url
	 * @param u url string
	 * @throws MalformedURLException
	 */
	public SMPUrl(String u) throws MalformedURLException{
		this.url=new java.net.URL(null, u, this);
	}
	
	public URLConnection openConnection() throws IOException{
		return this.openConnection(url);
	}
	
	@Override
	protected URLConnection openConnection(java.net.URL u) throws IOException {
		if(u.getProtocol().equalsIgnoreCase("smp")){
    	  return SMPURLConnectionFactory.getSmp(u);
		}else{
			return new java.net.URL(null, u.toString()).openConnection();
		}
			
    }
	
	public String getHost(){
		return this.url.getHost();
	}
	
    /**
     * Gets the path part of this <code>URL</code>.
     *
     * @return  the path part of this <code>URL</code>, or an
     * empty string if one does not exist
     * @since 1.3
     */
    public String getPath() {
        return url.getPath();
    }

    /**
     * Gets the userInfo part of this <code>URL</code>.
     *
     * @return  the userInfo part of this <code>URL</code>, or
     * <CODE>null</CODE> if one does not exist
     * @since 1.3
     */
    public String getUserInfo() {
        return url.getUserInfo();
    }

    /**
     * Gets the authority part of this <code>URL</code>.
     *
     * @return  the authority part of this <code>URL</code>
     * @since 1.3
     */
    public String getAuthority() {
        return url.getAuthority();
    }

    /**
     * Gets the port number of this <code>URL</code>.
     *
     * @return  the port number, or -1 if the port is not set
     */
    public int getPort() {
        return url.getPort();
    }

    /**
     * Gets the default port number of the protocol associated
     * with this <code>URL</code>. If the URL scheme or the URLStreamHandler
     * for the URL do not define a default port number,
     * then -1 is returned.
     *
     * @return  the port number
     * @since 1.4
     */
    public int getDefaultPort() {
        return url.getDefaultPort();
    }

    /**
     * Gets the protocol name of this <code>URL</code>.
     *
     * @return  the protocol of this <code>URL</code>.
     */
    public String getProtocol() {
        return url.getProtocol();
    }

 

    /**
     * Gets the file name of this <code>URL</code>.
     * The returned file portion will be
     * the same as <CODE>getPath()</CODE>, plus the concatenation of
     * the value of <CODE>getQuery()</CODE>, if any. If there is
     * no query portion, this method and <CODE>getPath()</CODE> will
     * return identical results.
     *
     * @return  the file name of this <code>URL</code>,
     * or an empty string if one does not exist
     */
    public String getFile() {
        return url.getFile();
    }

    /**
     * Gets the anchor (also known as the "reference") of this
     * <code>URL</code>.
     *
     * @return  the anchor (also known as the "reference") of this
     *          <code>URL</code>, or <CODE>null</CODE> if one does not exist
     */
    public String getRef() {
        return url.getRef();
    }


}
