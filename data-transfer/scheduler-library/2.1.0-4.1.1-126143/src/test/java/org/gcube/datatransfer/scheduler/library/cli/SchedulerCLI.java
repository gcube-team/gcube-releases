package org.gcube.datatransfer.scheduler.library.cli;

import static org.gcube.datatransfer.scheduler.library.proxies.Proxies.transferScheduler;
import static org.gcube.datatransfer.scheduler.library.proxies.Proxies.transferBinder;
import static org.gcube.datatransfer.scheduler.library.proxies.Proxies.transferManagement;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.scheduler.library.BinderLibrary;
import org.gcube.datatransfer.scheduler.library.ManagementLibrary;
import org.gcube.datatransfer.scheduler.library.SchedulerLibrary;
import org.gcube.datatransfer.scheduler.library.obj.SchedulerObj;
import org.gcube.datatransfer.scheduler.library.outcome.CallingManagementResult;
import org.gcube.datatransfer.scheduler.library.outcome.CallingSchedulerResult;
import org.gcube.datatransfer.scheduler.library.outcome.TransferInfo;
import org.gcube.datatransfer.scheduler.library.outcome.TransferObjectInfo;

import com.thoughtworks.xstream.XStream;


public class SchedulerCLI {
	static SchedulerLibrary schedulerLibrary = null;
	
	// ANSI escape codes (works for UNIX shell prompts)
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		

	    // create the parser
	    CommandLineParser parser;
	    CommandLine line=null;
	    BufferedReader br = new BufferedReader (new InputStreamReader(System.in));
	    String[] tempArgs = args;
	    String tempLine;
		Options options;
		String scope = null;
		String resource = null;
		
