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

/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Aug 31, 2016
 */
public class GeonetworkQueryTest {

	private static final int MAX = 10;

	//private String[] scopes = {"/gcube/devNext/NextNext"};

	private String[] scopesProd = {"/d4science.research-infrastructures.eu/gCubeApps/BiodiversityLab"};

	private LoginLevel loginLevel = LoginLevel.ADMIN;

	private Type accountType = Type.SCOPE;

	//@Test
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


	private boolean containsString(String txt, String value){
		return txt.contains(value);
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
