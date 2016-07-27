package org.apache.jackrabbit.j2ee;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.j2ee.workspacemanager.storage.GCUBEStorage;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;


public class RemoveRobertosFiles {
	private static final String nameResource 				= "HomeLibraryRepository";
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		//				ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		String rootScope = "/gcube";
		ScopeProvider.instance.set(rootScope);

		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq 'Database' and $resource/Profile/Name eq '"+ nameResource + "' ");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> resources = client.submit(query);


		try {
			ServiceEndpoint resource = resources.get(0);

			for (AccessPoint ap:resource.profile().accessPoints()) {

				if (ap.name().equals("JCR")) {

					//					String url = ap.address();
					//							url = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-webapp-patched-2.4.3";

					String user = ap.username();						
					String pass = StringEncrypter.getEncrypter().decrypt(ap.password());

//					String url = "http://ws-repo-test.d4science.org/home-library-webapp/";
					String url = "http://node11.d.d4science.research-infrastructures.eu:8080/home-library-webapp/";
					URLRemoteRepository repository = new URLRemoteRepository(url + "/rmi");
					Session session = repository.login( 
							new SimpleCredentials(user, pass.toCharArray()));

					
//					session.getItem("/Home/frnacesco.mangiacrapa/").remove();
//					session.save();

					String serviceName = "test-home-library";	
//
//
////										List<String> list = new ArrayList<String>();
////					
////										list.add("95aeee81-fb21-4b61-8601-bf8d3d951e95");
////										list.add("2c3a9b66-9ea4-4b6e-a2e4-f042e1273b3b");
////					
////										for(String uuid: list){
//										String folderPath = "/Home/valentina.marioli/Workspace/AAAA";
//					
//										System.out.println("remove " + folderPath + " from storage");
//										GCUBEStorage storage = new GCUBEStorage("valentina.marioli", rootScope, serviceName);
//										storage.removeRemoteFolder(folderPath);
//					
//										System.out.println("remove " + folderPath + " from jackrabbit");
//										Node node = session.getNode(folderPath);
//										node.remove();
//										session.save();
//										}



//					for (int i=0; i<=50; i++){
//						String portalLogin = "test-"+ i;
						
						String portalLogin = "francesco.mangiacrapa";
						String folderPath = "/Home/"+portalLogin+"/Workspace/test unzip";

						System.out.println("remove " + folderPath + " from storage");
						GCUBEStorage storage = new GCUBEStorage(portalLogin, rootScope, serviceName);
						storage.removeRemoteFolder(folderPath);

						System.out.println("remove " + folderPath + " from jackrabbit");
						Node node = session.getNode(folderPath);
						node.remove();
						session.save();
//					}


					//					NodeIterator iterator = node.getNodes();
					//					while(iterator.hasNext()){
					//						Node currentNode = iterator.nextNode();
					//						try{	
					//							System.out.println(currentNode.getPath());
					//							currentNode.remove();
					//							session.save();
					//						}catch (Exception e){
					//							try{	
					//							if (currentNode.isNodeType(JcrConstants.MIX_REFERENCEABLE)) {
					//								
					//								Node original = currentNode.getProperty(NodeProperty.REFERENCE.toString()).getNode();
					//								System.out.println("Reference to: " + original.getPath());
					//								
					//								} else {
					//									System.out.println("no referenceable");
					//								// there is a node with that uuid but the node does not expose it
					//
					//								}
					//							}catch (Exception e1){
					//							e1.printStackTrace();
					//							}
					//						}
					//					}



					//					List<String> list = new ArrayList<String>();
					//
					//					list.add("lucio.lelii");
					//					list.add("leonardo.candela");
					//					list.add("pasquale.pagano");
					//					list.add("giancarlo.panichi");
					//					list.add("roberto.cirillo");
					//					list.add("fabio.sinibaldi");
					//					list.add("valentina.marioli");
					//					list.add("francesco.mangiacrapa");
					//					list.add("emmanuel.blondel");
					//					list.add("donatella.castelli");
					//					list.add("agentuk");
					//					list.add("nicolas.bailly");
					//					list.add("anton.ellenbroek");
					//					list.add("federico.defaveri");
					//					list.add("gianpaolo.coro");
					//					list.add("o.kondratyeva");
					//					list.add("yann.laurent");
					//					list.add("camibofi");
					//					list.add("fabio.simeoni");
					//					list.add("davidcurrie2001");
					//					list.add("alice.tani");
					//					list.add("ocorcho");
					//					list.add("asymeon");
					//					list.add("irlfisheriescontrol");
					//					list.add("herve.caumont");
					//					list.add("andrea.manieri");
					//					list.add("loredana.liccardo");
					//					list.add("mickwilson");
					//					list.add("alistair");
					//					list.add("ieo");
					//					list.add("panagiota.koltsida");
					//					list.add("francesco.cerasuolo");
					//					list.add("george.kakaletris");
					//					list.add("tony.jarrett");
					//					list.add("francesco.calderini");
					//					list.add("warda");
					//					list.add("jerome.guitton");
					//					list.add("annkatrien");
					//					list.add("epi");
					//					list.add("okeeble");
					//					list.add("kbbe4life");
					//					list.add("thogui");
					//					list.add("jsantiago");
					//					list.add("maria.dfborges");
					//					list.add("andreapannocchi");
					//					list.add("carlo.allocca");
					//					list.add("felicitasl");
					//					list.add("paolo.manghi");
					//					list.add("carole");
					//					list.add("salome");
					//					list.add("fblanc");
					//					list.add("sparnocchia");
					//					list.add("ingrid");
					//					list.add("marc");
					//					list.add("adriano");
					//					list.add("kostas.kakaletris");
					//					list.add("pfonseca");
					//					list.add("helenedbqt");
					//					list.add("somass");
					//					list.add("tloubrieu");
					//					list.add("nicolas");
					//					list.add("bcalton");
					//					list.add("creverte");
					//					list.add("luca.frosini");
					//					list.add("jsadler2");
					//					list.add("gappanic");
					//					list.add("tommaso.piccioli");
					//					list.add("imma");
					//					list.add("andrea.mannocci");
					//					list.add("claudio.atzori");
					//					list.add("phdunipi");
					//					list.add("caf");
					//					list.add("andreacimino");
					//					list.add("rpalama");
					//					list.add("gsignori");
					//					list.add("rodrigues");
					//					list.add("julien.barde");
					//					list.add("mmikulicic");
					//					list.add("jlopez");
					//					list.add("nancie.cummings");
					//					list.add("jpeterseil");
					//					list.add("phil");
					//					list.add("paultaconet");
					//					list.add("largesi");
					//					list.add("costantino.perciante");
					//					list.add("constantinosl");
					//					list.add("antgers");
					//					list.add("marco.moscatelli");
					//					list.add("ozturaca");
					//					list.add("collins.udanor");
					//					list.add("rene.vanhorik");
					//					list.add("gergely.sipos");
					//					list.add("mike");
					//					list.add("anna");
					//					list.add("lambrosio66");
					//					list.add("zarifis");
					//					list.add("k.seferis");
					//					list.add("risse");
					//					list.add("statistical.manager");
					//					list.add("antgers.1");
					//					list.add("pierpaolo.petriccione");
					//					list.add("fabio2111");
					//					list.add("kind.of.blue");
					//					list.add("fabrizio.natale");
					//					list.add("johann.petrak");
					//					list.add("miles");
					//					list.add("massimiliano.assante");
					//					
					//					
					//					for(String login: list){
					//										Node node = session.getNode("/Home/"+ login +"/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-TabularDataLab");
					//
					//							System.out.println(node.getPath());			
					//										
					////																				node.remove();
					////										session.save();
					//					}


					//					while (iterator.hasNext()){
					//						Node node = iterator.nextNode();
					//											
					//						if (node.hasProperty("hl:portalLogin")){
					//							Property createdBy = node.getProperty("hl:portalLogin");
					////							System.out.println(node.getPath() + " - " + createdBy.getString());
					//							if (createdBy.getString().equals("valentina.marioli")){
					//
					//								System.out.println(node.getPath());
					//								try{
					//								node.remove();
					//								session.save();
					//								}catch (Exception e){
					//									e.printStackTrace();
					//								}
					//							}
					//
					//						}
					//						PropertyIterator properties = node.getProperties();
					//						while (properties.hasNext()){
					//							Property property = properties.nextProperty();
					//							System.out.println(property.getName());
					//						}
					//						iterator.nextNode().remove();
					//						session.save();
				}
				//					session.getNode("/Home/valentina.marioli/OutBox/1b09177f-43d4-4918-bb3f-6bc22a7d928d").remove();
				//					session.save();
				//					NodeIterator children = session.getNode("/Home/valentina.marioli/Workspace/MySpecialFolders").getNodes();
				//
				//					while (children.hasNext()){
				//						Node node = children.nextNode();
				////						System.out.println(node.getPrimaryNodeType().getName());
				//						if (node.getPrimaryNodeType().getName().equals("nthl:workspaceSharedItem")){
				////							if (node.getProperty("jcr:title").getString().startsWith("gcube-devsec-Tes")){
				////								System.out.println(node.getPath());
				//								node.remove();
				//								session.save();
				////							}
				//						}
				//					}


			}
			//		}
		}finally{}
	}


}
