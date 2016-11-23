/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.resources.impl;

import org.gcube.informationsystem.model.entity.facet.ContactFacet;
import org.gcube.informationsystem.model.entity.resource.Service;
import org.gcube.informationsystem.model.relation.isrelatedto.Runs;
import org.gcube.informationsystem.resourceregistry.api.exceptions.InternalException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextCreationException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.gcube.informationsystem.resourceregistry.dbinitialization.DatabaseIntializator;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * 
 */
public class ContextManagementImplTest {
	
	private static Logger logger = LoggerFactory.getLogger(ContextManagementImplTest.class);
	
	protected ContextManagementImpl contextManagementImpl;
	
	/*
	private static final String VALID_CHARACTER = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	private String randomString(int lenght) {
		StringBuilder sb = new StringBuilder(lenght);
		for (int i = 0; i < lenght; i++) {
			sb.append(VALID_CHARACTER.charAt(SECURE_RANDOM
					.nextInt(VALID_CHARACTER.length())));
		}
		return sb.toString();
	}
	*/
	
	public ContextManagementImplTest(){
		contextManagementImpl = new ContextManagementImpl();
		DatabaseIntializator.addPackage(Service.class.getPackage());
		DatabaseIntializator.addPackage(Runs.class.getPackage());
		DatabaseIntializator.addPackage(ContactFacet.class.getPackage());
	}
	
	public static final String CTX_NAME_A = "A";
	public static final String CTX_NAME_B = "B";
	public static final String CTX_NAME_C = "C";
	
	protected void invalidCreation(String parentUUID, String name) throws InternalException {
		try {
			contextManagementImpl.create(parentUUID, name);
			throw new RuntimeException("The context " + name + " with parent " + parentUUID + " was already created and MUST throw " + ContextCreationException.class.getSimpleName() + ". This is a bug in your code.");
		} catch (ContextCreationException e) {
			logger.debug("As expected the context {} with parent {} was already created and a {} has been thrown", name, parentUUID, e.getClass().getSimpleName());
		}
	}
	
	protected void invalidMoving(String parentUUID, String name){
		try {
			contextManagementImpl.move(parentUUID, name);
			throw new RuntimeException("The context " + name + " with parent " + parentUUID + " was already created and MUST throw " + ContextException.class.getSimpleName() + ". This is a bug in your code.");
		} catch (ContextNotFoundException e) {
			throw new RuntimeException(e);
		} catch (ContextException e) {
			logger.debug("As expected the context {} with parent {} was already created and a {} has been thrown", name, parentUUID, e.getClass().getSimpleName());
		} 
	}
	
	protected void invalidRename(String uuid, String newName){
		try {
			contextManagementImpl.rename(uuid, newName);
			throw new RuntimeException("The context with uuid " + uuid + " cannot be renamed to " + newName + " and MUST throw " + ContextException.class.getSimpleName() + ". This is a bug in your code.");
		} catch (ContextNotFoundException e) {
			throw new RuntimeException(e);
		} catch (ContextException e) {
			logger.debug("As expected the context with uuid {} cannot be renamed to {} and a {} has been thrown", uuid, newName, e.getClass().getSimpleName());
		} 
	}
	
	
	protected void invalidDelete(String uuid){
		try {
			contextManagementImpl.delete(uuid);
			throw new RuntimeException("The context with uuid " + uuid + " cannot be deleted and MUST throw " + ContextException.class.getSimpleName() + ". This is a bug in your code.");
		} catch (ContextNotFoundException e) {
			throw new RuntimeException(e);
		} catch (ContextException e) {
			logger.debug("As expected the context with uuid {} cannot be deleted and a {} has been thrown", uuid, e.getClass().getSimpleName());
		} 
	}
	
