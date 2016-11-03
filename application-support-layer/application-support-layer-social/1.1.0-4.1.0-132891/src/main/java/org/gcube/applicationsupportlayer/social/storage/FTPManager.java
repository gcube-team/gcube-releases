package org.gcube.applicationsupportlayer.social.storage;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.databook.shared.ImageType;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 *
 */
public class FTPManager {
	
	private static final Logger _log = LoggerFactory.getLogger(FTPManager.class);

	/**
	 * the FTP Server RuntimeResource coordinates
	 */
	private static String RUNTIME_RESOURCE_NAME = "SocialPortalStorage";
	private static String CATEGORY_NAME = "FTPServer";
	private static String UPLOAD_FOLDER_NAME = "previews";
	
	private static FTPManager singleton;
	
	private ServiceEndpoint endPoint;
	
	private FTPManager() {
		try {
			this.endPoint = getConfigurationFromIS();
			singleton = this;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static FTPManager getInstance() {
		if (singleton == null)
			singleton = new FTPManager();
		return singleton;
	}
	/**
	 * 
	 * @param previewThumbnailInputStream .
	 * @param imageExtension .
	 * @return the http url of the image uploaded on the ftp server
	 */
	public String uploadImageOnFTPServer(InputStream previewThumbnailInputStream, ImageType imageExtension) {
		FTPClient client = new FTPClient( );
		InputStream inputStream = previewThumbnailInputStream;
				
		String ftpUrl = "";
		String user = "";
		String pwd = "";
		String httpBaseURL = "";
		String fileName = UPLOAD_FOLDER_NAME + "/" + UUID.randomUUID() + "." + imageExtension.toString().toLowerCase();
		try {	
			ServiceEndpoint res = endPoint;
			AccessPoint ac = res.profile().accessPoints().iterator().next();
			ftpUrl = ac.address();
			httpBaseURL =  res.profile().runtime().hostedOn();
			user = ac.username();
			

			//set the scope
			String currScope = ScopeProvider.instance.get();
			ScopeProvider.instance.set("/"+PortalContext.getConfiguration().getInfrastructureName());
			pwd = StringEncrypter.getEncrypter().decrypt(ac.password());
			ScopeProvider.instance.set(currScope);
		
			// Connect to the FTP server 
		    client.connect(ftpUrl);
		    client.login(user, pwd);

		    client.setFileType(FTP.BINARY_FILE_TYPE);
		    client.enterLocalPassiveMode();
	    
		    BufferedInputStream bis = new BufferedInputStream(inputStream);		    
		    client.storeFile(fileName, bis);
		    bis.close();
	        client.logout();
		} catch(IOException ioe) {
			ioe.printStackTrace();
			_log.error( "Error communicating with FTP server." );
		} catch (Exception e) {
			_log.error( "Probably sth wrong in fetching FTP Server RuntimeResource from IS" );
			e.printStackTrace();
		} finally {
		    IOUtils.closeQuietly( inputStream );
		    try {
		        client.disconnect( );
		    } catch (IOException e) {
		    	_log.error( "Problem disconnecting from FTP server" );
		    }
		}
		StringBuilder sb = new StringBuilder().append(httpBaseURL).append(fileName);
        _log.info( "Uploaded file FTP server: http url: " + sb );
		return sb.toString();
	}	
	
	public String getBaseURL() throws Exception {
		String httpBaseURL =  endPoint.profile().runtime().hostedOn();
		if (httpBaseURL != null) {
			return httpBaseURL+UPLOAD_FOLDER_NAME+"/";
		}
		else throw new ServiceConfigurationError("Could not find a valid FTP Server in the infrastructure");
	}
	/**
	 * 
	 * @return the runtime resource of the FTP Server node
	 * @throws Exception
	 */
	private ServiceEndpoint getConfigurationFromIS() throws Exception  {
		
		//set the scope
		String currScope = ScopeProvider.instance.get();

		ScopeProvider.instance.set("/"+PortalContext.getConfiguration().getInfrastructureName());
	
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Name/text() eq '"+ RUNTIME_RESOURCE_NAME +"'");
		query.addCondition("$resource/Profile/Category/text() eq '"+ CATEGORY_NAME +"'");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> conf = client.submit(query);
		ScopeProvider.instance.set(currScope);		
		return conf.get(0);
	}	
}
