package org.gcube.dataanalysis.executor.scripts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.UUID;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.model.protocol.smp.Handler;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.slf4j.Logger;

public class ExecuteScript {

	private float status;
	private static String JOBOUTPUT = "job.txt";
	private static String CONFIGFILENAME = "config.dat";
	Logger logger;
	private static String genericWorkerDir = "/genericworker/";

	public float getStatus() {
		return status;
	}

	public ExecuteScript() {

	}

	public ExecuteScript(Logger logger) {
		this.logger = logger;
	}

	public void executeScript(List<String> localfileNames, List<String> remotefiles, String tempRootDir, String toExecute, String argument, String identifier, String scope, String serviceClass, String serviceName, String owner, String remoteDirectory, String session, String NodeConfiguration, boolean deletefiles) throws Exception {
		status = 0f;
		StringBuffer sb = new StringBuffer();
		File tempDir = null ;
		try {
			Handler.activateProtocol();
			String locDir = session;
			if (session == null)
				locDir = ("" + UUID.randomUUID()).replace("-", "");

			File workerDir = new File("." + genericWorkerDir);
			if (!workerDir.exists())
				workerDir.mkdir();

			String randomu = new File(tempRootDir).getAbsolutePath() + genericWorkerDir + locDir + "/";
			logger.debug("GenericWorker-> Creating local directory " + randomu);
			sb.append("Creating local directory " + randomu + "\n");
			tempDir = new File(randomu);
			boolean dirc = true;
			if (!tempDir.exists())
				dirc = tempDir.mkdir();
			if (dirc) {
				logger.debug("GenericWorker-> Retrieving files");
				sb.append("Retrieving files\n");
				int i = 0;
				for (String fileurl : remotefiles) {
					URL smpFile = new URL(fileurl);
					URLConnection uc = (URLConnection) smpFile.openConnection();
					InputStream is = uc.getInputStream();
					logger.debug("GenericWorker-> Retrieving from " + fileurl + " to :" + tempDir.getAbsolutePath() + "/" + localfileNames.get(i));
					sb.append("Retrieving from " + fileurl + " to :" + tempDir.getAbsolutePath() + "/" + localfileNames.get(i));
					inputStreamToFile(is, tempDir.getAbsolutePath() + "/" + localfileNames.get(i));
					is.close();
					i++;
				}
				if (NodeConfiguration != null) {
					// dump configuration file
					BufferedWriter oos = new BufferedWriter(new FileWriter(new File(tempDir,CONFIGFILENAME)));
					oos.write(NodeConfiguration);
					oos.close();
					// finished dumping file
				}

				logger.debug("GenericWorker-> Files Retrieved");
				sb.append("Files Retrieved\n");
				logger.debug("GenericWorker-> Executing script in " + System.getProperty("os.name"));
				sb.append("Executing script\n");
				String line = "";
				if (System.getProperty("os.name").startsWith("Windows"))
					line = new OSCommandGenericWorker().ExecuteGetLine("cmd /c cd " + tempDir.getAbsolutePath() + "\n " + toExecute + " " + argument + "\n exit\n", logger);
				else {
					line = new OSCommandGenericWorker().ExecuteGetLine("chmod +x " + tempDir.getAbsolutePath() + "/" + toExecute + "", logger);
					String arg = (argument +"_"+CONFIGFILENAME).replace(" ", "_");
					line = new OSCommandGenericWorker().ExecuteGetLine(tempDir.getAbsolutePath() + "/" + toExecute + " " + tempDir.getAbsolutePath() + "/ " +arg + "\nexit\n", logger);
				}
				if ((line != null) && (line.equals("ERROR")))
					throw new Exception("ERROR executing script");

				logger.debug("GenericWorker-> Script executed: " + line);
				sb.append("Script executed: " + line + "\n");
				/*
				 * Deprecated Write of log file String outputfile = randomu+JOBOUTPUT; BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputfile))); bw.write(sb.toString()); bw.flush(); bw.close(); logger.debug("GenericWorker-> writing back output"); sb.append("writing back output\n"); save2Storage(JOBOUTPUT,"_"+identifier,randomu,remoteDirectory,scope,serviceClass,serviceName,owner);
				 */

				logger.debug("GenericWorker-> all done");
			} else {
				logger.debug("GenericWorker-> Failed to create directory");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("GenericWorker-> ERROR: " + e.getLocalizedMessage());
			status = 100f;
			throw e;
		}
		finally{
			try{
			if (deletefiles && (tempDir!=null)) {
				logger.debug("GenericWorker-> ... deleting local files");
				// delete all after execution
				for (File singlefile : tempDir.listFiles()) {
					boolean del = singlefile.delete();
					if (!del)
						logger.debug("GenericWorker-> ERROR deleting " + singlefile.getName() + " " + del);
					else
						logger.debug("GenericWorker-> deleted LOCAL FILE " + singlefile.getName() + " " + del);
				}
				logger.debug("GenericWorker-> deleting temporary directory");
				tempDir.delete();
			}
			}catch(Exception e3){
				e3.printStackTrace();
				logger.debug("GenericWorker-> Error deleting files");
			}
			status = 100f;
		}
	}

	private void save2Storage(String filename, String suffix, String localdirectory, String remotedirectory, String scope$, String serviceClass, String serviceName, String owner) {
		try {
			// System.out.println("GenericWorker-> getting scope "+scope$);
			logger.debug("GenericWorker-> getting scope " + scope$);
//			GCUBEScope scope = GCUBEScope.getScope(scope$);
			ScopeProvider.instance.set(scope$);
			IClient client = new StorageClient(serviceClass, serviceName, owner, AccessType.SHARED).getClient();
			String localf = localdirectory + filename;
			String remotef = remotedirectory + filename;
			logger.debug("GenericWorker-> updating file " + localf + " to " + remotef);
			// FileInputStream fis = new FileInputStream(new File(localf));
			String id = client.put(true).LFile(localf).RFile(remotef);
			// fis.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("", e);
		}
	}

	private static void inputStreamToFile(InputStream is, String path) throws FileNotFoundException, IOException {
		FileOutputStream out = new FileOutputStream(new File(path));
		byte buf[] = new byte[1024];
		int len = 0;
		while ((len = is.read(buf)) > 0)
			out.write(buf, 0, len);
		out.close();
	}
}
