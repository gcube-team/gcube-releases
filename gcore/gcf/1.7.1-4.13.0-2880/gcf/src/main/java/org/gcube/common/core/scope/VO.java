package org.gcube.common.core.scope;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.resources.GCUBEHostingNode;

/**
 * A scope of type <code>ROOTVO</code> or <code>SUBVO</code>.  
 */
public class VO extends GCUBEScope {
	
	static Map<VO,ServiceMap> cached = new HashMap<VO, ServiceMap>();

	protected VO(String name) {
		super(name);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public ServiceMap getServiceMap() throws GCUBEScopeNotSupportedException {

			if (this.serviceMap!=null)
				return serviceMap;
		
			ServiceMap map = null;
				
			//retrieves the service map from an equivalent scope of the GHN
			GCUBEScope scope = GHNContext.getContext().getGHN().getScopes().get(this.toString());
			
			if (scope == null)
				
				if (GHNContext.getContext().isClientMode()) {
					
					map = cached.get(this);
					
					if (map==null)
						try {
							map = new ServiceMap();
							File mapFile= GHNContext.getContext().getFile(GCUBEHostingNode.MAP_PREFIX + this.getName() + ".xml");
					    	map.load(new FileReader(mapFile));
						}
						catch(Exception e) {
							e.printStackTrace();
							throw new GCUBEScopeNotSupportedException(this);
						}
				}
				else 
					throw new GCUBEScopeNotSupportedException(this);
			else
				map = scope.getServiceMap();
			
			this.serviceMap = map;
			cached.put(this,map);
			
		return map;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setServiceMap(ServiceMap map) {
		this.serviceMap=map;
	}


	
	
}
