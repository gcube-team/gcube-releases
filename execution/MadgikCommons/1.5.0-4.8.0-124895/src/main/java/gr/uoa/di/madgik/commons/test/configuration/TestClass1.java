package gr.uoa.di.madgik.commons.test.configuration;

/**
 *
 * @author gpapanikos
 */
public class TestClass1
{

	Integer arg1;
	Float arg2;
	Double arg3;

	/**
	 * 
	 * @param arg1 fgds
	 * @param arg2 sgfd
	 * @param arg3 sdgf
	 */
	public TestClass1(Integer arg1, Float arg2, Double arg3)
	{
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.arg3 = arg3;
	}

	/**
	 *
	 * @param arg1 fsd
	 * @param arg2 sdgf
	 * @param arg3 sdg
	 * @return dfgf
	 */
	public String method1(Integer arg1, Float arg2, Double arg3)
	{
		return "construtor = "+(this.sum(arg1,this.arg2,this.arg3))+" method = arg1: "+arg1+" arg2: "+arg2+" arg3: "+arg3;
	}

	@Override
	public String toString()
	{
		return "construtor = "+(this.sum(arg1,this.arg2,this.arg3))+" method = arg1: "+arg1+" arg2: "+arg2+" arg3: "+arg3;
	}

	private float sum(Integer arg1, Float arg2, Double arg3)
	{
		return ((float)arg1.intValue())+arg2.floatValue()+((float)arg3.doubleValue());
	}
}
