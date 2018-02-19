package org.gcube.usecases.ws.thredds;

import org.gcube.usecases.ws.thredds.faults.RemoteFileNotFoundException;
import org.gcube.usecases.ws.thredds.faults.UnableToLockException;

public class FileAccessTests {

	public static void main(String[] args) throws RemoteFileNotFoundException, UnableToLockException {
		TokenSetter.set("/gcube/devsec/devVRE");
		
		Runnable readCatalog=new Runnable() {@Override public void run() {	try{
					Commons.readThreddsFile("catalog.xml");
				
		}catch(Throwable t) {System.err.println(t);	}}};
			
		
		Runnable readInfo=new Runnable() {@Override public void run() {	try{
			Commons.getThreddsInfo();		
}catch(Throwable t) {System.err.println(t);	}}};
		
		
		repeatedTimedOperation(readCatalog,"READ CATALOG");
		
		repeatedTimedOperation(readInfo, "READ INFO");
		
		
		System.out.println("Checking lock ..");
				
		String processID="stupidProcess";
		String toLockFolder="devVRE";
		String lockPath=toLockFolder+"/"+Constants.LOCK_FILE;
		
		try {
			Commons.lockFolder(toLockFolder, processID);			
		}catch(UnableToLockException e) {
			System.out.println("Dirty from previous tests.. removing lock..");
			Commons.deleteThreddsFile(lockPath);
			System.out.println("Trying to lock again..");
			Commons.lockFolder(toLockFolder, processID);
		}
		
		try{
			System.out.println("Ok, next should fail...");
			Commons.lockFolder(toLockFolder, processID);
		}catch(UnableToLockException e) {
			System.out.println("Correctly failed, now we read lock.. ");
			
			String lockId=Commons.readThreddsFile(lockPath);
			if(lockId.equals(processID)) {
				System.out.println("Lock owned. Going to remove it.. ");
				Commons.deleteThreddsFile(lockPath);
				System.out.println("Now I can lock it again... ");
				Commons.lockFolder(toLockFolder, processID);
				System.out.println("Ok, cleaning it up.. ");
				Commons.deleteThreddsFile(lockPath);
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
