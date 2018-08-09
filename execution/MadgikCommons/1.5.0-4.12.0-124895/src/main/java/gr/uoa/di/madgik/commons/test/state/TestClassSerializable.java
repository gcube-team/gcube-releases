package gr.uoa.di.madgik.commons.test.state;

import java.io.Serializable;

/**
 *
 * @author gpapanikos
 */
public class TestClassSerializable implements Serializable
{

	private static final long serialVersionUID = 4;
	private String field1 = "hello world from Serializable class";
	private int field2 = 5;
	private String field3 = TestClassSerializable.class.getName();

	/**
	 * 
	 */
	public TestClassSerializable()
	{
	}

	@Override
	public String toString()
	{
		return field1 + " " + field2 + " " + field3;
	}
}
