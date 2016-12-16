/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.resources.impl;

import java.io.IOException;
import java.util.UUID;

import org.gcube.informationsystem.impl.utils.Entities;
import org.gcube.informationsystem.model.entity.Context;
import org.gcube.informationsystem.resourceregistry.api.exceptions.InternalException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextCreationException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class ContextManagementImplTest {

	private static Logger logger = LoggerFactory
			.getLogger(ContextManagementImplTest.class);

	protected ContextManagementImpl contextManagementImpl;

	/*
	 * private static final String VALID_CHARACTER =
	 * "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"; private
	 * static final SecureRandom SECURE_RANDOM = new SecureRandom();
	 * 
	 * private String randomString(int lenght) { StringBuilder sb = new
	 * StringBuilder(lenght); for (int i = 0; i < lenght; i++) {
	 * sb.append(VALID_CHARACTER.charAt(SECURE_RANDOM
	 * .nextInt(VALID_CHARACTER.length()))); } return sb.toString(); }
	 */

	public ContextManagementImplTest() {
		contextManagementImpl = new ContextManagementImpl();
	}

	public static final String CTX_NAME_A = "A";
	public static final String CTX_NAME_B = "B";
	public static final String CTX_NAME_C = "C";

	protected void invalidCreation(UUID parentUUID, String name)
			throws InternalException {
		try {
			contextManagementImpl.create(parentUUID, name);
			throw new RuntimeException("The context " + name + " with parent "
					+ parentUUID + " was already created and MUST throw "
					+ ContextCreationException.class.getSimpleName()
					+ ". This is a bug in your code.");
		} catch (ContextCreationException e) {
			logger.debug(
					"As expected the context {} with parent {} was already created and a {} has been thrown",
					name, parentUUID, e.getClass().getSimpleName());
		}
	}

	protected void invalidMoving(UUID parentUUID, UUID contextToMove) {
		try {
			contextManagementImpl.move(parentUUID, contextToMove);
			throw new RuntimeException("The context " + contextToMove
					+ " with parent " + parentUUID
					+ " was already created and MUST throw "
					+ ContextException.class.getSimpleName()
					+ ". This is a bug in your code.");
		} catch (ContextNotFoundException e) {
			throw new RuntimeException(e);
		} catch (ContextException e) {
			logger.debug(
					"As expected the context {} with parent {} was already created and a {} has been thrown",
					contextToMove, parentUUID, e.getClass().getSimpleName());
		}
	}

	protected void invalidRename(UUID uuid, String newName) {
		try {
			contextManagementImpl.rename(uuid, newName);
			throw new RuntimeException("The context with uuid " + uuid
					+ " cannot be renamed to " + newName + " and MUST throw "
					+ ContextException.class.getSimpleName()
					+ ". This is a bug in your code.");
		} catch (ContextNotFoundException e) {
			throw new RuntimeException(e);
		} catch (ContextException e) {
			logger.debug(
					"As expected the context with uuid {} cannot be renamed to {} and a {} has been thrown",
					uuid, newName, e.getClass().getSimpleName());
		}
	}

	protected void invalidDelete(UUID uuid) {
		try {
			contextManagementImpl.delete(uuid);
			throw new RuntimeException("The context with uuid " + uuid
					+ " cannot be deleted and MUST throw "
					+ ContextException.class.getSimpleName()
					+ ". This is a bug in your code.");
		} catch (ContextNotFoundException e) {
			throw new RuntimeException(e);
		} catch (ContextException e) {
			logger.debug(
					"As expected the context with uuid {} cannot be deleted and a {} has been thrown",
					uuid, e.getClass().getSimpleName());
		}
	}

	@Test
	public void simpleTest() throws ContextNotFoundException, ContextException,
			InternalException, JsonParseException, JsonMappingException,
			IOException {
		String contextJsonA1 = contextManagementImpl.create(null, CTX_NAME_A);
		Context createdContexA1 = Entities.unmarshal(Context.class,
				contextJsonA1);
		UUID A_1 = createdContexA1.getHeader().getUUID();
		logger.info("{}", contextJsonA1);

		/*
		 * A(1)
		 */

		String contextJsonA2 = contextManagementImpl.create(A_1, CTX_NAME_A);
		Context createdContexA2 = Entities.unmarshal(Context.class,
				contextJsonA2);
		logger.info("{}", contextJsonA2);
		UUID A_2 = createdContexA2.getHeader().getUUID();
		/*
		 * A_1 A_2
		 */

		String contextJsonB3 = contextManagementImpl.create(A_2, CTX_NAME_B);
		Context createdContexB3 = Entities.unmarshal(Context.class,
				contextJsonB3);
		logger.info("{}", contextJsonB3);
		UUID B_3 = createdContexB3.getHeader().getUUID();
		/*
		 * A_1 A_2 B_3
		 */

		contextJsonB3 = contextManagementImpl.move(A_1, B_3);
		createdContexB3 = Entities.unmarshal(Context.class, contextJsonB3);
		logger.info("{}", contextJsonB3);

		/*
		 * A_1 A_2 B_3
		 */

		contextJsonB3 = contextManagementImpl.rename(B_3, CTX_NAME_C);
		createdContexB3 = Entities.unmarshal(Context.class, contextJsonB3);
		logger.info("{}", contextJsonB3);
		/*
		 * A_1 A_2 C_3
		 */

		contextJsonB3 = contextManagementImpl.rename(B_3, CTX_NAME_B);
		createdContexB3 = Entities.unmarshal(Context.class, contextJsonB3);
		logger.info("{}", contextJsonB3);
		/*
		 * A_1 A_2 B_3
		 */

		contextJsonB3 = contextManagementImpl.move(A_2, B_3);
		createdContexB3 = Entities.unmarshal(Context.class, contextJsonB3);
		logger.info("{}", contextJsonB3);
		/*
		 * A_1 A_2 B_3
		 */

		boolean deleted = contextManagementImpl.delete(B_3);
		Assert.assertTrue(deleted);
		/*
		 * A_1 A_2
		 */

		deleted = contextManagementImpl.delete(A_2);
		Assert.assertTrue(deleted);
		/*
		 * A_1
		 */

		deleted = contextManagementImpl.delete(A_1);
		Assert.assertTrue(deleted);
		/*
		 * 
		 */

		logger.debug("The DB should be now clean");
	}
	
	@Test
	public void createContext() throws ContextNotFoundException,
			ContextException, InternalException, JsonParseException,
			JsonMappingException, IOException {
		String name = "test";
		
		String testJson = contextManagementImpl.create(null, name);
		Context testContext = Entities.unmarshal(Context.class, testJson);
		Assert.assertTrue(testContext.getName().compareTo(name)==0);
		UUID testUUID = testContext.getHeader().getUUID();
		logger.trace("/test : {}", testUUID);
		
		boolean deleted = contextManagementImpl.delete(testUUID);
		Assert.assertTrue(deleted);
		logger.debug("The DB should be now clean");
	}
	
	//@Test
	public void createDevContext() throws ContextNotFoundException,
			ContextException, InternalException, JsonParseException,
			JsonMappingException, IOException {
		String gcubeJson = contextManagementImpl.create(null, "gcube");
		Context gcubeContext = Entities.unmarshal(Context.class, gcubeJson);
		UUID gcube = gcubeContext.getHeader().getUUID();
		logger.trace("/gcube : {}", gcubeJson);

		String devsecJson = contextManagementImpl.create(gcube, "devsec");
		Context devsecContex = Entities.unmarshal(Context.class, devsecJson);
		UUID devsec = devsecContex.getHeader().getUUID();
		logger.trace("/gcube/devsec : {}", devsecJson);

		String devVREJson = contextManagementImpl.create(devsec, "devVRE");
		Context devVREContex = Entities.unmarshal(Context.class, devVREJson);
		@SuppressWarnings("unused")
		UUID devVRE = devVREContex.getHeader().getUUID();
		logger.trace("/gcube/devsec/devVRE : {}", devVREJson);

		String devNextJson = contextManagementImpl.create(gcube, "devNext");
		Context devNextContex = Entities.unmarshal(Context.class, devNextJson);
		UUID devNext = devNextContex.getHeader().getUUID();
		logger.trace("/gcube/devNext : {}", devNextJson);

		String NextNextJson = contextManagementImpl.create(devNext, "NextNext");
		Context NextNextContex = Entities.unmarshal(Context.class, NextNextJson);
		@SuppressWarnings("unused")
		UUID NextNext = NextNextContex.getHeader().getUUID();
		logger.trace("/gcube/devNext/NextNext : {}", NextNextJson);

		/*
		 * contextManagementImpl.delete(NextNext);
		 * contextManagementImpl.delete(devNext);
		 * contextManagementImpl.delete(devVRE);
		 * contextManagementImpl.delete(devsec);
		 * contextManagementImpl.delete(gcube);
		 */

		//logger.debug("The DB should be now clean");
	}

	//@Test
	public void removeContext() throws ContextNotFoundException,
			ContextException, InternalException {
		/*
		contextManagementImpl.delete(UUID
				.fromString(""));
		contextManagementImpl.delete(UUID
				.fromString(""));
		contextManagementImpl.delete(UUID
				.fromString(""));
		contextManagementImpl.delete(UUID
				.fromString(""));
		contextManagementImpl.delete(UUID
				.fromString(""));
		*/
	}

	@Test
	public void readTest() throws ContextNotFoundException, ContextException,
			InternalException, JsonParseException, JsonMappingException,
			IOException {
		String name = "LLL";
		String contextJsonA1 = contextManagementImpl.create(null, name);
		Context createdContexA1 = Entities.unmarshal(Context.class,
				contextJsonA1);
		UUID A_1 = createdContexA1.getHeader().getUUID();
		logger.info("{}", contextJsonA1);
		Assert.assertTrue(createdContexA1.getName().compareTo(name) == 0);

		String readContextJsonA1 = contextManagementImpl.read(A_1);
		Context readContexA1 = Entities.unmarshal(Context.class,
				readContextJsonA1);
		Assert.assertTrue(readContexA1.getName().compareTo(name) == 0);
		Assert.assertTrue(A_1.compareTo(readContexA1.getHeader().getUUID()) == 0);
		logger.trace("{}", createdContexA1);

		boolean deleted = contextManagementImpl.delete(A_1);
		Assert.assertTrue(deleted);
	}

	@Test
	public void completeTest() throws ContextNotFoundException,
			ContextException, InternalException, JsonParseException,
			JsonMappingException, IOException {
		String contextJsonA1 = contextManagementImpl.create(null, CTX_NAME_A);
		Context createdContexA1 = Entities.unmarshal(Context.class,
				contextJsonA1);
		UUID A_1 = createdContexA1.getHeader().getUUID();
		logger.info("{}", contextJsonA1);
		/*
		 * A(1)
		 */

		String contextJsonA2 = contextManagementImpl.create(A_1, CTX_NAME_A);
		Context createdContexA2 = Entities.unmarshal(Context.class,
				contextJsonA2);
		logger.info("{}", contextJsonA2);
		UUID A_2 = createdContexA2.getHeader().getUUID();

		/*
		 * A_1 A_2
		 */

		String contextJsonB3 = contextManagementImpl.create(A_2, CTX_NAME_B);
		Context createdContexB3 = Entities.unmarshal(Context.class,
				contextJsonB3);
		logger.info("{}", contextJsonB3);
		UUID B_3 = createdContexB3.getHeader().getUUID();
		/*
		 * A_1 A_2 B_3
		 */

		String contextJsonB4 = contextManagementImpl.create(A_1, CTX_NAME_B);
		Context createdContexB4 = Entities.unmarshal(Context.class,
				contextJsonB4);
		logger.info("{}", contextJsonB4);
		UUID B_4 = createdContexB4.getHeader().getUUID();
		/*
		 * A_1 A_2 B_4 B_3
		 */

		String contextJsonA5 = contextManagementImpl.create(B_4, CTX_NAME_A);
		Context createdContexA5 = Entities.unmarshal(Context.class,
				contextJsonA5);
		logger.info("{}", contextJsonA5);
		UUID A_5 = createdContexA5.getHeader().getUUID();
		/*
		 * A_1 A_2 B_4 B_3 A_5
		 */

		invalidCreation(null, CTX_NAME_A); // Trying to recreate A_1. Fails
		invalidCreation(A_1, CTX_NAME_A); // Trying to recreate A_2. Fails
		invalidCreation(A_2, CTX_NAME_B); // Trying to recreate B_3. Fails
		invalidCreation(A_1, CTX_NAME_B); // Trying to recreate B_4. Fails
		invalidCreation(B_4, CTX_NAME_A); // Trying to recreate A_5. Fails

		// Trying to move B_3 as child of A_1. It fails due to B_4. Fails
		invalidMoving(A_1, B_3);
		// Trying to move A_5 as child of A_1. It fails due to A_2. Fails
		invalidMoving(A_1, A_5);

		// Moving B_3 as child of B_4. OK
		String movedContextJsonB3 = contextManagementImpl.move(B_4, B_3);
		Context movedContexB3 = Entities.unmarshal(Context.class,
				movedContextJsonB3);
		Assert.assertTrue(B_3.compareTo(movedContexB3.getHeader().getUUID()) == 0);
		logger.info("{}", contextJsonB3);
		/*
		 * A_1 A_2 B_4 B_3 A_5
		 */

		// Restoring the initial situation by moving B_3 as child of A_2
		String movedAgainJsonB3 = contextManagementImpl.move(A_2, B_3);
		Context movedAgainContexB3 = Entities.unmarshal(Context.class,
				movedAgainJsonB3);
		Assert.assertTrue(B_3.compareTo(movedAgainContexB3.getHeader()
				.getUUID()) == 0);
		logger.info("{}", contextJsonB3);
		/*
		 * A_1 A_2 B_4 B_3 A_5
		 */

		// Trying to move B_3 as child of A_1. It fails due to B_4. Fails
		invalidMoving(A_1, B_3);

		// Renaming B_3 as C_3
		String contextJsonC3 = contextManagementImpl.rename(B_3, CTX_NAME_C);
		Context createdContexC3 = Entities.unmarshal(Context.class,
				contextJsonC3);
		logger.info("{}", contextJsonC3);
		UUID C_3 = createdContexC3.getHeader().getUUID();
		Assert.assertTrue(C_3.compareTo(B_3) == 0);
		/*
		 * A_1 A_2 B_4 C_3 A_5
		 */

		// Moving C_3 (was B_3) as child of A_1. Now it is possible
		contextJsonC3 = contextManagementImpl.move(A_1, C_3);
		createdContexC3 = Entities.unmarshal(Context.class, contextJsonC3);
		logger.info("{}", contextJsonC3);
		/*
		 * A_1 C_3 A_2 B_4 A_5
		 */

		// Trying to rename C_3 (was B_3) newly to B_3. Fails due to B_4
		invalidRename(C_3, CTX_NAME_B);

		// Moving back C_3 (was B_3) as child of A_2.
		contextJsonB3 = contextManagementImpl.move(A_2, C_3);
		createdContexB3 = Entities.unmarshal(Context.class, contextJsonB3);
		logger.info("{}", contextJsonB3);

		String contextJsonBC3 = contextManagementImpl.rename(C_3, CTX_NAME_B);
		Context createdContexBC3 = Entities.unmarshal(Context.class,
				contextJsonBC3);
		logger.info("{}", contextJsonBC3);
		UUID BC_3 = createdContexBC3.getHeader().getUUID();
		Assert.assertTrue(BC_3.compareTo(B_3) == 0);
		Assert.assertTrue(BC_3.compareTo(C_3) == 0);
		/*
		 * A_1 A_2 B_4 B_3 A_5
		 */

		invalidDelete(A_1);
		invalidDelete(A_2);
		invalidDelete(B_4);
		boolean deleted = contextManagementImpl.delete(A_5);
		Assert.assertTrue(deleted);
		/*
		 * A_1 A_2 B_4 B_3
		 */
		try {
			contextManagementImpl.delete(A_5);
		} catch (ContextNotFoundException e) {
			logger.debug(
					"The context with uuid {} was not found. (Was already deleted)",
					A_5);
		}

		deleted = contextManagementImpl.delete(B_3);
		Assert.assertTrue(deleted);
		/*
		 * A_1 A_2 B_4
		 */
		invalidDelete(A_1);
		deleted = contextManagementImpl.delete(B_4);
		Assert.assertTrue(deleted);
		/*
		 * A_1 A_2
		 */
		deleted = contextManagementImpl.delete(A_2);
		Assert.assertTrue(deleted);
		/*
		 * A_1
		 */

		deleted = contextManagementImpl.delete(A_1);
		Assert.assertTrue(deleted);

		logger.debug("The DB should be now clean");
	}

	@Test
	public void moveToRootTest() throws ContextNotFoundException,
			ContextException, InternalException, JsonParseException,
			JsonMappingException, IOException {
		String contextJsonA1 = contextManagementImpl.create(null, CTX_NAME_A);
		Context createdContexA1 = Entities.unmarshal(Context.class,
				contextJsonA1);
		UUID A_1 = createdContexA1.getHeader().getUUID();
		logger.info("{}", contextJsonA1);
		/*
		 * A(1)
		 */

		String contextJsonB2 = contextManagementImpl.create(A_1, CTX_NAME_B);
		Context createdContexB2 = Entities.unmarshal(Context.class,
				contextJsonB2);
		logger.info("{}", contextJsonB2);
		UUID B_2 = createdContexB2.getHeader().getUUID();

		/*
		 * A_1 B_2
		 */

		String movedContextJsonB2 = contextManagementImpl.move(null, B_2);
		Context movedContexB2 = Entities.unmarshal(Context.class,
				movedContextJsonB2);
		Assert.assertTrue(B_2.compareTo(movedContexB2.getHeader().getUUID()) == 0);
		contextManagementImpl.delete(A_1);
		contextManagementImpl.delete(B_2);
		logger.debug("The DB should be now clean");
	}

}
