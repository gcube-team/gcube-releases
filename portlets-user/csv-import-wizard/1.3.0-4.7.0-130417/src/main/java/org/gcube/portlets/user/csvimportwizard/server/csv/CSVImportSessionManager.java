/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.server.csv;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class CSVImportSessionManager {
	
	protected static CSVImportSessionManager instance;
	
	public static CSVImportSessionManager getInstance()
	{
		if (instance == null) instance = new CSVImportSessionManager();
		return instance;
	}
	
	protected Logger logger = LoggerFactory.getLogger(CSVImportSessionManager.class);
	
	protected Map<String, CSVImportSession> sessions = new LinkedHashMap<String, CSVImportSession>();
	
	
	public CSVImportSessionManager()
	{
		
	}
	
	protected synchronized String getNewId()
	{
		return UUID.randomUUID().toString();
	}
	
	public CSVImportSession createImportSession(String targetId)
	{
		String id = getNewId();
		
		CSVTargetRegistry csvTargetRegistry = CSVTargetRegistry.getInstance();
		CSVTarget target = csvTargetRegistry.get(targetId);
		if (target == null) throw new IllegalArgumentException("CSVTarget with id "+targetId+" not found.");
		
		CSVImportSession session = new CSVImportSession(id, target);
		sessions.put(id, session);
		
		return session;
	}
	
	public CSVImportSession getSession(String id)
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
