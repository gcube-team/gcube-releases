/**
 * 
 */
package org.gcube.portlets.admin.software_upload_wizard.server.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.gcube.portlets.admin.software_upload_wizard.shared.IOperationProgress;
import org.gcube.portlets.admin.software_upload_wizard.shared.OperationProgress;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class ProgressManager {
	
	protected static ProgressManager instance;
	
	public static ProgressManager getInstance()
	{
		if (instance == null) instance = new ProgressManager();
		return instance;
	}
	
	protected Map<String, OperationProgress> sessions = new LinkedHashMap<String, OperationProgress>();
	
	public String addOperationProgress(OperationProgress progress)
	{
		String id = UUID.randomUUID().toString();
		sessions.put(id, progress);
		return id;
	}
	
	public IOperationProgress getProgress(String operationId)
	{
		return sessions.get(operationId);
	}

}
