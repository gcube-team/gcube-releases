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

/**
 * Handles the actual reading of the configuration
 * 
 * @author jferrer
 */
class ConfigurationLoader {
    private static final Log log = LogFactory.getLog(ConfigurationLoader.class);

    public ComponentProperties readPropertiesConfiguration(String companyId, String componentName) {
        AggregatedProperties properties = new AggregatedProperties(companyId, componentName);
//        if (companyId != null) {
//            properties.addGlobalFileName(Conventions.GLOBAL_CONFIGURATION_FILE + Conventions.SLASH + companyId
//                    + Conventions.PROPERTIES_EXTENSION);
//        }
        properties.addGlobalFileName(Conventions.GLOBAL_CONFIGURATION_FILE + 
                Conventions.PROPERTIES_EXTENSION);
//        if (companyId != null) {
//            properties.addBaseFileName(componentName + Conventions.SLASH + companyId + 
//                    Conventions.PROPERTIES_EXTENSION);
//        }
        properties.addBaseFileName(componentName + Conventions.PROPERTIES_EXTENSION);

        log.info("Properties for " + componentName + " loaded from " + properties.loadedSources());
        return new ComponentProperties(properties);
    }
    
}
