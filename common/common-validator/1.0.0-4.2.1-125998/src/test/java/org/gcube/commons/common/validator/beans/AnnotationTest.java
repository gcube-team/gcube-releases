package org.gcube.commons.common.validator.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.common.validator.ValidationError;
import org.gcube.common.validator.ValidatorFactory;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotationTest {

	private static Logger logger = LoggerFactory.getLogger(AnnotationTest.class);
	
	@Test
	public void NotNullfail() throws Exception{
		ExampleValidation exampleValidation = new ExampleValidation();
		exampleValidation.setOffice("I43");
		List<Person> persons = new ArrayList<Person>();
		persons.add(new Person("Lucio", null, new Person[]{new Person(null, "Lelii", null)}));
		exampleValidation.setPeople(persons);
		Map<String, Person> slavePerPerson = new HashMap<String, Person>();
		//slavePerPerson.put("lucio", new Person("fabio", null, null));
		exampleValidation.setSlavePerPerson(slavePerPerson);
		Set<Person> bosses= new HashSet<Person>();
		bosses.add(new Person("", "pagano", null));
		exampleValidation.setBosses(bosses);
		List<ValidationError> errors = ValidatorFactory.validator().validate(exampleValidation);
		Assert.assertTrue(errors.size()>0);
		logger.trace(errors.toString());
		
	}
}
