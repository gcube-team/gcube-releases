package org.gcube.common.vremanagement.deployer.impl.resources.update;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.vremanagement.deployer.impl.operators.ant.AntRunner;
import org.gcube.common.vremanagement.deployer.impl.resources.BasePackage;
import org.gcube.common.vremanagement.deployer.impl.resources.BaseTypedPackage;


public class UpdatableMainPackage extends UpdatablePackage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private BaseTypedPackage base;
	
	/**	Local Ant runner */ 
	protected transient AntRunner run;
	
	public UpdatableMainPackage(BaseTypedPackage base){
		super(base.getKey().getServiceClass(), base.getKey().getServiceName(), base.getKey().getServiceVersion(),
				base.getKey().getPackageName(), base.getKey().getPackageVersion());
		this.base = base;
		this.base.logger = new GCUBELog(BasePackage.class);
		this.logger = new GCUBELog(BasePackage.class);

	}
	
	public UpdatableMainPackage(String serviceClass, String serviceName,
			String serviceVersion, String packageName, String packageVersion) {
		super(serviceClass, serviceName, serviceVersion, packageName, packageVersion);
		// TODO Auto-generated constructor stub
	}


}
