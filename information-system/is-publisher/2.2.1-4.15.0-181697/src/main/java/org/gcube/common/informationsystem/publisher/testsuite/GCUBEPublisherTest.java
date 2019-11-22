package org.gcube.common.informationsystem.publisher.testsuite;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;

import org.apache.log4j.PropertyConfigurator;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.publisher.ISPublisher;
import org.gcube.common.core.resources.GCUBEGenericResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManagerImpl;
import org.gcube.common.core.utils.logging.GCUBEClientLog;
import org.gcube.common.informationsystem.publisher.testsuite.GCUBEPublisherTest;

/**
 * A test which publishes a GCUBEGenericResource profile <p>
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
public class GCUBEPublisherTest {

	/**
	 * Object logger.
	 */
    protected static final GCUBEClientLog logger=new GCUBEClientLog(GCUBEPublisherTest.class);

    
    /**
     * 
     * @param args
     * <OL>
     * <LI> the absolute path of the file including the profile serialisation
     * <LI> the publishing scope
     * </OL>
     */
	public static void main(String[] args) {
		
		PropertyConfigurator.configure(System.getenv("GLOBUS_LOCATION")+ File.separator + "container-log4j.properties");
		
			//register a generic resource profile
			if (args.length == 2) {
				try {
					GCUBEGenericResource resource = GHNContext.getImplementation(GCUBEGenericResource.class);
					if (resource == null)
						logger.error("Failed to load from GHNContext");
						//System.err.println("Failed to load from GHNContext");
					try {						
						System.out.println(args[0]);
						resource.load(new FileReader(args[0]));
					} catch (FileNotFoundException e1) {
						// try as resource
						java.io.BufferedReader br = new java.io.BufferedReader((new InputStreamReader(GCUBEPublisherTest.class.getResourceAsStream(args[0]))));
						resource.load(br);
					}
						
					//resource.store(fir);
					logger.debug(GCUBEScope.getScope(args[1]).toString());						
					GCUBESecurityManagerImpl managerSec = new GCUBESecurityManagerImpl() {  public boolean isSecurityEnabled() {return false;}};									
					ISPublisher publisher  =GHNContext.getImplementation(ISPublisher.class);
					publisher.registerGCUBEResource(resource, GCUBEScope.getScope(args[1]), managerSec);
					logger.info("Profile published");
					//System.out.println("Profile published");
				}catch (Exception e ){
					e.printStackTrace();
				}
			}	
			else {
				logger.error("USAGE: GCUBEPublisherTest <profile file> <callerScope>");
				//System.err.println("USAGE: GCUBEPublisherTest <profile file> <callerScope>");
				System.exit(0);
			}

		
	}
}
