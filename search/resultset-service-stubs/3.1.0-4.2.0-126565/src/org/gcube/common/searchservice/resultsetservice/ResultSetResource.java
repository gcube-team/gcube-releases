package org.gcube.common.searchservice.resultsetservice;
 
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.apache.log4j.Logger;
import org.gcube.common.searchservice.searchlibrary.resultset.ResultSet;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.CreationParams;

import org.gcube.common.core.state.GCUBEWSResource;
import org.globus.wsrf.ResourceException;


/**
 * This is the resource kept by the ResultSetService resource home and which
 * contains a reference to the underlying
 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
 * 
 * @author UoA
 */
public class ResultSetResource extends GCUBEWSResource {
	/**
	 * The Logger used by this class
	 */
	private static Logger log = Logger.getLogger(ResultSetResource.class);

	protected static String[] RPNames = new String[0];

	protected ResultSet _rs = null;
	
	/**
	 * Intializes the resource
	 * 
	 * @param properties
	 *            the properties the {@link ResultSet} must have
	 * @param dataFlow
	 *            Wheter or not the {@link ResultSet} should support on demand
	 *            production
	 * @throws Exception
	 *             The resource could not be initialized
	 */
	private void initialize(String[] properties, boolean dataFlow)
			throws Exception {
		try {
			this.setResultSet(new ResultSet(properties, dataFlow));
		} catch (Exception e) {
			log.error("Could not initialize resource. Throwing Exception", e);
			throw new Exception("Could not initialize resource");
		}
	}

	/**
	 * Intializes the resource
	 * 
	 * @param properties
	 *            the properties the {@link ResultSet} must have
	 * @param dataFlow
	 *            Wheter or not the {@link ResultSet} should support on demand
	 *            production
	 * @throws Exception
	 *             The resource could not be initialized
	 */
	private void initializeSerialized(String properties, boolean dataFlow)
			throws Exception {
		try {
			this.setResultSet(new ResultSet(properties, dataFlow));
		} catch (Exception e) {
			log.error("Could not initialize resource. Throwing Exception", e);
			throw new Exception("Could not initialize resource");
		}
	}

	/**
	 * Initializes the resource based on the provided head part
	 * 
	 * @param headFileName
	 *            The name of the local file that holds the {@link ResultSet}
	 *            head part top base the creation of the new {@link ResultSet}
	 * @throws Exception
	 *             The initialization could not be performed
	 */
	private void initialize(String headFileName) throws Exception {
		try {
			setResultSet(new ResultSet(headFileName));
		} catch (Exception e) {
			log.error("Could not initialize resource. Throwinhg Exception", e);
			throw new Exception("Could not initialize resource");
		}
	}

	/**
	 * Initializes the resource based on the provided head part
	 * 
	 * @param headFileName
	 *            The name of the local file that holds the {@link ResultSet}
	 *            head part top base the creation of the new {@link ResultSet}
	 * @throws Exception
	 *             The initialization could not be performed
	 */
	private void initialize(String headFileName, PrivateKey pk) throws Exception {
		try {
			setResultSet(new ResultSet(headFileName,pk));
		} catch (Exception e) {
			log.error("Could not initialize resource. Throwinhg Exception", e);
			throw new Exception("Could not initialize resource");
		}
	}

	/**
	 * Retrieves the underlying {@link ResultSet}
	 * 
	 * @return The underlying {@link ResultSet}
	 */
	public ResultSet getResultSet() {
		return this._rs;
	}

	/**
	 * Sets the underlying {@link ResultSet}
	 * 
	 * @param value The {@link ResultSet}
	 * @throws Exception Failure
	 *            
	 */
	public synchronized void setResultSet(ResultSet value) throws Exception{
		this._rs = value;
	}

	/**
	 * Intializes the resource
	 * 
	 * @param properties
	 *            the properties the {@link ResultSet} must have
	 * @param dataFlow
	 *            Wheter or not the {@link ResultSet} should support on demand
	 *            production
	 * @throws Exception
	 *             The resource could not be initialized
	 */
	private void initialize(CreationParams createParams)
			throws Exception {
		try {
			this.setResultSet(new ResultSet(createParams));
		} catch (Exception e) {
			log.error("Could not initialize resource. Throwing Exception", e);
			throw new Exception("Could not initialize resource");
		}
	}


	
	@Override
	protected void initialise(Object... args) throws ResourceException {
		
		log.debug("Into ResultSetResource.initialise() method...");

		if (args.length != 1 && args.length != 2 && args.length != 6)
			throw new IllegalArgumentException(
					"The initialise() method of the ResultSetResource should only take one or two arguments."
							+ "\nConsequently, the ResourceHome create() method should only take one argument of type Object[] of size 1 or 2.");

		try {

//			ResourceProperty prop = new SimpleResourceProperty(ResultSetQNames.RP_RESULTSET);
//			this.getResourcePropertySet().add(prop);

			if(args.length == 1){
				//Creation of the global Result Set Service resource that expresses the Garbage Collector 
				//if a Boolean argument is passed to create()
				if(args[0] instanceof Boolean)
					log.info("Creation of the global Result Set Service resource that expresses the Garbage Collector");
				else
					initialize((String)args[0]);
			}else if(args.length == 2) {
				if(args[0] instanceof String) {
					if (args[1] instanceof Boolean){
						String properties = (String) args[0];
						boolean dataFlow = ((Boolean) args[1]).booleanValue();
						initializeSerialized(properties, dataFlow);
					}else{
						String headerFileName = (String) args[0];
						if (args[1] != null){
							byte[] encodedKey = (byte[])args[1];
							byte[] rawkey = new sun.misc.BASE64Decoder().decodeBuffer(new String(encodedKey));
							PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(rawkey);
				            KeyFactory factory = KeyFactory.getInstance("RSA");
							RSAPrivateKey pk = (RSAPrivateKey)factory.generatePrivate(spec);
							initialize(headerFileName,pk);
						}else{
							initialize(headerFileName);							
						}
					}
				} else if(args[0] instanceof String[]) {
					String[] properties = (String[]) args[0];
					boolean dataFlow = ((Boolean) args[1]).booleanValue();
					initialize(properties, dataFlow);
				}
			}else if(args.length == 6) {
				CreationParams createParams = new CreationParams();
				createParams.properties = new ArrayList<String>(Arrays.asList((String[]) args[0]));
				createParams.setDataflow(((Boolean) args[1]).booleanValue());
				createParams.setAccessReads(((Integer) args[2]).intValue());
				createParams.setForward(((Boolean) args[3]).booleanValue());
				Date expire_date = new Date(0);
				expire_date.setTime(((Long) args[4]).longValue());
				createParams.setExpire_date(expire_date);
				if (args[5] != null){
					byte[] encodedKey = (byte[])args[5];
					byte[] rawkey = new sun.misc.BASE64Decoder().decodeBuffer(new String(encodedKey));
					X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(rawkey);
					KeyFactory kf = KeyFactory.getInstance("RSA");
					PublicKey pk = (RSAPublicKey)kf.generatePublic(publicKeySpec);
					createParams.setPKey(pk);
				}
				initialize(createParams);
			}
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}

}
