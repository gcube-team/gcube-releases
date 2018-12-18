package org.gcube.common.vremanagement.deployer.impl.persistence;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.gcube.common.core.persistence.GCUBEWSFields2FilePersistenceDelegate;
import org.gcube.common.vremanagement.deployer.impl.state.DeployerResource;
import org.gcube.common.vremanagement.deployer.stubs.deployer.DeployedPackage;

/**
 * Persistence delegate for {@link DeployerResource}
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class DeployerPersistenceDelegate extends
		GCUBEWSFields2FilePersistenceDelegate<DeployerResource> {

	
	/**
	 * {@inheritDoc}
	 */
	protected void onLoad(DeployerResource resource,ObjectInputStream stream) throws Exception {
		//deserialise gCube properties
		super.onLoad(resource, stream);
		//deserialise Deployer's properties
		//resource.setNumberOfDeployedPackages((Integer)stream.readObject());
		resource.setDeployedPackages((DeployedPackage[])stream.readObject());
		resource.setLastDeployment((String)stream.readObject());
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void onStore(DeployerResource resource, ObjectOutputStream stream) throws Exception {	
		//serialise gCube properties
		super.onStore(resource,stream);
		//serialise Deployer's properties
		//stream.writeObject(resource.getNumberOfDeployedPackages());
		stream.writeObject(resource.getDeployedPackages());
		stream.writeObject(resource.getLastDeployment());
	}
				

}
