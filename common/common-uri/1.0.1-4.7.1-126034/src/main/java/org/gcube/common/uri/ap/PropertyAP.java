package org.gcube.common.uri.ap;

/**
 * An {@link AuthorityProvider} that returns the authority specified as the value of the system property {@link #AUTHORITY_PROPERTY}.
 * @author Fabio Simeoni
 *
 */
public class PropertyAP implements AuthorityProvider {

	/** The system property for the naming authority. */
	public static final String AUTHORITY_PROPERTY="org.gcube.common.uri.authority";
	
	@Override
	public String authority() {
		
		String authority = System.getProperty(AUTHORITY_PROPERTY);
		
		if (authority==null) 
			throw new IllegalStateException("property " + AUTHORITY_PROPERTY +" is undefined");
		
		return authority;
	}
}
