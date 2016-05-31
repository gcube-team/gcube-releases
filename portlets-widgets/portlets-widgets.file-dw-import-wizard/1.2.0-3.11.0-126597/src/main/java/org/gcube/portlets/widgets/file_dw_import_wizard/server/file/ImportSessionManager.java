package org.gcube.portlets.widgets.file_dw_import_wizard.server.file;
/**
 * 
 */


import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

//import org.apache.log4j.Level;
//import org.apache.log4j.Logger;
import org.gcube.portlets.widgets.file_dw_import_wizard.shared.FileType;



public class ImportSessionManager {
	
	protected static ImportSessionManager instance;
	
	public static ImportSessionManager getInstance()
	{
		if (instance == null) instance = new ImportSessionManager();
		return instance;
	}
	
	
	protected Map<String, ImportSessions> sessions = new LinkedHashMap<String, ImportSessions>();
	
	
	public ImportSessionManager()
	{
	}
	
	protected synchronized String getNewId()
	{
		return UUID.randomUUID().toString();
	}
	
	public ImportSessions createImportSession(String targetId,FileType type)
	{
		String id = getNewId();
		
		TargetRegistry targetRegistry = TargetRegistry.getInstance();
		Target target = targetRegistry.get(targetId);
		if (target == null) throw new IllegalArgumentException("Target with id "+targetId+" not found.");
		
		ImportSessions session = new ImportSessions(id, target,type);
		sessions.put(id, session);
		
		return session;
	}
	
	public ImportSessions getSession(String id)
	{
		return sessions.get(id);
	}
	
	/*public OperationStatusInfo getUploadStatus(long ticketId) throws CSVServiceException
	{
		if (!tickets.containsKey(ticketId)) throw new CSVServiceException("Wrong ticket id");
		
		ImportTicket importTicket = tickets.get(ticketId);
		
		logger.trace("ticket status: "+importTicket.getStatus());
		
		switch (importTicket.getStatus()) {
			case UPLOADING: return new OperationStatusInfo(importTicket.getContentLenght(), importTicket.getProgress());
			case UPLOAD_COMPLETE: return new OperationStatusInfo(importTicket.getContentLenght(), importTicket.getProgress(), true);
			case FAILED: return new OperationStatusInfo(importTicket.getContentLenght(), importTicket.getProgress(), importTicket.getLastErrorMessage());
			case TRANSMITTING: return new OperationStatusInfo(0, 0);
			case IMPORTING: return new OperationStatusInfo(importTicket.getContentLenght(), importTicket.getProgress());
			case COMPLETED: return new OperationStatusInfo(importTicket.getContentLenght(), importTicket.getProgress(), true);
		}
		
		return new OperationStatusInfo(importTicket.getContentLenght(), importTicket.getProgress(), importTicket.getStatus()==ImportStatus.CONFIGURING);
	}
	

	public void removeImportTicket(long ticketId)
	{
		ImportTicket importTicket = tickets.remove(ticketId);
		if (importTicket!=null) {
			if (importTicket.getImportedFile()!=null) importTicket.getImportedFile().delete();
		}
	}
	
	public List<ImportTicket> getImportTickets()
	{
		return new LinkedList<ImportTicket>(tickets.values());
	}*/
}
