package org.gcube.common.informationsystem.publisher.testsuite;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.PropertyConfigurator;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.publisher.ISPublisher;
import org.gcube.common.core.resources.GCUBEMCollection;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManagerImpl;
import org.gcube.common.core.utils.logging.GCUBEClientLog;

/**
 * A test which loops and publishes several {@link GCUBEMCollection} profiles in parallel <p>
 * 
 * The test requires the following command-line inputs:<p>
 * <OL>
 * <LI> the absolute path of the file including the profile serialisation
 * <LI> the publishing scope
 * </OL>
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class GCUBEPublisherParallelStressTest {

	protected static final GCUBEClientLog logger=new GCUBEClientLog(GCUBEPublisherParallelStressTest.class);
	
	protected static final int PARALLELISM = 2;
	
	protected static final int REGISTRATIONS_PER_THREAD = 500;
	
	static final DateFormat dateAndTimeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	
	/**
	 * @param args
	 * <OL>
	 * <LI> the absolute path of the file including the profile serialisation
	 * <LI> the publishing scope
	 * </OL>
	 */
	public static void main(String[] args) {
		PropertyConfigurator.configure(System.getenv("GLOBUS_LOCATION")+ File.separator + "container-log4j.properties");
		final String file = args[0];
		final GCUBEScope scope = GCUBEScope.getScope(args[1]);
		Integer threadStartFrom = 1;
		if (args.length == 2) {			
			for (Integer thread = 0; thread < PARALLELISM; thread++) {				
				new RegistrationThread(threadStartFrom, file, scope).start();				
				threadStartFrom = new Integer(threadStartFrom + REGISTRATIONS_PER_THREAD);				
			}
		}	
		else {
			logger.error("USAGE: GCUBEPublisherStressTest <profile file> <callerScope>");			
			System.exit(0);
		}

	}

	public static class RegistrationThread extends Thread {
		
		Integer startFrom = 0;
		String file = "";
		GCUBEScope scope;
		
		RegistrationThread(int startFrom, String file, GCUBEScope scope) {
			this.startFrom = startFrom;
			this.scope = scope;
			this.file = file;
		}
		
		public void run() {
			for (Integer i = 1; i <= REGISTRATIONS_PER_THREAD; i++) {
				try {
					GCUBEMCollection resource = GHNContext.getImplementation(GCUBEMCollection.class);
					if (resource == null)
						logger.error("Failed to load from GHNContext");
						//System.err.println("Failed to load from GHNContext");
					try {												
						resource.load(new FileReader(file));						
						resource.setID(new Integer(i + startFrom).toString());						
					} catch (FileNotFoundException e1) {
						// try as resource
						java.io.BufferedReader br = new java.io.BufferedReader((new InputStreamReader(GCUBEPublisherTest.class.getResourceAsStream(file))));
						resource.load(br);
					}
						
					//resource.store(fir);
					logger.debug(scope.toString());
					System.out.println( "Thread ID:" +  Thread.currentThread().getId() + ", " + dateAndTimeStamp.format(new Date()) + " - publishing resource #" + resource.getID());
					GCUBESecurityManagerImpl managerSec = new GCUBESecurityManagerImpl() {  public boolean isSecurityEnabled() {return false;}};									
					ISPublisher publisher  =GHNContext.getImplementation(ISPublisher.class);
					publisher.registerGCUBEResource(resource, scope, managerSec);
					System.out.println("Thread ID:" +  Thread.currentThread().getId() + ", " + dateAndTimeStamp.format(new Date()) + " - resource #" + resource.getID() + " published");
					publisher.updateGCUBEResource(resource, scope, managerSec);
					System.out.println("Thread ID:" +  Thread.currentThread().getId() + ", " + dateAndTimeStamp.format(new Date()) + " - resource #" + resource.getID() + " updated");
					publisher.updateGCUBEResource(resource, scope, managerSec);
					System.out.println("Thread ID:" +  Thread.currentThread().getId() + ", " + dateAndTimeStamp.format(new Date()) + " - resource #" + resource.getID() + " updated again");
					Thread.yield();
					//System.out.println("Profile published");
				}catch (Exception e ){
					logger.error("Unable to publish/update the resource ", e);
					e.printStackTrace();
				}	
			
			}
		}
	
	}

}
