/**
 * 
 */
package org.gcube.vremanagement.virtualplatform.model;

/**
 * 
 * A {@link Package} undeployed from a concrete instance of {@link TargetPlatform}
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public interface UndeployedPackage {

	public boolean isSuccess();

	public boolean verify();
	
	public Package getSourcePackage();
}
