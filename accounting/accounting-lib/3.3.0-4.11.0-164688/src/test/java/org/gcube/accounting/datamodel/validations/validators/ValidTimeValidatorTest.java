/**
 * 
 */
package org.gcube.accounting.datamodel.validations.validators;

import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.implementation.validations.validators.ValidLongValidator;
import org.junit.Test;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class ValidTimeValidatorTest {

	@Test
	public void testPrimitiveLong() throws InvalidValueException{
		ValidLongValidator validTimeValidator = new ValidLongValidator();
		long myLong = 4;
		validTimeValidator.validate(null, myLong, null);
	}
	
	@Test
	public void testClassLong() throws InvalidValueException{
		ValidLongValidator validTimeValidator = new ValidLongValidator();
		Long myLong = new Long(4);
		validTimeValidator.validate(null, myLong, null);
	}
	
	@Test(expected=InvalidValueException.class)
	public void testWrongValue() throws InvalidValueException {
		ValidLongValidator validTimeValidator = new ValidLongValidator();
		validTimeValidator.validate(null, "test", null);
	}
	
}
