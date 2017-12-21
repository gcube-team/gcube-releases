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


public class RemoveFiles {
	static JCRWorkspace ws = null;

	private static  String portalLogin = "valentina.marioli";
	private static Session session;
	private static String fileInput = "/home/valentina/Downloads/output_share_folder.csv";
//	private static String fileOutput= "/home/valentina/Downloads/output_share_folder.csv";
	private static final String NAME = "ISExporter";
	private static int found =0;
	private static int i=0;

	private static FileWriter fileWriter;
	private static CSVPrinter csvFilePrinter;


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
				String jcr_path = record.get("jcr_path");
				
				
				if (jcr_path.equals(""))
				

				try{
					System.out.println("*** "+ jcr_path);
					String jcr_id = record.get("jcr_id");
//					System.out.println(jcr_id);
					Node node = session.getNodeByIdentifier(jcr_id).getParent();
					System.out.println(node.getPath());
					
//					node.remove();
//					session.save();
		
				} catch (Exception e) {
					
					System.out.println("ID not foud " + jcr_path);
//					e.printStackTrace();
				}

			}

		} catch (Exception e) {

			e.printStackTrace();
		} finally {

		}
		System.out.println("found " + found + "/" + i);
	}




	
}




