package org.gcube.dataanalysis.executor.scripts;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;

import org.slf4j.Logger;

public class OSCommandGenericWorker {

	
	 Boolean aborted = false;
	 Boolean success = false;
 String errorReported = "";
	
	 
	 public String ExecuteGetLine(String cmd, final Logger logger) throws Exception{
			
		 Process process = null;
		 String lastline = "";
		 class ErrorController implements Runnable {
			 BufferedReader reader;
			 	public ErrorController(BufferedReader reader) {
					 	this.reader=reader;	
				}
				public void run() {
					try {
						String errorLine = reader.readLine();
						while (!aborted && !success){
						if ((errorLine!=null) && (errorLine.length()>0) && !errorLine.contains("WARN") && !errorLine.contains("warn")){ 
							 aborted = true;
							 logger.debug("GenericWorker-> OSCommand-> error line->"+errorLine);
							 errorReported= errorLine;
							 break;
						 }
						/*
						else{
							 logger.debug("GenericWorker-> OSCommand-> No Errors - Success? "+success);
						}
						*/
						 Thread.sleep(3000);
						}
						logger.debug("GenericWorker-> OSCommand-> Closing Error Watcher");
					} catch (Exception e) {
					}
				}

			}
		 ErrorController econtrol =null;
		 
		 try {
			 logger.debug("GenericWorker-> OSCommand-> Executing Control ->"+cmd);
			  
			 process = Runtime.getRuntime().exec(cmd);
			 
			 BufferedReader berror = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			 
			 econtrol = new ErrorController(berror);
			 Thread t = new Thread(econtrol);
			 t.start();
			 
			 BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			 String line = br.readLine();
			 logger.debug("GenericWorker-> OSCommand->  line->"+line);
			  while ((line!=null)&&(!aborted)){
				 try{
				 lastline = line;
				 logger.debug("GenericWorker-> OSCommand-> line->"+line);
				 line = br.readLine();
				 
				 }catch(EOFException e){
					 logger.debug("GenericWorker-> OSCommand -> Process Finished with EOF");
					 break;
				 }
				 catch(Exception e){
					 success=false;
					 line = "ERROR";
					 break;
				 }
			 }
			 
			  if (aborted){
				 logger.debug("GenericWorker-> OSCommand -> Process was aborted due to an Error");
				 
				 throw new Exception("Internal Error in script execution: "+errorReported);
			 }
			 else if (line==null){
				 success=true;
				 
				 logger.debug("GenericWorker-> OSCommand -> Success "+success);
			 }
			  
			 logger.debug("GenericWorker-> OSCommand -> Process Finished");
		} catch (Exception e) {
			logger.debug("GenericWorker-> OSCommand-> Error "+e.getMessage());
			logger.debug("GenericWorker-> OSCommand-> Error from Controller "+errorReported);
//			 e.printStackTrace();
			 lastline = "ERROR";
			 aborted=true;
			 throw e;
		}finally{
			if (process!=null)
				process.destroy();
		 logger.debug("GenericWorker-> OSCommand-> Process destroyed ");
		}
		 return lastline;
	 } 
	 
	 public static boolean FileCopy (String origin,String destination){
		 try{
			 
		 File inputFile = new File(origin);
		 System.out.println("GenericWorker-> OSCommand-> FileCopy-> "+inputFile.length()+" to "+inputFile.canRead());
		 int counterrors=0;
		 while ((inputFile.length()==0)&&(counterrors<10)){
			 Thread.sleep(20);
			 counterrors++;
		 }
		 
		    File outputFile = new File(destination);

		    FileReader in = new FileReader(inputFile);
		    FileWriter out = new FileWriter(outputFile);
		    int c;

		    while ((c = in.read()) != -1)
		      out.write(c);

		    in.close();
		    out.close();
		    return true;
		 }catch(Exception e){
			 e.printStackTrace();
			 return false;
		 }
	 }
}
