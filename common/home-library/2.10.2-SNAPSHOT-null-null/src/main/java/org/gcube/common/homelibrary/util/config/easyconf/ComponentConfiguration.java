/*
 * Copyright 2004-2005 Germinus XXI
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gcube.common.homelibrary.util.config.easyconf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains the configuration of an EasyConf component including properties 
 * configuration and an object graph configuration.
 *
 * @author  Jorge Ferrer
 * @version $Revision: 1.11 $
 *
 */
public class ComponentConfiguration {

	private static final Log log = LogFactory.getLog(ComponentConfiguration.class);
    private ComponentProperties properties;
    private String componentName;
    private ConfigurationLoader confManager = new ConfigurationLoader();
    private String companyId;
    private Map confObjectsCache = new HashMap();

    public ComponentConfiguration(String componentName) {
        this(null, componentName);
    }
    
    public ComponentConfiguration(String companyId, String componentName) {
        this.companyId = companyId;
        this.componentName = componentName;
    }

    /**
     * Get the name of the component which is associated with this configuration
     */
    public String getComponentName() {
        return componentName;
    }
    
    private ConfigurationLoader getConfigurationManager() {
        return confManager;
    }

    /**
     * Get a typed map of the properties associated with this component
     */
    public ComponentProperties getProperties() {
        ComponentProperties properties = getAvailableProperties();
        if ((!properties.hasBaseConfiguration())){
            String msg = "The base properties file was not found";
            throw new ConfigurationNotFoundException(componentName, msg);
        }
        return properties;
    }

    private ComponentProperties getAvailableProperties() {
        if (properties != null) {
            return properties;
        }
        properties = getConfigurationManager().
        	readPropertiesConfiguration(companyId, componentName);
        return properties;
    }

    
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ComponentConfiguration)) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		ComponentConfiguration cconf = (ComponentConfiguration) obj;
		return componentName.equals(cconf.getComponentName());
	}
	
	@Override
	public int hashCode() {
		return componentName.hashCode();
	}

}
