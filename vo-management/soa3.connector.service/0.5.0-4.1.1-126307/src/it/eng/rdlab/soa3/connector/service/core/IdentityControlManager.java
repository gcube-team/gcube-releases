package it.eng.rdlab.soa3.connector.service.core;


/**
 * 
 * Identity control manager singleton
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class IdentityControlManager extends AccessControlManager
{
	private static IdentityControlManager instance;

	
	private IdentityControlManager ()
	{
		super ();
	}
	
	public static IdentityControlManager getInstance ()
	{
		if (instance == null) instance = new IdentityControlManager();
		
		return instance;
	}
	

	
}
