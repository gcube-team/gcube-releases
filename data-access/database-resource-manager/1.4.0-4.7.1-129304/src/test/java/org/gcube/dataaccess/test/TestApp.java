package org.gcube.dataaccess.test;
//package org.gcube.dataanalysis.test;
//import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
//import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.List;
//import java.util.Scanner;
//
//import org.gcube.common.encryption.StringEncrypter;
//import org.gcube.common.resources.gcore.ServiceEndpoint;
//import org.gcube.common.scope.api.ScopeProvider;
//import org.gcube.dataanalysis.databases.resources.DBResource;
//import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
//import org.gcube.resources.discovery.client.api.DiscoveryClient;
//import org.gcube.resources.discovery.client.queries.impl.XQuery;
//import org.hibernate.SessionFactory;
//
//public class TestApp {
//
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		
//		
////		 ScopeProvider.instance.set("/gcube/devsec/devVRE");
//		 ScopeProvider.instance.set("/gcube/devsec");
//		   
//		   
//
//			XQuery query = queryFor(ServiceEndpoint.class);
//			query.addCondition("$resource/Profile/Category/text() eq 'Database'");
//			
//			DiscoveryClient<DBResource> prova=clientFor(DBResource.class);
//			List<DBResource> access = prova.submit(query);
//			
//			
//			System.out.println("size resource:  "+access.size());
//			
//			
//			
//		
//			
//			for(int i=0;i<access.size();i++){
//				
//				   			
//				//access.get(i).parse();
//				System.out.println("{ ID: "+access.get(i).getID()
//						+" ResourceName: "+access.get(i).getResourceName()+" HostedOn: "+access.get(i).getHostedOn()
//						+" PlatformName: "+access.get(i).getPlatformName()+" PlatformVersion: "+access.get(i).getPlatformVersion()+" }");
//				
//			    System.out.println();
//				
//				for (int j=0;j<access.get(i).getAccessPoints().size();j++){
//					
//					
//					System.out.println("############################# AccessPointInfo #################################");
//					System.out.println("Description: "+access.get(i).getAccessPoints().get(j).getDescription()
//							+"\n"+"Endpoint: "+access.get(i).getAccessPoints().get(j).address()
//							//+"\n"+"Port Number: "+access.get(i).getAccessPoints().get(j).getPort());
//							+"\n"+"Port Number: "+access.get(i).getPort());
////							);
//					
//					
//					System.out.println("Username: "+access.get(i).getAccessPoints().get(j).getUsername()
//							+"\n"+"Password: "+access.get(i).getAccessPoints().get(j).getPassword()
//							+"\n"+"DatabaseName: "+access.get(i).getAccessPoints().get(j).getDatabaseName()
//							+"\n"+"Driver: "+access.get(i).getAccessPoints().get(j).getDriver()
//					        +"\n"+"Dialect: "+access.get(i).getAccessPoints().get(j).getDialect()
//					        +"\n"+"MaxConnections: "+access.get(i).getAccessPoints().get(j).getMaxConnections()
//					        +"\n"+"Schema: "+access.get(i).getAccessPoints().get(j).getSchema()
//					        +"\n"+"tableSpaceCount: "+access.get(i).getAccessPoints().get(j).getTableSpaceCount()
//					        +"\n"+"tableSpacePrefix: "+access.get(i).getAccessPoints().get(j).getTableSpacePrefix());
//					
//					
//					//System.out.println("Dim: "+access.get(i).getAccessPoints().get(j).getAuxiliaryProperties().size());
//					
//					if (access.get(i).getAccessPoints().get(j).getAuxiliaryProperties().size()!=0){
//					
//						
//						
//					System.out.println("AuxiliaryProperty: "+"'aquamapsWorldTable' "+access.get(i).getAccessPoints().get(j).getAuxiliaryProperties().get("aquamapsWorldTable"));
//					System.out.println("AuxiliaryProperty: "+"'aquamapsDataStore' "+access.get(i).getAccessPoints().get(j).getAuxiliaryProperties().get("aquamapsDataStore"));
//					
//					
//					
//					
//					
//					
//					}
//				}
//				
//				System.out.println();
//				System.out.println();
//				
//				
//				
//				//System.out.println("ID "+access.get(i).getID()+" User "+access.get(i).getTestData().size());
//				
//			}
//			
//			
//			
//			
//			// Fase di Selezione del DB e Normalizzazione
//			
//			//access.get(2).normalize("//geoserver-dev.d4science-ii.research-infrastructures.eu:5432/aquamapsdb");
//			
////		access.get(1).normalize("jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
//			
//			
//	// retrieve the decrypted version		
////			try {
////				String password = StringEncrypter.getEncrypter().decrypt("Db/lnp5cAPwrAfjqorqctA==");
////				
////				System.out.println("password Obis: " +password);
////			} catch (Exception e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
////			
//			
//			
//			
//			Scanner sc = new Scanner(System.in);
//			
//			System.out.println("Seleziona la risorsa database: inserisci un numero fra 0 e "+Integer.toString((access.size())-1));
//			System.out.println();
//			
//	        String index_resource = sc.next();
//
//	        System.out.println("Ho letto: " + index_resource);
//			
//			
//	        int resourceIndex= Integer.valueOf(index_resource).intValue();
//	        
//	        
////	        System.out.println("Inserisci l'Endpoint della risorsa");
////	        String Endpoint_value = sc.next();
//	        
//	        
//	        System.out.println("Seleziona il database: inserisci un numero fra 0 e "+Integer.toString(access.get(resourceIndex).getAccessPoints().size()-1));
//			System.out.println();
//	        
//			
//			String db_value = sc.next();
//			
//			System.out.println("Ho letto: " + db_value);
//				
//				
//			int dbIndex= Integer.valueOf(db_value).intValue();
//	        
//			//access.get(resourceIndex).normalize(access.get(resourceIndex).getAccessPoints().get(dbIndex).address());
//			
//			
//			
//			
//			
//			
//			//Fase di Stampa
//			
//			
//			
//			
//			
//			
//			
//			
//			
//			
//			
//			///***** Da qui ho commentato
//			
////			try {
//				access.get(resourceIndex).normalize(dbIndex);
//				
//				
//				System.out.println();
//				System.out.println();
//				
//				
//				
//				System.out.println("---------------------------------------------------------------"+"  Normalization:  "+"-----------------------------------------------------");
//				System.out.println();
//				System.out.println();
//				System.out.println();
//				System.out.println();
//				
//				
//				for(int i=0;i<access.size();i++){
//					
//		   			
//					//access.get(i).parse();
//					System.out.println("{ ID: "+access.get(i).getID()
//							+" ResourceName: "+access.get(i).getResourceName()+" HostedOn: "+access.get(i).getHostedOn()
//							+" PlatformName: "+access.get(i).getPlatformName()+" PlatformVersion: "+access.get(i).getPlatformVersion()+" }");
//					
//					
//					System.out.println();
//					
//					for (int j=0;j<access.get(i).getAccessPoints().size();j++){
//						
//						
//						System.out.println("############################# AccessPointInfo #################################");
//						System.out.println("Description: "+access.get(i).getAccessPoints().get(j).getDescription()
//								+"\n"+"Endpoint: "+access.get(i).getAccessPoints().get(j).address()
//								//+"\n"+"Port Number: "+access.get(i).getAccessPoints().get(j).getPort());
//								+"\n"+"Port Number: "+access.get(i).getPort());
//						
//						
//						
//						
//						System.out.println("Username: "+access.get(i).getAccessPoints().get(j).getUsername()
//								+"\n"+"Password: "+access.get(i).getAccessPoints().get(j).getPassword()
//								+"\n"+"DatabaseName: "+access.get(i).getAccessPoints().get(j).getDatabaseName()
//								+"\n"+"Driver: "+access.get(i).getAccessPoints().get(j).getDriver()
//						        +"\n"+"Dialect: "+access.get(i).getAccessPoints().get(j).getDialect()
//						        +"\n"+"MaxConnections: "+access.get(i).getAccessPoints().get(j).getMaxConnections()
//						        +"\n"+"Schema: "+access.get(i).getAccessPoints().get(j).getSchema()
//						        +"\n"+"tableSpaceCount: "+access.get(i).getAccessPoints().get(j).getTableSpaceCount()
//						        +"\n"+"tableSpacePrefix: "+access.get(i).getAccessPoints().get(j).getTableSpacePrefix());
//						
//						
//						//System.out.println("Dim: "+access.get(i).getAccessPoints().get(j).getAuxiliaryProperties().size());
//						
//						if (access.get(i).getAccessPoints().get(j).getAuxiliaryProperties().size()!=0){
//							System.out.println();
//							
//							
//						System.out.println("AuxiliaryProperty: "+"'aquamapsWorldTable' "+access.get(i).getAccessPoints().get(j).getAuxiliaryProperties().get("aquamapsWorldTable"));
//						System.out.println("AuxiliaryProperty: "+"'aquamapsDataStore' "+access.get(i).getAccessPoints().get(j).getAuxiliaryProperties().get("aquamapsDataStore"));
//						
//						
//						
//						
//						}
//					}
//					
//					System.out.println();
//					System.out.println();
//					
//					
//					
//					//System.out.println("ID "+access.get(i).getID()+" User "+access.get(i).getTestData().size());
//					
//				}
//				
//				
//				
//				
//				System.out.println("---------------------------------------------------------------"+"  Database Query:  "+"-----------------------------------------------------");
//				
////				// Sottomissione query
////				
////				System.out.println("Insert the Query");
////				
////			
////			
////			
////			InputStreamReader is = new InputStreamReader(System.in);
////			BufferedReader br = new BufferedReader(is);
////			
////			String q="";
////			try {
////				q = br.readLine();
////			} catch (IOException e1) {
////				// TODO Auto-generated catch block
////				e1.printStackTrace();
////			}
////			
////			System.out.println("Letta: "+q);
//				
//				
//				
//				org.gcube.dataanalysis.databases.utilsold.ConnectionManager df=new org.gcube.dataanalysis.databases.utilsold.ConnectionManager();
//				
////				AlgorithmConfiguration config=df.setconfiguration
////						("./cfg/", access.get(1).getAccessPoints().get(0).getUsername(), access.get(1).getAccessPoints().get(0).getPassword(),
////								access.get(1).getAccessPoints().get(0).getDriver(), access.get(1).getAccessPoints().get(0).getDialect(), 
////								access.get(1).getAccessPoints().get(0).address());
//				
//				
//				
//				
//				
//				//Codice funzionante
////				AlgorithmConfiguration config=df.setconfiguration("./cfg/", "utente", "d4science", "org.postgresql.Driver","org.hibernate.dialect.MySQLDialect", 
////											"jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu:5432/testdb");
//				
//				
//				
//				
//				AlgorithmConfiguration config;
//				try {
//					config = df.setconfiguration("./cfg/", access.get(resourceIndex).getAccessPoints().get(dbIndex).getUsername(),
//							access.get(resourceIndex).getAccessPoints().get(dbIndex).getPassword(), access.get(resourceIndex).getAccessPoints().get(dbIndex).getDriver(), access.get(resourceIndex).getAccessPoints().get(dbIndex).getDialect(),
//							access.get(resourceIndex).getAccessPoints().get(dbIndex).address(), access.get(resourceIndex).getAccessPoints().get(dbIndex).getDatabaseName());
//				
//					SessionFactory sf=df.createConnection(config);
//					
//					boolean val=sf.isClosed();
//					
//					if (val!=true){
//						
//						
//						System.out.println("la connessione è attiva");
//						
//					}
//				
//				
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//				
////				if (config!=null){
//				
//				
////				SessionFactory sf=df.createConnection(config);
////				
////				boolean val=sf.isClosed();
////				
////				if (val!=true){
////					
////					
////					System.out.println("la connessione è attiva");
////					
////				}
//				
//				
//				
////				try {
//////					List<Object> rawnames = df.executeQuery("select * from (select a.field1 as aa, b.field1_id as bb, b.field1 as cc from a7f768710_c7b0_11df_b2bc_e0f36cf4c8cd as a "+ 
//////							"left join cur_00d4e2d0_ecbd_11df_87fa_de008e0850ff as b on a.field3_id=b.field1_id) as cd", sf);
////					
////					/*List<Object> rawnames = df.executeQuery("select * from (select a.field1, b.field1_id, b.field1 from a7f768710_c7b0_11df_b2bc_e0f36cf4c8cd as a "+ 
////							"left join cur_00d4e2d0_ecbd_11df_87fa_de008e0850ff as b on a.field3_id=b.field1_id) as cd", sf);*/
////					
//////					List<Object> rawnames = df.executeQuery("select a.field1 as aa, b.field1_id as bb, b.field1 as cc from a7f768710_c7b0_11df_b2bc_e0f36cf4c8cd as a "+ 
//////							"left join cur_00d4e2d0_ecbd_11df_87fa_de008e0850ff as b on a.field3_id=b.field1_id)", sf);
////					
////					
////					
////					
////					
////					//Query funzionante
////					
//////					List<Object> rawnames = df.executeQuery("select a.field1, b.field1_id from a7f768710_c7b0_11df_b2bc_e0f36cf4c8cd as a "+ 
//////							"left join cur_00d4e2d0_ecbd_11df_87fa_de008e0850ff as b on a.field3_id=b.field1_id", sf);
////					
////					
////					//Query non funzionante
//////					List<Object> rawnames = df.executeQuery("select a.field1, b.field1_id, b.field1 from a7f768710_c7b0_11df_b2bc_e0f36cf4c8cd as a "+ 
//////							"left join cur_00d4e2d0_ecbd_11df_87fa_de008e0850ff as b on a.field3_id=b.field1_id", sf);
////					
////					//Query funzionante con alias 
////					
//////					List<Object> rawnames = df.executeQuery("select a.field1 as aa, b.field1_id as bb, b.field1 as cc from a7f768710_c7b0_11df_b2bc_e0f36cf4c8cd as a "+ 
//////							"left join cur_00d4e2d0_ecbd_11df_87fa_de008e0850ff as b on a.field3_id=b.field1_id", sf);
////					
////					//Query funzionante
//////					List<Object> rawnames = df.executeQuery("select a.source_data as sourceA, b.source_data as sourceB, a.target_data_scientific_name as targetA, b.target_data_scientific_name as tagertB " +
//////					"from bionymoutsimplefaked1csvpreprcsv as a join bionymoutfaked1csvpreprcsv as b on a.source_data=b.source_data limit 10",sf);
////					
////					List<Object> rawnames = df.executeQuery(q,sf);
////					
////					
////					System.out.println("***************************************************************");
////					System.out.println();
////					
////					System.out.println("Size: "+rawnames.size());
//	//
////					for (int i = 0; i < rawnames.size(); i++) {
//	//
////						Object[] row = (Object[]) rawnames.get(i);
//	//
////						for (int j = 0; j < row.length; j++) {
//	//
////							System.out.print("\"" + row[j] + "\"; ");
//	//
////						}
////						System.out.println();
////						//System.out.println("Fine ");
//	//
////					}
////					
////					
////					
////				} catch (Exception e) {
////					// TODO Auto-generated catch block
////					//e.printStackTrace();
////					
////					System.out.println("***************************************************************");
////					System.out.println();
////					
////					String error=e.getCause().toString();
////					
////					if (error.contains("MySQLSyntaxErrorException"))
////					{
////						
////						System.out.println("ERROR "+e.getMessage()+" "+"because an error is present: "+e.getCause().getMessage());
////						System.out.println("Suggestion: insert an alias name for the columns");
////						
////						
////					}
////					
////					
////				}
//				
//				
//				
////		}else {
////			System.out.println("ERRORE: Non è possibile eseguire la connessione perchè l'indirizzo non è completo: databasename non dsiponibile");
////		}8
//				
//				
//				
////			} catch (IOException e1) {
////				// TODO Auto-generated catch block
////				e1.printStackTrace();
////			}
//			
//			
//			
//			
//			
//			
//	}
//}
