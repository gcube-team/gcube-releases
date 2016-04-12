/**
 * 
 */
package org.gcube.common.mycontainer;

import java.io.File;


/**
 * @author Fabio Simeoni
 *
 */
public class TestUtils {

	
	public static Gar existingGar() {
		
		return new Gar(new File("src/test/resources/sample-service.gar"));
	}

	public static Gar libOnly() {
		
		Gar gar = new Gar("stubs-only").
		   addLibrary("src/test/resources/sample-service-stubs.jar");
		
		return gar;
	}

	public static Gar syntheticGar() {
		
		Gar gar = new Gar("sample-service").
		   addLibrary("src/test/resources/sample-service.jar").
		   addLibrary("src/test/resources/sample-service-stubs.jar").
		   addInterfaces("src/test/resources/interfaces").
		   addConfigurations("src/test/resources/configuration");
		
		return gar;
	}
}
