package org.apache.jackrabbit.j2ee;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.commons.csv.CSVFormat;
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


public class RemoveMissingVREs {
	static JCRWorkspace ws = null;


	private static Session session;


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

			check(session);


		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (session!=null)
				session.logout();
		}

	}

	private static void check(Session session) throws FileNotFoundException, IOException {
		try (BufferedReader br = new BufferedReader(new FileReader("/home/valentina/vreFolders.txt"))) {

			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				String[] array = line.split(",");
				String user = array[1];
				String vre = array[0];

				Node node;
				try {
					node = session.getNode("/Home/"+user+"/Workspace/MySpecialFolders/"+vre);
					System.out.println(node.getPath());
					node.remove();
					session.save();

				} catch (PathNotFoundException e) {
					e.printStackTrace();
				} catch (RepositoryException e) {
					e.printStackTrace();
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}


	}




}




