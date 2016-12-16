package org.gcube.vremanagement.virtualplatform.model;

/**
 * 
 * A {@link Package} deployed in a concrete instance of {@link TargetPlatform}
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public interface DeployedPackage {

	public boolean isSuccess();

	public boolean verify();
	
	public Package getSourcePackage();
	
	public String[] getEndpoints();


}
