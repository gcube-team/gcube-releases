package org.gcube.datatransformation.datatransformationlibrary.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dimitris Katris, NKUA
 * 
 * Utility class which facilitates the execution of command line programs. 
 */
public class CLIUtils {

	private static Logger log = LoggerFactory.getLogger(CLIUtils.class);

	/**
	 * Executes a command in the underlying shell. 
	 * 
	 * @param command The command which will be executed.
	 * @return The return code of the executed command.
	 * @throws Exception If the command could not be performed.
	 */
	public static int executeCommand(String command) throws Exception {
		log.debug("Going to execute command: "+command);
		try {
			final Process process = Runtime.getRuntime().exec(command);
			new Thread() {
				public void run() {
					try {
						InputStream is = process.getInputStream();
						BufferedReader bufIn = new BufferedReader(new InputStreamReader(is));
						String line = null;
						while ( (line = bufIn.readLine()) != null) {
							log.debug(line);
						}		
						bufIn.close();
					} catch (Exception e) {
						log.error("Exception in reading stdin of cli command", e);
					}
				}
			}.start();
			new Thread() {
				public void run() {
					try {
						InputStream is = process.getErrorStream();
						BufferedReader bufIn = new BufferedReader(new InputStreamReader(is));
						String line = null;
						while ( (line = bufIn.readLine()) != null) {
							log.debug(line);
						}		
						bufIn.close();
					} catch (Exception e) {
						log.error("Exception in reading stderr of cli command", e);
					}
				}
			}.start();

			int returnCode = process.waitFor();
			if(returnCode!=0){
				log.warn("CL program execution failed with return code: "+ returnCode);
//				throw new Exception("CL program execution failed with return code: "+ returnCode);
			}else{
				log.debug("CL program execution succeded.");
			}
			return returnCode;
		} catch (Exception e) {
			log.error("Exception in executing cli command: "+command, e);
			throw new Exception("Exception in executing cli command: "+command, e);
		}
	}

	/**
	 * Executes a command in the underlying shell. 
	 * 
	 * @param command The command which will be executed.
	 * @param input The input stream that should be piped to command execution.
	 * @return The return code of the executed command.
	 * @throws Exception If the command could not be performed.
	 */
	public static int executeCommand(String command, final InputStream input) throws Exception {

		log.debug("Going to execute command: "+command);
		try {
			final Process process = Runtime.getRuntime().exec(command);
			new Thread() {
				public void run() {
					try {
						InputStream is = process.getInputStream();
						BufferedReader bufIn = new BufferedReader(new InputStreamReader(is));
						String line = null;
						while ( (line = bufIn.readLine()) != null) {
							log.debug(line);
						}		
						bufIn.close();
					} catch (Exception e) {
						log.error("Exception in reading stdin of cli command", e);
					}
				}
			}.start();
			new Thread() {
				public void run() {
					try {
						InputStream is = process.getErrorStream();
						BufferedReader bufIn = new BufferedReader(new InputStreamReader(is));
						String line = null;
						while ( (line = bufIn.readLine()) != null) {
							log.debug(line);
						}		
						bufIn.close();
					} catch (Exception e) {
						log.error("Exception in reading stderr of cli command", e);
					}
				}
			}.start();
			
			new Thread() {
				public void run() {
					try {
						copyStream(input, process.getOutputStream());
					} catch (Exception e) {
						log.error("Exception in reading stderr of cli command", e);
					}
				}
			}.start();

			
			int returnCode = process.waitFor();
			if(returnCode!=0){
				log.warn("CL program execution failed with return code: "+ returnCode);
//				throw new Exception("CL program execution failed with return code: "+ returnCode);
			}else{
				log.debug("CL program execution succeded.");
			}
			return returnCode;
		} catch (Exception e) {
			log.error("Exception in executing cli command: "+command, e);
			throw new Exception("Exception in executing cli command: "+command, e);
		}
	}
	
	public static void copyStream(InputStream input, OutputStream output)
		    throws IOException
		{
		    byte[] buffer = new byte[1024];
		    int bytesRead;
		    while ((bytesRead = input.read(buffer)) != -1)
		    {
		        output.write(buffer, 0, bytesRead);
		    }
		    
		    input.close();
		    output.flush();
		    output.close();
		}
}
