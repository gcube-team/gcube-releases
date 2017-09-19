package org.gcube.commons.common.validator.beans;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.common.validator.annotations.IsValid;
import org.gcube.common.validator.annotations.NotEmpty;
import org.gcube.common.validator.annotations.NotNull;

public class ExampleValidation {

	@NotNull
	private String office;
	
	@NotNull @IsValid
	private List<Person> people;

	@IsValid
	private Set<Person> bosses;
	
	@IsValid @NotEmpty
	private Map<String, Person> slavePerPerson;
	
	public void setOffice(String office) {
		this.office = office;
	}

	public void setPeople(List<Person> people) {
		this.people = people;
	}

	public void setBosses(Set<Person> bosses) {
		this.bosses = bosses;
	}

	public void setSlavePerPerson(Map<String, Person> slavePerPerson) {
		this.slavePerPerson = slavePerPerson;
	}

	@Override
	public String toString() {
		return "ExampleValidation [office=" + office + ", people=" + people
				+ ", bosses=" + bosses + ", slavePerPerson=" + slavePerPerson
				+ "]";
	}
	
	
}


