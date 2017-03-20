package org.gcube.data.tm.plugin;

import java.io.File;

import org.gcube.data.tm.context.ServiceContext;
import org.gcube.data.tmf.api.Environment;

/**
 * Default implementation of {@link Environment}.
 * 
 * @author Fabio Simeoni
 *
 */
public class PluginEnvironment implements Environment {
		
		private static final long serialVersionUID = 1L;
	
		/**{@inheritDoc}*/
		public File file(String path) {
			return ServiceContext.getContext().getPersistentFile(path,false);
		}
	
}
