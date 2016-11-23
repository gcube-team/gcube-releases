package org.gcube.common.vremanagement.deployer.impl.resources;

import java.io.Serializable;

import org.gcube.common.core.contexts.GCUBEContext;
import org.gcube.common.core.utils.logging.GCUBELog;


/**
 * Serializable {@link GCUBELog}
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class LocalGCUBELog extends GCUBELog  {	
	
	public LocalGCUBELog(String str) {
		super(str);
	}

	public LocalGCUBELog(Object obj, GCUBEContext context) {
		super(obj, context);
	}
	
	public LocalGCUBELog(Object obj) {
		super(obj);
	}
	
	public LocalGCUBELog() {
		super("");

	}

	public static class SerializableGCUBELog extends LocalGCUBELog implements Serializable{
		
		private static final long serialVersionUID = 1808981317141813380L;
	
		public SerializableGCUBELog(String str) {
			super(str);
		}

		public SerializableGCUBELog(Object obj, GCUBEContext context) {
			super(obj, context);
		}
		
		public SerializableGCUBELog(Object obj) {
			super(obj);
		}
			
	}

}
