package org.gcube.common.core.contexts;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringWriter;

import org.gcube.common.core.state.GCUBELocalHome;
import org.gcube.common.core.state.GCUBELocalResource;
import org.gcube.common.core.state.GCUBEPublicationProfile;
import org.gcube.common.core.state.GCUBEResourceHome;
import org.gcube.common.core.state.GCUBEWSHome;
import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.common.core.state.GCUBEWSResourceKey;

/**
 * Abstract specialisation of {@link GCUBEPortTypeContext} for stateful services.
 *
 * 
 * @author Fabio Simeoni (University of Strathclyde), Manuele Simi (ISTI-CNR)
 *
 */
public abstract class GCUBEStatefulPortTypeContext extends GCUBEPortTypeContext {

	/** Name of Resource Property Document (RPD) JNDI environment. */
	protected static final String RPDNAME_JNDI_NAME = "RPDName";
	/** Name of WS-resource home JNDI resource. */
	protected static final String WS_HOME_JNDI_NAME = "home";
	/** Name of stateful resource home JNDI resource. */
	protected static final String RESOURCE_HOME_JNDI_NAME = "localhome";
	/** Name of lifetime JNDI environment. */
	protected static final String LIFETIME_JNDI_NAME = "lifeTime";
	/** Name of publication profile JNDI resource. */
	protected static final String PUBLICATIONPROFILE_JNDI_NAME = "publicationProfile";
	/** Legal value of publication mode JNDI environment. */
	protected static final String PUBLICATION_MODE_JNDI_PULL = "pull";
	/** Legal value of publication mode JNDI environment. */
	protected static final String PUBLICATION_MODE_JNDI_PUSH = "push";

	
	/** Publication profile. */
	protected GCUBEPublicationProfile profile;
	/** Publication status. */
	protected boolean publicationStatus=true;
	
	/**{@inheritDoc}*/ 
	@Override protected void onInitialisation() throws Exception {
		super.onInitialisation();
		this.getLocalHome();//force initialisation of local home, if any
	}
	
	/**
	 * Returns the name of the root element of the Resource Property Document in the WSDL of the associated
	 * port-type. The name is configured as the value of a JNDI environment, in accordance with the following
	 * template (text in italics marks points of instantiations):</p>
	 * 
	 * <code>
	 * 	&lt;environment<br> 
		&nbsp;&nbsp;name="RPDName"<br> 
	 	&nbsp;&nbsp;value="[<em>RPD name</em>]"<br> 
	 	&nbsp;&nbsp;type="java.lang.String"<br>
	 	&nbsp;&nbsp;override="false" /&gt;   
	 * </code>
	 * 
	 * @return the element name.
	 */
	public String getRPDName() {
		String rpdName = (String) this.getProperty(RPDNAME_JNDI_NAME,false);
		return rpdName==null?this.getName()+"RPD":rpdName;
	}
	

	/** 
	 * Returns the {@link GCUBEWSHome} associated with the port-type.
	 * The home instance is configured as a JNDI resource, in accordance with with the following
	 * template (text in italics marks points of instantiations):</p>
	 * 
	 * <code>
	 * &lt;resource name="home" type="[<em>the FQN of a sublclass of {@link GCUBEWSHome}</em>]"&gt;<br>
	 *    &nbsp;&nbsp;&lt;resourceParams&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;parameter&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;name&gt;factory&lt;/name&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;value>org.globus.wsrf.jndi.BeanFactory&lt;/value&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;/parameter&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;parameter&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;name>resourceClass&lt;/name&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;value>[<em>FQN of a subclass of {@link GCUBEWSResource}</em>]&lt;/value&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;/parameter&gt;<br>
	 *	&nbsp;&nbsp;&nbsp;&lt;parameter&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;name>persistenceDelegateClass&lt;/name&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;value>[<em>FQN of a concrete subclass of {@link org.gcube.common.core.persistence.GCUBEPersistenceDelegate}</em>]&lt;/value&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;/parameter&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;parameter&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;name>keyName&lt;/name&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;value>[<em>the serialisation of a {@link javax.xml.namespace.QName}</em>]&lt;/value&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;/parameter&gt;<br>
	 *	    &nbsp;&nbsp;&nbsp;&lt;parameter&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;name>sweeperDelay&lt;/name&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;value>[<em>milliseconds</em>]&lt;/value&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;/parameter&gt;<br>
	 *	 &nbsp;&nbsp;&nbsp;&lt;parameter&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;name>cacheTimeout&lt;/name&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;value>[<em>milliseconds, e.g. 120000</em>]&lt;/value&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;/parameter&gt;<br>
	 *	&nbsp;&nbsp;&lt;/resourceParams&gt;<br>
	 *&lt;/resource&gt;
	 *</code>
	 *
	 *	<p> where <code>sweeperDelay</code> and <code>cacheTimeout</code> are optional, and the <code>name</code> of the JNDI resource and its context of occurrence
	 *are to be constrained by clients.
	 *
	 * @return the WS-Resource home. 
	 * 
	 * @see GCUBEResourceHome
	 * @see GCUBEWSHome
	 * 
	 */
	public GCUBEWSHome getWSHome() {
		return (GCUBEWSHome) getProperty(WS_HOME_JNDI_NAME,true);
	}
	
	
	/**
	 * Convenience method to builds a {@link GCUBEWSResourceKey} key from a given value. 
	 * @param value the value.
	 * @return the key.
	 */
	public GCUBEWSResourceKey makeKey(String value) {
		return new GCUBEWSResourceKey(this.getWSHome().getKeyTypeName(),value);
	}
	
