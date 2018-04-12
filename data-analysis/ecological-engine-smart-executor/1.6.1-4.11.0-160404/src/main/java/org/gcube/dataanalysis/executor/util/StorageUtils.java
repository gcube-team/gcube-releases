package org.gcube.dataanalysis.executor.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanager.storageclient.model.protocol.smp.Handler;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;

public class StorageUtils {

	
	public static void downloadInputFile(String fileurl, String destinationFile) throws Exception{
		downloadInputFile(fileurl, destinationFile, false);
	}
	
	public static void downloadInputFile(String fileurl, String destinationFile, boolean httpURL) throws Exception{
		try {
			if (!httpURL || !fileurl.toLowerCase().startsWith("http:"))
				Handler.activateProtocol();
			URL smpFile = new URL(fileurl);
			URLConnection uc = (URLConnection) smpFile.openConnection();
			InputStream is = uc.getInputStream();
			AnalysisLogger.getLogger().debug("Retrieving from " + fileurl + " to: " + destinationFile);
			inputStreamToFile(is, destinationFile);
			is.close();
		} catch (Exception e) {
			throw e;
		}
	}

	public static void inputStreamToFile(InputStream is, String path) throws FileNotFoundException, IOException {
		FileOutputStream out = new FileOutputStream(new File(path));
		byte buf[] = new byte[1024];
		int len = 0;
		while ((len = is.read(buf)) > 0)
			out.write(buf, 0, len);
		out.close();
	}

	public static String uploadFilesOnStorage(String scope, String user, String localFolder, String remoteFolder, String file, boolean httplink) throws Exception {
		try {
//			ScopeProvider.instance.set(scope);
			AnalysisLogger.getLogger().info("Loading file on scope: " + scope);
			IClient client = new StorageClient(AlgorithmConfiguration.StatisticalManagerClass, AlgorithmConfiguration.StatisticalManagerService, user, AccessType.SHARED, MemoryType.VOLATILE).getClient();
			String remotef = remoteFolder+file.replace(" ","%20");
			client.put(true).LFile(new File(localFolder,file).getAbsolutePath()).RFile(remotef);
			String url = "";
			if (httplink)
				url = client.getHttpUrl().RFile(remotef);
			else
				url = client.getUrl().RFile(remotef);
			try{client.close();}catch(Exception e){}
			AnalysisLogger.getLogger().info("Loading finished");
			System.gc();
			return url;
		} catch (Exception e) {
			AnalysisLogger.getLogger().info("Error in uploading file: " + e.getLocalizedMessage());
			throw e;
		}
	}
	
	public static String uploadFilesOnStorage(String scope, String user, String localFolder, String file) throws Exception {
		try {
//			ScopeProvider.instance.set(scope);
			AnalysisLogger.getLogger().info("Loading file on scope: " + scope);
			IClient client = new StorageClient(AlgorithmConfiguration.StatisticalManagerClass, AlgorithmConfiguration.StatisticalManagerService, user, AccessType.SHARED, MemoryType.VOLATILE).getClient();
			String remotef = "/"+file;
			client.put(true).LFile(new File(localFolder,file).getAbsolutePath()).RFile(remotef);
			String url = client.getUrl().RFile(remotef);
			AnalysisLogger.getLogger().info("Loading finished");
			System.gc();
			return url;
		} catch (Exception e) {
			AnalysisLogger.getLogger().info("Error in uploading file: " + e.getLocalizedMessage());
			throw e;
		}
	}

	public static int calcFileRows(File file, boolean hasheader){
		try{
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			if (hasheader)
				line = br.readLine(); // skip header
			int counter = 0;
			while (line!=null){
				counter++;
				line = br.readLine();
			}
			br.close();
			return counter;
		}catch(Exception e){
			e.printStackTrace();
			return 0;
		}
		
	}
	
	
	public static void FileSubset(File infile, File outfile, int index, int numberofelements, boolean hasheader) throws Exception{
		
			BufferedReader br = new BufferedReader(new FileReader(infile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
			
			String header = null;
			if (hasheader){
				header = br.readLine(); // skip header
				bw.write(header+"\n");
			}
			String line = br.readLine();
			int counter = 0;
			while (line!=null && counter < (index+numberofelements)){
				
				if (counter>=index)
					bw.write(line+"\n");
				
				counter++;
				line = br.readLine();
			}
			
			br.close();
			bw.close();
		
	}
	
	
	public static void downloadFilefromStorage(String scope, String user, String localFolder, String file) throws Exception {
		try {
//			ScopeProvider.instance.set(scope);
			AnalysisLogger.getLogger().info("Retrieving file on scope: " + scope);
			IClient client = new StorageClient(AlgorithmConfiguration.StatisticalManagerClass, AlgorithmConfiguration.StatisticalManagerService, user, AccessType.SHARED, MemoryType.VOLATILE).getClient();
			String remotef = "/"+file;
			client.get().LFile(new File(localFolder,file).getAbsolutePath()).RFile(remotef);
			AnalysisLogger.getLogger().info("Retrieving finished");
			System.gc();
		} catch (Exception e) {
			AnalysisLogger.getLogger().info("Error in retrieving file: " + e.getLocalizedMessage());
			throw e;
		}
	}

	public static void mergeFiles(String localFolder, List<String> filenames, String outputfile, boolean hasheader) throws Exception {
		try {
			
			int nfiles = filenames.size();
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(localFolder,outputfile)));
			for (int i=0;i<nfiles;i++){
				BufferedReader br = new BufferedReader(new FileReader(new File(localFolder,filenames.get(i))));
				String header = null;
				if (hasheader && i==0){
					header = br.readLine();
					bw.write(header+"\n");
				}
				else if (hasheader)
					br.readLine();
				
				String line = br.readLine();
				while (line!=null){
					bw.write(line+"\n");
					line = br.readLine();
				}
				
				br.close();
			}
			
			
			bw.close();
			
			
			
		} catch (Exception e) {
			AnalysisLogger.getLogger().info("Error in merging files: " + e.getLocalizedMessage());
			throw e;
		}
	}
	
	
	
	public static void main(String args[]) throws Exception{
		
//		uploadFilesOnStorage("/gcube", "CMSY", "./", "tacsat.csv");
//		downloadFilefromStorage("/gcube", "CMSY", "./PARALLEL_PROCESSING", "tacsat.csv");
		FileSubset(new File("D20_1.csv"), new File("D20_11.csv"), 3, 10, true);
	}
}
