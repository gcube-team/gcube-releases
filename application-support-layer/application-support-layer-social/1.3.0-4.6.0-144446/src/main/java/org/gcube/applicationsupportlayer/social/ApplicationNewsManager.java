package org.gcube.applicationsupportlayer.social;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingUser;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.databook.shared.Feed;
import org.gcube.portal.databook.shared.FeedType;
import org.gcube.portal.databook.shared.ImageType;
import org.gcube.portal.databook.shared.PrivacyLevel;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 *
 * use to share updates from within your applicationProfile, the update will be published in the Users News Feed belonging to the VRE your applicationProfile runs into 
 */
public class ApplicationNewsManager extends SocialPortalBridge implements NewsManager  {
	
	private static final Logger _log = LoggerFactory.getLogger(ApplicationNewsManager.class);

	/**
	 * the FTP Server RuntimeResource coordinates
	 */
	private static String RUNTIME_RESOURCE_NAME = "SocialPortalStorage";
	private static String CATEGORY_NAME = "FTPServer";
	/**
	 * 
	 * @param scope the current scope
	 * @param currUser an instance of {@link SocialNetworkingUser} filled with the required user data
	 * @param portletClassName your portlet class name will be used ad unique identifier for your applicationProfile
	 */
	public ApplicationNewsManager(String scope, SocialNetworkingUser currUser, String portletClassName) {
		super(scope, currUser, portletClassName);
	}	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean shareApplicationUpdate(String feedText) {
		return getStoreInstance().saveAppFeed(buildFeed(feedText, "", "", "", ""));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean shareApplicationUpdate(String feedText, String uriParams) {
		return getStoreInstance().saveAppFeed(buildFeed(feedText, uriParams, "", "", ""));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean shareApplicationUpdate(String feedText, String uriParams, String previewTitle, String previewDescription, String previewThumbnailUrl) {
		return getStoreInstance().saveAppFeed(buildFeed(feedText, uriParams, previewTitle, previewDescription, previewThumbnailUrl));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean shareApplicationUpdate(String feedText, String uriParams, String previewTitle, String previewDescription, InputStream previewThumbnailInputStream, ImageType imageExtension) {
		String httpImageUrl = uploadImageOnFTPServer(previewThumbnailInputStream, imageExtension);
		return shareApplicationUpdate(feedText, uriParams, previewTitle, previewDescription, httpImageUrl);
	}
	/**
	 * buid a an ApplicationProfile Feed
	 * 
	 * @param description add a description for the update you are sharing
	 * @param uriParams the additional parameteres your applicationProfile needs to open the subject of this update  e.g. id=12345&type=foo
	 * @param previewTitle the title to show in the preview
	 * @param previewDescription the description to show in the preview
	 * @param previewThumbnailUrl the image url to show in the preview
	 * @return a feed instance ready to be written
	 */
	private Feed buildFeed(String description, String uriParams, String previewTitle, String previewDescription, String previewThumbnailUrl) {
		String descToAdd = escapeHtml(description);

		String uri = applicationProfile.getUrl();
		//add the GET params if necessary
		if (uriParams != null && uriParams.compareTo("") != 0)
			uri += "?"+uriParams;		
		String scope = currScope;
		Feed toReturn = new Feed( 
				UUID.randomUUID().toString(), 
				FeedType.PUBLISH, 
				applicationProfile.getKey(), 
				new Date(), 
				scope, 
				uri, 
				previewThumbnailUrl, 
				descToAdd, 
				PrivacyLevel.SINGLE_VRE, 
				applicationProfile.getName(), 
				"no-email", 
				applicationProfile.getImageUrl(), 
				previewTitle, 
				previewDescription, 
				"", 
				true);
		return toReturn;
	}	
	/**
	 * 
	 * @param previewThumbnailInputStream .
	 * @param imageExtension .
	 * @return the http url of the image uploaded on the ftp server
	 */
	private String uploadImageOnFTPServer(InputStream previewThumbnailInputStream, ImageType imageExtension) {
		FTPClient client = new FTPClient( );
		InputStream inputStream = previewThumbnailInputStream;
				
		String ftpUrl = "";
		String user = "";
		String pwd = "";
		String httpBaseURL = "";
		String fileName = UUID.randomUUID() + "." + imageExtension.toString().toLowerCase();
		try {	
			ServiceEndpoint res = getConfigurationFromIS();
			AccessPoint ac = res.profile().accessPoints().iterator().next();
			ftpUrl = ac.address();
			httpBaseURL =  res.profile().runtime().hostedOn();
			user = ac.username();
			pwd = StringEncrypter.getEncrypter().decrypt(ac.password());
		
			// Connect to the FTP server as anonymous
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
		StringBuilder sb = new StringBuilder().append(httpBaseURL).append("/").append(fileName);
        _log.info( "Uploaded file FTP server: http url: " + sb );
		return sb.toString();
	}	

	/**
	 * 
	 * @return the runtime resource of the FTP Server node
	 * @throws Exception
	 */
	private ServiceEndpoint getConfigurationFromIS() throws Exception  {
		
		//set the scope
		String currScope = ScopeProvider.instance.get();
		ScopeBeanExt sbe = new ScopeBeanExt(this.currScope);
		String scopeToQuery = sbe.getInfrastructureScope();
		ScopeProvider.instance.set(scopeToQuery);
	
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Name/text() eq '"+ RUNTIME_RESOURCE_NAME +"'");
		query.addCondition("$resource/Profile/Category/text() eq '"+ CATEGORY_NAME +"'");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> conf = client.submit(query);
		ScopeProvider.instance.set(currScope);		
		return conf.get(0);
	}	
}
