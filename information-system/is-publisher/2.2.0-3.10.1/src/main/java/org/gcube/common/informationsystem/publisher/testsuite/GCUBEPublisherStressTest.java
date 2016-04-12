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
 * A test which loops and publishes 1000 {@link GCUBEMCollection} profiles <p>
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
public class GCUBEPublisherStressTest {

	protected static final GCUBEClientLog logger=new GCUBEClientLog(GCUBEPublisherStressTest.class);
	
	protected static final int MAX_REGISTRATIONS = 1000;
	
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
		
		if (args.length == 2) {
			
			for (Integer i = 1; i <= MAX_REGISTRATIONS; i++) {
				try {
					GCUBEMCollection resource = GHNContext.getImplementation(GCUBEMCollection.class);
					if (resource == null)
						logger.error("Failed to load from GHNContext");
						//System.err.println("Failed to load from GHNContext");
					try {						
						System.out.println(args[0]);
						resource.load(new FileReader(args[0]));
						resource.setID(i.toString());						
					} catch (FileNotFoundException e1) {
						// try as resource
						java.io.BufferedReader br = new java.io.BufferedReader((new InputStreamReader(GCUBEPublisherTest.class.getResourceAsStream(args[0]))));
						resource.load(br);
					}
						
					//resource.store(fir);
					logger.debug(GCUBEScope.getScope(args[1]).toString());
					System.out.println(dateAndTimeStamp.format(new Date()) + " - publishing resource #" + i);
					GCUBESecurityManagerImpl managerSec = new GCUBESecurityManagerImpl() {  public boolean isSecurityEnabled() {return false;}};									
					ISPublisher publisher  =GHNContext.getImplementation(ISPublisher.class);
					publisher.registerGCUBEResource(resource, GCUBEScope.getScope(args[1]), managerSec);
					System.out.println(dateAndTimeStamp.format(new Date()) + " - resource #" + i + " published");
					publisher.updateGCUBEResource(resource, GCUBEScope.getScope(args[1]), managerSec);
					System.out.println(dateAndTimeStamp.format(new Date()) + " - resource #" + i + " updated");
					publisher.updateGCUBEResource(resource, GCUBEScope.getScope(args[1]), managerSec);
					System.out.println(dateAndTimeStamp.format(new Date()) + " - resource #" + i + " updated again");
					
					//System.out.println("Profile published");
				}catch (Exception e ){
					e.printStackTrace();
				}
			}
		}	
		else {
			logger.error("USAGE: GCUBEPublisherStressTest <profile file> <callerScope>");
			System.err.println("USAGE: GCUBEPublisherStressTest <profile file> <callerScope>");
			System.exit(0);
		}



	}

}
