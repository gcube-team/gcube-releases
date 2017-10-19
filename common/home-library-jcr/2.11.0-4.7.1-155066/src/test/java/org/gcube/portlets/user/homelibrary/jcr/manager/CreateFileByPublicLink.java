package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.common.scope.api.ScopeProvider;

public class CreateFileByPublicLink {

	public static void main(String[] args) throws InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, org.gcube.common.homelibrary.model.exceptions.RepositoryException, InternalErrorException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException, InterruptedException {
		unShareTest();

	}

	
	public static void unShareTest() throws InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException, InterruptedException, org.gcube.common.homelibrary.model.exceptions.RepositoryException {

		JCRWorkspace ws = (JCRWorkspace) getWorkspace("valentina.marioli");
		
		
		URL url = new URL("http://goo.gl/1ypy6O");   
	    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();   
	    try 
	    {     
	        InputStream in = new BufferedInputStream(urlConnection.getInputStream());     
	    	FolderItem file = WorkspaceUtil.createExternalFile(ws.getRoot(), "test-gpoo.zip", "data.description", in, null,"zip",0);
	    	
	    	System.out.println(file.getPath());
	    }
	    finally 
	    {     
	        urlConnection.disconnect();   
	    } 
	    
	

	}

//	private static void uploadFile(WorkspaceSharedFolder subFolder) throws InsufficientPrivilegesException, ItemAlreadyExistException, InternalErrorException, IOException {
//		String fileName = "img-" + UUID.randomUUID()+ ".jpg";
//		InputStream is = null;
//
//		try {
//			is = new FileInputStream("/home/valentina/Downloads/4737062744_9dd84a2df2_z.jpg");
//			ExternalFile f = subFolder.createExternalImageItem(fileName, "test", "image/jpg", is);
//			System.out.println(f.getPath());
//
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}finally{
//			if (is!=null)
//				is.close();
//		}
//		
//	}


	private static Workspace getWorkspace(String user) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException {
		ScopeProvider.instance.set("/gcube");
//									ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		Workspace ws = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome(user).getWorkspace();

		return ws;

	}
	
//	
//    public String uploadData(StoredData data, WorkspaceFolder wsFolder, boolean changename) throws Exception {
//		AnalysisLogger.getLogger().debug("Dataspace->Analysing " + data.name);
//		// String filenameonwsString = WorkspaceUtil.getUniqueName(data.name, wsFolder);
//		String filenameonwsString = data.name ;
//		if (changename){
//			filenameonwsString = data.name + "_[" + data.computationId + "]";// ("_"+UUID.randomUUID()).replace("-", "");
//			if (data.type.equals("text/csv"))
//				filenameonwsString+=".csv";
//			else if (data.type.equals("image/png"))
//				filenameonwsString+=".png";
//		}
//		InputStream in = null;
//		String url = "";
//		try {
//			long size = 0;
//			if (data.type.equals("text/csv")||data.type.equals("application/d4science")||data.type.equals("image/png")) {
// 
//				if (new File(data.payload).exists() || !data.payload.startsWith("http")) {
//					AnalysisLogger.getLogger().debug("Dataspace->Uploading file " + data.payload);
//					in = new FileInputStream(new File(data.payload));
//					size = new File(data.payload).length();
//				} else {
//					AnalysisLogger.getLogger().debug("Dataspace->Uploading via URL " + data.payload);
//					int tries = 10;
//					for (int i=0;i<tries;i++){
//						try {
//							URL urlc = new URL(data.payload);
//							HttpURLConnection urlConnection = (HttpURLConnection) urlc.openConnection();
//							urlConnection.setConnectTimeout(10000);
//							urlConnection.setReadTimeout(10000);
//							in = new BufferedInputStream(urlConnection.getInputStream());
//						}catch(Exception ee){
//							AnalysisLogger.getLogger().debug(ee);
//							AnalysisLogger.getLogger().debug("Dataspace->Retrying connection to "+data.payload+" number "+(i+1));
//							in =null;
//						}
//						if (in!=null)
//							break;
//						else
//							Thread.sleep(10000);
//					}
// 
//				}
//				if (in==null)
//					throw new Exception("Impossible to open stream from "+data.payload);
// 
//				// AnalysisLogger.getLogger().debug("Dataspace->final file name on ws " + data.name+" description "+data.description);
//				AnalysisLogger.getLogger().debug("Dataspace->WS OP saving the following file on the WS " + filenameonwsString);
//				LinkedHashMap<String, String> properties = new LinkedHashMap<String, String>();
// 
//				properties.put(computation_id, data.computationId);
//				properties.put(vre, data.vre);
//				properties.put(creation_date, data.creationDate);
//				properties.put(operator, data.operator);
//				properties.put(data_id, data.id);
//				properties.put(data_description, data.description);
//				properties.put(IO, data.provenance.name());
//				properties.put(data_type, data.type);
//				properties.put(payload, url);
// 
//				FolderItem fileItem = WorkspaceUtil.createExternalFile(wsFolder, filenameonwsString, data.description, in,properties,data.type,size);
//				//fileItem.getProperties().addProperties(properties);
//				AnalysisLogger.getLogger().debug("Dataspace->WS OP file saved on the WS " + filenameonwsString);
// 
//				url = fileItem.getPublicLink(true);
//				AnalysisLogger.getLogger().debug("Dataspace->WS OP url produced for the file " + url);
// 
//				data.payload = url;
//				try {
//					in.close();
//				} catch (Exception e) {
//					AnalysisLogger.getLogger().debug("Dataspace->Error creating file " + e.getMessage());
//					AnalysisLogger.getLogger().debug(e);
//				}
//				AnalysisLogger.getLogger().debug("Dataspace->File created " + filenameonwsString);
//			} else {
//				AnalysisLogger.getLogger().debug("Dataspace->String parameter " + data.payload);
//				url = data.payload;
//			}
//		} catch (Throwable e) {
//			e.printStackTrace();
//			AnalysisLogger.getLogger().debug("Dataspace->Could not retrieve input payload " + data.payload+" - "+e.getLocalizedMessage());
//			AnalysisLogger.getLogger().debug(e);
//			url = "payload was not made available for this dataset";
//			data.payload = url;
//		}
//		return url;
//	}
	
}
