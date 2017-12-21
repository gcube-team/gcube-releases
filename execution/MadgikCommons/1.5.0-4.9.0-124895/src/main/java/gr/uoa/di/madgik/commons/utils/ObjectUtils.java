package gr.uoa.di.madgik.commons.utils;

public class ObjectUtils
{
	public static Object InstantiateWithDefaultConstructor(String ClassName) throws Exception
	{
		 Class<?> c=Class.forName(ClassName);
		 return c.newInstance();
	}
}
