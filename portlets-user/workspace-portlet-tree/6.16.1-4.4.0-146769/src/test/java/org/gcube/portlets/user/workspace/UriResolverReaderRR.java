/**
 *
 */
package org.gcube.portlets.user.workspace;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.workspace.server.GWTWorkspaceBuilder;
import org.gcube.portlets.user.workspace.server.resolver.UriResolverReaderParameterForResolverIndex;
import org.gcube.portlets.user.workspace.server.resolver.UriResolverReaderParameterForResolverIndex.RESOLVER_TYPE;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.impl.XQuery;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 26, 2013
 *
 */
public class UriResolverReaderRR {

	//Base Address
	String uri = "";
    //Query URL parameter
	String parameter = "";
	private String uriRequest = "";
	public static String DEFAULT_SCOPE = "/d4science.research-infrastructures.eu/gCubeApps"; //PRODUCTION
//	public static String DEFAULT_SCOPE = "/gcube/devsec"; //PRODUCTION

	public static Logger log = Logger.getLogger(UriResolverReaderRR.class);


	/**
	 * @throws Exception
	 *
	 */
	public UriResolverReaderRR(String scope) throws Exception {

		ScopeProvider.instance.set(scope);

		XQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Name/text() eq 'HTTP-URI-Resolver'").setResult("$resource/Profile/AccessPoint");

		DiscoveryClient<AccessPoint> client = clientFor(AccessPoint.class);

		List<AccessPoint> endpoints = client.submit(query);

		if (endpoints.size() == 0)
			throw new Exception("No Resolver available");

		//Base Address
//	    System.out.println(endpoints.get(0).address());

	    uri = endpoints.get(0)!=null?endpoints.get(0).address():"";

	    if(endpoints.get(0)!=null){

	    	parameter = endpoints.get(0).propertyMap()!=null?endpoints.get(0).propertyMap().get("parameter").value():"";
	    }

	    uriRequest  = uri+"?"+parameter;


	     //Query URL parameter
//		System.out.println(endpoints.get(0).propertyMap().get("parameter").value());

	}

	public InputStream resolve(String smp) throws IOException{
		String query = uriRequest + "="+smp;

		URL url = new URL(query);

		return url.openStream();
	}


	/**
	 *
	 * @return Base Address of Uri Resolver
	 */
	public String getUri() {
		return uri;
	}


