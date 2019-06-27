/**
 * 
 */
package org.gcube.dataaccess.spd.havingengine.test.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * @author Federico De Faveri defaveri@gmail.com
 *
 */
public class Person {
	
	protected static final SimpleDateFormat spd = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
	
	protected String name;
	protected String surname;
	protected int age;
	protected Calendar birthDate;
	
	protected List<Product> products;
	
		
	
	/**
	 * @param name
	 * @param surname
	 * @param age
	 * @param birthDate
	 */
	public Person(String name, String surname, int age, Calendar birthDate, List<Product> products) {
		this.name = name;
		this.surname = surname;
		this.age = age;
		this.birthDate = birthDate;
		this.products = products;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the surname
	 */
	public String getSurname() {
		return surname;
	}
	/**
	 * @param surname the surname to set
	 */
	public void setSurname(String surname) {
		this.surname = surname;
	}
	/**
	 * @return the age
	 */
	public int getAge() {
		return age;
	}
	/**
	 * @param age the age to set
	 */
	public void setAge(int age) {
		this.age = age;
	}
	/**
	 * @return the birthDate
	 */
	public Calendar getBirthDate() {
		return birthDate;
	}
	/**
	 * @param birthDate the birthDate to set
	 */
	public void setBirthDate(Calendar birthDate) {
		this.birthDate = birthDate;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Person [name=");
		builder.append(name);
		builder.append(", surname=");
		builder.append(surname);
		builder.append(", age=");
		builder.append(age);
		builder.append(", birthDate=");
		if (birthDate!=null) builder.append(spd.format(birthDate.getTime()));
		else builder.append(birthDate);
		builder.append("]");
		return builder.toString();
	}
	
	

}
