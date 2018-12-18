package org.gcube.dataanalysis.executor.job.management;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.gcube.contentmanager.storageclient.model.protocol.smp.Handler;
import org.gcube.dataanalysis.ecoengine.interfaces.ActorNode;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalInfraAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericWorker extends StandardLocalInfraAlgorithm{

	private static String genericWorkerDir = "/genericworker/";

	private static Logger logger = LoggerFactory.getLogger(GenericWorker.class);

	public static String AlgorithmClassParameter = "AlgorithmClass";
	public static String RightSetStartIndexParameter = "RightSetStartIndex";
	public static String NumberOfRightElementsToProcessParameter = "NumberOfRightElementsToProcess";
	public static String LeftSetStartIndexParameter = "LeftSetStartIndex";
	public static String NumberOfLeftElementsToProcessParameter = "NumberOfLeftElementsToProcess";
	public static String IsDuplicateParameter = "IsDuplicate";
	public static String SessionParameter = "Session";
	public static String ConfigurationFileParameter = "ConfigurationFile";
	public static String DeleteTemporaryFilesParameter = "DeleteTemporaryFiles";

	public static String OutputParameter = "Process_Outcome";
	public static String TASK_SUCCESS = "TASK_SUCCESS";
	public static String TASK_FAILURE = "TASK_FAILURE";
	public static String TASK_UNDEFINED = "TASK_UNDEFINED";

	private static void inputStreamToFile(InputStream is, String path) throws FileNotFoundException, IOException {
		FileOutputStream out = new FileOutputStream(new File(path));
		byte buf[] = new byte[1024];
		int len = 0;
		while ((len = is.read(buf)) > 0)
			out.write(buf, 0, len);
		out.close();
	}

	public void executeAlgorithm(String algorithmClass, 
			int rightStartIndex, 
			int numberOfRightElementsToProcess, 
			int leftStartIndex, 
			int numberOfLeftElementsToProcess, 
			boolean isduplicate, 
			String session,
			File nodeConfigurationFileObject,
			boolean deleteFiles) throws Exception {

		PrintStream origOut = System.out;
		PrintStream origErr = System.err;
		status = 0f;
		StringBuffer sb = new StringBuffer();
		File tempDir = null ;
		try {
			Handler.activateProtocol();
			// invoke the algorithm
			logger.debug("GenericWorker-> Creating algorithm " + algorithmClass);
			ActorNode node = (ActorNode) Class.forName(algorithmClass).newInstance();
			logger.debug("GenericWorker-> executing algorithm " + algorithmClass +" with parameters:");
			logger.debug("GenericWorker-> rightStartIndex:" +rightStartIndex);
			logger.debug("GenericWorker-> numberOfRightElementsToProcess:" +numberOfRightElementsToProcess);
			logger.debug("GenericWorker-> leftStartIndex:" +leftStartIndex);
			logger.debug("GenericWorker-> numberOfLeftElementsToProcess:" +numberOfLeftElementsToProcess);
			logger.debug("GenericWorker-> isduplicate:" +isduplicate);
			logger.debug("GenericWorker-> execution directory:" +config.getConfigPath());
			logger.debug("GenericWorker-> nodeConfigurationFileObject.getName():" +nodeConfigurationFileObject.getName());
			logger.debug("GenericWorker-> nodeConfigurationFileObject.getPath():" +nodeConfigurationFileObject.getAbsolutePath());

			logger.debug("GenericWorker-> session :" +session);
			logger.debug("GenericWorker-> delete files :" +deleteFiles);

			File sandboxfile = new File(config.getConfigPath(),nodeConfigurationFileObject.getName());

			Files.copy(nodeConfigurationFileObject.toPath(), sandboxfile.toPath(), REPLACE_EXISTING);

			logger.debug("GenericWorker-> copied configuration file as " +sandboxfile.getAbsolutePath());

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);

			System.setOut(ps);
			System.setErr(ps);
			int result = node.executeNode(leftStartIndex, numberOfLeftElementsToProcess, rightStartIndex, numberOfRightElementsToProcess, isduplicate, 
					config.getConfigPath(), nodeConfigurationFileObject.getName(), "log.txt");
			System.setOut(origOut);
			System.setErr(origErr);

			String log = new String(baos.toByteArray(), StandardCharsets.UTF_8);
			//manage known issues
			/*
				log=log.replace(".XMLStreamException: Unbound namespace URI", "Known Except");
				log=log.replace("java.io.IOException: Error copying XML", "Known Except");
				log=log.replace("java.io.FileNotFoundException: /home/gcube/tomcat/tmp/ConfigurationFile", "Known Except");
				log=log.replace("java.io.FileNotFoundException: payload was not made available for this dataset", "Known Except");

				logger.debug("GenericWorker-> Execution Fulllog" );
				logger.debug("GenericWorker-> " + log);
				logger.debug("GenericWorker-> Script executed! " );
			 */

			boolean del = sandboxfile.delete();
			logger.debug("GenericWorker-> deleted sandbox file: "+del );
			logger.debug("GenericWorker-> all done");


			//if (log.contains("Exception:")){
			if (result!= 0){
				outputParameters.put(OutputParameter, TASK_FAILURE);
				String cutLog = URLEncoder.encode(log, "UTF-8");
				/*
					int maxlen = 20240;

					if (log.length()>maxlen)
						cutLog = cutLog.substring(0,maxlen)+"...";
				 */
				cutLog = log;
				outputParameters.put("Log", cutLog);
				logger.debug("GenericWorker-> Failure!");
			}
			else{
				outputParameters.put(OutputParameter, TASK_SUCCESS);
				logger.debug("GenericWorker-> Success!");
			}
		} catch (Throwable e) {
			outputParameters.put(OutputParameter, TASK_FAILURE);
			outputParameters.put("Log", e.getLocalizedMessage());
			logger.error("GenericWorker-> ERROR: " ,e);
			status = 100f;
			throw new Exception(e.getLocalizedMessage());
		}
		finally{
			System.setOut(origOut);
			System.setErr(origErr);
			try{
				if (deleteFiles && (tempDir!=null)) {
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
				if (nodeConfigurationFileObject!=null && nodeConfigurationFileObject.exists())
					nodeConfigurationFileObject.delete();

			}catch(Exception e3){
				logger.warn("GenericWorker-> Error deleting files",e3);
			}
			status = 100f;
		}
	}

	@Override
	public void init() throws Exception {


	}

	@Override
	public String getDescription() {
		return "An algorithm that executes another other algorithm";
	}

	@Override
	protected void process() throws Exception {
		logger.debug("Parameters: "+config.getGeneralProperties());

		String algorithmClass = config.getParam(AlgorithmClassParameter);

		int rightStartIndex =  Integer.parseInt(config.getParam(RightSetStartIndexParameter));
		int numberOfRightElementsToProcess =Integer.parseInt(config.getParam(NumberOfRightElementsToProcessParameter)); 
		int leftStartIndex =Integer.parseInt(config.getParam(LeftSetStartIndexParameter)); 
		int numberOfLeftElementsToProcess =Integer.parseInt(config.getParam(NumberOfLeftElementsToProcessParameter)); 
		boolean isduplicate=Boolean.parseBoolean(config.getParam(IsDuplicateParameter)); 
		String session=config.getParam(SessionParameter);
		File nodeConfigurationFileObject=new File (config.getParam(ConfigurationFileParameter));
		boolean deleteFiles= Boolean.parseBoolean(config.getParam(DeleteTemporaryFilesParameter));

		logger.debug("Executing the algorithm");
		executeAlgorithm(algorithmClass, rightStartIndex, numberOfRightElementsToProcess, leftStartIndex, numberOfLeftElementsToProcess, isduplicate, session, nodeConfigurationFileObject, deleteFiles);
		logger.debug("Algorithm executed!");

	}

	@Override
	protected void setInputParameters() {

		addStringInput(AlgorithmClassParameter, "The full class path of the algorithm", "org.gcube.dataanalysis.executor.nodes.transducers.bionym.BionymFlexibleWorkflowTransducer");
		addIntegerInput(RightSetStartIndexParameter, "The start index of the right set in a cartesian product of the input", "1");
		addIntegerInput(NumberOfRightElementsToProcessParameter, "The number of elements to process in the right set", "1");
		addIntegerInput(LeftSetStartIndexParameter, "The start index of the left set in a cartesian product of the input", "1");
		addIntegerInput(NumberOfLeftElementsToProcessParameter, "The number of elements to process in the left set", "1");
		addBooleanInput(IsDuplicateParameter, "Indicate if this sub computation is a duplicate of another sub-computation", "false");
		addStringInput(SessionParameter, "The session this sub-computation belongs to", "123456");
		addFileInput(ConfigurationFileParameter, "A configuration file for the algorithm in an XML serialisation format for the AlgorithmConfiguration Object", "config.dat");
		addBooleanInput(DeleteTemporaryFilesParameter,"Delete local temporary files after the computation","true");
	}

	@Override
	public void shutdown() {

	}

}
