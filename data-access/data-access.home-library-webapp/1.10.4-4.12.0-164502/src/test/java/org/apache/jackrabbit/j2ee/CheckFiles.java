package org.apache.jackrabbit.j2ee;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.folder.items.File;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.scope.api.ScopeProvider;


public class CheckFiles {
	static JCRWorkspace ws = null;

	private static  String portalLogin = "valentina.marioli";
	private static Session session;
	private static String fileInput = "/home/valentina/Downloads/result.csv";
	private static String fileOutput= "/home/valentina/Downloads/output_share_folder.csv";
	private static final String NAME = "ISExporter";
	private static int found =0;
	private static int i=0;

	private static FileWriter fileWriter;
	private static CSVPrinter csvFilePrinter;


	//Delimiter used in CSV file
	private static final String NEW_LINE_SEPARATOR = "\n";

	//CSV file header
	private static final Object [] FILE_HEADER = {"jcr_id","owner", "created", "lastModified","jcr_path"};


	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, InterruptedException {
		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");

		try {
			String url = "http://workspace-repository.d4science.org/home-library-webapp";


			//										String admin = ap.username();						
			//										String pass = StringEncrypter.getEncrypter().decrypt(ap.password());


			URLRemoteRepository repository = new URLRemoteRepository(url + "/rmi");
			String admin = "workspacerep.imarine";
			String pass = "gcube2010*onan";

			session = repository.login( 
					new SimpleCredentials(admin, pass.toCharArray()));
			
			
//			check(session);


		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (session!=null)
				session.logout();
		}

	}

	private static void check(Session session) throws FileNotFoundException, IOException {



		//Create the CSVFormat object with "\n" as a record delimiter
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);

		try {

			//initialize FileWriter object
			fileWriter = new FileWriter(fileOutput);
			//initialize CSVPrinter object 
			csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
			//Create CSV file header
			csvFilePrinter.printRecord(FILE_HEADER);

			Reader in = new FileReader(fileInput);
			Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader("jcr_path", "jcr_id", "jcr_mongo_path","jcr_mongo_node","mongo_path", "mongo_node_id","payload_retrievable").parse(in);

			for (CSVRecord record : records) {
				String jcr_path = record.get("jcr_path");
				//			System.out.println(jcr_path);

				try{

					String jcr_id = record.get("jcr_id");
					String jcr_mongo_path = record.get("jcr_mongo_path");
					String jcr_mongo_node = record.get("jcr_mongo_node");
					String mongo_path = record.get("mongo_path");
					String mongo_node_id = record.get("mongo_node_id");
					//				System.out.println(mongo_node_id);
					boolean payload_retrievable = Boolean.parseBoolean(record.get("payload_retrievable"));


					if(!payload_retrievable){

						if (jcr_path.startsWith("/Share/")){
							//						System.out.println("jcr_path " +jcr_path + " - jcr_id " +jcr_id );
							//						System.out.println(i++ + jcr_path );
							String[] path = jcr_path.split("/");
							//							String owner = path[2];
							//												String owner = "valentina.marioli";
							String owner = null;

							getWsInfo(owner, jcr_path, jcr_id, mongo_node_id);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		} finally {
			try {

				fileWriter.close();
				csvFilePrinter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter/csvPrinter !!!");
				e.printStackTrace();
			}
		}
		System.out.println("found " + found + "/" + i);
	}




	private static void getWsInfo(String owner, String jcr_path, String jcr_id, String mongo_node_id) throws WorkspaceFolderNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException {

		String lastModified = null;
		String created = null;
		String path = null;

		try{
			//							System.out.println(i++ + " --> " + owner + " jcr_id " + jcr_id);
			String nodeId;
			Node node = session.getNodeByIdentifier(jcr_id);
			String storage_id = null;
			if (jcr_path.endsWith("jcr:content")){

				if (mongo_node_id.equals(""))
					storage_id = node.getProperty("hl:storageId").getString();
				Node parent = node.getParent();
				nodeId = parent.getIdentifier();
				node = parent;
			} else{
				nodeId = jcr_id;
			}

			if (owner==null){
				owner = node.getProperty("hl:portalLogin").getString();
			}

			created = node.getProperty("jcr:created").getString();
			lastModified = node.getProperty("jcr:lastModified").getString();

			HomeManager manager1 = HomeLibrary.getHomeManagerFactory().getHomeManager();
			ws = (JCRWorkspace) manager1.getHome(owner).getWorkspace();

			path = ws.getItem(node.getIdentifier()).getPath();
			System.out.println(i++ + " jcr_path " +path + " - jcr_id " +jcr_id + " mongo_node_id " + mongo_node_id + " owner " + owner );

			mongo_node_id = storage_id;
			System.out.println();

			System.out.println("mongo_node_id " + mongo_node_id);

			System.out.println(ws.getStorage().getRemoteFile(mongo_node_id).available());
			System.out.println("*********** Found!! " + found++);


		} catch (Exception e) {

			writeCsvFile(jcr_id,path, owner,lastModified,created);
			e.printStackTrace();
		}

	}

	private static void writeCsvFile(String jcr_id, String jcr_path, String owner, String lastModified, String created) {

		try {

			List<String> line = new ArrayList<String>();
			line.add(jcr_id);
			line.add(owner);
			line.add(created);
			line.add(lastModified);
			line.add(jcr_path);
			csvFilePrinter.printRecord(line);

			System.out.println("CSV line added successfully !!!");

		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
			} catch (IOException e) {
				System.out.println("Error while flushing fileWriter/csvPrinter !!!");
				e.printStackTrace();
			}
		}

	}
}