	/** 
	 * Returns the {@link GCUBELocalHome} associated with the port-type.
	 * 
	 * The home instance is configured as a JNDI resource, in accordance with with the following
	 * template (text in italics marks points of instantiations):</p>
	 * 
	 * <code>
	 * &lt;resource name="localhome" type="[<em>the FQN of a sublclass of {@link GCUBELocalHome}</em>]"&gt;<br>
	 *    &nbsp;&nbsp;&lt;resourceParams&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;parameter&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;name&gt;factory&lt;/name&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;value>org.globus.wsrf.jndi.BeanFactory&lt;/value&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;/parameter&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;parameter&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;name>resourceClass&lt;/name&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;value>[<em>FQN of a subclass of {@link GCUBELocalResource}</em>]&lt;/value&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;/parameter&gt;<br>
	 *	 &nbsp;&nbsp;&nbsp;&lt;parameter&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;name>cacheTimeout&lt;/name&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;value>[<em>milliseconds, e.g. 120000</em>]&lt;/value&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;/parameter&gt;<br>
	 *	&nbsp;&nbsp;&lt;/resourceParams&gt;<br>
	 *&lt;/resource&gt;
	 *</code>
	 *
	 *	<p> where <code>cacheTimeout</code> is optional.
	 *
	 * @see GCUBEResourceHome
	 * @see GCUBELocalHome
	 * @return the resource home. 
	 */
	public GCUBELocalHome getLocalHome() {return (GCUBELocalHome) getProperty(RESOURCE_HOME_JNDI_NAME);}

	/**
	 * Returns the time after which WS-Resources are <em>scheduled</em> to terminate.
	 * The name is configured as the value of a JNDI environment in accordance with the following
	 * template (text in italics marks points of instantiations):</p>
	 * 
	 * <code>
	 * 	&lt;environment<br> 
		&nbsp;&nbsp;name="lifetime"<br> 
	 	&nbsp;&nbsp;value="[<em>lifetime in seconds</em>]"<br> 
	 	&nbsp;&nbsp;type="java.lang.Integer"<br>
	 	&nbsp;&nbsp;override="false" /&gt;
	 * </code>
	 * 
	 * @return the termination time
	 */
	public Integer getResourceLifeTime() {
		try { 
			return (Integer) this.getProperty(LIFETIME_JNDI_NAME);
		}
		catch (Exception e) {
			return null;
		}
	}
	
	
	/**
	 * Loads and returns the {@link GCUBEPublicationProfile} of the port-type.<br>
	 * 
	 * The publication profile is configured as a JNDI resource in accordance with the following template (text
	 * in italics marks points of instantiations):</p>
	 * 
	 * <code>
	 * &lt;resource name="publicationProfile" type="org.gcube.common.core.service.GCUBEPublicationProfile"&gt;<br>
	 *    &nbsp;&nbsp;&lt;resourceParams&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;parameter&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;name&gt;factory&lt;/name&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;value>org.globus.wsrf.jndi.BeanFactory&lt;/value&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;/parameter&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;parameter&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;name>mode&lt;/name&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;value>[<em>push</em>|<em>pull</em>]&lt;/value&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;/parameter&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;parameter&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;name&gt;fileName&lt;/name&gt;<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;&lt;value&gt;@config.dir@/[<em>configuration file name</em>]&lt;/value&gt;<br>
	 *		&nbsp;&nbsp;&nbsp;&lt;/parameter&gt;<br>
	 *	&nbsp;&nbsp;&lt;/resourceParams&gt;<br>
	 *&lt;/resource&gt;
	 *</code>
	 *
	 * @return the profile, or <code>null</code> if the profile could not be loaded
	 *
	 */
	public synchronized GCUBEPublicationProfile getPublicationProfile() {
		
		//the value of profile and publicationStatus enforce one-time initialisation
		//synchronized enforces it also across multiple threads
		
		if (profile==null && publicationStatus) {//if not already cached
			
			try {
				//fetch profile
				this.profile = (GCUBEPublicationProfile) getProperty(PUBLICATIONPROFILE_JNDI_NAME);
				//sanity checks
				if (profile==null) throw new Exception("configuration was not found.");	
				if (!profile.getMode().equals(PUBLICATION_MODE_JNDI_PULL) && !profile.getMode().equals(PUBLICATION_MODE_JNDI_PUSH))
					throw new Exception("publication mode "+profile.getMode()+" is unknown");
				profile.setAbsolutePath(GHNContext.getContext().getLocation()+File.separator+(String)this.getServiceContext().getProperty(GCUBEServiceContext.CONFIG_DIR_JNDI_NAME));
				StringWriter writer = new StringWriter();
				BufferedReader reader = new BufferedReader(new FileReader(profile.getAbsoluteFileName()));					
				String line; while ((line=reader.readLine())!=null) writer.write(line);
				profile.setProfile(writer.toString());
			    writer.close();reader.close();			    
			}
			catch (Exception warn) {
				logger.warn("Could not configure resource publication because "+warn.getMessage());
				this.publicationStatus=false; //pre-empts further checks
				this.profile=null;
			}
		}
		return profile;
	}



}