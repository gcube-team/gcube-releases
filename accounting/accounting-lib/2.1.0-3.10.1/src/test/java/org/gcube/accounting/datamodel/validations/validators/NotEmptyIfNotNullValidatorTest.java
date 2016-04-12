/**
 * 
 */
package org.gcube.accounting.datamodel.validations.validators;

import java.io.Serializable;

import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.implementation.validations.validators.NotEmptyIfNotNullValidator;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class NotEmptyIfNotNullValidatorTest {

	@Test
	public void testBoolean() throws InvalidValueException{
		NotEmptyIfNotNullValidator notEmptyIfNotNullValidator = new NotEmptyIfNotNullValidator();
		Serializable primitiveTrue = notEmptyIfNotNullValidator.validate(null, true, null);
		Assert.assertTrue((Boolean) primitiveTrue);
		Serializable primitiveFalse  = notEmptyIfNotNullValidator.validate(null, false, null);
		Assert.assertFalse((Boolean) primitiveFalse);
		Serializable booleanClassTrue = notEmptyIfNotNullValidator.validate(null, Boolean.TRUE, null);
		Assert.assertTrue((Boolean) booleanClassTrue);
		Serializable booleanClassFalse = notEmptyIfNotNullValidator.validate(null, Boolean.FALSE, null);
		Assert.assertFalse((Boolean) booleanClassFalse);
	}
}
