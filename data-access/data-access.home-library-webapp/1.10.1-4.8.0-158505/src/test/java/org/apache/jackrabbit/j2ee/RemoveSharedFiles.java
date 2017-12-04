package org.apache.jackrabbit.j2ee;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
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


public class RemoveSharedFiles {
	static JCRWorkspace ws = null;

	private static  String portalLogin = "valentina.marioli";
	private static Session session;
	private static String fileInput = "/home/valentina/Downloads/output_share_folder.csv";
	private static String fileOutput= "/home/valentina/Downloads/output_toremove01.csv";
	//	private static String fileOutput= "/home/valentina/Downloads/output_share_folder.csv";
	private static final String NAME = "ISExporter";
	private static int found =0;
	private static int i=0;

	private static FileWriter fileWriter;
	private static CSVPrinter csvFilePrinter;
	//Delimiter used in CSV file
	private static final String NEW_LINE_SEPARATOR = "\n";

	//CSV file header
	private static final Object [] FILE_HEADER = {"user", "jcr_path"};
	

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, InterruptedException {
		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");

		try {
			String url = "http://workspace-repository.d4science.org/home-library-webapp";

			URLRemoteRepository repository = new URLRemoteRepository(url + "/rmi");
			String admin = "workspacerep.imarine";
			String pass = "gcube2010*onan";

			session = repository.login( 
					new SimpleCredentials(admin, pass.toCharArray()));


			//			Node node = session.getNode("/Home/statistical.manager/Workspace/DataMiner/Computations/BIONYM_LOCAL_ID_202974b0-ed77-4288-8ec9-f02c6fd3ad96/BIONYM_LOCAL_ID_202974b0-ed77-4288-8ec9-f02c6fd3ad96.xml");
			//			System.out.println(node.getPath());
			//			
			//			node.remove();
			//			session.save();


			check(session);


		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (session!=null)
				session.logout();
		}

	}

	private static void check(Session session) throws FileNotFoundException, IOException {

		try {

			Reader in = new FileReader(fileInput);
			Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader("jcr_id","owner", "created", "lastModified","jcr_path").parse(in);

			for (CSVRecord record : records) {

				String jcr_id = record.get("jcr_id");
				String jcr_path = record.get("jcr_path");			
				
				if (!jcr_path.equals(""))	
					if (jcr_path.contains("_backup_"))
					try{
						System.out.println("*** "+ jcr_path);
						//						Node node = session.getNode(jcr_path).getParent();
						Node node = session.getNodeByIdentifier(jcr_id).getParent();
//						System.out.println(node.getPath());
						String subpath = node.getPath().substring(0, 43);
						
//						if (!subpath.equals("/Share/477969bc-df77-42c0-889c-5d7a1262ea4e"))
//							continue;
//						System.out.println(subpath);
						Node shareNode = session.getNode(subpath);
//						System.out.println(shareNode.getPath());
						NodeIterator users = shareNode.getNode("hl:members").getNodes();

						while (users.hasNext()){

							Node user = users.nextNode();
//							System.out.println(user.getName());
							HomeManager manager1 = HomeLibrary.getHomeManagerFactory().getHomeManager();
							ws = (JCRWorkspace) manager1.getHome(user.getName()).getWorkspace();
							
							try{
							WorkspaceItem myNode = ws.getItem(node.getIdentifier());
//							System.out.println("FOUND for user " + user.getName() + " -" + myNode.getPath() );
							System.out.println("REMOVE NODE " + myNode.getPath());
							writeCsvFile(user.getName(), myNode.getPath());
							session.removeItem(myNode.getPath());
							session.save();
							} catch (Exception e) {
								System.out.println("NOT FOUND for user "+ user.getName() + " - " + jcr_path);								
								//					e.printStackTrace();
							}
						}

						//					System.out.println(jcr_id);
						//					node.remove();
						//					session.save();

						
						System.out.println("*** REMOVE " + node.getPath());
		
						writeCsvFile("", node.getPath());
						session.removeItem(node.getPath());
						session.save();
					} catch (Exception e) {

//						System.out.println("ID not foud " + jcr_path);
											e.printStackTrace();
					}

			}

		} catch (Exception e) {

			e.printStackTrace();
		} finally {

		}
		System.out.println("found " + found + "/" + i);
	}


	private static void writeCsvFile(String user, String jcr_path) {

		try {

			if (csvFilePrinter==null){
				//Create the CSVFormat object with "\n" as a record delimiter
				CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);

					//initialize FileWriter object
					fileWriter = new FileWriter(fileOutput);
					//initialize CSVPrinter object 
					csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
					//Create CSV file header
					csvFilePrinter.printRecord(FILE_HEADER);
			}
			
			
				
			List<String> line = new ArrayList<String>();
			line.add(user);
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