	@Test
	public void simpleTest() throws ContextNotFoundException, ContextException, InternalException {
		String A_1 = contextManagementImpl.create(null, CTX_NAME_A);
		/*
		 * 			A(1)
		 */
		
		String A_2 = contextManagementImpl.create(A_1, CTX_NAME_A);
		/*
		 * 			A_1
		 *		 A_2   
		 */
		String B_3 = contextManagementImpl.create(A_2, CTX_NAME_B);
		/*
		 * 			A_1
		 *		 A_2   
		 * 	  B_3     
		 */
		
		contextManagementImpl.move(A_1, B_3);
		/*
		 * 			A_1
		 *		 A_2   B_3
		 */
		
		contextManagementImpl.rename(B_3, CTX_NAME_C);
		/*
		 * 			A_1
		 *		 A_2   C_3
		 */
		
		contextManagementImpl.rename(B_3, CTX_NAME_B);
		/*
		 * 			A_1
		 *		 A_2   C_3
		 */
		
		contextManagementImpl.move(A_2, B_3);
		/*
		 * 			A_1
		 *		 A_2   
		 * 	  B_3     
		 */
		
		contextManagementImpl.delete(B_3);
		/*
		 * 			A_1
		 *		 A_2      
		 */
		contextManagementImpl.delete(A_2);
		/*
		 * 			A_1 
		 */
		contextManagementImpl.delete(A_1);
		/*
		 * 
		 */
		logger.debug("The DB should be now clean");
	}
	
	
	//@Test
	public void createDevContext() throws ContextNotFoundException, ContextException, InternalException {
		String gcube =  contextManagementImpl.create(null, "gcube");
		logger.trace("/gcube : {}", gcube);
		String devsec =  contextManagementImpl.create(gcube, "devsec");
		logger.trace("/gcube/devsec : {}", devsec);
		String devVRE =  contextManagementImpl.create(devsec, "devVRE");
		logger.trace("/gcube/devsec/devVRE : {}", devVRE);
		String devNext =  contextManagementImpl.create(gcube, "devNext");
		logger.trace("/gcube/devNext : {}", devNext);
		String NextNext =  contextManagementImpl.create(devNext, "NextNext");
		logger.trace("/gcube/devNext/NextNext : {}", NextNext);
		
		/*
		contextManagementImpl.delete(NextNext);
		contextManagementImpl.delete(devNext);
		contextManagementImpl.delete(devVRE);
		contextManagementImpl.delete(devsec);
		contextManagementImpl.delete(gcube);
		*/
		
		logger.debug("The DB should be now clean");
	}
	
	//@Test
	public void removeDevContext() throws ContextNotFoundException, ContextException, InternalException {
		contextManagementImpl.delete("508da10a-b8e7-414f-b176-65e227addcae");
		contextManagementImpl.delete("d729c993-32f9-4bcd-9311-d92f791e5e12");
		contextManagementImpl.delete("8bf286f8-a93c-4936-9826-91f1b1c2af34");
		contextManagementImpl.delete("7273c1c7-bc06-4d49-bade-19dc3ab322f7");
		contextManagementImpl.delete("132691d2-e22d-4e3b-b605-f8b2c94f2c77");
	}
	
	@Test
	public void readTest() throws ContextNotFoundException, ContextException, InternalException {
		String A_1 = contextManagementImpl.create(null, "LLL");
		String read = contextManagementImpl.read(A_1);
		logger.trace("A : {}", read);
		contextManagementImpl.delete(A_1);
	}
	
