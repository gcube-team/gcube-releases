package org.gcube.commons.common.validator.beans;

import java.util.Arrays;

import org.gcube.common.validator.annotations.NotEmpty;
import org.gcube.common.validator.annotations.NotNull;

public class Person extends Extension{
	
	public Person(String firstName, String lastName, Person[] brothers) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.brothers = brothers;
	}

	@NotNull @NotEmpty
	String firstName;
	
	@NotNull @NotEmpty
	String lastName;
	
	

	@Override
	public String toString() {
		return "Person [firstName=" + firstName + ", lastName=" + lastName
				+ ", brothers=" + Arrays.toString(brothers) + "]";
	}

	
	
	
}
