package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalFile;
import org.gcube.common.scope.api.ScopeProvider;

import com.thoughtworks.xstream.XStream;

public class STEP2 {
	static JCRWorkspace ws = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, InterruptedException {

		WorkspaceFolder folder = null;
		try {

			ScopeProvider.instance.set("/gcube/preprod/preVRE");

			String file = "check.txt";

			reader(file);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	private static void reader(String csvFile) throws ItemNotFoundException, org.gcube.common.homelibrary.model.exceptions.RepositoryException, InternalErrorException, WorkspaceFolderNotFoundException, HomeNotFoundException, UserNotFoundException, IOException {
		BufferedReader br = null;
		BufferedWriter bw = null;
		String line = "";
		String cvsSplitBy = "\t";
		XStream xstream = new XStream();
		try {
			ClassLoader classLoader = STEP2.class.getClassLoader();
			File file = new File(classLoader.getResource("check.txt").getFile());

			br = new BufferedReader(new FileReader(file.getAbsolutePath()));
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("check-new.txt"), "utf-8"));

			while ((line = br.readLine()) != null) {
				String[] shareLine = line.split(cvsSplitBy);
				String id = shareLine[0];
				String path = shareLine[1];
				String owner = shareLine[2];
//				System.out.println(shareLine[2]);
//				String users = shareLine[2];
//				System.out.println("Share [path= " +  path + " , owner=" + owner + " , users=" + users + "]");
				System.out.println("Share [path= " +  path + " , owner=" + owner  + "]");

				HomeManager manager = HomeLibrary.getHomeManagerFactory().getHomeManager();
				ws = (JCRWorkspace) manager.getHome(owner).getWorkspace();
				System.out.println(ws.getRoot().getPath());
				WorkspaceSharedFolder shared = (WorkspaceSharedFolder) ws.getItemByAbsPath(path);
				Map<ACLType, List<String>> aclMap = shared.getACLOwner();
				System.out.println(aclMap.toString());


//				bw.write(shared.getId()+ "\t" +path + "\t" + owner + "\t" + shared.isVreFolder() + "\t" + shareLine[2] + "\t"  +aclMap);
				bw.write(shared.getId()+ "\t" +path + "\t" + owner + "\t" + shared.isVreFolder() + "\t"  +aclMap);
				bw.write("\n");
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null)              
				br.close();        

			if (bw!=null)
				bw.close();
		}

	}


	public static String getSecurePassword(String message) throws InternalErrorException {
		String digest = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] hash = md.digest(message.getBytes("UTF-8"));

			//converting byte array to Hexadecimal String
			StringBuilder sb = new StringBuilder(2*hash.length);
			for(byte b : hash){
				sb.append(String.format("%02x", b&0xff));
			}
			digest = sb.toString();

		} catch (UnsupportedEncodingException e) {
			throw new InternalErrorException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new InternalErrorException(e);
		}
		return digest;
	}








}
