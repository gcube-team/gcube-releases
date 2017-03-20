
package org.apache.commons.vfs2.provider;


import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.provider.AbstractFileName;
import org.apache.commons.vfs2.provider.AbstractFileObject;
import org.apache.commons.vfs2.provider.DefaultFileContent;
import org.apache.commons.vfs2.provider.UriParser;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.model.protocol.smp.Handler;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;

/**
 * An Smp file.
 *
 * @author <a href="http://commons.apache.org/vfs/team-list.html">Commons VFS team</a>
 */
public class SmpFileObject extends AbstractFileObject
{
   private final Log log = LogFactory.getLog(SmpFileObject.class);
    private final SmpFileSystem smpFs;
    private final String relPath;
    private final String wholeLink;

    protected SmpFileObject(final AbstractFileName name,
                            final SmpFileSystem fileSystem,
                            final FileName rootName)
        throws FileSystemException
    {
        super(name, fileSystem);
        
        smpFs = fileSystem;
        
        String relPath = UriParser.decode(rootName.getRelativeName(name));
        //modification by Gianpaolo Coro to account for latest smp url formats
        wholeLink=name.toString();
        
        /*
        if(relPath.contains("?")){
        wholeLink=rootName.getURI()+relPath;
        }
        else{ // it means that the filename is the first part which was supposed to be the hostname
        	 wholeLink=rootName.getURI().substring(0,rootName.getURI().length()-1)+"?"+relPath;
        }
        */
        if (".".equals(relPath))
        {
            this.relPath = null;
        }
        else
        {
            this.relPath = relPath;
        }      
        
        try {
			doGetContentSize();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    // override the getURL function
    // enable the smp protocol and return the url
    @Override
    public URL getURL() throws FileSystemException
    {
    	Handler.activateProtocol();
    	URL returnedUrl=null;       
        try {
			returnedUrl=new URL(wholeLink);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
        return returnedUrl;
    }
    
	@Override
	protected FileType doGetType() throws Exception {
		// TODO Auto-generated method stub
		return FileType.FILE;
	}

		
	@Override
	protected String[] doListChildren() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected long doGetContentSize() throws Exception {
		//substitution with new code using URLs
		URLConnection conn = null;
	    try {
	    	log.debug("doGetContentSize - connecting to "+wholeLink);
	        conn = new URL(wholeLink).openConnection();
	        conn.getInputStream();
	        long len = conn.getContentLength();
	        log.debug("doGetContentSize - content length "+len);
	        return len;
	    } catch (Exception e) {
	        return -1;
	    } finally {
	        if (conn != null)
	        	conn.getInputStream().close();
	    }
		/*
		try{
		String url =wholeLink;
		log.debug("(doGetContentSize) whole link="+wholeLink);
		System.out.println("(doGetContentSize) whole link="+wholeLink);
		//String[] parts = url.split("\\?");
		String smpid = wholeLink.substring(wholeLink.lastIndexOf('/')+1);
		System.out.println("decripting ="+smpid);
		DecryptSmpUrl.decrypt(smpid);
		//new client (there is need to set the scope)
		ScopeProvider.instance.set(DecryptSmpUrl.scopeType);
		IClient client = new StorageClient(DecryptSmpUrl.serviceClass, DecryptSmpUrl.serviceName, DecryptSmpUrl.owner, AccessType.valueOf(DecryptSmpUrl.accessType.toUpperCase())).getClient();
		
//		String ur= parts[0].replaceFirst("smp://", "");
		String ur= url;
		System.out.println("(doGetContentSize) path="+ur);
		
		long size = client.getSize().RFile(ur);
		System.out.println("(doGetContentSize) size="+size);
		return size;
		}
		catch(Exception e){
			e.printStackTrace();
			return -1;
		}
		*/
	}
	
	public void sleepFiveSec(){
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	@Override
	protected FileContent doCreateFileContent() throws FileSystemException
    {
        return new DefaultFileContent(this, getFileContentInfoFactory());
    }

	@Override
	protected InputStream doGetInputStream() throws Exception {
		
	    Handler.activateProtocol();
		URL returnedUrl = null;
		try {
			returnedUrl = new URL(wholeLink);

		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		URLConnection uc = null;
		InputStream in = null;
		try {
			uc = ( URLConnection ) returnedUrl.openConnection ( );
			in = uc.getInputStream();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return in;
	}
	
	@Override
	protected OutputStream doGetOutputStream(boolean append) throws Exception {
		
        Handler.activateProtocol();
		URL returnedUrl = null;
		try {
			returnedUrl = new URL(wholeLink);

		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		URLConnection uc = null;
		OutputStream out = null;
		try {
			uc = ( URLConnection ) returnedUrl.openConnection ( );
			out = uc.getOutputStream();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return out;
	}

	
}