	    do{
			OptionBuilder.hasArgs(3);
			OptionBuilder.withArgName("scope - resource - fileName");
			OptionBuilder.withValueSeparator();
			OptionBuilder.withLongOpt( "schedule" );
			OptionBuilder.withDescription("schedule a transfer");
			Option schedule = OptionBuilder.create( "s");
			
			OptionBuilder.hasArgs(4);
			OptionBuilder.withArgName("scope - resource - transferId - force");
			OptionBuilder.withValueSeparator();
			OptionBuilder.withLongOpt( "cancel" );
			OptionBuilder.withDescription("cancel a transfer");
			Option cancel = OptionBuilder.create( "c");
			
			OptionBuilder.hasArgs(3);
			OptionBuilder.withArgName("scope - resource - transferId");
			OptionBuilder.withValueSeparator();
			OptionBuilder.withLongOpt( "monitor" );
			OptionBuilder.withDescription("monitor a transfer");
			Option monitor = OptionBuilder.create( "m");
			
			OptionBuilder.hasArgs(3);
			OptionBuilder.withArgName("scope - resource - transferId");
			OptionBuilder.withValueSeparator();
			OptionBuilder.withLongOpt( "getOutcomes" );
			OptionBuilder.withDescription("getOutcomes of a transfer");
			Option getOutcomes = OptionBuilder.create( "gout");
				
			OptionBuilder.hasArgs(2);
			OptionBuilder.withArgName("scope - (resource)");
			OptionBuilder.withValueSeparator();
			OptionBuilder.withLongOpt( "getTransfers" );
	        OptionBuilder.withDescription( "get the existed transfers in db assigned by a specific submitter (leave resource empty to get all the transfers)" );
	        Option getTransfers = OptionBuilder.create("gt");
	        
			OptionBuilder.withLongOpt( "help" );
			OptionBuilder.withDescription( "print this message" );	        
	        Option help = OptionBuilder.create("h");
	        
			OptionBuilder.withLongOpt( "exit" );      
			OptionBuilder.withDescription( "exit interactive CLI" );      
	        Option exit = OptionBuilder.create("e");
	        
			options = new Options();
			options.addOption( schedule );
			options.addOption( cancel );
			options.addOption( monitor );
			options.addOption( getOutcomes );
			options.addOption( getTransfers );
			options.addOption( help );
			options.addOption( exit );
		    parser = new GnuParser();
		try {
			// parse the command line arguments
			line = parser.parse( options, tempArgs );
		}
		catch( ParseException exp ) {
			// oops, something went wrong
			System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
			System.err.println( "try 'SchedulerCLI -help' for more information");
			return;
		}
		
			
		if( line.hasOption( "s" ) ) {
			// Initialize the member variable
			String[] scheduleValues = line.getOptionValues( "s" );
			if(scheduleValues.length<3){
				System.err.println( "Parsing failed.  Reason: schedule command needs three arguments");
				System.err.println( "try 'SchedulerCLI -help' for more information");
				return;
			}
			
			if(scheduleValues[0].compareTo("same")!=0)scope=scheduleValues[0];
			if(scheduleValues[1].compareTo("same")!=0)resource=scheduleValues[1];
			
			if(scope!=null&&resource!=null)retrieveSchedulerLibrary(scope,resource);
			else{
				System.err.println( "Parsing failed.  Reason: scope or(and) resource have not be given or have not been initialized");
				System.err.println( "try 'SchedulerCLI -help' for more information");
				return;
			}
			
			if(schedule(scheduleValues[2])==false){
				System.err.println( "Parsing failed.");
				System.err.println( "try 'SchedulerCLI -help' for more information");
				return;
			}
		}
		else if( line.hasOption( "c" ) ) {
			boolean force=false;
			// Initialize the member variable
			String[] cancelValues = line.getOptionValues("c");
			if(cancelValues.length<4){
				System.err.println( "Parsing failed.  Reason: cancel command needs four arguments");
				System.err.println( "try 'SchedulerCLI -help' for more information");
				return;
			}

			if(cancelValues[3].compareToIgnoreCase("true")==0)force=true;
			else if (cancelValues[3].compareToIgnoreCase("false")==0)force=false;
			else{
				System.err.println( "Parsing failed.  Reason: cancel command needs the argument force (true/false)");
				System.err.println( "try 'SchedulerCLI -help' for more information");
				return;
			}
			
			if(cancelValues[0].compareTo("same")!=0)scope=cancelValues[0];
			if(cancelValues[1].compareTo("same")!=0)resource=cancelValues[1];
			
			if(scope!=null&&resource!=null)retrieveSchedulerLibrary(scope,resource);
			else{
				System.err.println( "Parsing failed.  Reason: scope or(and) resource have not be given or have not been initialized");
				System.err.println( "try 'SchedulerCLI -help' for more information");
				return;
			}
			cancel(cancelValues[2],force);
		}
		else if( line.hasOption( "m" ) ) {
			// Initialize the member variable
			String[] monitorValues = line.getOptionValues( "m" );
			if(monitorValues.length<3){
				System.err.println( "Parsing failed.  Reason: monitor command needs three arguments");
				System.err.println( "try 'SchedulerCLI -help' for more information");
				return;
			}       
			
			if(monitorValues[0].compareTo("same")!=0)scope=monitorValues[0];
			if(monitorValues[1].compareTo("same")!=0)resource=monitorValues[1];
			
			if(scope!=null&&resource!=null)retrieveSchedulerLibrary(scope,resource);
			else{
				System.err.println( "Parsing failed.  Reason: scope or(and) resource have not be given or have not been initialized");
				System.err.println( "try 'SchedulerCLI -help' for more information");
				return;
			}
			monitor(monitorValues[2]);
		}
		else if( line.hasOption( "gout" ) ) {
			// Initialize the member variable
			String[] getOutcomesValues = line.getOptionValues( "gout" );
			if(getOutcomesValues.length<3){
				System.err.println( "Parsing failed.  Reason: getOutcomes command needs three arguments");
				System.err.println( "try 'SchedulerCLI -help' for more information");
				return;
			}       
			
			if(getOutcomesValues[0].compareTo("same")!=0)scope=getOutcomesValues[0];
			if(getOutcomesValues[1].compareTo("same")!=0)resource=getOutcomesValues[1];
			
			if(scope!=null&&resource!=null)retrieveSchedulerLibrary(scope,resource);
			else{
				System.err.println( "Parsing failed.  Reason: scope or(and) resource have not be given or have not been initialized");
				System.err.println( "try 'SchedulerCLI -help' for more information");
				return;
			}
			getOutcomes(getOutcomesValues[2]);
		}	    
		else if( line.hasOption( "gt" ) ) {

			String[] getTransfersValues = line.getOptionValues( "gt" );
			if(getTransfersValues.length<1){
				System.err.println( "Parsing failed.  Reason: getTransfers command needs at least one argument");
				System.err.println( "try 'SchedulerCLI -help' for more information");
				return;
			}
			else if(getTransfersValues.length==1){
				if(getTransfersValues[0].compareTo("same")!=0)scope=getTransfersValues[0];
				if(scope!=null)getTransfers(scope,"ALL");
				else{
					System.err.println( "Parsing failed.  Reason: scope has not be given or has not been initialized");
					System.err.println( "try 'SchedulerCLI -help' for more information");
					return;
				}
			}
			else if(getTransfersValues.length>1){
				if(getTransfersValues[0].compareTo("same")!=0)scope=getTransfersValues[0];
				if(getTransfersValues[1].compareTo("same")!=0)resource=getTransfersValues[1];
				
				if(scope!=null&&resource!=null)getTransfers(scope,resource);
				else{
					System.err.println( "Parsing failed.  Reason: scope or(and) resource have not be given or have not been initialized");
					System.err.println( "try 'SchedulerCLI -help' for more information");
					return;
				}
			}
		}
		else if( line.hasOption( "h" ) ) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "SchedulerCLI [OPTION]... [ARGUMENTS]...", options );
		}
		
		if( line.hasOption( "e" ) ) {
			break;
		}
		
		printHelpMsg(options);
		tempArgs=readFromCommandLine(br);
				
	}while((!line.hasOption( "e" ))&&(tempArgs!=null));	    	
	System.out.println( "\n exit.....\n");

	}
	
	public static String[] readFromCommandLine(BufferedReader br){
		String tempLine;
		
		try{
			tempLine = br.readLine();
		}catch(IOException ioe){
			System.err.println( "IO error trying to read from command line");
			return null;
		}
		return tempLine.split(" ");
	}
	
	public static void printHelpMsg(Options options){
		System.out.println("\n------------------------------------------------------\n");
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "SchedulerCLI [OPTION]... [ARGUMENTS]...\nfor keeping the previous values for scope and resource just type 'same' instead of a new value..", options );		
		System.out.println("\n------------------------------------------------------\n" +
				"interactive mode.. for exit type '-exit'" +
				"\n------------------------------------------------------");
	}
	
	public static void retrieveSchedulerLibrary(String scope, String ResourceName){
		ScopeProvider.instance.set(scope);
		
		//in case of first visit .. 
		//binderLibrary
		BinderLibrary binderLibrary = null;		
		try {		
			binderLibrary = transferBinder().at("pcitgt1012.cern.ch", 8080).build();		 
		}catch (Exception e) {
			System.err.print("exception when calling transferSchedulerBinder(..).at(..)\n"+e);
		}
		//Getting Scheduler EndpointReferenceType
		W3CEndpointReference schedulerEpr = null;
		try{
			schedulerEpr=binderLibrary.bind(ResourceName);
		}catch (Exception e) {
			System.err.print("exception when calling binderLibrary.bind(..).. \n"+e);
		}
		//in case of having visited before we go directly to the stateful by taking the epr from IS
		
		//schedulerLibrary		
		/*W3CEndpointReferenceBuilder w3cenpointBuilder = new W3CEndpointReferenceBuilder();
		try{
			w3cenpointBuilder.address(schedulerEpr.getAddress().toString());
			w3cenpointBuilder.referenceParameter((Element)schedulerEpr.getProperties().get(0));
		}catch (Exception e){
			System.err.print("exception when creating the W3CEndpointReferenceBuilder\n"+e);
		}		*/
		try {
			schedulerLibrary = transferScheduler().at(schedulerEpr).build();
		}catch (Exception e) {
			System.err.print("exception when calling transferScheduler(..).at(..)\n"+e);
		}
	}
	
	//Cancel
	public static void cancel(String transferId, boolean force){
		CallingSchedulerResult callingSchedulerResult=new CallingSchedulerResult();

		try{
			callingSchedulerResult = schedulerLibrary.cancelTransfer(transferId, force);
		}catch (Exception e) {
			System.err.print("exception when calling schedulerLibrary.cancelTransfer(..) \n"+e);
		}

		if(callingSchedulerResult.getErrors()!=null){
			try{		
				int i=0;
				for (String error : callingSchedulerResult.getErrors()){
					i++;
					if(i==1)System.err.print("We've got errors:");
					System.err.print("Error num:"+i+" - "+error);
				}
			}
			catch (Exception e) {
				System.err.print("exception when reading the errors from the callingSchedulerResult \n"+e);
			}
		}
		System.out.println("\n--------------------------------------\n" +
				"SchedulerCLI (cancel) - result="+
				callingSchedulerResult.getCancelResult()+
				"\n--------------------------------------");

	}

	//Monitor
	public 	static void monitor(String transferId){
		CallingSchedulerResult callingSchedulerResult=new CallingSchedulerResult();

		try{
			callingSchedulerResult = schedulerLibrary.monitorTransfer(transferId);
		}catch (Exception e) {
			System.err.print("exception when calling schedulerLibrary.monitorTransfer(..) \n"+e);
		}

		if(callingSchedulerResult.getErrors()!=null){
			try{		
				int i=0;
				for (String error : callingSchedulerResult.getErrors()){
					i++;
					if(i==1)System.err.print("We've got errors:");
					System.err.print("Error num:"+i+" - "+error);
				}
			}
			catch (Exception e) {
				System.err.print("exception when reading the errors from the callingSchedulerResult \n"+e);
			}
		}
		
		System.out.println("\n--------------------------------------\n" +
				"SchedulerCLI (monitor) - result="+
				callingSchedulerResult.getMonitorResult()+
				"\n--------------------------------------");
	}


	//GetOutcomes
	public 	static void getOutcomes(String transferId){
		CallingSchedulerResult callingSchedulerResult=new CallingSchedulerResult();
		try{
			callingSchedulerResult = schedulerLibrary.getOutcomesOfTransfer(transferId);
		}catch (Exception e) {
			System.err.print("exception when calling schedulerLibrary.getOutcomesOfTransfer(..) \n"+e);
		}

		if(callingSchedulerResult.getErrors()!=null){
			try{		
				int i=0;
				for (String error : callingSchedulerResult.getErrors()){
					i++;
					if(i==1)System.err.print("We've got errors:");
					System.err.print("Error num:"+i+" - "+error);
				}
			}
			catch (Exception e) {
				System.err.print("exception when reading the errors from the callingSchedulerResult \n"+e);
			}
		}
		
		System.out.println("\n--------------------------------------\n" +
				"SchedulerCLI (getOutcomes) - result="+
				callingSchedulerResult.getSchedulerOutcomes()+
				"\n--------------------------------------");

	}

	//schedule
	public static boolean schedule(String fileName){
		System.out.println("\n-------------------\n" +
				"SchedulerCLI (schedule) - fileName="+
				fileName+"\n-------------------");
		String text="";
		try{
			FileInputStream fstream = new FileInputStream(fileName);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String strLine;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				text=text.concat(strLine);
			}
			//Close the input stream
			in.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
			return false;
		}

		SchedulerObj schedulerObj= new SchedulerObj();

		text=text.replaceAll("&lt;", "<");
		text=text.replaceAll("&gt;", ">");

		XStream xstream = new XStream();
		schedulerObj=(SchedulerObj)xstream.fromXML(text);

		if(schedulerObj.getTypeOfSchedule().getManuallyScheduled()!=null){
			DateFormat formatter = new SimpleDateFormat("dd.MM.yy-HH.mm");
			Date tmpDate=null;
			String instanceString= schedulerObj.getTypeOfSchedule().getManuallyScheduled().getInstanceString();
			System.out.println("given string:\n"+instanceString);
			try {
				tmpDate = formatter.parse(instanceString);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Calendar tmpCalendar = Calendar.getInstance();
			tmpCalendar.setTime(tmpDate);
			//System.out.println("given date:\nYEAR="+tmpCalendar.get(Calendar.YEAR)+"\nMONTH="+tmpCalendar.get(Calendar.MONTH)+"\nDAY_OF_MONTH="+tmpCalendar.get(Calendar.DAY_OF_MONTH)+"\nHOUR="+tmpCalendar.get(Calendar.HOUR_OF_DAY)+"\nMINUTE="+tmpCalendar.get(Calendar.MINUTE));

			schedulerObj.getTypeOfSchedule().getManuallyScheduled().setCalendar(tmpCalendar);
		}
		else if(schedulerObj.getTypeOfSchedule().getPeriodicallyScheduled()!=null){
			DateFormat formatter = new SimpleDateFormat("dd.MM.yy-HH.mm");
			Date tmpDate=null;
			String startInstanceString= schedulerObj.getTypeOfSchedule().getPeriodicallyScheduled().getStartInstanceString();
			//System.out.println("given startInstanceString:\n"+startInstanceString);

			Calendar tmpCalendar = Calendar.getInstance();
			if(startInstanceString.compareToIgnoreCase("now")!=0){
				try {
					tmpDate = formatter.parse(startInstanceString);
				} catch (Exception e) {
					e.printStackTrace();
				}
				tmpCalendar.setTime(tmpDate);
				//System.out.println("given startInstance:\nYEAR="+tmpCalendar.get(Calendar.YEAR)+"\nMONTH="+tmpCalendar.get(Calendar.MONTH)+"\nDAY_OF_MONTH="+tmpCalendar.get(Calendar.DAY_OF_MONTH)+"\nHOUR="+tmpCalendar.get(Calendar.HOUR_OF_DAY)+"\nMINUTE="+tmpCalendar.get(Calendar.MINUTE));
			}
			else{
				System.out.println("given startInstance:\nYEAR="+tmpCalendar.get(Calendar.YEAR)+"\nMONTH="+tmpCalendar.get(Calendar.MONTH)+"\nDAY_OF_MONTH="+tmpCalendar.get(Calendar.DAY_OF_MONTH)+"\nHOUR="+tmpCalendar.get(Calendar.HOUR_OF_DAY)+"\nMINUTE="+tmpCalendar.get(Calendar.MINUTE));
			}
			schedulerObj.getTypeOfSchedule().getPeriodicallyScheduled().setStartInstance(tmpCalendar);
		}

		String transferId=null;
		try{
			transferId = schedulerLibrary.scheduleTransfer(schedulerObj);
		}catch (Exception e) {
			System.err.print("exception when calling schedulerLibrary.localFileBasedTransfer(..) \n"+e);
		}
	
		System.out.println("\n--------------------------------------\n" +
				"SchedulerCLI (schedule) - transferId="+
				transferId+"\n--------------------------------------");
		return true;
	}

	public static void getTransfers(String scope, String resourceName){
		ScopeProvider.instance.set(scope);
		//Management Library
		ManagementLibrary managementLibrary = null;		
		try {		
			managementLibrary = transferManagement().build();		 
		}catch (Exception e) {
			System.err.print("exception when calling transferSchedulerManagement().build()\n"+e);
		}
		
		CallingManagementResult callingManagementResult= null;
		try{
			callingManagementResult=managementLibrary.getAllTransfersInfo(resourceName); 
		}catch (Exception e) {
			System.err.print("exception when calling managementLibrary.getAllTransfersInfo(..).. \n"+e);
		}

		if(callingManagementResult==null)System.out.println("SchedulerCLI (getTransfers) - exception in calling the when calling endpoint.getAllTransfersInfo(message)");
		else{
			if(callingManagementResult.getAllTheTransfersInDB()!=null){
				System.out.println("\n--------------------------------------\n" +
						ANSI_CYAN+"SchedulerCLI (getTransfers) - Retrieve Transfers by method (ResourceName="+resourceName+"):\n"+ANSI_RESET);
				//System.out.println("\n--------------------------------------\nSchedulerCLI (getTransfers) - Retrieve Transfers by method (ResourceName="+resourceName+"):\n");
				
				for(TransferInfo obj: callingManagementResult.getAllTheTransfersInDB()){
					System.out.print("TransferId="+((TransferInfo)obj).getTransferId()+" -- Status=");
					if(((TransferInfo)obj).getStatus().compareTo("COMPLETED")==0)System.out.print(ANSI_GREEN);
					else if(((TransferInfo)obj).getStatus().compareTo("CANCELED")==0 || ((TransferInfo)obj).getStatus().compareTo("FAILED")==0)System.out.print(ANSI_RED);
					else if(((TransferInfo)obj).getStatus().compareTo("STANDBY")==0)System.out.print(ANSI_YELLOW);
					else if(((TransferInfo)obj).getStatus().compareTo("ONGOING")==0)System.out.print(ANSI_CYAN);

					//System.out.print("TransferId="+((TransferInfo)obj).getTransferId()+" -- Status="+((TransferInfo)obj).getStatus()+" -- Submitter="+((TransferInfo)obj).getSubmitter());
					
					System.out.print(((TransferInfo)obj).getStatus()+ANSI_RESET+" -- Submitter="+((TransferInfo)obj).getSubmitter());
					
					if(((TransferInfo)obj).getTypeOfSchedule().isDirectedScheduled()){
						System.out.println(" -- directlyScheduled");
					}
					else if(((TransferInfo)obj).getTypeOfSchedule().getManuallyScheduled()!=null){
						Calendar calendarTmp = ((TransferInfo)obj).getTypeOfSchedule().getManuallyScheduled().getCalendar();
						Date date=calendarTmp.getTime();
						System.out.println(" -- manuallyScheduled for: "+date.toString());
					}
					else if(((TransferInfo)obj).getTypeOfSchedule().getPeriodicallyScheduled()!=null){
						Calendar calendarTmp = ((TransferInfo)obj).getTypeOfSchedule().getPeriodicallyScheduled().getStartInstance();
						Date date=calendarTmp.getTime();
						int num = ("TransferId="+((TransferInfo)obj).getTransferId()+" -- Status="+((TransferInfo)obj).getStatus()+" -- Submitter="+((TransferInfo)obj).getSubmitter() ).length();
						String empty=" ";
						for(int i=0;i<num+3;i++)empty = empty.concat(" ");
						System.out.println(" -- periodicallyScheduled with FrequencyType:" +((TransferInfo)obj).getTypeOfSchedule().getPeriodicallyScheduled().getFrequency().getValue()+"\n"+empty+"next transfer at: "+date.toString());
					}
				}
			}
			System.out.println("");

			if(callingManagementResult.getAllTheTransferObjectsInDB()!=null){
				System.out.println(ANSI_CYAN+"\nSchedulerCLI (getTransfers) - Retrieve Transfer Objects by method (ResourceName="+resourceName+"):\n"+ANSI_RESET);
				//System.out.println("\nSchedulerCLI (getTransfers) - Retrieve Transfer Objects by method (ResourceName="+resourceName+"):\n");
				for(TransferObjectInfo obj: callingManagementResult.getAllTheTransferObjectsInDB()){
					System.out.println("TransferObjId="+((TransferObjectInfo)obj).getObjectId()+" -- transferid="+((TransferObjectInfo)obj).getTransferid());
						//	"\n            --TransferObjURI="+((TransferObjectInfo)obj).getURI());
				}
			}
			System.out.println("\n--------------------------------------");
			//System.out.println("\n test for colors... \n" + ANSI_RED+"..red" +ANSI_GREEN+"..green" +ANSI_YELLOW+"..yellow" +ANSI_BLUE+"..blue" +ANSI_PURPLE+"..purple" +ANSI_CYAN+"..cyan" +ANSI_RESET+ "..reset" +"\n");
			
		}
	}
}
