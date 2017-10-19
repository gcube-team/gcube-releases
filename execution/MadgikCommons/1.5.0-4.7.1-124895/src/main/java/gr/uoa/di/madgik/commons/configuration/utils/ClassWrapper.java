package gr.uoa.di.madgik.commons.configuration.utils;

import gr.uoa.di.madgik.commons.configuration.parameter.IParameter;
import gr.uoa.di.madgik.commons.configuration.parameter.elements.Argument;
import gr.uoa.di.madgik.commons.configuration.parameter.elements.Arguments;
import gr.uoa.di.madgik.commons.configuration.parameter.elements.Constructor;
import gr.uoa.di.madgik.commons.configuration.parameter.elements.Method;
import java.util.List;
import java.util.Map;

/**
 * Utility class that can instantiate and call methods of some class through reflection
 *
 * @author gpapanikos
 */
public class ClassWrapper
{

	/**
	 * The instance created
	 */
	public Object Instance = null;
	/**
	 * The class loaded
	 */
	public Class<?> ModuleClass = null;
	private Map<String, IParameter> Params = null;

	/**
	 * Creates a new instance
	 *
	 * @param Params The parameters that are available and can be used to retrieve and set values
	 */
	public ClassWrapper(Map<String, IParameter> Params)
	{
		this.Params = Params;
	}

	/**
	 * Creates a new instance of the provided class
	 *
	 * @param ModuleName The class that should be instantiated
	 * @param ConstructorToUse The constructor of the class to use
	 * @throws java.lang.Exception The instantiation could not be performed
	 */
	public void Instantiate(String ModuleName, Constructor ConstructorToUse) throws Exception
	{
		this.ModuleClass = Class.forName(ModuleName);
		if (ConstructorToUse != null)
		{
			if (ConstructorToUse.GetArguments().GetArguments().size() == 0)
			{
				this.Instance = ModuleClass.newInstance();
			} else
			{
				java.lang.reflect.Constructor<?> con = this.ModuleClass.getConstructor(this.GetArgumentList(ConstructorToUse.GetArguments()));
				if (con == null)
				{
					throw new Exception("Declared constructor not found");
				}
				this.Instance = con.newInstance(this.GetArgumentListValues(ConstructorToUse.GetArguments()));
			}
		}
	}

	/**
	 * Invokes a method of the created instance if the class was instantiated or a static
	 * method if no instantiation was performed
	 * 
	 * @param MethodToInvoke The method to invoke
	 * @throws java.lang.Exception The invocation could not be performed
	 */
	public void Invoke(Method MethodToInvoke) throws Exception
	{
		java.lang.reflect.Method m = this.ModuleClass.getDeclaredMethod(MethodToInvoke.GetName(), this.GetArgumentList(MethodToInvoke.GetArguments()));
		if (m == null)
		{
			throw new Exception("Declared method not found");
		}
		Object ret = m.invoke(this.Instance, this.GetArgumentListValues(MethodToInvoke.GetArguments()));
		if (MethodToInvoke.GetOutput() != null)
		{
			this.Params.get(MethodToInvoke.GetOutput().GetParameterName()).SetValue(ret);
			this.Params.get(MethodToInvoke.GetOutput().GetParameterName()).Check();
		}
	}

	private Class<?>[] GetArgumentList(Arguments args)
	{
		List<Argument> argsl = args.GetArguments();
		Class<?>[] ret = new Class[argsl.size()];
		for (int i = 0; i < argsl.size(); i += 1)
		{
			ret[i] = this.Params.get(argsl.get(i).GetParameterName()).GetParameterClassType();
		}
		return ret;
	}

	private Object[] GetArgumentListValues(Arguments args)
	{
		List<Argument> argsl = args.GetArguments();
		Object[] ret = new Object[argsl.size()];
		for (int i = 0; i < argsl.size(); i += 1)
		{
			ret[i] = this.Params.get(argsl.get(i).GetParameterName()).GetValue();
		}
		return ret;
	}
}
