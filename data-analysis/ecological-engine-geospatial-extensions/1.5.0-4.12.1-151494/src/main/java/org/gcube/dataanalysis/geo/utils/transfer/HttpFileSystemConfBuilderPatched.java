package org.gcube.dataanalysis.geo.utils.transfer;


import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.http.HttpFileSystemConfigBuilder;


/**
 * 
 * Patched version declaring timeout
 * @author Andrea
 *
 */
public final class HttpFileSystemConfBuilderPatched extends HttpFileSystemConfigBuilder {
	
	private static final HttpFileSystemConfBuilderPatched BUILDER = new HttpFileSystemConfBuilderPatched();

	protected HttpFileSystemConfBuilderPatched(String prefix) {
		super("http.");
	}
	
	protected HttpFileSystemConfBuilderPatched() {
		super("http.");
	}

	 public static HttpFileSystemConfBuilderPatched getInstance()
	    {
	        return BUILDER;
	    }
	 

	
	public void setTimeout(FileSystemOptions opts, int timeout)
    {
       setParam(opts, "http.socket.timeout", timeout);	
       
    }    
	
	public int getTimeout(FileSystemOptions opts){
		return getInteger(opts, "http.socket.timeout");
	}
	
	
}
