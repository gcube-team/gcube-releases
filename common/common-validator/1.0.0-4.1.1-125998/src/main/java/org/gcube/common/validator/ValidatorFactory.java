package org.gcube.common.validator;

public class ValidatorFactory {

	private static Validator validator = new DefaultValidator();
	

	public static Validator validator() {
		return validator;
	}

	public static void validator(Validator newValidator) {
		validator = newValidator;
	}
	
	
	
}
