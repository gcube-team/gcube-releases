import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchResponse;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkPublisher;
import org.gcube.spatial.data.geonetwork.GeoNetworkReader;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.configuration.Configuration;
import org.gcube.spatial.data.geonetwork.model.Account;
import org.gcube.spatial.data.geonetwork.model.Account.Type;
import org.junit.Test;
import org.opengis.metadata.Metadata;

/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Aug 31, 2016
 */
public class GeonetworkQueryTest {

	private static final int MAX = 10;

	private static final String UUID = "8a878105-ef06-4b1f-843f-120fc525b22b";

	//private String[] scopes = {"/gcube/devNext/NextNext"};

	//private String[] scopesProd = {"/d4science.research-infrastructures.eu/gCubeApps/SIASPA"};

	//private String[] scopesProd = {"/d4science.research-infrastructures.eu/gCubeApps/fisheriesandecosystematmii"};

	//private String[] scopesProd = {"/d4science.research-infrastructures.eu/D4Research"};

	private String[] scopesProd = {"/d4science.research-infrastructures.eu"};

	//private String[] scopesProd = {"/d4science.research-infrastructures.eu/gCubeApps/FAO_TunaAtlas"};

	//private String[] scopesProd = {"/d4science.research-infrastructures.eu/D4Research/Blue-Datathon"};

	private LoginLevel loginLevel = LoginLevel.CKAN;

	private Type accountType = Type.SCOPE;

	private String textToSearch = "geo_fea";