	@Test
	public void completeTest() throws ContextNotFoundException, ContextException, InternalException {
		String A_1 = contextManagementImpl.create(null, CTX_NAME_A);
		/*
		 * 			A(1)
		 */
		
		String A_2 = contextManagementImpl.create(A_1, CTX_NAME_A);
		/*
		 * 			A_1
		 *		 A_2   
		 */
		
		String B_3 = contextManagementImpl.create(A_2, CTX_NAME_B);
		/*
		 * 			A_1
		 *		 A_2   
		 * 	  B_3     
		 */
		
		
		String B_4 = contextManagementImpl.create(A_1, CTX_NAME_B);
		/*
		 * 			A_1
		 *		 A_2   B_4
		 * 	  B_3      
		 */
		
		String A_5 = contextManagementImpl.create(B_4, CTX_NAME_A);
		/*
		 * 			A_1
		 *		 A_2   B_4
		 * 	  B_3         A_5
		 */
		
		
		invalidCreation(null, CTX_NAME_A); // Trying to recreate A_1. Fails
		invalidCreation(A_1, CTX_NAME_A);  // Trying to recreate A_2. Fails
		invalidCreation(A_2, CTX_NAME_B);  // Trying to recreate B_3. Fails
		invalidCreation(A_1, CTX_NAME_B);  // Trying to recreate B_4. Fails
		invalidCreation(B_4, CTX_NAME_A);  // Trying to recreate A_5. Fails
		
		
		// Trying to move B_3 as child of A_1. It fails due to B_4. Fails
		invalidMoving(A_1, B_3);
		// Trying to move A_5 as child of A_1. It fails due to A_2. Fails
		invalidMoving(A_1, A_5);
		
		
		
		// Moving B_3 as child of B_4. OK
		contextManagementImpl.move(B_4, B_3);
		/*
		 * 			A_1
		 *		A_2     B_4
		 * 	         B_3   A_5
		 */
		
		// Restoring the initial situation by moving B_3 as child of A_2
		contextManagementImpl.move(A_2, B_3);
		/*
		 * 			A_1
		 *		 A_2   B_4
		 * 	  B_3         A_5
		 */
		
		
		// Trying to move B_3 as child of A_1. It fails due to B_4. Fails
		invalidMoving(A_1, B_3);
		
		// Renaming B_3 as C_3
		String C_3  = contextManagementImpl.rename(B_3, CTX_NAME_C);
		Assert.assertTrue(C_3.compareTo(B_3)==0);
		/*
		 * 			A_1
		 *		 A_2   B_4
		 * 	  C_3         A_5
		 */
		
		// Moving C_3 (was B_3) as child of A_1. Now it is possible
		contextManagementImpl.move(A_1, B_3);
		/*
		 * 			A_1
		 *	   C_3  A_2   B_4
		 * 	                 A_5
		 */
		
		
		
		// Trying to rename C_3 (was B_3) newly to B_3. Fails due to B_4
		invalidRename(C_3, CTX_NAME_B);

		
		// Moving back C_3 (was B_3) as child of A_2.
		contextManagementImpl.move(A_2, B_3);
		String BC_3 = contextManagementImpl.rename(C_3, CTX_NAME_B);
		Assert.assertTrue(BC_3.compareTo(B_3)==0);
		Assert.assertTrue(BC_3.compareTo(C_3)==0);
		/*
		 * 			A_1
		 *		 A_2   B_4
		 * 	  B_3         A_5
		 */
		
		
		invalidDelete(A_1);
		invalidDelete(A_2);
		invalidDelete(B_4);
		contextManagementImpl.delete(A_5);
		/*
		 * 			A_1
		 *		 A_2   B_4
		 * 	  B_3         
		 */
		try {
			contextManagementImpl.delete(A_5);
		} catch(ContextNotFoundException e){
			logger.debug("The context with uuid {} was not found. (Was already deleted)", A_5);
		} 
		
		contextManagementImpl.delete(B_3);
		/*
		 * 			A_1
		 *		 A_2   B_4         
		 */
		invalidDelete(A_1);
		contextManagementImpl.delete(B_4);
		/*
		 * 			A_1
		 *		 A_2        
		 */
		contextManagementImpl.delete(A_2);
		/*
		 * 			A_1      
		 */
		
		contextManagementImpl.delete(A_1);
		
		
		logger.debug("The DB should be now clean");
	}
	
	
	@Test
	public void moveToRootTest() throws ContextNotFoundException, ContextException, InternalException {
		String A_1 = contextManagementImpl.create(null, CTX_NAME_A);
		String A_2 = contextManagementImpl.create(A_1, CTX_NAME_B);
		contextManagementImpl.move(null, A_2);
		contextManagementImpl.delete(A_1);
		contextManagementImpl.delete(A_2);
		logger.debug("The DB should be now clean");
	}

}
