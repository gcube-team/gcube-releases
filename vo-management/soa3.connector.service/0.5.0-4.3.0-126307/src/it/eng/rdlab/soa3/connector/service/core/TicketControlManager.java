package it.eng.rdlab.soa3.connector.service.core;


/**
 * 
 * Ticket control manager singleton
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class TicketControlManager extends AccessControlManager
{
	private static TicketControlManager instance;

	
	private TicketControlManager ()
	{
		super ();
	}
	
	public static TicketControlManager getInstance ()
	{
		if (instance == null) instance = new TicketControlManager();
		
		return instance;
	}
	

	
}
