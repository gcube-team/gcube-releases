package org.gcube.usecases.ws.thredds;

import org.gcube.usecases.ws.thredds.engine.impl.ThreddsController;
import org.gcube.usecases.ws.thredds.faults.InternalException;
import org.gcube.usecases.ws.thredds.faults.UnableToLockException;

public class FileAccessTests {

	public static void main(String[] args) throws InternalException {
		TestCommons.setScope();
		String processID="stupidProcess";
		String toLockFolder="devVRE";
		
		final ThreddsController controller=new ThreddsController(toLockFolder, TokenSetter.getCurrentToken());
		
		Runnable readCatalog=new Runnable() {@Override public void run() {	try{
					controller.readThreddsFile("catalog.xml");
				
		}catch(Throwable t) {System.err.println(t);	}}};
			
		
		Runnable readInfo=new Runnable() {@Override public void run() {	try{
			controller.getThreddsInfo();		
}catch(Throwable t) {System.err.println(t);	}}};
		

		repeatedTimedOperation(readCatalog,"READ CATALOG");
		
		repeatedTimedOperation(readInfo, "READ INFO");
		
		
		System.out.println("Checking lock ..");
				
		
		
		try {
			controller.lockFolder(processID);			
		}catch(UnableToLockException e) {
			System.out.println("Dirty from previous tests.. removing lock..");
			controller.deleteThreddsFile(Constants.LOCK_FILE);
			System.out.println("Trying to lock again..");
			controller.lockFolder(processID);
		}
		
		try{
			System.out.println("Reading file descriptor... ");
			System.out.println(controller.getFileDescriptor(Constants.LOCK_FILE));
			System.out.println("Ok, next should fail...");
			controller.lockFolder(processID);
		}catch(UnableToLockException e) {
			System.out.println("Correctly failed, now we read lock.. ");
			
			String lockId=controller.readThreddsFile(Constants.LOCK_FILE);
			if(lockId.equals(processID)) {
				System.out.println("Lock owned. Going to remove it.. ");
				controller.deleteThreddsFile(Constants.LOCK_FILE);
				System.out.println("Now I can lock it again... ");
				controller.lockFolder(processID);
				System.out.println("Ok, cleaning it up.. ");
				controller.deleteThreddsFile(Constants.LOCK_FILE);
			}else throw new RuntimeException("LOCK ID "+lockId+" IS DIFFERENT then expected "+processID);
		}
		
		
	}

	
	private static void repeatedTimedOperation(Runnable op,String title) {
		System.out.println(title);
		for(int i=0;i<10;i++)	{
			long startTime=System.currentTimeMillis();
			op.run();
			System.out.println("["+i+"] in "+(System.currentTimeMillis()-startTime));			
		}
		
	}
	
}