	/**
	 *
	 * @return Query URL parameter of Uri Resolver
	 */
	public String getParameter() {
		return parameter;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UriResolverReaderParameter [uri=");
		builder.append(uri);
		builder.append(", parameter=");
		builder.append(parameter);
		builder.append("]");
		return builder.toString();
	}


	public static void main(String[] args) throws Exception {


//		log.trace(new UriResolverReaderRR());
//
//		UriResolverReaderRR uriResolver = new UriResolverReaderRR();
//		InputStream is = uriResolver.resolve("smp://Wikipedia_logo_silver.png?5ezvFfBOLqaqBlwCEtAvz4ch5BUu1ag3yftpCvV+gayz9bAtSsnO1/sX6pemTKbDe0qbchLexXeWgGcJlskYE8td9QSDXSZj5VSl9kdN9SN0/LRYaWUZuP4Q1J7lEiwkU4GKPsiD6PDRVcT4QAqTEy5hSIbr6o4Y");
//
//		File file = new File("test.png");
//
//		FileOutputStream out = new FileOutputStream(file);
//
//
//		IOUtils.copy(is, out);
//		is.close();
//
//		out.close();

//		066086bc-5db1-48fc-b365-2ee821db2fb7
//		370dc1cc-2e5e-4321-bc6f-ad860414db97

//		id: f70f01f9-5a06-4123-b6ec-bd121c1af82f, name: testResponse.html, ### Public link: smp://Home/francesco.mangiacrapa/Workspace41e79e40-ec82-416b-ba83-29b54bb43426?5ezvFfBOLqb3YESyI/kesN4T+ZD0mtmc/4sZ0vGMrl0lgx7k85j8o2Q1vF0ezJi/xIGDhncO9jOkV1T8u6Db7GZ/4ePgMws8Jxu8ierJajHBd20bUotElPG3BVG0ODMHf1ztm6rKJIAeb9R/0FEIDQ==
//		id: 35a0298e-da69-464f-9170-3dc764da564d, name: wiki ws backup, ### Public link: smp://Home/francesco.mangiacrapa/Workspaceb7c9bf85-b0e0-47ac-87e5-eba34048f7ab?5ezvFfBOLqb3YESyI/kesN4T+ZD0mtmc/4sZ0vGMrl0lgx7k85j8o2Q1vF0ezJi/xIGDhncO9jOkV1T8u6Db7GZ/4ePgMws8Jxu8ierJajHBd20bUotElPG3BVG0ODMHf1ztm6rKJIAeb9R/0FEIDQ==


//		id: 370dc1cc-2e5e-4321-bc6f-ad860414db97, name: testupload.txt, ### Public link: smp://Home/francesco.mangiacrapa/Workspace284ee688-e6fb-4080-bbcb-cc7c8cc5c381?5ezvFfBOLqb3YESyI/kesN4T+ZD0mtmc/4sZ0vGMrl0lgx7k85j8o2Q1vF0ezJi/xIGDhncO9jOkV1T8u6Db7GZ/4ePgMws8Jxu8ierJajHBd20bUotElPG3BVG0ODMHf1ztm6rKJIAeb9R/0FEIDQ==

		String itemId = "0e875263-f6bc-4945-9435-bfb8774027ca";
//		itemId = "f70f01f9-5a06-4123-b6ec-bd121c1af82f";
		System.out.println("uri is: "+getPublicLinkForFolderItemId(itemId));



//



	}

	public static String getPublicLinkForFolderItemId(String itemId){

		try{

			ScopeProvider.instance.set(DEFAULT_SCOPE);

			Workspace workspace =  HomeLibrary
					.getHomeManagerFactory()
					.getHomeManager()
					.getHome("leonardo.candela")
					.getWorkspace();

			GWTWorkspaceBuilder builder = new GWTWorkspaceBuilder();

			System.out.println("get item...");
			WorkspaceItem wsItem = workspace.getItem(itemId);

			if(wsItem.getType().equals(WorkspaceItemType.FOLDER_ITEM)){

				FolderItem folderItem = (FolderItem) wsItem;
				System.out.println("get public link...");
				String smpUri = builder.getPublicLinkForFolderItem(folderItem);
				System.out.println("smpUri "+smpUri);

				System.out.println("get uriResolver...");
				UriResolverReaderParameterForResolverIndex uriResolver = new UriResolverReaderParameterForResolverIndex(DEFAULT_SCOPE, RESOLVER_TYPE.SMP_ID);

//				String url = uriResolver.resolveAsUriRequest(smpUri, "testResponse!@#$$%^^&&.html", folderItem.getMimeType(), true);
				String url = uriResolver.resolveAsUriRequest(smpUri, folderItem.getName(), folderItem.getMimeType(), true);
				System.out.println("url econded: "+url);
//
//				 url = uriResolver.resolveAsUriRequest(smpUri, folderItem.getName(), folderItem.getMimeType(), false);
//				System.out.println("url doesn't econded: "+url);


//				uriResolver.getHashParemeters(smpUri, folderItem.getName(), folderItem.getMimeType());


//				HttpCallerUtil callerUtil = new HttpCallerUtil(uriResolver.getBaseUri(), "", "");
//				String query = uriResolver.getQuery();
//				callerUtil.callPost("", query, "application/x-www-form-urlencoded");


//				String smpUri = "smp://Home/test.user/Workspace/bla8200ceb0-c525-40e6-bad1-a63f83811d3d?5ezvFfBOLqb3YESyI/kesN4T+ZD0mtmc/4sZ0vGMrl0lgx7k85j8o2Q1vF0ezJi/xIGDhncO9jOkV1T8u6Db7GZ/4ePgMws8Jxu8ierJajHBd20bUotElPG3BVG0ODMHf1ztm6rKJIAeb9R/0FEIDQ==";


				UrlShortener urlShortner = new UrlShortener(DEFAULT_SCOPE);

				String shortUrl = urlShortner.shorten(url);
				System.out.println("url shortUrl: "+shortUrl);



				InputStream is = uriResolver.resolveAsInputStream(smpUri, folderItem.getName(), folderItem.getMimeType());

				File file = new File(folderItem.getName());

				FileOutputStream out = new FileOutputStream(file);


				IOUtils.copy(is, out);
				is.close();

				out.close();

				if(uriResolver!=null && uriResolver.isAvailable()){
					return uriResolver.resolveAsUriRequest(smpUri, folderItem.getName(), folderItem.getMimeType(), true);
				}

			}


			return "";

		}catch (Exception e) {
			System.out.println("Error getPublicLinkForFolderItemId url for");
			e.printStackTrace();
			return "";
		}

	}



}