	@Test
	public void getCount() throws Exception{
		try{
			for(String scope:scopesProd){
				ScopeProvider.instance.set(scope);
				GeoNetworkPublisher reader=GeoNetwork.get();

				Configuration config = reader.getConfiguration();
				Account account=config.getScopeConfiguration().getAccounts().get(accountType);

				//System.out.println("User: "+account.getUser()+", Pwd: "+account.getPassword());
				System.out.println("Admin: "+config.getAdminAccount().getUser()+", Pwd: "+config.getAdminAccount().getPassword());

				try{
					String decryptedPassword = StringEncrypter.getEncrypter().decrypt(account.getPassword());
					System.out.println("Decrypted Password: "+decryptedPassword);
				}catch(Exception e){
					System.out.println("ignoring exception during pwd decrypting");
				}


	//			req.addParam("keyword", "Thredds");
				final GNSearchRequest req=new GNSearchRequest();
//				req.addParam(GNSearchRequest.Param.any,"Thredds");
				GNSearchResponse resp = reader.query(req);
				int publicCount=resp.getCount();
				reader.login(loginLevel);
				int totalCount=reader.query(req).getCount();
				System.out.println("SCOPE "+scope+" found "+totalCount+" (public : "+publicCount+", private :"+(totalCount-publicCount)+")");
				if(totalCount==0)
					return;
				/*try{
					int last = totalCount>MAX?totalCount:MAX;
					for(int i=0; i<last; i++){
						String xml = reader.getByIdAsRawString(resp.getMetadata(i).getUUID());
						System.out.println(i+") is Thredds? "+containsString(xml, "Thredds"));
					}
				}catch(Exception e ){
					e.printStackTrace();
				}*/

			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	//@Test
	public void getLayerByUUID() throws Exception{
		try{
			for(String scope:scopesProd){
				ScopeProvider.instance.set(scope);
				GeoNetworkPublisher reader=GeoNetwork.get();

				Configuration config = reader.getConfiguration();
				Account account=config.getScopeConfiguration().getAccounts().get(accountType);

				//System.out.println("User: "+account.getUser()+", Pwd: "+account.getPassword());
				System.out.println("Admin: "+config.getAdminAccount().getUser()+", Pwd: "+config.getAdminAccount().getPassword());

				Metadata meta = reader.getById(UUID);


				try{
					String decryptedPassword = StringEncrypter.getEncrypter().decrypt(account.getPassword());
					System.out.println("Decrypted Password: "+decryptedPassword);
				}catch(Exception e){
					System.out.println("ignoring exception during pwd decrypting");
				}

				System.out.println("SCOPE "+scope+" found meta "+meta);

			}
		}catch(Exception e){
			System.err.println("Error on getting layer by UUID: "+UUID);
			e.printStackTrace();
		}
	}


	//@Test
	public void getLayersBySearch() throws Exception{
		try{
			for(String scope:scopesProd){
				ScopeProvider.instance.set(scope);
				GeoNetworkPublisher reader=GeoNetwork.get();

				Configuration config = reader.getConfiguration();
				Account account=config.getScopeConfiguration().getAccounts().get(accountType);

				//System.out.println("User: "+account.getUser()+", Pwd: "+account.getPassword());
				System.out.println("Admin: "+config.getAdminAccount().getUser()+", Pwd: "+config.getAdminAccount().getPassword());

				try{
					String decryptedPassword = StringEncrypter.getEncrypter().decrypt(account.getPassword());
					System.out.println("Decrypted Password: "+decryptedPassword);
				}catch(Exception e){
					System.out.println("ignoring exception during pwd decrypting");
				}


	//			req.addParam("keyword", "Thredds");
				//final GNSearchRequest req=new GNSearchRequest();
				final GNSearchRequest req = getRequest(true, textToSearch);
//				req.addParam(GNSearchRequest.Param.any,"Thredds");
				GNSearchResponse resp = reader.query(req);
				int publicCount=resp.getCount();
				reader.login(loginLevel);
				int totalCount=reader.query(req).getCount();
				System.out.println("QUERY "+textToSearch);
				System.out.println("SCOPE "+scope+" found "+totalCount+" (public : "+publicCount+", private :"+(totalCount-publicCount)+")");

//				if(totalCount==0)
//					return;
//				try{
//					int last = totalCount>MAX?totalCount:MAX;
//
//					for(int i=0; i<last; i++){
//						//String xml = reader.getByIdAsRawString(resp.getMetadata(i).getUUID());
//						GNMetadata xml = resp.getMetadata(i);
//						//System.out.println(i+") is Thredds? "+containsString(xml, "Thredds"));
//						System.out.println(xml.toString());
//					}
//				}catch(Exception e ){
//					e.printStackTrace();
//				}

			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	private boolean containsString(String txt, String value){
		return txt.contains(value);
	}

	/**
	 * Gets the request.
	 *
	 * @param sortByTitle the sort by title
	 * @param textToSearch the text to search
	 * @return the request
	 */
	public GNSearchRequest getRequest(boolean sortByTitle, String textToSearch) {

		GNSearchRequest req = new GNSearchRequest();

		if(sortByTitle)
			req.addConfig(GNSearchRequest.Config.sortBy, "title");

		if(textToSearch==null || textToSearch.isEmpty()){
			req.addParam(GNSearchRequest.Param.any, textToSearch);
			System.out.println("search by any text");
		}else{
			req.addParam(GNSearchRequest.Param.title, textToSearch);
			req.addConfig(GNSearchRequest.Config.similarity, "1");
			System.out.println("search by title");
		}
		System.out.println("text to search "+textToSearch);

		return req;
	}


//	@Test
	public void getCountProd() throws Exception{
		try{
			for(String scope:scopesProd){
				ScopeProvider.instance.set(scope);
				GeoNetworkReader reader=GeoNetwork.get();
				final GNSearchRequest req=new GNSearchRequest();
	//			req.addParam("keyword", "Thredds");
//				req.addParam(GNSearchRequest.Param.any,"Oscar");
				int publicCount=reader.query(req).getCount();
				reader.login(loginLevel);
				int totalCount=reader.query(req).getCount();
				System.out.println("SCOPE "+scope+" found "+totalCount+" (public : "+publicCount+", private :"+(totalCount-publicCount)+")");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
